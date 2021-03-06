package com.bvs_praktikum.actor

import akka.actor.{Actor, ActorLogging, ActorSelection, RootActorPath}
import akka.cluster.ClusterEvent.MemberUp
import akka.cluster.{Cluster, Member}

abstract class RegistrationActor extends Actor with ActorLogging {

  val cluster: Cluster = Cluster(context.system)
  var server:Option[ActorSelection] = None

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def register(member:Member):Unit = {
    if (member.hasRole("server")) {
      val actor = context.actorSelection(RootActorPath(member.address) / "user" / "server-actor")
      server = Some(actor)
    }
  }
}