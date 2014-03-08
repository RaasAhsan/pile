package com.gramplr.pile.utils

import play.api.libs.json._
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.net.Socket
import java.io.{ByteArrayOutputStream}
import com.gramplr.pile.utils.parsers.HttpResponseParser

object URLReader {

  def isImage(source: String): Future[Boolean] = future {
    // convert ink here
    val socket = new Socket("http://youtube.com", 80)
    val os = socket.getOutputStream
    os.write("HEAD /watch?v=0QCAoENRfPs\r\nHost: http://youtube.com\r\n\r\n".getBytes)
    os.flush

    val is = socket.getInputStream

    val baos = new ByteArrayOutputStream()

    def read(baos: ByteArrayOutputStream) {
      val buf = new Array[Byte](4096)
      val n = is.read(buf)
      if(n < 0) {
        return
      } else {
        baos.write(buf, 0, n)
        read(baos)
      }
    }

    read(baos)
    HttpResponseParser(baos.toByteArray.toString)
  }.map { x => {
      if(x.get != null) {
        val l = x.get.getHeader("Content-Type").getOrElse("")
        l.startsWith("image/")
      } else {
        false
      }
    }
  }

  def isYoutubeVideo(source: String): Future[Boolean] = future {
    //convert to youtube link here
    val socket = new Socket("http://youtube.com", 80)
    val os = socket.getOutputStream
    os.write("HEAD /watch?v=0QCAoENRfPs\r\nHost: http://youtube.com\r\n\r\n".getBytes)
    os.flush
  }

  def getURLs(from: String): Seq[(String, String)] = {
    val js = Json.parse(from)

    val jsl = (js \ "urls").as[JsArray]
    jsl.value.map(x => {
      (x.as[String], Keygen.generate)
    })
  }

}
