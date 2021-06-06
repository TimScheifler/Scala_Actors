package Aufgabe_1

import java.sql.Timestamp

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.util.control.Breaks.{break, breakable}

class A2_MeanCalcutator(serverActor: ActorRef) extends Actor with ActorLogging{

  val values = new ListBuffer[TemperatureAtTime]
  implicit val timeout: Timeout = 5.seconds

  override def receive: Receive = {
    case tat: TemperatureAtTime =>
      serverActor ! TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f))

    case s:String =>
      tryToRequestTimeFromDB(s, 500, 10)

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

  private def tryToRequestTimeFromDB(s: String, millisToWait: Int, maxRetries: Int): Unit = {
    breakable{
      for(a <- 1 to maxRetries) {
        val result = requestTimeFromDB(s)

        result match{
          case res : Float=>
            log.info("Received result "+res)
            break
          case _ =>
            Thread.sleep(millisToWait)
            log.info("["+a+"] Retrying in " + millisToWait + " millis...")
        }
      }
      log.warning("SERVER DOES NOT RESPOND - Retry later.")
    }
  }

  private def requestTimeFromDB(s: String): Any ={
    val future = serverActor ? s
    Await.result(future, timeout.duration)
  }

  private def getMeanOfPast24Hours(sum: Float, size: Int): Float = sum/size
}