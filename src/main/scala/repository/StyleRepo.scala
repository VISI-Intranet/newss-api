package repository

import model._

import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Await, Future}

object StyleRepo{
  val StyleQuery: TableQuery[StyleTable] = TableQuery[StyleTable]

  def insertData(data: StyleModel): Future[Int] = {
    val insertAction = StyleQuery += data
    DatabaseManager.db.run(insertAction)
  }

  def deleteData(id: String): Future[Int] = {
    val deleteAction = StyleQuery.filter(_.id === id.toInt).delete
    DatabaseManager.db.run(deleteAction)
  }

  def updateData(updatedData: StyleModel): Future[Int] = {
    val updateAction = StyleQuery.filter(_.id === updatedData.id)
      .map(style => (style.content, style.newsId))
      .update((updatedData.content, updatedData.newsId))

    DatabaseManager.db.run(updateAction)
  }

  def getAll(): Future[Seq[StyleModel]] = {
    val query = StyleQuery.result
    DatabaseManager.db.run(query)
  }

  def getByField(fieldName: String, value: String): Future[Seq[StyleModel]] = {
    val query = StyleQuery.filter(table => table.column[String](fieldName) === value).result
    DatabaseManager.db.run(query)
  }

  def getById(id: String): Future[Seq[StyleModel]] = {
    val query = StyleQuery.filter(_.id === id.toInt).result
    DatabaseManager.db.run(query)
  }


}
