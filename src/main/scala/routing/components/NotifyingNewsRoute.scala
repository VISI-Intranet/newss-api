package routing.components.blockNewsComponents

import Main._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import amqp._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import mail.MailSender
import model.newsComponents.NotifyingNewsModel
import repository.newsComponent.NotifyingNewsRepo
import org.json4s.{DefaultFormats, jackson}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object NotifyingNewsRoute extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  private val fields: List[String] = List(
    "id",
    "authorId",
    "content",
    "date",
    "titel"
  )

  val route: Route =
    pathPrefix("NotifytedNews") {
      concat(
        pathEnd {
          concat(
            (get & parameters("field", "parameter")) { (field, parameter) =>
              validate(fields.contains(field),
                s"Invalid table field name! Allowed fields: ${fields.mkString(", ")}") {
                val convertedParameter = if (parameter.matches("-?\\d+")) parameter.toInt else parameter
                onComplete(NotifyingNewsRepo.getByField(field, parameter)) {
                  case Success(queryResponse) => complete(StatusCodes.OK, queryResponse)
                  case Failure(ex) => complete(StatusCodes.InternalServerError, s"Failed to execute query! ${ex.getMessage}")
                }
              }
            },
            get {
              onComplete(NotifyingNewsRepo.getAll()) {
                case Success(notifytedNewsList) => complete(StatusCodes.OK, notifytedNewsList)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Error while fetching notifyted news: ${ex.getMessage}")
              }
            },
            post {
              entity(as[NotifyingNewsModel]) { notifytedNews =>
                onComplete(NotifyingNewsRepo.insertData(notifytedNews)) {
                  case Success(newNotifytedNewsId) =>
                    implicit val timeout: Timeout = Timeout(5.seconds)
                    val res = Main.amqpActor ? RabbitMQ.Ask("univer.ScalaServicePostgress.request", "")
                    res.onComplete {
                      case Success(mails: String) =>
                        MailSender.main(mails)
                      case Failure(e: Throwable) =>
                        println(e.getMessage)
                    }
                    complete(StatusCodes.Created, s"ID of the new notifyted news: $newNotifytedNewsId")
                  case Failure(ex) =>
                    complete(StatusCodes.InternalServerError, s"Failed to create notifyted news: ${ex.getMessage}")
                }
              }
            }
          )
        },
        path(Segment) { notifytedNewsId =>
          concat(
            get {
              onComplete(NotifyingNewsRepo.getById(notifytedNewsId)) {
                case Success(notifytedNews) => complete(StatusCodes.OK, notifytedNews)
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Error while fetching notifyted news: ${ex.getMessage}")
              }
            },
            put {
              entity(as[NotifyingNewsModel]) { updatedNotifytedNews =>
                onComplete(NotifyingNewsRepo.updateNotifytedNewsData(updatedNotifytedNews)) {
                  case Success(_) => complete(StatusCodes.OK, "Notifyted news successfully updated")
                  case Failure(ex) => complete(StatusCodes.InternalServerError, s"Failed to update notifyted news: ${ex.getMessage}")
                }
              }
            },
            delete {
              onComplete(NotifyingNewsRepo.deleteData(notifytedNewsId)) {
                case Success(_) => complete(StatusCodes.NoContent, "Notifyted news successfully deleted")
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"Failed to delete notifyted news: ${ex.getMessage}")
              }
            }
          )
        }
      )
    }
}
