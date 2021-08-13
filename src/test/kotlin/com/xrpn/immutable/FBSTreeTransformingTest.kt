package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FBSTreeTransformingTest : FunSpec({

    beforeTest {}

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

})
