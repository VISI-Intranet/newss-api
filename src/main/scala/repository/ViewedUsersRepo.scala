package repository

import model._
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.{Await, Future}

object ViewedUsersRepo {
  val ViewedUserQuery: TableQuery[ViewedUsersTable] = TableQuery[ViewedUsersTable]

  def insertData(data: ViewedUsersModel): Future[Int] = {
    val insertAction = ViewedUserQuery += data
    DatabaseManager.db.run(insertAction)
  }

  def deleteData(id: String): Future[Int] = {
    val deleteAction = ViewedUserQuery.filter(_.id === id.toInt).delete
    DatabaseManager.db.run(deleteAction)
  }

  def updateData(updatedData: ViewedUsersModel): Future[Int] = {
    val updateAction = ViewedUserQuery.filter(_.id === updatedData.id)
      .map(vum => (vum.userId, vum.userCategory))
      .update((updatedData.userId, updatedData.userCategory))

    DatabaseManager.db.run(updateAction)
  }

  def getAll(): Future[Seq[ViewedUsersModel]] = {
    val query = ViewedUserQuery.result
    DatabaseManager.db.run(query)
  }

  def getByField(fieldName: String, value: String): Future[Seq[ViewedUsersModel]] = {
    val query = ViewedUserQuery.filter(table => table.column[String](fieldName) === value).result
    DatabaseManager.db.run(query)
  }

  def getById(id: String): Future[Seq[ViewedUsersModel]] = {
    val query = ViewedUserQuery.filter(_.id === id.toInt).result
    DatabaseManager.db.run(query)
  }


}
