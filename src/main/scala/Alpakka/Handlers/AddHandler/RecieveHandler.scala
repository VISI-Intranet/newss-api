package Alpakka.Handlers.AddHandler

import akka.actor.ActorSystem
import akka.stream.Materializer
import model.NewsModel
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import repository.NewsRepo

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object RecieveHandler {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  implicit lazy val system: ActorSystem = ActorSystem("web-system")
  implicit lazy val mat: Materializer = Materializer(system)

  val handler: (String, String) => Unit = (message, routingKey) => {

    routingKey match {

      case "univer.news-api.newsPOST" =>

        println("hello")

        implicit val formats: DefaultFormats.type = DefaultFormats
        val messageJson = parse(message)
        val idj = (messageJson \ "event_id").extract[String]
        val namej = (messageJson \ "name").extract[String]
        val contentj = (messageJson \ "content").extract[String]
        val creatorj = (messageJson \ "id_creator").extract[String]
        val namecreatorj = (messageJson \ "name_creator").extract[String]
        val type_creator = (messageJson \ "type_of_creator").extract[String]
        val locationj = (messageJson \ "location").extract[String]
        val dateStr = (messageJson \ "date").extract[String]
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
        val date = LocalDateTime.parse(dateStr, dateFormatter)
        val datej = Timestamp.valueOf(date)


        val i = NewsModel(id = null,
          event_id = idj.toInt,
          authorId = creatorj.toInt,
          canComment = true,
          category = "Event",
          content = s"${contentj} Creator:${namecreatorj} Location:${locationj} Type_Creator:$type_creator",
          date = datej,
          filter = type_creator,
          importance = "hight",
          time = datej,
          titel = namej)

        val insertionResult: Future[Int] = NewsRepo.insertData(i)
        insertionResult.onComplete {
          case Success(_) =>
            println("Данные успешно добавлены в базу данных")
          // Действия после успешного добавления данных
          case Failure(exception) =>
            println(s"Ошибка при добавлении данных в базу данных: ${exception.getMessage}")
          // Действия при ошибке добавления данных
        }



      case "univer.news-api.newsUPDATE" =>
        implicit val formats: DefaultFormats.type = DefaultFormats
        val messageJson = parse(message)
        val idj = (messageJson \ "event_id").extract[String]
        val namej = (messageJson \ "name").extract[String]
        val contentj = (messageJson \ "content").extract[String]
        val  creatorj = (messageJson \ "id_creator").extract[String]
        val type_creator = (messageJson \ "type_of_creator").extract[String]
        val  locationj = (messageJson \ "location").extract[String]
        val dateStr = (messageJson \ "date").extract[String]
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
        val date = LocalDateTime.parse(dateStr, dateFormatter)
        val datej = Timestamp.valueOf(date)


        val i =  NewsModel(id = null,
          event_id=idj.toInt,
          authorId = creatorj.toInt,
          canComment = true,
          category = "Event",
          content = s"${contentj} Creator:${creatorj}\tLocation:${locationj}",
          date = datej,
          filter = type_creator,
          importance = "hight",
          time = datej,
          titel = namej)
        val updateResult: Future[Int] = NewsRepo.updateDataEvent(i)
        updateResult.onComplete {
          case Success(_) =>
            println("Данные успешно изменены в базу данных")
          // Действия после успешного добавления данных
          case Failure(exception) =>
            println(s"Ошибка при изменены данных в базу данных: ${exception.getMessage}")
          // Действия при ошибке добавления данных
        }





      case "univer.news-api.newsDELETE" =>
        implicit val formats: DefaultFormats.type = DefaultFormats
        val messageJson = parse(message)
        val idj = (messageJson \ "event_id").extract[String]

        val id=idj.toInt
        val deleteResult: Future[Int] = NewsRepo.deleteDataEvent(id)
        deleteResult.onComplete {
          case Success(_) =>
            println("Данные успешно удалены в базу данных")
          // Действия после успешного добавления данных
          case Failure(exception) =>
            println(s"Ошибка при удалены данных в базу данных: ${exception.getMessage}")
          // Действия при ошибке добавления данных
        }

      case _=>println("Key not found")
    }

  }

}
