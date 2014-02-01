package com.gramplr.pile.core

import akka.actor._
import spray.http._
import HttpMethods._
import spray.can.Http
import com.gramplr.pile.db.Database
import com.gramplr.pile.utils.{URLReader, Keygen}
import spray.http.HttpHeaders.Location
import play.api.libs.json.Json

class RootService extends Actor with Database with Config {

  lazy val baseurl = getString("url")

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      sender ! HttpResponse(entity = "Welcome to the Pile service!")
    case r@HttpRequest(GET, Uri.Path("/shorten"), _, _, _) => {
      val map = URLReader.getURLs(r.uri.query.getOrElse("url", ""))
      map.foreach(x => insertShorten(x._2, x._1))

      val back = Json.obj("urls" -> Json.toJson(map.map(x => baseurl + "/" + x._2)))

      sender ! HttpResponse(entity = Json.stringify(back))
    }
    case HttpRequest(GET, Uri.Path("/notfound"), _, _, _) => {
      sender ! HttpResponse(entity = "Not found.")
    }
    case HttpRequest(GET, Uri.Path(path), _, _, _) => {
      val url = getURL(path.substring(1))

      sender ! HttpResponse(
        status = StatusCodes.TemporaryRedirect,
        headers = Location(url) :: Nil
      )
    }
  }

}
