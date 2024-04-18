package model
import java.sql.{Date}
import model.enumClass.UserCategory
import slick.jdbc.MySQLProfile.api._
import java.time.LocalDate

case class CommentModel(
                         id : Option[Int],
                         newsId: Option[Int],
                         authorId: Option[Int],
                         authorCategory: Option[UserCategory],
                         date: Option[Date],
                         content: Option[String],
                         rating: Option[Int]
                       )extends Product with Serializable

class CommentTable(tag: Tag) extends Table[CommentModel](tag, "comment") {
  def id = column[Option[Int]]("id", O.PrimaryKey)
  def newsId = column[Option[Int]]("newsId")
  def authorId = column[Option[Int]]("authorId")
  def authorCategory = column[Option[UserCategory]]("authorCategory")
  def date = column[Date]("date", O.Default(Date.valueOf(LocalDate.now())))
  def content: Rep[Option[String]] = column[Option[String]]("content")
  def rating: Rep[Option[Int]] = column[Int]("rating", O.Default(0))

  def * = (id, newsId, authorId, authorCategory, date, content, rating).mapTo[CommentModel]
}
