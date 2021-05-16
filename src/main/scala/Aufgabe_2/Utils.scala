package Aufgabe_2

import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Utils {

  private val format = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm:ss")
  val DEFAULT_VALUE = -9999999
  def parseDateTime(text: String): LocalDateTime =
    LocalDateTime.parse(text, format)

  def toDateTime(date: LocalDateTime): String =
    date.format(format)

  def createSystem(fileName: String, systemName: String): ActorSystem = {
    val resource = getClass.getResourceAsStream(fileName)
    val configReader = new InputStreamReader(resource)
    val config = ConfigFactory.parseReader(configReader).resolve()
    val result = ActorSystem(systemName, config)
    result
  }
}
