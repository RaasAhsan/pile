package com.gramplr.pile.core

import akka.actor._
import spray.http._
import HttpMethods._
import spray.can.Http
import com.gramplr.pile.db.Database
import com.gramplr.pile.utils.{URLReader}
import spray.http.HttpHeaders.Location
import play.api.libs.json.{JsString, Json}

class RootService extends Actor with Database with Config {

  lazy val baseurl = getString("url")

  def receive = {
    case _: Http.Connected => sender ! Http.Register(self)
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      sender ! HttpResponse(entity = "Welcome to the Pile service!")
    case r@HttpRequest(GET, Uri.Path("/shorten"), _, _, _) => {
      if(r.headers.exists(_.name == "X-Real-IP")) {
        if(r.headers.exists(x => {x.name == "X-Real-IP" && x.value == "127.0.0.1"})) {
          sender ! HttpResponse(entity = Json.stringify(shorten(r.uri.query.getOrElse("url", ""))))
        } else {
          sender ! HttpResponse(entity = "You don't have permission to do that.")
        }
      } else {
        sender ! HttpResponse(entity = Json.stringify(shorten(r.uri.query.getOrElse("url", ""))))
      }
    }
    case r@HttpRequest(GET, Uri.Path("/image"), _, _, _) => {
      sender ! HttpResponse(entity = URLReader.isImage(r.uri.query.getOrElse("url", "")) + "")
    }
    case r@HttpRequest(GET, Uri.Path("/youtube"), _, _, _) => {
      sender ! HttpResponse(entity = URLReader.isYoutubeVideo("https://www.youtube.com/watch?v=" + r.uri.query.getOrElse("url", "")) + "")
    }
    case HttpRequest(GET, Uri.Path("/notfound"), _, _, _) => {
      sender ! HttpResponse(entity = "Not found.")
    }
    case HttpRequest(GET, Uri.Path(path), _, _, _) => {
      val url = getURL(path.substring(1))

      if(url != "notfound") {
        click(path.substring(1))
        sender ! HttpResponse(
          status = StatusCodes.TemporaryRedirect,
          headers = Location(url) :: Nil
        )
      } else {
        sender ! HttpResponse(entity = "Not found.")
      }
    }
  }

  private def shorten(urls: String) = {
    val map = URLReader.getURLs(urls)
    val nmap = map.map(x => {
      val t = URLReader.getType(x._1)
      insertShorten(x._2, x._1, t)
      (x._1, x._2, t)
    })

    Json.obj(
      "urls" -> Json.toJson(
        nmap.map(x => Json.obj("link" -> (baseurl + "/" + x._2), "type" -> x._3))
      )
    )
  }

}
