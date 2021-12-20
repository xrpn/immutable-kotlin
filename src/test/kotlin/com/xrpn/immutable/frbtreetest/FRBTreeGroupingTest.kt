package com.xrpn.immutable.frbtreetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.nul
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree

class FRBTreeGroupingTest  : FunSpec({

    val repeatsHigh = Pair(50, 100)

    beforeTest {}

    test("fgroupBy").config(enabled = false) {
        fail("need FMap done to make this happen")
    }

    test("fpartition") {
        nul<Int,Int>().fpartition { true } shouldBe Pair(nul(), nul())
        Arb.frbtree<Int, Int>(Arb.int(0..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
            val isEven: (tkv: TKVEntry<Int, Int>) -> Boolean = { tkv -> 0 == tkv.getv() % 2}
            val (even, odd) = frbt.fpartition(isEven)
            even.asCollection().forEach { tkv -> tkv.getv() % 2 shouldBe 0 }
            odd.asCollection().forEach { tkv -> tkv.getv() % 2 shouldBe 1 }
            even.size + odd.size shouldBe frbt.size
        }
    }

    test("fmaxDepth") {
        FRBTNil.fmaxDepth() shouldBe 0
        FRBTNode.of(TKVEntry.ofkv(mEntry.hashCode(), mEntry)).fmaxDepth() shouldBe 1

        ttDepthOneRight.fmaxDepth() shouldBe 2
        frbDepthOneLeft.fmaxDepth() shouldBe 2
        frbDepthOneFull.fmaxDepth() shouldBe 2

        ttDepthTwoLeftRight.fmaxDepth() shouldBe 3
        frbDepthTwoLeftLeft.fmaxDepth() shouldBe 3
        frbDepthTwoRightRight.fmaxDepth() shouldBe 3
        frbDepthTwoRightLeft.fmaxDepth() shouldBe 3

        frbWikiTree.fmaxDepth() shouldBe 4
        frbSlideShareTree.fmaxDepth() shouldBe 4
    }

    test("fminDepth") {
        FRBTNil.fminDepth() shouldBe 0
        FRBTNode.of(mEntry).fminDepth() shouldBe 1

        ttDepthOneRight.fminDepth() shouldBe 2
        frbDepthOneLeft.fminDepth() shouldBe 2
        frbDepthOneFull.fminDepth() shouldBe 2

        ttDepthTwoLeftRight.fminDepth() shouldBe 2
        frbDepthTwoLeftLeft.fminDepth() shouldBe 2
        frbDepthTwoRightRight.fminDepth() shouldBe 2
        frbDepthTwoRightLeft.fminDepth() shouldBe 2

        frbWikiTree.fminDepth() shouldBe 3
        frbSlideShareTree.fminDepth() shouldBe 3
    }

    test("fsize") {
        FRBTNil.size shouldBe 0
        FRBTNode.of(mEntry).size shouldBe 1

        ttDepthOneRight.size shouldBe 2
        frbDepthOneLeft.size shouldBe 2
        frbDepthOneFull.size shouldBe 3

        ttDepthTwoLeftRight.size shouldBe 4
        frbDepthTwoLeftLeft.size shouldBe 4
        frbDepthTwoRightRight.size shouldBe 4
        frbDepthTwoRightLeft.size shouldBe 4

        frbWikiTree.size shouldBe 9
        frbSlideShareTree.size shouldBe 8
    }
})