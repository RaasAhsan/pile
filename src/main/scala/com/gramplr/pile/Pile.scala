package com.gramplr.pile

import akka.actor.{Props}
import akka.io.IO
import spray.can.Http
import com.gramplr.pile.core.{Core, RootService}

object Pile extends App with Core {

  val host = "localhost"
  val port = 1337

  IO(Http) ! Http.Bind(system.actorOf(Props[RootService], name = "rootService"), host, port)

}
