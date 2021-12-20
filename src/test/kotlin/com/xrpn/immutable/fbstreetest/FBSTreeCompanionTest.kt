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
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.of
import com.xrpn.immutable.FBSTree.Companion.ofMap
import com.xrpn.immutable.FBSTree.Companion.ofMapNotUnique
import com.xrpn.immutable.FBSTree.Companion.ofc
import com.xrpn.immutable.FBSTree.Companion.ofvi
import com.xrpn.immutable.FBSTree.Companion.ofviMap
import com.xrpn.immutable.FBSTree.Companion.ofviMapNotUnique
import com.xrpn.immutable.FBSTree.Companion.ofvs
import com.xrpn.immutable.FBSTree.Companion.ofvsMap
import com.xrpn.immutable.FBSTree.Companion.ofvsMapNotUnique
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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.fbsItree
import kotlin.random.Random.Default.nextInt
import io.kotest.matchers.types.shouldBeInstanceOf

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

    test("toString() hashCode() unique") {
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

    test("toString() hashCode() generic") {
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
        emptyIMBTree<Int, Int>(true) shouldBe FBSTGeneric.empty
        (emptyIMBTree<Int, Int>(true) === FBSTGeneric.empty) shouldBe true
        emptyIMBTree<Int, Int>(false) shouldBe FBSTUnique.empty
        (emptyIMBTree<Int, Int>(false) === FBSTUnique.empty) shouldBe true
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
        of(7.toIAEntry(), 4.toIAEntry(), 9.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry()).inorder().softEqual(
                listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())) shouldBe true
    }

    test("co.of varargs A3") {
        of(9.toIAEntry(), 4.toIAEntry(), 7.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry(), 3.toIAEntry()).inorder().softEqual(
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())) shouldBe true
    }

    test("co.of varargs B") {
        of(9.toIAEntry(), 7.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry(), 4.toIAEntry()).inorder().softEqual(
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())) shouldBe true
    }

    test("co.of varargs B3") {
        of(9.toIAEntry(), 7.toIAEntry(), 3.toIAEntry(), 5.toIAEntry(), 6.toIAEntry(), 8.toIAEntry(), 4.toIAEntry(), 3.toIAEntry()).inorder().softEqual(
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())) shouldBe true
    }

    test("co.of varargs C") {
        of(9.toIAEntry(), 8.toIAEntry(), 7.toIAEntry(), 6.toIAEntry(), 5.toIAEntry(), 4.toIAEntry(), 3.toIAEntry()).inorder().softEqual(
            listOf(3.toIAEntry(),4.toIAEntry(),5.toIAEntry(),6.toIAEntry(),7.toIAEntry(),8.toIAEntry(),9.toIAEntry())) shouldBe true
    }

    test("co.of varargs D") {
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of varargs DD") {
        of(3.toSAEntry(), 1.toSAEntry(), 2.toSAEntry()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of varargs D1") {
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of varargs dups A") {
        of(*arrayOf<TKVEntry<Int, Int>>(), allowDups = true) shouldBe emptyIMBTree()
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry(), allowDups = true).inorder().softEqual(listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of varargs dups B") {
        // TODO remove
//        val aut0 = of(100.toIAEntry(), 50.toIAEntry())
//        aut0.fempty() shouldBe false
//        val aut = of(100.toIAEntry(), 50.toIAEntry(), 150.toIAEntry(), 60.toIAEntry(), 60.toIAEntry(), 61.toIAEntry(), 62.toIAEntry(), 40.toIAEntry(), allowDups = true)
//        val aut1 = aut.finsert(50.toIAEntry())
//        aut.fempty() shouldBe false
//        aut1.fempty() shouldBe false
    }

    test("co.of iterator") {
        of(emptyList<TKVEntry<Int, Int>>().iterator()) shouldBe emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of iterator dups") {
        of(emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator(), allowDups = true).inorder().softEqual(listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of IMList (dups and no dups)") {
        of<Int, Int>(FLNil) shouldBe FBSTUnique.empty
        of<Int, Int>(FLNil) shouldBe FBSTGeneric.empty
        of(FList.of(*arrayOf(mEntry, lEntry, nEntry))) shouldBe FBSTNode.of(false,mEntry, FBSTNode.of(false,lEntry), FBSTNode.of(false,nEntry))
        of(FList.of(*arrayOf(mEntry, cEntry, bEntry, dEntry, zEntry, bEntry)), allowDups = true).softEqual(
                FBSTNode.of(true,
                    mEntry,
                    FBSTNode.of(true,
                        cEntry,
                        FBSTNode.of(true,
                            bEntry, FBSTGeneric.empty,
                            FBSTNode.of(true,bEntry, FBSTGeneric.empty, FBSTGeneric.empty)
                        ),
                        FBSTNode.of(true,dEntry, FBSTGeneric.empty, FBSTGeneric.empty)
                    ),
                    FBSTNode.of(true,zEntry, FBSTGeneric.empty, FBSTGeneric.empty)
                )) shouldBe true
        of(FList.of(*arrayOf(mEntry, cEntry, bEntry, dEntry, zEntry, bEntry)) /*, allowDups = false */).softEqual(
                FBSTNode.of(false,
                    mEntry,
                    FBSTNode.of(false,
                        cEntry,
                        FBSTNode.of(false,bEntry, FBSTUnique.empty, FBSTUnique.empty),
                        FBSTNode.of(false,dEntry, FBSTUnique.empty, FBSTUnique.empty)
                    ),
                    FBSTNode.of(false,zEntry, FBSTUnique.empty, FBSTUnique.empty)
                )) shouldBe true
        of(wikiPreorder) shouldBe wikiTree
        of(slideSharePreorder) shouldBe slideShareTree
    }

    test("co.ofc varargs") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree(true)
        (ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>()) === emptyIMBTree<Int,Int>()) shouldBe true
        ofc(reverseIntCompare, 3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).inorder().softEqual(listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry())) shouldBe true
    }

    test("co.ofc varargs dups") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>(), allowDups = true) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>(), allowDups = true) shouldBe emptyIMBTree(true)
        (ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>(), allowDups = true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofc(reverseIntCompare, 3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry(), allowDups = true).inorder().softEqual(listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry(),1.toIAEntry())) shouldBe true
    }

    test("co.ofc iterator") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator()) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator()) shouldBe emptyIMBTree(true)
        (ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator()) === emptyIMBTree<Int,Int>()) shouldBe true
        ofc(reverseIntCompare, listOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).iterator()).inorder().softEqual(listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry())) shouldBe true
    }

    test("co.ofc iterator dups") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) shouldBe emptyIMBTree(true)
        (ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofc(reverseIntCompare, listOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).iterator(), allowDups = true).inorder().softEqual(listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry(),1.toIAEntry())) shouldBe true
    }

    test("co.ofvi varargs") {
        ofvi(*emptyArrayOfInt) shouldBe emptyIMBTree()
        (ofvi(*emptyArrayOfInt) === emptyIMBTree<Int,Int>()) shouldBe true
        ofvi(3, 2, 1, 1).inorder().softEqual(listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvi varargs dups") {
        ofvi(*emptyArrayOfInt, allowDups = true) shouldBe emptyIMBTree(true)
        (ofvi(*emptyArrayOfInt, allowDups = true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofvi(3, 2, 1, 1, allowDups = true).inorder().softEqual(listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvi iterator") {
        ofvi(emptyArrayOfInt.iterator()) shouldBe emptyIMBTree()
        (ofvi(emptyArrayOfInt.iterator()) === emptyIMBTree<Int,Int>()) shouldBe true
        ofvi(arrayOf(3,2,1,1).iterator()).inorder().softEqual(listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
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
        ofvi(emptyArrayOfInt.iterator(), allowDups = true) shouldBe emptyIMBTree(true)
        (ofvi(emptyArrayOfInt.iterator(), allowDups = true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofvi(arrayOf(3,2,1,1).iterator(), allowDups = true).inorder().softEqual(listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvi IMList") {
        ofvi(FList.emptyIMList()) shouldBe emptyIMBTree()
        (ofvi(FList.emptyIMList()) === emptyIMBTree<Int,Int>()) shouldBe true
        ofvi(FList.of(3, 2, 1, 1)).inorder().softEqual(listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvi IMList dups") {
        ofvi(FList.emptyIMList(), allowDups = true) shouldBe emptyIMBTree(true)
        (ofvi(FList.emptyIMList(), allowDups = true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofvi(FList.of(3, 2, 1, 1), allowDups = true).inorder().softEqual(listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvs varargs") {
        ofvs(*emptyArrayOfInt) shouldBe emptyIMBTree()
        (ofvs(*emptyArrayOfInt) === emptyIMBTree<Int,Int>()) shouldBe true
        ofvs(3, 2, 1, 1).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofvs varargs dups") {
        ofvs(*emptyArrayOfInt, allowDups = true) shouldBe emptyIMBTree(true)
        (ofvs(*emptyArrayOfInt, allowDups = true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofvs(3, 2, 1, 1, allowDups = true).inorder().softEqual(listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofvs iterator") {
        ofvs(emptyArrayOfInt.iterator()) shouldBe emptyIMBTree()
        (ofvs(emptyArrayOfInt.iterator()) === emptyIMBTree<Int,Int>()) shouldBe true
        ofvs(arrayOf(3,2,1,1).iterator()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofvs iterator dups") {
        ofvs(emptyArrayOfInt.iterator(), allowDups = true) shouldBe emptyIMBTree(true)
        (ofvs(emptyArrayOfInt.iterator(), allowDups = true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofvs(arrayOf(3,2,1,1).iterator(), allowDups = true).inorder().softEqual(listOf(1.toSAEntry(), 1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofvs IMList") {
        ofvs(FList.emptyIMList()) shouldBe emptyIMBTree()
        (ofvs(FList.emptyIMList()) === emptyIMBTree<Int,Int>()) shouldBe true
        ofvs(FList.of(3, 2, 1, 1)).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofvs IMList dups") {
        ofvs(FList.emptyIMList(), true) shouldBe emptyIMBTree(true)
        (ofvs(FList.emptyIMList(), true) === emptyIMBTree<Int,Int>(true)) shouldBe true
        ofvs(FList.of(3, 2, 1, 1), allowDups = true).inorder().softEqual(listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofMap ABCD Iterator") {
        ofMap(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv -> TKVEntry.ofkv(tkv.getk(), tkv.getv().toString()) } shouldBe emptyIMBTree()
        (ofMap(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv -> TKVEntry.ofkv(tkv.getk(), tkv.getv().toString()) } === emptyIMBTree<Int,Int>()) shouldBe true 
    }

    test("co emptyness") {
        for (i in 1..10) {
            (emptyIMBTree<Int,Int>() === emptyIMBTree<Int,Int>(true)) shouldBe false
            (emptyIMBTree<Int,Int>() === emptyIMBTree<Int,Int>()) shouldBe true
            (emptyIMBTree<Int,Int>(false) === emptyIMBTree<Int,Int>(false)) shouldBe true
        }
    }
    
    test("co.ofMap ABCD Iterator dups") {
        ofMapNotUnique(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv ->
            TKVEntry.ofkv(
                tkv.getk(),
                tkv.getv().toString()
            )
        } shouldBe emptyIMBTree(true)
        (ofMapNotUnique(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv ->
            TKVEntry.ofkv(
                tkv.getk(),
                tkv.getv().toString()
            )
        } === emptyIMBTree<Int,Int>(true)) shouldBe true 
        ofMapNotUnique(listOf(1, 1, 2, 3).map{
            TKVEntry.ofkk(
                it,
                -it
            )
        }.iterator()) { tkv -> TKVEntry.ofkv(tkv.getk(), tkv.getv().toString()) }.inorder().softEqual(
                listOf(TKVEntry.ofkv(1, "1"), TKVEntry.ofkv(1, "1"), TKVEntry.ofkv(2, "2"), TKVEntry.ofkv(3, "3"))) shouldBe true
    }

    test("co.ofviMap Iterator") {
        ofviMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        (ofviMap(emptyList<Int>().iterator()) { it.toString() } === emptyIMBTree<Int,Int>()) shouldBe true 
        ofviMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder().softEqual(
                listOf(
                    TKVEntry.ofkv(intKeyOf("1"), "1"),
                    TKVEntry.ofkv(intKeyOf("2"), "2"),
                    TKVEntry.ofkv(intKeyOf("3"), "3")
                )) shouldBe true
    }

    test("co.ofviMap Iterator dups") {
        ofviMapNotUnique(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree(true)
        (ofviMapNotUnique(emptyList<Int>().iterator()) { it.toString() } === emptyIMBTree<Int,Int>(true)) shouldBe true 
        ofviMapNotUnique(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder().softEqual(
                listOf(
                    TKVEntry.ofkv(intKeyOf("1"), "1"),
                    TKVEntry.ofkv(intKeyOf("1"), "1"),
                    TKVEntry.ofkv(intKeyOf("2"), "2"),
                    TKVEntry.ofkv(intKeyOf("3"), "3")
                )) shouldBe true
    }

    test("co.ofvsMap Iterator") {
        ofvsMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        (ofvsMap(emptyList<Int>().iterator()) { it.toString() } === emptyIMBTree<Int,Int>()) shouldBe true 
        ofvsMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder().softEqual(
                listOf(TKVEntry.ofkk("1", "1"), TKVEntry.ofkk("2", "2"), TKVEntry.ofkk("3", "3"))) shouldBe true
    }

    test("co.ofvsMap Iterator dups") {
        ofvsMapNotUnique(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree(true)
        (ofvsMapNotUnique(emptyList<Int>().iterator()) { it.toString() } === emptyIMBTree<Int,Int>(true)) shouldBe true 
        ofvsMapNotUnique(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder().softEqual(
                listOf(
                    TKVEntry.ofkk("1", "1"),
                    TKVEntry.ofkk("1", "1"),
                    TKVEntry.ofkk("2", "2"),
                    TKVEntry.ofkk("3", "3")
                )) shouldBe true
    }

    test("co.toIMBTree Map") {
        Arb.list(Arb.int()).checkAll(repeats) { l ->
            fun f(t: Int): Pair<Int, String> = Pair(t, (-t).toString())
            val m: Map<Int, String> = l.associate(::f)
            m.toIMBTree() shouldBe ofMap(l.map{ TKVEntry.ofkk(it, -it) }.iterator()) { tkv ->
                TKVEntry.ofkv(tkv.getk(), tkv.getv().toString())
            }
            m.toIMBTree() shouldBe ofMapNotUnique(l.map{ TKVEntry.ofkk(it, -it) }.iterator()) { tkv ->
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
        finsertIK(emptyIMBTree(), "c").shouldBeInstanceOf<FBSTNodeUnique<Int,String>>()
        finsertIK(emptyIMBTree(true), "c").shouldBeInstanceOf<FBSTNodeGeneric<Int,String>>()
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
        finsertSK(emptyIMBTree(), "c").shouldBeInstanceOf<FBSTNodeUnique<String,String>>()
        finsertSK(emptyIMBTree(true), "c").shouldBeInstanceOf<FBSTNodeGeneric<String,String>>()
    }

    // =========================== implementation

    test("co.NOT_FOUND") {
        NOT_FOUND shouldBe -1
    }

    test("co.nul") {
        (nul<Int, Int>(true) == FBSTGeneric.empty) shouldBe true
        (nul<Int, Int>(true) === FBSTGeneric.empty) shouldBe true
        (nul<Int, Int>() == FBSTUnique.empty) shouldBe true
        (nul<Int, Int>() === FBSTUnique.empty) shouldBe true
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
        val aux1 = bstInsert(wikiTreeLoose, wikiTree.froot()!!)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = bstInsert(
            bstInsert(
                wikiTreeLoose,
                wikiTree.froot()!!),
            wikiTree.froot()!!)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = bstInsert(wikiTreeLoose, wikiTree.fleftMost()!!)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = bstInsert(wikiTreeLoose, wikiTree.frightMost()!!)
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

    test("generic vs unique node invariant"){
        shouldThrow<IllegalStateException> {
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                    FBSTUnique.empty,
                    fbtAssertNodeInvariant(
                        FBSTNode.of(true,
                            sEntry,
                            fbtAssertNodeInvariant(FBSTNode.of(false, rEntry)),
                            FBSTUnique.empty
                        )
                    )
                )
            )
        }
        shouldThrow<IllegalStateException> {
            fbtAssertNodeInvariant(
                FBSTNode.of(true,
                    nEntry,
                    FBSTUnique.empty,
                    fbtAssertNodeInvariant(
                        FBSTNode.of(false,
                            sEntry,
                            fbtAssertNodeInvariant(FBSTNode.of(true, rEntry)),
                            FBSTUnique.empty
                        )
                    )
                )
            )
        }
    }

    test("co.bstPrune") {
        bstPrune(wikiTree, zEntry) /* missing match */ shouldBe wikiTree
        bstPrune(wikiTree, fEntry) /* prune at root */ shouldBe FBSTUnique.empty
        bstPrune(wikiTreeLoose, fEntry) /* prune at root */ shouldBe FBSTGeneric.empty

        bstPrune(depthOneLeft, lEntry) shouldBe FBSTNode.of(false,mEntry)
        bstPrune(depthOneRight, nEntry) shouldBe FBSTNode.of(false,mEntry)
        bstPrune(depthOneFull, lEntry) shouldBe depthOneRight
        bstPrune(depthOneFull, nEntry) shouldBe depthOneLeft

        bstPrune(depthTwoLeftRight, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(
                    FBSTNode.of(false,
                        lEntry,
                    FBSTUnique.empty,
                    fbtAssertNodeInvariant(FBSTNode.of(false,mEntry))
                )
                )
            )
            )
        bstPrune(depthTwoLeftRight, mEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(false,lEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(false,sEntry))
            )
            )
        bstPrune(depthTwoLeftRight, lEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                FBSTUnique.empty,
                fbtAssertNodeInvariant(FBSTNode.of(false,sEntry))
            )
            )
        bstPrune(depthTwoLeftRight, nEntry) shouldBe FBSTUnique.empty

        bstPrune(depthTwoLeftLeft, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(
                    FBSTNode.of(false,
                        lEntry,
                    fbtAssertNodeInvariant(FBSTNode.of(false,eEntry)),
                    FBSTUnique.empty
                    )
                )
            )
            )
        bstPrune(depthTwoLeftLeft, eEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(false,lEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(false,sEntry))
            )
            )
        bstPrune(depthTwoLeftLeft, lEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                FBSTUnique.empty,
                fbtAssertNodeInvariant(FBSTNode.of(false,sEntry))
            )
            )
        bstPrune(depthTwoLeftLeft, nEntry) shouldBe FBSTUnique.empty

        bstPrune(depthTwoRightRight, uEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(false,mEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(false,sEntry))
            )
            )
        bstPrune(depthTwoRightRight, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(false,mEntry)),
                FBSTUnique.empty
            )
            )
        bstPrune(depthTwoRightRight, mEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                FBSTUnique.empty,
                fbtAssertNodeInvariant(
                    FBSTNode.of(false,
                        sEntry,
                    FBSTUnique.empty,
                    fbtAssertNodeInvariant(FBSTNode.of(false,uEntry)))
                ))
            )
        bstPrune(depthTwoRightRight, nEntry) shouldBe FBSTUnique.empty

        bstPrune(depthTwoRightLeft, rEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(false,mEntry)),
                fbtAssertNodeInvariant(FBSTNode.of(false,sEntry))
            )
            )
        bstPrune(depthTwoRightLeft, sEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                fbtAssertNodeInvariant(FBSTNode.of(false,mEntry)),
                FBSTUnique.empty
            )
            )
        bstPrune(depthTwoRightLeft, mEntry) shouldBe
            fbtAssertNodeInvariant(
                FBSTNode.of(false,
                    nEntry,
                FBSTUnique.empty,
                fbtAssertNodeInvariant(
                    FBSTNode.of(false,
                        sEntry,
                    fbtAssertNodeInvariant(FBSTNode.of(false,rEntry)),
                    FBSTUnique.empty
                    )
                )
            )
            )
        bstPrune(depthTwoRightLeft, nEntry) shouldBe FBSTUnique.empty
    }

    test("co.addGraftTesting") {
        addGraftTestingGremlin(FBSTGeneric.empty, FBSTGeneric.empty).shouldBeInstanceOf<FBSTGeneric>()
        addGraftTestingGremlin(FBSTUnique.empty, FBSTGeneric.empty).shouldBeInstanceOf<FBSTUnique>()
        addGraftTestingGremlin(FBSTGeneric.empty, FBSTUnique.empty).shouldBeInstanceOf<FBSTGeneric>()
        addGraftTestingGremlin(FBSTUnique.empty, FBSTUnique.empty).shouldBeInstanceOf<FBSTUnique>()

        addGraftTestingGremlin(depthOneFull, FBSTUnique.empty) shouldBe depthOneFull
        (addGraftTestingGremlin(depthOneFull, FBSTUnique.empty) === depthOneFull) shouldBe true 
        addGraftTestingGremlin(depthOneFull, FBSTGeneric.empty) shouldBe depthOneFull
        (addGraftTestingGremlin(depthOneFull, FBSTGeneric.empty) === depthOneFull) shouldBe true
        
        addGraftTestingGremlin(FBSTGeneric.empty, depthOneFull) shouldBe depthOneFull
        val depthOneFullLoose = depthOneFull.toGeneric()
        (addGraftTestingGremlin(FBSTGeneric.empty, depthOneFullLoose) === depthOneFullLoose) shouldBe true
        (addGraftTestingGremlin(FBSTGeneric.empty, depthOneFull) === depthOneFull) shouldBe false
        addGraftTestingGremlin(FBSTGeneric.empty, depthOneFull).shouldBeInstanceOf<FBSTNodeGeneric<*,*>>()

        addGraftTestingGremlin(FBSTUnique.empty, depthOneFull) shouldBe depthOneFull
        (addGraftTestingGremlin(FBSTUnique.empty, depthOneFull) === depthOneFull) shouldBe true
        (addGraftTestingGremlin(FBSTUnique.empty, depthOneFull.toGeneric()) === depthOneFull) shouldBe false
        addGraftTestingGremlin(FBSTUnique.empty, depthOneFull).shouldBeInstanceOf<FBSTNodeUnique<*,*>>()

        addGraftTestingGremlin(bstPrune(depthOneFull, nEntry), FBSTNode.of(false,nEntry)) shouldBe depthOneFull
        addGraftTestingGremlin(bstPrune(depthOneFull, lEntry), FBSTNode.of(false,lEntry)) shouldBe depthOneFull

        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, mEntry), FBSTNode.of(false,mEntry)) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, mEntry), FBSTNode.of(true,mEntry)) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, sEntry), bstFind(depthTwoLeftRight, sEntry)!!) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, lEntry), bstFind(depthTwoLeftRight, lEntry)!!) shouldBe depthTwoLeftRight

        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, eEntry), FBSTNode.of(false,eEntry)) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, eEntry), FBSTNode.of(true,eEntry)) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, sEntry), bstFind(depthTwoLeftLeft, sEntry)!!) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, lEntry), bstFind(depthTwoLeftLeft, lEntry)!!) shouldBe depthTwoLeftLeft

        addGraftTestingGremlin(bstPrune(depthTwoRightRight, uEntry), FBSTNode.of(false,uEntry)) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, uEntry), FBSTNode.of(true,uEntry)) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, sEntry), bstFind(depthTwoRightRight, sEntry)!!) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, mEntry), bstFind(depthTwoRightRight, mEntry)!!) shouldBe depthTwoRightRight

        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, rEntry), FBSTNode.of(false,rEntry)) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, rEntry), FBSTNode.of(true,rEntry)) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, sEntry), bstFind(depthTwoRightLeft, sEntry)!!) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, mEntry), bstFind(depthTwoRightLeft, mEntry)!!) shouldBe depthTwoRightLeft
    }

    test("co.toArray") {
        Arb.fbsItree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val ary: Array<TKVEntry<Int, Int>> = toArray(fbst)
            fbst shouldBe of(ary.iterator())
            fbst shouldBe of(*ary)
        }
    }

})
