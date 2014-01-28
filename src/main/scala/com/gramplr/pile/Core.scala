package com.gramplr.pile

import akka.actor.ActorSystem

trait Core {

  implicit lazy val system = ActorSystem("PileSystem")

}
