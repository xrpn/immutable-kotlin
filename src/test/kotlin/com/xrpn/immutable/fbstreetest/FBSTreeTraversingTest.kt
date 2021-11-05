package com.xrpn.immutable.fbstreetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.fbtAssertNodeInvariant
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.aEntry
import com.xrpn.immutable.bEntry
import com.xrpn.immutable.cEntry
import com.xrpn.immutable.dEntry
import com.xrpn.immutable.depthOneFullBreadthFirst
import com.xrpn.immutable.depthOneFullInorder
import com.xrpn.immutable.depthOneFullPostorder
import com.xrpn.immutable.depthOneFullPreorder
import com.xrpn.immutable.depthTwoLeftLeftBreadthFirst
import com.xrpn.immutable.depthTwoLeftLeftInorder
import com.xrpn.immutable.depthTwoLeftLeftPostorder
import com.xrpn.immutable.depthTwoLeftLeftPreorder
import com.xrpn.immutable.depthTwoLeftRightBreadthFirst
import com.xrpn.immutable.depthTwoLeftRightInorder
import com.xrpn.immutable.depthTwoLeftRightPostorder
import com.xrpn.immutable.depthTwoLeftRightPreorder
import com.xrpn.immutable.depthTwoRightLeftBreadthFirst
import com.xrpn.immutable.depthTwoRightLeftInorder
import com.xrpn.immutable.depthTwoRightLeftPostorder
import com.xrpn.immutable.depthTwoRightLeftPreorder
import com.xrpn.immutable.depthTwoRightRightBreadthFirst
import com.xrpn.immutable.depthTwoRightRightInorder
import com.xrpn.immutable.depthTwoRightRightPostorder
import com.xrpn.immutable.depthTwoRightRightPreorder
import com.xrpn.immutable.eEntry
import com.xrpn.immutable.fEntry
import com.xrpn.immutable.gEntry
import com.xrpn.immutable.hEntry
import com.xrpn.immutable.iEntry
import com.xrpn.immutable.lEntry
import com.xrpn.immutable.mEntry
import com.xrpn.immutable.nEntry
import com.xrpn.immutable.rEntry
import com.xrpn.immutable.sEntry
import com.xrpn.immutable.slideShareBreadthFirst
import com.xrpn.immutable.slideShareInorder
import com.xrpn.immutable.slideSharePostorder
import com.xrpn.immutable.slideSharePreorder
import com.xrpn.immutable.uEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstree
import io.kotest.xrpn.fbstreeWithDups

class FBSTreeTraversingTest : FunSpec({

    val repeats = 25

    beforeTest {}

    test("preorder") {
        (nul<Int,Int>().preorder() === FLNil) shouldBe true
        (nul<Int,Int>(true).preorder() === FLNil) shouldBe true
        FBSTNode.of(false, mEntry).preorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.preorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.preorder() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.preorder() shouldBe depthOneFullPreorder

        depthTwoLeftRight.preorder() shouldBe depthTwoLeftRightPreorder
        depthTwoLeftLeft.preorder() shouldBe depthTwoLeftLeftPreorder
        depthTwoRightRight.preorder() shouldBe depthTwoRightRightPreorder
        depthTwoRightLeft.preorder() shouldBe depthTwoRightLeftPreorder

        wikiTree.preorder() shouldBe
            FLCons(
                fEntry,
                FLCons(
                    bEntry,
                    FLCons(
                        aEntry,
                        FLCons(
                            dEntry,
                            FLCons(
                                cEntry,
                                FLCons(
                                    eEntry,
                                    FLCons(
                                        gEntry,
                                        FLCons(
                                            iEntry,
                                            FLCons(hEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        slideShareTree.preorder() shouldBe slideSharePreorder
        slideShareTreeLoose.preorder() shouldBe slideSharePreorder
    }

    test("preorder reverse") {
        (nul<Int,Int>().preorder(true) === FLNil) shouldBe true
        (nul<Int,Int>(true).preorder(true) === FLNil) shouldBe true

        FBSTNode.of(false, mEntry).preorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.preorder(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.preorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.preorder(reverse = true) shouldBe depthOneFullPreorder.freverse()

        depthTwoLeftRight.preorder(reverse = true) shouldBe depthTwoLeftRightPreorder.freverse()
        depthTwoLeftLeft.preorder(reverse = true) shouldBe depthTwoLeftLeftPreorder.freverse()
        depthTwoRightRight.preorder(reverse = true) shouldBe depthTwoRightRightPreorder.freverse()
        depthTwoRightLeft.preorder(reverse = true) shouldBe depthTwoRightLeftPreorder.freverse()

        wikiTree.preorder(reverse = true) shouldBe
            FLCons(
                hEntry,
                FLCons(
                    iEntry,
                    FLCons(
                        gEntry,
                        FLCons(
                            eEntry,
                            FLCons(
                                cEntry,
                                FLCons(
                                    dEntry,
                                    FLCons(
                                        aEntry,
                                        FLCons(
                                            bEntry,
                                            FLCons(fEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        slideShareTree.preorder(reverse = true) shouldBe slideSharePreorder.freverse()
        slideShareTreeLoose.preorder(reverse = true) shouldBe slideSharePreorder.freverse()
    }

    test("inorder") {
        (nul<Int,Int>().inorder() === FLNil) shouldBe true
        (nul<Int,Int>(true).inorder() === FLNil) shouldBe true
        fbtAssertNodeInvariant(FBSTNode.of(true, mEntry)).inorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.inorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.inorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.inorder() shouldBe depthOneFullInorder

        depthTwoLeftRight.inorder() shouldBe depthTwoLeftRightInorder
        depthTwoLeftLeft.inorder() shouldBe depthTwoLeftLeftInorder
        depthTwoRightRight.inorder() shouldBe depthTwoRightRightInorder
        depthTwoRightLeft.inorder() shouldBe depthTwoRightLeftInorder

        wikiTree.inorder() shouldBe
            FLCons(
                aEntry,
                FLCons(
                    bEntry,
                    FLCons(
                        cEntry,
                        FLCons(
                            dEntry,
                            FLCons(
                                eEntry,
                                FLCons(
                                    fEntry,
                                    FLCons(
                                        gEntry,
                                        FLCons(
                                            hEntry,
                                            FLCons(iEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        slideShareTree.inorder() shouldBe slideShareInorder
        slideShareTreeLoose.inorder() shouldBe slideShareInorder
    }

    test("inorder reverse") {
        (nul<Int,Int>().inorder(true) === FLNil) shouldBe true
        (nul<Int,Int>(true).inorder(true) === FLNil) shouldBe true
        fbtAssertNodeInvariant(FBSTNode.of(false, mEntry)).inorder(reverse = true) shouldBe FLCons(mEntry, FLNil).freverse()

        depthOneRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil)).freverse()
        depthOneLeft.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil)).freverse()
        depthOneFull.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLNil))).freverse()

        depthTwoLeftRight.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        depthTwoLeftLeft.inorder(reverse = true) shouldBe FLCons(eEntry, FLCons(lEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        depthTwoRightRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLCons(uEntry, FLNil)))).freverse()
        depthTwoRightLeft.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(rEntry, FLCons(sEntry, FLNil)))).freverse()

        wikiTree.inorder(reverse = true) shouldBe
            FLCons(
                aEntry,
                FLCons(
                    bEntry,
                    FLCons(
                        cEntry,
                        FLCons(
                            dEntry,
                            FLCons(
                                eEntry,
                                FLCons(
                                    fEntry,
                                    FLCons(
                                        gEntry,
                                        FLCons(
                                            hEntry,
                                            FLCons(iEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ).freverse()
        slideShareTree.inorder(reverse = true) shouldBe slideShareInorder.freverse()
        slideShareTreeLoose.inorder(reverse = true) shouldBe slideShareInorder.freverse()
    }

    test("postorder") {
        (nul<Int,Int>().postorder() === FLNil) shouldBe true
        (nul<Int,Int>(true).postorder() === FLNil) shouldBe true
        FBSTNode.of(true, mEntry).postorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.postorder() shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.postorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.postorder() shouldBe depthOneFullPostorder

        depthTwoLeftRight.postorder() shouldBe depthTwoLeftRightPostorder
        depthTwoLeftLeft.postorder() shouldBe depthTwoLeftLeftPostorder
        depthTwoRightRight.postorder() shouldBe depthTwoRightRightPostorder
        depthTwoRightLeft.postorder() shouldBe depthTwoRightLeftPostorder

        wikiTree.postorder() shouldBe
            FLCons(
                aEntry,
                FLCons(
                    cEntry,
                    FLCons(
                        eEntry,
                        FLCons(
                            dEntry,
                            FLCons(
                                bEntry,
                                FLCons(
                                    hEntry,
                                    FLCons(
                                        iEntry,
                                        FLCons(
                                            gEntry,
                                            FLCons(fEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        slideShareTree.postorder() shouldBe slideSharePostorder
        slideShareTreeLoose.postorder() shouldBe slideSharePostorder
    }

    test("postorder reverse") {
        (nul<Int,Int>().postorder(true) === FLNil) shouldBe true
        (nul<Int,Int>(true).postorder(true) === FLNil) shouldBe true
        FBSTNode.of(false, mEntry).postorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.postorder(reverse = true) shouldBe depthOneFullPostorder.freverse()

        depthTwoLeftRight.postorder(reverse = true) shouldBe depthTwoLeftRightPostorder.freverse()
        depthTwoLeftLeft.postorder(reverse = true) shouldBe depthTwoLeftLeftPostorder.freverse()
        depthTwoRightRight.postorder(reverse = true) shouldBe depthTwoRightRightPostorder.freverse()
        depthTwoRightLeft.postorder(reverse = true) shouldBe depthTwoRightLeftPostorder.freverse()

        wikiTree.postorder(reverse = true) shouldBe // reverse of A, C, E, D, B, H, I, G, F.
            FLCons(
                fEntry,
                FLCons(
                    gEntry,
                    FLCons(
                        iEntry,
                        FLCons(
                            hEntry,
                            FLCons(
                                bEntry,
                                FLCons(
                                    dEntry,
                                    FLCons(
                                        eEntry,
                                        FLCons(
                                            cEntry,
                                            FLCons(aEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        slideShareTree.postorder(reverse = true) shouldBe slideSharePostorder.freverse()
        slideShareTreeLoose.postorder(reverse = true) shouldBe slideSharePostorder.freverse()
    }

    test("breadthFirst") {
        (nul<Int,Int>().breadthFirst() === FLNil) shouldBe true
        (nul<Int,Int>(true).breadthFirst() === FLNil) shouldBe true
        FBSTNode.of(false, mEntry).breadthFirst() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.breadthFirst() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.breadthFirst() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.breadthFirst() shouldBe depthOneFullBreadthFirst

        depthTwoLeftRight.breadthFirst() shouldBe depthTwoLeftRightBreadthFirst
        depthTwoLeftLeft.breadthFirst() shouldBe depthTwoLeftLeftBreadthFirst
        depthTwoRightRight.breadthFirst() shouldBe depthTwoRightRightBreadthFirst
        depthTwoRightLeft.breadthFirst() shouldBe depthTwoRightLeftBreadthFirst

        wikiTree.breadthFirst() shouldBe
            FLCons(
                fEntry,
                FLCons(
                    bEntry,
                    FLCons(
                        gEntry,
                        FLCons(
                            aEntry,
                            FLCons(
                                dEntry,
                                FLCons(
                                    iEntry,
                                    FLCons(
                                        cEntry,
                                        FLCons(
                                            eEntry,
                                            FLCons(hEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        slideShareTree.breadthFirst() shouldBe slideShareBreadthFirst
    }

    test("breadthFirst reverse") {
        (nul<Int,Int>().breadthFirst(true) === FLNil) shouldBe true
        (nul<Int,Int>(true).breadthFirst(true) === FLNil) shouldBe true
        FBSTNode.of(true, mEntry).breadthFirst(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.breadthFirst(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.breadthFirst(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.breadthFirst(reverse = true) shouldBe depthOneFullBreadthFirst.freverse()

        depthTwoLeftRight.breadthFirst(reverse = true) shouldBe depthTwoLeftRightBreadthFirst.freverse()
        depthTwoLeftLeft.breadthFirst(reverse = true) shouldBe depthTwoLeftLeftBreadthFirst.freverse()
        depthTwoRightRight.breadthFirst(reverse = true) shouldBe depthTwoRightRightBreadthFirst.freverse()
        depthTwoRightLeft.breadthFirst(reverse = true) shouldBe depthTwoRightLeftBreadthFirst.freverse()

        wikiTree.breadthFirst(reverse = true) shouldBe
            FLCons(
                hEntry,
                FLCons(
                    eEntry,
                    FLCons(
                        cEntry,
                        FLCons(
                            iEntry,
                            FLCons(
                                dEntry,
                                FLCons(
                                    aEntry,
                                    FLCons(
                                        gEntry,
                                        FLCons(
                                            bEntry,
                                            FLCons(fEntry, FLNil)
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        slideShareTree.breadthFirst(reverse = true) shouldBe slideShareBreadthFirst.freverse()
    }

    test("values") {
        Arb.fbstree(Arb.int()).checkAll(repeats) { fbst ->
            val lv = fbst.copyToMutableMap().map { it.value }.sorted()
            fbst.inorderValues() shouldBe lv
        }
        Arb.fbstreeWithDups(Arb.int(),5..30).checkAll(repeats) { fbst ->
            val lv: List<Int> = fbst.copyToMutableMap().map { it.value }.sorted()
            lv.forEach { v -> fbst.fcontainsValue(v) shouldBe true }
            fbst.forEach { v: TKVEntry<Int, Int> -> lv.contains(v.getv()) shouldBe true }
        }
    }
})
