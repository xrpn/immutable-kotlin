package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IMList
import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree

class FRBTreeTransformingTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("fflatMap (A)") {
        nul<Int,String>().fflatMap{ ttDepthOneRight } shouldBe nul()
        nul<Int,String>().fflatMap{ tkv -> ttDepthOneRight.finsert(tkv) } shouldBe nul()
        ttDepthOneRight.fflatMap{ nul<Int,String>() } shouldBe nul()
        ttDepthOneRight.fflatMap{ tkv -> nul<Int,String>().finsert(tkv) } shouldBe ttDepthOneRight
        nul<Int,String>().flatMap{ ttDepthOneRight } shouldBe nul()
        ttDepthOneRight.flatMap{ nul<Int,String>() } shouldBe nul()
        ttDepthOneRight.flatMap{ tkv -> nul<Int,String>().finsert(tkv) } shouldBe ttDepthOneRight
        val cheat1 = wikiTree.fflatMap { slideShareTree }
        cheat1 shouldBe slideShareTree
        val cheat2 = slideShareTree.fflatMap { wikiTree }
        cheat2 shouldBe wikiTree
    }

    test("fflatMap (B)") {
        checkAll(repeats, Arb.frbtree<Int, Int>(Arb.int()), Arb.frbtree<Int, Int>(Arb.int())) { fbst1, fbst2 ->

            var counter = 0
            // there are no dups; this is a (very inefficient) set-union
            val res: FRBTree<Int, Int> = fbst1.fflatMap { tkv ->
                val fbst: FRBTree<Int, Int> = fbst2.finsert(tkv)
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

//    test("ffold") {
//        // this has already been tested aplenty as a means to get things done all over the place
//    }
//
//    test("ffoldv") {
//        // this has already been tested aplenty as a means to get things done all ove rthe place
//    }

    test("fmap") {
        nul<Int, Int>().fmap { 2.toIAEntry() } shouldBe FBSTNil
        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val sum = fbst.ffoldv(0) {acc, v -> acc+v }
            val aut = fbst.fmap { tkv -> TKVEntry.ofkk(tkv.getk(), tkv.getv() * 13) }
            val sum13 = aut.ffoldv(0) {acc, v -> acc+v }
            (sum * 13) shouldBe sum13
        }
    }

    test("fmapToList") {
        nul<Int, Int>().fmapToList { 2.toIAEntry() } shouldBe FBSTNil
        val f: (tkv: TKVEntry<Int, Int>) -> TKVEntry<Int, Int> = { tkv -> TKVEntry.ofkk(tkv.getk(), tkv.getv() * 13) }
        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<TKVEntry<Int, Int>> = fbst.fmapToList(f)
            aut shouldBe fbst.preorder(reverse = true).fmap(f)
        }
    }

    test("fmapvToList") {
        nul<Int, Int>().fmapvToList { 2 } shouldBe FBSTNil
        val f: (v: Int) -> Int = { v -> v*13 }
        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: IMList<Int> = fbst.fmapvToList(f)
            aut shouldBe fbst.preorderValues(reverse = true).fmap(f)
        }
    }

    test("freduce") {
        nul<Int, Int>().freduce { _, _ -> 2.toIAEntry() } shouldBe null
        val f: (tkv1: TKVEntry<Int, Int>, tkv2: TKVEntry<Int, Int>) -> TKVEntry<Int, Int> = { tkv1, tkv2 -> (tkv1.getv()+tkv2.getv()).toIAEntry() }
        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val aut: TKVEntry<Int, Int>? = fbst.freduce(f)
            aut shouldBe fbst.preorder().ffoldLeft(0.toIAEntry(), f)
        }
    }

})
