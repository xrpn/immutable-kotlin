package com.xrpn.immutable

import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.SymKeyType
import com.xrpn.immutable.TKVEntry.Companion.toKKEntry
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

internal val emptyArrayOfInt: Array<Int> = arrayOf()
internal val emptyArrayOfStr: Array<String> = arrayOf()
internal val emptyArrayOfLong: Array<Long> = arrayOf()
internal val emptyArrayOfCoPA: Array<CoPa<Int,String>> = arrayOf()

internal val aEntry = TKVEntry.ofkv("A".hashCode(), "A")
internal val bEntry = TKVEntry.ofkv("B".hashCode(), "B")
internal val cEntry = TKVEntry.ofkv("C".hashCode(), "C")
internal val dEntry = TKVEntry.ofkv("D".hashCode(), "D")
internal val eEntry = TKVEntry.ofkv("E".hashCode(), "E")
internal val fEntry = TKVEntry.ofkv("F".hashCode(), "F")
internal val gEntry = TKVEntry.ofkv("G".hashCode(), "G")
internal val hEntry = TKVEntry.ofkv("H".hashCode(), "H")
internal val iEntry = TKVEntry.ofkv("I".hashCode(), "I")
internal val jEntry = TKVEntry.ofkv("J".hashCode(), "J")
internal val kEntry = TKVEntry.ofkv("K".hashCode(), "K")
internal val lEntry = TKVEntry.ofkv("L".hashCode(), "L")
internal val mEntry = TKVEntry.ofkv("M".hashCode(), "M")
internal val nEntry = TKVEntry.ofkv("N".hashCode(), "N")
internal val oEntry = TKVEntry.ofkv("O".hashCode(), "O")
internal val pEntry = TKVEntry.ofkv("P".hashCode(), "P")
internal val rEntry = TKVEntry.ofkv("R".hashCode(), "R")
internal val sEntry = TKVEntry.ofkv("S".hashCode(), "S")
internal val uEntry = TKVEntry.ofkv("U".hashCode(), "U")
internal val zEntry = TKVEntry.ofkv("Z".hashCode(), "Z")
internal val n44Entry = TKVEntry.ofkv(44, 44)
internal val n17Entry = TKVEntry.ofkv(17, 17)
internal val n32Entry = TKVEntry.ofkv(32, 32)
internal val n78Entry = TKVEntry.ofkv(78, 78)
internal val n50Entry = TKVEntry.ofkv(50, 50)
internal val n48Entry = TKVEntry.ofkv(48, 48)
internal val n62Entry = TKVEntry.ofkv(62, 62)
internal val n88Entry = TKVEntry.ofkv(88, 88)

internal val frbWikiInorder = FList.of(aEntry, bEntry, cEntry, dEntry, eEntry, fEntry, gEntry, hEntry, iEntry)
internal val frbWikiBreadthFirst = FList.of(dEntry, bEntry, hEntry, aEntry, cEntry, fEntry, iEntry, eEntry, gEntry)
internal val frbWikiTree: FRBTree<Int, String> = FRBTree.Companion.rbtInserts(FRBTNil, frbWikiInorder)
internal var rbWikiTree = RBTree.of(frbWikiInorder)
internal val frbWikiPreorder = frbWikiTree.preorder()
internal val frbWikiPostorder = frbWikiTree.postorder()

internal val frbSlideShareInorder = FList.of(n17Entry,n32Entry,n44Entry,n48Entry,n50Entry,n62Entry,n78Entry,n88Entry)
internal val frbSlideShareBreadthFirst = FList.of(n48Entry,n32Entry,n62Entry,n17Entry,n44Entry,n50Entry,n88Entry,n78Entry)
internal val frbSlideShareTree: FRBTree<Int, Int> = FRBTree.Companion.rbtInserts(FRBTNil, frbSlideShareInorder)
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
        FRBTNode.of(
            mEntry, FRBTree.BLACK,
            FRBTNil,
            FRBTree.frbtPartAssert(FRBTNode.of(nEntry, FRBTree.BLACK))
        )
    )

val frbDepthOneLeft: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode.of(
            mEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode.of(lEntry)),
            FRBTNil
        )
    )

val frbDepthOneFull: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode.of(
            mEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode.of(lEntry, FRBTree.BLACK)),
            FRBTree.frbtPartAssert(FRBTNode.of(nEntry, FRBTree.BLACK))
        )
    )

internal val frbDepthOneFullPreorder = FList.of(mEntry,lEntry,nEntry)
internal val frbDepthOneFullInorder = FList.of(lEntry,mEntry,nEntry)
internal val frbDepthOneFullPostorder = FList.of(lEntry,nEntry,mEntry)
internal val frbDepthOneFullBreadthFirst = FList.of(mEntry,lEntry,nEntry)

val ttDepthTwoLeftPartial: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode.of(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode.of(lEntry)),
            FRBTree.frbtPartAssert(FRBTNode.of(sEntry))
        )
    )
val ttDepthTwoLeftRight: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode.of(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(
                FRBTNode.of(
                    lEntry, FRBTree.BLACK,
                    FRBTNil,
                    FRBTree.frbtPartAssert(FRBTNode.of(mEntry))
                )
            ),
            FRBTree.frbtPartAssert(FRBTNode.of(sEntry, FRBTree.BLACK))
        )
    )
internal val ttDepthTwoLeftRightPreorder = FList.of(nEntry,lEntry,mEntry,sEntry)
internal val ttDepthTwoLeftRightInorder = FList.of(lEntry,mEntry,nEntry,sEntry)
internal val ttDepthTwoLeftRightPostorder = FList.of(mEntry,lEntry,sEntry,nEntry)
internal val ttDepthTwoLeftRightBreadthFirst = FList.of(nEntry,lEntry,sEntry,mEntry)

val frbDepthTwoLeftLeft: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode.of(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(
                FRBTNode.of(
                    lEntry, FRBTree.BLACK,
                    FRBTree.frbtPartAssert(FRBTNode.of(eEntry)),
                    FRBTNil
                )
            ),
            FRBTree.frbtPartAssert(FRBTNode.of(sEntry, FRBTree.BLACK))
        )
    )
internal val frbDepthTwoLeftLeftPreorder = FList.of(nEntry,lEntry,eEntry,sEntry)
internal val frbDepthTwoLeftLeftInorder = FList.of(eEntry,lEntry,nEntry,sEntry)
internal val frbDepthTwoLeftLeftPostorder = FList.of(eEntry,lEntry,sEntry,nEntry)
internal val frbDepthTwoLeftLeftBreadthFirst = FList.of(nEntry,lEntry,sEntry,eEntry)

val ttDepthTwoRightPartial: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode.of(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode.of(mEntry)),
            FRBTree.frbtPartAssert(FRBTNode.of(sEntry))
        )
    )
val frbDepthTwoRightRight: FRBTree<Int, String> =
    FRBTree.frbtPartAssert(
        FRBTNode.of(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode.of(mEntry, FRBTree.BLACK)),
            FRBTree.frbtPartAssert(
                FRBTNode.of(
                    sEntry, FRBTree.BLACK,
                    FRBTNil,
                    FRBTree.frbtPartAssert(FRBTNode.of(uEntry))
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
        FRBTNode.of(
            nEntry, FRBTree.BLACK,
            FRBTree.frbtPartAssert(FRBTNode.of(mEntry, FRBTree.BLACK)),
            FRBTree.frbtPartAssert(
                FRBTNode.of(
                    sEntry, FRBTree.BLACK,
                    FRBTree.frbtPartAssert(FRBTNode.of(rEntry)),
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

internal data class CoPa<A, B>(val p: Pair<A, B>): Comparable<CoPa<A,B>> where A: Any, A: Comparable<A> {
    override fun compareTo(other: CoPa<A, B>): Int = p.first.compareTo(other.p.first)
}

internal val copa1: CoPa<Int, String> = CoPa((1 to "1"))
internal val copaIS: KClass<out CoPa<Int, String>> = copa1::class
internal val copaKey: SymKeyType<CoPa<Int, String>> = SymKeyType(copaIS)

internal val copa2 = CoPa((2 to "11"))
internal val copa3 = CoPa((3 to "111"))
internal val copa4 = CoPa((4 to "1111"))


internal val nnodeRbtOf3 = FRBTree.of(TKVEntry.ofkv(copa1, "foo1"), TKVEntry.ofkv(copa2, "foo2"), TKVEntry.ofkv(copa3, "foo3"))
internal val knodeRbtOf3 = FRBTree.of(copa1.toKKEntry(), copa2.toKKEntry(), copa3.toKKEntry())
internal val inodeRbtOf3 = FRBTree.ofvi(copa1, copa2, copa3)
internal val snodeRbtOf3 = FRBTree.ofvs(copa1, copa2, copa3)
internal val nnodeRbtOf4 = FRBTree.of(TKVEntry.ofkv(copa1, "foo1"), TKVEntry.ofkv(copa2, "foo2"), TKVEntry.ofkv(copa3, "foo3"), TKVEntry.ofkv(copa4, "foo4"))
internal val knodeRbtOf4 = FRBTree.of(copa1.toKKEntry(), copa2.toKKEntry(), copa3.toKKEntry(), copa4.toKKEntry())
internal val inodeRbtOf4 = FRBTree.ofvi(copa1, copa2, copa3, copa4)
internal val snodeRbtOf4 = FRBTree.ofvs(copa1, copa2, copa3, copa4)

internal val nnodeBstOf3 = FBSTree.of(TKVEntry.ofkv(copa1, "foo1"), TKVEntry.ofkv(copa2, "foo2"), TKVEntry.ofkv(copa3, "foo3"))
internal val knodeBstOf3 = FBSTree.of(copa1.toKKEntry(), copa2.toKKEntry(), copa3.toKKEntry())
internal val inodeBstOf3 = FBSTree.ofvi(copa1, copa2, copa3)
internal val snodeBstOf3 = FBSTree.ofvs(copa1, copa2, copa3)
internal val nnodeBstOf4 = FBSTree.of(TKVEntry.ofkv(copa1, "foo1"), TKVEntry.ofkv(copa2, "foo2"), TKVEntry.ofkv(copa3, "foo3"), TKVEntry.ofkv(copa4, "foo4"))
internal val knodeBstOf4 = FBSTree.of(copa1.toKKEntry(), copa2.toKKEntry(), copa3.toKKEntry(), copa4.toKKEntry())
internal val inodeBstOf4 = FBSTree.ofvi(copa1, copa2, copa3, copa4)
internal val snodeBstOf4 = FBSTree.ofvs(copa1, copa2, copa3, copa4)

internal val copaNSetOf3 = ofBody(nnodeRbtOf3)
internal val copaKKSetOf3 = ofBody(knodeRbtOf3)
internal val copaISetOf3 = ofBody(inodeRbtOf3)
internal val copaSSetOf3 = ofBody(snodeRbtOf3)
internal val copanSetOf4 = ofBody(nnodeRbtOf4)
internal val copaKKSetOf4 = ofBody(knodeRbtOf4)
internal val copaISetOf4 = ofBody(inodeRbtOf4)
internal val copaSSetOf4 = ofBody(snodeRbtOf4)

tailrec fun <A: Comparable<A>, B: Any> goAll(t: IMBTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
    when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
            when (val deleted = t.fdropItem(acc.head)) {
                is FBSTNode -> {
                    deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
                }
                is FBSTNil -> true shouldBe false
            }
            goAll(t, acc.tail, inorder)
        }
    }

internal tailrec fun <A: Comparable<A>, B: Any> goTele(t: IMBTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
    when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
            val deleted = t.fdropItem(acc.head)
            val oracle = inorder.ffilterNot { it == acc.head }
            when (deleted) {
                is FBSTNode -> {
                    deleted.inorder() shouldBe oracle
                }
                is FBSTNil -> deleted.size shouldBe 0
            }
            goTele(deleted, acc.tail, oracle)
        }
    }
