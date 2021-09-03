package com.xrpn.immutable

import com.xrpn.imapi.IMBTreeEqual2
import com.xrpn.immutable.FBSTree.Companion.NOT_FOUND
import com.xrpn.immutable.FBSTree.Companion.addGraftTestingGremlin
import com.xrpn.immutable.FBSTree.Companion.bstDelete
import com.xrpn.immutable.FBSTree.Companion.bstFind
import com.xrpn.immutable.FBSTree.Companion.bstInsert
import com.xrpn.immutable.FBSTree.Companion.bstPrune
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FBSTree.Companion.fbtAssert
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.of
import com.xrpn.immutable.FBSTree.Companion.ofMap
import com.xrpn.immutable.FBSTree.Companion.ofc
import com.xrpn.immutable.FBSTree.Companion.ofvi
import com.xrpn.immutable.FBSTree.Companion.ofviMap
import com.xrpn.immutable.FBSTree.Companion.ofvs
import com.xrpn.immutable.FBSTree.Companion.ofvsMap
import com.xrpn.immutable.FBSTree.Companion.toIMBTree
import com.xrpn.immutable.TKVEntry.Companion.intKeyOf
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstree
import io.kotest.xrpn.fbstreeAllowDups
import io.kotest.xrpn.frbtree
import io.kotest.xrpn.fset
import kotlin.random.Random.Default.nextInt

private val intFbstOfNone = FBSTree.ofvi(*arrayOf<Int>())
private val intFbstOfOne = FBSTree.ofvi(*arrayOf<Int>(1))
private val intFbstOfTwo = FBSTree.ofvi(*arrayOf<Int>(1,2))
private val intFbstOfThree = FBSTree.ofvi(*arrayOf<Int>(2,1,3))
private val intFbstOfFourA = FBSTree.ofvi(*arrayOf<Int>(2,1,3,1), allowDups = true)
private val strFbstOfNone = FBSTree.ofvs(*arrayOf<String>())
private val strFbstOfOne = FBSTree.ofvs(*arrayOf<String>("a"))
private val strFbstOfTwo = FBSTree.ofvs(*arrayOf<String>("a","b"))
private val strFbstOfThree = FBSTree.ofvs(*arrayOf<String>("b","a","c"))
private val strFbstOfFourA = FBSTree.ofvs(*arrayOf<String>("b","a","c","a"), allowDups = true)

private val frbtOfNone = FRBTree.ofvi(*arrayOf<Int>())
private val frbtOfOneX = FRBTree.ofvs(*arrayOf<Int>(1))
private val frbtOfOneY = FRBTree.ofvi(*arrayOf<String>("A"))

private val fbstOfOneX = FBSTree.ofvs(*arrayOf<Int>(1))
private val fbstOfOneY = FBSTree.ofvi(*arrayOf<String>("A"))

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

    test("toString") {
        emptyIMBTree<Int,Int>().toString() shouldBe "FBSTNil"
        for (i in (1..100)) {
            emptyIMBTree<Int,Int>().hashCode() shouldBe emptyIMBTree<Int,Int>().hashCode()
        }
        intFbstOfTwo.toString() shouldStartWith "${FBSTree::class.simpleName}@"
        for (i in (1..100)) {
            intFbstOfTwo.hashCode() shouldBe intFbstOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FBSTNode.hashCode(intFbstOfTwo as FBSTNode<Int,Int>) shouldBe intFbstOfTwo.hashCode()
        }
    }

    // =========================== IMBTreeCompanion

    test("co.empty") {
        emptyIMBTree<Int, Int>() shouldBe FBSTNil
    }

    test("co.[ IMBTreeEqual2 ]") {
        IMBTreeEqual2 (intFbstOfNone, FBSTree.ofvi(*arrayOf<Int>())) shouldBe true
        IMBTreeEqual2 (intFbstOfNone, intFbstOfNone) shouldBe true
        IMBTreeEqual2 (FBSTree.ofvi(*arrayOf(1)), FBSTree.ofvi(*arrayOf<Int>())) shouldBe false
        IMBTreeEqual2 (intFbstOfNone, FBSTree.ofvi(*arrayOf(1))) shouldBe false
        IMBTreeEqual2 (intFbstOfOne, FBSTree.ofvi(*arrayOf<Int>(1))) shouldBe true
        IMBTreeEqual2 (intFbstOfOne, intFbstOfOne) shouldBe true
        IMBTreeEqual2 (FBSTree.ofvi(*arrayOf(1)), FBSTree.ofvi(*arrayOf<Int>(1, 2))) shouldBe false
        IMBTreeEqual2 (FBSTree.ofvi(*arrayOf<Int>(1, 2)), FBSTree.ofvi(*arrayOf(1))) shouldBe false
        IMBTreeEqual2 (FBSTree.ofvi(*arrayOf<Int>(1, 2)), FBSTree.ofvi(*arrayOf(1, 2))) shouldBe true
        IMBTreeEqual2 (FBSTree.ofvi(*arrayOf<Int>(1, 2)), FBSTree.ofvi(*arrayOf(2, 1))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, FBSTree.ofvi(*arrayOf(1, 3, 2))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, FBSTree.ofvi(*arrayOf(2, 3, 1))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, FBSTree.ofvi(*arrayOf(2, 1, 3))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, FBSTree.ofvi(*arrayOf(3, 1, 2))) shouldBe true
        IMBTreeEqual2 (intFbstOfThree, FBSTree.ofvi(*arrayOf(3, 2, 1))) shouldBe true
    }


    test("co.of varargs A") {
        of(*arrayOf<TKVEntry<Int,Int>>()) shouldBe emptyIMBTree()
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

    test("co.of varargs dups") {
        of(*arrayOf<TKVEntry<Int,Int>>(), allowDups = true) shouldBe emptyIMBTree()
        of(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry(), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of iterator") {
        of(emptyList<TKVEntry<Int,Int>>().iterator()) shouldBe emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of iterator dups") {
        of(emptyList<TKVEntry<Int,Int>>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        of(listOf(3.toSAEntry(), 2.toSAEntry(), 1.toSAEntry(), 1.toSAEntry()).iterator(), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.of IMList (dups and no dups)") {
        of<Int, Int>(FLNil) shouldBe FBSTNil
        of(FList.of(*arrayOf(mEntry,lEntry,nEntry))) shouldBe FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(nEntry))
        of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry)), allowDups = true) shouldBe
                FBSTNode(mEntry,
                    FBSTNode(cEntry,
                        FBSTNode(bEntry, FBSTNil,
                            FBSTNode(bEntry, FBSTNil, FBSTNil)),
                        FBSTNode(dEntry, FBSTNil, FBSTNil)),
                    FBSTNode(zEntry, FBSTNil, FBSTNil))
        of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry)) /*, allowDups = false */) shouldBe
                FBSTNode(mEntry,
                    FBSTNode(cEntry,
                        FBSTNode(bEntry, FBSTNil, FBSTNil),
                        FBSTNode(dEntry, FBSTNil, FBSTNil)),
                    FBSTNode(zEntry, FBSTNil, FBSTNil))
        of(wikiPreorder) shouldBe wikiTree
        of(slideSharePreorder) shouldBe slideShareTree
    }

    test("co.ofc varargs") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int,Int>>()) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, 3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry())
    }

    test("co.ofc varargs dups") {
        ofc(reverseIntCompare, *arrayOf<TKVEntry<Int,Int>>(), allowDups = true) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, 3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry(), allowDups = true).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry(),1.toIAEntry())
    }

    test("co.ofc iterator") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int,Int>>().iterator()) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, listOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).iterator()).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry())
    }

    test("co.ofc iterator dups") {
        ofc(reverseIntCompare, emptyList<TKVEntry<Int,Int>>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        ofc(reverseIntCompare, listOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry(), 1.toIAEntry()).iterator(), allowDups = true).inorder() shouldBe listOf(3.toIAEntry(),2.toIAEntry(),1.toIAEntry(),1.toIAEntry())
    }

    test("co.ofvi varargs") {
        ofvi(*arrayOf<Int>()) shouldBe emptyIMBTree()
        ofvi(3, 2, 1, 1).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi varargs dups") {
        ofvi(*arrayOf<Int>()) shouldBe emptyIMBTree()
        ofvi(3, 2, 1, 1, allowDups = true).inorder() shouldBe listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi iterator") {
        ofvi(arrayOf<Int>().iterator()) shouldBe emptyIMBTree()
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
        ofvi(arrayOf<Int>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        ofvi(arrayOf(3,2,1,1).iterator(), allowDups = true).inorder() shouldBe listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi IMList") {
        ofvi(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvi(FList.of(3,2,1,1)).inorder() shouldBe listOf(1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvi IMList dups") {
        ofvi(FList.emptyIMList(), allowDups = true) shouldBe emptyIMBTree()
        ofvi(FList.of(3,2,1,1), allowDups = true).inorder() shouldBe listOf(1.toIAEntry(),1.toIAEntry(),2.toIAEntry(),3.toIAEntry())
    }

    test("co.ofvs varargs") {
        ofvs(*arrayOf<Int>()) shouldBe emptyIMBTree()
        ofvs(3, 2, 1, 1).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs varargs dups") {
        ofvs(*arrayOf<Int>()) shouldBe emptyIMBTree()
        ofvs(3, 2, 1, 1, allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs iterator") {
        ofvs(arrayOf<Int>().iterator()) shouldBe emptyIMBTree()
        ofvs(arrayOf(3,2,1,1).iterator()).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs iterator dups") {
        ofvs(arrayOf<Int>().iterator(), allowDups = true) shouldBe emptyIMBTree()
        ofvs(arrayOf(3,2,1,1).iterator(), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(), 1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs IMList") {
        ofvs(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvs(FList.of(3,2,1,1)).inorder() shouldBe listOf(1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofvs IMList dups") {
        ofvs(FList.emptyIMList()) shouldBe emptyIMBTree()
        ofvs(FList.of(3,2,1,1), allowDups = true).inorder() shouldBe listOf(1.toSAEntry(),1.toSAEntry(),2.toSAEntry(),3.toSAEntry())
    }

    test("co.ofMap ABCD Iterator") {
        ofMap(emptyList<TKVEntry<Int, Int>>().iterator()) { tkv -> TKVEntry.of(tkv.getk(), tkv.getv().toString()) } shouldBe emptyIMBTree()
    }

    test("co.ofMap ABCD Iterator dups") {
        ofMap(emptyList<TKVEntry<Int, Int>>().iterator(), allowDups = true) { tkv -> TKVEntry.of(tkv.getk(), tkv.getv().toString()) } shouldBe emptyIMBTree()
        ofMap(listOf(1, 1, 2, 3).map{ TKVEntry.of(it, -it) }.iterator(), allowDups = true) { tkv -> TKVEntry.of(tkv.getk(), tkv.getv().toString()) }.inorder() shouldBe
                listOf(TKVEntry.of(1, "1"),TKVEntry.of(1, "1"),TKVEntry.of(2, "2"),TKVEntry.of(3, "3"))
    }

    test("co.ofviMap Iterator") {
        ofviMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofviMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder() shouldBe
                listOf(TKVEntry.of(intKeyOf("1"), "1"),TKVEntry.of(intKeyOf("2"), "2"),TKVEntry.of(intKeyOf("3"), "3"))
    }

    test("co.ofviMap Iterator dups") {
        ofviMap(emptyList<Int>().iterator(), allowDups = true) { it.toString() } shouldBe emptyIMBTree()
        ofviMap(listOf(1, 1, 2, 3).iterator(), allowDups = true) { it.toString() }.inorder() shouldBe
                listOf(TKVEntry.of(intKeyOf("1"), "1"),TKVEntry.of(intKeyOf("1"), "1"),TKVEntry.of(intKeyOf("2"), "2"),TKVEntry.of(intKeyOf("3"), "3"))
    }

    test("co.ofvsMap Iterator") {
        ofvsMap(emptyList<Int>().iterator()) { it.toString() } shouldBe emptyIMBTree()
        ofvsMap(listOf(1, 1, 2, 3).iterator()) { it.toString() }.inorder() shouldBe
                listOf(TKVEntry.of("1", "1"),TKVEntry.of("2", "2"),TKVEntry.of("3", "3"))
    }

    test("co.ofvsMap Iterator dups") {
        ofvsMap(emptyList<Int>().iterator(), allowDups = true) { it.toString() } shouldBe emptyIMBTree()
        ofvsMap(listOf(1, 1, 2, 3).iterator(), allowDups = true) { it.toString() }.inorder() shouldBe
                listOf(TKVEntry.of("1", "1"),TKVEntry.of("1", "1"),TKVEntry.of("2", "2"),TKVEntry.of("3", "3"))
    }

    test("co.toIMBTree Collection") {
        Arb.list(Arb.int()).checkAll(repeats) { l ->
            val lim = l.map{ it.toIAEntry() }
            lim.toIMBTree() shouldBe FBSTree.ofvi(l.iterator(), allowDups = true)
            lim.toIMBTree() shouldBe FBSTree.ofvi(*(l.toTypedArray()), allowDups = true)
            val sim = l.map{ it.toIAEntry() }.toSet()
            sim.toIMBTree() shouldBe FBSTree.ofvi(l.iterator(), allowDups = false)
            sim.toIMBTree() shouldBe FBSTree.ofvi(*(l.toTypedArray()), allowDups = false)
        }
        Arb.list(Arb.int((0..20))).checkAll(repeats) { l ->
            val lsm = l.map{ it.toSAEntry() }
            lsm.toIMBTree() shouldBe FBSTree.ofvs(l.iterator(), allowDups = true)
            lsm.toIMBTree() shouldBe FBSTree.ofvs(*(l.toTypedArray()), allowDups = true)
            val ssm = l.map{ it.toSAEntry() }.toSet()
            ssm.toIMBTree() shouldBe FBSTree.ofvs(l.iterator(), allowDups = false)
            ssm.toIMBTree() shouldBe FBSTree.ofvs(*(l.toTypedArray()), allowDups = false)
        }
        Arb.frbtree<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
            val lv = frbt.postorderValues().copyToMutableList().map{ it.toIAEntry() }
            frbt.toIMBTree() shouldBe lv.toIMBTree()
        }
        Arb.fbstreeAllowDups<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val lv = fbst.postorderValues().copyToMutableList().map{ it.toIAEntry() }
            fbst.toIMBTree() shouldBe lv.toIMBTree()
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
            m.toIMBTree() shouldBe FBSTree.ofMap(l.map{ TKVEntry.of(it, -it) }.iterator()) { tkv ->
                TKVEntry.of(tkv.getk(), tkv.getv().toString())
            }
        }
    }

    test("co.fcontainsIK") {
        FBSTree.fcontainsIK(intFbstOfThree, 1) shouldBe true
        FBSTree.fcontainsIK(intFbstOfThree, 2) shouldBe true
        FBSTree.fcontainsIK(intFbstOfThree, 3) shouldBe true
        FBSTree.fcontainsIK(intFbstOfThree, 0) shouldBe false
        FBSTree.fcontainsIK(intFbstOfThree, 4) shouldBe false
    }

    test("co.fdeleteIK") {
        FBSTree.fdeleteIK(intFbstOfThree, 3) shouldBe intFbstOfTwo
    }

    test("co.ffindIK") {
        FBSTree.ffindIK(intFbstOfFourA, 1) shouldBe FBSTree.ofvi(1, 1, allowDups = true)
        FBSTree.ffindIK(intFbstOfFourA, 2) shouldBe intFbstOfFourA
        FBSTree.ffindIK(intFbstOfFourA, 3) shouldBe FBSTree.ofvi(3)
        FBSTree.ffindIK(intFbstOfFourA, 0) shouldBe null
        FBSTree.ffindIK(intFbstOfFourA, 4) shouldBe null
    }

    test("co.ffindLastIK") {
        FBSTree.ffindLastIK(intFbstOfFourA, 1) shouldBe FBSTree.ofvi(1)
        FBSTree.ffindLastIK(intFbstOfFourA, 2) shouldBe intFbstOfFourA
        FBSTree.ffindLastIK(intFbstOfFourA, 3) shouldBe FBSTree.ofvi(3)
        FBSTree.ffindLastIK(intFbstOfFourA, 0) shouldBe null
        FBSTree.ffindLastIK(intFbstOfFourA, 4) shouldBe null
    }

    test("co.finsertIK") {
        FBSTree.finsertIK(intFbstOfTwo, 3) shouldBe intFbstOfThree
    }

    test("co.finsertDupIK") {
        FBSTree.finsertDupIK(FBSTree.finsertDupIK(intFbstOfOne, 2, allowDups = true), 2, allowDups = true) shouldBe FBSTree.finsertDupIK(intFbstOfTwo, 2, allowDups = true)
    }


    test("co.fcontainsSK") {
        FBSTree.fcontainsSK(strFbstOfThree, "a") shouldBe true
        FBSTree.fcontainsSK(strFbstOfThree, "b") shouldBe true
        FBSTree.fcontainsSK(strFbstOfThree, "c") shouldBe true
        FBSTree.fcontainsSK(strFbstOfThree, "d") shouldBe false
    }

    test("co.fdeleteSK") {
        FBSTree.fdeleteSK(strFbstOfThree, "c") shouldBe strFbstOfTwo
    }

    test("co.ffindSK") {
        FBSTree.ffindSK(strFbstOfFourA, "a") shouldBe FBSTree.ofvs("a", "a", allowDups = true)
        FBSTree.ffindSK(strFbstOfFourA, "b") shouldBe strFbstOfFourA
        FBSTree.ffindSK(strFbstOfFourA, "c") shouldBe FBSTree.ofvs("c")
        FBSTree.ffindSK(strFbstOfFourA, "d") shouldBe null
    }

    test("co.ffindLastSK") {
        FBSTree.ffindLastSK(strFbstOfFourA, "a") shouldBe FBSTree.ofvs("a")
        FBSTree.ffindLastSK(strFbstOfFourA, "b") shouldBe strFbstOfFourA
        FBSTree.ffindLastSK(strFbstOfFourA, "c") shouldBe FBSTree.ofvs("c")
        FBSTree.ffindLastSK(strFbstOfFourA, "d") shouldBe null
    }

    test("co.finsertSK") {
        FBSTree.finsertSK(strFbstOfTwo, "c") shouldBe strFbstOfThree
    }

    test("co.finsertDupSK") {
        FBSTree.finsertDupSK(FBSTree.finsertDupSK(strFbstOfOne, "b", allowDups = true), "b", allowDups = true) shouldBe FBSTree.finsertDupSK(strFbstOfTwo, "b", allowDups = true)
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

    test("co.toArray") {
        Arb.fbstree<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
            val ary: Array<TKVEntry<Int, Int>> = FBSTree.toArray(fbst)
            fbst shouldBe of(ary.iterator())
            fbst shouldBe of(*ary)
        }
    }

})
