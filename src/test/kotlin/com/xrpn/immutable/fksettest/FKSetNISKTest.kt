package com.xrpn.immutable.fksettest

import com.xrpn.bridge.FKSetIterator
import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.immutable.*
import com.xrpn.immutable.FKSet.Companion.toIMKSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FKSetNISKTest : FunSpec({

    test ("N") {
        copaNSetOf3 shouldBe null
        copaNSetOf3 shouldBe null
    }

    test ("I") {
        copaISetOf3.fisSubsetOf(copaISetOf4) shouldBe true
        copaISetOf3.fisSubsetOf(copaSSetOf4) shouldBe true
        copaISetOf3.fisSubsetOf(copaKKSetOf4) shouldBe true
        copaISetOf4.fisSubsetOf(copaISetOf3) shouldBe false
    }

    test ("S") {
        copaSSetOf3.fisSubsetOf(copaSSetOf4) shouldBe true
        copaSSetOf3.fisSubsetOf(copaISetOf4) shouldBe true
        copaSSetOf3.fisSubsetOf(copaKKSetOf4) shouldBe true
        copaSSetOf4.fisSubsetOf(copaSSetOf3) shouldBe false
    }

    test ("K") {
        copaKKSetOf3.fisSubsetOf(copaKKSetOf4) shouldBe true
        copaKKSetOf3.fisSubsetOf(copaISetOf4) shouldBe true
        copaKKSetOf3.fisSubsetOf(copaSSetOf4) shouldBe true
        copaKKSetOf4.fisSubsetOf(copaKKSetOf3) shouldBe false
    }

    test("K equals") {
        copaKKSetOf3.equals(copaKKSetOf3) shouldBe true
        copaKKSetOf3.equal(copaISetOf3) shouldBe true
        copaKKSetOf3.equal(copaSSetOf3) shouldBe true
    }

    test("S equals") {
        copaSSetOf3.equals(copaSSetOf3) shouldBe true
        copaSSetOf3.equal(copaKKSetOf3) shouldBe true
        copaSSetOf3.equal(copaISetOf3) shouldBe true
    }

    test("I equals") {
        copaISetOf3.equals(copaISetOf3) shouldBe true
        copaISetOf3.equal(copaKKSetOf3) shouldBe true
        copaISetOf3.equal(copaSSetOf3) shouldBe true
    }

    test("K to FKSet") {
        FKSetIterator(copaKKSetOf3).toIMKSet(copaKey)?.equals(copaKKSetOf3) shouldBe true
        (FKSetIterator(copaKKSetOf3).toIMKSet(copaKey) === copaKKSetOf3) shouldBe true
        FKSetIterator(copaKKSetOf3).toIMKSet(IntKeyType)?.equals(copaISetOf3) shouldBe true
        FKSetIterator(copaKKSetOf3).toIMKSet(StrKeyType)?.equals(copaSSetOf3) shouldBe true
    }

    test("I to FKSet") {
        FKSetIterator(copaISetOf3).toIMKSet(copaKey)?.equals(copaKKSetOf3) shouldBe true
        FKSetIterator(copaISetOf3).toIMKSet(IntKeyType)?.equals(copaISetOf3) shouldBe true
        (FKSetIterator(copaISetOf3).toIMKSet(IntKeyType) === copaISetOf3) shouldBe true
        FKSetIterator(copaISetOf3).toIMKSet(StrKeyType)?.equals(copaSSetOf3) shouldBe true
    }

    test("S to FKSet") {
        FKSetIterator(copaSSetOf3).toIMKSet(copaKey)?.equals(copaKKSetOf3) shouldBe true
        FKSetIterator(copaSSetOf3).toIMKSet(IntKeyType)?.equals(copaISetOf3) shouldBe true
        FKSetIterator(copaSSetOf3).toIMKSet(StrKeyType)?.equals(copaSSetOf3) shouldBe true
        (FKSetIterator(copaSSetOf3).toIMKSet(StrKeyType) === copaSSetOf3) shouldBe true
    }

})