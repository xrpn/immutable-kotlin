package com.xrpn.immutable

import com.xrpn.immutable.FStack.Companion.equal
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FStackTest : FunSpec({

    beforeTest {
    }

    test("isEmpty") {
        FStack.emptyFStack<Int>().isEmpty() shouldBe true
        FStackBody.of(FLCons("a", FLNil)).isEmpty() shouldBe false
    }

    test("nullablePeek") {
        FStack.emptyFStack<Int>().nullableTop() shouldBe null
        FStackBody.of(FLCons("a", FLNil)).nullableTop() shouldBe "a"
        FStackBody.of(FLCons("b", FLCons("a", FLNil))).nullableTop() shouldBe "b"
    }

    //
    // ================ companion object
    //

    test("co.== throws") {
        (FStack.emptyFStack<Int>() == FStack.emptyFStack<Int>()) shouldBe true
    }

    test("co.empty") {
        FStack.emptyFStack<Int>() shouldBe FStackBody.of(FLNil)
    }

    test("co.==") {
        FStack.emptyFStack<Int>().equal(FStack.emptyFStack<Int>()) shouldBe true
        FStackBody.of(FLCons("a", FLCons("b", FLNil))).equal(FStackBody.of(FLCons("a", FLCons("b", FLNil)))) shouldBe true
    }

    test("co.push") {
        FStack.push(FStack.emptyFStack(), "a") shouldBe FStackBody.of(FLCons("a", FLNil))
        FStack.push(FStackBody.of(FLCons("a", FLNil)), "b") shouldBe FStackBody.of(FLCons("b", FLCons("a", FLNil)))
    }

    test("co.equal") {
        FStack.equal2(FStack.emptyFStack<Int>(), FStack.emptyFStack<Int>()) shouldBe true
        FStack.equal2(FStackBody.of(FLCons("a", FLNil)), FStackBody.of(FLCons("b", FLNil))) shouldBe false
        FStack.equal2(FStackBody.of(FLCons("b", FLNil)), FStackBody.of(FLCons("a", FLNil))) shouldBe false
        FStack.equal2(FStackBody.of(FLCons("a", FLNil)), FStackBody.of(FLCons("a", FLNil))) shouldBe true
        FStack.equal2(FStackBody.of(FLCons("b", FLCons("a", FLNil))), FStackBody.of(FLCons("a", FLNil))) shouldBe false
        FStack.equal2(FStackBody.of(FLCons("a", FLCons("b", FLNil))), FStackBody.of(FLCons("a", FLNil))) shouldBe false
        FStack.equal2(FStackBody.of(FLCons("b", FLNil)), FStackBody.of(FLCons("b", FLCons("a", FLNil)))) shouldBe false
        FStack.equal2(FStackBody.of(FLCons("b", FLNil)), FStackBody.of(FLCons("b", FLCons("a", FLNil)))) shouldBe false
        FStack.equal2(FStackBody.of(FLCons("a", FLCons("b", FLNil))), FStackBody.of(FLCons("b", FLCons("a", FLNil)))) shouldBe false
        FStack.equal2(FStackBody.of(FLCons("a", FLCons("b", FLNil))), FStackBody.of(FLCons("a", FLCons("b", FLNil)))) shouldBe true
    }

})
