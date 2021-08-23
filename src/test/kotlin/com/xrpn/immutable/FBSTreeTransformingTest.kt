package com.xrpn.immutable

import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.IMList
import com.xrpn.immutable.FBSTree.Companion.toIMBTree
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstree
import io.kotest.xrpn.fbstreeAllowDups

class FBSTreeTransformingTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("fflatMap (A)") {
        FBSTree.nul<Int,String>().fflatMap{ depthOneRight } shouldBe FBSTree.nul()
        FBSTree.nul<Int,String>().fflatMap{ tkv -> depthOneRight.finsert(tkv) } shouldBe FBSTree.nul()
        depthOneRight.fflatMap{ FBSTree.nul<Int,String>() } shouldBe FBSTree.nul()
        depthOneRight.fflatMap{ tkv -> FBSTree.nul<Int,String>().finsert(tkv) } shouldBe depthOneRight
        FBSTree.nul<Int,String>().flatMap{ depthOneRight } shouldBe FBSTree.nul()
        depthOneRight.flatMap{ FBSTree.nul<Int,String>() } shouldBe FBSTree.nul()
        depthOneRight.flatMap{ tkv -> FBSTree.nul<Int,String>().finsert(tkv) } shouldBe depthOneRight
        val cheat1 = wikiTree.fflatMap { slideShareTree }
        cheat1 shouldBe slideShareTree
        val cheat2 = slideShareTree.fflatMap { wikiTree }
        cheat2 shouldBe wikiTree
    }

    test("fflatMap (B)") {
        checkAll(repeats, Arb.fbstree<Int, Int>(Arb.int()), Arb.fbstree<Int, Int>(Arb.int())) { fbst1, fbst2 ->

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

    test("fflatMapDup (A)") {
        FBSTree.nul<Int,String>().fflatMapDup(true) { depthOneRight } shouldBe FBSTree.nul()
        FBSTree.nul<Int,String>().fflatMapDup(true) { tkv -> depthOneRight.finsert(tkv) } shouldBe FBSTree.nul()
        depthOneRight.fflatMapDup(true) { FBSTree.nul<Int,String>() } shouldBe FBSTree.nul()
        depthOneRight.fflatMapDup(true) { tkv -> FBSTree.nul<Int,String>().finsert(tkv) } shouldBe depthOneRight
        FBSTree.nul<Int,String>().flatMap{ depthOneRight } shouldBe FBSTree.nul()
        depthOneRight.flatMap{ FBSTree.nul<Int,String>() } shouldBe FBSTree.nul()
        depthOneRight.flatMap{ tkv -> FBSTree.nul<Int,String>().finsert(tkv) } shouldBe depthOneRight
    }

    test("fflatMapDup (B)") {
        checkAll(repeats, Arb.fbstreeAllowDups<Int, Int>(Arb.int()), Arb.fbstreeAllowDups<Int, Int>(Arb.int())) { fbst1, fbst2 ->

            var counter = 0
            val res: FBSTree<Int, Int> = fbst1.fflatMapDup(true) { tkv ->
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
        FBSTree.nul<Int, Int>().fmap { 2.toIAEntry() } shouldBe FBSTNil
        Arb.fbstree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val sum = fbst.ffoldv(0) {acc, v -> acc+v }
            val aut = fbst.fmap { tkv -> TKVEntry.of(tkv.getk(), tkv.getv()*13) }
            val sum13 = aut.ffoldv(0) {acc, v -> acc+v }
            (sum * 13) shouldBe sum13
        }
    }

    test("fmapToList") {
        FBSTree.nul<Int, Int>().fmapToList { 2.toIAEntry() } shouldBe FBSTNil
        val f: (tkv: TKVEntry<Int,Int>) -> TKVEntry<Int,Int> = { tkv -> TKVEntry.of(tkv.getk(), tkv.getv()*13) }
        Arb.fbstreeAllowDups<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<TKVEntry<Int, Int>> = fbst.fmapToList(f)
            aut shouldBe fbst.preorder(reverse = true).fmap(f)
        }
    }

    test("fmapvToList") {
        FBSTree.nul<Int, Int>().fmapvToList { 2 } shouldBe FBSTNil
        val f: (v: Int) -> Int = { v -> v*13 }
        Arb.fbstreeAllowDups<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<Int> = fbst.fmapvToList(f)
            aut shouldBe fbst.preorderValues(reverse = true).fmap(f)
        }
    }

    test("freduce") {
        FBSTree.nul<Int, Int>().freduce { _, _ -> 2.toIAEntry() } shouldBe null
        val f: (tkv1: TKVEntry<Int,Int>, tkv2: TKVEntry<Int,Int>) -> TKVEntry<Int,Int> = { tkv1, tkv2 -> (tkv1.getv()+tkv2.getv()).toIAEntry() }
        Arb.fbstreeAllowDups<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: TKVEntry<Int, Int>? = fbst.freduce(f)
            aut shouldBe fbst.preorder().ffoldLeft(0.toIAEntry(), f)
        }
    }

})
