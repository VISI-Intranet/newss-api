package model

import slick.jdbc.MySQLProfile.api._
case class ViewedUsersModel(id: Option[Int],
                            c: String,
                            userId: Int) extends Product with Serializable


class ViewedUsersTable(tag: Tag) extends Table[ViewedUsersModel](tag, "viewedusers") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def userCategory = column[String]("userCategory")
  def userId = column[Int]("userId")

  def * = (id, userCategory, userId).mapTo[ViewedUsersModel]
}