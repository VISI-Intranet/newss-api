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

    val bindingFuture = Http().bindAndHandle(allRoutes, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
    val currentTimeMillis: Long = System.currentTimeMillis()

    //val insertionResult: Future[Int] = NewsRepo.insertData(new NewsModel(id = null,authorId = 1, canComment = true, category = "holiday", content = "NewYear", date = new Timestamp(currentTimeMillis),filter = "Student", importance = "hight", time = new Timestamp(currentTimeMillis),titel = "New"))
    //val insertionResult: Future[Int] = NewsRepo.deleteData(1)
    //val insertionResult: Future[Int] = NewsRepo.updateData(new NewsModel(id = Some(2),authorId = 1, canComment = true, category = "holiday", content = "OldYear", date = new Timestamp(currentTimeMillis),filter = "Student", importance = "hight", time = new Timestamp(currentTimeMillis),titel = "New"))
    //val insertionResult: Future[Seq[NewsModel]] = NewsRepo.getAll
    val insertionResult: Future[Seq[NewsModel]] = NewsRepo.getByField("filter", "Student")
    val result = Await.result(insertionResult, 10.seconds)
    println(s"Inserted $result rows.")


    //  // Пример создания таблицы
    //val tableQuery: TableQuery[YourTable] = TableQuery[YourTable]
    // val tegQuery:TableQuery[Teg] = TableQuery[Teg]
    //  val createTableAction = createTableQuery.schema.create
  }
}
