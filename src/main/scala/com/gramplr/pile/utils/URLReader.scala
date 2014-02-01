package com.gramplr.pile.utils

import play.api.libs.json._

object URLReader {

  def getURLs(from: String): Seq[(String, String)] = {
    val js = Json.parse(from)

    val jsl = (js \ "urls").as[JsArray]
    jsl.value.map(x => {
      (x.as[String], Keygen.generate)
    })
  }

}
