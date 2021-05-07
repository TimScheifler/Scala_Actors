
import akka.actor.{Actor, ActorLogging, ActorRef}

class Actor_1_4(stringReader: ActorRef) extends Actor with ActorLogging {

  def processLine(line: String) = {
    stringReader ! line
  }

  override def receive: Receive = {

    case path:String =>{
      val bufferedSource = io.Source.fromFile(path)
      for(line <- bufferedSource.getLines().drop(1)) {
        processLine(line)
      }
      log.info("Done.")
    }
    case _ => log.warning("unbekannten Nachricht")
  }
}
