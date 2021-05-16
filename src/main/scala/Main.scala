import java.sql.Timestamp

import Aufgabe_1.{Actor_1_2, TemperatureAtTime}
import Aufgabe_2.Utils
import akka.actor.Props

object Main extends App {
  val system = Utils.createSystem("/client.conf", "hfu")
  system.actorOf(Props[Actor_1_2], name = "client-actor")
  val props1 = Props(new Actor_1_2)
  val actor_2 = system.actorOf(props1, name = "ClientActor")

  val timestamp = Timestamp.valueOf("2009-01-07 22:30:00")
  actor_2 ! TemperatureAtTime(timestamp, -10.44f)
}