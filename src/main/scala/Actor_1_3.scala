import java.sql.{Connection, DriverManager, Timestamp}

import akka.actor.{Actor, ActorLogging, ActorRef}
import java.text.SimpleDateFormat

class Actor_1_3(meanActor: ActorRef) extends Actor with ActorLogging{

  Class.forName("org.h2.Driver")
  val conn: Connection = DriverManager.getConnection("jdbc:h2:~/h2test", "", "")

  def updateAndMean(add: Add): Unit = add match {
    case Add(timestamp: Timestamp, f:Float) =>
      meanActor ! add
  }

  def dateTimeFormatter(str: String): String = {
    //01.01.2009 00:10:00
    //println("unformatted: " + str)
    val inputFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
    //2021-05-06 17:11:00.000000
    val outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val formattedDate = outputFormat.format(inputFormat.parse(str))
    //println("formatted  : " + formattedDate)
    formattedDate
  }

  override def receive: Receive = {
    case s: String =>{
      val list = s.split(",")
      val add = Add(Timestamp.valueOf(dateTimeFormatter(list(0))), list(2).toFloat)
      updateAndMean(add)
    }
  }
}