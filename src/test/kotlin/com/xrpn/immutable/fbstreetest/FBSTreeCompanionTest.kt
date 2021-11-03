package com.xrpn.immutable.fbstreetest

import com.xrpn.imapi.IMBTreeEqual2
import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.NOT_FOUND
import com.xrpn.immutable.FBSTree.Companion.addGraftTestingGremlin
import com.xrpn.immutable.FBSTree.Companion.bstDelete
import com.xrpn.immutable.FBSTree.Companion.bstFind
import com.xrpn.immutable.FBSTree.Companion.bstInsert
import com.xrpn.immutable.FBSTree.Companion.bstPrune
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FBSTree.Companion.fbtAssertNodeInvariant
import com.xrpn.immutable.FBSTree.Companion.fcontainsIK
import com.xrpn.immutable.FBSTree.Companion.fcontainsSK
import com.xrpn.immutable.FBSTree.Companion.fdeleteIK
import com.xrpn.immutable.FBSTree.Companion.fdeleteSK
import com.xrpn.immutable.FBSTree.Companion.ffindIK
import com.xrpn.immutable.FBSTree.Companion.ffindSK
import com.xrpn.immutable.FBSTree.Companion.ffindLastIK
import com.xrpn.immutable.FBSTree.Companion.ffindLastSK
import com.xrpn.immutable.FBSTree.Companion.finsertIK
import com.xrpn.immutable.FBSTree.Companion.finsertSK
import com.xrpn.immutable.FBSTree.Companion.finsertDupIK
import com.xrpn.immutable.FBSTree.Companion.finsertDupSK
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.of
import com.xrpn.immutable.FBSTree.Companion.ofMap
import com.xrpn.immutable.FBSTree.Companion.ofc
import com.xrpn.immutable.FBSTree.Companion.ofvi
import com.xrpn.immutable.FBSTree.Companion.ofviMap
import com.xrpn.immutable.FBSTree.Companion.ofvs
import com.xrpn.immutable.FBSTree.Companion.ofvsMap
import com.xrpn.immutable.FBSTree.Companion.toArray
import com.xrpn.immutable.FBSTree.Companion.toIMBTree

import com.xrpn.immutable.TKVEntry.Companion.intKeyOf
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import com.xrpn.immutable.bEntry
import com.xrpn.immutable.cEntry
import com.xrpn.immutable.dEntry
import com.xrpn.immutable.eEntry
import com.xrpn.immutable.fEntry
import com.xrpn.immutable.lEntry
import com.xrpn.immutable.mEntry
import com.xrpn.immutable.nEntry
import com.xrpn.immutable.rEntry
import com.xrpn.immutable.sEntry
import com.xrpn.immutable.slideShareBreadthFirst
import com.xrpn.immutable.slideSharePreorder
import com.xrpn.immutable.uEntry
import com.xrpn.immutable.wikiPreorder
import com.xrpn.immutable.zEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstree
import kotlin.random.Random.Default.nextInt

private val intFbstOfNone = ofvi(*emptyArrayOfInt)
private val intFbstOfOne = ofvi(*arrayOf<Int>(1))
private val intFbstOfTwo = ofvi(*arrayOf<Int>(1,2))
private val intFbstOfThree = ofvi(*arrayOf<Int>(2,1,3))
private val intFbstOfFourA = ofvi(*arrayOf<Int>(2,1,3,1), allowDups = true)
private val strFbstOfNone = ofvs(*emptyArrayOfStr)
private val strFbstOfOne = ofvs(*arrayOf<String>("a"))
private val strFbstOfTwo = ofvs(*arrayOf<String>("a","b"))
private val strFbstOfThree = ofvs(*arrayOf<String>("b","a","c"))
private val strFbstOfFourA = ofvs(*arrayOf<String>("b","a","c","a"), allowDups = true)

private val frbtOfNone = FRBTree.ofvi(*emptyArrayOfInt)
private val frbtOfOneX = FRBTree.ofvs(*arrayOf<Int>(1))
private val frbtOfOneY = FRBTree.ofvi(*arrayOf<String>("A"))

private val fbstOfOneX = ofvs(*arrayOf<Int>(1))
private val fbstOfOneY = ofvi(*arrayOf<String>("A"))

class FBSTreeCompanionTest : FunSpec({

    val repeats = 10
    val repeatsHigh = Pair(50, 100)

    beforeTest {}

    test("equals") {
        emptyIMBTree<Int,Int>().equals(null) shouldBe false
        emptyIMBTree<Int,Int>().equals(emptyIMBTree<Int,Int>()) shouldBe true
        emptyIMBTree<Int,Int>().equals(1) shouldBe false
        /* Sigh... */ intFbstOfNone.equals(strFbstOfNone) shouldBe true

        intFbstOfTwo.equals(null) shouldBe false
        intFbstOfTwo.equals(intFbstOfNone) shouldBe false
        intFbstOfTwo.equals(intFbstOfOne) shouldBe false
        intFbstOfTwo.equals(intFbstOfTwo) shouldBe true

        intFbstOfTwo.equals(fbstOfOneX) shouldBe false
        intFbstOfTwo.equals(fbstOfOneY) shouldBe false

        intFbstOfTwo.equals(frbtOfNone) shouldBe false
        intFbstOfTwo.equals(frbtOfOneX) shouldBe false
        intFbstOfTwo.equals(frbtOfOneY) shouldBe false

        intFbstOfTwo.equals(strFbstOfNone) shouldBe false
        intFbstOfTwo.equals(strFbstOfOne) shouldBe false
        intFbstOfTwo.equals(strFbstOfTwo) shouldBe false
        
        intFbstOfTwo.equals(emptyList<Int>()) shouldBe false
        intFbstOfTwo.equals(listOf("foobar")) shouldBe false
        intFbstOfTwo.equals(listOf("foobar","babar")) shouldBe false
        intFbstOfTwo.equals(1) shouldBe false
    }

    test("toString() hashCode()") {
        emptyIMBTree<Int,Int>().toString() shouldBe "FBSTNil"
        val aux = emptyIMBTree<Int,Int>().hashCode()
        for (i in (1..100)) {
             aux shouldBe emptyIMBTree<Int,Int>().hashCode()
        }
        intFbstOfTwo.toString() shouldStartWith "${FBSTree::class.simpleName}@"
        val aux2 = intFbstOfTwo.hashCode()
        for (i in (1..100)) {
             aux2 shouldBe intFbstOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FBSTNode.hashCode(intFbstOfTwo as FBSTNode<Int, Int>) shouldBe intFbstOfTwo.hashCode()
        }
    }

    // =========================== IMBTreeCompanion

    test("co.empty") {
        emptyIMBTree<Int, Int>() shouldBe FBSTNil
    }

    test("co.[ IMBTreeEqual2 ]") {
        IMBTreeEqual2 (intFbstOfNone, ofvi(*emptyArrayOfInt)) shouldBe true
        IMBTreeEqual2 (intFbstOfNone, intFbstOfNone) shouldBe true
        IMBTreeEqual2 (ofvi(*arrayOf(1)), ofvi(*emptyArrayOfInt)) shouldBe false
        IMBTreeEqual2 (intFbstOfNone, ofvi(*arrayOf(1))) shouldBe false
        IMBTreeEqual2 (intFbstOfOne, ofvi(*arrayOf<Int>(1))) shouldBe true
        IMBTreeEqual2 (intFbstOfOne, intFbstOfOne) shouldBe true
        IMBTreeEqual2 (ofvi(*arrayOf(1)), ofvi(*arrayOf<Int>(1, 2))) shouldBe false
        IMBTreeEqual2 (ofvi(*arrayOf<Int>(1, 2)), ofvi(*arrayOf(1))) shouldBe false
        IMBTreeEqual2 (ofvi(*arrayOf<Int>(1, 2)), ofvi(*arrayOf(1, 2))) shouldBe true
        IMBTreeEqual2 (ofvi(*arrayOf<Int>(1, 2)), ofvi(*arrayOf(2, 1))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, ofvi(*arrayOf(1, 3, 2))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, ofvi(*arrayOf(2, 3, 1))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, ofvi(*arrayOf(2, 1, 3))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, ofvi(*arrayOf(3, 1, 2))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, ofvi(*arrayOf(3, 2, 1))) shouldBe true
    }


    test("co.of varargs A") {
        of(*arrayOf<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree()
        of(7.toIAEntry(), 4.toIAEntry(), 9.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry()).inorder() shouldBe
                listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())
    }

    test("co.of varargs A3") {
        of(9.toIAEntry(), 4.toIAEntry(), 7.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry(), 3.toIAEntry()).inorder() shouldBe
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())
    }

    test("co.of varargs B") {
        of(9.toIAEntry(), 7.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry(), 4.toIAEntry()).inorder() shouldBe
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())
    }

    test("co.of varargs B3") {
        of(9.toIAEntry(), 7.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry(), 4.toIAEntry(), 3.toIAEntry()).inorder() shouldBe
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())
    }

    test("co.of varargs C") {
        of(9.toIAEntry(), 8.toIAEntry(), 7.toIAEntry(), 6.toIAEntry(), 5.toIAEntry(), 4.toIAEntry(), 3.toIAEntry()).inorder() shouldBe
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())
    }

    test("co.of varargs D") {
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of varargs DD") {
        of(3.toSAEntry(), 1.toSAEntry(), 2.toSAEntry()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of varargs D1") {
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of varargs dups A") {
        of(*arrayOf<TKVEntry<Int, Int>>(), allowDups = true) shouldBe emptyIMBTree()
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry(), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of varargs dups B") {
        val aut0 = of(100.toIAEntry(), 50.toIAEntry())
        aut0.fempty() shouldBe false
        val aut = of(100.toIAEntry(), 50.toIAEntry(), 150.toIAEntry(), 60.toIAEntry(), 60.toIAEntry(), 61.toIAEntry(), 62.toIAEntry(), 40.toIAEntry(), allowDups = true)
        val aut1 = aut.finsertDup(50.toIAEntry(), allowDups = true)
        aut.fempty() shouldBe false
        aut1.fempty() shouldBe false
    }

    test("co.of iterator") {
        of(emptyList<TKVEntry<Int, Int>>().iterator()) shouldBe emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of iterator dups") {
        of(emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator(), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of IMList (dups and no dups)") {
        of<Int, Int>(FLNil) shouldBe FBSTNil
        of(FList.of(*arrayOf(mEntry, lEntry, nEntry))) shouldBe FBSTNode.of(mEntry, FBSTNode.of(lEntry), FBSTNode.of(nEntry))
        of(FList.of(*arrayOf(mEntry, cEntry, bEntry, dEntry, zEntry, bEntry)), allowDups = true) shouldBe
                FBSTNode.of(
                    mEntry,
                    FBSTNode.of(
                        cEntry,
                        FBSTNode.of(
                            bEntry, FBSTNil,
                            FBSTNode.of(bEntry, FBSTNil, FBSTNil)
                        ),
                        FBSTNode.of(dEntry, FBSTNil, FBSTNil)
                    ),
                    FBSTNode.of(zEntry, FBSTNil, FBSTNil)
                )
        of(FList.of(*arrayOf(mEntry, cEntry, bEntry, dEntry, zEntry, bEntry)) /*, allowDups = false */) shouldBe
                FBSTNode.of(
                    mEntry,
                    FBSTNode.of(
                        cEntry,
                        FBSTNode.of(bEntry, FBSTNil, FBSTNil),
                        FBSTNode.of(dEntry, FBSTNil, FBSTNil)
                    ),
                    FBSTNode.of(zEntry, FBSTNil, FBSTNil)
                )
        of(wikiPreorder) shouldBe wikiTree
        of(slideSharePreorder) shouldBe slideShareTree
    }

    test("co.ofc varargs") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, 3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry())
    }

    test("co.ofc varargs dups") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>(), allowDups = true) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, 3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry(), allowDups = true).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry(),1.toIAEntry())
    }

    test("co.ofc iterator") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator()) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, listOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).iterator()).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry())
    }

    test("co.ofc iterator dups") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, listOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).iterator(), allowDups = true).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry(),1.toIAEntry())
    }

    test("co.ofvi varargs") {
        ofvi(*emptyArrayOfInt) shouldBe emptyIMBTree()
        ofvi(3, 2, 1, 1).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi varargs dups") {
        ofvi(*emptyArrayOfInt) shouldBe emptyIMBTree()
        ofvi(3, 2, 1, 1, allowDups = true).inorder() shouldBe listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi iterator") {
        ofvi(emptyArrayOfInt.iterator()) shouldBe emptyIMBTree()
        ofvi(arrayOf(3,2,1,1).iterator()).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi iterator (property)") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = IntArray(n) { _: Int -> nextInt() }
            val bst = ofvi(values.iterator(), allowDups = true)
            bst.size shouldBe n
            val aut = bst.inorder()
            values.sort()
            val testOracle = FList.of(values.iterator()).fmap { TKVEntry.ofIntKey(it) }
            aut shouldBe testOracle
        }
    }

    test("co.ofvi iterator dups") {
        ofvi(emptyArrayOfInt.iterator(), allowDups = true) shouldBe emptyIMBTree()
        ofvi(arrayOf(3,2,1,1).iterator(), allowDups = true).inorder() shouldBe listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi IMList") {
        ofvi(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvi(FList.of(3, 2, 1, 1)).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi IMList dups") {
        ofvi(FList.emptyIMList(), allowDups = true) shouldBe emptyIMBTree()
        ofvi(FList.of(3, 2, 1, 1), allowDups = true).inorder() shouldBe listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvs varargs") {
        ofvs(*emptyArrayOfInt) shouldBe emptyIMBTree()
        ofvs(3, 2, 1, 1).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs varargs dups") {
        ofvs(*emptyArrayOfInt) shouldBe emptyIMBTree()
        ofvs(3, 2, 1, 1, allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs iterator") {
        ofvs(emptyArrayOfInt.iterator()) shouldBe emptyIMBTree()
        ofvs(arrayOf(3,2,1,1).iterator()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs iterator dups") {
        ofvs(emptyArrayOfInt.iterator(), allowDups = true) shouldBe emptyIMBTree()
        ofvs(arrayOf(3,2,1,1).iterator(), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(), 1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs IMList") {
        ofvs(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvs(FList.of(3, 2, 1, 1)).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs IMList dups") {
        ofvs(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvs(FList.of(3, 2, 1, 1), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofMap ABCD Iterator") {
        ofMap(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv -> TKVEntry.ofkv(tkv.getk(), tkv.getv().toString()) } shouldBe emptyIMBTree()
    }

    test("co.ofMap ABCD Iterator dups") {
        ofMap(emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) { tkv ->
            TKVEntry.ofkv(
                tkv.getk(),
                tkv.getv().toString()
            )
        } shouldBe emptyIMBTree()
        ofMap(listOf(1, 1, 2, 3).map{
            TKVEntry.ofkk(
                it,
                -it
            )
        }.iterator(), allowDups = true) { tkv -> TKVEntry.ofkv(tkv.getk(), tkv.getv().toString()) }.inorder() shouldBe
                listOf(TKVEntry.ofkv(1, "1"), TKVEntry.ofkv(1, "1"), TKVEntry.ofkv(2, "2"), TKVEntry.ofkv(3, "3"))
    }

    test("co.ofviMap Iterator") {
        ofviMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofviMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder() shouldBe
                listOf(
                    TKVEntry.ofkv(intKeyOf("1"), "1"),
                    TKVEntry.ofkv(intKeyOf("2"), "2"),
                    TKVEntry.ofkv(intKeyOf("3"), "3")
                )
    }

    test("co.ofviMap Iterator dups") {
        ofviMap(emptyList<Int>().iterator(), allowDups = true) { it.toString() } shouldBe emptyIMBTree()
        ofviMap(listOf(1, 1, 2, 3).iterator(), allowDups = true) { it.toString() }.inorder() shouldBe
                listOf(
                    TKVEntry.ofkv(intKeyOf("1"), "1"),
                    TKVEntry.ofkv(intKeyOf("1"), "1"),
                    TKVEntry.ofkv(intKeyOf("2"), "2"),
                    TKVEntry.ofkv(intKeyOf("3"), "3")
                )
    }

    test("co.ofvsMap Iterator") {
        ofvsMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofvsMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder() shouldBe
                listOf(TKVEntry.ofkk("1", "1"), TKVEntry.ofkk("2", "2"), TKVEntry.ofkk("3", "3"))
    }

    test("co.ofvsMap Iterator dups") {
        ofvsMap(emptyList<Int>().iterator(), allowDups = true) { it.toString() } shouldBe emptyIMBTree()
        ofvsMap(listOf(1, 1, 2, 3).iterator(), allowDups = true) { it.toString() }.inorder() shouldBe
                listOf(
                    TKVEntry.ofkk("1", "1"),
                    TKVEntry.ofkk("1", "1"),
                    TKVEntry.ofkk("2", "2"),
                    TKVEntry.ofkk("3", "3")
                )
    }

    test("co.toIMBTree Map") {
        Arb.list(Arb.int()).checkAll(repeats) { l ->
            fun f(t: Int): Pair<Int, String> = Pair(t, (-t).toString())
            val m: Map<Int, String> = l.associate(::f)
            m.toIMBTree() shouldBe ofMap(l.map{ TKVEntry.ofkk(it, -it) }.iterator()) { tkv ->
                TKVEntry.ofkv(tkv.getk(), tkv.getv().toString())
            }
        }
    }

    test("co.fcontainsIK") {
        fcontainsIK(intFbstOfThree, 1) shouldBe true
        fcontainsIK(intFbstOfThree, 2) shouldBe true
        fcontainsIK(intFbstOfThree, 3) shouldBe true
        fcontainsIK(intFbstOfThree, 0) shouldBe false
        fcontainsIK(intFbstOfThree, 4) shouldBe false
    }

    test("co.fdeleteIK") {
        fdeleteIK(intFbstOfThree, 3) shouldBe intFbstOfTwo
    }

    test("co.ffindIK") {
        ffindIK(intFbstOfFourA, 1) shouldBe ofvi(1, 1, allowDups = true)
        ffindIK(intFbstOfFourA, 2) shouldBe intFbstOfFourA
        ffindIK(intFbstOfFourA, 3) shouldBe ofvi(3)
        ffindIK(intFbstOfFourA, 0) shouldBe null
        ffindIK(intFbstOfFourA, 4) shouldBe null
    }

    test("co.ffindLastIK") {
        ffindLastIK(intFbstOfFourA, 1) shouldBe ofvi(1)
        ffindLastIK(intFbstOfFourA, 2) shouldBe intFbstOfFourA
        ffindLastIK(intFbstOfFourA, 3) shouldBe ofvi(3)
        ffindLastIK(intFbstOfFourA, 0) shouldBe null
        ffindLastIK(intFbstOfFourA, 4) shouldBe null
    }

    test("co.finsertIK") {
        finsertIK(intFbstOfTwo, 3) shouldBe intFbstOfThree
    }

    test("co.finsertDupIK") {
        finsertDupIK(finsertDupIK(intFbstOfOne, 2, allowDups = true), 2, allowDups = true) shouldBe finsertDupIK(
            intFbstOfTwo, 2, allowDups = true)
    }


    test("co.fcontainsSK") {
        fcontainsSK(strFbstOfThree, "a") shouldBe true
        fcontainsSK(strFbstOfThree, "b") shouldBe true
        fcontainsSK(strFbstOfThree, "c") shouldBe true
        fcontainsSK(strFbstOfThree, "d") shouldBe false
    }

    test("co.fdeleteSK") {
        fdeleteSK(strFbstOfThree, "c") shouldBe strFbstOfTwo
    }

    test("co.ffindSK") {
        ffindSK(strFbstOfFourA, "a") shouldBe ofvs("a", "a", allowDups = true)
        ffindSK(strFbstOfFourA, "b") shouldBe strFbstOfFourA
        ffindSK(strFbstOfFourA, "c") shouldBe ofvs("c")
        ffindSK(strFbstOfFourA, "d") shouldBe null
    }

    test("co.ffindLastSK") {
        ffindLastSK(strFbstOfFourA, "a") shouldBe ofvs("a")
        ffindLastSK(strFbstOfFourA, "b") shouldBe strFbstOfFourA
        ffindLastSK(strFbstOfFourA, "c") shouldBe ofvs("c")
        ffindLastSK(strFbstOfFourA, "d") shouldBe null
    }

    test("co.finsertSK") {
        finsertSK(strFbstOfTwo, "c") shouldBe strFbstOfThree
    }

    test("co.finsertDupSK") {
        finsertDupSK(finsertDupSK(strFbstOfOne, "b", allowDups = true), "b", allowDups = true) shouldBe finsertDupSK(
            strFbstOfTwo, "b", allowDups = true)
    }

    // =========================== implementation

    test("co.NOT_FOUND") {
        NOT_FOUND shouldBe -1
    }

    test("co.nul") {
        nul<Int, Int>() shouldBe FBSTNil
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
            bstInsert(
                wikiTree,
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

        bstPrune(depthOneLeft, lEntry) shouldBe FBSTNode.of(mEntry)
        bstPrune(depthOneRight, nEntry) shouldBe FBSTNode.of(mEntry)
        bstPrune(depthOneFull, lEntry) shouldBe depthOneRight
        bstPrune(depthOneFull, nEntry) shouldBe depthOneLeft

        bstPrune(depthTwoLeftRight, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(
                    FBSTNode.of(
                        lEntry,
                    FBSTNil,
                    fbtAssertNodeInvariant(FBSTNode.of(mEntry))
                )
                )
            )
            )
        bstPrune(depthTwoLeftRight, mEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(lEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(sEntry))
            )
            )
        bstPrune(depthTwoLeftRight, lEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                FBSTNil,
                fbtAssertNodeInvariant(FBSTNode.of(sEntry))
            )
            )
        bstPrune(depthTwoLeftRight, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoLeftLeft, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(
                    FBSTNode.of(
                        lEntry,
                    fbtAssertNodeInvariant(FBSTNode.of(eEntry)),
                    FBSTNil
                    )
                )
            )
            )
        bstPrune(depthTwoLeftLeft, eEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(lEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(sEntry))
            )
            )
        bstPrune(depthTwoLeftLeft, lEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                FBSTNil,
                fbtAssertNodeInvariant(FBSTNode.of(sEntry))
            )
            )
        bstPrune(depthTwoLeftLeft, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoRightRight, uEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(mEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(sEntry))
            )
            )
        bstPrune(depthTwoRightRight, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(mEntry)),
                FBSTNil
            )
            )
        bstPrune(depthTwoRightRight, mEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                FBSTNil,
                fbtAssertNodeInvariant(
                    FBSTNode.of(
                        sEntry,
                    FBSTNil,
                    fbtAssertNodeInvariant(FBSTNode.of(uEntry)))
                ))
            )
        bstPrune(depthTwoRightRight, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoRightLeft, rEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(mEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(sEntry))
            )
            )
        bstPrune(depthTwoRightLeft, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(mEntry)),
                FBSTNil
            )
            )
        bstPrune(depthTwoRightLeft, mEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(
                    nEntry,
                FBSTNil,
                fbtAssertNodeInvariant(
                    FBSTNode.of(
                        sEntry,
                    fbtAssertNodeInvariant(FBSTNode.of(rEntry)),
                    FBSTNil
                    )
                )
            )
            )
        bstPrune(depthTwoRightLeft, nEntry) shouldBe FBSTNil
    }

    test("co.addGraftTesting") {
        addGraftTestingGremlin(FBSTNil, FBSTNil) shouldBe FBSTNil
        addGraftTestingGremlin(depthOneFull, FBSTNil) shouldBe depthOneFull
        addGraftTestingGremlin(FBSTNil, depthOneFull) shouldBe depthOneFull

        addGraftTestingGremlin(bstPrune(depthOneFull, nEntry), FBSTNode.of(nEntry)) shouldBe depthOneFull
        addGraftTestingGremlin(bstPrune(depthOneFull, lEntry), FBSTNode.of(lEntry)) shouldBe depthOneFull

        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, mEntry), FBSTNode.of(mEntry)) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, sEntry), bstFind(depthTwoLeftRight, sEntry)!!) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, lEntry), bstFind(depthTwoLeftRight, lEntry)!!) shouldBe depthTwoLeftRight

        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, eEntry), FBSTNode.of(eEntry)) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, sEntry), bstFind(depthTwoLeftLeft, sEntry)!!) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, lEntry), bstFind(depthTwoLeftLeft, lEntry)!!) shouldBe depthTwoLeftLeft

        addGraftTestingGremlin(bstPrune(depthTwoRightRight, uEntry), FBSTNode.of(uEntry)) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, sEntry), bstFind(depthTwoRightRight, sEntry)!!) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, mEntry), bstFind(depthTwoRightRight, mEntry)!!) shouldBe depthTwoRightRight

        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, rEntry), FBSTNode.of(rEntry)) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, sEntry), bstFind(depthTwoRightLeft, sEntry)!!) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, mEntry), bstFind(depthTwoRightLeft, mEntry)!!) shouldBe depthTwoRightLeft
    }

    test("co.toArray") {
        Arb.fbstree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val ary: Array<TKVEntry<Int, Int>> = toArray(fbst)
            fbst shouldBe of(ary.iterator())
            fbst shouldBe of(*ary)
        }
    }

})
