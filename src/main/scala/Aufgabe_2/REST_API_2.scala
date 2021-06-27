import java.sql.Timestamp

import Aufgabe_1.RegistrationActor
import Aufgabe_2.Utils
import akka.actor.{ActorLogging, ActorSelection, ActorSystem, Props}
import akka.cluster.ClusterEvent.{CurrentClusterState, MemberUp}
import akka.cluster.protobuf.msg.ClusterMessages.MemberStatus
import akka.http.javadsl.server.directives.RouteDirectives
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route}
import akka.http.scaladsl.server.PathMatchers._
import akka.http.scaladsl.server.Directives.{complete, get, path, pathPrefix}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.io.StdIn

class REST_API_2 extends RegistrationActor with ActorLogging{

  implicit val actorSystem: ActorSystem = context.system
  implicit val timeout: Timeout = 5.seconds

  override def receive: Receive = {
    case MemberUp(member)=>log.info("received MemberUp for " + member)
      register(member)
      startHttpServer
    case state:CurrentClusterState =>
      log.info("received CurrentClusterState for "+ state)
      state.members.filter(_.status==MemberStatus.Up).foreach(register)
    case _=>
      println("unknown message")
  }

  lazy val apiRoutes: Route = Directives.concat(
    pathPrefix("when") {
      path(Segment) { time =>
        get {
          server match{
            case None =>
              complete(
                "not available"
              )
            case Some(actorSelection: ActorSelection)=>
              log.info("in SOME")
              val future: Future[Any] = actorSelection ? Timestamp.valueOf(Utils.parseDateTime(time)).toString
              val result: Any = Await.result(future, timeout.duration)
              complete {
                returnMean(time, result)
              }
          }
        }
      }
    },
    path("count"){
      get {
          sendString("count")
        }
    },
    path("delete"){
      get {
        sendString("delete")
      }
    }
  )

  private def sendString(s: String): Route={
    server match{
      case None =>
        complete(
          "not available"
        )
      case Some(actorSelection: ActorSelection)=>
        val future: Future[Any] = actorSelection ? s
        val result: Any = Await.result(future, timeout.duration)
        complete {
          "{ rows: " + result.toString + "}"
        }
    }
  }
  private def returnMean(time: String, result: Any) = {
    val whenString = "When: " + time.replace("_", "T")
    if (result != Utils.DEFAULT_VALUE)
      whenString + " Mean: " + result.toString
    else
      whenString
  }
  def startHttpServer(){
    server match{
      case None=>
        println("no HTTP Server available..")

      case Some(actorSelection: ActorSelection)=>
        val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(apiRoutes)
        StdIn.readLine()
    }
  }
}

object startREST_API extends App {
  val actorSystem: ActorSystem = Utils.createSystem("/client.conf", "hfu")
  val valueActor = actorSystem.actorOf(Props[REST_API_2], name = "REST_API")
}
