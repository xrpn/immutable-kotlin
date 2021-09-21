package com.xrpn.immutable

import com.xrpn.imapi.IMSetNotEmpty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FKSet.ofs(*arrayOf<Int>())
private val intSetOfOne = @Suppress("UNCHECKED_CAST") (FKSet.ofs(1) as IMSetNotEmpty<String, Int>)
private val intSetOfTwo = @Suppress("UNCHECKED_CAST") (FKSet.ofs(1, 2) as IMSetNotEmpty<String, Int>)
private val intSetOfThree = @Suppress("UNCHECKED_CAST") (FKSet.ofs(1, 2, 3) as IMSetNotEmpty<String, Int>)

class FSKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddSoO") {
        intSetOfNone.faddSoO(1.toSSoO()).strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.faddSoO(2.toSSoO()).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.faddSoO(3.toSSoO()).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.faddSoO(4.toSSoO()).fsize() shouldBe 4
    }

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intSetOfNone as IMSetNotEmpty<String, Int>)
        }
        intSetOfOne.faddItem(2).strongEqual(intSetOfTwo) shouldBe true
        1.toSSoO().faddItem(2).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.faddItem(3).strongEqual(intSetOfThree) shouldBe true
        1.toSSoO().faddItem(2).faddItem(3).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.faddItem(4).fsize() shouldBe 4
    }

})
