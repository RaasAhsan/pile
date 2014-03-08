package com.gramplr.pile.utils.webservices

case class HttpResponse(httpVersion: HttpVersion, responseValue: Int, responseCode: String, headers: List[HttpHeader]) {

  def getHeader(key: String): Option[String] = {
    headers.find(_.key == key).map(_.value)
  }

}
