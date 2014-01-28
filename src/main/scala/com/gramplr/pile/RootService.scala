package com.gramplr.pile

import akka.actor._
import spray.http._
import HttpMethods._
import spray.can.Http

class RootService extends Actor {

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      sender ! HttpResponse(entity = "welcome to the gramplr pile service")
    case HttpRequest(GET, Uri.Path("/shorten"), _, _, _) =>
      sender ! HttpResponse(entity = "http://shorten.gramplr.com/")
  }

}
