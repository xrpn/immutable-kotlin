package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.imapi.ErrExReport
import com.xrpn.imapi.IMSet
import com.xrpn.immutable.FKMap.Companion.of
import com.xrpn.immutable.FKMap.Companion.emptyIMMap
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int

class FValidationTest : FunSpec({

    // val repeatsLow = Pair(2, 40000)

    fun onError(e: ErrExReport?): ErrExReport = e!!

    beforeTest {}

    test("sanity, pass") {
        val foo = FSingleValidation(tpIntNumValidation(1..2), ::onError).validation(FList.of(TestProduct(1,"DDD")))
        foo.fempty() shouldBe false
    }

    test("sanity, fail") {
        val foo = FSingleValidation(tpIntNumValidation(1..2), ::onError).validation(FList.of(TestProduct(0,"DDD")))
        foo.fempty() shouldBe false
    }

})
