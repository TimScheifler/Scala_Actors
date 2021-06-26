import java.sql.Timestamp

import Aufgabe_2.{ServerActor, Utils}
import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives}
import akka.http.scaladsl.server.PathMatchers._
import akka.http.scaladsl.server.Directives.{complete, get, path, pathEnd, pathPrefix}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object REST_API extends App{

  implicit val actorSystem: ActorSystem = Utils.createSystem("/client.conf", "hfu")
  implicit val timeout: Timeout = 5.seconds
  val serverActor = actorSystem.actorOf(Props[ServerActor], name = "ServerActor")

  def returnMean(time: String, result: Any) = {
    val whenString = "When: " + time.replace("_", "T")
    if (result != Utils.DEFAULT_VALUE)
      whenString + " Mean: " + result.toString
    else
      whenString
  }

  lazy val apiRoutes = Directives.concat(
    pathPrefix("when") {

      path(Segment) { time =>
        get {
          val future: Future[Any] = serverActor ? Timestamp.valueOf(Utils.parseDateTime(time)).toString
          val result: Any = Await.result(future, timeout.duration)
          complete {
            returnMean(time, result)
          }
        }
      }
    },
    path("count"){
      get{
        val future: Future[Any] = serverActor ? "count"
        val result: Any = Await.result(future, timeout.duration)
        complete{
          result.toString
        }
      }

    }
  )
  Http().newServerAt("localhost", 8080).bind(apiRoutes)
}