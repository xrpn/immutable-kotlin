package com.xrpn.immutable.fsksettest

import com.xrpn.imapi.IMKASetNotEmpty
import com.xrpn.imapi.IMXSetNotEmpty
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val intSSetOfNone = FKSet.ofs(*emptyArrayOfInt)
private val intSSetOfOne = FKSet.ofs(1).ne()!!
private val intSSetOfTwo = FKSet.ofs(1, 2).ne()!!
private val intSSetOfThree = FKSet.ofs(1, 2, 3).ne()!!

private val intKKSetOfOne: IMXSetNotEmpty<Int> = FKSet.ofk(1).nex()!!
private val intKKSetOfTwo: IMXSetNotEmpty<Int> = FKSet.ofk(1, 2).nex()!!

class FSKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intSSetOfNone as IMKASetNotEmpty<String, Int>)
        }
        (@Suppress("UNCHECKED_CAST") (intSSetOfOne.faddItem(2) as IMKASetNotEmpty<String, Int>)).equal(intSSetOfTwo) shouldBe true
        (@Suppress("UNCHECKED_CAST") (intSSetOfTwo.faddItem(3) as IMKASetNotEmpty<String, Int>)).equal(intSSetOfThree) shouldBe true
        intSSetOfOne.faddItem(2).equal(intSSetOfTwo) shouldBe true
        intSSetOfTwo.faddItem(3).equal(intSSetOfThree) shouldBe true
        intSSetOfThree.faddItem(4).fsize() shouldBe 4

        intKKSetOfOne.faddItem(2).equal(intSSetOfTwo) shouldBe true
        intKKSetOfTwo.faddItem(3).equal(intSSetOfThree) shouldBe true
    }

    test("faddItem on empty") {
        val aux1 = intSSetOfNone.faddItem(1)
        aux1.equals(intSSetOfOne) shouldBe true
        (aux1 === intSSetOfOne) shouldBe false
        aux1.ner() shouldNotBe null
        aux1.ne() shouldNotBe null
        aux1.nex<String>() shouldBe null

    }
})
