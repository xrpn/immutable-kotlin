package com.xrpn.immutable.fbstreetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.ofvs
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import com.xrpn.immutable.bEntry
import com.xrpn.immutable.cEntry
import com.xrpn.immutable.dEntry
import com.xrpn.immutable.eEntry
import com.xrpn.immutable.lEntry
import com.xrpn.immutable.mEntry
import com.xrpn.immutable.nEntry
import com.xrpn.immutable.oEntry
import com.xrpn.immutable.pEntry
import com.xrpn.immutable.rEntry
import com.xrpn.immutable.sEntry
import com.xrpn.immutable.uEntry
import com.xrpn.immutable.zEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FBSTreeAlteringTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("finsert") {
        nul<Int, String>().finsert(mEntry) shouldBe FBSTNode.of(false,mEntry)
        FBSTNode.of(false,mEntry).finsert(lEntry) shouldBe depthOneLeft
        FBSTNode.of(false,mEntry).finsert(nEntry) shouldBe depthOneRight

        depthOneLeft.finsert(nEntry) shouldBe depthOneFull
        depthOneRight.finsert(lEntry) shouldBe depthOneFull

        depthTwoLeftPartial.finsert(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsert(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsert(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsert(rEntry) shouldBe depthTwoRightLeft

        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry)).finsert(pEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTUnique.empty, FBSTNode.of(false,pEntry)))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry)).finsert(nEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTNode.of(false,nEntry), FBSTUnique.empty))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry), FBSTNode.of(false,zEntry)).finsert(dEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTUnique.empty, FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry), FBSTNode.of(false,zEntry)).finsert(bEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTNode.of(false,bEntry), FBSTUnique.empty), FBSTNode.of(false,zEntry))

        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry)).finsert(pEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTUnique.empty, FBSTNode.of(false,pEntry)))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTUnique.empty, FBSTNode.of(false,pEntry))).finsert(nEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTNode.of(false,nEntry), FBSTNode.of(false,pEntry)))

        // --
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry), FBSTNode.of(false,zEntry)).finsert(dEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTUnique.empty, FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTUnique.empty, FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry)).finsert(bEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTNode.of(false,bEntry), FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry))
    }

    test("finsertDup (A)") {
        com.xrpn.immutable.FBSTree.nul<Int, String>().finsert(mEntry) shouldBe FBSTNode.of(true, mEntry)
        FBSTNode.of(true, mEntry).finsert(mEntry) shouldBe FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry))
        FBSTNode.of(true, mEntry).finsert(lEntry) shouldBe depthOneLeft
        FBSTNode.of(true, mEntry).finsert(nEntry) shouldBe depthOneRight

        FBSTNode.of(true, mEntry).finsert(mEntry) shouldBe FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry))
        depthOneLeft.finsert(nEntry) shouldBe depthOneFull
        depthOneRight.finsert(lEntry) shouldBe depthOneFull

        // dups right
        depthOneLeft.toGeneric().finsert(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTNode.of(true, lEntry), FBSTNode.of(true, mEntry))
        depthOneLeft.toGeneric().finsert(mEntry)
            .finsert(mEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry),
                                 FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry))
                )
        depthOneLeft.toGeneric().finsert(mEntry)
            .finsert(mEntry)
                .finsert(mEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry),
                                 FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry)))
                )

        // dups left
        depthOneLeft.toGeneric().finsert(lEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry, FBSTGeneric.empty, FBSTNode.of(true, lEntry)),
                                 FBSTGeneric.empty
                )
        depthOneLeft.toGeneric().finsert(lEntry)
            .finsert(lEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry, FBSTGeneric.empty, FBSTNode.of(true, lEntry, FBSTGeneric.empty, FBSTNode.of(true, lEntry))),
                                 FBSTGeneric.empty
                )
        depthOneLeft.toGeneric().finsert(lEntry)
            .finsert(lEntry)
                .finsert(lEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, 
                        lEntry, FBSTGeneric.empty, FBSTNode.of(true,
                            lEntry, FBSTGeneric.empty,
                            FBSTNode.of(true,
                                lEntry, FBSTGeneric.empty,
                                FBSTNode.of(true, lEntry)
                            )
                        )
                    ),
                    FBSTGeneric.empty
                )

        // dups right
        depthOneRight.toGeneric().finsert(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, depthOneRight.toGeneric())
        depthOneRight.toGeneric().finsert(mEntry)
            .finsert(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, depthOneRight.toGeneric()))
        depthOneRight.toGeneric().finsert(mEntry)
            .finsert(mEntry)
                .finsert(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, depthOneRight.toGeneric())))

        // dups left
        depthOneRight.toGeneric().finsert(nEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry)))
        depthOneRight.toGeneric().finsert(nEntry)
            .finsert(nEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry))))
        depthOneRight.toGeneric().finsert(nEntry)
            .finsert(nEntry)
                .finsert(nEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTGeneric.empty,
                                 FBSTNode.of(true, 
                                     nEntry, FBSTGeneric.empty,
                                                  FBSTNode.of(true, 
                                                      nEntry, FBSTGeneric.empty,
                                                                   FBSTNode.of(true, nEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry))
                                                  )
                                 )
                )
    }

    test("finsertDup (B)") {

        depthTwoLeftPartial.finsert(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsert(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsert(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsert(rEntry) shouldBe depthTwoRightLeft

        depthTwoLeftRight.toGeneric().finsert(nEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, mEntry)
                    ),
                    FBSTNode.of(true, 
                        nEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, sEntry)
                    )
                )
        depthTwoLeftRight.toGeneric().finsert(lEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, 
                            lEntry,
                            FBSTGeneric.empty,
                            FBSTNode.of(true, mEntry)
                        )
                    ),
                    FBSTNode.of(true, sEntry)
                )
        depthTwoLeftRight.toGeneric().finsert(mEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, 
                            mEntry,
                            FBSTGeneric.empty,
                            FBSTNode.of(true, mEntry)
                        )
                    ),
                    FBSTNode.of(true, sEntry)
                )
        depthTwoLeftRight.toGeneric().finsert(sEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, mEntry)
                    ),
                    FBSTNode.of(true, sEntry, FBSTGeneric.empty, FBSTNode.of(true, sEntry))
                )

        depthTwoLeftLeft.toGeneric().finsert(nEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTNode.of(true, eEntry),
                        FBSTGeneric.empty
                    ),
                    FBSTNode.of(true, 
                        nEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, sEntry)
                    )
                )
        depthTwoLeftLeft.toGeneric().finsert(lEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTNode.of(true, eEntry),
                        FBSTNode.of(true, lEntry)
                    ),
                    FBSTNode.of(true, sEntry)
                )
        depthTwoLeftLeft.toGeneric().finsert(eEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTNode.of(true, 
                            eEntry,
                            FBSTGeneric.empty,
                            FBSTNode.of(true, eEntry)
                        ),
                        FBSTGeneric.empty
                    ),
                    FBSTNode.of(true, sEntry)
                )
        depthTwoLeftLeft.toGeneric().finsert(sEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTNode.of(true, eEntry),
                        FBSTGeneric.empty
                    ),
                    FBSTNode.of(true, sEntry, FBSTGeneric.empty, FBSTNode.of(true, sEntry))
                )

        depthTwoRightRight.toGeneric().finsert(nEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, mEntry),
                    FBSTNode.of(true, 
                        nEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, 
                            sEntry,
                            FBSTGeneric.empty,
                            FBSTNode.of(true, uEntry)
                        )
                    )
                )
        depthTwoRightRight.toGeneric().finsert(mEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        mEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, mEntry)
                    ),
                    FBSTNode.of(true, 
                        sEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, uEntry)
                    )
                )
        depthTwoRightRight.toGeneric().finsert(sEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, mEntry),
                    FBSTNode.of(true, 
                        sEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, 
                            sEntry,
                            FBSTGeneric.empty,
                            FBSTNode.of(true, uEntry)
                        )
                    )
                )
        depthTwoRightRight.toGeneric().finsert(uEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, mEntry),
                    FBSTNode.of(true, 
                        sEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, 
                            uEntry,
                            FBSTGeneric.empty,
                            FBSTNode.of(true, uEntry)
                        )
                    )
                )

        depthTwoRightLeft.toGeneric().finsert(nEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, mEntry),
                    FBSTNode.of(true, 
                        nEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, 
                            sEntry,
                            FBSTNode.of(true, rEntry),
                            FBSTGeneric.empty
                        )
                    )
                )
        depthTwoRightLeft.toGeneric().finsert(mEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        mEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, mEntry)
                    ),
                    FBSTNode.of(true, 
                        sEntry,
                        FBSTNode.of(true, rEntry),
                        FBSTGeneric.empty
                    )
                )
        depthTwoRightLeft.toGeneric().finsert(sEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, mEntry),
                    FBSTNode.of(true, 
                        sEntry,
                        FBSTNode.of(true, rEntry),
                        FBSTNode.of(true, sEntry)
                    )
                )
        depthTwoRightLeft.toGeneric().finsert(rEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, mEntry),
                    FBSTNode.of(true, 
                        sEntry,
                        FBSTNode.of(true, 
                            rEntry,
                            FBSTGeneric.empty,
                            FBSTNode.of(true, rEntry)
                        ),
                        FBSTGeneric.empty
                    )
                )
    }

    test("finsertDup (B) rejection") {
        depthTwoLeftPartial.finsert(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsert(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsert(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsert(rEntry) shouldBe depthTwoRightLeft

        (depthTwoLeftRight.finsert(nEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftRight.finsert(lEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftRight.finsert(mEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftRight.finsert(sEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftLeft.finsert(nEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoLeftLeft.finsert(lEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoLeftLeft.finsert(eEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoLeftLeft.finsert(sEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoRightRight.finsert(nEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightRight.finsert(mEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightRight.finsert(sEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightRight.finsert(uEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightLeft.finsert(nEntry) === depthTwoRightLeft) shouldBe true
        (depthTwoRightLeft.finsert(mEntry) === depthTwoRightLeft) shouldBe true
        (depthTwoRightLeft.finsert(sEntry) === depthTwoRightLeft) shouldBe true
        (depthTwoRightLeft.finsert(rEntry) === depthTwoRightLeft) shouldBe true
    }

    test("finserts, finsertt, finsertsDup (A)") {
        nul<Int, Int>().finserts(FLNil) shouldBe emptyIMBTree<Int, Int>()
        (nul<Int, Int>().finserts(FLNil) === emptyIMBTree<Int, Int>()) shouldBe true
        nul<Int, Int>().finsertt(emptyIMBTree()) shouldBe emptyIMBTree<Int, Int>()
        (nul<Int, Int>().finsertt(emptyIMBTree()) === emptyIMBTree<Int, Int>()) shouldBe true
    }

    test("finserts, finsertt, finsertsDup (B)") {
        Arb.flist<Int, Int>(Arb.int(-25, 25)).checkAll(repeats) { fl ->
            // TODO OOM_ERR val tab = ofvs(fl.asList().iterator())
            val flkv: FList<TKVEntry<String, Int>> = fl.fmap { it.toSAEntry() }
            val l = flkv.copyToMutableList()
            val s = l.toSet()
            nul<String, Int>().finserts(flkv).inorder().softEqual(s.sorted()) shouldBe true
            // TODO OOM_ERR nul<String, Int>().finsertt(tab).inorder() shouldBe s.sorted()
            // TODO OOM_ERR tab.finsertt(FRBTree.nul()).inorder() shouldBe s.sorted()
        }
    }

    test("toLoose") {
        wikiTree.toGeneric().inorder() shouldBe wikiTreeLoose.inorder()
        wikiTree.toGeneric().preorder() shouldBe wikiTreeLoose.preorder()
        wikiTree.toGeneric().postorder() shouldBe wikiTreeLoose.postorder()

        slideShareTree.toGeneric().inorder() shouldBe slideShareTreeLoose.inorder()
        slideShareTree.toGeneric().preorder() shouldBe slideShareTreeLoose.preorder()
        slideShareTree.toGeneric().postorder() shouldBe slideShareTreeLoose.postorder()
    }

})