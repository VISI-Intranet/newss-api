package routing

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import model._
import scala.util.{Failure, Success}
import repository.NewsRepo

object NewsRoute extends CustomJson4sSupport {

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
                case Success(courses) => complete(StatusCodes.OK, courses)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Ошибка при получении курсов: ${ex.getMessage}")
              }
            },
            post {
              entity(as[NewsModel]) { news =>
                onComplete(NewsRepo.insertData(news)) {
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
