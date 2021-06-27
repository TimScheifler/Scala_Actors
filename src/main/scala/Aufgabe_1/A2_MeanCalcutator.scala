package Aufgabe_1
import java.sql.Timestamp

import akka.actor.{ActorSelection, ActorSystem}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

class A2_MeanCalcutator extends RegistrationActor {

  implicit val actorSystem: ActorSystem = context.system

  val listBuffer = new ListBuffer[TemperatureAtTime]

  val values = new ListBuffer[TemperatureAtTime]
  implicit val timeout: Timeout = 5.seconds

  override def receive: Receive = {

    case MemberUp(member)=>log.info("received MemberUp for " + member)
      register(member)
      sendListBuffer()

    case state:CurrentClusterState =>
      log.info("received CurrentClusterState for "+ state)
      state.members.filter(_.status==MemberStatus.Up).foreach(register)

    case tat: TemperatureAtTime =>
      server match {
        case None =>
          listBuffer+=TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f))

        case Some(actorSelection: ActorSelection) =>
          log.info("in some")
          actorSelection ! TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f))
      }

    case _ => new RuntimeException("unexpected Message received...")
  }

  def sendListBuffer(): Unit = {
    server match {
      case None =>
        log.info("still not available")
      case Some(actorSelection: ActorSelection) =>
        log.info("in some")
        if(listBuffer.nonEmpty) {
          listBuffer.foreach(actorSelection ! _)
          listBuffer.clear()
        }
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

  private def getMeanOfPast24Hours(sum: Float, size: Int): Float = sum/size
}