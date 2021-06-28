package com.bvs_praktikum.actor

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.bvs_praktikum.caseclass.{EOF, LineWithFilePath, TemperatureAtTime}

class A3_TimeFormatter(meanActor: ActorRef) extends Actor with ActorLogging{

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
    case eof: EOF => meanActor ! eof
    case lineWithFilePath: LineWithFilePath =>
      val list = lineWithFilePath.line.split(",")
      val tat = TemperatureAtTime(Timestamp.valueOf(test(list(0))), list(2).toFloat, lineWithFilePath.path)
      updateAndMean(tat)
    case _ =>
      log.warning("Actor_3: Eingabe konnte nicht verarbeitet werden")
  }
}