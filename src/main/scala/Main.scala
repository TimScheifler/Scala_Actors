import Aufgabe_1.A2_MeanCalcutator
import Aufgabe_2.{ServerActor, Utils}
import akka.actor.Props

object Main extends App {
  val system = Utils.createSystem("/client.conf", "hfu")
  system.actorOf(Props[ServerActor], name = "client-actor")

  val serverActor_props = Props(new ServerActor)
  val serverActor = system.actorOf(serverActor_props, name = "ServerActor")
  val actor_2_props = Props(new A2_MeanCalcutator(serverActor))
  val actor2 = system.actorOf(actor_2_props, name = "Actor2")

  actor2 ! "2009-01-07 22:30:00"
}