package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IMBTreeEqual2
import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.NOT_FOUND
import com.xrpn.immutable.FRBTree.Companion.BLACK
import com.xrpn.immutable.FRBTree.Companion.RED
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.of
import com.xrpn.immutable.FRBTree.Companion.ofMap
import com.xrpn.immutable.FRBTree.Companion.ofvi
import com.xrpn.immutable.FRBTree.Companion.ofvs
import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import com.xrpn.immutable.FRBTree.Companion.ofc
import com.xrpn.immutable.FRBTree.Companion.ofviMap
import com.xrpn.immutable.FRBTree.Companion.ofvsMap
import com.xrpn.immutable.FRBTree.Companion.toIMBTree
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import com.xrpn.immutable.bEntry
import com.xrpn.immutable.cEntry
import com.xrpn.immutable.dEntry
import com.xrpn.immutable.frbSlideShareTree
import com.xrpn.immutable.frbWikiTree
import com.xrpn.immutable.lEntry
import com.xrpn.immutable.mEntry
import com.xrpn.immutable.nEntry
import com.xrpn.immutable.slideSharePreorder
import com.xrpn.immutable.wikiPreorder
import com.xrpn.immutable.zEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree
import kotlin.random.Random

private val intFrbtOfNone = FRBTree.ofvi(*emptyArrayOfInt)
private val intFrbtOfOne = FRBTree.ofvi(*arrayOf<Int>(1))
private val intFrbtOfTwo = FRBTree.ofvi(*arrayOf<Int>(1,2))
private val intFrbtOfThree = FRBTree.ofvi(*arrayOf<Int>(2,1,3))
private val strFrbtOfNone = FRBTree.ofvs(*emptyArrayOfStr)
private val strFrbtOfOne = FRBTree.ofvs(*arrayOf<String>("a"))
private val strFrbtOfTwo = FRBTree.ofvs(*arrayOf<String>("a","b"))
private val strFrbtOfThree = FRBTree.ofvs(*arrayOf<String>("b","a","c"))

private val frbtOfOneX = FRBTree.ofvs(*arrayOf<Int>(1))
private val frbtOfOneY = FRBTree.ofvi(*arrayOf<String>("a"))

private val fbstOfNone = FBSTree.ofvi(*emptyArrayOfInt)
private val fbstOfOneX = FBSTree.ofvs(*arrayOf<Int>(1))
private val fbstOfOneY = FBSTree.ofvi(*arrayOf<String>("a"))

class FRBTreeCompanionTest : FunSpec({

    val repeats = 10
    val repeatsHigh = Pair(50, 100)

    beforeTest {}

    test("equals") {
        emptyIMBTree<Int, Int>().equals(null) shouldBe false
        emptyIMBTree<Int, Int>().equals(emptyIMBTree<Int, Int>()) shouldBe true
        emptyIMBTree<Int, Int>().equals(1) shouldBe false
        /* Sigh... */ intFrbtOfNone.equals(strFrbtOfNone) shouldBe true

        intFrbtOfTwo.equals(null) shouldBe false
        intFrbtOfTwo.equals(intFrbtOfNone) shouldBe false
        intFrbtOfTwo.equals(intFrbtOfOne) shouldBe false
        intFrbtOfTwo.equals(intFrbtOfTwo) shouldBe true

        intFrbtOfTwo.equals(frbtOfOneX) shouldBe false
        intFrbtOfTwo.equals(frbtOfOneY) shouldBe false

        intFrbtOfTwo.equals(fbstOfNone) shouldBe false
        intFrbtOfTwo.equals(fbstOfOneX) shouldBe false
        intFrbtOfTwo.equals(fbstOfOneY) shouldBe false

        intFrbtOfTwo.equals(strFrbtOfNone) shouldBe false
        intFrbtOfTwo.equals(strFrbtOfOne) shouldBe false
        intFrbtOfTwo.equals(strFrbtOfTwo) shouldBe false

        intFrbtOfTwo.equals(ofFIKSBody(intFrbtOfNone)) shouldBe false
        intFrbtOfTwo.equals(ofFIKSBody(intFrbtOfOne)) shouldBe false
        intFrbtOfTwo.equals(ofFIKSBody(intFrbtOfTwo)) shouldBe false // true
        intFrbtOfTwo.equals(ofFIKSBody(frbtOfOneY)) shouldBe false

        intFrbtOfTwo.equals(ofFIKSBody(intFrbtOfNone).copyToMutableSet()) shouldBe false
        intFrbtOfTwo.equals(ofFIKSBody(intFrbtOfOne).copyToMutableSet()) shouldBe false
        intFrbtOfTwo.equals(ofFIKSBody(intFrbtOfTwo).copyToMutableSet()) shouldBe false
        intFrbtOfTwo.equals(ofFIKSBody(frbtOfOneY).copyToMutableSet()) shouldBe false

        intFrbtOfTwo.equals(intFrbtOfNone.ffold(mutableSetOf<TKVEntry<Int, Int>>()) { acc, tkv -> acc.add(tkv); acc }) shouldBe false
        intFrbtOfTwo.equals(intFrbtOfOne.ffold(mutableSetOf<TKVEntry<Int, Int>>()) { acc, tkv -> acc.add(tkv); acc }) shouldBe false
        intFrbtOfTwo.equals(intFrbtOfTwo.ffold(mutableSetOf<TKVEntry<Int, Int>>()) { acc, tkv -> acc.add(tkv); acc }) shouldBe false // true
        intFrbtOfTwo.equals(frbtOfOneY.ffold(mutableSetOf<TKVEntry<Int, String>>()) { acc, tkv -> acc.add(tkv); acc }) shouldBe false

        intFrbtOfTwo.equals(emptyList<Int>()) shouldBe false
        intFrbtOfTwo.equals(listOf("foobar")) shouldBe false
        intFrbtOfTwo.equals(listOf("foobar","babar")) shouldBe false
        intFrbtOfTwo.equals(1) shouldBe false

    }

    test("toString() hashCode()") {
        emptyIMBTree<Int, Int>().toString() shouldBe "*"
        val aux = emptyIMBTree<Int, Int>().hashCode()
        for (i in (1..100)) {
             aux shouldBe emptyIMBTree<Int, Int>().hashCode()
        }
        intFrbtOfTwo.toString() shouldStartWith "([ "
        val aux2 = intFrbtOfTwo.hashCode()
        for (i in (1..100)) {
             aux2 shouldBe intFrbtOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FRBTNode.hashCode(intFrbtOfTwo as FRBTNode<Int, Int>) shouldBe intFrbtOfTwo.hashCode()
        }
    }

    // =========================== IMBTreeCompanion

    test("co.empty") {
        emptyIMBTree<Int, Int>() shouldBe FRBTNil
    }

    test("co.[ IMBTreeEqual2 ]") {
        IMBTreeEqual2 (intFrbtOfNone, ofvi(*emptyArrayOfInt)) shouldBe true
        IMBTreeEqual2 (intFrbtOfNone, intFrbtOfNone) shouldBe true
        IMBTreeEqual2 (ofvi(*arrayOf(1)), ofvi(*emptyArrayOfInt)) shouldBe false
        IMBTreeEqual2 (intFrbtOfNone, ofvi(*arrayOf(1))) shouldBe false
        IMBTreeEqual2 (intFrbtOfOne, ofvi(*arrayOf<Int>(1))) shouldBe true
        IMBTreeEqual2 (intFrbtOfOne, intFrbtOfOne) shouldBe true
        IMBTreeEqual2 (ofvi(*arrayOf(1)), ofvi(*arrayOf<Int>(1, 2))) shouldBe false
        IMBTreeEqual2 (ofvi(*arrayOf<Int>(1, 2)), ofvi(*arrayOf(1))) shouldBe false
        IMBTreeEqual2 (ofvi(*arrayOf<Int>(1, 2)), ofvi(*arrayOf(1, 2))) shouldBe true
        IMBTreeEqual2 (ofvi(*arrayOf<Int>(1, 2)), ofvi(*arrayOf(2, 1))) shouldBe true
        IMBTreeEqual2 (intFrbtOfThree, ofvi(*arrayOf(1, 3, 2))) shouldBe true
        IMBTreeEqual2 (intFrbtOfThree, ofvi(*arrayOf(2, 3, 1))) shouldBe true
        IMBTreeEqual2 (intFrbtOfThree, ofvi(*arrayOf(2, 1, 3))) shouldBe true
        IMBTreeEqual2 (intFrbtOfThree, ofvi(*arrayOf(3, 1, 2))) shouldBe true
        IMBTreeEqual2 (intFrbtOfThree, ofvi(*arrayOf(3, 2, 1))) shouldBe true
    }
    
    test("co.of varargs") {
        of(*arrayOf<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree()
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of iterator") {
        of(emptyList<TKVEntry<Int, Int>>().iterator()) shouldBe FRBTree.emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.of IMList") {
        of<Int, Int>(FLNil) shouldBe FRBTNil
        (of<Int, Int>(FLNil) === FRBTNil) shouldBe true
        of(FList.of(*arrayOf(mEntry, lEntry, nEntry))) shouldBe FRBTNode.of(
            mEntry,
            BLACK,
            FRBTNode.of(lEntry, BLACK),
            FRBTNode.of(nEntry, BLACK)
        )
        // TWO                             vvvvvv               vvvvvv
        of(FList.of(*arrayOf(mEntry, cEntry, bEntry, dEntry, zEntry, bEntry))) shouldBe
                FRBTNode.of(
                    mEntry, BLACK,
                    FRBTNode.of(
                        cEntry, RED,
                        // ONE   vvvvvv
                        FRBTNode.of(bEntry, BLACK),
                        FRBTNode.of(dEntry, BLACK)
                    ),
                    FRBTNode.of(zEntry, BLACK)
                )
        of(wikiPreorder) shouldBe frbWikiTree
        of(slideSharePreorder) shouldBe frbSlideShareTree
    }

    test("co.ofc varargs") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree()
        ofc(reverseStrCompare, 3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).inorder().softEqual(
                FList.of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry())) shouldBe true
    }

    test("co.ofc iterator") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int, Int>>().iterator()) shouldBe emptyIMBTree()
        ofc(reverseStrCompare, listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator()).inorder().softEqual(
                listOf(3.toSAEntry(),2.toSAEntry(),1.toSAEntry())) shouldBe true
    }

    test("co.ofvi varargs") {
        ofvi(*emptyArrayOfInt) shouldBe FRBTree.emptyIMBTree()
        ofvi(3, 2, 1, 1).inorder().softEqual(listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvi iterator") {
        ofvi(emptyArrayOfInt.iterator()) shouldBe emptyIMBTree()
        ofvi(arrayOf(3,2,1,1).iterator()).inorder().softEqual(listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvi iterator (property)") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = IntArray(n) { _: Int -> Random.nextInt() }
            val rbt = ofvi(values.iterator())
            val oracle: Set<Int> = values.toSet()
            rbt.size shouldBe oracle.size
            for (item in oracle) {
                rbt.fcontains(item.toIAEntry()) shouldBe true
            }
        }
    }

    test("co.ofvi IMList") {
        ofvi(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvi(FList.of(3, 2, 1, 1)).inorder().softEqual(listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())) shouldBe true
    }

    test("co.ofvs varargs") {
        ofvs(*emptyArrayOfInt) shouldBe emptyIMBTree()
        ofvs(3, 2, 1, 1).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofvs iterator") {
        ofvs(emptyArrayOfInt.iterator()) shouldBe emptyIMBTree()
        ofvs(arrayOf(3,2,1,1).iterator()).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofvs IMList") {
        ofvs(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvs(FList.of(3, 2, 1, 1)).inorder().softEqual(listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())) shouldBe true
    }

    test("co.ofMap ABCD Iterator") {
        ofMap(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv -> TKVEntry.ofkv(tkv.getk(), tkv.getv().toString()) } shouldBe emptyIMBTree()
    }

    test("co.ofviMap Iterator") {
        ofviMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofviMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder().softEqual(
                listOf(
                    TKVEntry.ofkv(TKVEntry.intKeyOf("1"), "1"),
                    TKVEntry.ofkv(TKVEntry.intKeyOf("2"), "2"),
                    TKVEntry.ofkv(
                        TKVEntry.intKeyOf("3"), "3"
                    )
                )) shouldBe true
    }

    test("co.ofvsMap Iterator") {
        ofvsMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofvsMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder().softEqual(
                listOf(TKVEntry.ofkk("1", "1"), TKVEntry.ofkk("2", "2"), TKVEntry.ofkk("3", "3"))) shouldBe true
    }

//    test("co.toIMBTree Collection") {
//        Arb.list(Arb.int()).checkAll(repeats) { l ->
//            val sim = l.map{ it.toIAEntry() }.toSet()
//            sim.toIMBTree() shouldBe ofvi(l.iterator())
//            sim.toIMBTree() shouldBe ofvi(*(l.toTypedArray()))
//        }
//        Arb.list(Arb.int((0..20))).checkAll(repeats) { l ->
//            val ssm = l.map{ it.toSAEntry() }.toSet()
//            ssm.toIMBTree() shouldBe ofvs(l.iterator())
//            ssm.toIMBTree() shouldBe ofvs(*(l.toTypedArray()))
//        }
//        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
//            val lv = frbt.postorderValues().copyToMutableList().map{ it.toIAEntry() }
//            frbt.toIMBTree() shouldBe lv.toIMBTree()
//        }
//        Arb.fset<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
//            val c: Collection<TKVEntry<Int, Int>> = fs.fmap { it.toIAEntry() }
//            c.toIMBTree().equals(fs.toIMBTree()) shouldBe true
//        }
//    }

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
        FRBTree.fcontainsIK(intFrbtOfThree, 1) shouldBe true
        FRBTree.fcontainsIK(intFrbtOfThree, 2) shouldBe true
        FRBTree.fcontainsIK(intFrbtOfThree, 3) shouldBe true
        FRBTree.fcontainsIK(intFrbtOfThree, 0) shouldBe false
        FRBTree.fcontainsIK(intFrbtOfThree, 4) shouldBe false
    }

    test("co.fdeleteIK") {
        FRBTree.fdeleteIK(intFrbtOfThree, 3) shouldBe intFrbtOfTwo
    }

    test("co.ffindIK") {
        FRBTree.ffindIK(intFrbtOfThree, 1) shouldBe ofvi(1)
        FRBTree.ffindIK(intFrbtOfThree, 2) shouldBe intFrbtOfThree
        FRBTree.ffindIK(intFrbtOfThree, 3) shouldBe ofvi(3)
        FRBTree.ffindIK(intFrbtOfThree, 0) shouldBe null
        FRBTree.ffindIK(intFrbtOfThree, 4) shouldBe null
    }

    test("co.ffindLastIK") {
        FRBTree.ffindLastIK(intFrbtOfThree, 1) shouldBe ofvi(1)
        FRBTree.ffindLastIK(intFrbtOfThree, 2) shouldBe intFrbtOfThree
        FRBTree.ffindLastIK(intFrbtOfThree, 3) shouldBe ofvi(3)
        FRBTree.ffindLastIK(intFrbtOfThree, 0) shouldBe null
        FRBTree.ffindLastIK(intFrbtOfThree, 4) shouldBe null
    }

    test("co.finsertIK") {
        FRBTree.finsertIK(intFrbtOfTwo, 3) shouldBe intFrbtOfThree
    }

    test("co.fcontainsSK") {
        FRBTree.fcontainsSK(strFrbtOfThree, "a") shouldBe true
        FRBTree.fcontainsSK(strFrbtOfThree, "b") shouldBe true
        FRBTree.fcontainsSK(strFrbtOfThree, "c") shouldBe true
        FRBTree.fcontainsSK(strFrbtOfThree, "d") shouldBe false
    }

    test("co.fdeleteSK") {
        FRBTree.fdeleteSK(strFrbtOfThree, "c") shouldBe strFrbtOfTwo
    }

    test("co.ffindSK") {
        FRBTree.ffindSK(strFrbtOfThree, "a") shouldBe ofvs("a")
        FRBTree.ffindSK(strFrbtOfThree, "b") shouldBe strFrbtOfThree
        FRBTree.ffindSK(strFrbtOfThree, "c") shouldBe ofvs("c")
        FRBTree.ffindSK(strFrbtOfThree, "d") shouldBe null
    }

    test("co.ffindLastSK") {
        FRBTree.ffindLastSK(strFrbtOfThree, "a") shouldBe ofvs("a")
        FRBTree.ffindLastSK(strFrbtOfThree, "b") shouldBe strFrbtOfThree
        FRBTree.ffindLastSK(strFrbtOfThree, "c") shouldBe ofvs("c")
        FRBTree.ffindLastSK(strFrbtOfThree, "d") shouldBe null
    }

    test("co.finsertSK") {
        FRBTree.finsertSK(strFrbtOfTwo, "c") shouldBe strFrbtOfThree
    }

    // =========================== implementation

    test("co.NOT_FOUND") {
        NOT_FOUND shouldBe -1
    }

    test("co.nul") {
        nul<Int, Int>() shouldBe FRBTNil
    }

    test("co.toArray") {
        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
            val ary: Array<TKVEntry<Int, Int>> = FRBTree.toArray(frbt)
            frbt shouldBe of(ary.iterator())
            frbt shouldBe of(*ary)
        }
    }
})

