package com.xrpn.immutable.misc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

private const val verbose = false

inline fun <reified R, reified S> reifiedGenericIsSame(r: R, s: S): Boolean = (r is S) && (s is R)
interface FooBar<out A: Any, out B: Any>
typealias FBOI<A> = FooBar<Int,A>
typealias FBOS<A> = FooBar<String,A>

interface I1<out A>
interface I2<A>: I1<A>
interface I3 {
    companion object {
        fun <A> f1(a: A, b: I2<A>) {
            print("$a $b")
        }
    }
}


class ExperimentalSandbox : FunSpec({

    beforeTest {}

    test("co.insert items sorted asc") {
        open class Base<out A: Any, out B: Any>(val a: A, val b: B): FooBar<A, B>

        class SpecialOfInt<out B: Any>(b: B) : Base<Int, B>(1, b)
        class SpecialOfStr<out B: Any>(b: B) : Base<String, B>("1", b)

// val soitd: FBOI<Int> = SpecialOfInt(2)
// val sostd: FBOS<Int> = SpecialOfStr(2)
        val soi = SpecialOfInt(2)
        val sos = SpecialOfStr(2)
//
// EDIT
//
        val soitd: FBOI<Int> = soi
        val sostd: FBOS<Int> = sos
        reifiedGenericIsSame(soi, sos) shouldBe false     // passes
        // reifiedGenericIsSame(soi, sos) shouldBe true      // FAILS
        // reifiedGenericIsSame(soitd, sostd) shouldBe false // FAILS
        reifiedGenericIsSame(soitd, sostd) shouldBe true  // passes
    }

//    test("puzzle") {
//        val i2 = object: I2<Int>{}
//        I3.f1("s", i2)
//    }

})
