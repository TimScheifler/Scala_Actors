package com.bvs_praktikum

import akka.actor.Props
import akka.routing.RandomGroup
import com.bvs_praktikum.actor.{A2_MeanCalculator, A3_TimeFormatter, A4_FileReader}

object Main extends App {

  val system = Utils.createSystem("/client.conf", "hfu")
  val actor_1 = system.actorOf(Props[A2_MeanCalculator], name = "client-actor")

  val serverActor_props = Props(new A2_MeanCalculator())
  val serverActor = system.actorOf(serverActor_props, name = "MeanCalculator")

  val timeFormatter_props = Props(new A3_TimeFormatter(serverActor))
  val timeFormatter = system.actorOf(timeFormatter_props, name = "TimeFormatter")

  /**
   * Drei FileReader um zu zeigen, dass es auch mit mehr als zwei FileReadern funktioniert.
   */
  val fileReader_props_1 = Props(new A4_FileReader(timeFormatter))
  val fileReader_1 = system.actorOf(fileReader_props_1, name = "FileReader_1")

  val fileReader_props_2 = Props(new A4_FileReader(timeFormatter))
  val fileReader_2 = system.actorOf(fileReader_props_2, name = "FileReader_2")

  val fileReader_props_3 = Props(new A4_FileReader(timeFormatter))
  val fileReader_3 = system.actorOf(fileReader_props_3, name = "FileReader_3")

  /**
   * Es wird der Path für die drei FileReader definiert. Ansonsten würden die FileReader
   * einen zufälligen / unleserlichen Namen haben.
   */
  val paths = List("/user/FileReader_1", "/user/FileReader_2", "/user/FileReader_3")

  /**
   * Die Namen der FileReader werden der RouterGroup hinzugefügt.
   */
  val routerGroup = system.actorOf(RandomGroup(paths).props(), "random-router-group")

  routerGroup ! ".\\src\\main\\resources\\files\\jena_head.csv"
  routerGroup ! ".\\src\\main\\resources\\files\\jena_tail.csv"
}
