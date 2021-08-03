package com.xrpn.immutable

val aEntry: TKVEntry<Int, String> = TKVEntryK("A".hashCode(), "A")
val bEntry: TKVEntry<Int, String> = TKVEntryK("B".hashCode(), "B")
val cEntry: TKVEntry<Int, String> = TKVEntryK("C".hashCode(), "C")
val dEntry: TKVEntry<Int, String> = TKVEntryK("D".hashCode(), "D")
val eEntry: TKVEntry<Int, String> = TKVEntryK("E".hashCode(), "E")
val fEntry: TKVEntry<Int, String> = TKVEntryK("F".hashCode(), "F")
val gEntry: TKVEntry<Int, String> = TKVEntryK("G".hashCode(), "G")
val hEntry: TKVEntry<Int, String> = TKVEntryK("H".hashCode(), "H")
val iEntry: TKVEntry<Int, String> = TKVEntryK("I".hashCode(), "I")
val jEntry: TKVEntry<Int, String> = TKVEntryK("J".hashCode(), "J")
val kEntry: TKVEntry<Int, String> = TKVEntryK("K".hashCode(), "K")
val lEntry: TKVEntry<Int, String> = TKVEntryK("L".hashCode(), "L")
val mEntry: TKVEntry<Int, String> = TKVEntryK("M".hashCode(), "M")
val nEntry: TKVEntry<Int, String> = TKVEntryK("N".hashCode(), "N")
val oEntry: TKVEntry<Int, String> = TKVEntryK("O".hashCode(), "O")
val pEntry: TKVEntry<Int, String> = TKVEntryK("P".hashCode(), "P")
val rEntry: TKVEntry<Int, String> = TKVEntryK("R".hashCode(), "R")
val sEntry: TKVEntry<Int, String> = TKVEntryK("S".hashCode(), "S")
val uEntry: TKVEntry<Int, String> = TKVEntryK("U".hashCode(), "U")
val zEntry: TKVEntry<Int, String> = TKVEntryK("Z".hashCode(), "Z")
val n44Entry: TKVEntry<Int, Int> = TKVEntryK(44, 44)
val n17Entry: TKVEntry<Int, Int> = TKVEntryK(17, 17)
val n32Entry: TKVEntry<Int, Int> = TKVEntryK(32, 32)
val n78Entry: TKVEntry<Int, Int> = TKVEntryK(78, 78)
val n50Entry: TKVEntry<Int, Int> = TKVEntryK(50, 50)
val n48Entry: TKVEntry<Int, Int> = TKVEntryK(48, 48)
val n62Entry: TKVEntry<Int, Int> = TKVEntryK(62, 62)
val n88Entry: TKVEntry<Int, Int> = TKVEntryK(88, 88)

val frbWikiInorder: FList<TKVEntry<Int, String>> = FList.of(aEntry, bEntry, cEntry, dEntry, eEntry, fEntry, gEntry, hEntry, iEntry)
val frbWikiBreadthFirst: FList<TKVEntry<Int, String>> = FList.of(dEntry, bEntry, hEntry, aEntry, cEntry, fEntry, iEntry, eEntry, gEntry)
val frbWikiTree = FRBTree.Companion.inserts(FRBTNil, frbWikiInorder)
var rbWikiTree = RBTree.of(frbWikiInorder)
val frbWikiPreorder = frbWikiTree.preorder()
val frbWikiPostorder = frbWikiTree.postorder()

val frbSlideShareInorder: FList<TKVEntry<Int, Int>> = FList.of(n17Entry,n32Entry,n44Entry,n48Entry,n50Entry,n62Entry,n78Entry,n88Entry)
val frbSlideShareBreadthFirst: FList<TKVEntry<Int, Int>> = FList.of(n48Entry,n32Entry,n62Entry,n17Entry,n44Entry,n50Entry,n88Entry,n78Entry)
val frbSlideShareTree = FRBTree.Companion.inserts(FRBTNil, frbSlideShareInorder)
var rbSlideShareTree = RBTree.of(frbSlideShareInorder)
val frbSlideSharePreorder = frbSlideShareTree.preorder()
val frbSlideSharePostorder = frbSlideShareTree.postorder()

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

val frbDepthOneFullPreorder = FList.of(mEntry,lEntry,nEntry)
val frbDepthOneFullInorder = FList.of(lEntry,mEntry,nEntry)
val frbDepthOneFullPostorder = FList.of(lEntry,nEntry,mEntry)
val frbDepthOneFullBreadthFirst = FList.of(mEntry,lEntry,nEntry)

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
val ttDepthTwoLeftRightPreorder = FList.of(nEntry,lEntry,mEntry,sEntry)
val ttDepthTwoLeftRightInorder = FList.of(lEntry,mEntry,nEntry,sEntry)
val ttDepthTwoLeftRightPostorder = FList.of(mEntry,lEntry,sEntry,nEntry)
val ttDepthTwoLeftRightBreadthFirst = FList.of(nEntry,lEntry,sEntry,mEntry)

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
val frbDepthTwoLeftLeftPreorder = FList.of(nEntry,lEntry,eEntry,sEntry)
val frbDepthTwoLeftLeftInorder = FList.of(eEntry,lEntry,nEntry,sEntry)
val frbDepthTwoLeftLeftPostorder = FList.of(eEntry,lEntry,sEntry,nEntry)
val frbDepthTwoLeftLeftBreadthFirst = FList.of(nEntry,lEntry,sEntry,eEntry)

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
val frbDepthTwoRightRightPreorder = FList.of(nEntry,mEntry,sEntry,uEntry)
val frbDepthTwoRightRightInorder = FList.of(mEntry,nEntry,sEntry,uEntry)
val frbDepthTwoRightRightPostorder = FList.of(mEntry,uEntry,sEntry,nEntry)
val frbDepthTwoRightRightBreadthFirst = FList.of(nEntry,mEntry,sEntry,uEntry)

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
val frbDepthTwoRightLeftPreorder = FList.of(nEntry,mEntry,sEntry,rEntry)
val frbDepthTwoRightLeftInorder = FList.of(mEntry,nEntry,rEntry,sEntry)
val frbDepthTwoRightLeftPostorder = FList.of(mEntry,rEntry,sEntry,nEntry)
val frbDepthTwoRightLeftBreadthFirst = FList.of(nEntry,mEntry,sEntry,rEntry)

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
val wikiPreorder = FList.of(fEntry,bEntry,aEntry,dEntry,cEntry,eEntry,gEntry,iEntry,hEntry)
val wikiInorder = FList.of(aEntry,bEntry,cEntry,dEntry,eEntry,fEntry,gEntry,hEntry,iEntry)
val wikiPostorder = FList.of( aEntry,cEntry,eEntry,dEntry,bEntry,hEntry,iEntry,gEntry,fEntry)

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
val slideSharePreorder = FList.of(n44Entry,n17Entry,n32Entry,n78Entry,n50Entry,n48Entry,n62Entry,n88Entry)
val slideShareInorder = FList.of(n17Entry,n32Entry,n44Entry,n48Entry,n50Entry,n62Entry,n78Entry,n88Entry)
val slideSharePostorder = FList.of(n32Entry,n17Entry,n48Entry,n62Entry,n50Entry,n88Entry,n78Entry,n44Entry)
val slideShareBreadthFirst = FList.of(n44Entry,n17Entry,n78Entry,n32Entry,n50Entry,n88Entry,n48Entry,n62Entry)

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
val depthOneFullPreorder = FList.of(mEntry,lEntry,nEntry)
val depthOneFullInorder = FList.of(lEntry,mEntry,nEntry)
val depthOneFullPostorder = FList.of(lEntry,nEntry,mEntry)
val depthOneFullBreadthFirst = FList.of(mEntry,lEntry,nEntry)

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
val depthTwoLeftRightPreorder = FList.of(nEntry,lEntry,mEntry,sEntry)
val depthTwoLeftRightInorder = FList.of(lEntry,mEntry,nEntry,sEntry)
val depthTwoLeftRightPostorder = FList.of(mEntry,lEntry,sEntry,nEntry)
val depthTwoLeftRightBreadthFirst = FList.of(nEntry,lEntry,sEntry,mEntry)

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
val depthTwoLeftLeftPreorder = FList.of(nEntry,lEntry,eEntry,sEntry)
val depthTwoLeftLeftInorder = FList.of(eEntry,lEntry,nEntry,sEntry)
val depthTwoLeftLeftPostorder = FList.of(eEntry,lEntry,sEntry,nEntry)
val depthTwoLeftLeftBreadthFirst = FList.of(nEntry,lEntry,sEntry,eEntry)

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
val depthTwoRightRightPreorder = FList.of(nEntry,mEntry,sEntry,uEntry)
val depthTwoRightRightInorder = FList.of(mEntry,nEntry,sEntry,uEntry)
val depthTwoRightRightPostorder = FList.of(mEntry,uEntry,sEntry,nEntry)
val depthTwoRightRightBreadthFirst = FList.of(nEntry,mEntry,sEntry,uEntry)

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
val depthTwoRightLeftPreorder = FList.of(nEntry,mEntry,sEntry,rEntry)
val depthTwoRightLeftInorder = FList.of(mEntry,nEntry,rEntry,sEntry)
val depthTwoRightLeftPostorder = FList.of(mEntry,rEntry,sEntry,nEntry)
val depthTwoRightLeftBreadthFirst = FList.of(nEntry,mEntry,sEntry,rEntry)