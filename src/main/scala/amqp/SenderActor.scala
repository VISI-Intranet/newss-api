//package amqp
//
//import com.rabbitmq.client.{AMQP, Channel, ConnectionFactory}
//
//import javax.mail.internet.{InternetAddress, MimeMessage}
//import javax.mail.{Message, PasswordAuthentication, Session}
//import java.util.Properties
//import javax.mail.Transport
//import scala.collection.mutable
//import scala.concurrent.Future
//
//
//
//
//object SenderObject{
//
//  private val responseRoutingKey = s"teacher_request";
//  private val exchangeName: String = "Teacher_Exchanger"
//
//  private val factory = new ConnectionFactory()
//  factory.setHost("localhost")
//  private val connection = factory.newConnection()
//  private val channel = connection.createChannel()
//
//  def send(generalize: RMQOptionModel): Future[Unit] = {
//    println("sender working")
//    generalize match {
//      //-------------------------------------------------------------------------------
//      case RMQOptionModel.Tell(routingKey, message) =>
//
//        val messageBytes = message.getBytes("UTF-8")
//        // Отправляем сообщение в очередь
//        channel.basicPublish(exchangeName, routingKey, new AMQP.BasicProperties(), messageBytes)
//        Future.successful()
//
//      //-------------------------------------------------------------------------------
//      case RMQOptionModel.Answer(routingKey, correlationId, message) =>
//        // Преобразуем сообщение в массив байтов
//        val messageBytes = message.getBytes("UTF-8")
//
//        val properties = new AMQP.BasicProperties.Builder()
//          .correlationId(correlationId)
//          .contentType("request")
//          .build()
//
//        // Отправляем сообщение в очередь
//        channel.basicPublish(exchangeName, routingKey, properties, messageBytes)
//        Future.successful()
//      //-------------------------------------------------------------------------------
//      case RMQOptionModel.Ask(routingKey, content, ref) =>
//        // Создание уникального идентификатора для запроса
//        val requestId = java.util.UUID.randomUUID().toString
//
//        // Настройки отправляемого сообщения
//        val properties = new AMQP.BasicProperties.Builder()
//          // В какой очередь нужно вернуться
//          .replyTo(responseRoutingKey)
//          // Идентификатор сообщения
//          .correlationId(requestId)
//          .contentType("request")
//          .build()
//
//        Sendors.sendors += (requestId -> ref)
//        val body = content.getBytes("UTF-8")
//        channel.basicPublish(exchangeName, routingKey, properties, body)
//        Future.successful()
//    }
//
//  }
//
//  def sendMail(body: String): Future[Unit] = {
//    println("mail working")
//    val username = "suraubaev04@mail.ru"
//    val password = "65YMuLGmtGYmPy42NswD"
//    val recipient = "suraubaev04@mail.ru"
//
//    val props = new Properties()
//    props.put("mail.smtp.auth", "true")
//    props.put("mail.smtp.starttls.enable", "true")
//    props.put("mail.smtp.host", "smtp.mail.ru")
//    props.put("mail.smtp.port", "587")
//
//    val session = Session.getInstance(props,
//      new javax.mail.Authenticator() {
//        override def getPasswordAuthentication(): PasswordAuthentication = {
//          new PasswordAuthentication(username, password)
//        }
//      })
//
//    try {
//      val message = new MimeMessage(session)
//      message.setFrom(new InternetAddress(username))
//      message.setRecipients(Message.RecipientType.TO, recipient)
//      message.setSubject("Testing Mail")
//      message.setText("This is a test email sent from Scala.")
//
//      Transport.send(message)
//      Future.successful("Email send successfully!")
//
//    } catch {
//      case e: Exception =>
//        Future.unit
//    }
//  }
//
//}
//
//object Sendors{
//  var sendors: mutable.Map[String, String => Future[Unit]] = mutable.Map()
//}


package amqp

import akka.actor.{Actor, ActorLogging, Props}
import com.rabbitmq.client.{AMQP, Channel}


class SenderActor(channel: Channel, exchangeName:String) extends Actor with ActorLogging {

  override def receive: Receive = {
    case RabbitMQ.Tell(routingKey, message) =>
      // Преобразуем сообщение в массив байтов
      val messageBytes = message.getBytes("UTF-8")
      // Отправляем сообщение в очередь
      channel.basicPublish(exchangeName, routingKey, new AMQP.BasicProperties(), messageBytes)
      log.info(s"Отправлено сообщение по ключу: $routingKey. Тело: $message")

    case RabbitMQ.Answer(routingKey,correlationId,message) =>
      // Преобразуем сообщение в массив байтов
      val messageBytes = message.getBytes("UTF-8")

      val properties = new AMQP.BasicProperties.Builder()
        .correlationId(correlationId)
        .build()

      // Отправляем сообщение в очередь
      channel.basicPublish(exchangeName, routingKey,properties, messageBytes)
      log.info(s"Отправлено ответ по ключу: $routingKey. Тело: $message")

  }
}

object SenderActor {
  def props(channel: Channel, exchangeName: String): Props = Props(new SenderActor(channel,exchangeName))
}