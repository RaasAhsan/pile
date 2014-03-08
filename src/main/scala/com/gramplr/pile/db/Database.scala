package com.gramplr.pile.db

import com.gramplr.pile.core.{Core, Config}
import java.sql.DriverManager

trait Database extends Config with Core {

  Class.forName(getString("driver"))
  lazy val dbconn = DriverManager.getConnection(getString("db"), getString("user"), getString("password"))
  lazy val table = "url-shorten"

  lazy val getVersion = dbconn.prepareStatement("SELECT version();")
  lazy val insert = dbconn.prepareStatement("INSERT INTO `" + table + "` VALUES (?, ?, ?)")
  lazy val retrieve = dbconn.prepareStatement("SELECT * FROM `" + table + "` WHERE `key`=?")
  lazy val getKey = dbconn.prepareStatement("SELECT * FROM `" + table + "` WHERE `value`=?")

  def insertShorten(key: String, value: String, linkType: Int) {
    if(getKey(value) == "notfound") {
      insert.setString(1, key)
      insert.setString(2, value)
      insert.setInt(3, linkType)
      insert.executeUpdate()
    }
  }

  def getKey(value: String): String = {
    retrieve.setString(1, value)
    val rs = retrieve.executeQuery()
    if(rs.next())
      rs.getString("key")
    else
      "notfound"
  }

  def getURL(key: String): String = {
    retrieve.setString(1, key)
    val rs = retrieve.executeQuery()
    if(rs.next())
      rs.getString("value")
    else
      "notfound"
  }

  def version {
    getVersion.executeQuery()
  }

}
