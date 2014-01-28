package com.gramplr.pile.core

import akka.actor.ActorSystem

trait Core {

  implicit lazy val system = ActorSystem("PileSystem")



}
