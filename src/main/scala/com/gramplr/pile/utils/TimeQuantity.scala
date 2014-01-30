package com.gramplr.pile.utils

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

object TimeQuantity {

  implicit def int2TimeQuantity(from: Int) = new TimeQuantity(from)

}

class TimeQuantity(from: Int) {

  def minutes = Duration.create(from, TimeUnit.MINUTES)
  def seconds = Duration.create(from, TimeUnit.SECONDS)

}
