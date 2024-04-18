package repositories

import akka.http.scaladsl.marshallers.sprayjson.*
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.*
import spray.json.DefaultJsonProtocol.*
import spray.json.*
import domain.*

import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import spray.json._
  implicit val userFormat: RootJsonFormat[User] = jsonFormat5(User)
  implicit val userListFormat: RootJsonFormat[List[User]] = listFormat(userFormat)
  implicit val userUpdateRequestFormat: RootJsonFormat[UserUpdateRequest] = jsonFormat5(UserUpdateRequest)

  implicit val petitionFormat: RootJsonFormat[Petition] = jsonFormat8(Petition)
  implicit val petitionListFormat: RootJsonFormat[List[Petition]] = listFormat(petitionFormat)
  implicit val petitionCreateRequestFormat: RootJsonFormat[PetitionCreateRequest] = jsonFormat8(PetitionCreateRequest)
  implicit val petitionUpdateRequestFormat: RootJsonFormat[PetitionUpdateRequest] = jsonFormat8(PetitionUpdateRequest)

  implicit val commentFormat:RootJsonFormat[Comment] = jsonFormat5(Comment)
  implicit val commentListFormat:RootJsonFormat[List[Comment]]=listFormat(commentFormat)
  implicit val commentCreateRequestFormat: RootJsonFormat[CommentCreateRequest] = jsonFormat5(CommentCreateRequest)
  implicit val commentUpdateRequestFormat: RootJsonFormat[CommentUpdateRequest] = jsonFormat5(CommentUpdateRequest)

  implicit val petitionVotingFormat:RootJsonFormat[PetitionVoting] = jsonFormat4(PetitionVoting)
  implicit val petitionVotingListFormat: RootJsonFormat[List[PetitionVoting]]=listFormat(petitionVotingFormat)
  implicit val petitionVotingCreateRequestFormat: RootJsonFormat[PetitionVotingCreateRequest] = jsonFormat4(PetitionVotingCreateRequest)
  implicit val petitionVotingUpdateRequestFormat: RootJsonFormat[PetitionVotingUpdateRequest] = jsonFormat4(PetitionVotingUpdateRequest)

}