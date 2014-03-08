package com.gramplr.pile.utils.parsers

import scala.util.parsing.combinator.RegexParsers
import com.gramplr.pile.utils.webservices.Link

object LinkParser extends RegexParsers {

  def scheme: Parser[String] = "http" | "https"

  def base: Parser[String] = "[a-z.-]+".r

  def uri: Parser[String] = ".+".r

  def linkParser: Parser[Link] = scheme ~ "://" ~ base ~ opt("/") ~ opt(uri) ^^ {
    case (s ~ _ ~ b ~ _ ~ u) => {
      Link(b, u.getOrElse(""))
    }
  }

  def apply(input: String): Option[Link] = {
    this.parse(linkParser, input) match {
      case Success(out, next) => Some(out)
      case NoSuccess(error, _) => None
    }
  }

}
