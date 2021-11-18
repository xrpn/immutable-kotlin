package com.xrpn.immutable.fstacktest

import com.xrpn.imapi.IMStack
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fstack

class FStackLawsTest: FunSpec({

    val repeats = Triple(10, 10, 30)

    beforeTest {}

    test("fstack functor law") {
        checkAll(repeats.first, Arb.fstack(Arb.int(),repeats.second..repeats.third)) { fs: IMStack<Int> ->
            fstackFunctorLaw.identityLaw(fs) shouldBe true
            fstackFunctorLaw.associativeLaw(fs, mapInt2String, mapString2Double, mapDouble2Long) shouldBe true
        }
    }

})