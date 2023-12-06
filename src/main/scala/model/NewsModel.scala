package model
import akka.http.scaladsl.model.DateTime
import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import DBManager.DatabaseManager._

case class NewsModel(id: Option[Int],
                     authorId: Int,
                     canComment: Boolean,
                     category: String,
                     content: String,
                     date: Timestamp,
                     filter: String,
                     importance: String,
                     time: Timestamp,
                     titel:String ) extends Product with Serializable


class NewsTable(tag: Tag) extends Table[NewsModel](tag, "news") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def authorId = column[Int]("authorId")
  def canComment = column[Boolean]("canComment")
  def category = column[String]("category")
  def content = column[String]("content")
  def date = column[Timestamp]("date")
  def filter = column[String]("filter")
  def importance = column[String]("importance")
  def time = column[Timestamp]("time")
  def titel = column[String]("titel")

  def * = (
    id,
    authorId,
    canComment,
    category,
    content,
    date,
    filter,
    importance,
    time,
    titel
  ).mapTo[NewsModel]
}

