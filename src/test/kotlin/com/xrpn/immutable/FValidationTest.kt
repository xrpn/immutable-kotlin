package com.xrpn.immutable

import com.xrpn.imapi.ErrExReport
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FValidationTest : FunSpec({

    // val repeatsLow = Pair(2, 40000)

    fun onError(e: ErrExReport<String>?): ErrExReport<String> = e!!.longMsg()
    val multipleTests = FList.of(tpIntNumValidation(1..2), tpStrMsgValidation(1..2))

    beforeTest {}

    test("sanity, pass") {
        val sInt = FSingleMappValidation(tpIntNumValidation(1..2), ::onError).validation(FList.of(TestProduct(1,"DD")))
        sInt.right()?.fsize() shouldBe 1
        val sStr = FSingleMappValidation(tpStrMsgValidation(1..2), ::onError).validation(FList.of(TestProduct(1,"DD")))
        sStr.right()?.fsize() shouldBe 1
    }

    test("sanity, fail") {
        val s1 = FSingleMappValidation(tpIntNumValidation(1..2), ::onError).validation(FList.of(TestProduct(0,"DDD")))
        s1.left()?.fsize() shouldBe 1
        val s2 = FSingleMappValidation(tpStrMsgValidation(1..2), ::onError).validation(FList.of(TestProduct(0,"DDD")))
        s2.left()?.fsize() shouldBe 1
        val m1a = FMultiMappValidation(multipleTests, ::onError).validation(FList.of(TestProduct(0,"DD")))
        m1a.left()?.fsize() shouldBe 1
        val m1b = FMultiMappValidation(multipleTests, ::onError).validation(FList.of(TestProduct(1,"DDD")))
        m1b.left()?.fsize() shouldBe 1
        val m2 = FMultiMappValidation(multipleTests, ::onError).validation(FList.of(TestProduct(0,"DDD")))
        m2.left()?.fsize() shouldBe 2
    }

})
