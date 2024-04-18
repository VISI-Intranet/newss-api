package routing

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import model._

import scala.util.{Failure, Success}
import org.json4s.{DefaultFormats, jackson}
import Main._
import amqp._
import akka.pattern.ask
import akka.util.Timeout
import mail.MailSender
import model.newsComponents.NewsModel

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
object NewsRoute extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  private val fields: List[String] = List(
    "authorId",
    "canComment",
    "category",
    "content",
    "date",
    "filter",
    "importance",
    "time",
    "titel"
  )

  val route =
    pathPrefix("News") {
      concat(
        pathEnd {
          concat(
            (get & parameters("field", "parameter")) {
              (field, parameter) => {
                validate(fields.contains(field),
                  s"Вы ввели неправильное имя поля таблицы! Допустимые поля: ${fields.mkString(", ")}") {
                  val convertedParameter = if (parameter.matches("-?\\d+")) parameter.toInt else parameter
                  onComplete(NewsRepo.getByField(field, parameter)) {
                    case Success(queryResponse) => complete(StatusCodes.OK, queryResponse)
                    case Failure(ex) => complete(StatusCodes.InternalServerError, s"Не удалось сделать запрос! ${ex.getMessage}")
                  }
                }
              }
            },
            get {
              onComplete(NewsRepo.getAll()) {
                case Success(courses) =>
                  complete(StatusCodes.OK, courses)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Ошибка при получении курсов: ${ex.getMessage}")
              }
            },
            post {
              entity(as[NewsModel]) { news =>
                onComplete(NewsRepo.insertData(news)) {
                  case Success(newCourseId) =>
                    implicit val timeout: Timeout = Timeout(5.seconds) // Устанавливаем таймаут
                    val res = Main.amqpActor ? RabbitMQ.Ask("univer.ScalaServicePostgress.request", "")
                    res.onComplete {
                      case Success(mails: String) =>
                        MailSender.main(mails)
                      case Failure(e : Throwable) =>
                        println(e.getMessage)

                    }

                    complete(StatusCodes.Created, s"ID нового курса: $newCourseId")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError, s"Не удалось создать курс: ${ex.getMessage}")
                }
              }
            }
          )
        },
        path(Segment) { newsId =>
          concat(
            get {
              onComplete(NewsRepo.getById(newsId)) {
                case Success(course) => complete(StatusCodes.OK, course)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Ошибка при получении курса: ${ex.getMessage}")
              }
            },
            put {
              entity(as[NewsModel]) { updatedNews =>
                onComplete(NewsRepo.updateData(updatedNews)) {
                  case Success(_) => complete(StatusCodes.OK, "Курс успешно обновлен")
                  case Failure(ex) => complete(StatusCodes.InternalServerError, s"Не удалось обновить курс: ${ex.getMessage}")
                }
              }
            },
            delete {
              onComplete(NewsRepo.deleteData(newsId)) {
                case Success(_) => complete(StatusCodes.NoContent, "Курс успешно удален")
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Не удалось удалить курс: ${ex.getMessage}")
              }
            }
          )
        },
      )
    }
}
