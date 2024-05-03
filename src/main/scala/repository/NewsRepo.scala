package repository

import model._
import DBManager._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Await, Future}


object NewsRepo {
  val NewsQuery:TableQuery[NewsTable] = TableQuery[NewsTable]

  def insertData(data: NewsModel): Future[Int] = {
    val insertAction = NewsQuery += data
    DatabaseManager.db.run(insertAction)
  }

  def deleteData(id: String): Future[Int] = {
    val deleteAction = NewsQuery.filter(_.id === id.toInt).delete
    DatabaseManager.db.run(deleteAction)
  }

  def deleteDataEvent(id: Int): Future[Int] = {
    val deleteAction = NewsQuery.filter(_.event_id === id).delete
    DatabaseManager.db.run(deleteAction)
  }

  def updateData(updatedData: NewsModel): Future[Int] = {
    val updateAction = NewsQuery.filter(_.id === updatedData.id)
      .map(news => (news.event_id,news.authorId, news.canComment, news.category, news.content, news.date, news.filter, news.importance, news.time, news.titel))
      .update((updatedData.event_id,updatedData.authorId, updatedData.canComment, updatedData.category, updatedData.content, updatedData.date, updatedData.filter, updatedData.importance, updatedData.time, updatedData.titel))

    DatabaseManager.db.run(updateAction)
  }
  def updateDataEvent(updatedData: NewsModel): Future[Int] = {
    val updateAction = NewsQuery.filter(_.event_id === updatedData.event_id)
      .map(news => (news.event_id,news.authorId, news.canComment, news.category, news.content, news.date, news.filter, news.importance, news.time, news.titel))
      .update((updatedData.event_id,updatedData.authorId, updatedData.canComment, updatedData.category, updatedData.content, updatedData.date, updatedData.filter, updatedData.importance, updatedData.time, updatedData.titel))

    DatabaseManager.db.run(updateAction)
  }
  def getAll(): Future[Seq[NewsModel]] = {
    val query = NewsQuery.result
    DatabaseManager.db.run(query)
  }

  def getById(id: String): Future[Seq[NewsModel]] = {
    val query = NewsQuery.filter(_.id === id.toInt).result
    DatabaseManager.db.run(query)
  }


  def getByField(fieldName: String, value: String): Future[Seq[NewsModel]] = {
    val query = NewsQuery.filter(table => table.column[String](fieldName) === value).result
    DatabaseManager.db.run(query)
  }

}
