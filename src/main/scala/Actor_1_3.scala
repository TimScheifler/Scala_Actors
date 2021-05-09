import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, ActorRef}
import java.text.SimpleDateFormat

class Actor_1_3(meanActor: ActorRef) extends Actor with ActorLogging{

  private def updateAndMean(tat: TemperatureAtTime): Unit = tat match {
    case tat: TemperatureAtTime =>
      meanActor ! tat
  }

  private def dateTimeFormatter(str: String): String = {
    //01.01.2009 00:10:00
    val inputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    //2021-05-06 17:11:00
    val outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val formattedDate = outputFormat.format(inputFormat.parse(str))
    formattedDate
  }

  override def receive: Receive = {
    case s: String =>
      val list = s.split(",")
      val tat = TemperatureAtTime(Timestamp.valueOf(dateTimeFormatter(list(0))), list(2).toFloat)
      updateAndMean(tat)
    case _ =>
      log.warning("Actor_3: Eingabe konnte nicht verarbeitet werden")
  }
}