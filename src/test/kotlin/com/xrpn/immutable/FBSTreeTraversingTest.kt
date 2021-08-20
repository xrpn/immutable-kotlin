package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.fbtAssert
import com.xrpn.immutable.FBSTree.Companion.toIMBTree
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstree
import io.kotest.xrpn.fbstreeAllowDups
import java.util.concurrent.atomic.AtomicInteger

class FBSTreeTraversingTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("preorder") {
        FBSTNil.preorder() shouldBe FLNil
        FBSTNode(mEntry).preorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.preorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.preorder() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.preorder() shouldBe depthOneFullPreorder

        depthTwoLeftRight.preorder() shouldBe depthTwoLeftRightPreorder
        depthTwoLeftLeft.preorder() shouldBe depthTwoLeftLeftPreorder
        depthTwoRightRight.preorder() shouldBe depthTwoRightRightPreorder
        depthTwoRightLeft.preorder() shouldBe depthTwoRightLeftPreorder

        wikiTree.preorder() shouldBe
            FLCons(fEntry,
                FLCons(bEntry,
                    FLCons(aEntry,
                        FLCons(dEntry,
                            FLCons(cEntry,
                                FLCons(eEntry,
                                    FLCons(gEntry,
                                        FLCons(iEntry,
                                            FLCons(hEntry, FLNil)))))))))
        slideShareTree.preorder() shouldBe slideSharePreorder
    }

    test("preorder reverse") {
        FBSTNil.preorder(reverse = true) shouldBe FLNil
        FBSTNode(mEntry).preorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.preorder(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.preorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.preorder(reverse = true) shouldBe depthOneFullPreorder.freverse()

        depthTwoLeftRight.preorder(reverse = true) shouldBe depthTwoLeftRightPreorder.freverse()
        depthTwoLeftLeft.preorder(reverse = true) shouldBe depthTwoLeftLeftPreorder.freverse()
        depthTwoRightRight.preorder(reverse = true) shouldBe depthTwoRightRightPreorder.freverse()
        depthTwoRightLeft.preorder(reverse = true) shouldBe depthTwoRightLeftPreorder.freverse()

        wikiTree.preorder(reverse = true) shouldBe
            FLCons(hEntry,
                FLCons(iEntry,
                    FLCons(gEntry,
                        FLCons(eEntry,
                            FLCons(cEntry,
                                FLCons(dEntry,
                                    FLCons(aEntry,
                                        FLCons(bEntry,
                                            FLCons(fEntry, FLNil)))))))))
        slideShareTree.preorder(reverse = true) shouldBe slideSharePreorder.freverse()
    }

    test("inorder") {
        FBSTNil.inorder() shouldBe FLNil
        fbtAssert(FBSTNode(mEntry)).inorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.inorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.inorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.inorder() shouldBe depthOneFullInorder

        depthTwoLeftRight.inorder() shouldBe depthTwoLeftRightInorder
        depthTwoLeftLeft.inorder() shouldBe depthTwoLeftLeftInorder
        depthTwoRightRight.inorder() shouldBe depthTwoRightRightInorder
        depthTwoRightLeft.inorder() shouldBe depthTwoRightLeftInorder

        wikiTree.inorder() shouldBe
            FLCons(aEntry,
                FLCons(bEntry,
                    FLCons(cEntry,
                        FLCons(dEntry,
                            FLCons(eEntry,
                                FLCons(fEntry,
                                    FLCons(gEntry,
                                        FLCons(hEntry,
                                            FLCons(iEntry, FLNil)))))))))
        slideShareTree.inorder() shouldBe slideShareInorder
    }

    test("inorder reverse") {
        FBSTNil.inorder(reverse = true) shouldBe FLNil
        fbtAssert(FBSTNode(mEntry)).inorder(reverse = true) shouldBe FLCons(mEntry, FLNil).freverse()

        depthOneRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil)).freverse()
        depthOneLeft.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil)).freverse()
        depthOneFull.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLNil))).freverse()

        depthTwoLeftRight.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        depthTwoLeftLeft.inorder(reverse = true) shouldBe FLCons(eEntry, FLCons(lEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        depthTwoRightRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLCons(uEntry, FLNil)))).freverse()
        depthTwoRightLeft.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(rEntry, FLCons(sEntry, FLNil)))).freverse()

        wikiTree.inorder(reverse = true) shouldBe
            FLCons(aEntry,
                FLCons(bEntry,
                    FLCons(cEntry,
                        FLCons(dEntry,
                            FLCons(eEntry,
                                FLCons(fEntry,
                                    FLCons(gEntry,
                                        FLCons(hEntry,
                                            FLCons(iEntry, FLNil))))))))).freverse()
        slideShareTree.inorder(reverse = true) shouldBe slideShareInorder.freverse()
    }

    test("postorder") {
        FBSTNil.postorder() shouldBe FLNil
        FBSTNode(mEntry).postorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.postorder() shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.postorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.postorder() shouldBe depthOneFullPostorder

        depthTwoLeftRight.postorder() shouldBe depthTwoLeftRightPostorder
        depthTwoLeftLeft.postorder() shouldBe depthTwoLeftLeftPostorder
        depthTwoRightRight.postorder() shouldBe depthTwoRightRightPostorder
        depthTwoRightLeft.postorder() shouldBe depthTwoRightLeftPostorder

        wikiTree.postorder() shouldBe
            FLCons(aEntry,
                FLCons(cEntry,
                    FLCons(eEntry,
                        FLCons(dEntry,
                            FLCons(bEntry,
                                FLCons(hEntry,
                                    FLCons(iEntry,
                                        FLCons(gEntry,
                                            FLCons(fEntry, FLNil)))))))))
        slideShareTree.postorder() shouldBe slideSharePostorder
    }

    test("postorder reverse") {
        FBSTNil.postorder(reverse = true) shouldBe FLNil
        FBSTNode(mEntry).postorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.postorder(reverse = true) shouldBe depthOneFullPostorder.freverse()

        depthTwoLeftRight.postorder(reverse = true) shouldBe depthTwoLeftRightPostorder.freverse()
        depthTwoLeftLeft.postorder(reverse = true) shouldBe depthTwoLeftLeftPostorder.freverse()
        depthTwoRightRight.postorder(reverse = true) shouldBe depthTwoRightRightPostorder.freverse()
        depthTwoRightLeft.postorder(reverse = true) shouldBe depthTwoRightLeftPostorder.freverse()

        wikiTree.postorder(reverse = true) shouldBe // reverse of A, C, E, D, B, H, I, G, F.
            FLCons(fEntry,
                FLCons(gEntry,
                    FLCons(iEntry,
                        FLCons(hEntry,
                            FLCons(bEntry,
                                FLCons(dEntry,
                                    FLCons(eEntry,
                                        FLCons(cEntry,
                                            FLCons(aEntry, FLNil)))))))))
        slideShareTree.postorder(reverse = true) shouldBe slideSharePostorder.freverse()
    }

    test("breadthFirst") {
        FBSTNil.breadthFirst() shouldBe FLNil
        FBSTNode(mEntry).breadthFirst() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.breadthFirst() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.breadthFirst() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.breadthFirst() shouldBe depthOneFullBreadthFirst

        depthTwoLeftRight.breadthFirst() shouldBe depthTwoLeftRightBreadthFirst
        depthTwoLeftLeft.breadthFirst() shouldBe depthTwoLeftLeftBreadthFirst
        depthTwoRightRight.breadthFirst() shouldBe depthTwoRightRightBreadthFirst
        depthTwoRightLeft.breadthFirst() shouldBe depthTwoRightLeftBreadthFirst

        wikiTree.breadthFirst() shouldBe
            FLCons(fEntry,
                FLCons(bEntry,
                    FLCons(gEntry,
                        FLCons(aEntry,
                            FLCons(dEntry,
                                FLCons(iEntry,
                                    FLCons(cEntry,
                                        FLCons(eEntry,
                                            FLCons(hEntry, FLNil)))))))))
        slideShareTree.breadthFirst() shouldBe slideShareBreadthFirst
    }

    test("breadthFirst reverse") {
        FBSTNil.breadthFirst(reverse = true) shouldBe FLNil
        FBSTNode(mEntry).breadthFirst(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.breadthFirst(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.breadthFirst(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.breadthFirst(reverse = true) shouldBe depthOneFullBreadthFirst.freverse()

        depthTwoLeftRight.breadthFirst(reverse = true) shouldBe depthTwoLeftRightBreadthFirst.freverse()
        depthTwoLeftLeft.breadthFirst(reverse = true) shouldBe depthTwoLeftLeftBreadthFirst.freverse()
        depthTwoRightRight.breadthFirst(reverse = true) shouldBe depthTwoRightRightBreadthFirst.freverse()
        depthTwoRightLeft.breadthFirst(reverse = true) shouldBe depthTwoRightLeftBreadthFirst.freverse()

        wikiTree.breadthFirst(reverse = true) shouldBe
            FLCons(hEntry,
                FLCons(eEntry,
                    FLCons(cEntry,
                        FLCons(iEntry,
                            FLCons(dEntry,
                                FLCons(aEntry,
                                    FLCons(gEntry,
                                        FLCons(bEntry,
                                            FLCons(fEntry, FLNil)))))))))
        slideShareTree.breadthFirst(reverse = true) shouldBe slideShareBreadthFirst.freverse()
    }

    test("values") {
        Arb.fbstree<Int, Int>(Arb.int()).checkAll(repeats, PropTestConfig(seed = -5060833568559122518)) { fbst ->
            val lv = fbst.copyToMutableMap().map { it.value }.sorted()
            fbst.inorderValues() shouldBe lv
        }
    }
})
