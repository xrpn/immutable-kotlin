package com.xrpn.immutable

import com.xrpn.imapi.IMStackEqual2
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FStackCompanionTest : FunSpec({

    beforeTest {}

    test("co.emptyImStack") {
        (FStack.emptyIMStack<Int>() == FStack.emptyIMStack<Int>()) shouldBe true
    }

    test("co.empty") {
        FStack.emptyIMStack<Int>() shouldBe FStackBody.of(FLNil)
    }

    test("co. [ IMStackEqual2 ]") {
        IMStackEqual2(FStack.emptyIMStack<Int>(), FStack.emptyIMStack<Int>()) shouldBe true
        IMStackEqual2(FStackBody.of(FLCons("a", FLNil)), FStackBody.of(FLCons("b", FLNil))) shouldBe false
        IMStackEqual2(FStackBody.of(FLCons("b", FLNil)), FStackBody.of(FLCons("a", FLNil))) shouldBe false
        IMStackEqual2(FStackBody.of(FLCons("a", FLNil)), FStackBody.of(FLCons("a", FLNil))) shouldBe true
        IMStackEqual2(FStackBody.of(FLCons("b", FLCons("a", FLNil))), FStackBody.of(FLCons("a", FLNil))) shouldBe false
        IMStackEqual2(FStackBody.of(FLCons("a", FLCons("b", FLNil))), FStackBody.of(FLCons("a", FLNil))) shouldBe false
        IMStackEqual2(FStackBody.of(FLCons("b", FLNil)), FStackBody.of(FLCons("b", FLCons("a", FLNil)))) shouldBe false
        IMStackEqual2(FStackBody.of(FLCons("b", FLNil)), FStackBody.of(FLCons("b", FLCons("a", FLNil)))) shouldBe false
        IMStackEqual2(FStackBody.of(FLCons("a", FLCons("b", FLNil))), FStackBody.of(FLCons("b", FLCons("a", FLNil)))) shouldBe false
        IMStackEqual2(FStackBody.of(FLCons("a", FLCons("b", FLNil))), FStackBody.of(FLCons("a", FLCons("b", FLNil)))) shouldBe true
    }

})
