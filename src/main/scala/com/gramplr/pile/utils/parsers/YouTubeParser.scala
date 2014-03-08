package com.gramplr.pile.utils.parsers

import scala.util.parsing.combinator.RegexParsers
import com.gramplr.pile.utils.webservices.YouTubeVideo

object YouTubeParser extends RegexParsers {

  def scheme: Parser[String] = ("http://" | "https://")

  def youtubeLink: Parser[String] = ("youtube.com/watch/?v=" | "www.youtube.com/watch/?v=" | "youtube.com/watch?v=" | "www.youtube.com/watch?v=")

  def videoID: Parser[String] = ".+".r

  def youTubeParser: Parser[YouTubeVideo] = scheme ~ youtubeLink ~ videoID ^^
  {
    case(scheme ~ youtubeLink ~ vID) => {
      YouTubeVideo(vID)
    }
  }

  def apply(input: String): Option[YouTubeVideo] = {
    parse(youTubeParser, input) match {
      case Success(out, next) => Some(out)
      case NoSuccess(error, _) => None
    }
  }

}
