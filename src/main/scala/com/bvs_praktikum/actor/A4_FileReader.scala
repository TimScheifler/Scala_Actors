package com.bvs_praktikum.actor

import java.io.FileNotFoundException

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.bvs_praktikum.caseclass.{EOF, LineWithFilePath}

import scala.io.Source.fromFile

class A4_FileReader(stringReader: ActorRef) extends Actor with ActorLogging {

  private def processLine(line: String, path: String): Unit = {
    stringReader ! LineWithFilePath(line, path)
  }

  override def receive: Receive = {

    case path:String =>
      try {
        val bufferedSource = fromFile(path)
        var count = 1
        for (line <- bufferedSource.getLines()) {
          log.info(self + " is adding " + count + "th. line..")
          count = count+1
          processLine(line, path)
        }
        stringReader ! EOF(path)
      }catch {
        case e: FileNotFoundException => log.error("ERROR! File could not be found. " + e)
      }finally {
        log.info("Done.")
      }
    case _ =>
      log.warning("Actor_4: Eingabe konnte nicht verarbeitet werden")
  }
}