package com.gramplr.pile.utils.parsers

import scala.util.parsing.combinator.RegexParsers
import com.gramplr.pile.utils.webservices.Link

object LinkParser extends RegexParsers {

  def scheme: Parser[String] = "http" | "https"

  def base: Parser[String] = "[.-\\w]+".r

  def uri: Parser[String] = ".".r

  def linkParser = Parser[Link] = scheme ~ "://" ~ base ~ "/" ~ uri ^^ {
    case (s ~ _ ~ b ~ _ ~ u) => {
      Link(b, u)
    }
  }

}
