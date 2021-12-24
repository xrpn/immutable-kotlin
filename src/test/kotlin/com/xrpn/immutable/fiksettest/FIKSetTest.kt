package com.xrpn.immutable.fiksettest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.imapi.SymKeyType
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val copaISetOfNone = FKSet.ofi(*emptyArrayOfCoPA)

private val strISetOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISetOf3 = FKSet.ofi("1", "2", "3")
private val strKKSetOf3 = FKSet.ofk("1", "2", "3")

private val lskt = SymKeyType(Long::class)
private val strkk = SymKeyType(String::class)

class FIKSetTest : FunSpec({

    beforeTest {}

    test("toIMKSetNotEmpty a") {
        copaISetOfNone.toIMKSetNotEmpty(IntKeyType) shouldBe null
        (copaISetOf3.toIMKSetNotEmpty(IntKeyType) === copaISetOf3) shouldBe true
        copaISetOfNone.toIMKSetNotEmpty(StrKeyType) shouldBe null
        copaISetOf3.toIMKSetNotEmpty(StrKeyType)?.equals(copaSSetOf3) shouldBe true
        copaISetOfNone.toIMKSetNotEmpty(lskt) shouldBe null
        copaISetOf3.toIMKSetNotEmpty(lskt) shouldBe null
        copaISetOfNone.toIMKSetNotEmpty(copaKey) shouldBe null
        copaISetOf3.toIMKSetNotEmpty(copaKey)?.equals(copaKKSetOf3) shouldBe true
    }

    test("toIMKSetNotEmpty b") {
        strISetOfNone.toIMKSetNotEmpty(IntKeyType) shouldBe null
        (strISetOf3.toIMKSetNotEmpty(IntKeyType) === strISetOf3) shouldBe true
        strISetOfNone.toIMKSetNotEmpty(StrKeyType) shouldBe null
        strISetOf3.toIMKSetNotEmpty(StrKeyType)?.equals(strKKSetOf3) shouldBe true
        strISetOfNone.toIMKSetNotEmpty(lskt) shouldBe null
        strISetOf3.toIMKSetNotEmpty(lskt) shouldBe null
        strISetOfNone.toIMKSetNotEmpty(strkk) shouldBe null
        strISetOf3.toIMKSetNotEmpty(strkk)?.equals(strKKSetOf3) shouldBe true
    }

})
