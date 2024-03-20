import org.json4s.AsJsonInput.stringAsJsonInput
import org.json4s.JsonAST.JValue
import org.json4s.{DefaultFormats, JObject, Serialization, jackson}
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import routing.TagRoute.customTimestampSerializer

val json =
  s"""{
     |"jsonAction":"event.create_petitionPOST",
     |"body":{
     |   "name":"createdPetition.name",
     |   "description":"createdPetition.description",
     |   "goalOfVotes":"createdPetition.goalOfVotes"
     | }
     |}""".stripMargin

implicit val serialization: Serialization = jackson.Serialization
implicit val formats: DefaultFormats = DefaultFormats

val json1 = parse(json)
val body = (json1 \ "body" \ "name").extract[String]

println(body)