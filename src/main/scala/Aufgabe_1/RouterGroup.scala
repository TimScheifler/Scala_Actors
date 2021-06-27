package Aufgabe_1

import akka.actor.{Actor, ActorLogging}

class RouterGroup (routees: List[String]) extends Actor with ActorLogging{
  def receive = {
    case path: String =>
      log.info("RouterGroup - received Message!")
      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward path
  }
}
