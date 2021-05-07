import java.sql.{Connection, DriverManager, Timestamp}

import akka.actor.{Actor, ActorLogging}

case class Add(timestamp: Timestamp, f: Float)
case class TimeStampAndMean(timestamp: Timestamp, f: Float)

class Actor_1_1 extends Actor with ActorLogging{

  Class.forName("org.h2.Driver")
  val conn: Connection = DriverManager.getConnection("jdbc:h2:~/h2test", "", "")

  var counter = 0
  def receive = {
    case add: Add => {
      try {
        insertIntoDB(conn, add)
        counter = counter+1
        log.info(counter + "inserted " + add.f)
      }finally {
        //conn.close()
        //context.stop(self)
      }
    }
    case timeStampAndMean: TimeStampAndMean => {
      log.info("received mean " + timeStampAndMean.f + " at " + timeStampAndMean.timestamp)
    }
    case _ => log.warning("Keine neuen Werte wurden hinzugefügt")
    /*case s: String => {
      createDbStructure(conn)
      log.info("created new DB")
    }*/
  }
  /*def createDbStructure(conn: Connection): Unit = {
    val sql = """
      create schema if not exists bvs;

      set schema bvs;

      create table if not exists bvs_aufgabe_1 (
        id int auto_increment primary key,
        zeitstempel timestamp not null,
        messwert int not null);"""
    val stmt = conn.createStatement()
    try{
      stmt.execute(sql)
    }finally {
      stmt.close()
    }
  }*/
  def insertIntoDB(conn: Connection, add: Add): Unit = {
    val sqlInsert = """insert into bvs_aufgabe_1(zeitstempel, messwert) values (?, ?)"""
    val stmtLogBegin = conn.prepareStatement(sqlInsert)
    stmtLogBegin.setTimestamp(1, add.timestamp)
    stmtLogBegin.setFloat(2, add.f)

    stmtLogBegin.executeUpdate()
    stmtLogBegin.close()
    log.info(self.path.name + " from "+sender() + " inserted " + add.f + "into DB")
  }
}