package com.xrpn.immutable

import com.xrpn.imapi.IMBTreeEqual2
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
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree
import io.kotest.xrpn.fset
import kotlin.random.Random

private val intFrbtOfNone = FRBTree.ofvi(*arrayOf<Int>())
private val intFrbtOfOne = FRBTree.ofvi(*arrayOf<Int>(1))
private val intFrbtOfTwo = FRBTree.ofvi(*arrayOf<Int>(1,2))
private val intFrbtOfThree = FRBTree.ofvi(*arrayOf<Int>(2,1,3))
private val strFrbtOfNone = FRBTree.ofvs(*arrayOf<String>())
private val strFrbtOfOne = FRBTree.ofvs(*arrayOf<String>("a"))
private val strFrbtOfTwo = FRBTree.ofvs(*arrayOf<String>("a","b"))
private val strFrbtOfThree = FRBTree.ofvs(*arrayOf<String>("b","a","c"))

private val frbtOfOneX = FRBTree.ofvs(*arrayOf<Int>(1))
private val frbtOfOneY = FRBTree.ofvi(*arrayOf<String>("a"))

private val fbstOfNone = FBSTree.ofvi(*arrayOf<Int>())
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

        intFrbtOfTwo.equals(FIKSetBody.of(intFrbtOfNone)) shouldBe false
        intFrbtOfTwo.equals(FIKSetBody.of(intFrbtOfOne)) shouldBe false
        intFrbtOfTwo.equals(FIKSetBody.of(intFrbtOfTwo)) shouldBe false // true
        intFrbtOfTwo.equals(FIKSetBody.of(frbtOfOneY)) shouldBe false

        intFrbtOfTwo.equals(FIKSetBody.of(intFrbtOfNone).copyToMutableSet()) shouldBe false
        intFrbtOfTwo.equals(FIKSetBody.of(intFrbtOfOne).copyToMutableSet()) shouldBe false
        intFrbtOfTwo.equals(FIKSetBody.of(intFrbtOfTwo).copyToMutableSet()) shouldBe false
        intFrbtOfTwo.equals(FIKSetBody.of(frbtOfOneY).copyToMutableSet()) shouldBe false

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
            FRBTNode.hashCode(intFrbtOfTwo as FRBTNode<Int,Int>) shouldBe intFrbtOfTwo.hashCode()
        }
    }

    // =========================== IMBTreeCompanion

    test("co.empty") {
        emptyIMBTree<Int, Int>() shouldBe FRBTNil
    }

    test("co.[ IMBTreeEqual2 ]") {
        IMBTreeEqual2 (intFrbtOfNone, ofvi(*arrayOf<Int>())) shouldBe true
        IMBTreeEqual2 (intFrbtOfNone, intFrbtOfNone) shouldBe true
        IMBTreeEqual2 (ofvi(*arrayOf(1)), ofvi(*arrayOf<Int>())) shouldBe false
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
        of(*arrayOf<TKVEntry<Int,Int>>()) shouldBe FRBTree.emptyIMBTree()
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of iterator") {
        of(emptyList<TKVEntry<Int,Int>>().iterator()) shouldBe FRBTree.emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of IMList") {
        of<Int, Int>(FLNil) shouldBe FRBTNil
        of(FList.of(*arrayOf(mEntry,lEntry,nEntry))) shouldBe FRBTNode(mEntry, BLACK, FRBTNode(lEntry, BLACK), FRBTNode(nEntry, BLACK))
        // TWO                             vvvvvv               vvvvvv
        of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry))) shouldBe
                FRBTNode(mEntry, BLACK,
                    FRBTNode(cEntry, RED,
                        // ONE   vvvvvv
                        FRBTNode(bEntry, BLACK),
                        FRBTNode(dEntry, BLACK)),
                    FRBTNode(zEntry, BLACK))
        of(wikiPreorder) shouldBe frbWikiTree
        of(slideSharePreorder) shouldBe frbSlideShareTree
    }

    test("co.ofc varargs") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int,Int>>()) shouldBe FRBTree.emptyIMBTree()
        ofc(reverseStrCompare, 3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).inorder() shouldBe
                FList.of(3.toSAEntry(),2.toSAEntry(),1.toSAEntry())
    }

    test("co.ofc iterator") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int,Int>>().iterator()) shouldBe FRBTree.emptyIMBTree()
        ofc(reverseStrCompare, listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator()).inorder() shouldBe
                listOf(3.toSAEntry(),2.toSAEntry(),1.toSAEntry())
    }

    test("co.ofvi varargs") {
        ofvi(*arrayOf<Int>()) shouldBe FRBTree.emptyIMBTree()
        ofvi(3, 2, 1, 1).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi iterator") {
        ofvi(arrayOf<Int>().iterator()) shouldBe FRBTree.emptyIMBTree()
        ofvi(arrayOf(3,2,1,1).iterator()).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
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
        ofvi(FList.emptyIMList()) shouldBe FRBTree.emptyIMBTree()
        ofvi(FList.of(3,2,1,1)).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvs varargs") {
        ofvs(*arrayOf<Int>()) shouldBe FRBTree.emptyIMBTree()
        ofvs(3, 2, 1, 1).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs iterator") {
        ofvs(arrayOf<Int>().iterator()) shouldBe FRBTree.emptyIMBTree()
        ofvs(arrayOf(3,2,1,1).iterator()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs IMList") {
        ofvs(FList.emptyIMList()) shouldBe FRBTree.emptyIMBTree()
        ofvs(FList.of(3,2,1,1)).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofMap ABCD Iterator") {
        ofMap(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv -> TKVEntry.of(tkv.getk(), tkv.getv().toString()) } shouldBe emptyIMBTree()
    }

    test("co.ofviMap Iterator") {
        ofviMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofviMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder() shouldBe
                listOf(TKVEntry.of(TKVEntry.intKeyOf("1"), "1"),TKVEntry.of(TKVEntry.intKeyOf("2"), "2"),TKVEntry.of(
                    TKVEntry.intKeyOf("3"), "3"))
    }

    test("co.ofvsMap Iterator") {
        ofvsMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofvsMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder() shouldBe
                listOf(TKVEntry.of("1", "1"),TKVEntry.of("2", "2"),TKVEntry.of("3", "3"))
    }

    test("co.toIMBTree Collection") {
        Arb.list(Arb.int()).checkAll(repeats) { l ->
            val sim = l.map{ it.toIAEntry() }.toSet()
            sim.toIMBTree() shouldBe ofvi(l.iterator())
            sim.toIMBTree() shouldBe ofvi(*(l.toTypedArray()))
        }
        Arb.list(Arb.int((0..20))).checkAll(repeats) { l ->
            val ssm = l.map{ it.toSAEntry() }.toSet()
            ssm.toIMBTree() shouldBe ofvs(l.iterator())
            ssm.toIMBTree() shouldBe ofvs(*(l.toTypedArray()))
        }
        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
            val lv = frbt.postorderValues().copyToMutableList().map{ it.toIAEntry() }
            frbt.toIMBTree() shouldBe lv.toIMBTree()
        }
        Arb.fset<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
            val c: Collection<TKVEntry<Int, Int>> = fs.fmap { it.toIAEntry() }
            c.toIMBTree().equals(fs.toIMBTree()) shouldBe true
        }
    }

    test("co.toIMBTree Map") {
        Arb.list(Arb.int()).checkAll(repeats) { l ->
            fun f(t: Int): Pair<Int, String> = Pair(t, (-t).toString())
            val m: Map<Int, String> = l.associate(::f)
            m.toIMBTree() shouldBe ofMap(l.map{ TKVEntry.of(it, -it) }.iterator()) { tkv ->
                TKVEntry.of(tkv.getk(), tkv.getv().toString())
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

