package Aufgabe_1

import java.sql.{Connection, DriverManager, Timestamp}

import Aufgabe_2.Utils
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

case class TemperatureAtTime(timestamp: Timestamp, f: Float)

class Actor_1_1 extends Actor with ActorLogging{
  private val sqlInsert = """insert into bvs_aufgabe_1(zeitstempel, messwert) values (?, ?)"""
  Class.forName("org.h2.Driver")
  private val conn: Connection = DriverManager.getConnection("jdbc:h2:~/h2test", "", "")
  private val stmtLogBegin = conn.prepareStatement(sqlInsert)

  def receive: Receive = {

    case tat: TemperatureAtTime =>
        log.info("INSERT INTO DB")
        insertIntoDB(tat)

    case "count" =>
      val senderName = sender()
      senderName ! getDBRecord()

    case s:String =>
      val senderName = sender()
      senderName ! getMedianAtGivenTime(s)

    case _ =>
      log.warning("Actor_1: Eingabe konnte nicht verarbeitet werden")

  stmtLogBegin.close()
  }

  def getDBRecord(): Int ={
    val stmtLogBegin = conn.createStatement()
    val rs = stmtLogBegin.executeQuery(
      "SELECT COUNT(*) FROM bvs_aufgabe_1")
    if (rs != null) {
      rs.last()
      log.info("LastRow: "+rs.getInt("COUNT(*)"))
      rs.getInt("COUNT(*)")
    }
    else {
      Utils.DEFAULT_VALUE
    }
  }

  private def getMedianAtGivenTime(s: String): Float = {
    val stmtLogBegin = conn.createStatement()

    val rs = stmtLogBegin.executeQuery(
      "SELECT messwert FROM bvs_aufgabe_1 WHERE zeitstempel = '" + s + "' LIMIT 1")
    if(rs.next()) {
      log.info("returning " + rs.getFloat("messwert") + " to " + sender)
      rs.getFloat("messwert")
    } else {
      Utils.DEFAULT_VALUE
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
object Actor_1 extends App{
  val system = ActorSystem("hfu")
  val server = system.actorOf(Props[Actor_1_1], name = "server-actor")
}