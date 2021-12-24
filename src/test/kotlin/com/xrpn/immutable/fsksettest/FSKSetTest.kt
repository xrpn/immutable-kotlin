package com.xrpn.immutable.fsksettest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.imapi.SymKeyType
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val copaSSetOfNone = FKSet.ofs(*emptyArrayOfCoPA)

private val intSSetOfNone = FKSet.ofs(*emptyArrayOfInt)
private val intSSetOf3 = FKSet.ofs(1, 2, 3)
private val intKKSetOf3 = FKSet.ofk(1, 2, 3)

private val intkk = SymKeyType(Int::class)

private val lskt = SymKeyType(Long::class)

class FSKSetTest : FunSpec({

    beforeTest {}

    test("toIMKSetNotEmpty") {
        copaSSetOfNone.toIMKSetNotEmpty(IntKeyType) shouldBe null
        copaSSetOf3.toIMKSetNotEmpty(IntKeyType)?.equals(copaISetOf3) shouldBe true
        copaSSetOfNone.toIMKSetNotEmpty(StrKeyType) shouldBe null
        (copaSSetOf3.toIMKSetNotEmpty(StrKeyType) === copaSSetOf3) shouldBe true
        copaSSetOfNone.toIMKSetNotEmpty(lskt) shouldBe null
        copaSSetOf3.toIMKSetNotEmpty(lskt) shouldBe null
        copaSSetOfNone.toIMKSetNotEmpty(copaKey) shouldBe null
        copaSSetOf3.toIMKSetNotEmpty(copaKey)?.equals(copaKKSetOf3) shouldBe true
    }

    test("toIMKSetNotEmpty b") {
        intSSetOfNone.toIMKSetNotEmpty(IntKeyType) shouldBe null
        intSSetOf3.toIMKSetNotEmpty(IntKeyType)?.equals(intKKSetOf3) shouldBe true
        intSSetOfNone.toIMKSetNotEmpty(StrKeyType) shouldBe null
        (intSSetOf3.toIMKSetNotEmpty(StrKeyType) === intSSetOf3) shouldBe true
        intSSetOfNone.toIMKSetNotEmpty(lskt) shouldBe null
        intSSetOf3.toIMKSetNotEmpty(lskt) shouldBe null
        intSSetOfNone.toIMKSetNotEmpty(intkk) shouldBe null
        intSSetOf3.toIMKSetNotEmpty(intkk)?.equals(intKKSetOf3) shouldBe true
    }
})
