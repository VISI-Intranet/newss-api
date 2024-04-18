package amqp

import akka.actor.{Actor, ActorLogging, Props}
import com.rabbitmq.client.*



class ReceiverActor(
                     channelForConsumer: Channel,
                     queueName: String,
                     exchangeName:String,
                     bind_routing_key:String,
                     handle:Message=>Unit) extends Actor with ActorLogging {

  private var consumerTag:String = _


  override def preStart(): Unit = {
    // На новом канале создается очередь и привязывается
    channelForConsumer.queueDeclare(queueName, false, false, false, null)
    channelForConsumer.queueBind(queueName, exchangeName, bind_routing_key)

    // На новом канале начинется слушать
    consumerTag = channelForConsumer.basicConsume(queueName, true, consumer)
  }



  override def receive: Receive = {
    // Актор с создания будет слушать
    // Если отправить ему Unlisted он перестает слушать
    // и сменить поведение и будеть обрабатывать только сообщение Listen
    case "Unlisted" =>
      channelForConsumer.basicCancel(consumerTag)
      context.become{
        case "Listen" =>
          consumerTag = channelForConsumer.basicConsume(queueName, true, consumer)
          context.unbecome()
      }
  }

  override def postStop(): Unit = {
    try {
      channelForConsumer.basicCancel(consumerTag)
    } catch {
      case e: Exception =>
        println("Ошибка при отмене подписки консьюмера:"+ e.getMessage)
    } finally {
      channelForConsumer.close()
    }
  }

  // Сам консюмер который будет слушать
  private val consumer = new DefaultConsumer(channelForConsumer) {
    override def handleDelivery(consumerTag: String,
                                envelope: Envelope,
                                properties: AMQP.BasicProperties,
                                body: Array[Byte]): Unit = {
      val message = new String(body, "UTF-8")
      log.info(
        s"""
           |Пришло сообшение:
           |    contentType : ${properties.getContentType}
           |    routingKey : ${envelope.getRoutingKey}
           |    body : $message""".stripMargin)
      handle(Message(message, envelope.getRoutingKey, properties.getReplyTo, properties.getCorrelationId))
    }
  }

}

object ReceiverActor {
  def props(channelForConsumer: Channel, queueName: String, exchangeName: String,
            bind_routing_key: String, handle: Message=>Unit): Props =
    Props(new ReceiverActor(channelForConsumer, queueName,exchangeName,bind_routing_key, handle))
}