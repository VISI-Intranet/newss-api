package repository

import model._
import Main._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Await, Future}

object NewsRepo {
  // Определение запроса к таблице новостей
  val NewsQuery: TableQuery[NewsTable] = TableQuery[NewsTable]

  // Метод для вставки данных новости в базу данных
  def insertData(data: NewsModel): Future[Int] = {
    // Создание действия вставки
    val insertAction = NewsQuery += data
    // Запуск действия вставки в базу данных и возврат фьючера с результатом
    DatabaseManager.db.run(insertAction)
  }

  def deleteData(id: String): Future[Int] = {
    val deleteAction = NewsQuery.filter(_.id === id.toInt).delete
    DatabaseManager.db.run(deleteAction)
  }

  def updateData(updatedData: NewsModel): Future[Int] = {
    val updateAction = NewsQuery.filter(_.id === updatedData.id)
      .map(news => (news.authorId, news.canComment, news.category, news.content, news.date, news.filter, news.importance, news.lifetime, news.titel))
      .update((updatedData.authorId, updatedData.canComment, updatedData.category, updatedData.content, updatedData.date, updatedData.filter, updatedData.importance, updatedData.lifetime, updatedData.titel))
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
