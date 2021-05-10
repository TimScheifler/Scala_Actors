import java.sql.{Connection, DriverManager, Timestamp}

import akka.actor.{Actor, ActorLogging, PoisonPill}

case class TemperatureAtTime(timestamp: Timestamp, f: Float)

class Actor_1_1 extends Actor with ActorLogging{
  private val sqlInsert = """insert into bvs_aufgabe_1(zeitstempel, messwert) values (?, ?)"""
  Class.forName("org.h2.Driver")
  private val conn: Connection = DriverManager.getConnection("jdbc:h2:~/h2test", "", "")

  def receive: Receive = {
    case tat: TemperatureAtTime =>
      try {
        insertIntoDB(conn, tat)
      }finally {
        self ! PoisonPill
      }
    case _ =>
      log.warning("Actor_1: Eingabe konnte nicht verarbeitet werden")
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