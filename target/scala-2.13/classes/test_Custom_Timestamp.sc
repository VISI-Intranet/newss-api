import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read

import java.sql.Timestamp
import java.text.SimpleDateFormat

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


case class MyData(name: String, timestamp: Timestamp)

// Создаем объект форматтера для десериализации
val customTimestampSerializer = new CustomTimestampSerializer

// Устанавливаем форматтеры с кастомным сериализатором и десериализатором
implicit val formats: Formats = DefaultFormats + customTimestampSerializer

// Сериализация и десериализация
val myData = MyData("example", new Timestamp(System.currentTimeMillis()))
val jsonString = Serialization.write(myData)
val deserializedData = read[MyData](jsonString)

println(jsonString)
println(deserializedData.timestamp.getTime)
