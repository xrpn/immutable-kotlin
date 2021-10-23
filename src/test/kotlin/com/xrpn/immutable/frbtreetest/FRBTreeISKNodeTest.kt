package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FRBTreeISKNodeTest : FunSpec({

    test ("N") {
        nnodeRbtOf3.equals(nnodeRbtOf3) shouldBe true
        nnodeRbtOf3.equals(nnodeBstOf3) shouldBe true
        nnodeRbtOf3.equals(nnodeRbtOf4) shouldBe false
    }

    test ("I") {
        inodeRbtOf3.equals(inodeRbtOf3) shouldBe true
        inodeRbtOf3.equals(inodeBstOf3) shouldBe true
        inodeRbtOf3.equals(inodeRbtOf4) shouldBe false
    }

    test ("S") {
        snodeRbtOf3.equals(snodeRbtOf3) shouldBe true
        snodeRbtOf3.equals(snodeBstOf3) shouldBe true
        snodeRbtOf3.equals(snodeRbtOf4) shouldBe false
    }

    test ("K") {
        knodeRbtOf3.equals(knodeRbtOf3) shouldBe true
        knodeRbtOf3.equals(knodeBstOf3) shouldBe true
        knodeRbtOf3.equals(knodeRbtOf4) shouldBe false
    }

    test("N equals I") {
        nnodeRbtOf3.equals(inodeRbtOf3) shouldBe false
        inodeRbtOf3.equals(nnodeRbtOf3) shouldBe false
    }

    test("N equals S") {
        nnodeRbtOf3.equals(snodeRbtOf3) shouldBe false
        snodeRbtOf3.equals(nnodeRbtOf3) shouldBe false
    }

    test("N equals K") {
        nnodeRbtOf3.equals(knodeRbtOf3) shouldBe false
        knodeRbtOf3.equals(snodeRbtOf3) shouldBe false
    }

    test("K toFRBTree") {
        knodeRbtOf3.toIMBTree(IntKeyType)?.equals(inodeRbtOf3) shouldBe true
        knodeRbtOf3.toIMBTree(StrKeyType)?.equals(snodeRbtOf3) shouldBe true
    }

    test("I toFRBTree") {
        inodeRbtOf3.toIMBTree(copaKey)?.equals(knodeRbtOf3) shouldBe true
        inodeRbtOf3.toIMBTree(StrKeyType)?.equals(snodeRbtOf3) shouldBe true
    }

    test("S toFRBTree") {
        snodeRbtOf3.toIMBTree(copaKey)?.equals(knodeRbtOf3) shouldBe true
        snodeRbtOf3.toIMBTree(IntKeyType)?.equals(inodeRbtOf3) shouldBe true
    }

})