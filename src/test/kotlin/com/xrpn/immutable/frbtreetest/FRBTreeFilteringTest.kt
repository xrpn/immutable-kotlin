package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IMBTreeUtility
import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.of
import com.xrpn.immutable.FRBTree.Companion.ofvi
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.bEntry
import com.xrpn.immutable.cEntry
import com.xrpn.immutable.dEntry
import com.xrpn.immutable.eEntry
import com.xrpn.immutable.frbSlideShareBreadthFirst
import com.xrpn.immutable.frbSlideShareInorder
import com.xrpn.immutable.frbSlideSharePostorder
import com.xrpn.immutable.frbSlideSharePreorder
import com.xrpn.immutable.frbSlideShareTree
import com.xrpn.immutable.frbWikiBreadthFirst
import com.xrpn.immutable.frbWikiInorder
import com.xrpn.immutable.frbWikiPostorder
import com.xrpn.immutable.frbWikiPreorder
import com.xrpn.immutable.frbWikiTree
import com.xrpn.immutable.hEntry
import com.xrpn.immutable.lEntry
import com.xrpn.immutable.mEntry
import com.xrpn.immutable.n32Entry
import com.xrpn.immutable.n48Entry
import com.xrpn.immutable.n50Entry
import com.xrpn.immutable.n62Entry
import com.xrpn.immutable.nEntry
import com.xrpn.immutable.rEntry
import com.xrpn.immutable.rbSlideShareTree
import com.xrpn.immutable.rbWikiTree
import com.xrpn.immutable.sEntry
import com.xrpn.immutable.uEntry
import com.xrpn.immutable.zEntry
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FRBTreeFilteringTest : FunSpec({

    val repeatsHigh = Pair(50, 100)
    val repeatsMid = Pair(25, 500)

    beforeTest {}

    test("fcontains") {
        nul<Int, String>().fcontains(zEntry) shouldBe false
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    t.fcontains(acc.head) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        frbWikiTree.fcontains(zEntry) shouldBe false
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.fcontains(TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("dropAll (nil)") {
        FBSTree.nul<Int, Int>().fdropAll(FList.emptyIMList<TKVEntry<Int, Int>>()) shouldBe FRBTree.emptyIMBTree()
        FBSTree.nul<Int, Int>().fdropAll(FLCons(1.toIAEntry(), FLNil)) shouldBe FRBTree.emptyIMBTree()
    }

    test("fdropAll") {
        ofvi(1, 2, 3).fdropAll(FList.emptyIMList()) shouldBe ofvi(1, 2, 3)
        ofvi(1, 2, 3).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe ofvi(3)
        ofvi(1, 2, 3, 4).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe ofvi(3, 4)
        ofvi(1, 2, 3).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(1)
        ofvi(1, 2, 3, 4).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(1, 4)
        ofvi(1, 2, 3).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(2)
        ofvi(1, 2, 3, 4).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe ofvi(2, 4)
    }

    test("dropAlt (nil)") {
        FBSTree.nul<Int, Int>().fdropAlt(emptyIMBTree<Int, Int>()) shouldBe FBSTree.emptyIMBTree()
        FBSTree.nul<Int, Int>().fdropAlt(of(1.toIAEntry())) shouldBe FBSTree.emptyIMBTree()
    }

    test("dropAlt") {
        FBSTree.ofvi(1, 2, 3).fdropAlt(FBSTree.emptyIMBTree<Int, Int>()) shouldBe FBSTree.ofvi(1, 2, 3)
        FBSTree.ofvi(1, 2, 3).fdropAlt(FBSTree.of(1.toIAEntry(), 2.toIAEntry())) shouldBe FBSTree.ofvi(3)
        FBSTree.ofvi(1, 2, 3, 4).fdropAlt(FBSTree.of(1.toIAEntry(), 2.toIAEntry())) shouldBe FBSTree.ofvi(3, 4)
        FBSTree.ofvi(1, 2, 3).fdropAlt(FBSTree.of(2.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(1)
        FBSTree.ofvi(1, 2, 3, 4).fdropAlt(FBSTree.of(2.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(1, 4)
        FBSTree.ofvi(1, 2, 3).fdropAlt(FBSTree.of(1.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(2)
        FBSTree.ofvi(1, 2, 3, 4).fdropAlt(FBSTree.of(1.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(2, 4)
    }

    test("fdropItem") {
        nul<Int, Int>().fdropItem(1.toIAEntry()) shouldBe FRBTree.emptyIMBTree()

        tailrec fun goAllWiki(frb: FRBTree<Int, String>, acc: FList<TKVEntry<Int, String>>, inorder: FList<TKVEntry<Int, String>>): FList<TKVEntry<Int, String>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val rbDeleted: RBTree<Int, String> = rbWikiTree.copy()
                    rbDeleted.rbDelete(TKVEntry.ofkv(acc.head.getk(), acc.head.getv()))
                    when (val deleted = frb.fdropItem(acc.head)) {
                        is FRBTNode -> {
                            FRBTree.rbRootSane(deleted) shouldBe true
                            val aut1in = deleted.inorder()
                            val oracle = inorder.ffilterNot { it == acc.head }
                            aut1in shouldBe oracle
                            IMBTreeUtility.strongEqual(deleted, rbDeleted) shouldBe true
                        }
                        is FRBTNil -> {
                            true shouldBe false
                        }
                    }
                    goAllWiki(frb, acc.tail, inorder)
                }
            }

        // tailrec fun <A: Comparable<A>, B: Any> goAllSS(frb: FRBTree<A,B>, acc: FList<TKVEntry<A,B>>, inorder: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
        tailrec fun goAllSS(frb: FRBTree<Int, Int>, acc: FList<TKVEntry<Int, Int>>, inorder: FList<TKVEntry<Int, Int>>): FList<TKVEntry<Int, Int>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val rbDeleted: RBTree<Int, Int> = rbSlideShareTree.copy()
                    rbDeleted.rbDelete(TKVEntry.ofkk(acc.head.getk(), acc.head.getv()))
                    when (val deleted = frb.fdropItem(acc.head)) {
                        is FRBTNode -> {
                            FRBTree.rbRootSane(deleted) shouldBe true
                            val aut1in = deleted.inorder()
                            val oracle = inorder.ffilterNot { it == acc.head }
                            aut1in shouldBe oracle
                            IMBTreeUtility.strongEqual(deleted, rbDeleted) shouldBe true
                        }
                        is FRBTNil -> {
                            true shouldBe false
                        }
                    }
                    goAllSS(frb, acc.tail, inorder)
                }
            }

        tailrec fun <A: Comparable<A>, B: Any> goTele(t: FRBTree<A, B>, rbDeleted: RBTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    rbDeleted.rbDelete(acc.head)
                    val deleted = t.fdropItem(acc.head)
                    val oracle = inorder.ffilterNot { it == acc.head }
                    when (deleted) {
                        is FRBTNode -> {
                            FRBTree.rbRootSane(deleted) shouldBe true
                            val aut1in = deleted.inorder()
                            aut1in shouldBe oracle
                            IMBTreeUtility.strongEqual(deleted, rbDeleted) shouldBe true
                        }
                        is FRBTNil -> {
                            deleted.size shouldBe 0
                            rbDeleted.size() shouldBe 0
                        }
                    }
                    goTele(deleted, rbDeleted, acc.tail, oracle)
                }
            }
        goAllWiki(frbWikiTree, frbWikiPreorder, frbWikiInorder)
        goAllWiki(frbWikiTree, frbWikiPostorder, frbWikiInorder)
        goAllWiki(frbWikiTree, frbWikiInorder, frbWikiInorder)
        goAllWiki(frbWikiTree, frbWikiBreadthFirst, frbWikiInorder)
        goAllWiki(frbWikiTree, frbWikiPreorder.freverse(), frbWikiInorder)
        goAllWiki(frbWikiTree, frbWikiPostorder.freverse(), frbWikiInorder)
        goAllWiki(frbWikiTree, frbWikiInorder.freverse(), frbWikiInorder)
        goAllWiki(frbWikiTree, frbWikiBreadthFirst.freverse(), frbWikiInorder)
        var rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiPreorder, frbWikiInorder)
        rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiPostorder, frbWikiInorder)
        rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiInorder, frbWikiInorder)
        rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiBreadthFirst, frbWikiInorder)
        rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiPreorder.freverse(), frbWikiInorder)
        rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiPostorder.freverse(), frbWikiInorder)
        rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiInorder.freverse(), frbWikiInorder)
        rbMutable = rbWikiTree.copy()
        goTele(frbWikiTree, rbMutable, frbWikiBreadthFirst.freverse(), frbWikiInorder)

        frbWikiTree.fdropItem(zEntry) shouldBe frbWikiTree

        goAllSS(frbSlideShareTree, frbSlideSharePreorder, frbSlideShareInorder)
        goAllSS(frbSlideShareTree, frbSlideSharePostorder, frbSlideShareInorder)
        goAllSS(frbSlideShareTree, frbSlideShareInorder, frbSlideShareInorder)
        goAllSS(frbSlideShareTree, frbSlideShareBreadthFirst, frbSlideShareInorder)
        goAllSS(frbSlideShareTree, frbSlideSharePreorder.freverse(), frbSlideShareInorder)
        goAllSS(frbSlideShareTree, frbSlideSharePostorder.freverse(), frbSlideShareInorder)
        goAllSS(frbSlideShareTree, frbSlideShareInorder.freverse(), frbSlideShareInorder)
        goAllSS(frbSlideShareTree, frbSlideShareBreadthFirst.freverse(), frbSlideShareInorder)
        var rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePreorder, frbSlideShareInorder)
        rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePostorder, frbSlideShareInorder)
        rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideShareInorder, frbSlideShareInorder)
        rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideShareBreadthFirst, frbSlideShareInorder)
        rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePreorder.freverse(), frbSlideShareInorder)
        rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePostorder.freverse(), frbSlideShareInorder)
        rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideShareInorder.freverse(), frbSlideShareInorder)
        rbMutableSs = rbSlideShareTree.copy()
        goTele(frbSlideShareTree, rbMutableSs, frbSlideShareBreadthFirst.freverse(), frbSlideShareInorder)

        frbSlideShareTree.fdropItem(TKVEntry.ofIntKey(100)) shouldBe frbSlideShareTree
    }

    test("fdropItem (property), sorted asc") {
        checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val ix1 = nextInt(0, n)
            val frbTree = of(values.iterator())
            val aut = frbTree.fdropItem(TKVEntry.ofIntKey(ix1))
            aut.size shouldBe n - 1
            FRBTree.rbRootSane(aut) shouldBe true
            val testOracle = FList.of(values.iterator())
                .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
            aut.inorder() shouldBe testOracle
        }
    }

    test("fdropItem (property), sorted desc") {
        checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            reversed.reverse()
            val ix1 = nextInt(0, n)
            val frbTree = of(values.iterator())
            val aut = frbTree.fdropItem(TKVEntry.ofIntKey(ix1))
            aut.size shouldBe n - 1
            FRBTree.rbRootSane(aut) shouldBe true
            val testOracle = FList.of(values.iterator())
                .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
            aut.inorder() shouldBe testOracle
        }
    }

    test("fdropItem (property), shuffled") {
        checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            shuffled.shuffle()
            val randoms = IntArray(n/10) { i: Int -> i }
            randoms.shuffle()
            val ix1 = randoms[0]
            val ix2 = randoms[1]
            val ix3 = randoms[2]
            val frbTree = of(shuffled.iterator())
            val aux0 = frbTree.fdropItem(TKVEntry.ofIntKey(ix1))
            val aux1 = aux0.fdropItem(TKVEntry.ofIntKey(ix2))
            val aut = aux1.fdropItem(TKVEntry.ofIntKey(ix3))
            aut.size shouldBe n - 3
            FRBTree.rbRootSane(aut) shouldBe true
            val testOracle = FList.of(values.iterator())
                .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
                .ffilterNot { it == TKVEntry.ofIntKey(ix2) }
                .ffilterNot { it == TKVEntry.ofIntKey(ix3) }
            aut.inorder() shouldBe testOracle
        }
    }

    test("ffdropItemAll") {
        nul<Int, Int>().fdropItemAll(1.toIAEntry()) shouldBe emptyIMBTree()
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = t.fdropItemAll(acc.head)) {
                        is FRBTNode -> deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
                        is FRBTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = frbWikiTree.finsert(frbWikiTree.froot()!!)
        go(aux1, frbWikiPreorder, aux1.inorder())
        val aux2 = frbWikiTree.finsert(frbWikiTree.froot()!!)
            .finsert(frbWikiTree.froot()!!)
        go(aux2, frbWikiPreorder, aux2.inorder())
        val aux3 = frbWikiTree.finsert(frbWikiTree.fleftMost()!!)
        go(aux3, frbWikiPreorder, aux3.inorder())
        val aux4 = frbWikiTree.finsert(frbWikiTree.frightMost()!!)
        go(aux4, frbWikiPreorder, aux4.inorder())
        val aux5 = frbSlideShareTree.finsert(frbSlideShareTree.fleftMost()!!)
            .finsert(frbSlideShareTree.fleftMost()!!)
        go(aux5, frbSlideShareBreadthFirst, aux5.inorder())
        val aux6 = frbSlideShareTree.finsert(frbSlideShareTree.frightMost()!!)
            .finsert(frbSlideShareTree.frightMost()!!)
        go(aux6, frbSlideShareBreadthFirst, aux6.inorder())
    }

    test ("ffind (A)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FRBTree<Int, Int> = of(svalues.iterator())
            tree.size shouldBe ora1

            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            saAll1.size shouldBe ora1
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            saAll2.size shouldBe ora2
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe theRestSansOra2
        }
    }

    test ("ffind (B)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FRBTree<Int, Int> = of(svalues.iterator())
            tree.size shouldBe ora1

            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            saAll2.size shouldBe ora2
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe theRestSansOra2
        }
    }

    test ("ffind (C)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FRBTree<Int, Int> = of(svalues.iterator())
            tree.size shouldBe ora1

            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            saAll1.size shouldBe ora1
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            saAll2.size shouldBe ora2
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe theRestSansOra2
        }
    }

    test("ffind, ffindDistinct (nil)") {
        FBSTree.nul<Int, Int>().ffind { false } shouldBe FList.emptyIMList()
        FBSTree.nul<Int, Int>().ffindDistinct { true } shouldBe null
    }

    test("ffind") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    if (t.ffind{ it == acc.head }.fsize() != 1)  fail("not found: ${acc.head}")
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        frbWikiTree.ffindItem(zEntry) shouldBe null
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.ffindItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindAny") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    if (t.ffindAny{ it == acc.head }?.let{ itAny -> itAny == acc.head } != true)  fail("not found: ${acc.head}")
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        go(frbWikiTree, frbWikiPostorder)
        go(frbWikiTree, frbWikiInorder)
        frbWikiTree.ffindAny{ it == zEntry } shouldBe null
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.ffindAny{ it == TKVEntry.ofIntKey(100) } shouldBe null
    }

    test("ffind, ffindDistinct (A)") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree1: FRBTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)
            tree1.ffind { false } shouldBe FList.emptyIMList()

            val ora2 = ora1 / 2
            tree1.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffind, ffindDistinct (B)") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree1: FRBTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)
            tree1.ffind { false } shouldBe FList.emptyIMList()

            val ora2 = ora1 / 2
            tree1.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffind, ffindDistinct (C)") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree1: FRBTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)
            tree1.ffind { false } shouldBe FList.emptyIMList()

            val ora2 = ora1 / 2
            tree1.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffindItem") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindItem(acc.head)) {
                        is FRBTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        frbWikiTree.ffindItem(zEntry) shouldBe null
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.ffindItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindKey") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindKey(acc.head.getk())) {
                        is FRBTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        frbWikiTree.ffindKey(zEntry.getk()) shouldBe null
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.ffindKey(100) shouldBe null
    }

    test("ffindLastItem") {
        nul<Int, Int>().ffindLastItem(1.toIAEntry()) shouldBe null
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastItem(acc.head)) {
                        is FRBTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    t.fisDup(acc.head) shouldBe false
                    t.fhasDups() shouldBe false
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        frbWikiTree.ffindItem(zEntry) shouldBe null
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.ffindItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindLastKey") {
        nul<Int, Int>().ffindLastKey(1) shouldBe null
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastKey(acc.head.getk())) {
                        is FRBTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        frbWikiTree.ffindItem(zEntry) shouldBe null
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.ffindItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindValueOfKey (A)") {
        FBSTree.nul<Int, Int>().ffindValueOfKey(1) shouldBe null
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindValueOfKey(acc.head.getk())) {
                        null -> fail("not found: ${acc.head}")
                        else -> found shouldBe acc.head.getv()
                    }
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        frbWikiTree.ffindKey(zEntry.getk()) shouldBe null
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        frbSlideShareTree.ffindKey(100) shouldBe null
    }

    test("ffindValueOfKey (B)") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
            val ora1 = values.size
            val ixs = ((ora1/5)..(ora1/3))
            val tree1: FRBTree<Int, Int> = of(values.iterator())
            tree1.size shouldBe ora1
            for (ix in ixs) {
                tree1.ffindValueOfKey(ix) shouldBe ix
            }
            tree1.ffindValueOfKey(ora1+2) shouldBe null
        }
    }

    test("fleftMost") {
        FRBTNil.fleftMost() shouldBe null
    }

    test("fhasDups") {
        nul<Int, Int>().fhasDups() shouldBe false
        ofvi(1,1,2).fhasDups() shouldBe false
    }

    test("fisDup") {
        nul<Int, Int>().fisDup(1.toIAEntry()) shouldBe false
        ofvi(1,1,2).fisDup(1.toIAEntry()) shouldBe false
    }

    test("fparentOf") {
        FRBTree.nul<Int,String>().fparentOf(TKVEntry.ofIntKey("")) shouldBe null
        FRBTNode.of(mEntry).fparentOf( mEntry) shouldBe FRBTNil

        frbDepthOneLeft.fparentOf( lEntry) shouldBe frbDepthOneLeft
        ttDepthOneRight.fparentOf( nEntry) shouldBe ttDepthOneRight
        frbDepthOneFull.fparentOf( lEntry) shouldBe frbDepthOneFull
        frbDepthOneFull.fparentOf( nEntry) shouldBe frbDepthOneFull

        (ttDepthTwoLeftRight.fparentOf( mEntry) as FRBTNode).entry shouldBe lEntry
        (ttDepthTwoLeftRight.fparentOf( lEntry) as FRBTNode).entry shouldBe nEntry
        (ttDepthTwoLeftRight.fparentOf( sEntry) as FRBTNode).entry shouldBe nEntry
        (frbDepthTwoLeftLeft.fparentOf( eEntry) as FRBTNode).entry shouldBe lEntry
        (frbDepthTwoLeftLeft.fparentOf( lEntry) as FRBTNode).entry shouldBe nEntry
        (frbDepthTwoLeftLeft.fparentOf( sEntry) as FRBTNode).entry shouldBe nEntry
        (frbDepthTwoRightRight.fparentOf( uEntry) as FRBTNode).entry shouldBe sEntry
        (frbDepthTwoRightRight.fparentOf( sEntry) as FRBTNode).entry shouldBe nEntry
        (frbDepthTwoRightRight.fparentOf( mEntry) as FRBTNode).entry shouldBe nEntry
        (frbDepthTwoRightLeft.fparentOf( rEntry) as FRBTNode).entry shouldBe sEntry
        (frbDepthTwoRightLeft.fparentOf( sEntry) as FRBTNode).entry shouldBe nEntry
        (frbDepthTwoRightLeft.fparentOf( mEntry) as FRBTNode).entry shouldBe nEntry

        frbWikiTree.fparentOf( dEntry)  /* parent of root */ shouldBe FRBTNil
        (frbWikiTree.fparentOf( cEntry) as FRBTNode).entry shouldBe bEntry
        (frbWikiTree.fparentOf( hEntry) as FRBTNode).entry shouldBe dEntry
        frbWikiTree.fparentOf( zEntry) /* parent of missing value */ shouldBe null

        (frbSlideShareTree.fparentOf( n32Entry) as FRBTNode).entry shouldBe n48Entry
        (frbSlideShareTree.fparentOf( n50Entry) as FRBTNode).entry shouldBe n62Entry
    }

    test("fpeek") {
        FRBTNil.fpeek() shouldBe null
    }

    test("fpeek int") {
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

    test("fleftMost frightMost int") {
        for (size in IntRange(1, 20)) {
            val ary = IntArray(size) {nextInt()}
            val max = ary.maxOrNull()!!
            val min = ary.minOrNull()!!
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).fleftMost() shouldBe TKVEntry.ofIntKey(min)
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).frightMost() shouldBe TKVEntry.ofIntKey(max)
        }
    }

    test("frightMost") {
        FRBTNil.frightMost() shouldBe null
    }

    test("froot") {
        FRBTNil.froot() shouldBe null
        val ary = IntArray(1) { nextInt() }
        of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).froot() shouldBe TKVEntry.ofIntKey(ary[0])
        val itemAry = Array(1) { TKVEntry.ofIntKey(nextInt()) }
        of(itemAry.iterator()).froot() shouldBe itemAry[0]
    }
})