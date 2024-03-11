package DBManager


import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import model._
import repository._

import akka.http.scaladsl.server.Directives._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn
import routing._

object DatabaseManager {
  val db: Database = Database.forConfig("mysqlDB") // "mysqlDB" - это имя конфигурации из вашего файла application.conf
}

object Main{
  implicit val system: ActorSystem = ActorSystem("web-service")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher


  def main(args: Array[String]): Unit = {
    val allRoutes =
      NewsRoute.route ~
      StyleRoute.route ~
      TagRoute.route ~
      ViewedUsersRoute.route

    val bindingFuture =  Http().newServerAt("localhost",8080).bind(allRoutes)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    sys.addShutdownHook {
      bindingFuture
        .flatMap(_.unbind())
        .onComplete(_ => system.terminate())
    }
  }
}
