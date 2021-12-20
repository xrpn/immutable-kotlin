package com.xrpn.immutable.fkksettest

import com.xrpn.imapi.*
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FKSet.Companion.ofk
import com.xrpn.immutable.FKSet.Companion.ofs
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val intKKSetOfNone = ofk(*emptyArrayOfInt)
private val intKKSetOfOne: IMXSetNotEmpty<Int> = ofk(1).nex()!!
private val intKKSetOfTwo: IMXSetNotEmpty<Int> = ofk(1, 2).nex()!!
private val intKKSetOfThree = ofk(1, 2, 3).nex<Int>()!!

private val intSSetOfOne: IMSetNotEmpty<Int> = ofs(1).ne()!!
private val intSSetOfTwo: IMSetNotEmpty<Int> = ofs(1, 2).ne()!!

class FKKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intKKSetOfNone as IMKASetNotEmpty<Int, Int>)
        }
        (@Suppress("UNCHECKED_CAST") (intKKSetOfOne.faddItem(2) as IMKKSetNotEmpty<Int>)).equal(intKKSetOfTwo) shouldBe true
        (@Suppress("UNCHECKED_CAST") (intKKSetOfTwo.faddItem(3) as IMKKSetNotEmpty<Int>)).equal(intKKSetOfThree) shouldBe true
        intKKSetOfOne.faddItem(2).equals(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.faddItem(3).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.faddItem(4).fsize() shouldBe 4

        intSSetOfOne.faddItem(2).equals(intKKSetOfTwo) shouldBe true
        intSSetOfOne.faddItem(2).equals(intSSetOfTwo) shouldBe true
    }

    test("faddItem on empty") {
        val aux1 = intKKSetOfNone.faddItem(1)
        aux1.equals(intKKSetOfOne)  shouldBe true
        (aux1 === intKKSetOfOne) shouldBe false
        aux1.ner() shouldNotBe null
        aux1.ne() shouldBe null
        aux1.nex<String>() shouldNotBe null
    }
})
