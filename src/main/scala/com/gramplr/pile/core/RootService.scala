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
      val map = URLReader.getURLs(r.uri.query.getOrElse("url", ""))
      val nmap = map.map(x => {
        val t = URLReader.getType(x._1)
        insertShorten(x._2, x._1, t)
        (x._1, x._2, t)
      })

      val back = Json.obj(
        "urls" -> Json.toJson(
          nmap.map(x => Json.obj("link" -> (baseurl + "/" + x._2), "type" -> x._3))
        )
      )

      println(Json.stringify(back))

      sender ! HttpResponse(entity = Json.stringify(back))
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

}
