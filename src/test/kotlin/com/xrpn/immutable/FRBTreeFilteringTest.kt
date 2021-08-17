package com.xrpn.immutable

import com.xrpn.immutable.FRBTree.Companion.of
import com.xrpn.immutable.FRBTree.Companion.ofvi
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FRBTreeFilteringTest : FunSpec({

    beforeTest {
        rbWikiTree = RBTree.of(frbWikiInorder)
        rbSlideShareTree = RBTree.of(frbSlideShareInorder)
    }

    test("fempty") {
        FRBTNil.fempty() shouldBe true
        ofvi(1).fempty() shouldBe false
    }

    test ("ffilter, ffilterNot, ffind (A)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FRBTree<Int, Int> = of(svalues.iterator())
            tree.size shouldBe ora1

            val sAll1: FRBTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val snAll1: FRBTree<Int, Int> = tree.ffilterNot(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            snAll1.size shouldBe 0
            sAll1.size shouldBe ora1
            saAll1.size shouldBe ora1
            val sEmpty1: FRBTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val snEmpty1: FRBTree<Int, Int> = tree.ffilterNot(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            snEmpty1.size shouldBe ora1
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FRBTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val snAll2: FRBTree<Int, Int> = tree.ffilterNot(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            snAll2.size shouldBe theRestSansOra2 + 1
            saAll2.size shouldBe ora2
            val sEmpty2: FRBTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val snEmpty2: FRBTree<Int, Int> = tree.ffilterNot(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            sEmpty2.size shouldBe theRestSansOra2
            snEmpty2.size shouldBe ora1 - theRestSansOra2
            saEmpty2.size shouldBe theRestSansOra2
        }
    }

    test ("ffilter, ffind (B)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FRBTree<Int, Int> = of(svalues.iterator())
            tree.size shouldBe ora1

            val sAll1: FRBTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            saAll1.size shouldBe ora1
            val sEmpty1: FRBTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FRBTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            saAll2.size shouldBe ora2
            val sEmpty2: FRBTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            sEmpty2.size shouldBe theRestSansOra2
            saEmpty2.size shouldBe theRestSansOra2
        }
    }

    test ("ffilter, ffind (C)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.of(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FRBTree<Int, Int> = of(svalues.iterator())
            tree.size shouldBe ora1

            val sAll1: FRBTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            saAll1.size shouldBe ora1
            val sEmpty1: FRBTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FRBTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            saAll2.size shouldBe ora2
            val sEmpty2: FRBTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            sEmpty2.size shouldBe theRestSansOra2
            saEmpty2.size shouldBe theRestSansOra2
        }
    }

    test("ffindDistinct") {
        checkAll(50, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val ora1 = values.size
            val tree: FRBTree<Int, Int> = of(values.iterator())
            tree.size shouldBe ora1

            tree.ffind { true } shouldBe tree.preorder(reverse = true)

            val ora2 = ora1 / 2
            tree.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree.ffindDistinct { it.getv() < ora2 } shouldBe null
            tree.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("fleftMost") {
        FRBTNil.fleftMost() shouldBe null
    }

    test("fpick") {
        FRBTNil.fpick() shouldBe null
    }

    test("frightMost") {
        FRBTNil.frightMost() shouldBe null
    }

    test("fpick int") {
        for (size in IntRange(0, 20)) {
            val ary = IntArray(size) {nextInt()}
            val min = ary.minOrNull()
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).fpick() shouldBe min?.let { TKVEntry.ofIntKey(min) }
        }
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
        FRBTNil.froot() shouldBe null
        val ary = IntArray(1) { nextInt() }
        of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).froot() shouldBe TKVEntry.ofIntKey(ary[0])
        val itemAry = Array(1) { TKVEntry.ofIntKey(nextInt()) }
        of(itemAry.iterator()).froot() shouldBe itemAry[0]
    }
})