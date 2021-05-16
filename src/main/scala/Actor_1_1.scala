import java.sql.{Connection, DriverManager, Timestamp}

import akka.actor.TypedActor.dispatcher
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.concurrent.Future

case class TemperatureAtTime(timestamp: Timestamp, f: Float)

class Actor_1_1 extends Actor with ActorLogging{
  private val sqlInsert = """insert into bvs_aufgabe_1(zeitstempel, messwert) values (?, ?)"""
  Class.forName("org.h2.Driver")
  private val conn: Connection = DriverManager.getConnection("jdbc:h2:~/h2test", "", "")
  var x = 0
  def receive: Receive = {
    case tat: TemperatureAtTime =>
        x+=1
        log.info(x+" | INSERT INTO DB")
        insertIntoDB(conn, tat)
    case s:String =>
      val mySender = sender()
      Future {
        mySender ! getMedianAtGivenTime(s)
      }

    case _ =>
      log.warning("Actor_1: Eingabe konnte nicht verarbeitet werden")
  }

  private def getMedianAtGivenTime(s: String): Float = {
    val stmtLogBegin = conn.createStatement()
    val rs = stmtLogBegin.executeQuery(
      "SELECT messwert FROM bvs_aufgabe_1 WHERE zeitstempel = '" + s + "' LIMIT 1")
    if(rs.next()) {
      log.info("returning " + rs.getFloat("messwert"))
      rs.getFloat("messwert")
    } else {
      -9999999
    }
  }
  private def insertIntoDB(conn: Connection, tat: TemperatureAtTime): Unit = {
    val stmtLogBegin = conn.prepareStatement(sqlInsert)
    stmtLogBegin.setTimestamp(1, tat.timestamp)
    stmtLogBegin.setFloat(2, tat.f)

    stmtLogBegin.executeUpdate()
    stmtLogBegin.close()
    log.info(self.path.name + " inserted (" + tat.timestamp + " | " + tat.f + ") into DB")
  }
}
object Server_01 extends App{
  val system = ActorSystem("hfu")
  val server = system.actorOf(Props[Actor_1_1], name = "server-actor")
}