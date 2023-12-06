package model
import slick.jdbc.MySQLProfile.api._

case class StyleModel(id: Option[Int],
                      content: String,
                      newsId: Int)extends Product with Serializable

class StyleTable(tag: Tag) extends Table[StyleModel](tag, "style") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def content = column[String]("content")
  def newsId = column[Int]("newsId")

  def * = (id, content, newsId).mapTo[StyleModel]
}