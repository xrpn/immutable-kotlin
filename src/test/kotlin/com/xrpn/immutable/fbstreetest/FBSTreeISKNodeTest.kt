package com.xrpn.immutable.fbstreetest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FBSTreeISKNodeTest : FunSpec({

    test ("N") {
        nnodeBstOf3.equals(nnodeBstOf3) shouldBe true
        nnodeBstOf3.softEqual(nnodeRbtOf3) shouldBe true
        nnodeBstOf3.softEqual(nnodeBstOf4) shouldBe false
    }

    test ("I") {
        inodeBstOf3.equals(inodeBstOf3) shouldBe true
        inodeBstOf3.softEqual(inodeRbtOf3) shouldBe true
        inodeBstOf3.softEqual(inodeBstOf4) shouldBe false
    }

    test ("S") {
        snodeBstOf3.equals(snodeBstOf3) shouldBe true
        snodeBstOf3.softEqual(snodeRbtOf3) shouldBe true
        snodeBstOf3.softEqual(snodeBstOf4) shouldBe false
    }

    test ("K") {
        knodeBstOf3.equals(knodeBstOf3) shouldBe true
        knodeBstOf3.softEqual(knodeRbtOf3) shouldBe true
        knodeBstOf3.softEqual(knodeBstOf4) shouldBe false
    }

    test("N equals I") {
        nnodeBstOf3.softEqual(inodeBstOf3) shouldBe false
        inodeBstOf3.softEqual(nnodeBstOf3) shouldBe false
    }

    test("N equals S") {
        nnodeBstOf3.softEqual(snodeBstOf3) shouldBe false
        snodeBstOf3.softEqual(nnodeBstOf3) shouldBe false
    }

    test("N equals K") {
        nnodeBstOf3.softEqual(knodeBstOf3) shouldBe false
        knodeBstOf3.softEqual(snodeBstOf3) shouldBe false
    }

    test("K toFBSTree") {
        knodeBstOf3.toIMBTree(IntKeyType)?.equals(inodeBstOf3) shouldBe true
        knodeBstOf3.toIMBTree(StrKeyType)?.equals(snodeBstOf3) shouldBe true
    }

    test("I toFBSTree") {
        inodeBstOf3.toIMBTree(copaKey)?.equals(knodeBstOf3) shouldBe true
        inodeBstOf3.toIMBTree(StrKeyType)?.equals(snodeBstOf3) shouldBe true
    }

    test("S toFBSTree") {
        snodeBstOf3.toIMBTree(copaKey)?.equals(knodeBstOf3) shouldBe true
        snodeBstOf3.toIMBTree(IntKeyType)?.equals(inodeBstOf3) shouldBe true
    }


})