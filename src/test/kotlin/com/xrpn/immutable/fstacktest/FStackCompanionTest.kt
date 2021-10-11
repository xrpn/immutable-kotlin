package com.xrpn.immutable.fstacktest

import com.xrpn.imapi.IMStackEqual2
import com.xrpn.immutable.*
import com.xrpn.immutable.FStack.Companion.emptyIMStack
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith

private val itemA = "A"
private val itemB = "B"
private val itemC = "C"
private val strStackOfNone = FStack.of(*emptyArrayOfStr)
private val intStackOfNone = FStack.of(*emptyArrayOfInt)
private val strStackOfOne = FStack.of(*arrayOf<String>(itemA))
private val intStackOfOne = FStack.of(*arrayOf<Int>(1))
private val strStackOfTwo = FStack.of(*arrayOf<String>(itemA, itemB))
private val strStackOfTwoN = FStack.of(*arrayOf<String>("1", "2"))
private val intStackOfTwo = FStack.of(*arrayOf<Int>(1, 2))
private val strStackOfThree = FStack.of(*arrayOf<String>(itemA, itemB, itemC))
private val intStackOfThree = FStack.of(*arrayOf<Int>(1, 2, 3))

class FStackCompanionTest : FunSpec({

    beforeTest {}

    test("equals") {
        emptyIMStack<Int>().equals(null) shouldBe false
        emptyIMStack<Int>().equals(1) shouldBe false
        emptyIMStack<Int>().equals("") shouldBe false
        /* Sigh... */ strStackOfNone.equals(intStackOfNone) shouldBe true
        intStackOfTwo.equals(null) shouldBe false
        intStackOfTwo.equals(strStackOfNone) shouldBe false
        intStackOfTwo.equals(strStackOfTwo) shouldBe false
        strStackOfTwo.equals(intStackOfTwo) shouldBe false
        intStackOfTwo.equals(strStackOfTwoN) shouldBe false
        strStackOfTwoN.equals(intStackOfTwo) shouldBe false
        intStackOfTwo.equals("foobar") shouldBe false
        intStackOfTwo.equals(3) shouldBe false
    }

    test("toString() hashCode()") {
        FStack.emptyIMStack<Int>().toString() shouldBe "FStack(*)"
        val aux = FStack.emptyIMStack<Int>().hashCode()
        for (i in (1..100)) {
            aux shouldBe FStack.emptyIMStack<Int>().hashCode()
        }
        intStackOfTwo.toString() shouldStartWith "${FStackBody::class.simpleName}({"
        val aux2 = intStackOfTwo.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe intStackOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FStackBody.hashCode(intStackOfTwo as FStackBody<Int>) shouldBe intStackOfTwo.hashCode()
        }
    }
    
    // IMStackCompanion

    test("co.emptyImStack") {
        (FStack.emptyIMStack<Int>() === FStackBody.empty) shouldBe true
        FStack.emptyIMStack<Int>() shouldBe FStackBody.of(FLNil)
        FStack.emptyIMStack<Int>() shouldBe FStackBody.of(null)
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

    test("co.of varargs") {
        intStackOfNone.equals(emptyIMStack<Int>()) shouldBe true
        intStackOfNone.equals(FStackBody.of(FLNil)) shouldBe true
        intStackOfOne shouldBe FStackBody.of(FLCons(1,FLNil))
        intStackOfTwo shouldBe FStackBody.of(FLCons(1,FLCons(2,FLNil)))
        intStackOfThree shouldBe FStackBody.of(FLCons(1,FLCons(2,FLCons(3,FLNil))))
    }

    test("co.of iterator") {
        (FStack.of(emptyArrayOfInt.iterator()) === emptyIMStack<Int>()) shouldBe true
        FStack.of(arrayOf<Int>(1).iterator()) shouldBe FStackBody.of(FLCons(1,FLNil))
        FStack.of(arrayOf<Int>(1,2).iterator()) shouldBe FStackBody.of(FLCons(1,FLCons(2,FLNil)))
        FStack.of(arrayOf<Int>(1,2,3).iterator()) shouldBe FStackBody.of(FLCons(1,FLCons(2,FLCons(3,FLNil))))
    }

    test("co.of Stack") {
        (FStack.of(emptyList()) === emptyIMStack<Int>()) shouldBe true
        FStack.of(listOf(1)) shouldBe FStackBody.of(FLCons(1,FLNil))
        FStack.of(listOf(1,2)) shouldBe FStackBody.of(FLCons(1,FLCons(2,FLNil)))
        FStack.of(listOf(1,2,3)) shouldBe FStackBody.of(FLCons(1,FLCons(2,FLCons(3,FLNil))))
    }

    test("co.of IMList") {
    }

    test("co.ofMap iterator") {
        (strStackOfNone === FStack.ofMap(emptyArrayOfInt.iterator(), ::fidentity)) shouldBe true
        strStackOfOne shouldBe FStack.ofMap(arrayOf(0).iterator()) { a -> (a+'A'.code).toChar().toString() }
        strStackOfTwo shouldBe FStack.ofMap(arrayOf(0, 1).iterator()) { a -> (a+'A'.code).toChar().toString() }
        strStackOfThree shouldBe FStack.ofMap(arrayOf(0, 1, 2).iterator()) { a -> (a+'A'.code).toChar().toString() }
    }

    test("co.ofMap Stack") {
        (strStackOfNone === FStack.ofMap(emptyList<Int>()) { a -> a }) shouldBe true
        strStackOfOne shouldBe FStack.ofMap(listOf(0)) { a -> (a+'A'.code).toChar().toString() }
        strStackOfTwo shouldBe FStack.ofMap(listOf(0, 1)) { a -> (a+'A'.code).toChar().toString() }
        strStackOfThree shouldBe FStack.ofMap(listOf(0, 1, 2)) { a -> (a+'A'.code).toChar().toString() }
    }
})
