package com.xrpn.hash

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class FDigestHashPropertyTest : FunSpec({

  val repeats = 1000

  beforeTest {}

  test ("fun crc32") {
    Arb.string().checkAll(repeats, PropTestConfig(seed = 241775417572653412)) { s ->
      val ba = s.toByteArray()
      DigestHash.crc32(ba).toLong() shouldBe DigestHash.crc32i(ba).toLong() + Int.MAX_VALUE.toLong()
    }
  }

  test ("fun crc32c") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      DigestHash.crc32c(ba).toLong() shouldBe DigestHash.crc32ci(ba).toLong() + Int.MAX_VALUE.toLong()
    }
  }

  test ("fun adler32") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      DigestHash.adler32(ba).toLong() shouldBe DigestHash.adler32i(ba).toLong() + Int.MAX_VALUE.toLong()
    }
  }
})
