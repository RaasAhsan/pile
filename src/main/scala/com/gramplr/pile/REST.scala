package com.gramplr.pile

import akka.actor.{Props}
import akka.io.IO
import spray.can.Http

object REST extends App with Core {

  IO(Http) ! Http.Bind(system.actorOf(Props[RootService], name = "handler"), "localhost", 1337)

}
