package com.xrpn.immutable.fksettest

import com.xrpn.imapi.IMSet
import com.xrpn.immutable.fksetFunctorLaw
import com.xrpn.immutable.mapDouble2Long
import com.xrpn.immutable.mapInt2String
import com.xrpn.immutable.mapString2Double
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fiset

class FKSetLawsTest: FunSpec({

    val repeats = Triple(10, 10, 30)

    beforeTest {}

    test("fqueue functor law") {
        checkAll(repeats.first, Arb.fiset(Arb.int(),repeats.second..repeats.third)) { fs: IMSet<Int> ->
            fksetFunctorLaw.identityLaw(fs) shouldBe true
            fksetFunctorLaw.associativeLaw(fs, mapInt2String, mapString2Double, mapDouble2Long) shouldBe true
        }
    }

})