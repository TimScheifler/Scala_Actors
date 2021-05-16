package Aufgabe_1

import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, ActorSelection}
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class Actor_1_2 extends Actor with ActorLogging{

  val path = "akka://hfu@127.0.0.1:2565/user/server-actor"
  val server: ActorSelection = context.actorSelection(path)
  val values = new ListBuffer[TemperatureAtTime]
  implicit val timeout: Timeout = 5.seconds

  override def receive: Receive = {
    case tat: TemperatureAtTime =>
      server ! TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f))
    case f:Float =>
      log.info("F: "+f)

    case s:String =>
      val future: Future[Any] = server ? s
      val result: Any = Await.result(future, timeout.duration)
      log.info("Result: " +result.toString)
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