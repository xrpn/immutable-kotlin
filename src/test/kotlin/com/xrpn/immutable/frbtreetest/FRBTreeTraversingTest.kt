package com.xrpn.immutable.frbtreetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.BLACK
import com.xrpn.immutable.FRBTree.Companion.frbtPartAssert
import com.xrpn.immutable.eEntry
import com.xrpn.immutable.frbDepthOneFullBreadthFirst
import com.xrpn.immutable.frbDepthOneFullInorder
import com.xrpn.immutable.frbDepthOneFullPostorder
import com.xrpn.immutable.frbDepthOneFullPreorder
import com.xrpn.immutable.frbDepthTwoLeftLeftBreadthFirst
import com.xrpn.immutable.frbDepthTwoLeftLeftInorder
import com.xrpn.immutable.frbDepthTwoLeftLeftPostorder
import com.xrpn.immutable.frbDepthTwoLeftLeftPreorder
import com.xrpn.immutable.frbDepthTwoRightLeftBreadthFirst
import com.xrpn.immutable.frbDepthTwoRightLeftInorder
import com.xrpn.immutable.frbDepthTwoRightLeftPostorder
import com.xrpn.immutable.frbDepthTwoRightLeftPreorder
import com.xrpn.immutable.frbDepthTwoRightRightBreadthFirst
import com.xrpn.immutable.frbDepthTwoRightRightInorder
import com.xrpn.immutable.frbDepthTwoRightRightPostorder
import com.xrpn.immutable.frbDepthTwoRightRightPreorder
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
import com.xrpn.immutable.lEntry
import com.xrpn.immutable.mEntry
import com.xrpn.immutable.nEntry
import com.xrpn.immutable.rEntry
import com.xrpn.immutable.sEntry
import com.xrpn.immutable.ttDepthTwoLeftRightBreadthFirst
import com.xrpn.immutable.ttDepthTwoLeftRightInorder
import com.xrpn.immutable.ttDepthTwoLeftRightPostorder
import com.xrpn.immutable.ttDepthTwoLeftRightPreorder
import com.xrpn.immutable.uEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FRBTreeTraversingTest : FunSpec({

    beforeTest {}

    test("preorder") {
        FRBTNil.preorder() shouldBe FLNil
        FRBTNode.of(mEntry).preorder() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.preorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.preorder() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        frbDepthOneFull.preorder() shouldBe frbDepthOneFullPreorder

        ttDepthTwoLeftRight.preorder() shouldBe ttDepthTwoLeftRightPreorder
        frbDepthTwoLeftLeft.preorder() shouldBe frbDepthTwoLeftLeftPreorder
        frbDepthTwoRightRight.preorder() shouldBe frbDepthTwoRightRightPreorder
        frbDepthTwoRightLeft.preorder() shouldBe frbDepthTwoRightLeftPreorder
    }

    test("preorder reverse") {
        FRBTNil.preorder(reverse = true) shouldBe FLNil
        FRBTNode.of(mEntry).preorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.preorder(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        frbDepthOneLeft.preorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.preorder(reverse = true) shouldBe frbDepthOneFullPreorder.freverse()

        ttDepthTwoLeftRight.preorder(reverse = true) shouldBe ttDepthTwoLeftRightPreorder.freverse()
        frbDepthTwoLeftLeft.preorder(reverse = true) shouldBe frbDepthTwoLeftLeftPreorder.freverse()
        frbDepthTwoRightRight.preorder(reverse = true) shouldBe frbDepthTwoRightRightPreorder.freverse()
        frbDepthTwoRightLeft.preorder(reverse = true) shouldBe frbDepthTwoRightLeftPreorder.freverse()

        frbWikiTree.preorder(reverse = true) shouldBe frbWikiPreorder.freverse()
        frbSlideShareTree.preorder(reverse = true) shouldBe frbSlideSharePreorder.freverse()
    }

    test("inorder") {
        FRBTNil.inorder() shouldBe FLNil
        frbtPartAssert(FRBTNode.of(mEntry, BLACK)).inorder() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.inorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.inorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.inorder() shouldBe frbDepthOneFullInorder

        ttDepthTwoLeftRight.inorder() shouldBe ttDepthTwoLeftRightInorder
        frbDepthTwoLeftLeft.inorder() shouldBe frbDepthTwoLeftLeftInorder
        frbDepthTwoRightRight.inorder() shouldBe frbDepthTwoRightRightInorder
        frbDepthTwoRightLeft.inorder() shouldBe frbDepthTwoRightLeftInorder

        frbWikiTree.inorder() shouldBe frbWikiInorder
        frbSlideShareTree.inorder() shouldBe frbSlideShareInorder
    }

    test("inorder reverse") {
        FRBTNil.inorder(reverse = true) shouldBe FLNil
        frbtPartAssert(FRBTNode.of(mEntry)).inorder(reverse = true) shouldBe FLCons(mEntry, FLNil).freverse()

        ttDepthOneRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil)).freverse()
        frbDepthOneLeft.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil)).freverse()
        frbDepthOneFull.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLNil))).freverse()

        ttDepthTwoLeftRight.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        frbDepthTwoLeftLeft.inorder(reverse = true) shouldBe FLCons(eEntry, FLCons(lEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        frbDepthTwoRightRight.inorder(reverse = true) shouldBe FLCons(
            mEntry, FLCons(
                nEntry, FLCons(
                    sEntry, FLCons(
                        uEntry, FLNil
                    )
                )
            )
        ).freverse()
        frbDepthTwoRightLeft.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(rEntry, FLCons(sEntry, FLNil)))).freverse()

        frbWikiTree.inorder(reverse = true) shouldBe frbWikiInorder.freverse()
        frbSlideShareTree.inorder(reverse = true) shouldBe frbSlideShareInorder.freverse()
    }

    test("postorder") {
        FRBTNil.postorder() shouldBe FLNil
        FRBTNode.of(mEntry).postorder() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.postorder() shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        frbDepthOneLeft.postorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.postorder() shouldBe frbDepthOneFullPostorder

        ttDepthTwoLeftRight.postorder() shouldBe ttDepthTwoLeftRightPostorder
        frbDepthTwoLeftLeft.postorder() shouldBe frbDepthTwoLeftLeftPostorder
        frbDepthTwoRightRight.postorder() shouldBe frbDepthTwoRightRightPostorder
        frbDepthTwoRightLeft.postorder() shouldBe frbDepthTwoRightLeftPostorder
    }

    test("postorder reverse") {
        FRBTNil.postorder(reverse = true) shouldBe FLNil
        FRBTNode.of(mEntry).postorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        frbDepthOneFull.postorder(reverse = true) shouldBe frbDepthOneFullPostorder.freverse()

        ttDepthTwoLeftRight.postorder(reverse = true) shouldBe ttDepthTwoLeftRightPostorder.freverse()
        frbDepthTwoLeftLeft.postorder(reverse = true) shouldBe frbDepthTwoLeftLeftPostorder.freverse()
        frbDepthTwoRightRight.postorder(reverse = true) shouldBe frbDepthTwoRightRightPostorder.freverse()
        frbDepthTwoRightLeft.postorder(reverse = true) shouldBe frbDepthTwoRightLeftPostorder.freverse()

        frbWikiTree.postorder(reverse = true) shouldBe frbWikiPostorder.freverse()
        frbSlideShareTree.postorder(reverse = true) shouldBe frbSlideSharePostorder.freverse()
    }

    test("breadthFirst") {
        FRBTNil.breadthFirst() shouldBe FLNil
        FRBTNode.of(mEntry).breadthFirst() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.breadthFirst() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.breadthFirst() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        frbDepthOneFull.breadthFirst() shouldBe frbDepthOneFullBreadthFirst

        ttDepthTwoLeftRight.breadthFirst() shouldBe ttDepthTwoLeftRightBreadthFirst
        frbDepthTwoLeftLeft.breadthFirst() shouldBe frbDepthTwoLeftLeftBreadthFirst
        frbDepthTwoRightRight.breadthFirst() shouldBe frbDepthTwoRightRightBreadthFirst
        frbDepthTwoRightLeft.breadthFirst() shouldBe frbDepthTwoRightLeftBreadthFirst

        frbWikiTree.breadthFirst() shouldBe frbWikiBreadthFirst
        frbSlideShareTree.breadthFirst() shouldBe frbSlideShareBreadthFirst
    }

    test("breadthFirst reverse") {
        FRBTNil.breadthFirst(reverse = true) shouldBe FLNil
        FRBTNode.of(mEntry).breadthFirst(reverse = true) shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.breadthFirst(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        frbDepthOneLeft.breadthFirst(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.breadthFirst(reverse = true) shouldBe frbDepthOneFullBreadthFirst.freverse()

        ttDepthTwoLeftRight.breadthFirst(reverse = true) shouldBe ttDepthTwoLeftRightBreadthFirst.freverse()
        frbDepthTwoLeftLeft.breadthFirst(reverse = true) shouldBe frbDepthTwoLeftLeftBreadthFirst.freverse()
        frbDepthTwoRightRight.breadthFirst(reverse = true) shouldBe frbDepthTwoRightRightBreadthFirst.freverse()
        frbDepthTwoRightLeft.breadthFirst(reverse = true) shouldBe frbDepthTwoRightLeftBreadthFirst.freverse()

        frbWikiTree.breadthFirst(reverse = true) shouldBe frbWikiBreadthFirst.freverse()
        frbSlideShareTree.breadthFirst(reverse = true) shouldBe frbSlideShareBreadthFirst.freverse()
    }

})