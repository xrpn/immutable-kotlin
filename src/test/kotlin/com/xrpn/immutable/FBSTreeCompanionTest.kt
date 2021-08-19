package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.NOT_FOUND
import com.xrpn.immutable.FBSTree.Companion.fbtAssert
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.toIMBTree
import com.xrpn.immutable.FBSTree.Companion.bstParent
import com.xrpn.immutable.FBSTree.Companion.bstPrune
import com.xrpn.immutable.FBSTree.Companion.bstFind
import com.xrpn.immutable.FBSTree.Companion.bstFindLast
import com.xrpn.immutable.FBSTree.Companion.addGraftTestingGremlin
import com.xrpn.immutable.FBSTree.Companion.bstInsert
import com.xrpn.immutable.FBSTree.Companion.bstDelete
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FList.Companion.toIMList
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import io.kotest.assertions.fail
import io.kotest.property.Arb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

private val intFbstOfNone = FBSTree.ofvi(*arrayOf<Int>())
private val intFbstOfOne = FBSTree.ofvi(*arrayOf<Int>(1))
private val intFbstOfTwo = FBSTree.ofvi(*arrayOf<Int>(1,2))
private val intFbstOfThree = FBSTree.ofvi(*arrayOf<Int>(1,2,3))
private val strFbstOfNone = FBSTree.ofvi(*arrayOf<String>())
private val strFbstOfOne = FBSTree.ofvi(*arrayOf<String>("a"))
private val strFbstOfTwo = FBSTree.ofvi(*arrayOf<String>("a","b"))
private val strFbstOfThree = FBSTree.ofvi(*arrayOf<String>("a","b","c"))

class FBSTreeCompanionTest : FunSpec({

    val repeats = 10
    val repeatsHigh = Pair(50, 100)

    beforeTest {}

    test("equals") {
        // stowaway, used to make up for missing coverage
        emptyIMBTree<Int,Int>().equals(null) shouldBe false
        emptyIMBTree<Int,Int>().equals(emptyIMBTree<Int,Int>()) shouldBe true
        emptyIMBTree<Int,Int>().equals(1) shouldBe false
        /* Sigh... */ intFbstOfNone.equals(strFbstOfNone) shouldBe true
        intFbstOfTwo.equals(null) shouldBe false
        intFbstOfTwo.equals(strFbstOfNone) shouldBe false
        intFbstOfTwo.equals(strFbstOfTwo) shouldBe false
//        intFbstOfTwo.equals(emptyList<Int>()) shouldBe false
//        intFbstOfTwo.equals(listOf("foobar")) shouldBe false
//        intFbstOfTwo.equals(listOf("foobar","babar")) shouldBe false
        intFbstOfTwo.equals(1) shouldBe false
    }

    test("toString") {
        // stowaway, used to make up for missing coverage
        emptyIMBTree<Int,Int>().toString() shouldBe "FBSTNil"
        for (i in (1..100)) {
            emptyIMBTree<Int,Int>().hashCode() shouldBe emptyIMBTree<Int,Int>().hashCode()
        }
        intFbstOfTwo.toString() shouldStartWith "${FBSTree::class.simpleName}:"
        for (i in (1..100)) {
            intFbstOfTwo.hashCode() shouldBe intFbstOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FBSTNode.hashCode(intFbstOfTwo as FBSTNode<Int,Int>) shouldBe intFbstOfTwo.hashCode()
        }
    }

    // IMBTreeCompanion

    test("co.nul") {
        nul<Int, Int>() shouldBe FBSTNil
    }

    test("co.empty") {
        FBSTree.emptyIMBTree<Int, Int>() shouldBe FBSTNil
    }

    test("co.equal2") {

    }

    test("co.of varargs") {
    }

    test("co.of varargs dups") {
    }

    test("co.of iterator") {
    }

    test("co.of iterator dups") {
    }

    test("co.of IMList (dups and no dups)") {
        FBSTree.of<Int, Int>(FLNil) shouldBe FBSTNil
        FBSTree.of(FList.of(*arrayOf(mEntry,lEntry,nEntry))) shouldBe FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(nEntry))
        FBSTree.of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry)), allowDups = true) shouldBe
                FBSTNode(mEntry,
                    FBSTNode(cEntry,
                        FBSTNode(bEntry, FBSTNil,
                            FBSTNode(bEntry, FBSTNil, FBSTNil)),
                        FBSTNode(dEntry, FBSTNil, FBSTNil)),
                    FBSTNode(zEntry, FBSTNil, FBSTNil))
        FBSTree.of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry)) /*, allowDups = false */) shouldBe
                FBSTNode(mEntry,
                    FBSTNode(cEntry,
                        FBSTNode(bEntry, FBSTNil, FBSTNil),
                        FBSTNode(dEntry, FBSTNil, FBSTNil)),
                    FBSTNode(zEntry, FBSTNil, FBSTNil))
        FBSTree.of(wikiPreorder) shouldBe wikiTree
        FBSTree.of(slideSharePreorder) shouldBe slideShareTree
    }

//    test("co.ofvi varargs") {
//    }
//
//    test("co.ofvi varargs dups") {
//    }

//    test("co.ofvi iterator") {
//    }

    test("co.ofvi iterator (property)") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = IntArray(n) { _: Int -> nextInt() }
            val bst = FBSTree.ofvi(values.iterator(), allowDups = true)
            bst.size shouldBe n
            val aut = bst.inorder()
            values.sort()
            val testOracle = FList.of(values.iterator()).fmap { TKVEntry.ofIntKey(it) }
            aut shouldBe testOracle
        }
    }

//    test("co.ofvi iterator dups") {
//    }

    test("co.ofvi IMList") {
    }

    test("co.ofvi IMList dups") {
    }

//    test("co.ofvs varargs") {
//    }
//
//    test("co.ofvs varargs dups") {
//    }

//    test("co.ofvs iterator") {
//    }
//
//    test("co.ofvs iterator dups") {
//    }

    test("co.ofvs IMList") {
    }

    test("co.ofvs IMList dups") {
    }

//    test("co.ofMap ABCD Iterator") {
//    }

    test("co.ofMap ABCD Iterator dups") {
    }

    test("co.ofviMap Iterator") {
    }

    test("co.ofviMap Iterator dups") {
    }

    test("co.ofvsMap Iterator") {
    }

    test("co.ofvsMap Iterator dups") {
    }

    test("co.fcontainsIK") {

    }

    test("co.toIMBTree Collection") {
        Arb.list(Arb.int()).checkAll(repeats) { l ->
            val lim = l.map{ it.toIAEntry() }
            lim.toIMBTree() shouldBe FBSTree.ofvi(l.iterator(), allowDups = true)
            lim.toIMBTree() shouldBe FBSTree.ofvi(l.toIntArray(), allowDups = true)
            val sim = l.map{ it.toIAEntry() }.toSet()
            sim.toIMBTree() shouldBe FBSTree.ofvi(l.iterator(), allowDups = false)
            sim.toIMBTree() shouldBe FBSTree.ofvi(l.toIntArray(), allowDups = false)
        }
        Arb.list(Arb.string((0..20))).checkAll(repeats) { l ->
            val lsm = l.map{ it.toSAEntry() }
            lsm.toIMBTree() shouldBe FBSTree.ofvs(l.iterator(), allowDups = true)
            lsm.toIMBTree() shouldBe FBSTree.ofvs(l.toTypedArray(), allowDups = true)
            val ssm = l.map{ it.toSAEntry() }.toSet()
            ssm.toIMBTree() shouldBe FBSTree.ofvs(l.iterator(), allowDups = false)
            ssm.toIMBTree() shouldBe FBSTree.ofvs(l.toTypedArray(), allowDups = false)
        }
    }

    test("co.toIMBTree Map") {
        Arb.list(Arb.int()).checkAll(repeats) { l ->
            fun f(t: Int): Pair<Int, String> = Pair(t, (-t).toString())
            val m: Map<Int, String> = l.associate(::f)
            m.toIMBTree() shouldBe FBSTree.ofMap(l.map{ TKVEntry.of(it, -it) }.iterator()) { tkv ->
                TKVEntry.of(tkv.getk(), tkv.getv().toString())
            }
        }
    }

    test("co.fdeleteIK") {

    }

    test("co.ffindIK") {

    }

    test("co.ffindLastIK") {

    }

    test("co.finsertIK") {

    }

    test("co.finsertDupIK") {

    }


    test("co.fcontainsSK") {

    }

    test("co.fdeleteSK") {

    }

    test("co.ffindSK") {

    }

    test("co.ffindLastSK") {

    }

    test("co.finsertSK") {

    }

    test("co.finsertDupSK") {

    }

    // implementation

    test("co.NOT_FOUND") {
        NOT_FOUND shouldBe -1
    }

    test("co.bstDelete with dups (remove only one dup)") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = bstDelete(t, acc.head, atMostOne = true)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.fdropFirst { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = bstInsert(wikiTree, wikiTree.froot()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = bstInsert(
            bstInsert(wikiTree,
                wikiTree.froot()!!, allowDups = true),
            wikiTree.froot()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = bstInsert(wikiTree, wikiTree.fleftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = bstInsert(wikiTree, wikiTree.frightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = bstInsert(
            bstInsert(slideShareTree, slideShareTree.fleftMost()!!),
            slideShareTree.fleftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = bstInsert(
            bstInsert(slideShareTree, slideShareTree.frightMost()!!),
            slideShareTree.frightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }

    test("co.bstPrune") {
        bstPrune(wikiTree, zEntry) /* missing match */ shouldBe wikiTree
        bstPrune(wikiTree, fEntry) /* prune at root */ shouldBe FBSTNil

        bstPrune(depthOneLeft,lEntry) shouldBe FBSTNode(mEntry)
        bstPrune(depthOneRight,nEntry) shouldBe FBSTNode(mEntry)
        bstPrune(depthOneFull,lEntry) shouldBe depthOneRight
        bstPrune(depthOneFull,nEntry) shouldBe depthOneLeft

        bstPrune(depthTwoLeftRight, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry,
                    FBSTNil,
                    fbtAssert(FBSTNode(mEntry))
                ))
            ))
        bstPrune(depthTwoLeftRight, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftRight, lEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftRight, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoLeftLeft, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry,
                    fbtAssert(FBSTNode(eEntry)),
                    FBSTNil))
            ))
        bstPrune(depthTwoLeftLeft, eEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftLeft, lEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftLeft, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoRightRight, uEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoRightRight, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                FBSTNil
            ))
        bstPrune(depthTwoRightRight, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry,
                    FBSTNil,
                    fbtAssert(FBSTNode(uEntry))))))
        bstPrune(depthTwoRightRight, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoRightLeft, rEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoRightLeft, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                FBSTNil
            ))
        bstPrune(depthTwoRightLeft, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry,
                    fbtAssert(FBSTNode(rEntry)),
                    FBSTNil))
            ))
        bstPrune(depthTwoRightLeft, nEntry) shouldBe FBSTNil
    }

    test("co.addGraftTesting") {
        addGraftTestingGremlin(FBSTNil, FBSTNil) shouldBe FBSTNil
        addGraftTestingGremlin(depthOneFull, FBSTNil) shouldBe depthOneFull
        addGraftTestingGremlin(FBSTNil, depthOneFull) shouldBe depthOneFull

        addGraftTestingGremlin(bstPrune(depthOneFull, nEntry), FBSTNode(nEntry)) shouldBe depthOneFull
        addGraftTestingGremlin(bstPrune(depthOneFull, lEntry), FBSTNode(lEntry)) shouldBe depthOneFull

        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, mEntry), FBSTNode(mEntry)) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, sEntry), bstFind(depthTwoLeftRight,sEntry)!!) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, lEntry), bstFind(depthTwoLeftRight,lEntry)!!) shouldBe depthTwoLeftRight

        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, eEntry), FBSTNode(eEntry)) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, sEntry), bstFind(depthTwoLeftLeft,sEntry)!!) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, lEntry), bstFind(depthTwoLeftLeft,lEntry)!!) shouldBe depthTwoLeftLeft

        addGraftTestingGremlin(bstPrune(depthTwoRightRight, uEntry), FBSTNode(uEntry)) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, sEntry), bstFind(depthTwoRightRight,sEntry)!!) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, mEntry), bstFind(depthTwoRightRight,mEntry)!!) shouldBe depthTwoRightRight

        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, rEntry), FBSTNode(rEntry)) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, sEntry), bstFind(depthTwoRightLeft,sEntry)!!) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, mEntry), bstFind(depthTwoRightLeft,mEntry)!!) shouldBe depthTwoRightLeft
    }

})
