package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FRBTreeISKNodeTest : FunSpec({

    test ("N") {
        nnodeRbtOf3.softEqual(nnodeRbtOf3) shouldBe true
        nnodeRbtOf3.softEqual(nnodeBstOf3) shouldBe true
        nnodeRbtOf3.softEqual(nnodeRbtOf4) shouldBe false
    }

    test ("I") {
        inodeRbtOf3.equals(inodeRbtOf3) shouldBe true
        inodeRbtOf3.softEqual(inodeBstOf3) shouldBe true
        inodeRbtOf3.softEqual(inodeRbtOf4) shouldBe false
    }

    test ("S") {
        snodeRbtOf3.equals(snodeRbtOf3) shouldBe true
        snodeRbtOf3.softEqual(snodeBstOf3) shouldBe true
        snodeRbtOf3.softEqual(snodeRbtOf4) shouldBe false
    }

    test ("K") {
        knodeRbtOf3.equals(knodeRbtOf3) shouldBe true
        knodeRbtOf3.softEqual(knodeBstOf3) shouldBe true
        knodeRbtOf3.softEqual(knodeRbtOf4) shouldBe false
    }

    test("N equals I") {
        nnodeRbtOf3.softEqual(inodeRbtOf3) shouldBe false
        inodeRbtOf3.softEqual(nnodeRbtOf3) shouldBe false
    }

    test("N equals S") {
        nnodeRbtOf3.softEqual(snodeRbtOf3) shouldBe false
        snodeRbtOf3.softEqual(nnodeRbtOf3) shouldBe false
    }

    test("N equals K") {
        nnodeRbtOf3.softEqual(knodeRbtOf3) shouldBe false
        knodeRbtOf3.softEqual(snodeRbtOf3) shouldBe false
    }

    test("K toFRBTree") {
        knodeRbtOf3.toIMBTree(IntKeyType)?.softEqual(inodeRbtOf3) shouldBe true
        knodeRbtOf3.toIMBTree(StrKeyType)?.softEqual(snodeRbtOf3) shouldBe true
    }

    test("I toFRBTree") {
        inodeRbtOf3.toIMBTree(copaKey)?.softEqual(knodeRbtOf3) shouldBe true
        inodeRbtOf3.toIMBTree(StrKeyType)?.softEqual(snodeRbtOf3) shouldBe true
    }

    test("S toFRBTree") {
        snodeRbtOf3.toIMBTree(copaKey)?.softEqual(knodeRbtOf3) shouldBe true
        snodeRbtOf3.toIMBTree(IntKeyType)?.softEqual(inodeRbtOf3) shouldBe true
    }

})