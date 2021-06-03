package Aufgabe_1

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import Aufgabe_2.Utils.format
import akka.actor.{Actor, ActorLogging, ActorRef}

class Actor_1_3(meanActor: ActorRef) extends Actor with ActorLogging{

  private def updateAndMean(tat: TemperatureAtTime): Unit = tat match {
    case tat: TemperatureAtTime =>
      meanActor ! tat
  }

  def dateTimeFormatter(str: String): String = {
    val inputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    val outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    outputFormat.format(inputFormat.parse(str))
  }

  def test(str: String): LocalDateTime = {
    val format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    LocalDateTime.parse(str, format)
  }

  override def receive: Receive = {
    case s: String =>
      val list = s.split(",")
      val tat = TemperatureAtTime(Timestamp.valueOf(test(list(0))), list(2).toFloat)
      //val tat = TemperatureAtTime(Timestamp.valueOf(dateTimeFormatter(list(0))), list(2).toFloat)
      updateAndMean(tat)
    case _ =>
      log.warning("Actor_3: Eingabe konnte nicht verarbeitet werden")
  }
}