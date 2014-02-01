package com.gramplr.pile

import akka.actor.{Props}
import akka.io.IO
import spray.can.Http
import com.gramplr.pile.core._
import com.gramplr.pile.utils.TimeQuantity._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

object Pile extends App with Core with Config {

  val host: String = getString("host")
  val port: Int = getInt("port")

  IO(Http) ! Http.Bind(system.actorOf(Props[RootService], name = "rootService"), host, port)

  val timeoutActor = system.actorOf(Props[KeepAliveService], name = "keepAliveService")
  system.scheduler.schedule(0 seconds, 1 minutes, timeoutActor, KeepAlive)

}
