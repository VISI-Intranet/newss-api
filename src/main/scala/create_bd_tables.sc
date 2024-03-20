import slick.jdbc.MySQLProfile.api._
import model._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps
import scala.util.{Failure, Success}

// Это код для создания базы и внутри него таблицы которые нужны

val db = Database.forURL(
  url = "jdbc:mysql://localhost:3306", // MySQL дың портын көрсету керек
  user = "root",
  password = "root",
  driver = "com.mysql.cj.jdbc.Driver"
)

implicit val executor: ExecutionContext = scala.concurrent.ExecutionContext.global

val newsTable = TableQuery[NewsTable]
val styleTable = TableQuery[StyleTable]
val tagTable = TableQuery[TagTable]
val viewedUsersTable = TableQuery[ViewedUsersTable]

val setupAction = DBIO.seq(
  sqlu"CREATE DATABASE IF NOT EXISTS univer",
  sqlu"USE univer",
  (newsTable.schema ++ styleTable.schema ++ tagTable.schema ++ viewedUsersTable.schema).create
)

val setupFuture = db.run(setupAction)

setupFuture.onComplete {
  case Success(_) =>
    println("База данных и таблица успешно созданы")
  case Failure(exception) =>
    println(s"Произошла ошибка при создании базы данных и таблицы: ${exception.getMessage}")
}

// Ждем завершения будущего
Await.ready(setupFuture, Duration.Inf)
println("Created")
