package com.xrpn.immutable.fbstreetest

import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.IMList
import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbsItree
import io.kotest.xrpn.fbstreeWithDups

class FBSTreeTransformingTest : FunSpec({

    val repeats = 10

    beforeTest {}

    test("fflatMap (A)") {
        nul<Int, String>()
            .fflatMap{ depthOneRight } shouldBe nul()
        nul<Int, String>()
            .fflatMap{ tkv -> depthOneRight.finsert(tkv) } shouldBe nul()
        depthOneRight.fflatMap{ nul<Int, String>() } shouldBe nul()
        depthOneRight.fflatMap{ tkv -> nul<Int, String>().finsert(tkv) } shouldBe depthOneRight
        FTreeIterator(nul<Int, String>()).flatMap{  FTreeIterator(depthOneRight) } shouldBe FTreeIterator( nul<Int, String>())
        FTreeIterator(depthOneRight).flatMap{ FTreeIterator( nul<Int, String>() ) } shouldBe FTreeIterator( nul<Int, String>())
        depthOneRight.softEqual(FTreeIterator(depthOneRight).flatMap{ FTreeIterator( nul<Int, String>() ) }.toList()) shouldBe true
        val cheat1 = wikiTree.fflatMap { slideShareTree }
        cheat1 shouldBe slideShareTree
        val cheat2 = slideShareTree.fflatMap { wikiTree }
        cheat2 shouldBe wikiTree
    }

    test("fflatMap (B)") {
        checkAll(repeats, Arb.fbsItree<Int, Int>(Arb.int()), Arb.fbsItree<Int, Int>(Arb.int())) { fbst1, fbst2 ->

            var counter = 0
            // there are no dups; this is a (very inefficient) set-union
            val res: FBSTree<Int, Int> = fbst1.fflatMap { tkv ->
                val fbst: FBSTree<Int, Int> = fbst2.finsert(tkv)
                fbst.fhasDups() shouldBe false
                counter += fbst.size
                fbst
            }
            res.fhasDups() shouldBe false

            (counter <= (fbst1.size * (fbst2.size + 1))) shouldBe true
            (res.fsize() <= (fbst1.size + fbst2.size)) shouldBe true
            // 1 and 2 have no common elements
            if (res.fsize() == (fbst1.size + fbst2.size)) { counter shouldBe (fbst1.size * (fbst2.size+1)) }

            fbst2.fcount(res::fcontains) shouldBe fbst2.size
            res.fcount(fbst2::fcontains) shouldBe fbst2.size
            fbst1.fcount(res::fcontains) shouldBe fbst1.size
            res.fcount(fbst1::fcontains) shouldBe fbst1.size
        }
    }

    test("fflatMap (AA)") {
        nul<Int, String>()
            .fflatMap { depthOneRight } shouldBe nul()
        nul<Int, String>()
            .fflatMap { tkv -> depthOneRight.finsert(tkv) } shouldBe nul()
        depthOneRight.fflatMap{ nul<Int, String>() } shouldBe nul()
        depthOneRight.fflatMap{ tkv -> nul<Int, String>().finsert(tkv) }.equals(depthOneRight) shouldBe true
        FTreeIterator( nul<Int,String>()).flatMap{  FTreeIterator(depthOneRight) } shouldBe FTreeIterator( nul<Int, String>()).iterator()
        FTreeIterator(depthOneRight).flatMap{ FTreeIterator( nul<Int, String>()) } shouldBe FTreeIterator( nul<Int, String>()).iterator()
        FTreeIterator(depthOneRight).flatMap{ tkv -> FTreeIterator( nul<Int, String>().finsert(tkv)) } shouldBe FTreeIterator(depthOneRight)
        FTreeIterator(depthOneRight) shouldBe FTreeIterator(depthOneRight).flatMap{ tkv -> FTreeIterator(nul<Int, String>().finsert(tkv)) }
        depthOneRight.softEqual(FTreeIterator(depthOneRight).flatMap{ tkv -> FTreeIterator(nul<Int, String>().finsert(tkv)) }) shouldBe false
    }

    test("fflatMap (BB)") {
        checkAll(repeats, Arb.fbstreeWithDups<Int, Int>(Arb.int()), Arb.fbstreeWithDups<Int, Int>(Arb.int())) { fbst1, fbst2 ->

            var counter = 0
            val res: FBSTree<Int, Int> = fbst1.fflatMap { tkv ->
                val fbst: FBSTree<Int, Int> = fbst2.finsert(tkv)
                counter += fbst.size
                fbst
            }
            res.fsize() shouldBe counter
            fbst2.fcount(res::fcontains) shouldBe fbst2.size
            (res.fcount(fbst2::fcontains) >= fbst2.size) shouldBe true
            fbst1.fcount(res::fcontains) shouldBe fbst1.size
            (res.fcount(fbst1::fcontains) >= fbst1.size) shouldBe true
        }
    }

//    test("ffold") {
//        // this has already been tested aplenty as a means to get things done all over the place
//    }
//
//    test("ffoldv") {
//        // this has already been tested aplenty as a means to get things done all ove rthe place
//    }

    test("fmap") {
        (nul<Int, Int>().fmap { 2.toIAEntry() } === FBSTUnique.empty) shouldBe true
        Arb.fbsItree(Arb.int((-50..50))).checkAll(repeats) { fbst ->
            val sum = fbst.ffoldv(0) {acc, v -> acc+v }
            val aut = fbst.fmap { tkv -> TKVEntry.ofkk(tkv.getk(), tkv.getv() * 13) }
            aut.fhasDups() shouldBe fbst.fhasDups()
            val sum13 = aut.ffoldv(0) {acc, v -> acc+v }
            (sum * 13) shouldBe sum13
        }
        TODO()
//        fail("revise fmap for fbsTree")
//        nul<Int, Int>().fmap { 2.toIAEntry() } shouldBe FBSTNil
//        Arb.fbstree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
//            val sum = fbst.ffoldv(0) {acc, v -> acc+v }
//            val aut = fbst.fmap { tkv -> TKVEntry.ofkk(tkv.getk(), tkv.getv() * 13) }
//            val sum13 = aut.ffoldv(0) {acc, v -> acc+v }
//            (sum * 13) shouldBe sum13
//        }
    }

    test("fmapDup") {
        (nul<Int, Int>(true).fmap { 2.toIAEntry() } === FBSTGeneric.empty) shouldBe true
        Arb.fbstreeWithDups(Arb.int((-50..50))).checkAll(repeats) { fbst ->
            val sum = fbst.ffoldv(0) {acc, v -> acc+v }
            val aut = fbst.fmap { tkv -> TKVEntry.ofkk(tkv.getk(), tkv.getv() * 13) }
            aut.fhasDups() shouldBe fbst.fhasDups()
            val sum13 = aut.ffoldv(0) {acc, v -> acc+v }
            (sum * 13) shouldBe sum13
        }
    }

    test("fmapToList") {
        (nul<Int, Int>().fmapToList { 2.toIAEntry() } === FLNil) shouldBe true
        (nul<Int, Int>(true).fmapToList { 2.toIAEntry() } === FLNil) shouldBe true
        val f: (tkv: TKVEntry<Int, Int>) -> TKVEntry<Int, Int> = { tkv -> TKVEntry.ofkk(tkv.getk(), tkv.getv() * 13) }
        Arb.fbstreeWithDups<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<TKVEntry<Int, Int>> = fbst.fmapToList(f)
            aut shouldBe fbst.preorder(reverse = true).fmap(f)
        }
        Arb.fbsItree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<TKVEntry<Int, Int>> = fbst.fmapToList(f)
            aut shouldBe fbst.preorder(reverse = true).fmap(f)
        }
    }

    test("fmapvToList") {
        (nul<Int, Int>().fmapvToList { 2 } === FLNil) shouldBe true
        (nul<Int, Int>(true).fmapvToList { 2 } === FLNil) shouldBe true
        val f: (v: Int) -> Int = { v -> v*13 }
        Arb.fbstreeWithDups(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<Int> = fbst.fmapvToList(f)
            aut shouldBe fbst.preorderValues(reverse = true).fmap(f)
        }
        Arb.fbsItree(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<Int> = fbst.fmapvToList(f)
            aut shouldBe fbst.preorderValues(reverse = true).fmap(f)
        }
    }

    test("freduce") {
        nul<Int, Int>().freduce { _, _ -> 2.toIAEntry() } shouldBe null
        nul<Int, Int>(true).freduce { _, _ -> 2.toIAEntry() } shouldBe null
        val f: (tkv1: TKVEntry<Int, Int>, tkv2: TKVEntry<Int, Int>) -> TKVEntry<Int, Int> = { tkv1, tkv2 -> (tkv1.getv()+tkv2.getv()).toIAEntry() }
        Arb.fbstreeWithDups(Arb.int()).checkAll(repeats) { fbst ->
            val aut: TKVEntry<Int, Int>? = fbst.freduce(f)
            aut shouldBe fbst.preorder().ffoldLeft(0.toIAEntry(), f)
        }
        Arb.fbsItree(Arb.int()).checkAll(repeats) { fbst ->
            val aut: TKVEntry<Int, Int>? = fbst.freduce(f)
            aut shouldBe fbst.preorder().ffoldLeft(0.toIAEntry(), f)
        }
    }

})
