package model.blockNewsComponents

import slick.jdbc.MySQLProfile.api._

case class NewsBlockModel(id: Option[Int], // блоки одной новости
                      nbpId: Option[Int], // id основного блока к которому будут дополняться блоки
                      content: Option[String],
                      contentType: Option[String],
                      order: Option[Int], // порядковый номер блока
                      css: Option[String]
                      )extends Product with Serializable

class NewsBlockTable(tag: Tag) extends Table[NewsBlockModel](tag, "block") {
  def id: Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
  def nbpId: Rep[Option[Int]] = column[Option[Int]]("nbpId")
  def content: Rep[Option[String]] = column[Option[String]]("content")
  def contentType: Rep[Option[String]] = column[Option[String]]("contentType")
  def order: Rep[Option[Int]] = column[Option[Int]]("order")
  def css: Rep[Option[String]] = column[Option[String]]("css")

  def * = (id, nbpId, content, contentType, order, css).mapTo[NewsBlockModel]
}