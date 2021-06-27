package com.bvs_praktikum.actor

import java.io.FileNotFoundException

import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.io.Source.fromFile

class A4_FileReader(stringReader: ActorRef) extends Actor with ActorLogging {

  private def processLine(line: String): Unit = {
    stringReader ! line
  }

  override def receive: Receive = {

    case path:String =>
      try {
        val bufferedSource = fromFile(path)
        var x = 1
        for (line <- bufferedSource.getLines()) {
          log.info( x + " " + self + " is inserting " + line)
          x = x + 1
          processLine(line)
        }
      }catch {
        case e: FileNotFoundException => log.error("ERROR! File could not be found. " + e)
      }finally {
        log.info("Done.")
      }
    case _ =>
      log.warning("Actor_4: Eingabe konnte nicht verarbeitet werden")
  }
}