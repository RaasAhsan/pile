package com.gramplr.pile.core

import com.typesafe.config.ConfigFactory

trait Config { this: Core =>

  lazy val config = ConfigFactory.load().getConfig("Pile")

  def getString(k: String) = config.getString(k)

  def getInt(k: String) = config.getInt(k)

}
