package Aufgabe_1

import java.sql.{Connection, DriverManager, Timestamp}
import Aufgabe_2.Utils.DEFAULT_VALUE
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

case class TemperatureAtTime(timestamp: Timestamp, f: Float)

class Actor_1_1 extends Actor with ActorLogging{
  private val sqlInsert = """insert into bvs_aufgabe_1(zeitstempel, messwert) values (?, ?)"""
  Class.forName("org.h2.Driver")
  private val conn: Connection = DriverManager.getConnection("jdbc:h2:~/h2test", "", "")
  private val stmtLogBegin = conn.prepareStatement(sqlInsert)
  var x = 0
  def receive: Receive = {
    case tat: TemperatureAtTime =>
        x+=1
        log.info(x+" | INSERT INTO DB")
        insertIntoDB(tat)
    case s:String =>
      log.info("in getMedianAtGivenTime")
      sender() ! getMedianAtGivenTime(s)
    case _ =>
      log.warning("Actor_1: Eingabe konnte nicht verarbeitet werden")
  stmtLogBegin.close()
  }

  private def getMedianAtGivenTime(s: String): Float = {
    val stmtLogBegin = conn.createStatement()
    val rs = stmtLogBegin.executeQuery(
      "SELECT messwert FROM bvs_aufgabe_1 WHERE zeitstempel = '" + s + "' LIMIT 1")
    if(rs.next()) {
      log.info("returning " + rs.getFloat("messwert"))
      rs.getFloat("messwert")
    } else {
      DEFAULT_VALUE
    }
  }

  private def insertIntoDB(tat: TemperatureAtTime): Unit = {

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