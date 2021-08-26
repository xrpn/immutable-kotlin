package com.xrpn.immutable

import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
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
        FBSTree.nul<Int, String>().finsert(mEntry) shouldBe FBSTNode(mEntry)
        FBSTNode(mEntry).finsert(lEntry) shouldBe depthOneLeft
        FBSTNode(mEntry).finsert(nEntry) shouldBe depthOneRight

        depthOneLeft.finsert(nEntry) shouldBe depthOneFull
        depthOneRight.finsert(lEntry) shouldBe depthOneFull

        depthTwoLeftPartial.finsert(mEntry) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsert(eEntry) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsert(uEntry) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsert(rEntry) shouldBe depthTwoRightLeft

        FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)).finsert(pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)).finsert(nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNil))
        FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)).finsert(dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)).finsert(bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNil), FBSTNode(zEntry))

        FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)).finsert(pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry))).finsert(nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNode(pEntry)))

        // --
        FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)).finsert(dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry)).finsert(bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNode(dEntry)), FBSTNode(zEntry))
    }

    test("finsertDup (A)") {
        FBSTree.nul<Int, String>().finsertDup(mEntry, allowDups = true) shouldBe FBSTNode(mEntry)
        FBSTNode(mEntry).finsertDup(mEntry, allowDups = true) shouldBe FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
        FBSTNode(mEntry).finsertDup(lEntry, allowDups = true) shouldBe depthOneLeft
        FBSTNode(mEntry).finsertDup(nEntry, allowDups = true) shouldBe depthOneRight

        FBSTNode(mEntry).finsertDup(mEntry, allowDups = true) shouldBe FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
        depthOneLeft.finsertDup(nEntry, allowDups = true) shouldBe depthOneFull
        depthOneRight.finsertDup(lEntry, allowDups = true) shouldBe depthOneFull

        // dups right
        depthOneLeft.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(mEntry))
        depthOneLeft.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNode(lEntry),
                                 FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry)))
        depthOneLeft.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true)
                .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNode(lEntry),
                                 FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))))

        // dups left
        depthOneLeft.finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry)),
                                 FBSTNil)
        depthOneLeft.finsertDup(lEntry, allowDups = true)
            .finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry))),
                                 FBSTNil)
        depthOneLeft.finsertDup(lEntry, allowDups = true)
            .finsertDup(lEntry, allowDups = true)
                .finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry, FBSTNil,
                                                                            FBSTNode(lEntry, FBSTNil,
                                                                                             FBSTNode(lEntry)))),
                                 FBSTNil)

        // dups right
        depthOneRight.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNil, depthOneRight)
        depthOneRight.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight))
        depthOneRight.finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true)
                .finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight)))

        // dups left
        depthOneRight.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNil, FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry)))
        depthOneRight.finsertDup(nEntry, allowDups = true)
            .finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNil, FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry))))
        depthOneRight.finsertDup(nEntry, allowDups = true)
            .finsertDup(nEntry, allowDups = true)
                .finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode(mEntry, FBSTNil,
                                 FBSTNode(nEntry, FBSTNil,
                                                  FBSTNode(nEntry, FBSTNil,
                                                                   FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry)))))
    }

    test("finsertDup (B)") {

        depthTwoLeftPartial.finsertDup(mEntry, allowDups = true) shouldBe depthTwoLeftRight
        depthTwoLeftPartial.finsertDup(eEntry, allowDups = true) shouldBe depthTwoLeftLeft
        depthTwoRightPartial.finsertDup(uEntry, allowDups = true) shouldBe depthTwoRightRight
        depthTwoRightPartial.finsertDup(rEntry, allowDups = true) shouldBe depthTwoRightLeft

        depthTwoLeftRight.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNil,
                        FBSTNode(mEntry)),
                    FBSTNode(nEntry,
                        FBSTNil,
                        FBSTNode(sEntry)))
        depthTwoLeftRight.finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNil,
                        FBSTNode(lEntry,
                            FBSTNil,
                            FBSTNode(mEntry))),
                    FBSTNode(sEntry))
        depthTwoLeftRight.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNil,
                        FBSTNode(mEntry,
                            FBSTNil,
                            FBSTNode(mEntry))),
                    FBSTNode(sEntry))
        depthTwoLeftRight.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNil,
                        FBSTNode(mEntry)),
                    FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        depthTwoLeftLeft.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNode(eEntry),
                        FBSTNil),
                    FBSTNode(nEntry,
                        FBSTNil,
                        FBSTNode(sEntry)))
        depthTwoLeftLeft.finsertDup(lEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNode(eEntry),
                        FBSTNode(lEntry)),
                    FBSTNode(sEntry))
        depthTwoLeftLeft.finsertDup(eEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNode(eEntry,
                            FBSTNil,
                            FBSTNode(eEntry)),
                        FBSTNil),
                    FBSTNode(sEntry))
        depthTwoLeftLeft.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(lEntry,
                        FBSTNode(eEntry),
                        FBSTNil),
                    FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        depthTwoRightRight.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry),
                    FBSTNode(nEntry,
                        FBSTNil,
                        FBSTNode(sEntry,
                            FBSTNil,
                            FBSTNode(uEntry))))
        depthTwoRightRight.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry,
                        FBSTNil,
                        FBSTNode(mEntry)),
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry)))
        depthTwoRightRight.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry),
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(sEntry,
                            FBSTNil,
                            FBSTNode(uEntry))))
        depthTwoRightRight.finsertDup(uEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry),
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry,
                            FBSTNil,
                            FBSTNode(uEntry))))

        depthTwoRightLeft.finsertDup(nEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry),
                    FBSTNode(nEntry,
                        FBSTNil,
                        FBSTNode(sEntry,
                            FBSTNode(rEntry),
                            FBSTNil)))
        depthTwoRightLeft.finsertDup(mEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry,
                        FBSTNil,
                        FBSTNode(mEntry)),
                    FBSTNode(sEntry,
                        FBSTNode(rEntry),
                        FBSTNil))
        depthTwoRightLeft.finsertDup(sEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry),
                    FBSTNode(sEntry,
                        FBSTNode(rEntry),
                        FBSTNode(sEntry)))
        depthTwoRightLeft.finsertDup(rEntry, allowDups = true) shouldBe
                FBSTNode(nEntry,
                    FBSTNode(mEntry),
                    FBSTNode(sEntry,
                        FBSTNode(rEntry,
                            FBSTNil,
                            FBSTNode(rEntry)),
                        FBSTNil))

    }

    test("finserts, finsertt, finsertsDup (A)") {
        FBSTree.nul<Int, Int>().finserts(FLNil).inorder() shouldBe FBSTree.emptyIMBTree<Int, Int>()
        FBSTree.nul<Int, Int>().finsertt(FBSTree.emptyIMBTree()).inorder() shouldBe FBSTree.emptyIMBTree<Int, Int>()
        FBSTree.nul<Int, Int>().finsertsDup(FLNil, allowDups = false) shouldBe FBSTree.emptyIMBTree<Int, Int>()
        FBSTree.nul<Int, Int>().finsertsDup(FLNil, allowDups = true) shouldBe FBSTree.emptyIMBTree<Int, Int>()
    }

    test("finserts, finsertt, finsertsDup (B)") {
        Arb.flist<Int, Int>(Arb.int(-25, 25)).checkAll(repeats) { fl ->
            val tab = FBSTree.ofvs(fl.iterator())
            val flkv: FList<TKVEntry<String, Int>> = fl.fmap { it.toSAEntry() }
            val l = flkv.copyToMutableList()
            val s = l.toSet()
            FBSTree.nul<String, Int>().finserts(flkv).inorder() shouldBe s.sorted()
            FBSTree.nul<String, Int>().finsertt(tab).inorder() shouldBe s.sorted()
            FBSTree.nul<String, Int>().finsertsDup(flkv, allowDups = false).inorder() shouldBe s.sorted()
            FBSTree.nul<String, Int>().finsertsDup(flkv, allowDups = true).inorder() shouldBe l.sorted()
        }
    }

})