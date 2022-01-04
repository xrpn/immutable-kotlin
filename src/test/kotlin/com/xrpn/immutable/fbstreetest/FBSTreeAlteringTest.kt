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
        nul<Int, String>().finsertTkv(mEntry) shouldBe FBSTNode.of(false,mEntry)
        FBSTNode.of(false,mEntry).finsertTkv(lEntry) shouldBe depthOneLeft
        FBSTNode.of(false,mEntry).finsertTkv(nEntry) shouldBe depthOneRight

        depthOneLeft.finsertTkv(nEntry) shouldBe depthOneFull
        depthOneRight.finsertTkv(lEntry) shouldBe depthOneFull

        depthTwoLeftPartial.finsertTkv(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsertTkv(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsertTkv(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsertTkv(rEntry) shouldBe depthTwoRightLeft

        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry)).finsertTkv(pEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTUnique.empty, FBSTNode.of(false,pEntry)))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry)).finsertTkv(nEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTNode.of(false,nEntry), FBSTUnique.empty))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry), FBSTNode.of(false,zEntry)).finsertTkv(dEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTUnique.empty, FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry), FBSTNode.of(false,zEntry)).finsertTkv(bEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTNode.of(false,bEntry), FBSTUnique.empty), FBSTNode.of(false,zEntry))

        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry)).finsertTkv(pEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTUnique.empty, FBSTNode.of(false,pEntry)))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTUnique.empty, FBSTNode.of(false,pEntry))).finsertTkv(nEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,oEntry, FBSTNode.of(false,nEntry), FBSTNode.of(false,pEntry)))

        // --
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry), FBSTNode.of(false,zEntry)).finsertTkv(dEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTUnique.empty, FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry))
        FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTUnique.empty, FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry)).finsertTkv(bEntry) shouldBe
            FBSTNode.of(false,mEntry, FBSTNode.of(false,cEntry, FBSTNode.of(false,bEntry), FBSTNode.of(false,dEntry)), FBSTNode.of(false,zEntry))
    }

    test("finsertDup (A)") {
        com.xrpn.immutable.FBSTree.nul<Int, String>().finsertTkv(mEntry) shouldBe FBSTNode.of(true, mEntry)
        FBSTNode.of(true, mEntry).finsertTkv(mEntry) shouldBe FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry))
        FBSTNode.of(true, mEntry).finsertTkv(lEntry) shouldBe depthOneLeft
        FBSTNode.of(true, mEntry).finsertTkv(nEntry) shouldBe depthOneRight

        FBSTNode.of(true, mEntry).finsertTkv(mEntry) shouldBe FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry))
        depthOneLeft.finsertTkv(nEntry) shouldBe depthOneFull
        depthOneRight.finsertTkv(lEntry) shouldBe depthOneFull

        // dups right
        depthOneLeft.toGeneric().finsertTkv(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTNode.of(true, lEntry), FBSTNode.of(true, mEntry))
        depthOneLeft.toGeneric().finsertTkv(mEntry)
            .finsertTkv(mEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry),
                                 FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry))
                )
        depthOneLeft.toGeneric().finsertTkv(mEntry)
            .finsertTkv(mEntry)
                .finsertTkv(mEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry),
                                 FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry)))
                )

        // dups left
        depthOneLeft.toGeneric().finsertTkv(lEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry, FBSTGeneric.empty, FBSTNode.of(true, lEntry)),
                                 FBSTGeneric.empty
                )
        depthOneLeft.toGeneric().finsertTkv(lEntry)
            .finsertTkv(lEntry) shouldBe
                FBSTNode.of(true, 
                    mEntry, FBSTNode.of(true, lEntry, FBSTGeneric.empty, FBSTNode.of(true, lEntry, FBSTGeneric.empty, FBSTNode.of(true, lEntry))),
                                 FBSTGeneric.empty
                )
        depthOneLeft.toGeneric().finsertTkv(lEntry)
            .finsertTkv(lEntry)
                .finsertTkv(lEntry) shouldBe
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
        depthOneRight.toGeneric().finsertTkv(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, depthOneRight.toGeneric())
        depthOneRight.toGeneric().finsertTkv(mEntry)
            .finsertTkv(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, depthOneRight.toGeneric()))
        depthOneRight.toGeneric().finsertTkv(mEntry)
            .finsertTkv(mEntry)
                .finsertTkv(mEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, mEntry, FBSTGeneric.empty, depthOneRight.toGeneric())))

        // dups left
        depthOneRight.toGeneric().finsertTkv(nEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry)))
        depthOneRight.toGeneric().finsertTkv(nEntry)
            .finsertTkv(nEntry) shouldBe
                FBSTNode.of(true, mEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry, FBSTGeneric.empty, FBSTNode.of(true, nEntry))))
        depthOneRight.toGeneric().finsertTkv(nEntry)
            .finsertTkv(nEntry)
                .finsertTkv(nEntry) shouldBe
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

        depthTwoLeftPartial.finsertTkv(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsertTkv(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsertTkv(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsertTkv(rEntry) shouldBe depthTwoRightLeft

        depthTwoLeftRight.toGeneric().finsertTkv(nEntry) shouldBe
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
        depthTwoLeftRight.toGeneric().finsertTkv(lEntry) shouldBe
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
        depthTwoLeftRight.toGeneric().finsertTkv(mEntry) shouldBe
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
        depthTwoLeftRight.toGeneric().finsertTkv(sEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTGeneric.empty,
                        FBSTNode.of(true, mEntry)
                    ),
                    FBSTNode.of(true, sEntry, FBSTGeneric.empty, FBSTNode.of(true, sEntry))
                )

        depthTwoLeftLeft.toGeneric().finsertTkv(nEntry) shouldBe
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
        depthTwoLeftLeft.toGeneric().finsertTkv(lEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTNode.of(true, eEntry),
                        FBSTNode.of(true, lEntry)
                    ),
                    FBSTNode.of(true, sEntry)
                )
        depthTwoLeftLeft.toGeneric().finsertTkv(eEntry) shouldBe
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
        depthTwoLeftLeft.toGeneric().finsertTkv(sEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, 
                        lEntry,
                        FBSTNode.of(true, eEntry),
                        FBSTGeneric.empty
                    ),
                    FBSTNode.of(true, sEntry, FBSTGeneric.empty, FBSTNode.of(true, sEntry))
                )

        depthTwoRightRight.toGeneric().finsertTkv(nEntry) shouldBe
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
        depthTwoRightRight.toGeneric().finsertTkv(mEntry) shouldBe
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
        depthTwoRightRight.toGeneric().finsertTkv(sEntry) shouldBe
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
        depthTwoRightRight.toGeneric().finsertTkv(uEntry) shouldBe
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

        depthTwoRightLeft.toGeneric().finsertTkv(nEntry) shouldBe
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
        depthTwoRightLeft.toGeneric().finsertTkv(mEntry) shouldBe
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
        depthTwoRightLeft.toGeneric().finsertTkv(sEntry) shouldBe
                FBSTNode.of(true, 
                    nEntry,
                    FBSTNode.of(true, mEntry),
                    FBSTNode.of(true, 
                        sEntry,
                        FBSTNode.of(true, rEntry),
                        FBSTNode.of(true, sEntry)
                    )
                )
        depthTwoRightLeft.toGeneric().finsertTkv(rEntry) shouldBe
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
        depthTwoLeftPartial.finsertTkv(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsertTkv(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsertTkv(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsertTkv(rEntry) shouldBe depthTwoRightLeft

        (depthTwoLeftRight.finsertTkv(nEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftRight.finsertTkv(lEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftRight.finsertTkv(mEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftRight.finsertTkv(sEntry) === depthTwoLeftRight) shouldBe true
        (depthTwoLeftLeft.finsertTkv(nEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoLeftLeft.finsertTkv(lEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoLeftLeft.finsertTkv(eEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoLeftLeft.finsertTkv(sEntry) === depthTwoLeftLeft) shouldBe true
        (depthTwoRightRight.finsertTkv(nEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightRight.finsertTkv(mEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightRight.finsertTkv(sEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightRight.finsertTkv(uEntry) === depthTwoRightRight) shouldBe true
        (depthTwoRightLeft.finsertTkv(nEntry) === depthTwoRightLeft) shouldBe true
        (depthTwoRightLeft.finsertTkv(mEntry) === depthTwoRightLeft) shouldBe true
        (depthTwoRightLeft.finsertTkv(sEntry) === depthTwoRightLeft) shouldBe true
        (depthTwoRightLeft.finsertTkv(rEntry) === depthTwoRightLeft) shouldBe true
    }

    test("finserts, finsertt, finsertsDup (A)") {
        nul<Int, Int>().finsertTkvs(FLNil) shouldBe emptyIMBTree<Int, Int>()
        (nul<Int, Int>().finsertTkvs(FLNil) === emptyIMBTree<Int, Int>()) shouldBe true
        nul<Int, Int>().finsertTkvs(emptyIMBTree()) shouldBe emptyIMBTree<Int, Int>()
        (nul<Int, Int>().finsertTkvs(emptyIMBTree()) === emptyIMBTree<Int, Int>()) shouldBe true
    }

    test("finserts, finsertt, finsertsDup (B)") {
        Arb.flist<Int, Int>(Arb.int(-25, 25)).checkAll(repeats) { fl ->
            // TODO OOM_ERR val tab = ofvs(fl.asList().iterator())
            val flkv: FList<TKVEntry<String, Int>> = fl.fmap { it.toSAEntry() }
            val l = flkv.copyToMutableList()
            val s = l.toSet()
            nul<String, Int>().finsertTkvs(flkv).inorder().softEqual(s.sorted()) shouldBe true
            // TODO OOM_ERR nul<String, Int>().finsertTkvs(tab).inorder() shouldBe s.sorted()
            // TODO OOM_ERR tab.finsertTkvs(FRBTree.nul()).inorder() shouldBe s.sorted()
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