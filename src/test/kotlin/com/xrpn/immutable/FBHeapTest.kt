package com.xrpn.immutable

import com.xrpn.immutable.FBHeap.Companion.RULE
import com.xrpn.immutable.FBHeap.Companion.add
import com.xrpn.immutable.FBHeap.Companion.enumerate
import com.xrpn.immutable.FBHeap.Companion.heapSane
import com.xrpn.immutable.FBHeap.Companion.insert
import com.xrpn.immutable.FBHeap.Companion.maxHeapNul
import com.xrpn.immutable.FBHeap.Companion.maxHeapOf
import com.xrpn.immutable.FBHeap.Companion.minHeapNul
import com.xrpn.immutable.FBHeap.Companion.minHeapOf
import com.xrpn.immutable.FBHeap.Companion.of
import com.xrpn.immutable.FList.Companion.toArray
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int
import kotlin.random.Random.Default.nextInt

private const val verbose = false


class FBHeapTest : FunSpec({

    beforeTest {}

    test("root") {
        FBHMinNil.top() shouldBe null
        FBHMinNil.top() shouldBe null
        val ary = Array(1) { TKVEntry.ofIntKey(nextInt()) }
        of(ary.iterator(), RULE.MIN).top() shouldBe ary[0]
        of(ary.iterator(), RULE.MAX).top() shouldBe ary[0]
    }

    test("height") {
        FBHMaxNil.height() shouldBe 0
        FBHMaxNil.height() shouldBe 0
        FBHBranch(
            TKVEntry.of(mEntry.hashCode(), mEntry),
            FBHMaxNil,
            FBHMaxNil
        ).height() shouldBe 1

        of(ttDepthOneRight.preorder(), RULE.MIN).height() shouldBe 2
        of(ttDepthOneRight.postorder(), RULE.MIN).height() shouldBe 2
        of(ttDepthOneRight.inorder(), RULE.MIN).height() shouldBe 2
        of(ttDepthOneRight.breadthFirst(), RULE.MIN).height() shouldBe 2
        of(ttDepthOneRight.preorder(), RULE.MAX).height() shouldBe 2
        of(ttDepthOneRight.postorder(), RULE.MAX).height() shouldBe 2
        of(ttDepthOneRight.inorder(), RULE.MAX).height() shouldBe 2
        of(ttDepthOneRight.breadthFirst(), RULE.MAX).height() shouldBe 2

        of(frbDepthOneLeft.preorder(), RULE.MIN).height() shouldBe 2
        of(frbDepthOneLeft.postorder(), RULE.MIN).height() shouldBe 2
        of(frbDepthOneLeft.inorder(), RULE.MIN).height() shouldBe 2
        of(frbDepthOneLeft.breadthFirst(), RULE.MIN).height() shouldBe 2
        of(frbDepthOneLeft.preorder(), RULE.MAX).height() shouldBe 2
        of(frbDepthOneLeft.postorder(), RULE.MAX).height() shouldBe 2
        of(frbDepthOneLeft.inorder(), RULE.MAX).height() shouldBe 2
        of(frbDepthOneLeft.breadthFirst(), RULE.MAX).height() shouldBe 2

        of(frbDepthOneFullPreorder, RULE.MIN).height() shouldBe 2
        of(frbDepthOneFullInorder, RULE.MIN).height() shouldBe 2
        of(frbDepthOneFullPostorder, RULE.MIN).height() shouldBe 2
        of(frbDepthOneFullBreadthFirst, RULE.MIN).height() shouldBe 2
        of(frbDepthOneFullPreorder, RULE.MAX).height() shouldBe 2
        of(frbDepthOneFullInorder, RULE.MAX).height() shouldBe 2
        of(frbDepthOneFullPostorder, RULE.MAX).height() shouldBe 2
        of(frbDepthOneFullBreadthFirst, RULE.MAX).height() shouldBe 2

        of(ttDepthTwoLeftRight.preorder(), RULE.MIN).height() shouldBe 3
        of(ttDepthTwoLeftRight.postorder(), RULE.MIN).height() shouldBe 3
        of(ttDepthTwoLeftRight.inorder(), RULE.MIN).height() shouldBe 3
        of(ttDepthTwoLeftRight.breadthFirst(), RULE.MIN).height() shouldBe 3
        of(ttDepthTwoLeftRight.preorder(), RULE.MAX).height() shouldBe 3
        of(ttDepthTwoLeftRight.postorder(), RULE.MAX).height() shouldBe 3
        of(ttDepthTwoLeftRight.inorder(), RULE.MAX).height() shouldBe 3
        of(ttDepthTwoLeftRight.breadthFirst(), RULE.MAX).height() shouldBe 3

        of(frbDepthTwoLeftLeftPreorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoLeftLeftPostorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoLeftLeftInorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoLeftLeftBreadthFirst, RULE.MIN).height() shouldBe 3

        of(frbDepthTwoRightRightPreorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightRightPostorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightRightInorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightRightBreadthFirst, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightRightPreorder, RULE.MAX).height() shouldBe 3
        of(frbDepthTwoRightRightPostorder, RULE.MAX).height() shouldBe 3
        of(frbDepthTwoRightRightInorder, RULE.MAX).height() shouldBe 3
        of(frbDepthTwoRightRightBreadthFirst, RULE.MAX).height() shouldBe 3

        of(frbDepthTwoRightLeftPreorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightLeftPostorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightLeftInorder, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightLeftBreadthFirst, RULE.MIN).height() shouldBe 3
        of(frbDepthTwoRightLeftPreorder, RULE.MAX).height() shouldBe 3
        of(frbDepthTwoRightLeftPostorder, RULE.MAX).height() shouldBe 3
        of(frbDepthTwoRightLeftInorder, RULE.MAX).height() shouldBe 3
        of(frbDepthTwoRightLeftBreadthFirst, RULE.MAX).height() shouldBe 3

        of(frbWikiPreorder, RULE.MIN).height() shouldBe 4
        of(frbWikiPostorder, RULE.MIN).height() shouldBe 4
        of(frbWikiInorder, RULE.MIN).height() shouldBe 4
        of(frbWikiBreadthFirst, RULE.MIN).height() shouldBe 4
        of(frbWikiPreorder, RULE.MAX).height() shouldBe 4
        of(frbWikiPostorder, RULE.MAX).height() shouldBe 4
        of(frbWikiInorder, RULE.MAX).height() shouldBe 4
        of(frbWikiBreadthFirst, RULE.MAX).height() shouldBe 4

        of(frbSlideSharePreorder, RULE.MIN).height() shouldBe 4
        of(frbSlideSharePostorder, RULE.MIN).height() shouldBe 4
        of(frbSlideShareInorder, RULE.MIN).height() shouldBe 4
        of(frbSlideShareBreadthFirst, RULE.MIN).height() shouldBe 4
        of(frbSlideSharePreorder, RULE.MAX).height() shouldBe 4
        of(frbSlideSharePostorder, RULE.MAX).height() shouldBe 4
        of(frbSlideShareInorder, RULE.MAX).height() shouldBe 4
        of(frbSlideShareBreadthFirst, RULE.MAX).height() shouldBe 4
    }

    test("size") {
        FBHMinNil.size() shouldBe 0
        FBHMinNil.size() shouldBe 0
        FBHBranch(
            TKVEntry.of(mEntry.hashCode(), mEntry),
            FBHMaxNil,
            FBHMaxNil
        ).size() shouldBe 1

        of(ttDepthOneRight.preorder(), RULE.MIN).size() shouldBe 2
        of(ttDepthOneRight.postorder(), RULE.MIN).size() shouldBe 2
        of(ttDepthOneRight.inorder(), RULE.MIN).size() shouldBe 2
        of(ttDepthOneRight.breadthFirst(), RULE.MIN).size() shouldBe 2
        of(ttDepthOneRight.preorder(), RULE.MAX).size() shouldBe 2
        of(ttDepthOneRight.postorder(), RULE.MAX).size() shouldBe 2
        of(ttDepthOneRight.inorder(), RULE.MAX).size() shouldBe 2
        of(ttDepthOneRight.breadthFirst(), RULE.MAX).size() shouldBe 2

        of(frbDepthOneLeft.preorder(), RULE.MIN).size() shouldBe 2
        of(frbDepthOneLeft.postorder(), RULE.MIN).size() shouldBe 2
        of(frbDepthOneLeft.inorder(), RULE.MIN).size() shouldBe 2
        of(frbDepthOneLeft.breadthFirst(), RULE.MIN).size() shouldBe 2
        of(frbDepthOneLeft.preorder(), RULE.MAX).size() shouldBe 2
        of(frbDepthOneLeft.postorder(), RULE.MAX).size() shouldBe 2
        of(frbDepthOneLeft.inorder(), RULE.MAX).size() shouldBe 2
        of(frbDepthOneLeft.breadthFirst(), RULE.MAX).size() shouldBe 2

        of(frbDepthOneFullPreorder, RULE.MIN).size() shouldBe 3
        of(frbDepthOneFullInorder, RULE.MIN).size() shouldBe 3
        of(frbDepthOneFullPostorder, RULE.MIN).size() shouldBe 3
        of(frbDepthOneFullBreadthFirst, RULE.MIN).size() shouldBe 3
        of(frbDepthOneFullPreorder, RULE.MAX).size() shouldBe 3
        of(frbDepthOneFullInorder, RULE.MAX).size() shouldBe 3
        of(frbDepthOneFullPostorder, RULE.MAX).size() shouldBe 3
        of(frbDepthOneFullBreadthFirst, RULE.MAX).size() shouldBe 3

        of(ttDepthTwoLeftRight.preorder(), RULE.MIN).size() shouldBe 4
        of(ttDepthTwoLeftRight.postorder(), RULE.MIN).size() shouldBe 4
        of(ttDepthTwoLeftRight.inorder(), RULE.MIN).size() shouldBe 4
        of(ttDepthTwoLeftRight.breadthFirst(), RULE.MIN).size() shouldBe 4
        of(ttDepthTwoLeftRight.preorder(), RULE.MAX).size() shouldBe 4
        of(ttDepthTwoLeftRight.postorder(), RULE.MAX).size() shouldBe 4
        of(ttDepthTwoLeftRight.inorder(), RULE.MAX).size() shouldBe 4
        of(ttDepthTwoLeftRight.breadthFirst(), RULE.MAX).size() shouldBe 4

        of(frbDepthTwoLeftLeftPreorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoLeftLeftPostorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoLeftLeftInorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoLeftLeftBreadthFirst, RULE.MIN).size() shouldBe 4

        of(frbDepthTwoRightRightPreorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightRightPostorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightRightInorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightRightBreadthFirst, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightRightPreorder, RULE.MAX).size() shouldBe 4
        of(frbDepthTwoRightRightPostorder, RULE.MAX).size() shouldBe 4
        of(frbDepthTwoRightRightInorder, RULE.MAX).size() shouldBe 4
        of(frbDepthTwoRightRightBreadthFirst, RULE.MAX).size() shouldBe 4

        of(frbDepthTwoRightLeftPreorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightLeftPostorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightLeftInorder, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightLeftBreadthFirst, RULE.MIN).size() shouldBe 4
        of(frbDepthTwoRightLeftPreorder, RULE.MAX).size() shouldBe 4
        of(frbDepthTwoRightLeftPostorder, RULE.MAX).size() shouldBe 4
        of(frbDepthTwoRightLeftInorder, RULE.MAX).size() shouldBe 4
        of(frbDepthTwoRightLeftBreadthFirst, RULE.MAX).size() shouldBe 4

        of(frbWikiPreorder, RULE.MIN).size() shouldBe 9
        of(frbWikiPostorder, RULE.MIN).size() shouldBe 9
        of(frbWikiInorder, RULE.MIN).size() shouldBe 9
        of(frbWikiBreadthFirst, RULE.MIN).size() shouldBe 9
        of(frbWikiPreorder, RULE.MAX).size() shouldBe 9
        of(frbWikiPostorder, RULE.MAX).size() shouldBe 9
        of(frbWikiInorder, RULE.MAX).size() shouldBe 9
        of(frbWikiBreadthFirst, RULE.MAX).size() shouldBe 9

        of(frbSlideSharePreorder, RULE.MIN).size() shouldBe 8
        of(frbSlideSharePostorder, RULE.MIN).size() shouldBe 8
        of(frbSlideShareInorder, RULE.MIN).size() shouldBe 8
        of(frbSlideShareBreadthFirst, RULE.MIN).size() shouldBe 8
        of(frbSlideSharePreorder, RULE.MAX).size() shouldBe 8
        of(frbSlideSharePostorder, RULE.MAX).size() shouldBe 8
        of(frbSlideShareInorder, RULE.MAX).size() shouldBe 8
        of(frbSlideShareBreadthFirst, RULE.MAX).size() shouldBe 8
    }

    test("top") {
        FBHMinNil.top() shouldBe null
        FBHMaxNil.top() shouldBe null
        FBHBranch(
            mEntry,
            FBHMaxNil,
            FBHMaxNil
        ).top() shouldBe mEntry

        of(ttDepthOneRight.preorder(), RULE.MIN).top() shouldBe mEntry
        of(ttDepthOneRight.postorder(), RULE.MIN).top() shouldBe mEntry
        of(ttDepthOneRight.inorder(), RULE.MIN).top() shouldBe  mEntry
        of(ttDepthOneRight.breadthFirst(), RULE.MIN).top() shouldBe  mEntry
        of(ttDepthOneRight.preorder(), RULE.MAX).top() shouldBe nEntry
        of(ttDepthOneRight.postorder(), RULE.MAX).top() shouldBe nEntry
        of(ttDepthOneRight.inorder(), RULE.MAX).top() shouldBe nEntry
        of(ttDepthOneRight.breadthFirst(), RULE.MAX).top() shouldBe nEntry

        of(frbDepthOneLeft.preorder(), RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneLeft.postorder(), RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneLeft.inorder(), RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneLeft.breadthFirst(), RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneLeft.preorder(), RULE.MAX).top() shouldBe mEntry
        of(frbDepthOneLeft.postorder(), RULE.MAX).top() shouldBe mEntry
        of(frbDepthOneLeft.inorder(), RULE.MAX).top() shouldBe mEntry
        of(frbDepthOneLeft.breadthFirst(), RULE.MAX).top() shouldBe mEntry

        of(frbDepthOneFullPreorder, RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneFullInorder, RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneFullPostorder, RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneFullBreadthFirst, RULE.MIN).top() shouldBe lEntry
        of(frbDepthOneFullPreorder, RULE.MAX).top() shouldBe nEntry
        of(frbDepthOneFullInorder, RULE.MAX).top() shouldBe nEntry
        of(frbDepthOneFullPostorder, RULE.MAX).top() shouldBe nEntry
        of(frbDepthOneFullBreadthFirst, RULE.MAX).top() shouldBe nEntry

        of(ttDepthTwoLeftRight.preorder(), RULE.MIN).top() shouldBe lEntry
        of(ttDepthTwoLeftRight.postorder(), RULE.MIN).top() shouldBe lEntry
        of(ttDepthTwoLeftRight.inorder(), RULE.MIN).top() shouldBe lEntry
        of(ttDepthTwoLeftRight.breadthFirst(), RULE.MIN).top() shouldBe lEntry
        of(ttDepthTwoLeftRight.preorder(), RULE.MAX).top() shouldBe sEntry
        of(ttDepthTwoLeftRight.postorder(), RULE.MAX).top() shouldBe sEntry
        of(ttDepthTwoLeftRight.inorder(), RULE.MAX).top() shouldBe sEntry
        of(ttDepthTwoLeftRight.breadthFirst(), RULE.MAX).top() shouldBe sEntry

        of(frbDepthTwoLeftLeftPreorder, RULE.MIN).top() shouldBe eEntry
        of(frbDepthTwoLeftLeftPostorder, RULE.MIN).top() shouldBe eEntry
        of(frbDepthTwoLeftLeftInorder, RULE.MIN).top() shouldBe eEntry
        of(frbDepthTwoLeftLeftBreadthFirst, RULE.MIN).top() shouldBe eEntry
        of(frbDepthTwoLeftLeftPreorder, RULE.MAX).top() shouldBe sEntry
        of(frbDepthTwoLeftLeftPostorder, RULE.MAX).top() shouldBe sEntry
        of(frbDepthTwoLeftLeftInorder, RULE.MAX).top() shouldBe sEntry
        of(frbDepthTwoLeftLeftBreadthFirst, RULE.MAX).top() shouldBe sEntry

        of(frbDepthTwoRightRightPreorder, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightRightPostorder, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightRightInorder, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightRightBreadthFirst, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightRightPreorder, RULE.MAX).top() shouldBe uEntry
        of(frbDepthTwoRightRightPostorder, RULE.MAX).top() shouldBe uEntry
        of(frbDepthTwoRightRightInorder, RULE.MAX).top() shouldBe uEntry
        of(frbDepthTwoRightRightBreadthFirst, RULE.MAX).top() shouldBe uEntry

        of(frbDepthTwoRightLeftPreorder, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightLeftPostorder, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightLeftInorder, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightLeftBreadthFirst, RULE.MIN).top() shouldBe mEntry
        of(frbDepthTwoRightLeftPreorder, RULE.MAX).top() shouldBe sEntry
        of(frbDepthTwoRightLeftPostorder, RULE.MAX).top() shouldBe sEntry
        of(frbDepthTwoRightLeftInorder, RULE.MAX).top() shouldBe sEntry
        of(frbDepthTwoRightLeftBreadthFirst, RULE.MAX).top() shouldBe sEntry

        of(frbWikiPreorder, RULE.MIN).top() shouldBe aEntry
        of(frbWikiPostorder, RULE.MIN).top() shouldBe aEntry
        of(frbWikiInorder, RULE.MIN).top() shouldBe aEntry
        of(frbWikiBreadthFirst, RULE.MIN).top() shouldBe aEntry
        of(frbWikiPreorder, RULE.MAX).top() shouldBe iEntry
        of(frbWikiPostorder, RULE.MAX).top() shouldBe iEntry
        of(frbWikiInorder, RULE.MAX).top() shouldBe iEntry
        of(frbWikiBreadthFirst, RULE.MAX).top() shouldBe iEntry

        of(frbSlideSharePreorder, RULE.MIN).top() shouldBe n17Entry
        of(frbSlideSharePostorder, RULE.MIN).top() shouldBe n17Entry
        of(frbSlideShareInorder, RULE.MIN).top() shouldBe n17Entry
        of(frbSlideShareBreadthFirst, RULE.MIN).top() shouldBe n17Entry
        of(frbSlideSharePreorder, RULE.MAX).top() shouldBe n88Entry
        of(frbSlideSharePostorder, RULE.MAX).top() shouldBe n88Entry
        of(frbSlideShareInorder, RULE.MAX).top() shouldBe n88Entry
        of(frbSlideShareBreadthFirst, RULE.MAX).top() shouldBe n88Entry
    }

    //
    // companion object
    //

    test("co.nul") {
        minHeapNul<Int, Int>() shouldBe FBHMinNil
        maxHeapNul<Int, Int>() shouldBe FBHMaxNil
    }

    test("co.==") {
        (minHeapNul<Int, Int>() == minHeapNul<Int, Int>()) shouldBe true
        (minHeapNul<Int, Int>() == FBHMinNil) shouldBe true
        (FBHMinNil == minHeapNul<Int, Int>()) shouldBe true
        (maxHeapNul<Int, Int>() == FBHMaxNil) shouldBe true
        (FBHMaxNil == maxHeapNul<Int, Int>()) shouldBe true
        (maxHeapNul<Int, Int>() == FBHMinNil) shouldBe false
        (minHeapNul<Int, Int>() == FBHMaxNil) shouldBe false
        (FBHBranch(
            TKVEntry.of(aEntry.hashCode(), aEntry),
            FBHMaxNil,
            FBHMaxNil
        ) == FBHBranch(
            TKVEntry.of(aEntry.hashCode(), aEntry),
            FBHMaxNil,
            FBHMaxNil
        )) shouldBe true
        (FBHBranch(
            TKVEntry.of(aEntry.hashCode(), aEntry),
            FBHMinNil,
            FBHMinNil
        ) == FBHBranch(
            TKVEntry.of(aEntry.hashCode(), aEntry),
            FBHMinNil,
            FBHMinNil
        )) shouldBe true
        (FBHBranch(
            TKVEntry.of(aEntry.hashCode(), aEntry),
            FBHMinNil,
            FBHMinNil
        ) == FBHBranch(
            TKVEntry.of(aEntry.hashCode(), aEntry),
            FBHMaxNil,
            FBHMaxNil
        )) shouldBe false
    }

    test("co.insert item throws"){
        shouldThrow<RuntimeException> {
            insert(FBHMaxNil, mEntry, RULE.MIN) shouldBe FBHBranch(
                mEntry,
                FBHMinNil,
                FBHMinNil
            )
        }
    }

    test("co.insert item") {
        insert(FBHMinNil, mEntry, RULE.MIN) shouldBe FBHBranch(
            mEntry,
            FBHMinNil,
            FBHMinNil
        )
        insert(FBHMaxNil, mEntry, RULE.MAX) shouldBe FBHBranch(
            mEntry,
            FBHMaxNil,
            FBHMaxNil
        )

        val a1 = insert(
            FBHBranch(
                mEntry,
                FBHMinNil,
                FBHMinNil
            ),nEntry, RULE.MIN)
        val b1 = insert(
            FBHBranch(
                nEntry,
                FBHMinNil,
                FBHMinNil
            ),mEntry, RULE.MIN)
        val c1 = of(ttDepthOneRight.postorder(), RULE.MIN)
        a1 shouldBe c1
        a1 shouldBe b1

        val a2 = insert(
            FBHBranch(
                mEntry,
                FBHMaxNil,
                FBHMaxNil
            ),nEntry, RULE.MAX)
        val b2 = insert(
            FBHBranch(
                nEntry,
                FBHMaxNil,
                FBHMaxNil
            ),mEntry, RULE.MAX)
        val c2 = of(ttDepthOneRight.preorder(), RULE.MAX)
        a2 shouldBe c2
        a2 shouldBe b2

        val a3 = insert(of(ttDepthOneRight.preorder(), RULE.MIN), lEntry, RULE.MIN)
        val b3 = insert(of(frbDepthOneLeft.preorder(), RULE.MIN), nEntry, RULE.MIN)
        val c3 = of(frbDepthOneFullPreorder, RULE.MIN)
        a3.height() shouldBe 2
        b3.height() shouldBe 2
        c3.height() shouldBe 2
        a3.top() shouldBe b3.top()
        c3.top() shouldBe b3.top()

        val a4 = insert(of(ttDepthOneRight.preorder(), RULE.MAX), lEntry, RULE.MAX)
        val b4 = insert(of(frbDepthOneLeft.preorder(), RULE.MAX), nEntry, RULE.MAX)
        val c4 = of(frbDepthOneFullPreorder, RULE.MAX)
        a4.height() shouldBe 2
        b4.height() shouldBe 2
        c4.height() shouldBe 2
        a4.top() shouldBe b4.top()
        c4.top() shouldBe b4.top()

        val a5 = insert(of(ttDepthTwoRightPartial.postorder(), RULE.MIN), rEntry, RULE.MIN)
        val c5 = of(frbDepthTwoRightLeftPreorder, RULE.MIN)
        a5.height() shouldBe 3
        c5.height() shouldBe 3
        a5.top() shouldBe c5.top()
        val d5 = insert(a5, uEntry, RULE.MIN)
        val e5 = insert(c5, uEntry, RULE.MIN)
        d5.height() shouldBe 3
        e5.height() shouldBe 3
        d5.top() shouldBe e5.top()

        val a6 = insert(of(ttDepthTwoRightPartial.postorder(), RULE.MAX), rEntry, RULE.MAX)
        val c6 = of(frbDepthTwoRightLeftPreorder, RULE.MAX)
        a6.height() shouldBe 3
        c6.height() shouldBe 3
        a6.top() shouldBe c6.top()
        val d6 = insert(a6, uEntry, RULE.MAX)
        val e6 = insert(c6, uEntry, RULE.MAX)
        d6.height() shouldBe 3
        e6.height() shouldBe 3
        d6.top() shouldBe e6.top()
    }

    test("co.enumerate wikiMinHeap") {
        val wikiMinHeap = minHeapOf(wikiPreorder)
        heapSane(wikiMinHeap) shouldBe true
        val ary = toArray(enumerate(wikiMinHeap).fmap { it.getk() })
        ary.sort()
        val ora = toArray(wikiInorder.fmap { it.getk() })
        ary shouldBe ora
    }

    test("co.enumerate wikiMaxHeap") {
        val wikiMaxHeap = maxHeapOf(frbWikiBreadthFirst)
        heapSane(wikiMaxHeap) shouldBe true
        val ary = toArray(enumerate(wikiMaxHeap).fmap { it.getk() })
        ary.sort()
        val ora = toArray(wikiInorder.fmap { it.getk() })
        ary shouldBe ora
    }

    test("co.enumerate ssMinHeap") {
        val ssMinHeap = minHeapOf(slideSharePreorder)
        heapSane(ssMinHeap) shouldBe true
        val ary = toArray(enumerate(ssMinHeap).fmap { it.getk() })
        ary.sort()
        val ora = toArray(slideShareInorder.fmap { it.getk() })
        ary shouldBe ora
    }

    test("co.enumerate ssMaxHeap") {
        val ssMaxHeap = maxHeapOf(frbSlideShareBreadthFirst)
        heapSane(ssMaxHeap) shouldBe true
        val ary = toArray(enumerate(ssMaxHeap).fmap { it.getk() })
        ary.sort()
        val ora = toArray(slideShareInorder.fmap { it.getk() })
        ary shouldBe ora
    }

    test("co.(insert/add/pop) item (property) sorted asc, small, min") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val oracle = (Array(n) { it })
            val heapo = minHeapOf(values.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val auto = toArray(enumerate(heapo).fmap{ it.getk() })
            auto.sort()
            auto shouldBe oracle

            var heapi: FBHeap<Int, Int> =
                FBHMinNil
            for (v in values) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) sorted desc, small, min") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val oracle = (Array(n) { it })
            val heapo = minHeapOf(values.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val aut = toArray(enumerate(heapo).fmap{ it.getk() })
            aut.sort()
            aut shouldBe oracle
            var heapi: FBHeap<Int, Int> =
                FBHMinNil

            for (v in values) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) sorted asc, small, max") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val oracle = (Array(n) { it })
            val heapo = maxHeapOf(values.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val auto = toArray(enumerate(heapo).fmap{ it.getk() })
            auto.sort()
            auto shouldBe oracle

            var heapi: FBHeap<Int, Int> =
                FBHMaxNil
            for (v in values) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) sorted desc, small, max") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val oracle = (Array(n) { it })
            val heapo = maxHeapOf(values.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val aut = toArray(enumerate(heapo).fmap{ it.getk() })
            aut.sort()
            aut shouldBe oracle
            var heapi: FBHeap<Int, Int> =
                FBHMaxNil

            for (v in values) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) sorted asc, large, min").config(enabled = true) {
        checkAll(2, Arb.int(10000..100000)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val oracle = (Array(n) { it })
            val heapo = minHeapOf(values.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val auto = toArray(enumerate(heapo).fmap{ it.getk() })
            auto.sort()
            auto shouldBe oracle

            var heapi: FBHeap<Int, Int> =
                FBHMinNil
            for (v in values) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) sorted desc, large, max").config(enabled = true) {
        checkAll(2, Arb.int(10000..100000)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val oracle = (Array(n) { it })
            val heapo = maxHeapOf(values.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val aut = toArray(enumerate(heapo).fmap{ it.getk() })
            aut.sort()
            aut shouldBe oracle
            var heapi: FBHeap<Int, Int> =
                FBHMaxNil

            for (v in values) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) random, small, min") {
        // checkAll(PropTestConfig(iterations = 50, seed = 5792981224933522729), Arb.int(20..100)) { n ->
        checkAll(500, Arb.int(10..400)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            // shuffled.shuffle(Random(seed = 5792981224933522729))
            shuffled.shuffle()
            val oracle = (Array(n) { it })

            val heapo = minHeapOf(shuffled.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val auto = toArray(enumerate(heapo).fmap{ it.getk() })
            auto.sort()
            auto shouldBe oracle

            var heapi: FBHeap<Int, Int> =
                FBHMinNil
            for (v in shuffled) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) random, small, max") {
        // checkAll(PropTestConfig(iterations = 50, seed = 5792981224933522729), Arb.int(20..100)) { n ->
        checkAll(500, Arb.int(10..400)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            // shuffled.shuffle(Random(seed = 5792981224933522729))
            shuffled.shuffle()
            val oracle = (Array(n) { it })

            val heapo = maxHeapOf(shuffled.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val auto = toArray(enumerate(heapo).fmap{ it.getk() })
            auto.sort()
            auto shouldBe oracle

            var heapi: FBHeap<Int, Int> =
                FBHMaxNil
            for (v in shuffled) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }

    test("co.(insert/add/pop) item (property) random, large, min max").config(enabled = true) {
        checkAll(3, Arb.int(10000..100000)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i)}
            // shuffled.shuffle(Random(seed = 5792981224933522729))
            shuffled.shuffle()
            val oracle = (Array(n) { it })

            val heapo = minHeapOf(shuffled.iterator())
            displayHeapOnVerbose(heapo, n)
            heapSane(heapo) shouldBe true
            heapo.size() shouldBe n
            val auto = toArray(enumerate(heapo).fmap{ it.getk() })
            auto.sort()
            auto shouldBe oracle

            var heapi: FBHeap<Int, Int> =
                FBHMaxNil
            for (v in shuffled) {
                heapi = add(heapi, v)
            }
            displayHeapOnVerbose(heapi, n)
            heapSane(heapi) shouldBe true
            heapi.size() shouldBe n
            val auti = toArray(enumerate(heapi).fmap{ it.getk() })
            auti.sort()
            auti shouldBe oracle
        }
    }
})

private fun displayHeapOnVerbose(heap: FBHeap<Int, Int>, n: Int, force: Boolean = false) {
    if (verbose || ! heapSane(heap) || force) {
        print("size " + n)
        println(" $heap")
    }
}
