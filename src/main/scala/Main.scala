import java.sql.Timestamp

import akka.actor.Props

object Main extends App{
  val system = Utils.createSystem("/client.conf", "hfu")
  system.actorOf(Props[Actor_1_2], name = "client-actor")
  val props1 = Props(new Actor_1_2)
  val actor_2 = system.actorOf(props1, name="ClientActor")
  //val props2 = Props(new Actor_1_3(actor_2))
  //val actor_3 = system.actorOf(props2, name="Actor3")
  //val props3 = Props(new Actor_1_4(actor_3))
  //val actor_4 = system.actorOf(props3, name="Actor4")

  val timestamp = Timestamp.valueOf("2009-01-07 22:30:00")
  actor_2 ! TemperatureAtTime(timestamp , -10.44f)

  //actor_4 ! ".\\src\\main\\resources\\small_jena.csv"
}
