package com.gramplr.pile.core

import akka.actor._
import spray.http._
import HttpMethods._
import spray.can.Http
import com.gramplr.pile.db.Database

class RootService extends Actor with Database {

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      sender ! HttpResponse(entity = "welcome to the gramplr pile service")
    case HttpRequest(GET, Uri.Path("/shorten"), _, _, _) => {
      sender ! HttpResponse(entity = "http://shorten.gramplr.com/")
      insertShorten("abcdefg", "thisisrealurl")
    }
  }

}
