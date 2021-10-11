package com.xrpn.immutable.fksettest

import com.xrpn.imapi.IMSet
import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.immutable.*
import com.xrpn.immutable.FKSet.Companion.toIMKSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fset
import io.kotest.xrpn.fsset

private val intKKSetOfNone = FKSet.ofi(*emptyArrayOfInt)
private val intKKSetOfOne = FKSet.ofi(1).rrne()!!
private val intKKSetOfOne3 = FKSet.ofi(3).rrne()!!
private val intKKSetOfTwo = FKSet.ofi(1, 2).rrne()!!
private val intKKSetOfTwoOfst1 = FKSet.ofi(2, 3).rrne()!!
private val intKKSetOfTwoOfst2 = FKSet.ofi(3, 4).rrne()!!
private val intKKSetOfThree = FKSet.ofi(1, 2, 3).rrne()!!
private val intKKSetOfFour = FKSet.ofi(1, 2, 3, 4).rrne()!!
private val intKKSetMaverick = FKSet.ofi(113, 97).rrne()!!

private val intSSetOfNone = FKSet.ofs(*emptyArrayOfInt)
private val intSSetOfOne = FKSet.ofs(1).rne()!!
private val intSSetOfOne3 = FKSet.ofs(3).rne()!!
private val intSSetOfTwo = FKSet.ofs(1, 2).rne()!!
private val intSSetOfTwoOfst1 = FKSet.ofs(2, 3).rne()!!
private val intSSetOfTwoOfst2 = FKSet.ofs(3, 4).rne()!!
private val intSSetOfThree = FKSet.ofs(1, 2, 3).rne()!!
private val intSSetOfFour = FKSet.ofs(1, 2, 3, 4).rne()!!
private val intSSetMaverick = FKSet.ofs(113, 97).rne()!!

class FKSetFilteringIntVTest : FunSpec({

    val repeats = 50
    
    beforeTest {}

    test("fcontains") {
        intKKSetOfNone.fcontains(1) shouldBe false
        intKKSetOfOne.fcontains(0) shouldBe false
        intKKSetOfOne.fcontains(1) shouldBe true
        intKKSetOfOne.fcontains(2) shouldBe false
        intKKSetOfTwo.fcontains(0) shouldBe false
        intKKSetOfTwo.fcontains(1) shouldBe true
        intKKSetOfTwo.fcontains(2) shouldBe true
        intKKSetOfTwo.fcontains(3) shouldBe false
        intKKSetOfThree.fcontains(0) shouldBe false
        intKKSetOfThree.fcontains(1) shouldBe true
        intKKSetOfThree.fcontains(2) shouldBe true
        intKKSetOfThree.fcontains(3) shouldBe true
        intKKSetOfThree.fcontains(4) shouldBe false
        
        intSSetOfNone.fcontains(1) shouldBe false
        intSSetOfOne.fcontains(0) shouldBe false
        intSSetOfOne.fcontains(1) shouldBe true
        intSSetOfOne.fcontains(2) shouldBe false
        intSSetOfTwo.fcontains(0) shouldBe false
        intSSetOfTwo.fcontains(1) shouldBe true
        intSSetOfTwo.fcontains(2) shouldBe true
        intSSetOfTwo.fcontains(3) shouldBe false
        intSSetOfThree.fcontains(0) shouldBe false
        intSSetOfThree.fcontains(1) shouldBe true
        intSSetOfThree.fcontains(2) shouldBe true
        intSSetOfThree.fcontains(3) shouldBe true
        intSSetOfThree.fcontains(4) shouldBe false

    }

    test("fcontainsAny") {
        intKKSetOfNone.fcontainsAny(intKKSetOfNone) shouldBe false

        intKKSetOfOne.fcontainsAny(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fcontainsAny(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fcontainsAny(intKKSetOfTwo) shouldBe true
        intKKSetOfOne.fcontainsAny(intKKSetMaverick) shouldBe false

        intKKSetOfTwo.fcontainsAny(intKKSetOfNone) shouldBe true
        intKKSetOfTwo.fcontainsAny(intKKSetOfOne) shouldBe true
        intKKSetOfTwo.fcontainsAny(intKKSetOfFour) shouldBe true
        intKKSetOfTwo.fcontainsAny(intKKSetMaverick) shouldBe false

        intKKSetOfThree.fcontainsAny(intKKSetOfNone) shouldBe true
        intKKSetOfThree.fcontainsAny(intKKSetOfFour) shouldBe true
        intKKSetOfThree.fcontainsAny(intKKSetMaverick) shouldBe false

        intSSetOfNone.fcontainsAny(intSSetOfNone) shouldBe false

        intSSetOfOne.fcontainsAny(intSSetOfNone) shouldBe true
        intSSetOfOne.fcontainsAny(intSSetOfOne) shouldBe true
        intSSetOfOne.fcontainsAny(intSSetOfTwo) shouldBe true
        intSSetOfOne.fcontainsAny(intSSetMaverick) shouldBe false

        intSSetOfTwo.fcontainsAny(intSSetOfNone) shouldBe true
        intSSetOfTwo.fcontainsAny(intSSetOfOne) shouldBe true
        intSSetOfTwo.fcontainsAny(intSSetOfFour) shouldBe true
        intSSetOfTwo.fcontainsAny(intSSetMaverick) shouldBe false

        intSSetOfThree.fcontainsAny(intSSetOfNone) shouldBe true
        intSSetOfThree.fcontainsAny(intSSetOfFour) shouldBe true
        intSSetOfThree.fcontainsAny(intSSetMaverick) shouldBe false

        // mixed mode

        intKKSetOfThree.fcontainsAny(intSSetOfNone) shouldBe true
        intKKSetOfThree.fcontainsAny(intSSetOfFour) shouldBe true
        intKKSetOfThree.fcontainsAny(intSSetMaverick) shouldBe false

        intSSetOfThree.fcontainsAny(intKKSetOfNone) shouldBe true
        intSSetOfThree.fcontainsAny(intKKSetOfFour) shouldBe true
        intSSetOfThree.fcontainsAny(intKKSetMaverick) shouldBe false

    }

    test("fdropItem") {
        intKKSetOfNone.fdropItem(0).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fdropItem(0).equals(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fdropItem(1).equals(intKKSetOfNone) shouldBe true

        intKKSetOfTwo.fdropItem(0).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fdropItem(1).equals(FKSet.ofi(2)) shouldBe true
        intKKSetOfTwo.fdropItem(2).equals(intKKSetOfOne) shouldBe true
        intKKSetOfTwo.fdropItem(3).equals(intKKSetOfTwo) shouldBe true

        intKKSetOfThree.fdropItem(0).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fdropItem(1).equals(FKSet.ofi(2, 3)) shouldBe true
        intKKSetOfThree.fdropItem(2).equals(FKSet.ofi(1, 3)) shouldBe true
        intKKSetOfThree.fdropItem(3).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfThree.fdropItem(4).equals(intKKSetOfThree) shouldBe true

        intSSetOfNone.fdropItem(0).equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fdropItem(0).equals(intSSetOfOne) shouldBe true
        intSSetOfOne.fdropItem(1).equals(intSSetOfNone) shouldBe true

        intSSetOfTwo.fdropItem(0).equals(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fdropItem(1).equals(FKSet.ofs(2)) shouldBe true
        intSSetOfTwo.fdropItem(2).equals(intSSetOfOne) shouldBe true
        intSSetOfTwo.fdropItem(3).equals(intSSetOfTwo) shouldBe true

        intSSetOfThree.fdropItem(0).equals(intSSetOfThree) shouldBe true
        intSSetOfThree.fdropItem(1).equals(FKSet.ofs(2, 3)) shouldBe true
        intSSetOfThree.fdropItem(2).equals(FKSet.ofs(1, 3)) shouldBe true
        intSSetOfThree.fdropItem(3).equals(intSSetOfTwo) shouldBe true
        intSSetOfThree.fdropItem(4).equals(intSSetOfThree) shouldBe true

        // mixed mode

        intSSetOfThree.fdropItem(3).equal(intKKSetOfTwo) shouldBe true
        intKKSetOfThree.fdropItem(3).equal(intSSetOfTwo) shouldBe true
    }

    test("fdropAll") {
        intKKSetOfNone.fdropAll(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true

        intKKSetOfOne.fdropAll(intKKSetOfNone).equals(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fdropAll(intKKSetOfOne).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fdropAll(intKKSetOfTwo).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fdropAll(intKKSetMaverick).equals(intKKSetOfOne) shouldBe true

        intKKSetOfTwo.fdropAll(intKKSetOfNone).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fdropAll(intKKSetOfOne).equals(FKSet.ofi(2)) shouldBe true
        intKKSetOfTwo.fdropAll(intKKSetOfFour).equals(intKKSetOfNone) shouldBe true
        intKKSetOfTwo.fdropAll(intKKSetMaverick).equals(intKKSetOfTwo) shouldBe true

        intKKSetOfThree.fdropAll(intKKSetOfNone).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fdropAll(intKKSetOfFour).equals(intKKSetOfNone) shouldBe true
        intKKSetOfThree.fdropAll(intKKSetMaverick).equals(intKKSetOfThree) shouldBe true

        intSSetOfNone.fdropAll(intSSetOfNone).equals(intSSetOfNone) shouldBe true

        intSSetOfOne.fdropAll(intSSetOfNone).equals(intSSetOfOne) shouldBe true
        intSSetOfOne.fdropAll(intSSetOfOne).equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fdropAll(intSSetOfTwo).equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fdropAll(intSSetMaverick).equals(intSSetOfOne) shouldBe true

        intSSetOfTwo.fdropAll(intSSetOfNone).equals(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fdropAll(intSSetOfOne).equals(FKSet.ofs(2)) shouldBe true
        intSSetOfTwo.fdropAll(intSSetOfFour).equals(intSSetOfNone) shouldBe true
        intSSetOfTwo.fdropAll(intSSetMaverick).equals(intSSetOfTwo) shouldBe true

        intSSetOfThree.fdropAll(intSSetOfNone).equals(intSSetOfThree) shouldBe true
        intSSetOfThree.fdropAll(intSSetOfFour).equals(intSSetOfNone) shouldBe true
        intSSetOfThree.fdropAll(intSSetMaverick).equals(intSSetOfThree) shouldBe true

        // mixed mode

        intSSetOfThree.fdropAll(intKKSetOfFour).equal(intSSetOfNone) shouldBe true
        intKKSetOfThree.fdropAll(intSSetOfFour).equal(intSSetOfNone) shouldBe true

    }

    test("fdropAll coverage") {
        val llt = FRBTree.of(TKVEntry.ofkv(1L, 1L), TKVEntry.ofkv(3L, 3L), TKVEntry.ofkv(2L, 2L))
        val auxll = ofFKKSNotEmpty(llt as FRBTKNode)
        val ilt = FRBTree.of(TKVEntry.ofkv(1, 1L), TKVEntry.ofkv(2, 2L), TKVEntry.ofkv(4, 4L))
        val auxil = ofFIKSNotEmpty(ilt as FRBTINode)
        val aut1 = auxll.fdropAll(auxil)
        aut1.fsize() shouldBe 1
        aut1.equals(ofFKKSNotEmpty(FRBTree.of(TKVEntry.ofkv(3L, 3L)) as FRBTKNode)) shouldBe true
        val aut2 = auxil.fdropAll(auxll)
        aut2.fsize() shouldBe 1
        aut2.equals(ofFIKSNotEmpty(FRBTree.of(TKVEntry.ofkv(4, 4L)) as FRBTINode)) shouldBe true
    }

    test("fdropWhen") {
        intKKSetOfNone.fdropWhen { false }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.fdropWhen { true }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fdropWhen { it == 1 }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fdropWhen { it != 1 }.equals(intKKSetOfOne) shouldBe true
        intKKSetOfThree.fdropWhen { it < 2 }.equals(intKKSetOfTwo.fmap { it+1 }) shouldBe true
        intKKSetOfThree.fdropWhen { it >= 2 }.equals(intKKSetOfOne) shouldBe true

        intSSetOfNone.fdropWhen { false }.equals(intSSetOfNone) shouldBe true
        intSSetOfNone.fdropWhen { true }.equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fdropWhen { it == 1 }.equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fdropWhen { it != 1 }.equals(intSSetOfOne) shouldBe true
        intSSetOfThree.fdropWhen { it < 2 }.equals(intSSetOfTwo.fmap { it+1 }) shouldBe true
        intSSetOfThree.fdropWhen { it >= 2 }.equals(intSSetOfOne) shouldBe true
    }

    test("fempty") {
        intKKSetOfNone.fempty() shouldBe true
        intKKSetOfOne.fempty() shouldBe false
        intSSetOfNone.fempty() shouldBe true
        intSSetOfOne.fempty() shouldBe false
        (intKKSetOfNone === intSSetOfNone) shouldBe true
    }

    test("ffilter") {
        intKKSetOfNone.ffilter { false }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.ffilter { true }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.ffilter { it == 1 }.equals(intKKSetOfOne) shouldBe true
        intKKSetOfOne.ffilter { it != 1 }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfThree.ffilter { it < 2 }.equals(intKKSetOfOne) shouldBe true
        intKKSetOfThree.ffilter { it >= 2 }.equals(intKKSetOfTwo.fmap { it+1 }) shouldBe true

        intSSetOfNone.ffilter { false }.equals(intSSetOfNone) shouldBe true
        intSSetOfNone.ffilter { true }.equals(intSSetOfNone) shouldBe true
        intSSetOfOne.ffilter { it == 1 }.equals(intSSetOfOne) shouldBe true
        intSSetOfOne.ffilter { it != 1 }.equals(intSSetOfNone) shouldBe true
        intSSetOfThree.ffilter { it < 2 }.equals(intSSetOfOne) shouldBe true
        intSSetOfThree.ffilter { it >= 2 }.equals(intSSetOfTwo.fmap { it+1 }) shouldBe true
    }
    
    test("ffilterNot") {
        intKKSetOfNone.ffilterNot { false }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.ffilterNot { true }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.ffilterNot { it == 1 }.equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.ffilterNot { it != 1 }.equals(intKKSetOfOne) shouldBe true
        intKKSetOfThree.ffilterNot { it < 2 }.equals(intKKSetOfTwo.fmap { it+1 }) shouldBe true
        intKKSetOfThree.ffilterNot { it >= 2 }.equals(intKKSetOfOne) shouldBe true

        intSSetOfNone.ffilterNot { false }.equals(intSSetOfNone) shouldBe true
        intSSetOfNone.ffilterNot { true }.equals(intSSetOfNone) shouldBe true
        intSSetOfOne.ffilterNot { it == 1 }.equals(intSSetOfNone) shouldBe true
        intSSetOfOne.ffilterNot { it != 1 }.equals(intSSetOfOne) shouldBe true
        intSSetOfThree.ffilterNot { it < 2 }.equals(intSSetOfTwo.fmap { it+1 }) shouldBe true
        intSSetOfThree.ffilterNot { it >= 2 }.equals(intSSetOfOne) shouldBe true
    }

    test("ffind") {
        intKKSetOfNone.ffind { false } shouldBe null
        intKKSetOfNone.ffind { true } shouldBe null
        intKKSetOfOne.ffind { it == 1 } shouldBe 1
        intKKSetOfOne.ffind { it != 1 } shouldBe null
        intKKSetOfThree.ffind { it < 2 } shouldBe 1
        intKKSetOfThree.ffind { it >= 2 } shouldBe null

        intSSetOfNone.ffind { false } shouldBe null
        intSSetOfNone.ffind { true } shouldBe null
        intSSetOfOne.ffind { it == 1 } shouldBe 1
        intSSetOfOne.ffind { it != 1 } shouldBe null
        intSSetOfThree.ffind { it < 2 } shouldBe 1
        intSSetOfThree.ffind { it >= 2 } shouldBe null
    }

    test("fisSubsetOf") {
        intKKSetOfNone.fisSubsetOf(intKKSetOfNone) shouldBe true

        intKKSetOfOne.fisSubsetOf(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fisSubsetOf(intKKSetOfNone) shouldBe false
        intKKSetOfNone.fisSubsetOf(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fisSubsetOf(intKKSetMaverick) shouldBe false
        intKKSetMaverick.fisSubsetOf(intKKSetOfOne) shouldBe false
        intKKSetOfOne.fisSubsetOf(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fisSubsetOf(intKKSetOfOne) shouldBe false

        intKKSetOfThree.fisSubsetOf(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fisSubsetOf(intKKSetOfNone) shouldBe false
        intKKSetOfNone.fisSubsetOf(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fisSubsetOf(intKKSetMaverick) shouldBe false
        intKKSetMaverick.fisSubsetOf(intKKSetOfThree) shouldBe false
        intKKSetOfThree.fisSubsetOf(intKKSetOfTwo) shouldBe false
        intKKSetOfTwo.fisSubsetOf(intKKSetOfThree) shouldBe true

        intSSetOfNone.fisSubsetOf(intSSetOfNone) shouldBe true

        intSSetOfOne.fisSubsetOf(intSSetOfOne) shouldBe true
        intSSetOfOne.fisSubsetOf(intSSetOfNone) shouldBe false
        intSSetOfNone.fisSubsetOf(intSSetOfOne) shouldBe true
        intSSetOfOne.fisSubsetOf(intSSetMaverick) shouldBe false
        intSSetMaverick.fisSubsetOf(intSSetOfOne) shouldBe false
        intSSetOfOne.fisSubsetOf(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fisSubsetOf(intSSetOfOne) shouldBe false

        intSSetOfThree.fisSubsetOf(intSSetOfThree) shouldBe true
        intSSetOfThree.fisSubsetOf(intSSetOfNone) shouldBe false
        intSSetOfNone.fisSubsetOf(intSSetOfThree) shouldBe true
        intSSetOfThree.fisSubsetOf(intSSetMaverick) shouldBe false
        intSSetMaverick.fisSubsetOf(intSSetOfThree) shouldBe false
        intSSetOfThree.fisSubsetOf(intSSetOfTwo) shouldBe false
        intSSetOfTwo.fisSubsetOf(intSSetOfThree) shouldBe true

        // mixed mode

        intSSetOfThree.fisSubsetOf(intKKSetOfThree) shouldBe true
        intSSetOfThree.fisSubsetOf(intKKSetOfNone) shouldBe false
        intSSetOfNone.fisSubsetOf(intKKSetOfThree) shouldBe true
        intSSetOfThree.fisSubsetOf(intKKSetMaverick) shouldBe false
        intSSetMaverick.fisSubsetOf(intKKSetOfThree) shouldBe false
        intSSetOfThree.fisSubsetOf(intKKSetOfTwo) shouldBe false
        intSSetOfTwo.fisSubsetOf(intKKSetOfThree) shouldBe true

        intKKSetOfThree.fisSubsetOf(intSSetOfThree) shouldBe true
        intKKSetOfThree.fisSubsetOf(intSSetOfNone) shouldBe false
        intKKSetOfNone.fisSubsetOf(intSSetOfThree) shouldBe true
        intKKSetOfThree.fisSubsetOf(intSSetMaverick) shouldBe false
        intKKSetMaverick.fisSubsetOf(intSSetOfThree) shouldBe false
        intKKSetOfThree.fisSubsetOf(intSSetOfTwo) shouldBe false
        intKKSetOfTwo.fisSubsetOf(intSSetOfThree) shouldBe true

    }

    test("fpick") {
        intKKSetOfNone.fpick() shouldBe null
        checkAll(repeats, Arb.fset(Arb.int(),20..100)) { fii: FKSet<Int, Int> ->
            val fsi: IMSet<Int> = fii.toIMKSet(StrKeyType)!!
            var count = 0
            for (item in fii) {
                fsi.fcontains(item) shouldBe true
                count += 1
            }
            count shouldBe fii.fsize()
            fsi.fcontains(fii.fpick()!!) shouldBe true
        }
        intSSetOfNone.fpick() shouldBe null
        checkAll(repeats, Arb.fsset(Arb.int(),20..100)) { fsi: FKSet<String, Int> ->
            val fii: IMSet<Int> = fsi.toIMKSet(IntKeyType)!!
            var count = 0
            for (item in fsi) {
                fii.fcontains(item) shouldBe true
                count += 1
            }
            count shouldBe fsi.fsize()
            fii.fcontains(fsi.fpick()!!) shouldBe true
        }
    }

    test("fAND") { 
        intKKSetOfNone.fAND(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.fAND(intKKSetOfOne).equals(intKKSetOfNone) shouldBe true

        intKKSetOfOne.fAND(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fAND(intKKSetOfOne).equals(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fAND(intKKSetOfThree).equals(intKKSetOfOne) shouldBe true
        intKKSetOfThree.fAND(intKKSetOfOne).equals(intKKSetOfOne) shouldBe true

        intKKSetOfTwo.fAND(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfTwo.fAND(intKKSetOfTwo).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fAND(intKKSetOfThree).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfThree.fAND(intKKSetOfTwo).equals(intKKSetOfTwo) shouldBe true

        intKKSetOfThree.fAND(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfThree.fAND(intKKSetOfThree).equals(intKKSetOfThree) shouldBe true
        FKSet.ofi(2).fAND(intKKSetOfThree).equals(FKSet.ofi(2)) shouldBe true
        intKKSetOfThree.fAND(FKSet.ofi(2)).equals(FKSet.ofi(2)) shouldBe true

        intSSetOfNone.fAND(intSSetOfNone).equals(intSSetOfNone) shouldBe true
        intSSetOfNone.fAND(intSSetOfOne).equals(intSSetOfNone) shouldBe true

        intSSetOfOne.fAND(intSSetOfNone).equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fAND(intSSetOfOne).equals(intSSetOfOne) shouldBe true
        intSSetOfOne.fAND(intSSetOfThree).equals(intSSetOfOne) shouldBe true
        intSSetOfThree.fAND(intSSetOfOne).equals(intSSetOfOne) shouldBe true

        intSSetOfTwo.fAND(intSSetOfNone).equals(intSSetOfNone) shouldBe true
        intSSetOfTwo.fAND(intSSetOfTwo).equals(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fAND(intSSetOfThree).equals(intSSetOfTwo) shouldBe true
        intSSetOfThree.fAND(intSSetOfTwo).equals(intSSetOfTwo) shouldBe true

        intSSetOfThree.fAND(intSSetOfNone).equals(intSSetOfNone) shouldBe true
        intSSetOfThree.fAND(intSSetOfThree).equals(intSSetOfThree) shouldBe true
        FKSet.ofi(2).fAND(intSSetOfThree).equals(FKSet.ofi(2)) shouldBe true
        intSSetOfThree.fAND(FKSet.ofi(2)).equals(FKSet.ofs(2)) shouldBe true

        // mixed mode

        intSSetOfThree.fAND(intKKSetOfTwo).equals(intSSetOfTwo) shouldBe true
        intKKSetOfThree.fAND(intSSetOfTwo).equals(intKKSetOfTwo) shouldBe true

    }

    test("fNOT") {
        intKKSetOfNone.fNOT(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.fNOT(intKKSetOfOne).equals(intKKSetOfNone) shouldBe true

        intKKSetOfOne.fNOT(intKKSetOfNone).equals(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fNOT(intKKSetOfOne).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fNOT(intKKSetOfThree).equals(intKKSetOfNone) shouldBe true
        intKKSetOfThree.fNOT(intKKSetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        intKKSetOfTwo.fNOT(intKKSetOfNone).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fNOT(intKKSetOfTwo).equals(intKKSetOfNone) shouldBe true
        intKKSetOfTwo.fNOT(intKKSetOfThree).equals(intKKSetOfNone) shouldBe true
        intKKSetOfThree.fNOT(intKKSetOfTwo).equals(intKKSetOfOne3) shouldBe true

        intKKSetOfThree.fNOT(intKKSetOfNone).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fNOT(intKKSetOfThree).equals(intKKSetOfNone) shouldBe true
        FKSet.ofi(2).fNOT(intKKSetOfThree).equals(intKKSetOfNone) shouldBe true
        intKKSetOfThree.fNOT(FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true

        intSSetOfNone.fNOT(intSSetOfNone).equals(intSSetOfNone) shouldBe true
        intSSetOfNone.fNOT(intSSetOfOne).equals(intSSetOfNone) shouldBe true

        intSSetOfOne.fNOT(intSSetOfNone).equals(intSSetOfOne) shouldBe true
        intSSetOfOne.fNOT(intSSetOfOne).equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fNOT(intSSetOfThree).equals(intSSetOfNone) shouldBe true
        intSSetOfThree.fNOT(intSSetOfOne).equals(FKSet.ofs(2,3)) shouldBe true

        intSSetOfTwo.fNOT(intSSetOfNone).equals(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fNOT(intSSetOfTwo).equals(intSSetOfNone) shouldBe true
        intSSetOfTwo.fNOT(intSSetOfThree).equals(intSSetOfNone) shouldBe true
        intSSetOfThree.fNOT(intSSetOfTwo).equals(intSSetOfOne3) shouldBe true

        intSSetOfThree.fNOT(intSSetOfNone).equals(intSSetOfThree) shouldBe true
        intSSetOfThree.fNOT(intSSetOfThree).equals(intSSetOfNone) shouldBe true
        FKSet.ofs(2).fNOT(intSSetOfThree).equals(intSSetOfNone) shouldBe true
        intSSetOfThree.fNOT(FKSet.ofi(2)).equals(FKSet.ofs(1,3)) shouldBe true

        // mixed mode

        intSSetOfThree.fNOT(intKKSetOfTwo).equals(intSSetOfOne3) shouldBe true
        intKKSetOfThree.fNOT(intSSetOfTwo).equals(intKKSetOfOne3) shouldBe true
    }

    test("fOR") {
        intKKSetOfNone.fOR(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fOR(intKKSetOfNone).equals(intKKSetOfOne) shouldBe true
        intKKSetOfNone.fOR(intKKSetOfOne).equals(intKKSetOfOne) shouldBe true

        intKKSetOfTwo.fOR(intKKSetOfTwo).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fOR(intKKSetOfNone).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfNone.fOR(intKKSetOfTwo).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fOR(intKKSetOfTwoOfst1).equals(intKKSetOfThree) shouldBe true
        intKKSetOfTwoOfst1.fOR(intKKSetOfTwo).equals(intKKSetOfThree) shouldBe true
        intKKSetOfTwo.fOR(intKKSetOfTwoOfst2).equals(intKKSetOfFour) shouldBe true
        intKKSetOfTwoOfst2.fOR(intKKSetOfTwo).equals(intKKSetOfFour) shouldBe true

        intKKSetOfThree.fOR(intKKSetOfNone).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fOR(intKKSetOfThree).equals(intKKSetOfThree) shouldBe true
        FKSet.ofi(2).fOR(intKKSetOfThree).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fOR(FKSet.ofi(2)).equals(intKKSetOfThree) shouldBe true

        intSSetOfNone.fOR(intSSetOfNone).equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fOR(intSSetOfNone).equals(intSSetOfOne) shouldBe true
        intSSetOfNone.fOR(intSSetOfOne).equals(intSSetOfOne) shouldBe true

        intSSetOfTwo.fOR(intSSetOfTwo).equals(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fOR(intSSetOfNone).equals(intSSetOfTwo) shouldBe true
        intSSetOfNone.fOR(intSSetOfTwo).equals(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fOR(intSSetOfTwoOfst1).equals(intSSetOfThree) shouldBe true
        intSSetOfTwoOfst1.fOR(intSSetOfTwo).equals(intSSetOfThree) shouldBe true
        intSSetOfTwo.fOR(intSSetOfTwoOfst2).equals(intSSetOfFour) shouldBe true
        intSSetOfTwoOfst2.fOR(intSSetOfTwo).equals(intSSetOfFour) shouldBe true

        intSSetOfThree.fOR(intSSetOfNone).equals(intSSetOfThree) shouldBe true
        intSSetOfThree.fOR(intSSetOfThree).equals(intSSetOfThree) shouldBe true
        FKSet.ofs(2).fOR(intSSetOfThree).equals(intSSetOfThree) shouldBe true
        intSSetOfThree.fOR(FKSet.ofs(2)).equals(intSSetOfThree) shouldBe true

        // mixed mode

        intSSetOfTwoOfst1.fOR(intKKSetOfTwo).equals(intSSetOfThree) shouldBe true
        intSSetOfTwo.fOR(intKKSetOfTwoOfst2).equals(intSSetOfFour) shouldBe true

        intKKSetOfTwoOfst1.fOR(intSSetOfTwo).equals(intKKSetOfThree) shouldBe true
        intKKSetOfTwo.fOR(intSSetOfTwoOfst2).equals(intKKSetOfFour) shouldBe true
    }

    test("fXOR") {
        intKKSetOfNone.fXOR(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.fXOR(intKKSetOfOne).equals(intKKSetOfOne) shouldBe true

        intKKSetOfOne.fXOR(intKKSetOfNone).equals(intKKSetOfOne) shouldBe true
        intKKSetOfOne.fXOR(intKKSetOfOne).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fXOR(intKKSetOfThree).equals(FKSet.ofi(2,3)) shouldBe true
        intKKSetOfThree.fXOR(intKKSetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        intKKSetOfTwo.fXOR(intKKSetOfNone).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.fXOR(intKKSetOfTwo).equals(intKKSetOfNone) shouldBe true
        intKKSetOfTwo.fXOR(intKKSetOfThree).equals(intKKSetOfOne3) shouldBe true
        intKKSetOfThree.fXOR(intKKSetOfTwo).equals(intKKSetOfOne3) shouldBe true

        intKKSetOfThree.fXOR(intKKSetOfNone).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.fXOR(intKKSetOfThree).equals(intKKSetOfNone) shouldBe true
        FKSet.ofi(2).fXOR(intKKSetOfThree).equals(FKSet.ofi(1,3)) shouldBe true
        intKKSetOfThree.fXOR(FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true

        intSSetOfNone.fXOR(intSSetOfNone).equals(intSSetOfNone) shouldBe true
        intSSetOfNone.fXOR(intSSetOfOne).equals(intSSetOfOne) shouldBe true

        intSSetOfOne.fXOR(intSSetOfNone).equals(intSSetOfOne) shouldBe true
        intSSetOfOne.fXOR(intSSetOfOne).equals(intSSetOfNone) shouldBe true
        intSSetOfOne.fXOR(intSSetOfThree).equals(FKSet.ofs(2,3)) shouldBe true
        intSSetOfThree.fXOR(intSSetOfOne).equals(FKSet.ofs(2,3)) shouldBe true

        intSSetOfTwo.fXOR(intSSetOfNone).equals(intSSetOfTwo) shouldBe true
        intSSetOfTwo.fXOR(intSSetOfTwo).equals(intSSetOfNone) shouldBe true
        intSSetOfTwo.fXOR(intSSetOfThree).equals(intSSetOfOne3) shouldBe true
        intSSetOfThree.fXOR(intSSetOfTwo).equals(intSSetOfOne3) shouldBe true

        intSSetOfThree.fXOR(intSSetOfNone).equals(intSSetOfThree) shouldBe true
        intSSetOfThree.fXOR(intSSetOfThree).equals(intSSetOfNone) shouldBe true
        FKSet.ofs(2).fXOR(intSSetOfThree).equals(FKSet.ofs(1,3)) shouldBe true
        intSSetOfThree.fXOR(FKSet.ofs(2)).equals(FKSet.ofs(1,3)) shouldBe true

        // mixed mode

        intSSetOfTwo.fXOR(intKKSetOfThree).equals(intSSetOfOne3) shouldBe true
        intKKSetOfTwo.fXOR(intSSetOfThree).equals(intKKSetOfOne3) shouldBe true
    }
})
