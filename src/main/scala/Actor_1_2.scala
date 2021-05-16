import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging}

import scala.collection.mutable.ListBuffer

class Actor_1_2 extends Actor with ActorLogging{

  val path = "akka://hfu@127.0.0.1:2565/user/server-actor"
  val server = context.actorSelection(path)
  val values = new ListBuffer[TemperatureAtTime]

  override def receive: Receive = {
    case tat: TemperatureAtTime =>
      server ! TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f))
    case f:Float =>
      log.info("F: "+f)
    case _ => new RuntimeException("unexpected Message received...")
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