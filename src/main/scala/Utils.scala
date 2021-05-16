import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import java.io.{InputStreamReader}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {
  private val format = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

  def parseDateTime(text: String) =
    LocalDateTime.parse(text, format)

  def toDateTime(date: LocalDateTime) =
    date.format(format)

  def createSystem(fileName: String, systemName: String): ActorSystem = {
    val resource = getClass.getResourceAsStream(fileName)
    val configReader = new InputStreamReader(resource)
    val config = ConfigFactory.parseReader(configReader).resolve()
    val result = ActorSystem(systemName, config)
    result
  }
}