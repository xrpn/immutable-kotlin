package com.xrpn.immutable

import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.ofvi
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
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

    test("fcount") {
        Arb.frbtree<Int, Int>(Arb.int(0..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
            val mm = frbt.copyToMutableMap()
            val ss = mm.size // size without duplicates
            val ds = frbt.size // size with duplicates
            var tot = 0
            var totDups = 0
            var nonDistinct = 0
            for (entry in mm) {
                val aux = TKVEntry.of(entry)
                val counter = frbt.fcount { it == aux }
                tot += counter
                if (frbt.fisDup(aux)) {
                    nonDistinct += 1
                    totDups += counter
                }
            }
            tot shouldBe ds
            (ss + totDups - nonDistinct) shouldBe ds
        }
    }

    test("fgroupBy").config(enabled = false) {
        fail("need FMap done to make this happen")
    }

    test("fpartition") {
        nul<Int,Int>().fpartition { true } shouldBe Pair(nul(), nul())
        Arb.frbtree<Int, Int>(Arb.int(0..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
            val isEven: (tkv: TKVEntry<Int,Int>) -> Boolean = { tkv -> 0 == tkv.getv() % 2}
            val (even, odd) = frbt.fpartition(isEven)
            even.forEach { tkv -> tkv.getv() % 2 shouldBe 0 }
            odd.forEach { tkv -> tkv.getv() % 2 shouldBe 1 }
            even.size + odd.size shouldBe frbt.size
        }
    }

    test("fpopAndReminder") {
        val (nilPop, nilReminder) = emptyIMBTree<Int,Int>().fpopAndReminder()
        nilPop shouldBe null
        nilReminder shouldBe emptyIMBTree()

        val (onePop, oneReminder) = ofvi(1).fpopAndReminder()
        onePop shouldBe 1.toIAEntry()
        oneReminder shouldBe emptyIMBTree()

        // this traverses slideShareTree popping one element at a time, and rebuilding the tree with the popped element
        // could probably have been a forEach...  It's always a fold in the end.
        val res = frbSlideShareTree.ffold(Pair(nul<Int, Int>(), frbSlideShareTree.fpopAndReminder())) { acc, _ ->
            val (rebuild, popAndStub) = acc
            val (pop, stub) = popAndStub
            Pair(rebuild.finsert(pop!!), stub.fpopAndReminder())
        }
        res.first shouldBe slideShareTree
        val (lastPopped, lastReminder) = res.second
        lastPopped shouldBe null
        lastReminder shouldBe emptyIMBTree()
    }

    test("fmaxDepth") {
        FRBTNil.fmaxDepth() shouldBe 0
        FRBTNode(TKVEntry.of(mEntry.hashCode(),mEntry)).fmaxDepth() shouldBe 1

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
        FRBTNode(mEntry).fminDepth() shouldBe 1

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
        FRBTNode(mEntry).size shouldBe 1

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