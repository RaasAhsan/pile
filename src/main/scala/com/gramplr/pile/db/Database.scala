package com.gramplr.pile.db

import com.gramplr.pile.core.{Core, Config}
import java.sql.DriverManager

trait Database extends Config with Core {

  Class.forName(getString("driver"))
  lazy val dbconn = DriverManager.getConnection(getString("db"), getString("user"), getString("password"))
  lazy val table = "url-shorten"

  def insertShorten(key: String, value: String) {
    dbconn.prepareStatement("INSERT INTO `" + table + "` VALUES (\"" + key + "\", \"" + value + "\")").executeUpdate()
  }

}
