package com.xrpn.hash

import com.xrpn.hash.DigestHash.mrmr64
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

  test ("Murmur64, fun murmur64 8") {

    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf(i.toByte())
      val oraHash = mrmr64(ba[0].toLong())
      mrmr64.update(ba[0].toLong())
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 16") {
    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf(0x5, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt16(ba).toLong())
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x5)
      val oraHash = mrmr64(byteToInt16(ba).toLong())
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 24") {
    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf(0x5, (i shl 8).toByte(), 0x7)
      val oraHash = mrmr64(byteToInt32(ba).toLong())
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf(0x7, (i shl 8).toByte(), 0x5)
      val oraHash = mrmr64(byteToInt32(ba).toLong())
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 32") {
    val mrmr64 = MrMr64()
    for (i in (0x00 .. 0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x5, (i shl 8).toByte(), 0x7)
      val oraHash = mrmr64(byteToInt32(ba).toLong())
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf(0x7, (i shl 8).toByte(), 0x5, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt32(ba).toLong())
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 40") {
    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x03, 0x5, (i shl 8).toByte(), 0x7)
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf(0x7, 0x03, (i shl 8).toByte(), 0x5, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 48") {
    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x03, 0x5, (i shl 8).toByte(), 0x7, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x7, 0x03, (i shl 8).toByte(), 0x5, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 56") {
    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x03, 0x5, (i shl 8).toByte(), 0x0, 0x7, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x7, 0x03, (i shl 8).toByte(), 0x0, 0x5, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 64") {
    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x03, (i shl 8).toByte(), 0x5, (i shl 8).toByte(), 0x0, 0x7, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x7, 0x03, (i shl 8).toByte(), 0x0, (i shl 8).toByte(), 0x5, (i shl 8).toByte())
      val oraHash = mrmr64(byteToInt64(ba))
      mrmr64.update(ba)
      mrmr64.value shouldBe oraHash
    }
  }

  test ("Murmur64, fun murmur64 96") {
    val mrmr64 = MrMr64()
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x03, (i shl 8).toByte(), 0x5, (i shl 8).toByte(), 0x03, (i shl 8).toByte(), 0x5, (i shl 8).toByte(), 0x0, 0x7, (i shl 8).toByte())
      val oraHash1 = mrmr64(byteToInt64(ba.copyOfRange(0, 8)))
      val aux2 = byteToInt32(ba.copyOfRange(8, 12)).toLong()
      mrmr64.update(ba)
      mrmr64.value shouldBe mrmr64(aux2 + mrmr64(oraHash1))
    }
    for (i in (0x00..0xFF)) {
      mrmr64.reset()
      val ba = byteArrayOf((i shl 8).toByte(), 0x7, 0x03, (i shl 8).toByte(), 0x0, (i shl 8).toByte(), 0x5, (i shl 8).toByte(), 0x0, (i shl 8).toByte(), 0x5, (i shl 8).toByte())
      val oraHash1 = mrmr64(byteToInt64(ba.copyOfRange(0, 8)))
      val aux2 = byteToInt32(ba.copyOfRange(8, 12)).toLong()
      mrmr64.update(ba)
      mrmr64.value shouldBe mrmr64(aux2 + mrmr64(oraHash1))
    }
  }

})
