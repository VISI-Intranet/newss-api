package model.blockNewsComponents

import model.enumClass.{Filter, Importance, UserCategory}
import slick.jdbc.MySQLProfile.api._

import java.sql.Date


case class NewsBlockPrimeModel(id: Option[Int],
                     authorId: Option[Int],
                     canComment: Option[Boolean],
                     userCategory: Option[UserCategory],
                     date: Option[Date],
                     filter: Option[Filter],
                     importance: Option[Importance],
                     lifetime: Option[Date],
                     titel: Option[String],
                     like: Option[Int],
                     dislike: Option[Int] // нужно чтобы вычислить отношение лайков к дизлайкам
                       ) extends Product with Serializable


class NewsBlockPrimeTable(tag: Tag) extends Table[NewsBlockPrimeModel](tag, "blockNews") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def authorId = column[Int]("authorId")
  def canComment = column[Boolean]("canComment")
  def userCategory = column[UserCategory]("category")
  def date = column[Date]("date")
  def filter = column[Filter]("filter")
  def importance = column[Importance]("importance")
  def lifetime = column[Date]("lifetime")
  def titel = column[String]("titel")

  def like = column[Int]("like")
  def dislike = column[Int]("dislike")
  def * = (
    id,
    authorId,
    canComment,
    userCategory,
    date,
    filter,
    importance,
    lifetime,
    titel,
    like,
    dislike

  ).mapTo[NewsBlockPrimeModel]
}

