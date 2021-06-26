package Aufgabe_2

import akka.actor.{Actor, ActorLogging, ActorSelection, RootActorPath}
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

case class NotYetRegistered()

class ServerActor extends Actor with ActorLogging{

  val cluster: Cluster = Cluster(context.system)
  var server:Option[ActorSelection] = None

  implicit val timeout: Timeout = 5.seconds

  override def receive: Receive = {
    case MemberUp(member)=>
      register(member)

    case state:CurrentClusterState =>
      state.members.filter(_.status==MemberStatus.Up).foreach(register)

    case "count" =>
      server match {
        case None =>
          val senderName = sender()
          senderName ! NotYetRegistered
        case Some(actor) =>
          log.info("In DynamicActor COUNT")
          val senderName = sender()
          val future = actor ? "count"
          val result = Await.result(future, timeout.duration)
          senderName ! result
      }

    case s:String =>
      server match{
        case None =>
          val senderName = sender()
          senderName ! NotYetRegistered
        case Some(actor)=>
          val senderName = sender()
          val future = actor ? s
          val result = Await.result(future, timeout.duration)
          senderName ! result
      }
  }

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  private def register(member:Member):Unit ={
    if(member.hasRole("server")) {
      val actor = context.actorSelection(RootActorPath(member.address) / "user" / "server-actor")
      server = Some(actor)
    }
  }
}