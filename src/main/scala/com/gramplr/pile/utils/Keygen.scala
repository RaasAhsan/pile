package com.gramplr.pile.utils

import java.util.UUID

object Keygen {

  def generate = {
    UUID.randomUUID().toString.replaceAll("-", "").substring(0, 9)
  }

}
