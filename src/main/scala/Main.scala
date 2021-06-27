import Aufgabe_1.{A2_MeanCalcutator, A3_TimeFormatter, A4_FileReader}
import Aufgabe_2.Utils
import java.sql.Timestamp

import akka.actor.Props
import akka.routing.RandomGroup

object Main extends App {

  val timestamp = Timestamp.valueOf("2009-01-07 22:30:00")

  val system = Utils.createSystem("/client.conf", "hfu")
  val actor_1 = system.actorOf(Props[A2_MeanCalcutator], name = "client-actor")

  val serverActor_props = Props(new A2_MeanCalcutator())
  val serverActor = system.actorOf(serverActor_props, name = "MeanCalculator")

  val timeFormatter_props = Props(new A3_TimeFormatter(serverActor))
  val timeFormatter = system.actorOf(timeFormatter_props, name="TimeFormatter")

  val fileReader_props_1 = Props(new A4_FileReader(timeFormatter))
  val fileReader_1 = system.actorOf(fileReader_props_1, name="FileReader_1")

  val fileReader_props_2 = Props(new A4_FileReader(timeFormatter))
  val fileReader_2 = system.actorOf(fileReader_props_2, name="FileReader_2")

  val fileReader_props_3 = Props(new A4_FileReader(timeFormatter))
  val fileReader_3 = system.actorOf(fileReader_props_3, name="FileReader_3")

  val paths = List("/user/FileReader_1", "/user/FileReader_2", "/user/FileReader_3")

  val routerGroup = system.actorOf(RandomGroup(paths).props(), "random-router-group")

  routerGroup ! ".\\src\\main\\resources\\files\\jena_head.csv"
  routerGroup ! ".\\src\\main\\resources\\files\\jena_tail.csv"
}