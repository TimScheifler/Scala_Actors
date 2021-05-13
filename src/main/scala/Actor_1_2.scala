import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

import scala.collection.mutable.ListBuffer

class Actor_1_2(adder: ActorRef) extends Actor with ActorLogging{
  val values = new ListBuffer[TemperatureAtTime]

  private def updateAndMeasure(tat: TemperatureAtTime): Unit = tat match{
    case tat: TemperatureAtTime =>
      //adder ! tat
  }

  override def receive: Receive = {
    case tat: TemperatureAtTime =>
      updateAndMeasure(TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f)))
    case s:String =>
      log.info("Actor2 received: " + s)
    case _ =>
      log.warning("Actor_2: Eingabe konnte nicht verarbeitet werden")
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

  private def getMeanOfPast24Hours(sum: Float, size: Int): Float ={
    sum/size
  }
}
object Server_02 extends App{
  val system = ActorSystem("hfu")
  val server =system.actorOf(Props[Actor_1_2], name = "server-actor-1")
}