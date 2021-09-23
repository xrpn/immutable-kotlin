package com.xrpn.immutable

internal val aEntry = TKVEntryK("A".hashCode(), "A")
internal val bEntry = TKVEntryK("B".hashCode(), "B")
internal val cEntry = TKVEntryK("C".hashCode(), "C")
internal val dEntry = TKVEntryK("D".hashCode(), "D")
internal val eEntry = TKVEntryK("E".hashCode(), "E")
internal val fEntry = TKVEntryK("F".hashCode(), "F")
internal val gEntry = TKVEntryK("G".hashCode(), "G")
internal val hEntry = TKVEntryK("H".hashCode(), "H")
internal val iEntry = TKVEntryK("I".hashCode(), "I")
internal val jEntry = TKVEntryK("J".hashCode(), "J")
internal val kEntry = TKVEntryK("K".hashCode(), "K")
internal val lEntry = TKVEntryK("L".hashCode(), "L")
internal val mEntry = TKVEntryK("M".hashCode(), "M")
internal val nEntry = TKVEntryK("N".hashCode(), "N")
internal val oEntry = TKVEntryK("O".hashCode(), "O")
internal val pEntry = TKVEntryK("P".hashCode(), "P")
internal val rEntry = TKVEntryK("R".hashCode(), "R")
internal val sEntry = TKVEntryK("S".hashCode(), "S")
internal val uEntry = TKVEntryK("U".hashCode(), "U")
internal val zEntry = TKVEntryK("Z".hashCode(), "Z")
internal val n44Entry = TKVEntryK(44, 44)
internal val n17Entry = TKVEntryK(17, 17)
internal val n32Entry = TKVEntryK(32, 32)
internal val n78Entry = TKVEntryK(78, 78)
internal val n50Entry = TKVEntryK(50, 50)
internal val n48Entry = TKVEntryK(48, 48)
internal val n62Entry = TKVEntryK(62, 62)
internal val n88Entry = TKVEntryK(88, 88)

internal val frbWikiInorder = FList.of(aEntry, bEntry, cEntry, dEntry, eEntry, fEntry, gEntry, hEntry, iEntry)
internal val frbWikiBreadthFirst = FList.of(dEntry, bEntry, hEntry, aEntry, cEntry, fEntry, iEntry, eEntry, gEntry)
internal val frbWikiTree = FRBTree.Companion.rbtInserts(FRBTNil, frbWikiInorder)
internal var rbWikiTree = RBTree.of(frbWikiInorder)
internal val frbWikiPreorder = frbWikiTree.preorder()
internal val frbWikiPostorder = frbWikiTree.postorder()

internal val frbSlideShareInorder = FList.of(n17Entry,n32Entry,n44Entry,n48Entry,n50Entry,n62Entry,n78Entry,n88Entry)
internal val frbSlideShareBreadthFirst = FList.of(n48Entry,n32Entry,n62Entry,n17Entry,n44Entry,n50Entry,n88Entry,n78Entry)
internal val frbSlideShareTree = FRBTree.Companion.rbtInserts(FRBTNil, frbSlideShareInorder)
internal var rbSlideShareTree = RBTree.of(frbSlideShareInorder)
internal val frbSlideSharePreorder = frbSlideShareTree.preorder()
internal val frbSlideSharePostorder: FList<TKVEntry<Int, Int>> = frbSlideShareTree.postorder()

val reverseIntCompare: Comparator<Int> = Comparator { p0, p1 ->
    when {
        p0!! == p1!! -> 0
        p0 < p1 -> 1
        else -> -1
    }
}

val intCompare: Comparator<Int> = Comparator { p0, p1 ->
    when {
        p0!! == p1!! -> 0
        p0 < p1 -> 1
        else -> -1
    }
}

val reverseStrCompare: Comparator<String> = Comparator { p0, p1 ->
    when {
        p0!! == p1!! -> 0
        p0 < p1 -> 1
        else -> -1
    }
}

val ttDepthOneRight: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            mEntry, FRBTree.BLACK,
            FRBTNil,
            FRBTree.frbtPartAssert(FRBTNode(nEntry, FRBTree.BLACK))
        )
    )

val frbDepthOneLeft: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            mEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode(lEntry)),
            FRBTNil
        )
    )

val frbDepthOneFull: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            mEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode(lEntry, FRBTree.BLACK)),
            FRBTree.frbtPartAssert(FRBTNode(nEntry, FRBTree.BLACK))
        )
    )

internal val frbDepthOneFullPreorder = FList.of(mEntry,lEntry,nEntry)
internal val frbDepthOneFullInorder = FList.of(lEntry,mEntry,nEntry)
internal val frbDepthOneFullPostorder = FList.of(lEntry,nEntry,mEntry)
internal val frbDepthOneFullBreadthFirst = FList.of(mEntry,lEntry,nEntry)

val ttDepthTwoLeftPartial: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode(lEntry)),
            FRBTree.frbtPartAssert(FRBTNode(sEntry))
        )
    )
val ttDepthTwoLeftRight: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(
                FRBTNode(
                    lEntry, FRBTree.BLACK,
                    FRBTNil,
                    FRBTree.frbtPartAssert(FRBTNode(mEntry))
                )
            ),
            FRBTree.frbtPartAssert(FRBTNode(sEntry, FRBTree.BLACK))
        )
    )
internal val ttDepthTwoLeftRightPreorder = FList.of(nEntry,lEntry,mEntry,sEntry)
internal val ttDepthTwoLeftRightInorder = FList.of(lEntry,mEntry,nEntry,sEntry)
internal val ttDepthTwoLeftRightPostorder = FList.of(mEntry,lEntry,sEntry,nEntry)
internal val ttDepthTwoLeftRightBreadthFirst = FList.of(nEntry,lEntry,sEntry,mEntry)

val frbDepthTwoLeftLeft: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(
                FRBTNode(
                    lEntry, FRBTree.BLACK,
                    FRBTree.frbtPartAssert(FRBTNode(eEntry)),
                    FRBTNil
                )
            ),
            FRBTree.frbtPartAssert(FRBTNode(sEntry, FRBTree.BLACK))
        )
    )
internal val frbDepthTwoLeftLeftPreorder = FList.of(nEntry,lEntry,eEntry,sEntry)
internal val frbDepthTwoLeftLeftInorder = FList.of(eEntry,lEntry,nEntry,sEntry)
internal val frbDepthTwoLeftLeftPostorder = FList.of(eEntry,lEntry,sEntry,nEntry)
internal val frbDepthTwoLeftLeftBreadthFirst = FList.of(nEntry,lEntry,sEntry,eEntry)

val ttDepthTwoRightPartial: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode(mEntry)),
            FRBTree.frbtPartAssert(FRBTNode(sEntry))
        )
    )
val frbDepthTwoRightRight: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode(mEntry, FRBTree.BLACK)),
            FRBTree.frbtPartAssert(
                FRBTNode(
                    sEntry, FRBTree.BLACK,
                    FRBTNil,
                    FRBTree.frbtPartAssert(FRBTNode(uEntry))
                )
            )
        )
    )
internal val frbDepthTwoRightRightPreorder = FList.of(nEntry,mEntry,sEntry,uEntry)
internal val frbDepthTwoRightRightInorder = FList.of(mEntry,nEntry,sEntry,uEntry)
internal val frbDepthTwoRightRightPostorder = FList.of(mEntry,uEntry,sEntry,nEntry)
internal val frbDepthTwoRightRightBreadthFirst = FList.of(nEntry,mEntry,sEntry,uEntry)

val frbDepthTwoRightLeft: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode(mEntry, FRBTree.BLACK)),
            FRBTree.frbtPartAssert(
                FRBTNode(
                    sEntry, FRBTree.BLACK,
                    FRBTree.frbtPartAssert(FRBTNode(rEntry)),
                    FRBTNil
                )
            )
        )
    )
internal val frbDepthTwoRightLeftPreorder = FList.of(nEntry,mEntry,sEntry,rEntry)
internal val frbDepthTwoRightLeftInorder = FList.of(mEntry,nEntry,rEntry,sEntry)
internal val frbDepthTwoRightLeftPostorder = FList.of(mEntry,rEntry,sEntry,nEntry)
internal val frbDepthTwoRightLeftBreadthFirst = FList.of(nEntry,mEntry,sEntry,rEntry)

// ======================================

// https://en.wikipedia.org/wiki/Tree_traversal
val wikiTree: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            fEntry,
            FBSTree.fbtAssert(
                FBSTNode(
                    bEntry,
                    FBSTree.fbtAssert(FBSTNode(aEntry)),
                    FBSTree.fbtAssert(
                        FBSTNode(
                            dEntry,
                            FBSTree.fbtAssert(FBSTNode(cEntry)),
                            FBSTree.fbtAssert(FBSTNode(eEntry))
                        )
                    ),
                )
            ),
            FBSTree.fbtAssert(
                FBSTNode(
                    gEntry,
                    FBSTNil,
                    FBSTree.fbtAssert(
                        FBSTNode(
                            iEntry,
                            FBSTree.fbtAssert(FBSTNode(hEntry)),
                            FBSTNil,
                        )
                    )
                )
            )
        )
    )
internal val wikiPreorder = FList.of(fEntry,bEntry,aEntry,dEntry,cEntry,eEntry,gEntry,iEntry,hEntry)
internal val wikiInorder = FList.of(aEntry,bEntry,cEntry,dEntry,eEntry,fEntry,gEntry,hEntry,iEntry)
internal val wikiPostorder = FList.of( aEntry,cEntry,eEntry,dEntry,bEntry,hEntry,iEntry,gEntry,fEntry)

// https://www.slideshare.net/ERPunitJain/binary-search-tree-472n88Entry612
val slideShareTree: FBSTree<Int, Int> =
    FBSTree.fbtAssert(
        FBSTNode(
            n44Entry,
            FBSTree.fbtAssert(
                FBSTNode(
                    n17Entry,
                    FBSTNil,
                    FBSTree.fbtAssert(FBSTNode(n32Entry))
                )
            ),
            FBSTree.fbtAssert(
                FBSTNode(
                    n78Entry,
                    FBSTree.fbtAssert(
                        FBSTNode(
                            n50Entry,
                            FBSTree.fbtAssert(FBSTNode(n48Entry)),
                            FBSTree.fbtAssert(FBSTNode(n62Entry))
                        )
                    ),
                    FBSTree.fbtAssert(FBSTNode(n88Entry))
                )
            )
        )
    )
internal val slideSharePreorder = FList.of(n44Entry,n17Entry,n32Entry,n78Entry,n50Entry,n48Entry,n62Entry,n88Entry)
internal val slideShareInorder = FList.of(n17Entry,n32Entry,n44Entry,n48Entry,n50Entry,n62Entry,n78Entry,n88Entry)
internal val slideSharePostorder = FList.of(n32Entry,n17Entry,n48Entry,n62Entry,n50Entry,n88Entry,n78Entry,n44Entry)
internal val slideShareBreadthFirst = FList.of(n44Entry,n17Entry,n78Entry,n32Entry,n50Entry,n88Entry,n48Entry,n62Entry)

val depthOneRight: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            mEntry,
            FBSTNil,
            FBSTree.fbtAssert(FBSTNode(nEntry))
        )
    )

val depthOneLeft: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            mEntry,
            FBSTree.fbtAssert(FBSTNode(lEntry)),
            FBSTNil
        )
    )

val depthOneFull: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            mEntry,
            FBSTree.fbtAssert(FBSTNode(lEntry)),
            FBSTree.fbtAssert(FBSTNode(nEntry))
        )
    )
internal val depthOneFullPreorder = FList.of(mEntry,lEntry,nEntry)
internal val depthOneFullInorder = FList.of(lEntry,mEntry,nEntry)
internal val depthOneFullPostorder = FList.of(lEntry,nEntry,mEntry)
internal val depthOneFullBreadthFirst = FList.of(mEntry,lEntry,nEntry)

val depthTwoLeftPartial: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            nEntry,
            FBSTree.fbtAssert(FBSTNode(lEntry)),
            FBSTree.fbtAssert(FBSTNode(sEntry))
        )
    )
val depthTwoLeftRight: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            nEntry,
            FBSTree.fbtAssert(
                FBSTNode(
                    lEntry,
                    FBSTNil,
                    FBSTree.fbtAssert(FBSTNode(mEntry))
                )
            ),
            FBSTree.fbtAssert(FBSTNode(sEntry))
        )
    )
internal val depthTwoLeftRightPreorder = FList.of(nEntry,lEntry,mEntry,sEntry)
internal val depthTwoLeftRightInorder = FList.of(lEntry,mEntry,nEntry,sEntry)
internal val depthTwoLeftRightPostorder = FList.of(mEntry,lEntry,sEntry,nEntry)
internal val depthTwoLeftRightBreadthFirst = FList.of(nEntry,lEntry,sEntry,mEntry)

val depthTwoLeftLeft: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            nEntry,
            FBSTree.fbtAssert(
                FBSTNode(
                    lEntry,
                    FBSTree.fbtAssert(FBSTNode(eEntry)),
                    FBSTNil
                )
            ),
            FBSTree.fbtAssert(FBSTNode(sEntry))
        )
    )
internal val depthTwoLeftLeftPreorder = FList.of(nEntry,lEntry,eEntry,sEntry)
internal val depthTwoLeftLeftInorder = FList.of(eEntry,lEntry,nEntry,sEntry)
internal val depthTwoLeftLeftPostorder = FList.of(eEntry,lEntry,sEntry,nEntry)
internal val depthTwoLeftLeftBreadthFirst = FList.of(nEntry,lEntry,sEntry,eEntry)

val depthTwoRightPartial: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            nEntry,
            FBSTree.fbtAssert(FBSTNode(mEntry)),
            FBSTree.fbtAssert(FBSTNode(sEntry))
        )
    )
val depthTwoRightRight: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            nEntry,
            FBSTree.fbtAssert(FBSTNode(mEntry)),
            FBSTree.fbtAssert(
                FBSTNode(
                    sEntry,
                    FBSTNil,
                    FBSTree.fbtAssert(FBSTNode(uEntry))
                )
            )
        )
    )
internal val depthTwoRightRightPreorder = FList.of(nEntry,mEntry,sEntry,uEntry)
internal val depthTwoRightRightInorder = FList.of(mEntry,nEntry,sEntry,uEntry)
internal val depthTwoRightRightPostorder = FList.of(mEntry,uEntry,sEntry,nEntry)
internal val depthTwoRightRightBreadthFirst = FList.of(nEntry,mEntry,sEntry,uEntry)

val depthTwoRightLeft: FBSTree<Int, String> =
    FBSTree.fbtAssert(
        FBSTNode(
            nEntry,
            FBSTree.fbtAssert(FBSTNode(mEntry)),
            FBSTree.fbtAssert(
                FBSTNode(
                    sEntry,
                    FBSTree.fbtAssert(FBSTNode(rEntry)),
                    FBSTNil
                )
            )
        )
    )
internal val depthTwoRightLeftPreorder = FList.of(nEntry,mEntry,sEntry,rEntry)
internal val depthTwoRightLeftInorder = FList.of(mEntry,nEntry,rEntry,sEntry)
internal val depthTwoRightLeftPostorder = FList.of(mEntry,rEntry,sEntry,nEntry)
internal val depthTwoRightLeftBreadthFirst = FList.of(nEntry,mEntry,sEntry,rEntry)