import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}


class ClientActor extends Actor with ActorLogging{
  val path = "akka://hfu@127.0.0.1:2565/user/server-actor"
  val server = context.actorSelection(path)
  server ! "Hello"

  override def receive: Receive = {
    case _ => new RuntimeException("unexpected Message received...")
  }
}
object Aufgabe_1 extends App{

  //val timestamp = Timestamp.valueOf("2009-01-07 22:30:00")
  val system = Utils.createSystem("/client.conf", "hfu")
  //val actor_1 = system.actorOf(Props[Actor_1_1],"Actor1")
  system.actorOf(Props[ClientActor], name = "client-actor")
  //actor_1 ! TemperatureAtTime(timestamp, 4f)

  /*val props1 = Props(new Actor_1_2(actor_1))
  val actor_2 = system.actorOf(props1, name="Actor2")

  val props2 = Props(new Actor_1_3(actor_2))
  val actor_3 = system.actorOf(props2, name="Actor3")

  val props3 = Props(new Actor_1_4(actor_3))
  val actor_4 = system.actorOf(props3, name="Actor4")
*/
  //actor_1 ! TemperatureAtTime(timestamp, -10.44f)
  //actor_2 ! TemperatureAtTime(timestamp , -10.44f)
  //actor_3 ! "07.05.2021 17:03:00,,4.2,,,,,,,,,,,"

  //actor_4 ! ".\\src\\main\\resources\\small_jena.csv"
}