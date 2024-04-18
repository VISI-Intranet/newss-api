package repository

import Main._
import model._
import model.newsComponents.CommentModel
import slick.jdbc.MySQLProfile.api._
import repository.blockNewsComponents._

import scala.concurrent.Future

object NewsBlockAssembly {
  def getAll(): Future[Seq[CommentModel]] = {
    var nbp = NewsBlockPrimeRepo.getAll()
    var nb = NewsBlockRepo.getAll()
    return
  }

  def getById(id: String): Future[Option[CommentModel]] = {
    val query = CommentQuery.filter(_.id === id).result.headOption
    DatabaseManager.db.run(query)
  }
}
