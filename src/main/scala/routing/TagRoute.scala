package routing

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import model.TagModel
import org.json4s.{DefaultFormats, jackson}
import repository.TagRepo

import scala.util.{Failure, Success}
object TagRoute extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  private val fields: List[String] = List(
    "newsId",
    "tag",
  )

  val route =
    pathPrefix("Tag") {
      concat(
        pathEnd {
          concat(
            (get & parameters("field", "parameter")) {
              (field, parameter) => {
                validate(fields.contains(field),
                  s"Вы ввели неправильное имя поля таблицы! Допустимые поля: ${fields.mkString(", ")}") {
                  val convertedParameter = if (parameter.matches("-?\\d+")) parameter.toInt else parameter
                  onComplete(TagRepo.getByField(field, parameter)) {
                    case Success(queryResponse) => complete(StatusCodes.OK, queryResponse)
                    case Failure(ex) => complete(StatusCodes.InternalServerError, s"Не удалось сделать запрос! ${ex.getMessage}")
                  }
                }
              }
            },
            get {
              onComplete(TagRepo.getAll()) {
                case Success(courses) => complete(StatusCodes.OK, courses)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Ошибка при получении курсов: ${ex.getMessage}")
              }
            },
            post {
              entity(as[TagModel]) { news =>
                onComplete(TagRepo.insertData(news)) {
                  case Success(newCourseId) =>
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
              onComplete(TagRepo.getById(newsId)) {
                case Success(course) => complete(StatusCodes.OK, course)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Ошибка при получении курса: ${ex.getMessage}")
              }
            },
            put {
              entity(as[TagModel]) { updatedNews =>
                onComplete(TagRepo.updateData(updatedNews)) {
                  case Success(_) => complete(StatusCodes.OK, "Курс успешно обновлен")
                  case Failure(ex) => complete(StatusCodes.InternalServerError, s"Не удалось обновить курс: ${ex.getMessage}")
                }
              }
            },
            delete {
              onComplete(TagRepo.deleteData(newsId)) {
                case Success(_) => complete(StatusCodes.NoContent, "Курс успешно удален")
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Не удалось удалить курс: ${ex.getMessage}")
              }
            }
          )
        },
      )
    }
}
