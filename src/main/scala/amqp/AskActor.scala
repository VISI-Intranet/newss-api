package amqp

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.rabbitmq.client.*

import scala.collection.mutable

class AskActor(channel: Channel, exchangeName:String, serviceName: String) extends Actor with ActorLogging {

  val responseQueueName = s"response_queue_${serviceName}"
  val responseRoutingKey = s"univer.${serviceName}.response"

  // Создание очереди для ответов. Имя очереди responseQueueName
  channel.queueDeclare(responseQueueName, false, false, false, null)
  channel.queueBind(responseQueueName,exchangeName,responseRoutingKey)

  // Сохранение ссылок на акторы которые запрашивали
  private var sendors: mutable.Map[String, ActorRef] = mutable.Map()


  // Создание потребителя
  val consumer = new DefaultConsumer(channel) {
    override def handleDelivery(
                                 consumerTag: String,
                                 envelope: Envelope,
                                 properties: AMQP.BasicProperties,
                                 body: Array[Byte]
                               ): Unit = {
      // TODO: Проверить на очередность выполнения. Чтобы одновременно не изменять sendors
      val response = new String(body, "UTF-8")
      val correlationId = properties.getCorrelationId

      val actorRef: Option[ActorRef] = sendors.remove(correlationId)
      if (sendors.isEmpty) {
        channel.basicCancel(consumerTag)
      }
      actorRef match {
        case Some(actor: ActorRef) => {
          log.info(
            s"""
               |Пришел ответ на запрос с correlationId : $correlationId:
               |    body : $response""".stripMargin)
          actor ! response
        }
        case None =>
          log.warning(s"При возвращений ответа с идентификатором $correlationId не найдено отправителья")
      }
    }
  }

  def receive: Receive = {
    case RabbitMQ.Ask(routingKey,content) =>
      // Создание уникального идентификатора для запроса
      val correlationId = java.util.UUID.randomUUID().toString

      // Настройки отправляемого сообщения
      val properties = new AMQP.BasicProperties.Builder()
        // В какой очередь нужно вернуться
        .replyTo(responseRoutingKey)
        // Идентификатор сообщения
        .correlationId(correlationId)
        .contentType("Request")
        .build()

      if(sendors.isEmpty) {
        channel.basicConsume(responseQueueName,true,consumer)
      }

      // Сохраняем ключ значение как айди и его отправитель,
      // чтобы вернуть правильное сообщение к отправителью
      sendors += (correlationId -> sender())
      val body = content.getBytes("UTF-8")
      
      log.info(
        s"""
           |Был отправлен запрос c ожидание ответа:
           |    service name: $serviceName
           |    routingKey : $routingKey
           |    contentType : Request
           |    correlationId: $correlationId
           |    body : $content""".stripMargin)
      channel.basicPublish(exchangeName, routingKey, properties, body)
  }
}

object AskActor {
  def props(channel: Channel, exchangeName:String, serviceName: String): Props
  = Props(new AskActor(channel, exchangeName, serviceName))
}
