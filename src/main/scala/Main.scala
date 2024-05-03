package DBManager


import Alpakka.Operations.RecieveMessageAlpakka
import Alpakka.RabbitMQModel.RabbitMQModel
import slick.jdbc.MySQLProfile.api._
import akka.http.scaladsl.server.Directives._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.{AmqpConnectionProvider, AmqpLocalConnectionProvider}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import routing._

object DatabaseManager {
  val db: Database = Database.forConfig("mysqlDB")
}

object Main extends Json4sSupport {
  implicit val system: ActorSystem = ActorSystem("web-service")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {


    val allRoutes =
      NewsRoute.route ~
      StyleRoute.route ~
      TagRoute.route ~
      ViewedUsersRoute.route;



    val amqpConnectionProvider :AmqpConnectionProvider = AmqpLocalConnectionProvider

    val pubMQModel: RabbitMQModel = RabbitMQModel("EventSub","","")

    RecieveMessageAlpakka.subscription(pubMQModel,amqpConnectionProvider)



    val bindingFuture = Http().bindAndHandle(allRoutes, "localhost", 808)
    println(s"Server online at http://localhost:808/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
