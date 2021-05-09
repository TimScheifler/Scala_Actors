import java.sql.Timestamp
import akka.actor.{Actor, ActorLogging, ActorRef}
import scala.collection.mutable.ListBuffer

class Actor_1_2(adder: ActorRef) extends Actor with ActorLogging{

  val values = new ListBuffer[TemperatureAtTime]

  private def updateAndMeasure(tat: TemperatureAtTime): Unit = tat match{
    case tat: TemperatureAtTime =>
      adder ! tat
  }

  override def receive: Receive = {
    case tat: TemperatureAtTime =>
      updateAndMeasure(TemperatureAtTime(tat.timestamp,computeMean(tat.timestamp, tat.f)))
    case _ =>
      log.warning("Actor_2: Eingabe konnte nicht verarbeitet werden")
  }

  private def computeMean(timestamp: Timestamp, f: Float): Float = {
    values+=TemperatureAtTime(timestamp, f)

    val measurements = new ListBuffer[Float]
    val testTimePeriod: Timestamp = new Timestamp(Timestamp.valueOf(timestamp.toLocalDateTime.minusDays(1)).getTime - 1)

    for(x <- values){
      if(testTimePeriod.before(x.timestamp)){
        measurements+=x.f
      }
    }
    getMeanOfPast24Hours(measurements)
  }

  private def getMeanOfPast24Hours(measurements: ListBuffer[Float]): Float ={
    var sum: Float = 0
    for(y <- measurements){
      sum+=y
    }
    sum/measurements.length
  }
}