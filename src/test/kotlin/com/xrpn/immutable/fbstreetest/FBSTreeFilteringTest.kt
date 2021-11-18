package com.xrpn.immutable.fbstreetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.of
import com.xrpn.immutable.FBSTree.Companion.ofvi
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbsItree
import kotlin.random.Random.Default.nextInt

class FBSTreeFilteringTest : FunSpec({

    val repeats = 50

    beforeTest {}

    fun <A, B: Any> isChildMatchOracle(node: FBSTNode<A, B>, match: TKVEntry<A, B>): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
        val leftChildMatch = (node.bLeft is FBSTNode) && node.bLeft.entry == match
        val rightChildMatch = (node.bRight is FBSTNode) && node.bRight.entry == match
        return Pair(leftChildMatch, rightChildMatch)
    }

    test("dropAll (nil)") {
        nul<Int, Int>().fdropAll(FList.emptyIMList<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree()
        nul<Int, Int>().fdropAll(FLCons(1.toIAEntry(), FLNil)) shouldBe emptyIMBTree()
    }

    test("dropAll") {
        ofvi(1,2,3).fdropAll(FList.emptyIMList()) shouldBe ofvi(1,2,3)
        ofvi(1,2,3).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe ofvi(3)
        ofvi(1,2,3,4).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe ofvi(3, 4)
        ofvi(1,2,3).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(1)
        ofvi(1,2,3,4).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(1, 4)
        ofvi(1,2,3).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(2)
        ofvi(1,2,3,4).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(2, 4)
    }

    test("dropAlt (nil)") {
        nul<Int, Int>().fdropAlt(emptyIMBTree<Int, Int>()) shouldBe emptyIMBTree()
        (nul<Int, Int>().fdropAlt(emptyIMBTree<Int, Int>()) === emptyIMBTree<Int,Int>()) shouldBe true
        nul<Int, Int>().fdropAlt(of(1.toIAEntry())) shouldBe emptyIMBTree()
    }

    test("dropAlt") {
        ofvi(1,2,3).fdropAlt(FRBTree.emptyIMBTree<Int, Int>()) shouldBe ofvi(1,2,3)
        ofvi(1,2,3).fdropAlt(FRBTree.of(1.toIAEntry(), 2.toIAEntry())) shouldBe ofvi(3)
        ofvi(1,2,3,4).fdropAlt(FRBTree.of(1.toIAEntry(), 2.toIAEntry())) shouldBe ofvi(3, 4)
        ofvi(1,2,3).fdropAlt(FRBTree.of(2.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(1)
        ofvi(1,2,3,4).fdropAlt(FRBTree.of(2.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(1, 4)
        ofvi(1,2,3).fdropAlt(FRBTree.of(1.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(2)
        ofvi(1,2,3,4).fdropAlt(FRBTree.of(1.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(2, 4)
    }

    test("ffind (A)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(repeats, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            saAll1.size shouldBe ora1 * 2
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            saAll2.size shouldBe ora2 * 2
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
        }
    }

    test("ffind (B)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(repeats, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            saAll1.size shouldBe ora1 * 2
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            saAll2.size shouldBe ora2 * 2
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
        }
    }

    test("ffind (C)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(repeats, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            saAll1.size shouldBe ora1 * 2
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            saAll2.size shouldBe ora2 * 2
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
        }
    }

    test("ffind, ffindDistinct (nil)") {
        nul<Int, Int>().ffind { false } shouldBe FList.emptyIMList()
        nul<Int, Int>().ffindDistinct { true } shouldBe null
    }

    test("ffind") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    if (t.ffind{ it == acc.head }.fsize() != 1)  fail("not found: ${acc.head}")
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.ffindItem(zEntry) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffind, ffindDistinct (A)") {
        checkAll(repeats, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)
            tree1.ffind { false } shouldBe FList.emptyIMList()

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffind, ffindDistinct (B)") {
        checkAll(repeats, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)
            tree1.ffind { false } shouldBe FList.emptyIMList()

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffind, ffindDistinct (C)") {
        checkAll(repeats, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)
            tree1.ffind { false } shouldBe FList.emptyIMList()

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffindItem") {
        nul<Int, Int>().ffindItem(1.toIAEntry()) shouldBe null
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindItem(acc.head)) {
                        is FBSTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.ffindItem(zEntry) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindKey") {
        nul<Int, Int>().ffindKey(1) shouldBe null
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindKey(acc.head.getk())) {
                        is FBSTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.ffindKey(zEntry.getk()) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindKey(100) shouldBe null
    }

    test("ffindLastItem no dups") {
        nul<Int, Int>().ffindLastItem(1.toIAEntry()) shouldBe null
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastItem(acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchOracle(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.ffindLastItem(zEntry) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindLastItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindLastItem with dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastItem(acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchOracle(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTreeLoose.finsert(wikiTree.froot()!!), wikiPreorder)
        go(wikiTreeLoose.finsert(wikiTree.froot()!!).finsert(wikiTree.froot()!!), wikiPreorder)
        go(wikiTreeLoose.finsert(wikiTree.fleftMost()!!), wikiPreorder)
        go(wikiTreeLoose.finsert(wikiTree.frightMost()!!), wikiPreorder)
        go(slideShareTreeLoose.finsert(slideShareTree.fleftMost()!!).finsert(slideShareTree.fleftMost()!!), slideShareBreadthFirst)
        go(slideShareTreeLoose.finsert(slideShareTree.frightMost()!!).finsert(slideShareTree.frightMost()!!), slideShareBreadthFirst)
    }

    test("ffindLastKey no dups") {
        nul<Int, Int>().ffindLastKey(1) shouldBe null
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastKey(acc.head.getk())) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchOracle(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.ffindLastKey(zEntry.getk()) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindLastKey(100) shouldBe null
    }

    test("ffindLastKey with dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastKey(acc.head.getk())) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchOracle(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTreeLoose.finsert(wikiTree.froot()!!), wikiPreorder)
        go(wikiTreeLoose.finsert(wikiTree.froot()!!).finsert(wikiTree.froot()!!), wikiPreorder)
        go(wikiTreeLoose.finsert(wikiTree.fleftMost()!!), wikiPreorder)
        go(wikiTreeLoose.finsert(wikiTree.frightMost()!!), wikiPreorder)
        go(slideShareTreeLoose.finsert(slideShareTree.fleftMost()!!).finsert(slideShareTree.fleftMost()!!), slideShareBreadthFirst)
        go(slideShareTreeLoose.finsert(slideShareTree.frightMost()!!).finsert(slideShareTree.frightMost()!!), slideShareBreadthFirst)
    }

    test("ffindValueOfKey") {
        nul<Int, Int>().ffindValueOfKey(1) shouldBe null
        // checkAll(PropTestConfig(seed = 5699135300091264211), Arb.int(20..100)) { n ->
        checkAll(repeats, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val ora1 = values.size
            val ixs = ((ora1/5)..(ora1/3))
            val tree1: FBSTree<Int, Int> = of(values.iterator(), allowDups = true)
            tree1.size shouldBe ora1
            for (ix in ixs) {
                tree1.ffindValueOfKey(ix) shouldBe ix
            }
            tree1.ffindValueOfKey(ora1+2) shouldBe null
        }
    }

    test("fleftMost") {
        FBSTGeneric.empty.fleftMost() shouldBe null
        FBSTUnique.empty.fleftMost() shouldBe null
        of(1.toIAEntry()).fleftMost() shouldBe 1.toIAEntry()
        of(3.toIAEntry(), 1.toIAEntry()).fleftMost() shouldBe 1.toIAEntry()
        of(3.toIAEntry(), 4.toIAEntry()).fleftMost() shouldBe 3.toIAEntry()
        of(3.toIAEntry(), 1.toIAEntry(), 4.toIAEntry()).fleftMost() shouldBe 1.toIAEntry()
    }

    test("fhasDups") {
        Arb.fbsItree<Int, Int>(Arb.int(0..200)).checkAll(repeats) { fbst ->
            val ss = fbst.copyToMutableMap().size
            fbst.fhasDups() shouldBe (ss != fbst.size)
        }
    }

    test("fisDup") {
        nul<Int, Int>().fisDup(1.toIAEntry()) shouldBe false
        slideShareTree.fisDup(slideShareTree.fleftMost()!!) shouldBe false
        val aux5a = slideShareTreeLoose.finsert(slideShareTree.fleftMost()!!)
        aux5a.fisDup(slideShareTree.fleftMost()!!) shouldBe true
        aux5a.fisDup(slideShareTree.froot()!!) shouldBe false
        val aux5b = aux5a.finsert(slideShareTree.fleftMost()!!)
        aux5b.fisDup(slideShareTree.fleftMost()!!) shouldBe true
        aux5b.fisDup(slideShareTree.froot()!!) shouldBe false
        val aux5c = slideShareTreeLoose.finsert(slideShareTree.froot()!!)
        aux5c.fisDup(slideShareTree.fleftMost()!!) shouldBe false
        aux5c.fisDup(slideShareTree.froot()!!) shouldBe true
    }

    test("fparentOf") {
        nul<Int, String>().fparentOf(TKVEntry.ofIntKey("")) shouldBe null
        nul<Int, String>(true).fparentOf(TKVEntry.ofIntKey("")) shouldBe null
        (FBSTNode.of(false, mEntry).fparentOf(mEntry) === FBSTUnique.empty) shouldBe true
        (FBSTNode.of(true, mEntry).fparentOf(mEntry) === FBSTGeneric.empty) shouldBe true

        depthOneLeft.fparentOf(lEntry) shouldBe depthOneLeft
        depthOneRight.fparentOf(nEntry) shouldBe depthOneRight
        depthOneFull.fparentOf(lEntry) shouldBe depthOneFull
        depthOneFull.fparentOf(nEntry) shouldBe depthOneFull

        (depthTwoLeftRight.fparentOf(mEntry) as FBSTNode).entry shouldBe lEntry
        (depthTwoLeftRight.fparentOf(lEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoLeftRight.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoLeftLeft.fparentOf(eEntry) as FBSTNode).entry shouldBe lEntry
        (depthTwoLeftLeft.fparentOf(lEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoLeftLeft.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightRight.fparentOf(uEntry) as FBSTNode).entry shouldBe sEntry
        (depthTwoRightRight.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightRight.fparentOf(mEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightLeft.fparentOf(rEntry) as FBSTNode).entry shouldBe sEntry
        (depthTwoRightLeft.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightLeft.fparentOf(mEntry) as FBSTNode).entry shouldBe nEntry

        wikiTree.fparentOf(fEntry)  /* parent of root */ .shouldBeInstanceOf<FBSTUnique>()
        wikiTreeLoose.fparentOf(fEntry)  /* parent of root */ .shouldBeInstanceOf<FBSTGeneric>()
        (wikiTree.fparentOf(cEntry) as FBSTNode).entry shouldBe dEntry
        (wikiTree.fparentOf(hEntry) as FBSTNode).entry shouldBe iEntry
        wikiTree.fparentOf(zEntry) /* parent of missing value */ shouldBe null

        (slideShareTree.fparentOf(n32Entry) as FBSTNode).entry shouldBe n17Entry
        (slideShareTree.fparentOf(n50Entry) as FBSTNode).entry shouldBe n78Entry
    }

    test("fpeek") {
        FBSTGeneric.empty.fpeek() shouldBe null
        FBSTUnique.empty.fpeek() shouldBe null
        slideShareTree.fpeek() shouldNotBe null
    }

    test("fpick int") {
        for (size in IntRange(0, 20)) {
            val ary = IntArray(size) {nextInt()}
            val min = ary.minOrNull()
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).fpeek() shouldBe min?.let {
                TKVEntry.ofIntKey(
                    min
                )
            }
        }
    }

    test("frightMost") {
        FBSTGeneric.empty.frightMost() shouldBe null
        FBSTUnique.empty.frightMost() shouldBe null
    }

    test("fleftMost frightMost int") {
        for (size in IntRange(1, 20)) {
            val ary = IntArray(size) {nextInt()}
            val max = ary.maxOrNull()!!
            val min = ary.minOrNull()!!
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).fleftMost() shouldBe TKVEntry.ofIntKey(min)
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).frightMost() shouldBe TKVEntry.ofIntKey(max)
        }
    }

    test("froot") {
        FBSTGeneric.empty.froot() shouldBe null
        FBSTUnique.empty.froot() shouldBe null
        for (size in IntRange(1, 20)) {
            val ary = IntArray(size) {nextInt()}
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).froot() shouldBe TKVEntry.ofIntKey(ary[0])
        }
    }
})
