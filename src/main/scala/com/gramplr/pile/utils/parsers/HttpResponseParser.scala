package com.gramplr.pile.utils.parsers

import scala.util.parsing.combinator.RegexParsers
import com.gramplr.pile.utils.webservices._
import com.gramplr.pile.utils.webservices.HttpHeader

object HttpResponseParser extends RegexParsers {

  override def skipWhitespace = true

  def spaces = """[ ]+""".r

  def lineEnd = "\r\n"

  def nonEndings = "[^\r\n]+".r

  def httpVersionLegacy: Parser[HttpVersion] = "HTTP/1.0" ^^ { _ =>
    HttpVersionOneDotZero
  }

  def httpVersionModern: Parser[HttpVersion] = "HTTP/1.1" ^^ { _ =>
    HttpVersionOneDotOne
  }

  def httpVersion: Parser[HttpVersion] = httpVersionLegacy | httpVersionModern

  def responseValue: Parser[Int] = "\\d+".r ^^ {
    _.toInt
  }

  def responseCode: Parser[String] = "([^\\s]+)".r ^^ {
    _.toString
  }

  def httpHeader: Parser[HttpHeader] = "[^:\r\n]+".r ~ ":" ~ spaces ~ nonEndings ~ lineEnd ^^ {
    case (key ~ _ ~ _ ~ value ~ _) => HttpHeader(key, value)
  }

  def httpParser: Parser[HttpResponse] =
    httpVersion ~ " " ~ responseValue ~ " " ~ responseCode ~ lineEnd ~
    rep(httpHeader) ~ lineEnd ^^
    {
      case(version ~ _ ~ resVal ~ _ ~ resCode ~ _ ~ headers ~ _) => {
        HttpResponse(version, resVal, resCode, headers)
      }
    }

  def apply(input: String): Option[HttpResponse] = {
    this.parse(httpParser, input) match {
      case Success(out, next) => Some(out)
      case NoSuccess(error, _) => None
    }
  }

}
