package com.xrpn.immutable.fbstreetest

import com.xrpn.imapi.IMBTreeEqual2
import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.of
import com.xrpn.immutable.FBSTree.Companion.ofvi
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.cEntry
import com.xrpn.immutable.dEntry
import com.xrpn.immutable.eEntry
import com.xrpn.immutable.fEntry
import com.xrpn.immutable.hEntry
import com.xrpn.immutable.iEntry
import com.xrpn.immutable.lEntry
import com.xrpn.immutable.mEntry
import com.xrpn.immutable.n17Entry
import com.xrpn.immutable.n32Entry
import com.xrpn.immutable.n50Entry
import com.xrpn.immutable.n78Entry
import com.xrpn.immutable.nEntry
import com.xrpn.immutable.rEntry
import com.xrpn.immutable.sEntry
import com.xrpn.immutable.slideShareBreadthFirst
import com.xrpn.immutable.slideShareInorder
import com.xrpn.immutable.slideSharePostorder
import com.xrpn.immutable.slideSharePreorder
import com.xrpn.immutable.uEntry
import com.xrpn.immutable.wikiInorder
import com.xrpn.immutable.wikiPostorder
import com.xrpn.immutable.wikiPreorder
import com.xrpn.immutable.zEntry
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstree
import kotlin.random.Random.Default.nextInt

class FBSTreeFilteringTest : FunSpec({

    val repeats = 50

    beforeTest {}

    fun <A, B: Any> isChildMatchOracle(node: FBSTNode<A, B>, match: TKVEntry<A, B>): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
        val leftChildMatch = (node.bLeft is FBSTNode) && node.bLeft.entry == match
        val rightChildMatch = (node.bRight is FBSTNode) && node.bRight.entry == match
        return Pair(leftChildMatch, rightChildMatch)
    }

    test("fcontains") {
        nul<Int, String>().fcontains(zEntry) shouldBe false
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val p = t.fcontains(acc.head)
                    p shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.fcontains(zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.fcontains(TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("fcontainsKey") {
        nul<Int, String>().fcontainsKey(zEntry.getk()) shouldBe false
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val p = t.fcontainsKey(acc.head.getk())
                    p shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.fcontainsKey(zEntry.getk()) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.fcontainsKey(100) shouldBe false
    }

    test("fcontainsValue") {
        nul<Int, String>().fcontainsValue(zEntry.getv()) shouldBe false
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val p = t.fcontainsValue(acc.head.getv())
                    p shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.fcontainsValue(zEntry.getv()) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.fcontainsValue(100) shouldBe false
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

    test("dropItem") {
        nul<Int, Int>().fdropItem(1.toIAEntry()) shouldBe emptyIMBTree()

        tailrec fun <A: Comparable<A>, B: Any> goAll(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = t.fdropItem(acc.head)) {
                        is FBSTNode -> {
                            deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
                        }
                        is FBSTNil -> true shouldBe false
                    }
                    goAll(t, acc.tail, inorder)
                }
            }

        tailrec fun <A: Comparable<A>, B: Any> goTele(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val deleted = t.fdropItem(acc.head)
                    val oracle = inorder.ffilterNot { it == acc.head }
                    when (deleted) {
                        is FBSTNode -> {
                            deleted.inorder() shouldBe oracle
                        }
                        is FBSTNil -> deleted.size shouldBe 0
                    }
                    goTele(deleted, acc.tail, oracle)
                }
            }

        goAll(wikiTree, wikiPreorder, wikiInorder)
        goAll(wikiTree, wikiInorder, wikiInorder)
        goAll(wikiTree, wikiPostorder, wikiInorder)
        goAll(wikiTree, wikiPreorder.freverse(), wikiInorder)
        goAll(wikiTree, wikiInorder.freverse(), wikiInorder)
        goAll(wikiTree, wikiPostorder.freverse(), wikiInorder)
        wikiTree.fdropItem(zEntry) shouldBe wikiTree
        goTele(wikiTree, wikiPreorder, wikiInorder)
        goTele(wikiTree, wikiInorder, wikiInorder)
        goTele(wikiTree, wikiPostorder, wikiInorder)
        goTele(wikiTree, wikiPreorder.freverse(), wikiInorder)
        goTele(wikiTree, wikiInorder.freverse(), wikiInorder)
        goTele(wikiTree, wikiPostorder.freverse(), wikiInorder)

        goAll(slideShareTree, slideSharePreorder, slideShareInorder)
        goAll(slideShareTree, slideShareInorder, slideShareInorder)
        goAll(slideShareTree, slideSharePostorder, slideShareInorder)
        goAll(slideShareTree, slideShareBreadthFirst, slideShareInorder)
        goAll(slideShareTree, slideSharePreorder.freverse(), slideShareInorder)
        goAll(slideShareTree, slideShareInorder.freverse(), slideShareInorder)
        goAll(slideShareTree, slideSharePostorder.freverse(), slideShareInorder)
        goAll(slideShareTree, slideShareBreadthFirst.freverse(), slideShareInorder)
        slideShareTree.fdropItem(TKVEntry.ofIntKey(100)) shouldBe slideShareTree
        goTele(slideShareTree, slideSharePreorder, slideShareInorder)
        goTele(slideShareTree, slideShareInorder, slideShareInorder)
        goTele(slideShareTree, slideSharePostorder, slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst, slideShareInorder)
        goTele(slideShareTree, slideSharePreorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideShareInorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideSharePostorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst.freverse(), slideShareInorder)

        // remove only one
        val aux5a = slideShareTree.finsertDup(slideShareTree.fleftMost()!!, allowDups = true)
        val aux5b = aux5a.finsertDup(slideShareTree.fleftMost()!!, allowDups = true)
        IMBTreeEqual2(aux5b.fdropItem(slideShareTree.fleftMost()!!), aux5a) shouldBe true
    }

    test("ffdropItemAll") {
        nul<Int, Int>().fdropItemAll(1.toIAEntry()) shouldBe FRBTree.emptyIMBTree()
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = t.fdropItemAll(acc.head)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
            .finsertDup(wikiTree.froot()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = wikiTree.finsertDup(wikiTree.fleftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = wikiTree.finsertDup(wikiTree.frightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = slideShareTree.finsert(slideShareTree.fleftMost()!!)
            .finsert(slideShareTree.fleftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = slideShareTree.finsert(slideShareTree.frightMost()!!)
            .finsert(slideShareTree.frightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }

    // TODO dropWhen

    test("fempty") {
        FBSTNil.fempty() shouldBe true
        ofvi(1).fempty() shouldBe false
    }

    test("ffilter, filterNot (nil)") {
        nul<Int, Int>().ffilter { true } shouldBe FRBTree.emptyIMBTree()
        nul<Int, Int>().ffilterNot { false } shouldBe FRBTree.emptyIMBTree()
    }

    test("ffilter, filterNot, ffind (A)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(repeats, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
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
        checkAll(repeats, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
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
        checkAll(repeats, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
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

    test("ffindAny") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    if (t.ffindAny{ it == acc.head }?.let{ itAny -> itAny == acc.head } != true)  fail("not found: ${acc.head}")
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        go(wikiTree, wikiPostorder)
        go(wikiTree, wikiInorder)
        wikiTree.ffindAny{ it == zEntry } shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindAny{ it == TKVEntry.ofIntKey(100) } shouldBe null
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
        go(wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true), wikiPreorder)
        go(
            wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
            .finsertDup(wikiTree.froot()!!, allowDups = true), wikiPreorder
        )
        go(wikiTree.finsertDup(wikiTree.fleftMost()!!, allowDups = true), wikiPreorder)
        go(wikiTree.finsertDup(wikiTree.frightMost()!!, allowDups = true), wikiPreorder)
        go(
            slideShareTree.finsert(slideShareTree.fleftMost()!!)
            .finsert(slideShareTree.fleftMost()!!), slideShareBreadthFirst
        )
        go(
            slideShareTree.finsert(slideShareTree.frightMost()!!)
            .finsert(slideShareTree.frightMost()!!), slideShareBreadthFirst
        )
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
        go(wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true), wikiPreorder)
        go(
            wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
            .finsertDup(wikiTree.froot()!!, allowDups = true), wikiPreorder
        )
        go(wikiTree.finsertDup(wikiTree.fleftMost()!!, allowDups = true), wikiPreorder)
        go(wikiTree.finsertDup(wikiTree.frightMost()!!, allowDups = true), wikiPreorder)
        go(
            slideShareTree.finsert(slideShareTree.fleftMost()!!)
            .finsert(slideShareTree.fleftMost()!!), slideShareBreadthFirst
        )
        go(
            slideShareTree.finsert(slideShareTree.frightMost()!!)
            .finsert(slideShareTree.frightMost()!!), slideShareBreadthFirst
        )
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
        FBSTNil.fleftMost() shouldBe null
    }

    test("fhasDups") {
        Arb.fbstree<Int, Int>(Arb.int(0..200)).checkAll(repeats) { fbst ->
            val ss = fbst.copyToMutableMap().size
            fbst.fhasDups() shouldBe (ss != fbst.size)
        }
    }

    test("fisDup") {
        nul<Int, Int>().fisDup(1.toIAEntry()) shouldBe false
        slideShareTree.fisDup(slideShareTree.fleftMost()!!) shouldBe false
        val aux5a = slideShareTree.finsertDup(slideShareTree.fleftMost()!!, allowDups = true)
        aux5a.fisDup(slideShareTree.fleftMost()!!) shouldBe true
        aux5a.fisDup(slideShareTree.froot()!!) shouldBe false
        val aux5b = aux5a.finsertDup(slideShareTree.fleftMost()!!, allowDups = true)
        aux5b.fisDup(slideShareTree.fleftMost()!!) shouldBe true
        aux5b.fisDup(slideShareTree.froot()!!) shouldBe false
        val aux5c = slideShareTree.finsertDup(slideShareTree.froot()!!, allowDups = true)
        aux5c.fisDup(slideShareTree.fleftMost()!!) shouldBe false
        aux5c.fisDup(slideShareTree.froot()!!) shouldBe true
    }

    test("fparentOf") {
        nul<Int, String>().fparentOf(TKVEntry.ofIntKey("")) shouldBe null
        FBSTNode(mEntry).fparentOf(mEntry) shouldBe FBSTNil

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

        wikiTree.fparentOf(fEntry)  /* parent of root */ shouldBe FBSTNil
        (wikiTree.fparentOf(cEntry) as FBSTNode).entry shouldBe dEntry
        (wikiTree.fparentOf(hEntry) as FBSTNode).entry shouldBe iEntry
        wikiTree.fparentOf(zEntry) /* parent of missing value */ shouldBe null

        (slideShareTree.fparentOf(n32Entry) as FBSTNode).entry shouldBe n17Entry
        (slideShareTree.fparentOf(n50Entry) as FBSTNode).entry shouldBe n78Entry
    }

    test("fpick") {
        FBSTNil.fpeek() shouldBe null
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
        FBSTNil.frightMost() shouldBe null
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