package repository

import model._
import Main._
import model.blockNewsComponents.{NewsBlockPrimeModel, NewsBlockPrimeTable}
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

object NewsBlockPrimeRepo {
  // Определение запроса к таблице блоков основной новости
  val NewsBlockPrimeQuery: TableQuery[NewsBlockPrimeTable] = TableQuery[NewsBlockPrimeTable]

  // Метод для вставки данных блока основной новости в базу данных
  def insertData(data: NewsBlockPrimeModel): Future[Int] = {
    // Создание действия вставки
    val insertAction = NewsBlockPrimeQuery += data
    // Запуск действия вставки в базу данных и возврат фьючера с результатом
    DatabaseManager.db.run(insertAction)
  }

  // Метод для удаления данных блока основной новости из базы данных по идентификатору
  def deleteData(id: Int): Future[Int] = {
    val deleteAction = NewsBlockPrimeQuery.filter(_.id === id).delete
    DatabaseManager.db.run(deleteAction)
  }

  // Метод для обновления данных блока основной новости в базе данных
  def updateData(updatedData: NewsBlockPrimeModel): Future[Int] = {
    val updateAction = NewsBlockPrimeQuery.filter(_.id === updatedData.id)
      .map(newsBlock => (newsBlock.authorId, newsBlock.canComment, newsBlock.userCategory, newsBlock.date, newsBlock.filter, newsBlock.importance, newsBlock.lifetime, newsBlock.titel, newsBlock.like, newsBlock.dislike))
      .update((updatedData.authorId, updatedData.canComment, updatedData.userCategory, updatedData.date, updatedData.filter, updatedData.importance, updatedData.lifetime, updatedData.titel, updatedData.like, updatedData.dislike))
    DatabaseManager.db.run(updateAction)
  }

  // Метод для получения всех блоков основной новости из базы данных
  def getAll(): Future[Seq[NewsBlockPrimeModel]] = {
    val query = NewsBlockPrimeQuery.result
    DatabaseManager.db.run(query)
  }

  // Метод для получения блока основной новости из базы данных по идентификатору
  def getById(id: Int): Future[Option[NewsBlockPrimeModel]] = {
    val query = NewsBlockPrimeQuery.filter(_.id === id).result.headOption
    DatabaseManager.db.run(query)
  }

  // Метод для получения блока основной новости из базы данных по заданному полю и значению
  def getByField(fieldName: String, value: String): Future[Seq[NewsBlockPrimeModel]] = {
    val query = NewsBlockPrimeQuery.filter(table => table.column[String](fieldName) === value).result
    DatabaseManager.db.run(query)
  }
}
