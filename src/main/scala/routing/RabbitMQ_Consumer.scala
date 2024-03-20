package routing

import DBManager.Main
import model.NewsModel
import amqp._
import repository.NewsRepo
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.AsJsonInput.stringAsJsonInput

import scala.util.{Failure, Success}
import java.sql.Timestamp
import scala.concurrent.ExecutionContext

object RabbitMQ_Consumer extends CustomJson4sSupport {
  implicit val executor: ExecutionContext = Main.system.getDispatcher
  def handle(message:Message)={
    message.routingKey match {
      case "univer.news_api.create_petitionPOST" =>{
        val json: JValue = parse(message.body)
        val body = json \ "body"
        val new_news_about_petition = NewsModel(
          id = None,
          authorId = 0,
          canComment = true,
          category = "Petition",
          content =
            s"Создан новая петиция под названием ${(body \ "name").extract[String]}." +
              s"Описание петиций: ${(body \ "description").extract[String]}." +
              s"Цель голосов: ${(body \ "goalOfVotes").extract[String]}",
          date = new Timestamp(System.currentTimeMillis()),
          filter = "None",
          importance = "None",
          time = new Timestamp(System.currentTimeMillis()),
          titel = "Создана новая петиция на системе петиций!"
        )
        val future = NewsRepo.insertData(new_news_about_petition)
        future onComplete {
          case Success(value) => println("Создана новая новость про петицию")
          case Failure(exception) => println(exception.getMessage)
        }
      }
      case "univer.news_api.test"=>{
        print(message.body)
      }

    }
  }
}