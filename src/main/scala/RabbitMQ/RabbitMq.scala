package RabbitMQ

//package rmq
//import DBManager.Main.executionContext
//import com.rabbitmq.client._
//import model.NewsModel
//import repository.NewsRepo
//import org.json4s._
//import org.json4s.jackson.JsonMethods._
//import repository.NewsRepo.{updateData,deleteData,updateDataEvent}
//import scala.concurrent.duration._
//import java.sql.Timestamp
//import scala.concurrent.{Await, Future}
//import scala.concurrent.duration.DurationInt
//import scala.util.{Failure, Success, Try}
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//object RabbitMQConsumer {
//
//
//  def h(): Unit = {
//
//
//    val factory = new ConnectionFactory()
//    factory.setHost("localhost") // Здесь укажите хост RabbitMQ, если он у вас находится не локально
//    factory.setUsername("guest") // Укажите свои учетные данные, если они отличаются
//    factory.setPassword("guest")
//
//    val connection = factory.newConnection()
//    val channel = connection.createChannel()
//
//
//
//    val queueName = "EventSub"
//    val routingKeyDELETE = // Название очереди, из которой будем получать сообщения
//
//    channel.queueDeclare(queueName, true, false, false, null)
//    println(s" [*] Ожидание сообщений из очереди '$queueName'. Для выхода нажмите CTRL+C")
//
//    val consumer = new DefaultConsumer(channel) {
//      override def handleDelivery(consumerTag: String,envelope: Envelope,properties: AMQP.BasicProperties,body: Array[Byte]): Unit = {
//        val message = new String(body, "UTF-8")
//        println(s" [x] Получено сообщение: '$message'")
//
//
//
//
//
//
//
//
//
//        envelope.getRoutingKey match {
//
//
//          case "univer.event.eventPOST" => {
//            implicit val formats: DefaultFormats.type = DefaultFormats
//            val messageJson = parse(message)
//            val idj = (messageJson \ "event_id").extract[String]
//            val namej = (messageJson \ "name").extract[String]
//            val contentj = (messageJson \ "content").extract[String]
//            val  creatorj = (messageJson \ "creator").extract[String]
//            val  locationj = (messageJson \ "location").extract[String]
//            val dateStr = (messageJson \ "date").extract[String]
//            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
//            val date = LocalDateTime.parse(dateStr, dateFormatter)
//            val datej = Timestamp.valueOf(date)
//
//
//            val i = new NewsModel(id = null,
//              event_id=idj.toInt,
//              authorId = 1,
//              canComment = true,
//              category = "Event",
//              content = s"${contentj} Creator:${creatorj} Location:${locationj}",
//              date = datej,
//              filter = "Student",
//              importance = "hight",
//              time = datej,
//              titel = namej)
//
//            val insertionResult: Future[Int] = NewsRepo.insertData(i)
//            insertionResult.onComplete {
//              case Success(_) =>
//                println("Данные успешно добавлены в базу данных")
//              // Действия после успешного добавления данных
//              case Failure(exception) =>
//                println(s"Ошибка при добавлении данных в базу данных: ${exception.getMessage}")
//              // Действия при ошибке добавления данных
//            }
//            val result = Await.result(insertionResult, 10.seconds)
//            println(s"Inserted $result rows.")
//          }
//
//
//
//
//
//
//          case "univer.event.eventPUT" =>{
//            implicit val formats: DefaultFormats.type = DefaultFormats
//            val messageJson = parse(message)
//            val idj = (messageJson \ "event_id").extract[String]
//            val namej = (messageJson \ "name").extract[String]
//            val contentj = (messageJson \ "content").extract[String]
//            val  creatorj = (messageJson \ "id_creator").extract[String]
//            val  locationj = (messageJson \ "location").extract[String]
//            val dateStr = (messageJson \ "date").extract[String]
//            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")
//            val date = LocalDateTime.parse(dateStr, dateFormatter)
//            val datej = Timestamp.valueOf(date)
//
//
//            val i = NewsModel(id = null,
//              event_id=idj.toInt,
//              authorId = creatorj.toInt,
//              canComment = true,
//              category = "Event",
//              content = s"${contentj} Creator:${creatorj}\tLocation:${locationj}",
//              date = datej,
//              filter = "Student",
//              importance = "hight",
//              time = datej,
//              titel = namej)
//            val updateResult: Future[Int] = NewsRepo.updateDataEvent(i)
//            updateResult.onComplete {
//              case Success(_) =>
//                println("Данные успешно изменены в базу данных")
//              // Действия после успешного добавления данных
//              case Failure(exception) =>
//                println(s"Ошибка при изменены данных в базу данных: ${exception.getMessage}")
//              // Действия при ошибке добавления данных
//            }
//            val result = Await.result(updateResult, 10.seconds)
//            println(s"Updated $result rows.")
//          }
//
//
//
//
//
//          case "univer.event.eventDELETE" =>{
//            implicit val formats: DefaultFormats.type = DefaultFormats
//            val messageJson = parse(message)
//            val idj = (messageJson \ "event_id").extract[String]
//
//            val id=idj.toInt
//            val deleteResult: Future[Int] = NewsRepo.deleteDataEvent(id)
//            deleteResult.onComplete {
//              case Success(_) =>
//                println("Данные успешно удалены в базу данных")
//              // Действия после успешного добавления данных
//              case Failure(exception) =>
//                println(s"Ошибка при удалены данных в базу данных: ${exception.getMessage}")
//              // Действия при ошибке добавления данных
//            }
//            val result = Await.result(deleteResult, 10.seconds)
//            println(s"Deleted $result rows.")
//          }
//        }
//      }
//    }
//
//    Try(channel.basicConsume(queueName, true,"univer.event.eventPOST", consumer)) // Запуск потребителя
//      .recover {
//        case ex: Exception => println(s"POST Ошибка при запуске потребителя: ${ex.getMessage}")
//      }
//    Try(channel.basicConsume(queueName, true,"univer.event.eventPUT", consumer)) // Запуск потребителя
//      .recover {
//        case ex: Exception => println(s"PUT Ошибка при запуске потребителя: ${ex.getMessage}")
//      }
//    Try(channel.basicConsume(queueName, true,"univer.event.eventDELETE", consumer)) // Запуск потребителя
//      .recover {
//        case ex: Exception => println(s"DELETE Ошибка при запуске потребителя: ${ex.getMessage}")
//      }
//
//    sys.addShutdownHook {
//      println("Закрываем соединение с RabbitMQ")
//      Try(channel.close())
//      Try(connection.close())
//    }
//  }
//}
//
//
//
