import java.sql.{Connection, DriverManager, ResultSet, Timestamp}

import akka.actor.{Actor, ActorLogging, ActorRef}

class Actor_1_2(adder: ActorRef) extends Actor with ActorLogging{

  Class.forName("org.h2.Driver")
  val conn: Connection = DriverManager.getConnection("jdbc:h2:~/h2test", "", "")

  def updateAndMeasure(add: Add): Unit = add match{
    case Add(timestamp: Timestamp, f:Float) =>
      adder ! add
  }

  override def receive: Receive = {
    case add: Add => {
      updateAndMeasure(add)
      //val mean = computeMean(getMeasurementsOfPast24Hours())
      //log.info("Mean = " + mean)
    }
  }

  def computeMean(rs: ResultSet): Float ={
    if(!rs.isBeforeFirst) 0
    else {
      val tmp = Iterator.from(0).takeWhile(_ => rs.next()).map(_ => rs.getInt(1)).toList
      tmp.sum/tmp.size
    }
  }

  def getMeasurementsOfPast24Hours(): ResultSet ={
    val prepStatement = conn.prepareStatement(
      """SELECT MESSWERT FROM bvs_aufgabe_1 WHERE ZEITSTEMPEL >= DATEADD(hh, -24, CURRENT_TIMESTAMP())""")
    prepStatement.executeQuery()
  }
}