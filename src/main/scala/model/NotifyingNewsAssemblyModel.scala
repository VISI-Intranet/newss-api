package model

import slick.jdbc.MySQLProfile.api._

import java.sql.Date
case class NotifyingNewsModel(id: Option[Int], // класс служить для автоматического создания новости издругих сервисов
                     authorId: Option[Int],
                     content: Option[String],
                     titel:Option[String],
                     date: Option[Date] // дата создания при отправке из другово сервиса НЕ указывается
                             ) extends Product with Serializable


class NotifyingNewsTable(tag: Tag) extends Table[NotifyingNewsModel](tag, "notifyingNews") {
  def id: Rep[Option[Int]] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def authorId = column[Int]("authorId")
  def content = column[String]("content")
  def date = column[Date]("date")
  def titel = column[String]("titel")

  def * = (
    id,
    authorId,
    content,
    date,
    titel
  ).mapTo[NotifyingNewsModel]
}

