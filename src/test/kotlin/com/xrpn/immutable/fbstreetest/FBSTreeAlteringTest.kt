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
        nul<Int, String>().finsert(mEntry) shouldBe FBSTNode.of(mEntry)
        FBSTNode.of(mEntry).finsert(lEntry) shouldBe depthOneLeft
        FBSTNode.of(mEntry).finsert(nEntry) shouldBe depthOneRight

        depthOneLeft.finsert(nEntry) shouldBe depthOneFull
        depthOneRight.finsert(lEntry) shouldBe depthOneFull

        depthTwoLeftPartial.finsert(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsert(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsert(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsert(rEntry) shouldBe depthTwoRightLeft

        FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry)).finsert(pEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry, FBSTNil, FBSTNode.of(pEntry)))
        FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry)).finsert(nEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry, FBSTNode.of(nEntry), FBSTNil))
        FBSTNode.of(mEntry, FBSTNode.of(cEntry), FBSTNode.of(zEntry)).finsert(dEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(cEntry, FBSTNil, FBSTNode.of(dEntry)), FBSTNode.of(zEntry))
        FBSTNode.of(mEntry, FBSTNode.of(cEntry), FBSTNode.of(zEntry)).finsert(bEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(cEntry, FBSTNode.of(bEntry), FBSTNil), FBSTNode.of(zEntry))

        FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry)).finsert(pEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry, FBSTNil, FBSTNode.of(pEntry)))
        FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry, FBSTNil, FBSTNode.of(pEntry))).finsert(nEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(oEntry, FBSTNode.of(nEntry), FBSTNode.of(pEntry)))

        // --
        FBSTNode.of(mEntry, FBSTNode.of(cEntry), FBSTNode.of(zEntry)).finsert(dEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(cEntry, FBSTNil, FBSTNode.of(dEntry)), FBSTNode.of(zEntry))
        FBSTNode.of(mEntry, FBSTNode.of(cEntry, FBSTNil, FBSTNode.of(dEntry)), FBSTNode.of(zEntry)).finsert(bEntry) shouldBe
            FBSTNode.of(mEntry, FBSTNode.of(cEntry, FBSTNode.of(bEntry), FBSTNode.of(dEntry)), FBSTNode.of(zEntry))
    }

    test("finsertDup (A)") {
        com.xrpn.immutable.FBSTree.nul<Int, String>().finsertDup(mEntry, allowDups = true) shouldBe FBSTNode.of(mEntry)
        FBSTNode.of(mEntry).finsertDup(mEntry, allowDups = true) shouldBe FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry))
        FBSTNode.of(mEntry).finsertDup(lEntry, allowDups = true) shouldBe depthOneLeft
        FBSTNode.of(mEntry).finsertDup(nEntry, allowDups = true) shouldBe depthOneRight

        FBSTNode.of(mEntry).finsertDup(mEntry, allowDups = true) shouldBe FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry))
        depthOneLeft.finsertDup(nEntry, allowDups = true) shouldBe depthOneFull
        depthOneRight.finsertDup(lEntry, allowDups = true) shouldBe depthOneFull

        // dups right
        depthOneLeft.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(mEntry))
        depthOneLeft.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    mEntry, FBSTNode.of(lEntry),
                                 FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry))
                )
        depthOneLeft.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true)
                .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    mEntry, FBSTNode.of(lEntry),
                                 FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry)))
                )

        // dups left
        depthOneLeft.finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    mEntry, FBSTNode.of(lEntry, FBSTNil, FBSTNode.of(lEntry)),
                                 FBSTNil
                )
        depthOneLeft.finsertDup(lEntry, allowDups = true)
            .finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    mEntry, FBSTNode.of(lEntry, FBSTNil, FBSTNode.of(lEntry, FBSTNil, FBSTNode.of(lEntry))),
                                 FBSTNil
                )
        depthOneLeft.finsertDup(lEntry, allowDups = true)
            .finsertDup(lEntry, allowDups = true)
                .finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    mEntry, FBSTNode.of(
                        lEntry, FBSTNil, FBSTNode.of(
                            lEntry, FBSTNil,
                                                                            FBSTNode.of(
                                                                                lEntry, FBSTNil,
                                                                                             FBSTNode.of(lEntry)
                                                                            )
                        )
                    ),
                                 FBSTNil
                )

        // dups right
        depthOneRight.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(mEntry, FBSTNil, depthOneRight)
        depthOneRight.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry, FBSTNil, depthOneRight))
        depthOneRight.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true)
                .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(mEntry, FBSTNil, depthOneRight)))

        // dups left
        depthOneRight.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(nEntry, FBSTNil, FBSTNode.of(nEntry)))
        depthOneRight.finsertDup(nEntry, allowDups = true)
            .finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode.of(mEntry, FBSTNil, FBSTNode.of(nEntry, FBSTNil, FBSTNode.of(nEntry, FBSTNil, FBSTNode.of(nEntry))))
        depthOneRight.finsertDup(nEntry, allowDups = true)
            .finsertDup(nEntry, allowDups = true)
                .finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    mEntry, FBSTNil,
                                 FBSTNode.of(
                                     nEntry, FBSTNil,
                                                  FBSTNode.of(
                                                      nEntry, FBSTNil,
                                                                   FBSTNode.of(nEntry, FBSTNil, FBSTNode.of(nEntry))
                                                  )
                                 )
                )
    }

    test("finsertDup (B)") {

        depthTwoLeftPartial.finsertDup(mEntry, allowDups = true) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsertDup(eEntry, allowDups = true) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsertDup(uEntry, allowDups = true) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsertDup(rEntry, allowDups = true) shouldBe depthTwoRightLeft

        depthTwoLeftRight.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNil,
                        FBSTNode.of(mEntry)
                    ),
                    FBSTNode.of(
                        nEntry,
                        FBSTNil,
                        FBSTNode.of(sEntry)
                    )
                )
        depthTwoLeftRight.finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNil,
                        FBSTNode.of(
                            lEntry,
                            FBSTNil,
                            FBSTNode.of(mEntry)
                        )
                    ),
                    FBSTNode.of(sEntry)
                )
        depthTwoLeftRight.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNil,
                        FBSTNode.of(
                            mEntry,
                            FBSTNil,
                            FBSTNode.of(mEntry)
                        )
                    ),
                    FBSTNode.of(sEntry)
                )
        depthTwoLeftRight.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNil,
                        FBSTNode.of(mEntry)
                    ),
                    FBSTNode.of(sEntry, FBSTNil, FBSTNode.of(sEntry))
                )

        depthTwoLeftLeft.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNode.of(eEntry),
                        FBSTNil
                    ),
                    FBSTNode.of(
                        nEntry,
                        FBSTNil,
                        FBSTNode.of(sEntry)
                    )
                )
        depthTwoLeftLeft.finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNode.of(eEntry),
                        FBSTNode.of(lEntry)
                    ),
                    FBSTNode.of(sEntry)
                )
        depthTwoLeftLeft.finsertDup(eEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNode.of(
                            eEntry,
                            FBSTNil,
                            FBSTNode.of(eEntry)
                        ),
                        FBSTNil
                    ),
                    FBSTNode.of(sEntry)
                )
        depthTwoLeftLeft.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        lEntry,
                        FBSTNode.of(eEntry),
                        FBSTNil
                    ),
                    FBSTNode.of(sEntry, FBSTNil, FBSTNode.of(sEntry))
                )

        depthTwoRightRight.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(mEntry),
                    FBSTNode.of(
                        nEntry,
                        FBSTNil,
                        FBSTNode.of(
                            sEntry,
                            FBSTNil,
                            FBSTNode.of(uEntry)
                        )
                    )
                )
        depthTwoRightRight.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        mEntry,
                        FBSTNil,
                        FBSTNode.of(mEntry)
                    ),
                    FBSTNode.of(
                        sEntry,
                        FBSTNil,
                        FBSTNode.of(uEntry)
                    )
                )
        depthTwoRightRight.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(mEntry),
                    FBSTNode.of(
                        sEntry,
                        FBSTNil,
                        FBSTNode.of(
                            sEntry,
                            FBSTNil,
                            FBSTNode.of(uEntry)
                        )
                    )
                )
        depthTwoRightRight.finsertDup(uEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(mEntry),
                    FBSTNode.of(
                        sEntry,
                        FBSTNil,
                        FBSTNode.of(
                            uEntry,
                            FBSTNil,
                            FBSTNode.of(uEntry)
                        )
                    )
                )

        depthTwoRightLeft.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(mEntry),
                    FBSTNode.of(
                        nEntry,
                        FBSTNil,
                        FBSTNode.of(
                            sEntry,
                            FBSTNode.of(rEntry),
                            FBSTNil
                        )
                    )
                )
        depthTwoRightLeft.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(
                        mEntry,
                        FBSTNil,
                        FBSTNode.of(mEntry)
                    ),
                    FBSTNode.of(
                        sEntry,
                        FBSTNode.of(rEntry),
                        FBSTNil
                    )
                )
        depthTwoRightLeft.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(mEntry),
                    FBSTNode.of(
                        sEntry,
                        FBSTNode.of(rEntry),
                        FBSTNode.of(sEntry)
                    )
                )
        depthTwoRightLeft.finsertDup(rEntry, allowDups = true) shouldBe
                FBSTNode.of(
                    nEntry,
                    FBSTNode.of(mEntry),
                    FBSTNode.of(
                        sEntry,
                        FBSTNode.of(
                            rEntry,
                            FBSTNil,
                            FBSTNode.of(rEntry)
                        ),
                        FBSTNil
                    )
                )

    }

    test("finserts, finsertt, finsertsDup (A)") {
        nul<Int, Int>().finserts(FLNil).inorder() shouldBe emptyIMBTree<Int, Int>()
        nul<Int, Int>().finsertt(emptyIMBTree()).inorder() shouldBe emptyIMBTree<Int, Int>()
        nul<Int, Int>().finsertsDup(FLNil, allowDups = false) shouldBe emptyIMBTree<Int, Int>()
        nul<Int, Int>().finsertsDup(FLNil, allowDups = true) shouldBe emptyIMBTree<Int, Int>()
    }

    test("finserts, finsertt, finsertsDup (B)") {
        Arb.flist<Int, Int>(Arb.int(-25, 25)).checkAll(repeats) { fl ->
            val tab = ofvs(fl.iterator())
            val flkv: FList<TKVEntry<String, Int>> = fl.fmap { it.toSAEntry() }
            val l = flkv.copyToMutableList()
            val s = l.toSet()
            nul<String, Int>().finserts(flkv).inorder() shouldBe s.sorted()
            nul<String, Int>().finsertt(tab).inorder() shouldBe s.sorted()
            tab.finsertt(FRBTree.nul()).inorder() shouldBe s.sorted()
            nul<String, Int>().finsertsDup(flkv, allowDups = false).inorder() shouldBe s.sorted()
            nul<String, Int>().finsertsDup(flkv, allowDups = true).inorder() shouldBe l.sorted()
        }
    }

})