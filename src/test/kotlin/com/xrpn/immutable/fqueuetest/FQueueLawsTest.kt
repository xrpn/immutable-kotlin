package com.xrpn.immutable.fqueuetest

import com.xrpn.imapi.IMQueue
import com.xrpn.immutable.fqueueFunctorLaw
import com.xrpn.immutable.mapDouble2Long
import com.xrpn.immutable.mapInt2String
import com.xrpn.immutable.mapString2Double
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fqueue

class FQueueLawsTest: FunSpec({

    val repeats = Triple(10, 10, 30)

    beforeTest {}

    test("fqueue functor law") {
        checkAll(repeats.first, Arb.fqueue(Arb.int(),repeats.second..repeats.third)) { fq: IMQueue<Int> ->
            fqueueFunctorLaw.identityLaw(fq) shouldBe true
            fqueueFunctorLaw.associativeLaw(fq, mapInt2String, mapString2Double, mapDouble2Long) shouldBe true
        }
    }

})