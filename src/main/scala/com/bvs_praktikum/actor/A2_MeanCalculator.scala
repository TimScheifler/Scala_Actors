package com.bvs_praktikum.actor

import akka.actor.{ActorSelection, ActorSystem}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.util.Timeout
import com.bvs_praktikum.caseclass.{EOF, TemperatureAtTime}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt

class A2_MeanCalculator extends RegistrationActor {

  implicit val actorSystem: ActorSystem = context.system

  var currentPath: String = ""

  val paths = new ListBuffer[String]
  val hashMapBuffer = new mutable.HashMap[String, ListBuffer[TemperatureAtTime]]

  val hashMapValues = new mutable.HashMap[String, ListBuffer[TemperatureAtTime]]

  implicit val timeout: Timeout = 5.seconds

  def initIfHashMapsNonExists() = {
    if(!hashMapBuffer.contains(currentPath))
      hashMapBuffer.put(currentPath, new ListBuffer[TemperatureAtTime] )

    if(!hashMapValues.contains(currentPath))
      hashMapValues.put(currentPath, new ListBuffer[TemperatureAtTime] )
  }

  override def receive: Receive = {

    case MemberUp(member)=>log.info("received MemberUp for " + member)
      register(member)
      initIfHashMapsNonExists()
      for(path<-paths)
        sendListBuffer(hashMapBuffer(path))

    case state:CurrentClusterState => log.info("received CurrentClusterState for "+ state)
      state.members.filter(_.status==MemberStatus.Up).foreach(register)

    case eof: EOF =>
      sendListBuffer(hashMapBuffer(eof.fileName))

    case tat: TemperatureAtTime =>

      currentPath = tat.path
      if(!paths.contains())
        paths+=(currentPath)
      initIfHashMapsNonExists()

      log.info(currentPath)
      server match {
        case None =>
          hashMapBuffer(currentPath).append(computeMean(hashMapValues(currentPath), tat))

        case Some(actorSelection: ActorSelection) =>
          hashMapBuffer(currentPath).append(computeMean(hashMapValues(currentPath), tat))
          sendListBuffer(hashMapBuffer(currentPath))
      }

    case _ => new RuntimeException("unexpected Message received...")
  }

  def sendListBuffer(buffer: ListBuffer[TemperatureAtTime]): Unit = {
    server match {
      case None =>
      case Some(actorSelection: ActorSelection) =>
        if(buffer.nonEmpty) {
          buffer.foreach(actorSelection ! _)
          buffer.clear()
        }
    }
  }

  var count = 0
  private def computeMean(wert: ListBuffer[TemperatureAtTime], tat: TemperatureAtTime): TemperatureAtTime = {
    var sum = 0f
    wert+=tat
    if(wert.size > 145)
      wert.remove(0)

    for(x <- wert)
      sum += x.f
    count = count + 1
    val mean = getMeanOfPast24Hours(sum, wert.size)
    TemperatureAtTime(tat.timestamp, mean, tat.path)
  }

  private def getMeanOfPast24Hours(sum: Float, size: Int): Float = sum/size
}