package com.xrpn.immutable

import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstreeAllowDups

class FBSTreeGroupingTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("fcount") {
        Arb.fbstreeAllowDups<Int, Int>(Arb.int(0..100)).checkAll(repeats) { fbst ->
            val mm = fbst.copyToMutableMap()
            val ss = mm.size // size without duplicates
            val ds = fbst.size // size with duplicates
            var tot = 0
            var totDups = 0
            var nonDistinct = 0
            for (entry in mm) {
                val aux = TKVEntry.of(entry)
                val counter = fbst.fcount { it == aux }
                tot += counter
                if (fbst.fisDup(aux)) {
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
        FBSTree.nul<Int,Int>().fpartition { true } shouldBe Pair(FBSTree.nul(), FBSTree.nul())
        Arb.fbstreeAllowDups<Int, Int>(Arb.int(0..100)).checkAll(repeats) { fbst ->
            val isEven: (tkv: TKVEntry<Int,Int>) -> Boolean = { tkv -> 0 == tkv.getv() % 2}
            val (even, odd) = fbst.fpartition(isEven)
            even.forEach { tkv -> tkv.getv() % 2 shouldBe 0 }
            odd.forEach { tkv -> tkv.getv() % 2 shouldBe 1 }
            even.size + odd.size shouldBe fbst.size
        }
    }

    test("fpopAndReminder") {
        val (nilPop, nilReminder) = FBSTree.emptyIMBTree<Int,Int>().fpopAndReminder()
        nilPop shouldBe null
        nilReminder shouldBe FBSTree.emptyIMBTree()

        val (onePop, oneReminder) = FBSTree.ofvi(1).fpopAndReminder()
        onePop shouldBe 1.toIAEntry()
        oneReminder shouldBe FBSTree.emptyIMBTree()

        // this traverses slideShareTree pop_ping one element at a time, and rebuilding the tree with it
        // could probably have been a forEach...  It's always a fold in the end.
        val res = slideShareTree.ffold(Pair(FBSTree.nul<Int, Int>(), slideShareTree.fpopAndReminder())) { acc, _ ->
            val (rebuild, popAndStub) = acc
            val (pop, stub) = popAndStub
            Pair(rebuild.finsertDup(pop!!, allowDups = true), stub.fpopAndReminder())
        }
        res.first shouldBe slideShareTree
        val (lastPopped, lastReminder) = res.second
        lastPopped shouldBe null
        lastReminder shouldBe FBSTree.emptyIMBTree()
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

})
