package com.gramplr.pile.utils

import play.api.libs.json._
import scala.concurrent._
import ExecutionContext.Implicits.global
import java.net.Socket
import java.io.{ByteArrayOutputStream}
import com.gramplr.pile.utils.parsers.{YouTubeParser, LinkParser, HttpResponseParser}
import com.gramplr.pile.utils.webservices.{YouTubeVideo, Link}

object URLReader {

  def getType(source: String): Int = {
    if(isImage(source)) {
      1
    } else if(isYoutubeVideo(source)) {
      2
    } else {
      0
    }
  }

  def isImage(source: String): Boolean = {
    val link = LinkParser(source)

    if(link != None) {
      val l = link match {
        case Some(Link(d, u)) => {
          val socket = new Socket(d, 80)
          if(socket.isConnected) {
            val os = socket.getOutputStream
            os.write(("HEAD /" + u + " HTTP/1.0\r\nHost: " + d + "\r\n\r\n").getBytes)
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
            HttpResponseParser(baos.toString)
          } else {
            None
          }
        }
      }
      l.map(_.headers.exists(x => {x.key == "Content-Type" && x.value.startsWith("image/")})).getOrElse(false)
    } else {
      false
    }
  }

  def isYoutubeVideo(source: String): Boolean = {
    val link = YouTubeParser(source)

    link match {
      case Some(YouTubeVideo(v)) => {
        val socket = new Socket("youtube.com", 80)
        if(socket.isConnected) {
          val os = socket.getOutputStream
          os.write(("HEAD /watch?v=" + v + " HTTP/1.0\r\nHost: www.youtube.com\r\n\r\n").getBytes)
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
          val l = HttpResponseParser(baos.toString)
          l.map(_.responseValue == 200).getOrElse(false)
        } else {
          false
        }
      }
      case None => {
        false
      }
    }
  }

  def getURLs(from: String): Seq[(String, String)] = {
    val js = Json.parse(from)

    val jsl = (js \ "urls").as[JsArray]
    jsl.value.map(x => {
      (x.as[String], Keygen.generate)
    })
  }

}
