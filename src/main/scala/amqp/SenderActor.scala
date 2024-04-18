package amqp

import akka.actor.{Actor, ActorLogging, Props}
import com.rabbitmq.client.*



class SenderActor(channel: Channel, exchangeName:String,serviceName:String) extends Actor with ActorLogging {

  override def receive: Receive = {
    case RabbitMQ.Tell(routingKey, body) =>
      // Преобразуем сообщение в массив байтов
      val messageBytes = body.getBytes("UTF-8")

      val properties = new AMQP.BasicProperties.Builder()
        .contentType("Event")
        .build()

      // Отправляем сообщение в очередь
      channel.basicPublish(exchangeName, routingKey, properties, messageBytes)
      log.info(
        s"""
           |Отправлено сообщение:
           |    service name: $serviceName
           |    routingKey : $routingKey
           |    contentType : Event
           |    body : $body""".stripMargin)


    case RabbitMQ.Answer(routingKey,correlationId,body:String) =>
      // Преобразуем сообщение в массив байтов
      val messageBytes = body.getBytes("UTF-8")

      val properties = new AMQP.BasicProperties.Builder()
        .correlationId(correlationId)
        .contentType("Response")
        .build()
      
      // Отправляем сообщение в очередь
      channel.basicPublish(exchangeName, routingKey,properties, messageBytes)
      log.info(
        s"""
           |Отправлено ответ на запрос:
           |    service name: $serviceName
           |    correlationId : $correlationId
           |    routingKey : $routingKey
           |    contentType : Response
           |    body : $body""".stripMargin)

  }
}

object SenderActor {
  def props(channel: Channel, exchangeName: String,serviceName:String): Props = Props(new SenderActor(channel,exchangeName,serviceName))
}