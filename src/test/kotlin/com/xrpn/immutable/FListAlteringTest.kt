package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone = FList.of(*arrayOf<Int>())
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfSix = FList.of(*arrayOf<Int>(1,2,3,3,2,1))

class FListAlteringTest : FunSpec({

    beforeTest {}

    test("co.append") {
        intListOfNone.fappend(0) shouldBe FLCons(0,FLNil)
        intListOfOne.fappend(0) shouldBe FLCons(1,FLCons(0,FLNil))
        FList.of(*arrayOf<Int>(2,1)).fappend(0) shouldBe FLCons(2, FLCons(1,FLCons(0,FLNil)))
        FList.of(*arrayOf<Int>(3,2,1)).fappend(0) shouldBe
                FLCons(3,FLCons(2,FLCons(1,FLCons(0,FLNil))))
    }

    test("co.appendAll") {
        intListOfNone.fappendAll(intListOfNone) shouldBe FLNil
        intListOfOne.fappendAll(intListOfNone) shouldBe intListOfOne
        intListOfOne.fappendAll(intListOfOne) shouldBe FLCons(1,FLCons(1,FLNil))
        intListOfOne.fappendAll(intListOfTwo) shouldBe FLCons(1,FLCons(1,FLCons(2,FLNil)))
        FList.of(*arrayOf<Int>(2,1)).fappendAll(intListOfThree) shouldBe FLCons(2, FLCons(1, intListOfThree))
        FList.of(*arrayOf<Int>(3,2,1)).fappendAll(intListOfThree) shouldBe
                FLCons(3,FLCons(2,FLCons(1,intListOfThree)))
    }

    test("co.prepend") {
        intListOfNone.fprepend(0) shouldBe FLCons(0,FLNil)
        intListOfOne.fprepend(0) shouldBe FLCons(0,FLCons(1,FLNil))
        FList.of(*arrayOf<Int>(2,1)).fprepend(0) shouldBe FLCons(0, FLCons(2,FLCons(1,FLNil)))
        FList.of(*arrayOf<Int>(3,2,1)).fprepend(0) shouldBe
                FLCons(0,FLCons(3,FLCons(2,FLCons(1,FLNil))))
    }

    test("co.prependAll") {
        intListOfNone.fprependAll(intListOfNone) shouldBe FLNil
        intListOfOne.fprependAll(intListOfNone) shouldBe intListOfOne
        intListOfOne.fprependAll(intListOfOne) shouldBe FLCons(1,FLCons(1,FLNil))
        intListOfOne.fprependAll(intListOfTwo) shouldBe FLCons(1,FLCons(2,FLCons(1,FLNil)))
        FList.of(*arrayOf<Int>(2,1)).fprependAll(intListOfThree) shouldBe intListOfThree.fappend(2).fappend(1)
        FList.of(*arrayOf<Int>(3,2,1)).fprependAll(intListOfThree) shouldBe intListOfSix
    }
})