package Aufgabe_1

import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, ActorSelection, RootActorPath}
import akka.cluster.{Cluster, Member}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

class Actor_1_2 extends Actor with ActorLogging{

  val cluster = Cluster(context.system)
  var server:Option[ActorSelection] = None

  val values = new ListBuffer[TemperatureAtTime]
  implicit val timeout: Timeout = 5.seconds

  override def receive: Receive = {
    case MemberUp(member)=>
      log.info("Received MemberUp for "+member)
      register(member)
    case state:CurrentClusterState =>
      log.info("Received CurrentClusterState for "+state)
      state.members.filter(_.status==MemberStatus.Up).foreach(register)
    case tat: TemperatureAtTime =>
      server.get ! TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f))

    case f:Float =>
      log.info("F: "+f)

    case s:String =>
      server match{
        case None => log.info("not yet registered...")
        case Some(actor)=>
          log.info("received: "+s)
          val future: Future[Any] = actor ? s
          val result: Any = Await.result(future, timeout.duration)
          log.info("Result: " +result.toString)
      }

    case _ => new RuntimeException("unexpected Message received...")
  }

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])

  override def postStop(): Unit = cluster.unsubscribe(self)

  private def register(member:Member):Unit ={
    if(member.hasRole("server")) {
      log.info("Found Server "+member)
      val actor = context.actorSelection(RootActorPath(member.address) / "user" / "server-actor")
      server = Some(actor)
    }
  }

  private def computeMean(timestamp: Timestamp, f: Float): Float = {
    var sum = 0f
    values+=TemperatureAtTime(timestamp, f)
    if(values.size > 145)
      values.remove(0)
    for(x <- values)
      sum += x.f
    getMeanOfPast24Hours(sum, values.size)
  }
  private def getMeanOfPast24Hours(sum: Float, size: Int): Float =
    sum/size
}