package com.xrpn.immutable.ftraversal

import com.xrpn.imapi.*
import com.xrpn.immutable.FList
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.FMultipleOperationsApplicativeTraversal
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FMultipleOperationTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("multiple operations applicative traversal") {
        val top = 10
        fun opMaybeA(i: Int): TSDJ<Int, Int> = if ( 0 == i % (top-1) ) TSDJInvalid(i) else TSDJValid(i)
        fun opMaybeB(i: Int): TSDJ<Int, Int> = if ( 0 == i % 3 ) TSDJInvalid(i) else TSDJValid(i)
        val opsMaybe = FList.of(::opMaybeA, ::opMaybeB)
        fun opValidA(i: Int): TSDJ<Int, Int> = TSDJValid(i)
        fun opValidB(i: Int): TSDJ<Int, Int> = TSDJValid(i)
        fun opValidC(i: Int): TSDJ<Int, Int> = TSDJValid(i)
        val opsValid = FList.of(::opValidA, ::opValidB, ::opValidC)
        fun opInvalidA(i: Int): TSDJ<Int, Int> = TSDJInvalid(i)
        fun opInvalidB(i: Int): TSDJ<Int, Int> = TSDJInvalid(i)
        fun opInvalidC(i: Int): TSDJ<Int, Int> = TSDJInvalid(i)
        fun opInvalidD(i: Int): TSDJ<Int, Int> = TSDJInvalid(i)
        val opsInvalid = FList.of(::opInvalidA, ::opInvalidB, ::opInvalidC, ::opInvalidD)
        fun fail(i: Int?): String = "${i?.toString() ?: 'x'} IS BAD!"
        fun pass(i: Int?): String = "${i?.toString() ?: '0'} is a good number"
        fun boom(i: Int?): String = throw RuntimeException("$i")
        val motMaybe = FMultipleOperationsApplicativeTraversal(opsMaybe, ::fail, ::pass)
        val motValid = FMultipleOperationsApplicativeTraversal(opsValid, ::boom, ::pass)
        val motInvalid = FMultipleOperationsApplicativeTraversal(opsInvalid, ::fail, ::boom)
        checkAll(repeats, Arb.flist<Int, Int>(Arb.int(0..top),1..20)) { fl ->
            
            // maybe
            
            val (errMaybe: IMList<String>, resMaybe: IMList<String>) = motMaybe.grossTraversal(fl)
            errMaybe.fsize() + resMaybe.fsize() shouldBe fl.fsize() * opsMaybe.fsize()
            val djMaybe: IMDj<IMList<String>, IMList<String>> = motMaybe.traversal(fl)
            djMaybe.left()?.let {
                it.fsize() shouldBe errMaybe.fsize()
            } ?: djMaybe.right()!!.fsize() shouldBe resMaybe.fsize()
            djMaybe.right()?.let {
                it.fsize() shouldBe resMaybe.fsize()
            } ?: djMaybe.left()!!.fsize() shouldBe errMaybe.fsize()

            // all fail

            val (errAll, resEmpty) = motInvalid.grossTraversal(fl)
            resEmpty.fempty() shouldBe true
            errAll.fempty() shouldBe false
            errAll.fsize() shouldBe fl.fsize() * opsInvalid.fsize()
            val djErrs = motInvalid.traversal(fl)
            djErrs.isRight() shouldBe false
            djErrs.right() shouldBe null
            djErrs.isLeft() shouldBe true
            djErrs.left()!!.fsize() shouldBe errAll.fsize()

            // all succeed

            val (errEmpty, resAll) = motValid.grossTraversal(fl)
            resAll.fempty() shouldBe false
            resAll.fsize() shouldBe fl.fsize() * opsValid.fsize()
            errEmpty.fempty() shouldBe true
            val djRes = motValid.traversal(fl)
            djRes.isRight() shouldBe true
            djRes.right()!!.fsize() shouldBe resAll.fsize()
            djRes.isLeft() shouldBe false
            djRes.left() shouldBe null
        }

        val (errMaybe, resMaybe) = motMaybe.grossTraversal(emptyIMList())
        val djMaybe = motMaybe.traversal(emptyIMList())
        resMaybe.fempty() shouldBe true
        errMaybe.fempty() shouldBe true
        djMaybe.isRight() shouldBe false
        djMaybe.isLeft() shouldBe false

    }

})
