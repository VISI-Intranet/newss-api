package Alpakka.Handlers

object SendAndWaitHandler {

  val handler: (String, String) => String = (message, routingKey) => {

    println("Hello \n\n\n")
    routingKey match {
      case "univer.teacher-api.studentsByIdGet" =>
        ""
      case "key2" =>

        ""
      case _ =>

        ""
    }

  }

}
