package model

import slick.jdbc.MySQLProfile.api._

case class TagModel(id: Option[Int],
                    newsId: Int,
                    teg: String) extends Product with Serializable

class TagTable(tag: Tag) extends Table[TagModel](tag, "teg") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def newsId = column[Int]("NewsId")
  def teg = column[String]("teg")

  def * = (id, newsId, teg).mapTo[TagModel]
}