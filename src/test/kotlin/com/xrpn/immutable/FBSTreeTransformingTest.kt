package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FBSTreeTransformingTest : FunSpec({

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
    }

    test("ffold") {
        // this has already been tested aplenty as a means to get things done
    }

    test("ffoldv") {
        // this has already been tested aplenty as a means to get things done
    }

    test("fmap") {
    }

    test("map") {
        FBSTNil.mapi { 2 } shouldBe FBSTNil

        depthOneRight.mapi { s -> "z$s" }.inorder() shouldBe depthOneRight.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        //depthOneRight.maps { s -> "z$s" }.inorder() shouldBe depthOneRight.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
        depthOneLeft.mapi { s -> "z$s" }.inorder() shouldBe depthOneLeft.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        //depthOneLeft.maps { s -> "z$s" }.inorder() shouldBe depthOneLeft.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
        depthOneFull.mapi { s -> "z$s" }.inorder() shouldBe depthOneFull.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        //depthOneFull.maps { s -> "z$s" }.inorder() shouldBe depthOneFull.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
        wikiTree.mapi { s -> "z$s" }.inorder() shouldBe wikiTree.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        //wikiTree.maps { s -> "z$s" }.inorder() shouldBe wikiTree.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
        slideShareTree.mapi { s -> "z$s" }.inorder() shouldBe slideShareTree.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        //slideShareTree.maps { s -> "z$s" }.inorder() shouldBe slideShareTree.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
    }

    test("fmapToList") {
    }

    test("fmapvToList") {
    }

    test("freduce") {
    }

})
