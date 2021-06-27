package Aufgabe_1

import akka.actor.{Actor, ActorLogging, ActorSelection, RootActorPath}
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.MemberUp

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