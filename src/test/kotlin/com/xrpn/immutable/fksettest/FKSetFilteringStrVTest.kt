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

private val strKKSetOfNone = FKSet.ofs(*emptyArrayOfStr)
private val strKKSetOfOne = FKSet.ofs("1").rrne()!!
private val strKKSetOfOne3 = FKSet.ofs("3").rrne()!!
private val strKKSetOfTwo = FKSet.ofs("1", "2").rrne()!!
private val strKKSetOfTwoOfst1 = FKSet.ofs("2", "3").rrne()!!
private val strKKSetOfTwoOfst2 = FKSet.ofs("3", "4").rrne()!!
private val strKKSetOfThree = FKSet.ofs("1", "2", "3").rrne()!!
private val strKKSetOfFour = FKSet.ofs("1", "2", "3", "4").rrne()!!
private val strKKSetMaverick = FKSet.ofs("113", "97").rrne()!!

private val strISetOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISetOfOne = FKSet.ofi("1").rne()!!
private val strISetOfOne3 = FKSet.ofi("3").rne()!!
private val strISetOfTwo = FKSet.ofi("1", "2").rne()!!
private val strISetOfTwoOfst1 = FKSet.ofi("2", "3").rne()!!
private val strISetOfTwoOfst2 = FKSet.ofi("3", "4").rne()!!
private val strISetOfThree = FKSet.ofi("1", "2", "3").rne()!!
private val strISetOfFour = FKSet.ofi("1", "2", "3", "4").rne()!!
private val strISetMaverick = FKSet.ofi("113", "97").rne()!!

class FKSetFilteringStrVTest : FunSpec({

    val repeats = 50
    
    beforeTest {}

    test("fcontains") {
        strKKSetOfNone.fcontains("1") shouldBe false
        strKKSetOfOne.fcontains("0") shouldBe false
        strKKSetOfOne.fcontains("1") shouldBe true
        strKKSetOfOne.fcontains("2") shouldBe false
        strKKSetOfTwo.fcontains("0") shouldBe false
        strKKSetOfTwo.fcontains("1") shouldBe true
        strKKSetOfTwo.fcontains("2") shouldBe true
        strKKSetOfTwo.fcontains("3") shouldBe false
        strKKSetOfThree.fcontains("0") shouldBe false
        strKKSetOfThree.fcontains("1") shouldBe true
        strKKSetOfThree.fcontains("2") shouldBe true
        strKKSetOfThree.fcontains("3") shouldBe true
        strKKSetOfThree.fcontains("4") shouldBe false
        
        strISetOfNone.fcontains("1") shouldBe false
        strISetOfOne.fcontains("0") shouldBe false
        strISetOfOne.fcontains("1") shouldBe true
        strISetOfOne.fcontains("2") shouldBe false
        strISetOfTwo.fcontains("0") shouldBe false
        strISetOfTwo.fcontains("1") shouldBe true
        strISetOfTwo.fcontains("2") shouldBe true
        strISetOfTwo.fcontains("3") shouldBe false
        strISetOfThree.fcontains("0") shouldBe false
        strISetOfThree.fcontains("1") shouldBe true
        strISetOfThree.fcontains("2") shouldBe true
        strISetOfThree.fcontains("3") shouldBe true
        strISetOfThree.fcontains("4") shouldBe false

    }

    test("fcontainsAny") {
        strKKSetOfNone.fcontainsAny(strKKSetOfNone) shouldBe false

        strKKSetOfOne.fcontainsAny(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fcontainsAny(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fcontainsAny(strKKSetOfTwo) shouldBe true
        strKKSetOfOne.fcontainsAny(strKKSetMaverick) shouldBe false

        strKKSetOfTwo.fcontainsAny(strKKSetOfNone) shouldBe true
        strKKSetOfTwo.fcontainsAny(strKKSetOfOne) shouldBe true
        strKKSetOfTwo.fcontainsAny(strKKSetOfFour) shouldBe true
        strKKSetOfTwo.fcontainsAny(strKKSetMaverick) shouldBe false

        strKKSetOfThree.fcontainsAny(strKKSetOfNone) shouldBe true
        strKKSetOfThree.fcontainsAny(strKKSetOfFour) shouldBe true
        strKKSetOfThree.fcontainsAny(strKKSetMaverick) shouldBe false

        strISetOfNone.fcontainsAny(strISetOfNone) shouldBe false

        strISetOfOne.fcontainsAny(strISetOfNone) shouldBe true
        strISetOfOne.fcontainsAny(strISetOfOne) shouldBe true
        strISetOfOne.fcontainsAny(strISetOfTwo) shouldBe true
        strISetOfOne.fcontainsAny(strISetMaverick) shouldBe false

        strISetOfTwo.fcontainsAny(strISetOfNone) shouldBe true
        strISetOfTwo.fcontainsAny(strISetOfOne) shouldBe true
        strISetOfTwo.fcontainsAny(strISetOfFour) shouldBe true
        strISetOfTwo.fcontainsAny(strISetMaverick) shouldBe false

        strISetOfThree.fcontainsAny(strISetOfNone) shouldBe true
        strISetOfThree.fcontainsAny(strISetOfFour) shouldBe true
        strISetOfThree.fcontainsAny(strISetMaverick) shouldBe false

        // mixed mode

        strKKSetOfThree.fcontainsAny(strISetOfNone) shouldBe true
        strKKSetOfThree.fcontainsAny(strISetOfFour) shouldBe true
        strKKSetOfThree.fcontainsAny(strISetMaverick) shouldBe false

        strISetOfThree.fcontainsAny(strKKSetOfNone) shouldBe true
        strISetOfThree.fcontainsAny(strKKSetOfFour) shouldBe true
        strISetOfThree.fcontainsAny(strKKSetMaverick) shouldBe false

    }

    test("fdropItem") {
        strKKSetOfNone.fdropItem("0").equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fdropItem("0").equals(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fdropItem("1").equals(strKKSetOfNone) shouldBe true

        strKKSetOfTwo.fdropItem("0").equals(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fdropItem("1").equals(FKSet.ofs("2")) shouldBe true
        strKKSetOfTwo.fdropItem("2").equals(strKKSetOfOne) shouldBe true
        strKKSetOfTwo.fdropItem("3").equals(strKKSetOfTwo) shouldBe true

        strKKSetOfThree.fdropItem("0").equals(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fdropItem("1").equals(FKSet.ofs("2", "3")) shouldBe true
        strKKSetOfThree.fdropItem("2").equals(FKSet.ofs("1", "3")) shouldBe true
        strKKSetOfThree.fdropItem("3").equals(strKKSetOfTwo) shouldBe true
        strKKSetOfThree.fdropItem("4").equals(strKKSetOfThree) shouldBe true

        strISetOfNone.fdropItem("0").equals(strISetOfNone) shouldBe true
        strISetOfOne.fdropItem("0").equals(strISetOfOne) shouldBe true
        strISetOfOne.fdropItem("1").equals(strISetOfNone) shouldBe true

        strISetOfTwo.fdropItem("0").equals(strISetOfTwo) shouldBe true
        strISetOfTwo.fdropItem("1").equals(FKSet.ofi("2")) shouldBe true
        strISetOfTwo.fdropItem("2").equals(strISetOfOne) shouldBe true
        strISetOfTwo.fdropItem("3").equals(strISetOfTwo) shouldBe true

        strISetOfThree.fdropItem("0").equals(strISetOfThree) shouldBe true
        strISetOfThree.fdropItem("1").equals(FKSet.ofi("2", "3")) shouldBe true
        strISetOfThree.fdropItem("2").equals(FKSet.ofi("1", "3")) shouldBe true
        strISetOfThree.fdropItem("3").equals(strISetOfTwo) shouldBe true
        strISetOfThree.fdropItem("4").equals(strISetOfThree) shouldBe true

        // mixed mode

        strISetOfThree.fdropItem("3").equal(strKKSetOfTwo) shouldBe true
        strKKSetOfThree.fdropItem("3").equal(strISetOfTwo) shouldBe true
    }

    test("fdropAll") {
        strKKSetOfNone.fdropAll(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true

        strKKSetOfOne.fdropAll(strKKSetOfNone).equals(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fdropAll(strKKSetOfOne).equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fdropAll(strKKSetOfTwo).equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fdropAll(strKKSetMaverick).equals(strKKSetOfOne) shouldBe true

        strKKSetOfTwo.fdropAll(strKKSetOfNone).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fdropAll(strKKSetOfOne).equals(FKSet.ofs("2")) shouldBe true
        strKKSetOfTwo.fdropAll(strKKSetOfFour).equals(strKKSetOfNone) shouldBe true
        strKKSetOfTwo.fdropAll(strKKSetMaverick).equals(strKKSetOfTwo) shouldBe true

        strKKSetOfThree.fdropAll(strKKSetOfNone).equals(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fdropAll(strKKSetOfFour).equals(strKKSetOfNone) shouldBe true
        strKKSetOfThree.fdropAll(strKKSetMaverick).equals(strKKSetOfThree) shouldBe true

        strISetOfNone.fdropAll(strISetOfNone).equals(strISetOfNone) shouldBe true

        strISetOfOne.fdropAll(strISetOfNone).equals(strISetOfOne) shouldBe true
        strISetOfOne.fdropAll(strISetOfOne).equals(strISetOfNone) shouldBe true
        strISetOfOne.fdropAll(strISetOfTwo).equals(strISetOfNone) shouldBe true
        strISetOfOne.fdropAll(strISetMaverick).equals(strISetOfOne) shouldBe true

        strISetOfTwo.fdropAll(strISetOfNone).equals(strISetOfTwo) shouldBe true
        strISetOfTwo.fdropAll(strISetOfOne).equals(FKSet.ofi("2")) shouldBe true
        strISetOfTwo.fdropAll(strISetOfFour).equals(strISetOfNone) shouldBe true
        strISetOfTwo.fdropAll(strISetMaverick).equals(strISetOfTwo) shouldBe true

        strISetOfThree.fdropAll(strISetOfNone).equals(strISetOfThree) shouldBe true
        strISetOfThree.fdropAll(strISetOfFour).equals(strISetOfNone) shouldBe true
        strISetOfThree.fdropAll(strISetMaverick).equals(strISetOfThree) shouldBe true

        // mixed mode

        strISetOfThree.fdropAll(strKKSetOfFour).equal(strISetOfNone) shouldBe true
        strKKSetOfThree.fdropAll(strISetOfFour).equal(strISetOfNone) shouldBe true

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
        strKKSetOfNone.fdropWhen { false }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.fdropWhen { true }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fdropWhen { it == "1" }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fdropWhen { it != "1" }.equals(strKKSetOfOne) shouldBe true
        strKKSetOfThree.fdropWhen { it < "2" }.equals(strKKSetOfTwo.fmap { (it[0].code+1).toChar().toString() }) shouldBe true
        strKKSetOfThree.fdropWhen { it >= "2" }.equals(strKKSetOfOne) shouldBe true

        strISetOfNone.fdropWhen { false }.equals(strISetOfNone) shouldBe true
        strISetOfNone.fdropWhen { true }.equals(strISetOfNone) shouldBe true
        strISetOfOne.fdropWhen { it == "1" }.equals(strISetOfNone) shouldBe true
        strISetOfOne.fdropWhen { it != "1" }.equals(strISetOfOne) shouldBe true
        strISetOfThree.fdropWhen { it < "2" }.equals(strISetOfTwo.fmap { (it[0].code+1).toChar().toString() }) shouldBe true
        strISetOfThree.fdropWhen { it >= "2" }.equals(strISetOfOne) shouldBe true
    }

    test("fempty") {
        strKKSetOfNone.fempty() shouldBe true
        strKKSetOfOne.fempty() shouldBe false
        strISetOfNone.fempty() shouldBe true
        strISetOfOne.fempty() shouldBe false
        (strKKSetOfNone === strISetOfNone) shouldBe true
    }

    test("ffilter") {
        strKKSetOfNone.ffilter { false }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.ffilter { true }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.ffilter { it == "1" }.equals(strKKSetOfOne) shouldBe true
        strKKSetOfOne.ffilter { it != "1" }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfThree.ffilter { it < "2" }.equals(strKKSetOfOne) shouldBe true
        strKKSetOfThree.ffilter { it >= "2" }.equals(strKKSetOfTwo.fmap { (it[0].code+1).toChar().toString() }) shouldBe true

        strISetOfNone.ffilter { false }.equals(strISetOfNone) shouldBe true
        strISetOfNone.ffilter { true }.equals(strISetOfNone) shouldBe true
        strISetOfOne.ffilter { it == "1" }.equals(strISetOfOne) shouldBe true
        strISetOfOne.ffilter { it != "1" }.equals(strISetOfNone) shouldBe true
        strISetOfThree.ffilter { it < "2" }.equals(strISetOfOne) shouldBe true
        strISetOfThree.ffilter { it >= "2" }.equals(strISetOfTwo.fmap { (it[0].code+1).toChar().toString() }) shouldBe true
    }
    
    test("ffilterNot") {
        strKKSetOfNone.ffilterNot { false }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.ffilterNot { true }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.ffilterNot { it == "1" }.equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.ffilterNot { it != "1" }.equals(strKKSetOfOne) shouldBe true
        strKKSetOfThree.ffilterNot { it < "2" }.equals(strKKSetOfTwo.fmap {(it[0].code+1).toChar().toString() }) shouldBe true
        strKKSetOfThree.ffilterNot { it >= "2" }.equals(strKKSetOfOne) shouldBe true

        strISetOfNone.ffilterNot { false }.equals(strISetOfNone) shouldBe true
        strISetOfNone.ffilterNot { true }.equals(strISetOfNone) shouldBe true
        strISetOfOne.ffilterNot { it == "1" }.equals(strISetOfNone) shouldBe true
        strISetOfOne.ffilterNot { it != "1" }.equals(strISetOfOne) shouldBe true
        strISetOfThree.ffilterNot { it < "2" }.equals(strISetOfTwo.fmap { (it[0].code+1).toChar().toString() }) shouldBe true
        strISetOfThree.ffilterNot { it >= "2" }.equals(strISetOfOne) shouldBe true
    }

    test("ffind") {
        strKKSetOfNone.ffind { false } shouldBe null
        strKKSetOfNone.ffind { true } shouldBe null
        strKKSetOfOne.ffind { it == "1" } shouldBe "1"
        strKKSetOfOne.ffind { it != "1" } shouldBe null
        strKKSetOfThree.ffind { it < "2" } shouldBe "1"
        strKKSetOfThree.ffind { it >= "2" } shouldBe null

        strISetOfNone.ffind { false } shouldBe null
        strISetOfNone.ffind { true } shouldBe null
        strISetOfOne.ffind { it == "1" } shouldBe "1"
        strISetOfOne.ffind { it != "1" } shouldBe null
        strISetOfThree.ffind { it < "2" } shouldBe "1"
        strISetOfThree.ffind { it >= "2" } shouldBe null
    }

    test("fisSubsetOf") {
        strKKSetOfNone.fisSubsetOf(strKKSetOfNone) shouldBe true

        strKKSetOfOne.fisSubsetOf(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fisSubsetOf(strKKSetOfNone) shouldBe false
        strKKSetOfNone.fisSubsetOf(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fisSubsetOf(strKKSetMaverick) shouldBe false
        strKKSetMaverick.fisSubsetOf(strKKSetOfOne) shouldBe false
        strKKSetOfOne.fisSubsetOf(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fisSubsetOf(strKKSetOfOne) shouldBe false

        strKKSetOfThree.fisSubsetOf(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fisSubsetOf(strKKSetOfNone) shouldBe false
        strKKSetOfNone.fisSubsetOf(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fisSubsetOf(strKKSetMaverick) shouldBe false
        strKKSetMaverick.fisSubsetOf(strKKSetOfThree) shouldBe false
        strKKSetOfThree.fisSubsetOf(strKKSetOfTwo) shouldBe false
        strKKSetOfTwo.fisSubsetOf(strKKSetOfThree) shouldBe true

        strISetOfNone.fisSubsetOf(strISetOfNone) shouldBe true

        strISetOfOne.fisSubsetOf(strISetOfOne) shouldBe true
        strISetOfOne.fisSubsetOf(strISetOfNone) shouldBe false
        strISetOfNone.fisSubsetOf(strISetOfOne) shouldBe true
        strISetOfOne.fisSubsetOf(strISetMaverick) shouldBe false
        strISetMaverick.fisSubsetOf(strISetOfOne) shouldBe false
        strISetOfOne.fisSubsetOf(strISetOfTwo) shouldBe true
        strISetOfTwo.fisSubsetOf(strISetOfOne) shouldBe false

        strISetOfThree.fisSubsetOf(strISetOfThree) shouldBe true
        strISetOfThree.fisSubsetOf(strISetOfNone) shouldBe false
        strISetOfNone.fisSubsetOf(strISetOfThree) shouldBe true
        strISetOfThree.fisSubsetOf(strISetMaverick) shouldBe false
        strISetMaverick.fisSubsetOf(strISetOfThree) shouldBe false
        strISetOfThree.fisSubsetOf(strISetOfTwo) shouldBe false
        strISetOfTwo.fisSubsetOf(strISetOfThree) shouldBe true

        // mixed mode

        strISetOfThree.fisSubsetOf(strKKSetOfThree) shouldBe true
        strISetOfThree.fisSubsetOf(strKKSetOfNone) shouldBe false
        strISetOfNone.fisSubsetOf(strKKSetOfThree) shouldBe true
        strISetOfThree.fisSubsetOf(strKKSetMaverick) shouldBe false
        strISetMaverick.fisSubsetOf(strKKSetOfThree) shouldBe false
        strISetOfThree.fisSubsetOf(strKKSetOfTwo) shouldBe false
        strISetOfTwo.fisSubsetOf(strKKSetOfThree) shouldBe true

        strKKSetOfThree.fisSubsetOf(strISetOfThree) shouldBe true
        strKKSetOfThree.fisSubsetOf(strISetOfNone) shouldBe false
        strKKSetOfNone.fisSubsetOf(strISetOfThree) shouldBe true
        strKKSetOfThree.fisSubsetOf(strISetMaverick) shouldBe false
        strKKSetMaverick.fisSubsetOf(strISetOfThree) shouldBe false
        strKKSetOfThree.fisSubsetOf(strISetOfTwo) shouldBe false
        strKKSetOfTwo.fisSubsetOf(strISetOfThree) shouldBe true

    }

    test("fpick") {
        strKKSetOfNone.fpick() shouldBe null
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
        strISetOfNone.fpick() shouldBe null
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
        strKKSetOfNone.fAND(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.fAND(strKKSetOfOne).equals(strKKSetOfNone) shouldBe true

        strKKSetOfOne.fAND(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fAND(strKKSetOfOne).equals(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fAND(strKKSetOfThree).equals(strKKSetOfOne) shouldBe true
        strKKSetOfThree.fAND(strKKSetOfOne).equals(strKKSetOfOne) shouldBe true

        strKKSetOfTwo.fAND(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        strKKSetOfTwo.fAND(strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fAND(strKKSetOfThree).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfThree.fAND(strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true

        strKKSetOfThree.fAND(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        strKKSetOfThree.fAND(strKKSetOfThree).equals(strKKSetOfThree) shouldBe true
        FKSet.ofs("2").fAND(strKKSetOfThree).equals(FKSet.ofs("2")) shouldBe true
        strKKSetOfThree.fAND(FKSet.ofs("2")).equals(FKSet.ofs("2")) shouldBe true

        strISetOfNone.fAND(strISetOfNone).equals(strISetOfNone) shouldBe true
        strISetOfNone.fAND(strISetOfOne).equals(strISetOfNone) shouldBe true

        strISetOfOne.fAND(strISetOfNone).equals(strISetOfNone) shouldBe true
        strISetOfOne.fAND(strISetOfOne).equals(strISetOfOne) shouldBe true
        strISetOfOne.fAND(strISetOfThree).equals(strISetOfOne) shouldBe true
        strISetOfThree.fAND(strISetOfOne).equals(strISetOfOne) shouldBe true

        strISetOfTwo.fAND(strISetOfNone).equals(strISetOfNone) shouldBe true
        strISetOfTwo.fAND(strISetOfTwo).equals(strISetOfTwo) shouldBe true
        strISetOfTwo.fAND(strISetOfThree).equals(strISetOfTwo) shouldBe true
        strISetOfThree.fAND(strISetOfTwo).equals(strISetOfTwo) shouldBe true

        strISetOfThree.fAND(strISetOfNone).equals(strISetOfNone) shouldBe true
        strISetOfThree.fAND(strISetOfThree).equals(strISetOfThree) shouldBe true
        FKSet.ofs("2").fAND(strISetOfThree).equals(FKSet.ofs("2")) shouldBe true
        strISetOfThree.fAND(FKSet.ofs("2")).equals(FKSet.ofi("2")) shouldBe true

        // mixed mode

        strISetOfThree.fAND(strKKSetOfTwo).equals(strISetOfTwo) shouldBe true
        strKKSetOfThree.fAND(strISetOfTwo).equals(strKKSetOfTwo) shouldBe true

    }

    test("fNOT") {
        strKKSetOfNone.fNOT(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.fNOT(strKKSetOfOne).equals(strKKSetOfNone) shouldBe true

        strKKSetOfOne.fNOT(strKKSetOfNone).equals(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fNOT(strKKSetOfOne).equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fNOT(strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        strKKSetOfThree.fNOT(strKKSetOfOne).equals(FKSet.ofs("2","3")) shouldBe true

        strKKSetOfTwo.fNOT(strKKSetOfNone).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fNOT(strKKSetOfTwo).equals(strKKSetOfNone) shouldBe true
        strKKSetOfTwo.fNOT(strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        strKKSetOfThree.fNOT(strKKSetOfTwo).equals(strKKSetOfOne3) shouldBe true

        strKKSetOfThree.fNOT(strKKSetOfNone).equals(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fNOT(strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        FKSet.ofs("2").fNOT(strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        strKKSetOfThree.fNOT(FKSet.ofs("2")).equals(FKSet.ofs("1","3")) shouldBe true

        strISetOfNone.fNOT(strISetOfNone).equals(strISetOfNone) shouldBe true
        strISetOfNone.fNOT(strISetOfOne).equals(strISetOfNone) shouldBe true

        strISetOfOne.fNOT(strISetOfNone).equals(strISetOfOne) shouldBe true
        strISetOfOne.fNOT(strISetOfOne).equals(strISetOfNone) shouldBe true
        strISetOfOne.fNOT(strISetOfThree).equals(strISetOfNone) shouldBe true
        strISetOfThree.fNOT(strISetOfOne).equals(FKSet.ofi("2","3")) shouldBe true

        strISetOfTwo.fNOT(strISetOfNone).equals(strISetOfTwo) shouldBe true
        strISetOfTwo.fNOT(strISetOfTwo).equals(strISetOfNone) shouldBe true
        strISetOfTwo.fNOT(strISetOfThree).equals(strISetOfNone) shouldBe true
        strISetOfThree.fNOT(strISetOfTwo).equals(strISetOfOne3) shouldBe true

        strISetOfThree.fNOT(strISetOfNone).equals(strISetOfThree) shouldBe true
        strISetOfThree.fNOT(strISetOfThree).equals(strISetOfNone) shouldBe true
        FKSet.ofs("2").fNOT(strISetOfThree).equals(strISetOfNone) shouldBe true
        strISetOfThree.fNOT(FKSet.ofs("2")).equals(FKSet.ofi("1","3")) shouldBe true

        // mixed mode

        strISetOfThree.fNOT(strKKSetOfTwo).equals(strISetOfOne3) shouldBe true
        strKKSetOfThree.fNOT(strISetOfTwo).equals(strKKSetOfOne3) shouldBe true
    }

    test("fOR") {
        strKKSetOfNone.fOR(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fOR(strKKSetOfNone).equals(strKKSetOfOne) shouldBe true
        strKKSetOfNone.fOR(strKKSetOfOne).equals(strKKSetOfOne) shouldBe true

        strKKSetOfTwo.fOR(strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fOR(strKKSetOfNone).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfNone.fOR(strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fOR(strKKSetOfTwoOfst1).equals(strKKSetOfThree) shouldBe true
        strKKSetOfTwoOfst1.fOR(strKKSetOfTwo).equals(strKKSetOfThree) shouldBe true
        strKKSetOfTwo.fOR(strKKSetOfTwoOfst2).equals(strKKSetOfFour) shouldBe true
        strKKSetOfTwoOfst2.fOR(strKKSetOfTwo).equals(strKKSetOfFour) shouldBe true

        strKKSetOfThree.fOR(strKKSetOfNone).equals(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fOR(strKKSetOfThree).equals(strKKSetOfThree) shouldBe true
        FKSet.ofs("2").fOR(strKKSetOfThree).equals(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fOR(FKSet.ofs("2")).equals(strKKSetOfThree) shouldBe true

        strISetOfNone.fOR(strISetOfNone).equals(strISetOfNone) shouldBe true
        strISetOfOne.fOR(strISetOfNone).equals(strISetOfOne) shouldBe true
        strISetOfNone.fOR(strISetOfOne).equals(strISetOfOne) shouldBe true

        strISetOfTwo.fOR(strISetOfTwo).equals(strISetOfTwo) shouldBe true
        strISetOfTwo.fOR(strISetOfNone).equals(strISetOfTwo) shouldBe true
        strISetOfNone.fOR(strISetOfTwo).equals(strISetOfTwo) shouldBe true
        strISetOfTwo.fOR(strISetOfTwoOfst1).equals(strISetOfThree) shouldBe true
        strISetOfTwoOfst1.fOR(strISetOfTwo).equals(strISetOfThree) shouldBe true
        strISetOfTwo.fOR(strISetOfTwoOfst2).equals(strISetOfFour) shouldBe true
        strISetOfTwoOfst2.fOR(strISetOfTwo).equals(strISetOfFour) shouldBe true

        strISetOfThree.fOR(strISetOfNone).equals(strISetOfThree) shouldBe true
        strISetOfThree.fOR(strISetOfThree).equals(strISetOfThree) shouldBe true
        FKSet.ofi("2").fOR(strISetOfThree).equals(strISetOfThree) shouldBe true
        strISetOfThree.fOR(FKSet.ofs("2")).equals(strISetOfThree) shouldBe true

        // mixed mode

        strISetOfTwoOfst1.fOR(strKKSetOfTwo).equals(strISetOfThree) shouldBe true
        strISetOfTwo.fOR(strKKSetOfTwoOfst2).equals(strISetOfFour) shouldBe true

        strKKSetOfTwoOfst1.fOR(strISetOfTwo).equals(strKKSetOfThree) shouldBe true
        strKKSetOfTwo.fOR(strISetOfTwoOfst2).equals(strKKSetOfFour) shouldBe true
    }

    test("fXOR") {
        strKKSetOfNone.fXOR(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.fXOR(strKKSetOfOne).equals(strKKSetOfOne) shouldBe true

        strKKSetOfOne.fXOR(strKKSetOfNone).equals(strKKSetOfOne) shouldBe true
        strKKSetOfOne.fXOR(strKKSetOfOne).equals(strKKSetOfNone) shouldBe true
        strKKSetOfOne.fXOR(strKKSetOfThree).equals(FKSet.ofs("2","3")) shouldBe true
        strKKSetOfThree.fXOR(strKKSetOfOne).equals(FKSet.ofs("2","3")) shouldBe true

        strKKSetOfTwo.fXOR(strKKSetOfNone).equals(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.fXOR(strKKSetOfTwo).equals(strKKSetOfNone) shouldBe true
        strKKSetOfTwo.fXOR(strKKSetOfThree).equals(strKKSetOfOne3) shouldBe true
        strKKSetOfThree.fXOR(strKKSetOfTwo).equals(strKKSetOfOne3) shouldBe true

        strKKSetOfThree.fXOR(strKKSetOfNone).equals(strKKSetOfThree) shouldBe true
        strKKSetOfThree.fXOR(strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        FKSet.ofs("2").fXOR(strKKSetOfThree).equals(FKSet.ofs("1", "3")) shouldBe true
        strKKSetOfThree.fXOR(FKSet.ofs("2")).equals(FKSet.ofs("1","3")) shouldBe true

        strISetOfNone.fXOR(strISetOfNone).equals(strISetOfNone) shouldBe true
        strISetOfNone.fXOR(strISetOfOne).equals(strISetOfOne) shouldBe true

        strISetOfOne.fXOR(strISetOfNone).equals(strISetOfOne) shouldBe true
        strISetOfOne.fXOR(strISetOfOne).equals(strISetOfNone) shouldBe true
        strISetOfOne.fXOR(strISetOfThree).equals(FKSet.ofi("2","3")) shouldBe true
        strISetOfThree.fXOR(strISetOfOne).equals(FKSet.ofi("2","3")) shouldBe true

        strISetOfTwo.fXOR(strISetOfNone).equals(strISetOfTwo) shouldBe true
        strISetOfTwo.fXOR(strISetOfTwo).equals(strISetOfNone) shouldBe true
        strISetOfTwo.fXOR(strISetOfThree).equals(strISetOfOne3) shouldBe true
        strISetOfThree.fXOR(strISetOfTwo).equals(strISetOfOne3) shouldBe true

        strISetOfThree.fXOR(strISetOfNone).equals(strISetOfThree) shouldBe true
        strISetOfThree.fXOR(strISetOfThree).equals(strISetOfNone) shouldBe true
        FKSet.ofs("2").fXOR(strISetOfThree).equals(FKSet.ofs("1","3")) shouldBe true
        strISetOfThree.fXOR(FKSet.ofs("2")).equals(FKSet.ofi("1","3")) shouldBe true

        // mixed mode

        strISetOfTwo.fXOR(strKKSetOfThree).equals(strISetOfOne3) shouldBe true
        strKKSetOfTwo.fXOR(strISetOfThree).equals(strKKSetOfOne3) shouldBe true
    }
})
