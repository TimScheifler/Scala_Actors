import java.sql.{Connection, DriverManager, Timestamp}
import java.util.Calendar

import akka.actor.{Actor, ActorSystem, Props}

object SimpleActor extends App{
  class SimpleActor extends Actor {

    Class.forName("org.h2.Driver")
    //meine JDBC URL lautet: jdbc:h2:file:C:\Users\timsc\Downloads\munge-maven-plugin-munge-maven-plugin-1.0\NextTry\db\bvs
    val conn: Connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "")

    def receive = {
      case i: Int => {
        try {
          insertIntoDB(conn, i)
          println(self.path.name + " from "+sender() + " inserted " + i + "into DB")
        } finally {
          conn.close()
        }
      }
      case s: String => {
        createDbStructure(conn)
        println("created new DB")
      }
    }

    def createDbStructure(conn: Connection): Unit = {
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
    }

    def insertIntoDB(conn: Connection, i: Int): Unit = {
      val sqlInsert = """insert into bvs_aufgabe_1(zeitstempel, messwert) values (?, ?)"""
      val stmtLogBegin = conn.prepareStatement(sqlInsert)
      stmtLogBegin.setTimestamp(1, new Timestamp(Calendar.getInstance().getTime().getTime()))
      stmtLogBegin.setInt(2, i)
      stmtLogBegin.executeUpdate()
      stmtLogBegin.close()
    }
  }
  val value = 42
  val system = ActorSystem("SimpleSystem")
  val actor = system.actorOf(Props[SimpleActor],"SimpleActor1")
  actor ! (value)
}