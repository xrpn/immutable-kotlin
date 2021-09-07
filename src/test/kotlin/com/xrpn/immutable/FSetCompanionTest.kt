package com.xrpn.immutable

import com.xrpn.imapi.IMList
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.FSet.Companion.NOT_FOUND
import com.xrpn.immutable.FSet.Companion.emptyIMSet
import com.xrpn.immutable.FSet.Companion.or
import com.xrpn.immutable.FSet.Companion.and
import com.xrpn.immutable.FSet.Companion.xor
import com.xrpn.immutable.FSet.Companion.not
import com.xrpn.immutable.FSet.Companion.toIMSet
import com.xrpn.immutable.FSet.Companion.of
import com.xrpn.immutable.FSet.Companion.ofMap
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.fset

private val intSetOfNone: FSet<Int> = FSet.of(*arrayOf())
private val strSetOfNone: FSet<String> = FSet.of(*arrayOf())
private val intSetOfOne = FSet.of(1)
private val strSetOfOne = FSet.of("a")
private val intSetOfTwo = FSet.of(1, 2)
private val intSetOfTwoOfst1 = FSet.of(2, 3)
private val intSetOfTwoOfst2 = FSet.of(3, 4)
private val intSetOfThree = FSet.of(1, 2, 3)
private val strSetOfThree = FSet.of("1", "2", "3")
private val intSetOfFour = FSet.of(1, 2, 3, 4)

private val kSetOfNone: Set<Int> = setOf(*arrayOf())
private val ksSetOfNone: Set<String> = setOf(*arrayOf())
private val kSetOfOne = setOf(1)
private val ksSetOfOne = setOf("a")
private val kSetOfTwo = setOf(1, 2)
private val kSetOfTwoOfst1 = setOf(2, 3)
private val kSetOfThree = setOf(1, 2, 3)

class FSetCompanionTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("equals Set") {
        intSetOfNone.equals(kSetOfNone) shouldBe true
        intSetOfNone.equals(kSetOfOne) shouldBe false
        intSetOfOne.equals(kSetOfNone) shouldBe false
        intSetOfOne.equals(kSetOfOne) shouldBe true
        intSetOfOne.equals(kSetOfTwo) shouldBe false
        intSetOfTwo.equals(kSetOfOne) shouldBe false
        intSetOfTwo.equals(kSetOfTwo) shouldBe true
        intSetOfTwo.equals(kSetOfTwoOfst1) shouldBe false
        intSetOfTwoOfst1.equals(kSetOfTwo) shouldBe false
        intSetOfTwo.equals(kSetOfThree) shouldBe false
        intSetOfTwoOfst1.equals(kSetOfThree) shouldBe false
        intSetOfThree.equals(kSetOfTwo) shouldBe false
        intSetOfThree.equals(kSetOfTwoOfst1) shouldBe false
        intSetOfThree.equals(kSetOfThree) shouldBe true

        kSetOfNone.equals(intSetOfNone) shouldBe true
        kSetOfNone.equals(intSetOfOne) shouldBe false
        kSetOfOne.equals(intSetOfNone) shouldBe false
        kSetOfOne.equals(intSetOfOne) shouldBe true
        kSetOfOne.equals(intSetOfTwo) shouldBe false
        kSetOfTwo.equals(intSetOfOne) shouldBe false
        kSetOfTwo.equals(intSetOfTwo) shouldBe true
        kSetOfTwo.equals(intSetOfTwoOfst1) shouldBe false
        kSetOfTwoOfst1.equals(intSetOfTwo) shouldBe false
        kSetOfTwo.equals(intSetOfThree) shouldBe false
        kSetOfTwoOfst1.equals(intSetOfThree) shouldBe false
        kSetOfThree.equals(intSetOfTwo) shouldBe false
        kSetOfThree.equals(intSetOfTwoOfst1) shouldBe false
        kSetOfThree.equals(intSetOfThree) shouldBe true

        kSetOfNone.equals(strSetOfNone) shouldBe true
        ksSetOfNone.equals(intSetOfNone) shouldBe true

        // this is why "equal" (vs. equals) is _always_ preferred
        intSetOfNone.equals(strSetOfNone) shouldBe true
        strSetOfNone.equals(intSetOfNone) shouldBe true
    }

    test("equals fail") {
        intSetOfNone.equals(null) shouldBe false

        intSetOfOne.equals(strSetOfOne) shouldBe false
        strSetOfOne.equals(intSetOfOne) shouldBe false
        intSetOfTwo.equals(strSetOfOne) shouldBe false
        strSetOfOne.equals(intSetOfTwo) shouldBe false

        kSetOfOne.equals(strSetOfOne) shouldBe false
        ksSetOfOne.equals(intSetOfOne) shouldBe false
        kSetOfTwo.equals(strSetOfOne) shouldBe false
        ksSetOfOne.equals(intSetOfTwo) shouldBe false

        strSetOfOne.equals(kSetOfOne) shouldBe false
        intSetOfOne.equals(ksSetOfOne) shouldBe false
        strSetOfOne.equals(kSetOfTwo) shouldBe false
        intSetOfTwo.equals(ksSetOfOne) shouldBe false

        intSetOfOne.equals(1) shouldBe false
        strSetOfOne.equals("a") shouldBe false
    }

    test("toString() hashCode()") {
        emptyIMSet<Int>().toString() shouldBe "FSet(EMPTY)"

        val aux = emptyIMSet<Int>().hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMSet<Int>().hashCode()
        }
        intSetOfTwo.toString() shouldStartWith "FSetBody("
        val aux2 = intSetOfTwo.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe intSetOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FSet.hashCode(intSetOfTwo) shouldBe intSetOfTwo.hashCode()
        }
    }

    // IMSetCompanion

    test("co.emptyIMSet"){
        emptyIMSet<Int>() shouldBe FSetBody.empty
        (emptyIMSet<Int>() === FSetBody.empty) shouldBe true
    }
    
    test("co.of vararg"){
        of(*arrayOf()) shouldBe emptyIMSet()
        (of(*arrayOf()) === emptyIMSet<Int>()) shouldBe true
    }

    test("co.of Iterator"){
        of(arrayOf<Int>().iterator()) shouldBe emptyIMSet()
        (of(arrayOf<Int>().iterator()) === emptyIMSet<Int>()) shouldBe true
        of(arrayOf(1, 2, 3).iterator()).equal(intSetOfThree) shouldBe true
    }

    test("co.of IMBTree<K, A>"){
        of(FRBTree.nul<Int, Int>()) shouldBe emptyIMSet()
        (of(FRBTree.nul<Int, Int>()) === emptyIMSet<Int>()) shouldBe true
        of(FBSTree.nul<Int, Int>()) shouldBe emptyIMSet()
        (of(FBSTree.nul<Int, Int>()) === emptyIMSet<Int>()) shouldBe true
        of(FRBTree.ofvi(1, 2, 3)) shouldBe intSetOfThree
        of(FBSTree.ofvi(1, 2, 3)) shouldBe intSetOfThree
    }

    test("co.of IMList"){
        of(emptyIMList()) shouldBe emptyIMSet()
        (of(emptyIMList()) === emptyIMSet<Int>()) shouldBe true
        of(FLCons(2, FLCons(3, FLCons(1, FLNil)))) shouldBe intSetOfThree
    }

    test("co.ofMap Iterator"){
        ofMap(arrayOf<Int>().iterator()){ it.toString() } shouldBe emptyIMSet()
        (ofMap(arrayOf<Int>().iterator()){ it.toString() } === emptyIMSet<Int>()) shouldBe true
        ofMap(arrayOf(1, 2, 3).iterator()){ it.toString() } shouldBe strSetOfThree
    }

    test("co.ofMap IMList"){
        ofMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } shouldBe emptyIMSet()
        (ofMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } === emptyIMSet<Int>()) shouldBe true
        ofMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toString() } shouldBe strSetOfThree
    }

    test("co.ofMap List"){
        ofMap(emptyList<Int>()){ it.toString() } shouldBe emptyIMSet()
        (ofMap(emptyList<Int>()){ it.toString() } === emptyIMSet<Int>()) shouldBe true
        ofMap(listOf(1, 2, 3)){ it.toString() } shouldBe strSetOfThree
    }

    test("co.or"){
        (intSetOfNone or intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfOne or intSetOfNone).equals(intSetOfOne) shouldBe true
        (intSetOfNone or intSetOfOne).equals(intSetOfOne) shouldBe true

        (intSetOfTwo or intSetOfTwo).equals(intSetOfTwo) shouldBe true
        (intSetOfTwo or intSetOfNone).equals(intSetOfTwo) shouldBe true
        (intSetOfNone or intSetOfTwo).equals(intSetOfTwo) shouldBe true
        (intSetOfTwo or intSetOfTwoOfst1).equals(intSetOfThree) shouldBe true
        (intSetOfTwoOfst1 or intSetOfTwo).equals(intSetOfThree) shouldBe true
        (intSetOfTwo or intSetOfTwoOfst2).equals(intSetOfFour) shouldBe true
        (intSetOfTwoOfst2 or intSetOfTwo).equals(intSetOfFour) shouldBe true

        (intSetOfThree or intSetOfNone).equal(intSetOfThree) shouldBe true
        (intSetOfThree or intSetOfThree).equal(intSetOfThree) shouldBe true
        (FSet.of(2) or intSetOfThree).equal(intSetOfThree) shouldBe true
        (intSetOfThree or FSet.of(2)).equal(intSetOfThree) shouldBe true
    }
    
    test("co.and"){
        (intSetOfNone and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfNone and intSetOfOne).equal(intSetOfNone) shouldBe true

        (intSetOfOne and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfOne and intSetOfOne).equal(intSetOfOne) shouldBe true
        (intSetOfOne and intSetOfThree).equal(intSetOfOne) shouldBe true
        (intSetOfThree and intSetOfOne).equal(intSetOfOne) shouldBe true

        (intSetOfTwo and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfTwo and intSetOfTwo).equal(intSetOfTwo) shouldBe true
        (intSetOfTwo and intSetOfThree).equal(intSetOfTwo) shouldBe true
        (intSetOfThree and intSetOfTwo).equal(intSetOfTwo) shouldBe true

        (intSetOfThree and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfThree and intSetOfThree).equal(intSetOfThree) shouldBe true
        (FSet.of(2) and intSetOfThree).equal(FSet.of(2)) shouldBe true
        (intSetOfThree and FSet.of(2)).equal(FSet.of(2)) shouldBe true
    }
    
    test("co.xor"){
        (intSetOfNone xor intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfNone xor intSetOfOne).equal(intSetOfOne) shouldBe true

        (intSetOfOne xor intSetOfNone).equal(intSetOfOne) shouldBe true
        (intSetOfOne xor intSetOfOne).equal(intSetOfNone) shouldBe true
        (intSetOfOne xor intSetOfThree).equal(FSet.of(2,3)) shouldBe true
        (intSetOfThree xor intSetOfOne).equal(FSet.of(2,3)) shouldBe true

        (intSetOfTwo xor intSetOfNone).equal(intSetOfTwo) shouldBe true
        (intSetOfTwo xor intSetOfTwo).equal(intSetOfNone) shouldBe true
        (intSetOfTwo xor intSetOfThree).equal(FSet.of(3)) shouldBe true
        (intSetOfThree xor intSetOfTwo).equal(FSet.of(3)) shouldBe true

        (intSetOfThree xor intSetOfNone).equal(intSetOfThree) shouldBe true
        (intSetOfThree xor intSetOfThree).equal(intSetOfNone) shouldBe true
        (FSet.of(2) xor intSetOfThree).equal(FSet.of(1,3)) shouldBe true
        (intSetOfThree xor FSet.of(2)).equal(FSet.of(1,3)) shouldBe true
    }

    test("co.not"){
        (intSetOfNone not intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfNone not intSetOfOne).equal(intSetOfNone) shouldBe true

        (intSetOfOne not intSetOfNone).equal(intSetOfOne) shouldBe true
        (intSetOfOne not intSetOfOne).equal(intSetOfNone) shouldBe true
        (intSetOfOne not intSetOfThree).equal(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfOne).equal(FSet.of(2,3)) shouldBe true

        (intSetOfTwo not intSetOfNone).equal(intSetOfTwo) shouldBe true
        (intSetOfTwo not intSetOfTwo).equal(intSetOfNone) shouldBe true
        (intSetOfTwo not intSetOfThree).equal(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfTwo).equal(FSet.of(3)) shouldBe true

        (intSetOfThree not intSetOfNone).equal(intSetOfThree) shouldBe true
        (intSetOfThree not intSetOfThree).equal(intSetOfNone) shouldBe true
        (FSet.of(2) not intSetOfThree).equal(intSetOfNone) shouldBe true
        (intSetOfThree not FSet.of(2)).equal(FSet.of(1,3)) shouldBe true
    }

    test("co.toIMSet()"){
        Arb.list(Arb.int()).checkAll(repeats) { kl ->
            val ks = kl.toSet()
            val fs1 = kl.toIMSet()
            val fs2 = ks.toIMSet()
            fs1.equal(fs2) shouldBe true
            fs1.equals(ks) shouldBe true
            ks.equals(fs2) shouldBe true
        }
    }

    // implementation

    test("co.NOT_FOUND"){
        NOT_FOUND shouldBe -1
    }

    test("co.toArray"){
        Arb.fset<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
            val ary: Array<Int> = FSet.toArray(fs)
            fs.equal(FSet.of(ary.iterator())) shouldBe true
            fs.equal(FSet.of(*ary)) shouldBe true
        }
    }

})