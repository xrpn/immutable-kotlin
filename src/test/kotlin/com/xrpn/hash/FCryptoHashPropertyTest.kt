package com.xrpn.hash

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.math.BigInteger

class FCryptoHashPropertyTest : FunSpec({

  val repeats = 100

  beforeTest {}

  test ("fun SHA3_512") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA3_512(ba).size shouldBe 64
      CryptoHash.SHA3_512_hex(ba).length shouldBe 128
      (CryptoHash.SHA3_512_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA_384") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA_384(ba).size shouldBe 48
      CryptoHash.SHA_384_hex(ba).length shouldBe 96
      (CryptoHash.SHA_384_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA(ba).size shouldBe 20
      CryptoHash.SHA_hex(ba).length shouldBe 40
      (CryptoHash.SHA_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA3_384") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA3_384(ba).size shouldBe 48
      CryptoHash.SHA3_384_hex(ba).length shouldBe 96
      (CryptoHash.SHA3_384_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA_224") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA_224(ba).size shouldBe 28
      CryptoHash.SHA_224_hex(ba).length shouldBe 56
      (CryptoHash.SHA_224_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA_512_256") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA_512_256(ba).size shouldBe 32
      CryptoHash.SHA_512_256_hex(ba).length shouldBe 64
      (CryptoHash.SHA_512_256_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA_256") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA_256(ba).size shouldBe 32
      CryptoHash.SHA_256_hex(ba).length shouldBe 64
      (CryptoHash.SHA_256_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun MD2") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.MD2(ba).size shouldBe 16
      CryptoHash.MD2_hex(ba).length shouldBe 32
      (CryptoHash.MD2_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA_512_224") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA_512_224(ba).size shouldBe 28
      CryptoHash.SHA_512_224_hex(ba).length shouldBe 56
      (CryptoHash.SHA_512_224_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA3_256") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA3_256(ba).size shouldBe 32
      CryptoHash.SHA3_256_hex(ba).length shouldBe 64
      (CryptoHash.SHA3_256_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA_512") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA_512(ba).size shouldBe 64
      CryptoHash.SHA_512_hex(ba).length shouldBe 128
      (CryptoHash.SHA_512_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun MD5") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.MD5(ba).size shouldBe 16
      CryptoHash.MD5_hex(ba).length shouldBe 32
      (CryptoHash.MD5_num(ba) is BigInteger) shouldBe true
    }
  }

  test ("fun SHA3_224") {
    Arb.string().checkAll(repeats) { s ->
      val ba = s.toByteArray()
      CryptoHash.SHA3_224(ba).size shouldBe 28
      CryptoHash.SHA3_224_hex(ba).length shouldBe 56
      (CryptoHash.SHA3_224_num(ba) is BigInteger) shouldBe true
    }
  }
})
