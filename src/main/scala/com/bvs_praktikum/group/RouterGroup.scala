package com.bvs_praktikum.group

import akka.actor.{Actor, ActorLogging}

class RouterGroup (routees: List[String]) extends Actor with ActorLogging{
  def receive: Receive = {
    case path: String =>
      log.info("RouterGroup - received Message!")
      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward path
  }
}