package repository

import Main._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future
import model._
import model.newsComponents.{CommentModel, CommentTable}

object CommentRepo {
  // Определение запроса к таблице комментариев
  val CommentQuery: TableQuery[CommentTable] = TableQuery[CommentTable]

  // Метод для вставки данных комментария в базу данных
  def insertData(data: CommentModel): Future[Int] = {
    // Создание действия вставки
    val insertAction = CommentQuery += data
    // Запуск действия вставки в базу данных и возврат фьючера с результатом
    DatabaseManager.db.run(insertAction)
  }

  // Метод для удаления данных комментария из базы данных по идентификатору
  def deleteData(id: Int): Future[Int] = {
    val deleteAction = CommentQuery.filter(_.id === id).delete
    DatabaseManager.db.run(deleteAction)
  }

  // Метод для обновления данных комментария в базе данных
  def updateData(updatedData: CommentModel): Future[Int] = {
    val updateAction = CommentQuery.filter(_.id === updatedData.id)
      .map(comment => (comment.newsId, comment.authorId, comment.authorCategory, comment.date, comment.content, comment.rating))
      .update((updatedData.newsId, updatedData.authorId, updatedData.authorCategory, updatedData.date, updatedData.content, updatedData.rating))
    DatabaseManager.db.run(updateAction)
  }

  // Метод для получения всех комментариев из базы данных
  def getAll(): Future[Seq[CommentModel]] = {
    val query = CommentQuery.result
    DatabaseManager.db.run(query)
  }

  // Метод для получения комментария из базы данных по идентификатору
  def getById(id: Int): Future[Option[CommentModel]] = {
    val query = CommentQuery.filter(_.id === id).result.headOption
    DatabaseManager.db.run(query)
  }

  // Метод для получения комментариев из базы данных по заданному полю и значению
  def getByField(fieldName: String, value: String): Future[Seq[CommentModel]] = {
    val query = CommentQuery.filter(table => table.column[String](fieldName) === value).result
    DatabaseManager.db.run(query)
  }
}
