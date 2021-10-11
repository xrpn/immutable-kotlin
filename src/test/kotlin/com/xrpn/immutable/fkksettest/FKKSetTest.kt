package com.xrpn.immutable.fkksettest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.imapi.SymKeyType
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val copaKKSetOfNone = FKSet.ofk(*emptyArrayOfCoPA)

private val strKKSetOfNone = FKSet.ofs(*emptyArrayOfStr)
private val strKKSetOf3 = FKSet.ofs("1", "2", "3")
private val strISetOf3 = FKSet.ofi("1", "2", "3")
private val strkk = SymKeyType(String::class)

private val intKKSetOfNone = FKSet.ofs(*emptyArrayOfStr)
private val intKKSetOf3 = FKSet.ofi(1, 2, 3)
private val intSSetOf3 = FKSet.ofs(1, 2, 3)
private val intkk = SymKeyType(Int::class)


private val lskt = SymKeyType(Long::class)

class FKKSetTest : FunSpec({

    beforeTest {}

    test("toIMKSetNotEmpty a") {
        copaKKSetOfNone.toIMKSetNotEmpty(IntKeyType) shouldBe null
        copaKKSetOf3.toIMKSetNotEmpty(IntKeyType)?.equals(copaISetOf3) shouldBe true
        copaKKSetOfNone.toIMKSetNotEmpty(StrKeyType) shouldBe null
        copaKKSetOf3.toIMKSetNotEmpty(StrKeyType)?.equals(copaSSetOf3) shouldBe true
        copaKKSetOfNone.toIMKSetNotEmpty(lskt) shouldBe null
        copaKKSetOf3.toIMKSetNotEmpty(lskt) shouldBe null
        copaKKSetOfNone.toIMKSetNotEmpty(copaKey) shouldBe null
        (copaKKSetOf3.toIMKSetNotEmpty(copaKey) === copaKKSetOf3) shouldBe true
    }

    test("toIMKSetNotEmpty b") {
        strKKSetOfNone.toIMKSetNotEmpty(IntKeyType) shouldBe null
        strKKSetOf3.toIMKSetNotEmpty(IntKeyType)?.equals(strISetOf3) shouldBe true
        strKKSetOfNone.toIMKSetNotEmpty(StrKeyType) shouldBe null
        (strKKSetOf3.toIMKSetNotEmpty(StrKeyType) === strKKSetOf3) shouldBe true
        strKKSetOfNone.toIMKSetNotEmpty(lskt) shouldBe null
        strKKSetOf3.toIMKSetNotEmpty(lskt) shouldBe null
        strKKSetOfNone.toIMKSetNotEmpty(strkk) shouldBe null
        (strKKSetOf3.toIMKSetNotEmpty(strkk) === strKKSetOf3) shouldBe true
    }

    test("toIMKSetNotEmpty c") {
        intKKSetOfNone.toIMKSetNotEmpty(IntKeyType) shouldBe null
        (intKKSetOf3.toIMKSetNotEmpty(IntKeyType) === intKKSetOf3) shouldBe true
        intKKSetOfNone.toIMKSetNotEmpty(StrKeyType) shouldBe null
        intKKSetOf3.toIMKSetNotEmpty(StrKeyType)?.equals(intSSetOf3) shouldBe true
        intKKSetOfNone.toIMKSetNotEmpty(lskt) shouldBe null
        intKKSetOf3.toIMKSetNotEmpty(lskt) shouldBe null
        intKKSetOfNone.toIMKSetNotEmpty(intkk) shouldBe null
        (intKKSetOf3.toIMKSetNotEmpty(intkk) === intKKSetOf3) shouldBe true
    }

})
