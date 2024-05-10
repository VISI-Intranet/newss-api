package repository

import model._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Await, Future}

import scala.concurrent.Future

object TagRepo{
  val TagQuery: TableQuery[TagTable] = TableQuery[TagTable]

  def insertData(data: TagModel): Future[Int] = {
    val insertAction = TagQuery += data
    DatabaseManager.db.run(insertAction)
  }

  def deleteData(id: String): Future[Int] = {
    val deleteAction = TagQuery.filter(_.id === id.toInt).delete
    DatabaseManager.db.run(deleteAction)
  }

  def updateData(updatedData: TagModel): Future[Int] = {
    val updateAction = TagQuery.filter(_.id === updatedData.id)
      .map(tag => (tag.newsId,tag.teg))
      .update((updatedData.newsId, updatedData.teg))

    DatabaseManager.db.run(updateAction)
  }

  def getAll(): Future[Seq[TagModel]] = {
    val query = TagQuery.result
    DatabaseManager.db.run(query)
  }

  def getByField(fieldName: String, value: String): Future[Seq[TagModel]] = {
    val query = TagQuery.filter(table => table.column[String](fieldName) === value).result
    DatabaseManager.db.run(query)
  }

  def getById(id: String): Future[Seq[TagModel]] = {
    val query = TagQuery.filter(_.id === id.toInt).result
    DatabaseManager.db.run(query)
  }


}

