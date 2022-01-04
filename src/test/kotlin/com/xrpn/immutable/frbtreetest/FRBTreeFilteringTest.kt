package com.xrpn.immutable.frbtreetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.of
import com.xrpn.immutable.FRBTree.Companion.ofvi
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FRBTreeFilteringTest : FunSpec({

    val repeatsHigh = Pair(50, 100)

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
        val aux1 = frbWikiTree.finsertTkv(frbWikiTree.froot()!!)
        go(aux1, frbWikiPreorder, aux1.inorder())
        val aux2 = frbWikiTree.finsertTkv(frbWikiTree.froot()!!)
            .finsertTkv(frbWikiTree.froot()!!)
        go(aux2, frbWikiPreorder, aux2.inorder())
        val aux3 = frbWikiTree.finsertTkv(frbWikiTree.fleftMost()!!)
        go(aux3, frbWikiPreorder, aux3.inorder())
        val aux4 = frbWikiTree.finsertTkv(frbWikiTree.frightMost()!!)
        go(aux4, frbWikiPreorder, aux4.inorder())
        val aux5 = frbSlideShareTree.finsertTkv(frbSlideShareTree.fleftMost()!!)
            .finsertTkv(frbSlideShareTree.fleftMost()!!)
        go(aux5, frbSlideShareBreadthFirst, aux5.inorder())
        val aux6 = frbSlideShareTree.finsertTkv(frbSlideShareTree.frightMost()!!)
            .finsertTkv(frbSlideShareTree.frightMost()!!)
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

    test("fAND") {
        TODO()
    }

    test("fNOT") {
        TODO()
    }

    test("fOR") {
        TODO()
    }

    test("fXOR") {
        TODO()
    }

})