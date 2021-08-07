package com.xrpn.hash

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.short
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.math.BigInteger

class FByteAryToNumberPropertyTest : FunSpec({

  val repeats = 500

  fun tba(n: Number): ByteArray = BigInteger(n.toString()).toByteArray()

  beforeTest {}

  test("byteToInt16 sanity") {
    byteToInt16(tba(Short.MIN_VALUE)) shouldBe Short.MIN_VALUE
    byteToInt16(tba(1)) shouldBe 1.toShort()
    byteToInt16(tba(0)) shouldBe 0.toShort()
    byteToInt16(tba(-1)) shouldBe -1.toShort()
    byteToInt16(tba(Short.MAX_VALUE)) shouldBe Short.MAX_VALUE
    shouldThrow<IllegalArgumentException> {
      val ba = tba(Int.MAX_VALUE)
      byteToInt16(ba)
    }
  }

  test("byteToInt16") {
    Arb.short().checkAll(repeats) { s ->
      byteToInt16(tba(s)) shouldBe s
    }
  }

  test("byteToInt32 sanity") {
    byteToInt32(tba(Int.MIN_VALUE)) shouldBe Int.MIN_VALUE
    byteToInt32(tba(Int.MIN_VALUE shl 9)) shouldBe (Int.MIN_VALUE shl 9)
    byteToInt32(tba(Short.MIN_VALUE)) shouldBe Short.MIN_VALUE
    byteToInt32(tba(1)) shouldBe 1.toShort()
    byteToInt32(tba(0)) shouldBe 0.toShort()
    byteToInt32(tba(-1)) shouldBe -1.toShort()
    byteToInt32(tba(Short.MAX_VALUE)) shouldBe Short.MAX_VALUE
    byteToInt32(tba(Int.MAX_VALUE shl 9)) shouldBe (Int.MAX_VALUE shl 9)
    byteToInt32(tba(Int.MAX_VALUE)) shouldBe Int.MAX_VALUE
    shouldThrow<IllegalArgumentException> {
      val ba = tba(Long.MAX_VALUE)
      byteToInt32(ba)
    }
  }

  test("byteToInt32") {
    Arb.int().checkAll(repeats) { s ->
      byteToInt32(tba(s)) shouldBe s
    }
  }

  test("byteToInt64 sanity") {
    byteToInt64(tba(Long.MIN_VALUE)) shouldBe Long.MIN_VALUE
    byteToInt64(tba(Long.MIN_VALUE shl 9)) shouldBe (Long.MIN_VALUE shl 9)
    byteToInt64(tba(Long.MIN_VALUE shl 17)) shouldBe (Long.MIN_VALUE shl 17)
    byteToInt64(tba(Long.MIN_VALUE shl 25)) shouldBe (Long.MIN_VALUE shl 25)
    byteToInt64(tba(Int.MIN_VALUE)) shouldBe Int.MIN_VALUE
    byteToInt64(tba(Int.MIN_VALUE shl 9)) shouldBe (Int.MIN_VALUE shl 9)
    byteToInt64(tba(Short.MIN_VALUE)) shouldBe Short.MIN_VALUE
    byteToInt64(tba(1)) shouldBe 1.toShort()
    byteToInt64(tba(0)) shouldBe 0.toShort()
    byteToInt64(tba(-1)) shouldBe -1.toShort()
    byteToInt64(tba(Short.MAX_VALUE)) shouldBe Short.MAX_VALUE
    byteToInt64(tba(Int.MAX_VALUE shl 9)) shouldBe (Int.MAX_VALUE shl 9)
    byteToInt64(tba(Int.MAX_VALUE)) shouldBe Int.MAX_VALUE
    byteToInt64(tba(Long.MAX_VALUE shl 25)) shouldBe (Long.MAX_VALUE shl 25)
    byteToInt64(tba(Long.MAX_VALUE shl 17)) shouldBe (Long.MAX_VALUE shl 17)
    byteToInt64(tba(Long.MAX_VALUE shl 9)) shouldBe (Long.MAX_VALUE shl 9)
    byteToInt64(tba(Long.MAX_VALUE)) shouldBe Long.MAX_VALUE
    shouldThrow<IllegalArgumentException> {
      val ba = tba(BigInteger(Long.MAX_VALUE.toString()+Long.MAX_VALUE.toString()))
      byteToInt64(ba)
    }
  }

  test("byteToInt64") {
    Arb.long().checkAll(repeats) { s ->
      byteToInt64(tba(s)) shouldBe s
    }
  }
})
