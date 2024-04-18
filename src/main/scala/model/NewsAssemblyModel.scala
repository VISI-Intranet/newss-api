package model

import model.enumClass.{Filter, Importance, UserCategory}
import slick.jdbc.MySQLProfile.api._

import java.sql.Date

case class NewsModel(id: Option[Int],
                     authorId: Option[Int],
                     canComment: Option[Boolean],
                     userCategory: Option[UserCategory],
                     filter: Option[Filter],
                     importance: Option[Importance],
                     date: Option[Date],
                     lifetime: Option[Date],
                     content: Option[String],
                     titel:Option[String],
                     sample: Option[String], // номер шаблона по умолчанию
                     img: Option[String], // img это имя изображения внутри папки images
                     like: Option[Int],
                     dislike: Option[Int] // нужно чтобы вычислить отношение лайков к дизлайкам
                    ) extends Product with Serializable


class NewsTable(tag: Tag) extends Table[NewsModel](tag, "news") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def authorId = column[Int]("authorId")
  def canComment = column[Boolean]("canComment")
  def userCategory = column[String]("category")
  def filter = column[String]("filter")
  def importance = column[String]("importance")
  def date = column[Date]("date")
  def lifetime = column[Date]("lifetime")

  def content = column[String]("content")
  def titel = column[String]("titel")
  def sample = column[String]("titel")
  def img = column[String]("titel")
  def like = column[String]("titel")
  def dislike = column[String]("titel")
  def * = (
    id,
    authorId,
    canComment,
    userCategory,
    filter,
    importance,
    date,
    lifetime,
    content,
    titel,
    sample,
    img,
    like,
    dislike
  ).mapTo[NewsModel]
}

