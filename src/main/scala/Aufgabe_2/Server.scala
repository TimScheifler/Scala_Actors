import java.sql.Timestamp

import Aufgabe_1.Actor_1_1
import Aufgabe_2.Utils
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.PathMatchers._
import akka.http.scaladsl.server.Directives.{complete, get, path, pathPrefix}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object Server extends App {

  implicit val actorSystem: ActorSystem = Utils.createSystem("/application.conf", "hfu")
  implicit val timeout: Timeout = 5.seconds
  val actor_1_1 = actorSystem.actorOf(Props[Actor_1_1], name = "ServerActor")

  def returnMean(time: String, result: Any) = {
    val whenString = "When: " + time.replace("_", "T")
    if (result != Utils.DEFAULT_VALUE)
      whenString + " Mean: " + result.toString
    else
      whenString
  }

  lazy val apiRoutes = pathPrefix("when") {

    path(Segment) { time =>
      get {
        val future: Future[Any] = actor_1_1 ? Timestamp.valueOf(Utils.parseDateTime(time)).toString
        val result: Any = Await.result(future, timeout.duration)
        complete {
          returnMean(time, result)
        }
      }
    }
  }
  Http().newServerAt("localhost", 8080).bind(apiRoutes)
}