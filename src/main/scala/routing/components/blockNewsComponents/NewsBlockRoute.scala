package routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import model._
import repository.{NewsAssemblyRepo, JsonSupport}

import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

class CommentRoute(implicit val system:ActorSystem) extends JsonSupport {

  implicit val executionContext: ExecutionContext = system.dispatcher
  val amqpActor = system.actorSelection("user/amqpActor")

  // field параметрінің дұрыстығын тексеру үшін
  private val fields: List[String] = List(
    "id",
    "autor_id",
    "petition_id",
    "text",
    "date_of_create"
  )

  val route = pathPrefix("comments") {
    pathEndOrSingleSlash {
      (get & parameters("field", "parameter")) {
        (field, parameter) => {
          if (!fields.contains(field)) {
            complete(StatusCodes.BadRequest,
              s"Вы ввели неправильный имя поля таблицы! Допустимые поля: ${fields.mkString(", ")}")
          }
          else {
            onComplete(commentRepo.customFilter(field, parameter)) {
              case Success(queryResponse) => complete(StatusCodes.OK, queryResponse.toList)
              case Failure(_) => complete(StatusCodes.InternalServerError, "Не удалось сделать запрос!")
            }
          }
        }
      } ~
        get {
          onComplete(commentRepo.getAll) {
            case Success(result) =>
              val commentList = result.toList
              complete(StatusCodes.OK, commentList)
            case Failure(ex) => complete(StatusCodes.NotFound, s"Ошибка в коде: ${ex.getMessage}")
          }
        } ~
        post {
          entity(as[CommentCreateRequest]) {
            newComment => {
              val newValidComment = commentRepo.fromCreateRequest(newComment)
              val validation = validateCustom (
                userRepo.doesUserExist(newValidComment.authorId)
                  -> "Не правильно указали ID пользователья!",
                petitionRepo.doesPetitionExist(newValidComment.petitionId)
                  -> "Не правильно указали ID петиций!",
                userRepo.checkUserStatus(newValidComment.authorId)
                  -> "Этот пользователь не можеть оставлять коментарий!",
                petitionRepo.checkPetitionStatus(newValidComment.petitionId)
                  -> "На эту петицию нельзя оставлять коментарий!"
              )
              onComplete(validation) {
                case Success(true,_)=>
                  onComplete(commentRepo.create(newValidComment)) {
                    case Success(newCommentId) =>
                      complete(StatusCodes.Created, s"ID нового коментария ${newCommentId.get.toString}")
                    case Failure(_) =>
                      complete(StatusCodes.InternalServerError, "Не удалось создать коментарий!")
                  }
                case Success(false, message) => complete(StatusCodes.BadRequest, message)
                case Failure(_) => complete(StatusCodes.InternalServerError, "Проверка не выполнилось!")
              }
            }
          }
        }
    } ~
      path(IntNumber) { commentId =>
        get {
          onComplete(commentRepo.getById(commentId)) {
            case Success(comment) => complete(StatusCodes.OK, comment)
            case Success(None) => complete(StatusCodes.BadRequest,s"Коментарий под айди $commentId не существует!")
            case Failure(ex) => complete(StatusCodes.NotFound, s"Ошибка в коде: ${ex.getMessage}")
          }
        } ~
          put {
            entity(as[CommentUpdateRequest]) {
              updatedComment => {
                val updatedCommentWithId = commentRepo.commentForUpdate(commentId, updatedComment)
                val validation = validateCustom(
                  userRepo.doesUserExist(updatedCommentWithId.authorId)
                    ->"Не правильно указали ID пользователья",
                  petitionRepo.doesPetitionExist(updatedCommentWithId.petitionId)
                    -> "Не правильно указали ID петиций!",
                  userRepo.checkUserStatus(updatedCommentWithId.authorId)
                    -> "Этот пользователь не можеть оставлять коментарий!",
                  petitionRepo.checkPetitionStatus(updatedCommentWithId.petitionId)
                    -> "На эту петицию нельзя оставлять коментарий!"
                )
                onComplete(validation) {
                  case Success(true,_)=>
                    onComplete(commentRepo.update(commentId, updatedCommentWithId)) {
                      case Success(updatedCommentId) => complete(StatusCodes.OK, updatedCommentId.toString)
                      case Failure(ex) => complete(StatusCodes.NotFound, s"Ошибка в коде: ${ex.getMessage}")
                    }
                  case Success(false, message) => complete(StatusCodes.BadRequest, message)
                  case Failure(_) => complete(StatusCodes.InternalServerError, "Проверка не выполнилось!")
                }
              }
            }
          } ~
          delete {
            onComplete(commentRepo.delete(commentId)) {
              case Success(deletedCommentId) =>
                complete(StatusCodes.OK, s"Число удаленных строк ${deletedCommentId.toString}")
              case Failure(ex) => complete(StatusCodes.NotFound, s"Ошибка в коде:${ex.getMessage}")
            }
          }
      }
  }
}