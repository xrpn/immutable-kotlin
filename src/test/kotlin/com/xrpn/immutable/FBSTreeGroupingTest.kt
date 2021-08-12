package com.xrpn.immutable

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FBSTreeGroupingTest : FunSpec({

    beforeTest {}

    test("fsize") {
        FBSTNil.fsize() shouldBe 0
        FBSTNode(mEntry).fsize() shouldBe 1

        depthOneRight.fsize() shouldBe 2
        depthOneLeft.fsize() shouldBe 2
        depthOneFull.fsize() shouldBe 3

        depthTwoLeftRight.fsize() shouldBe 4
        depthTwoLeftLeft.fsize() shouldBe 4
        depthTwoRightRight.fsize() shouldBe 4
        depthTwoRightLeft.fsize() shouldBe 4

        wikiTree.fsize() shouldBe 9
        slideShareTree.fsize() shouldBe 8
    }

    test("fpopAndReminder") {
        fail("not implemented")
    }

    test("maxDepth") {
        FBSTNil.fmaxDepth() shouldBe 0
        FBSTNode(mEntry).fmaxDepth() shouldBe 1

        depthOneRight.fmaxDepth() shouldBe 2
        depthOneLeft.fmaxDepth() shouldBe 2
        depthOneFull.fmaxDepth() shouldBe 2

        depthTwoLeftRight.fmaxDepth() shouldBe 3
        depthTwoLeftLeft.fmaxDepth() shouldBe 3
        depthTwoRightRight.fmaxDepth() shouldBe 3
        depthTwoRightLeft.fmaxDepth() shouldBe 3

        wikiTree.fmaxDepth() shouldBe 4
        slideShareTree.fmaxDepth() shouldBe 4
    }

    test("minDepth") {
        FBSTNil.fminDepth() shouldBe 0
        // FBTNode(mEntry).minDepth() shouldBe 1

        depthOneRight.fminDepth() shouldBe 2
        depthOneLeft.fminDepth() shouldBe 2
        depthOneFull.fminDepth() shouldBe 2

        depthTwoLeftRight.fminDepth() shouldBe 2
        depthTwoLeftLeft.fminDepth() shouldBe 2
        depthTwoRightRight.fminDepth() shouldBe 2
        depthTwoRightLeft.fminDepth() shouldBe 2

        wikiTree.fminDepth() shouldBe 3
        slideShareTree.fminDepth() shouldBe 3
    }
})
