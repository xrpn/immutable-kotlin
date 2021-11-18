package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMMappable
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FListLawsTest: FunSpec({

    val repeats = Triple(10, 10, 30)

    beforeTest {}

    test("flist functor law") {
        Arb.flist(Arb.int(), repeats.second..repeats.third).checkAll(repeats.first) { fl: IMList<Int> ->
            flistFunctorLaw.identityLaw(fl) shouldBe true
            flistFunctorLaw.associativeLaw(fl, mapInt2String, mapString2Double, mapDouble2Long) shouldBe true
        }
    }

})