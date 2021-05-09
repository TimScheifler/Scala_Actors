import java.io.FileNotFoundException
import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.io.Source.fromFile

class Actor_1_4(stringReader: ActorRef) extends Actor with ActorLogging {

  private def processLine(line: String): Unit = {
    stringReader ! line
  }

  override def receive: Receive = {

    case path:String =>
      try {
        val bufferedSource = fromFile(path)
        for (line <- bufferedSource.getLines().drop(1)) {
          processLine(line)
        }
      }catch {
        case e: FileNotFoundException => log.error("ERROR! File could not be found. " + e)
      }finally {
        log.info("Done.")
        //context.stop(self)
      }
    case _ =>
      log.warning("Actor_4: Eingabe konnte nicht verarbeitet werden")
  }
}
