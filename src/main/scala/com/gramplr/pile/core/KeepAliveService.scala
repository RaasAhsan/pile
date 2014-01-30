package com.gramplr.pile.core

import akka.actor.Actor
import com.gramplr.pile.db.Database

case object KeepAlive

class KeepAliveService extends Actor with Database {

  def receive = {
    case KeepAlive => {
      version
    }
  }

}
