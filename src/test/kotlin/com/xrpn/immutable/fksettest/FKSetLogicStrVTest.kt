package com.xrpn.immutable.fksettest

import com.xrpn.bridge.FKSetIterator
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
import io.kotest.xrpn.fiset
import io.kotest.xrpn.fsset

private val strKKSetOfNone = FKSet.ofs(*emptyArrayOfStr)
private val strKKSetOfOne = FKSet.ofs("1").necvs<String>()!!
private val strKKSetOfOne3 = FKSet.ofs("3").necvs<String>()!!
private val strKKSetOfTwo = FKSet.ofs("1", "2").necvs<String>()!!
private val strKKSetOfTwoOfst1 = FKSet.ofs("2", "3").necvs<String>()!!
private val strKKSetOfTwoOfst2 = FKSet.ofs("3", "4").necvs<String>()!!
private val strKKSetOfThree = FKSet.ofs("1", "2", "3").necvs<String>()!!
private val strKKSetOfFour = FKSet.ofs("1", "2", "3", "4").necvs<String>()!!
private val strKKSetMaverick = FKSet.ofs("113", "97").necvs<String>()!!

private val strISetOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISetOfOne = FKSet.ofi("1").nevs()!!
private val strISetOfOne3 = FKSet.ofi("3").nevs()!!
private val strISetOfTwo = FKSet.ofi("1", "2").nevs()!!
private val strISetOfTwoOfst1 = FKSet.ofi("2", "3").nevs()!!
private val strISetOfTwoOfst2 = FKSet.ofi("3", "4").nevs()!!
private val strISetOfThree = FKSet.ofi("1", "2", "3").nevs()!!
private val strISetOfFour = FKSet.ofi("1", "2", "3", "4").nevs()!!
private val strISetMaverick = FKSet.ofi("113", "97").nevs()!!

class FKSetLogicStrVTest : FunSpec({

    beforeTest {}

    test("fAND") {
        strKKSetOfNone.fAND(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfNone and strKKSetOfOne).equals(strKKSetOfNone) shouldBe true

        (strKKSetOfOne and strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfOne and strKKSetOfOne).equals(strKKSetOfOne) shouldBe true
        (strKKSetOfOne and strKKSetOfThree).equals(strKKSetOfOne) shouldBe true
        (strKKSetOfThree and strKKSetOfOne).equals(strKKSetOfOne) shouldBe true

        (strKKSetOfTwo and strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfTwo and strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true
        (strKKSetOfTwo and strKKSetOfThree).equals(strKKSetOfTwo) shouldBe true
        (strKKSetOfThree and strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true

        (strKKSetOfThree and strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfThree and strKKSetOfThree).equals(strKKSetOfThree) shouldBe true
        (FKSet.ofs("2") and strKKSetOfThree).equals(FKSet.ofs("2")) shouldBe true
        (strKKSetOfThree and FKSet.ofs("2")).equals(FKSet.ofs("2")) shouldBe true

        (strISetOfNone and strISetOfNone).equals(strISetOfNone) shouldBe true
        (strISetOfNone and strISetOfOne).equals(strISetOfNone) shouldBe true

        (strISetOfOne and strISetOfNone).equals(strISetOfNone) shouldBe true
        (strISetOfOne and strISetOfOne).equals(strISetOfOne) shouldBe true
        (strISetOfOne and strISetOfThree).equals(strISetOfOne) shouldBe true
        (strISetOfThree and strISetOfOne).equals(strISetOfOne) shouldBe true

        (strISetOfTwo and strISetOfNone).equals(strISetOfNone) shouldBe true
        (strISetOfTwo and strISetOfTwo).equals(strISetOfTwo) shouldBe true
        (strISetOfTwo and strISetOfThree).equals(strISetOfTwo) shouldBe true
        (strISetOfThree and strISetOfTwo).equals(strISetOfTwo) shouldBe true

        (strISetOfThree and strISetOfNone).equals(strISetOfNone) shouldBe true
        (strISetOfThree and strISetOfThree).equals(strISetOfThree) shouldBe true
        (FKSet.ofs("2") and strISetOfThree).equals(FKSet.ofs("2")) shouldBe true
        (strISetOfThree and FKSet.ofs("2")).equals(FKSet.ofi("2")) shouldBe true

        // mixed mode

        (strISetOfThree and strKKSetOfTwo).equals(strISetOfTwo) shouldBe true
        (strKKSetOfThree and strISetOfTwo).equals(strKKSetOfTwo) shouldBe true

    }

    test("fNOT") {
        strKKSetOfNone.fNOT(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfNone not strKKSetOfOne).equals(strKKSetOfNone) shouldBe true

        (strKKSetOfOne not strKKSetOfNone).equals(strKKSetOfOne) shouldBe true
        (strKKSetOfOne not strKKSetOfOne).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfOne not strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfThree not strKKSetOfOne).equals(FKSet.ofs("2","3")) shouldBe true

        (strKKSetOfTwo not strKKSetOfNone).equals(strKKSetOfTwo) shouldBe true
        (strKKSetOfTwo not strKKSetOfTwo).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfTwo not strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfThree not strKKSetOfTwo).equals(strKKSetOfOne3) shouldBe true

        (strKKSetOfThree not strKKSetOfNone).equals(strKKSetOfThree) shouldBe true
        (strKKSetOfThree not strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        (FKSet.ofs("2") not strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfThree not FKSet.ofs("2")).equals(FKSet.ofs("1","3")) shouldBe true

        (strISetOfNone not strISetOfNone).equals(strISetOfNone) shouldBe true
        (strISetOfNone not strISetOfOne).equals(strISetOfNone) shouldBe true

        (strISetOfOne not strISetOfNone).equals(strISetOfOne) shouldBe true
        (strISetOfOne not strISetOfOne).equals(strISetOfNone) shouldBe true
        (strISetOfOne not strISetOfThree).equals(strISetOfNone) shouldBe true
        (strISetOfThree not strISetOfOne).equals(FKSet.ofi("2","3")) shouldBe true

        (strISetOfTwo not strISetOfNone).equals(strISetOfTwo) shouldBe true
        (strISetOfTwo not strISetOfTwo).equals(strISetOfNone) shouldBe true
        (strISetOfTwo not strISetOfThree).equals(strISetOfNone) shouldBe true
        (strISetOfThree not strISetOfTwo).equals(strISetOfOne3) shouldBe true

        (strISetOfThree not strISetOfNone).equals(strISetOfThree) shouldBe true
        (strISetOfThree not strISetOfThree).equals(strISetOfNone) shouldBe true
        (FKSet.ofs("2") not strISetOfThree).equals(strISetOfNone) shouldBe true
        (strISetOfThree not FKSet.ofs("2")).equals(FKSet.ofi("1","3")) shouldBe true

        // mixed mode

        (strISetOfThree not strKKSetOfTwo).equals(strISetOfOne3) shouldBe true
        (strKKSetOfThree not strISetOfTwo).equals(strKKSetOfOne3) shouldBe true
    }

    test("fOR") {
        strKKSetOfNone.fOR(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfOne or strKKSetOfNone).equals(strKKSetOfOne) shouldBe true
        (strKKSetOfNone or strKKSetOfOne).equals(strKKSetOfOne) shouldBe true

        (strKKSetOfTwo or strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true
        (strKKSetOfTwo or strKKSetOfNone).equals(strKKSetOfTwo) shouldBe true
        (strKKSetOfNone or strKKSetOfTwo).equals(strKKSetOfTwo) shouldBe true
        (strKKSetOfTwo or strKKSetOfTwoOfst1).equals(strKKSetOfThree) shouldBe true
        (strKKSetOfTwoOfst1 or strKKSetOfTwo).equals(strKKSetOfThree) shouldBe true
        (strKKSetOfTwo or strKKSetOfTwoOfst2).equals(strKKSetOfFour) shouldBe true
        (strKKSetOfTwoOfst2 or strKKSetOfTwo).equals(strKKSetOfFour) shouldBe true

        (strKKSetOfThree or strKKSetOfNone).equals(strKKSetOfThree) shouldBe true
        (strKKSetOfThree or strKKSetOfThree).equals(strKKSetOfThree) shouldBe true
        (FKSet.ofs("2") or strKKSetOfThree).equals(strKKSetOfThree) shouldBe true
        (strKKSetOfThree or FKSet.ofs("2")).equals(strKKSetOfThree) shouldBe true

        (strISetOfNone or strISetOfNone).equals(strISetOfNone) shouldBe true
        (strISetOfOne or strISetOfNone).equals(strISetOfOne) shouldBe true
        (strISetOfNone or strISetOfOne).equals(strISetOfOne) shouldBe true

        (strISetOfTwo or strISetOfTwo).equals(strISetOfTwo) shouldBe true
        (strISetOfTwo or strISetOfNone).equals(strISetOfTwo) shouldBe true
        (strISetOfNone or strISetOfTwo).equals(strISetOfTwo) shouldBe true
        (strISetOfTwo or strISetOfTwoOfst1).equals(strISetOfThree) shouldBe true
        (strISetOfTwoOfst1 or strISetOfTwo).equals(strISetOfThree) shouldBe true
        (strISetOfTwo or strISetOfTwoOfst2).equals(strISetOfFour) shouldBe true
        (strISetOfTwoOfst2 or strISetOfTwo).equals(strISetOfFour) shouldBe true

        (strISetOfThree or strISetOfNone).equals(strISetOfThree) shouldBe true
        (strISetOfThree or strISetOfThree).equals(strISetOfThree) shouldBe true
        (FKSet.ofi("2") or strISetOfThree).equals(strISetOfThree) shouldBe true
        (strISetOfThree or FKSet.ofs("2")).equals(strISetOfThree) shouldBe true

        // mixed mode

        (strISetOfTwoOfst1 or strKKSetOfTwo).equals(strISetOfThree) shouldBe true
        (strISetOfTwo or strKKSetOfTwoOfst2).equals(strISetOfFour) shouldBe true

        (strKKSetOfTwoOfst1 or strISetOfTwo).equals(strKKSetOfThree) shouldBe true
        (strKKSetOfTwo or strISetOfTwoOfst2).equals(strKKSetOfFour) shouldBe true
    }

    test("fXOR") {
        strKKSetOfNone.fXOR(strKKSetOfNone).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfNone xor strKKSetOfOne).equals(strKKSetOfOne) shouldBe true

        (strKKSetOfOne xor strKKSetOfNone).equals(strKKSetOfOne) shouldBe true
        (strKKSetOfOne xor strKKSetOfOne).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfOne xor strKKSetOfThree).equals(FKSet.ofs("2","3")) shouldBe true
        (strKKSetOfThree xor strKKSetOfOne).equals(FKSet.ofs("2","3")) shouldBe true

        (strKKSetOfTwo xor strKKSetOfNone).equals(strKKSetOfTwo) shouldBe true
        (strKKSetOfTwo xor strKKSetOfTwo).equals(strKKSetOfNone) shouldBe true
        (strKKSetOfTwo xor strKKSetOfThree).equals(strKKSetOfOne3) shouldBe true
        (strKKSetOfThree xor strKKSetOfTwo).equals(strKKSetOfOne3) shouldBe true

        (strKKSetOfThree xor strKKSetOfNone).equals(strKKSetOfThree) shouldBe true
        (strKKSetOfThree xor strKKSetOfThree).equals(strKKSetOfNone) shouldBe true
        (FKSet.ofs("2") xor strKKSetOfThree).equals(FKSet.ofs("1", "3")) shouldBe true
        (strKKSetOfThree xor FKSet.ofs("2")).equals(FKSet.ofs("1","3")) shouldBe true

        (strISetOfNone xor strISetOfNone).equals(strISetOfNone) shouldBe true
        (strISetOfNone xor strISetOfOne).equals(strISetOfOne) shouldBe true

        (strISetOfOne xor strISetOfNone).equals(strISetOfOne) shouldBe true
        (strISetOfOne xor strISetOfOne).equals(strISetOfNone) shouldBe true
        (strISetOfOne xor strISetOfThree).equals(FKSet.ofi("2","3")) shouldBe true
        (strISetOfThree xor strISetOfOne).equals(FKSet.ofi("2","3")) shouldBe true

        (strISetOfTwo xor strISetOfNone).equals(strISetOfTwo) shouldBe true
        (strISetOfTwo xor strISetOfTwo).equals(strISetOfNone) shouldBe true
        (strISetOfTwo xor strISetOfThree).equals(strISetOfOne3) shouldBe true
        (strISetOfThree xor strISetOfTwo).equals(strISetOfOne3) shouldBe true

        (strISetOfThree xor strISetOfNone).equals(strISetOfThree) shouldBe true
        (strISetOfThree xor strISetOfThree).equals(strISetOfNone) shouldBe true
        (FKSet.ofs("2") xor strISetOfThree).equals(FKSet.ofs("1","3")) shouldBe true
        (strISetOfThree xor FKSet.ofs("2")).equals(FKSet.ofi("1","3")) shouldBe true

        // mixed mode

        (strISetOfTwo xor strKKSetOfThree).equals(strISetOfOne3) shouldBe true
        (strKKSetOfTwo xor strISetOfThree).equals(strKKSetOfOne3) shouldBe true
    }
})
