package com.gramplr.pile.utils.webservices

sealed trait HttpVersion
case object HttpVersionOneDotZero extends HttpVersion
case object HttpVersionOneDotOne  extends HttpVersion
