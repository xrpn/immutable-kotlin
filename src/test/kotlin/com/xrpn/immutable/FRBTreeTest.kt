package com.xrpn.immutable

import com.xrpn.imapi.IMBTreeUtility
import com.xrpn.immutable.FRBTree.Companion.BLACK
import com.xrpn.immutable.FRBTree.Companion.RED
import com.xrpn.immutable.FRBTree.Companion.rbtDelete
import com.xrpn.immutable.FRBTree.Companion.rbtFind
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.of
import com.xrpn.immutable.FRBTree.Companion.rbtParent
import com.xrpn.immutable.FRBTree.Companion.rbRootSane
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FRBTreeTest : FunSpec({

    fun <A, B: Any> rbtContains2(treeStub: FRBTree<A, B>, item: TKVEntry<A, B>): Boolean where A: Any, A: Comparable<A> =
        rbtFind(treeStub, item) != null

    beforeTest {
        rbWikiTree = RBTree.of(frbWikiInorder)
        rbSlideShareTree = RBTree.of(frbSlideShareInorder)
    }

    test("map") {
//        FRBTNil.mapi { 2 } shouldBe FRBTNil
//
//        frbDepthOneLeft.mapi { s -> "z$s" }.inorder() shouldBe frbDepthOneLeft.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
//        frbDepthOneLeft.maps { s -> "z$s" }.inorder() shouldBe frbDepthOneLeft.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
//        FRBTree.map(frbDepthOneLeft, {it + 2}, { "F$it"} ) shouldBe FRBTNode(TKVEntry.of(mEntry.getk() + 2, "F${mEntry.getv()}"), FRBTree.BLACK,
//                                                                        FRBTNode(TKVEntry.of(lEntry.getk() + 2, "F${lEntry.getv()}")),
//                                                                        FRBTNil)
//        frbDepthOneFull.mapi { s -> "z$s" }.inorder() shouldBe frbDepthOneFull.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
//        frbDepthOneFull.maps { s -> "z$s" }.inorder() shouldBe frbDepthOneFull.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
//        frbWikiTree.mapi { s -> "z$s" }.inorder() shouldBe frbWikiTree.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
//        frbWikiTree.maps { s -> "z$s" }.inorder() shouldBe frbWikiTree.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
//        frbSlideShareTree.mapi { s -> "z$s" }.inorder() shouldBe frbSlideShareTree.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
//        frbSlideShareTree.maps { s -> "z$s" }.inorder() shouldBe frbSlideShareTree.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
    }

    //
    // companion object
    //

    test("co.nul") {
        nul<Int, Int>() shouldBe FRBTNil
    }

    test("co.==") {
        (nul<Int, Int>() == nul<Int, Int>()) shouldBe true
        (nul<Int, Int>() == FRBTNil) shouldBe true
        (FRBTNil == nul<Int, Int>()) shouldBe true
        (FRBTNode(TKVEntry.of(aEntry.hashCode(),aEntry)) == FRBTNode(TKVEntry.of(aEntry.hashCode(),aEntry))) shouldBe true
    }


    test("co.of(list)") {
        of(FList.of(*arrayOf(mEntry,lEntry,nEntry))) shouldBe FRBTNode(mEntry, BLACK, FRBTNode(lEntry, BLACK), FRBTNode(nEntry, BLACK))
        // TWO                             vvvvvv               vvvvvv
        of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry))) shouldBe
            FRBTNode(mEntry, BLACK,
                FRBTNode(cEntry, RED,
                    // ONE   vvvvvv
                    FRBTNode(bEntry, BLACK),
                    FRBTNode(dEntry, BLACK)),
                FRBTNode(zEntry, BLACK))
    }
})

