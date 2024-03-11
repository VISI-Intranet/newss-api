package routing

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import org.json4s.{Formats, JString, JValue, MappingException, Serializer, TypeInfo}

import java.sql.Timestamp
import java.text.SimpleDateFormat



trait CustomJson4sSupport extends Json4sSupport{
  // Создаем объект форматтера для десериализации
  val customTimestampSerializer = new CustomTimestampSerializer
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats + customTimestampSerializer

  // Класс кастомного сериализатора
  class CustomTimestampSerializer extends Serializer[Timestamp] {
    private val TimestampClass = classOf[Timestamp]

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Timestamp] = {
      case (TypeInfo(TimestampClass, _), json) => json match {
        case JString(s) => new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s).getTime)
        case _ => throw new MappingException(s"Can't convert $json to Timestamp")
      }
    }

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: Timestamp => JString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(x))
    }
  }
}
