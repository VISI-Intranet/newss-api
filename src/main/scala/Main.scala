package DBManager


import slick.jdbc.MySQLProfile.api._
import amqp._
import akka.http.scaladsl.server.Directives._
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContextExecutor}
import routing._

import scala.language.postfixOps

object DatabaseManager {
  val db: Database = Database.forConfig("mysqlDB") // "mysqlDB" - это имя конфигурации из вашего файла application.conf
}

object Main{
  val config = ConfigFactory.load("service_app.conf")

  // Извлечение значения параметра serviceName
  val serviceName = config.getString("service.serviceName")


  implicit val system: ActorSystem = ActorSystem(serviceName)
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val amqpActor = system.actorOf(Props(new AmqpActor("X:routing.topic",serviceName)),"amqpActor")

  def main(args: Array[String]): Unit = {
    val allRoutes =
      NewsRoute.route ~
      StyleRoute.route ~
      TagRoute.route ~
      ViewedUsersRoute.route

    val bindingFuture =  Http().newServerAt("localhost",8081).bind(allRoutes)
    println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")

    amqpActor ! RabbitMQ.DeclareListener("news_api_queue",s"univer.$serviceName.#","consumer_actor_1",
      new RabbitMQ_Consumer().handle)

    sys.addShutdownHook {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  }
}
