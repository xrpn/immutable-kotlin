package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.of
import com.xrpn.immutable.FBSTree.Companion.ofvi
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FBSTreeFilteringTest : FunSpec({

    beforeTest {}

    test("fempty") {
        FBSTNil.fempty() shouldBe true
        ofvi(1).fempty() shouldBe false
    }

    test("ffilter, filterNot, ffind (A)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val sAll1: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val snAll1: FBSTree<Int, Int> = tree.ffilterNot(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            snAll1.size shouldBe 0
            saAll1.size shouldBe ora1 * 2
            val sEmpty1: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val snEmpty1: FBSTree<Int, Int> = tree.ffilterNot(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            snEmpty1.size shouldBe ora1
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val snAll2: FBSTree<Int, Int> = tree.ffilterNot(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            snAll2.size shouldBe theRestSansOra2 + 1
            saAll2.size shouldBe ora2 * 2
            val sEmpty2: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val snEmpty2: FBSTree<Int, Int> = tree.ffilterNot(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            sEmpty2.size shouldBe theRestSansOra2
            snEmpty2.size shouldBe ora1 - theRestSansOra2
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
        }
    }

    test("ffilter, ffind (B)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val sAll1: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            saAll1.size shouldBe ora1 * 2
            val sEmpty1: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            saAll2.size shouldBe ora2 * 2
            val sEmpty2: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
            sEmpty2.size shouldBe theRestSansOra2
        }
    }

    test("ffilter, ffind (C)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.of(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val sAll1: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            saAll1.size shouldBe ora1 * 2
            val sEmpty1: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            saAll2.size shouldBe ora2 * 2
            val sEmpty2: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
            sEmpty2.size shouldBe theRestSansOra2
        }
    }

    test("ffindDistinct (A)") {
        // checkAll(PropTestConfig(seed = 5699135300091264211), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffindDistinct (B)") {
        checkAll(50, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffindDistinct (C)") {
        checkAll(50, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.of(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("fleftMost") {
        FBSTNil.fleftMost() shouldBe null
    }

    test("fpick") {
        FBSTNil.fpick() shouldBe null
    }

    test("frightMost") {
        FBSTNil.frightMost() shouldBe null
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
        FBSTNil.froot() shouldBe null
        for (size in IntRange(1, 20)) {
            val ary = IntArray(size) {nextInt()}
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).froot() shouldBe TKVEntry.ofIntKey(ary[0])
        }
    }
})
