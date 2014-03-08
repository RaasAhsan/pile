package com.gramplr.pile.utils.parsers

import scala.util.parsing.combinator.RegexParsers
import com.gramplr.pile.utils.webservices.YouTubeVideo

object YouTubeParser extends RegexParsers {

  def scheme: Parser[String] = "http" | "https"

  def videoID: Parser[String] = "[a-z\\d\\-]+".r

  def youTubeParser: Parser[YouTubeVideo] = scheme ~ "://youtube.com/watch/?v=" ~ videoID ^^
  {
    case(httpType ~ _ ~ vID) => {
      YouTubeVideo(vID)
    }
  }

}
