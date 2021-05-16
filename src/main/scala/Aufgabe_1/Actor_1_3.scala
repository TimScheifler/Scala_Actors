package Aufgabe_1

import java.sql.Timestamp
import java.text.SimpleDateFormat

import akka.actor.{Actor, ActorLogging, ActorRef}

class Actor_1_3(meanActor: ActorRef) extends Actor with ActorLogging{

  private def updateAndMean(tat: TemperatureAtTime): Unit = tat match {
    case tat: TemperatureAtTime =>
      meanActor ! tat
  }

  def dateTimeFormatter(str: String): String = {
    val inputFormat = new SimpleDateFormat("dd.MM.yyyy_HH:mm:ss")
    val outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    outputFormat.format(inputFormat.parse(str))
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