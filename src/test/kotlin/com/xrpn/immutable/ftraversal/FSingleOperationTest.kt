package com.xrpn.immutable.ftraversal

import com.xrpn.imapi.TSDJ
import com.xrpn.imapi.TSDJInvalid
import com.xrpn.imapi.TSDJValid
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.FSingleOperationApplicativeTraversal
import com.xrpn.immutable.TraversalImpl
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FSingleOperationTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("single operation applicative traversal") {
        val top = 5
        fun opMaybe(i: Int): TSDJ<Int, Int> = if ( 0 == i % (top-1) ) TSDJInvalid(i) else TSDJValid(i)
        fun opValid(i: Int): TSDJ<Int, Int> = TSDJValid(i)
        fun opInvalid(i: Int): TSDJ<Int, Int> = TSDJInvalid(i)
        fun fail(i: Int?): String = "${i?.toString() ?: 'x'} IS BAD!"
        fun pass(i: Int?): String = "${i?.toString() ?: '0'} is a good number"
        fun boom(i: Int?): String = throw Throwable("$i")
        val sotMaybe = FSingleOperationApplicativeTraversal(::opMaybe, ::fail, ::pass)
        val sotValid = FSingleOperationApplicativeTraversal(::opValid, ::boom, ::pass)
        val sotInvalid = FSingleOperationApplicativeTraversal(::opInvalid, ::fail, ::boom)
        checkAll(repeats, Arb.flist<Int, Int>(Arb.int(0..top),1..20)) { fl ->
            
            // maybe
            
            val (errMaybe, resMaybe) = sotMaybe.grossTraversal(fl)
            errMaybe.fsize() + resMaybe.fsize() shouldBe fl.fsize()
            val djMaybe = sotMaybe.traversal(fl)
            djMaybe.left()?.let {
                it.fsize() shouldBe errMaybe.fsize()
            } ?: djMaybe.right()!!.fsize() shouldBe resMaybe.fsize()
            djMaybe.right()?.let {
                it.fsize() shouldBe resMaybe.fsize()
            } ?: djMaybe.left()!!.fsize() shouldBe errMaybe.fsize()

            // all fail

            val (errAll, resEmpty) = sotInvalid.grossTraversal(fl)
            resEmpty.fempty() shouldBe true
            errAll.fempty() shouldBe false
            errAll.fsize() shouldBe fl.fsize()
            val djErrs = sotInvalid.traversal(fl)
            djErrs.isRight() shouldBe false
            djErrs.right() shouldBe null
            djErrs.isLeft() shouldBe true
            djErrs.left()!!.fsize() shouldBe errAll.fsize()

            // all succeed

            val (errEmpty, resAll) = sotValid.grossTraversal(fl)
            resAll.fempty() shouldBe false
            resAll.fsize() shouldBe fl.fsize()
            errEmpty.fempty() shouldBe true
            val djRes = sotValid.traversal(fl)
            djRes.isRight() shouldBe true
            djRes.right()!!.fsize() shouldBe resAll.fsize()
            djRes.isLeft() shouldBe false
            djRes.left() shouldBe null
        }

        val (errMaybe, resMaybe) = sotMaybe.grossTraversal(emptyIMList())
        val djMaybe = sotMaybe.traversal(emptyIMList())
        resMaybe.fempty() shouldBe true
        errMaybe.fempty() shouldBe true
        djMaybe.isRight() shouldBe false
        djMaybe.isLeft() shouldBe false

    }

    test("single operation traversal") {
        val top = 5
        fun opMaybe(i: Int): Int = if ( 0 == i % (top-1) ) throw RuntimeException("$i") else i
        fun opValid(i: Int): Int = i
        fun opInvalid(i: Int): Int = throw RuntimeException("$i")
        fun fail(i: Int?): String = "${i?.toString() ?: 'x'} IS BAD!"
        fun boom(i: Int?): String = throw RuntimeException("$i")
        checkAll(repeats, Arb.flist<Int, Int>(Arb.int(0..top),1..20)) { fl ->

            // maybe

            val timpl = TraversalImpl(::fail, fl)

            val (errMaybe, resMaybe) = timpl.grossTraverse<Int,Exception>(::opMaybe)
            errMaybe.fsize() + resMaybe.fsize() shouldBe fl.fsize()
            val djMaybe = timpl.traverse<Int,Exception>(::opMaybe)
            djMaybe.left()?.let {
                it.fsize() shouldBe errMaybe.fsize()
            } ?: djMaybe.right()!!.fsize() shouldBe resMaybe.fsize()
            djMaybe.right()?.let {
                it.fsize() shouldBe resMaybe.fsize()
            } ?: djMaybe.left()!!.fsize() shouldBe errMaybe.fsize()

            // all fail

            val (errAll, resEmpty) = timpl.grossTraverse<Int,Exception>(::opInvalid)
            resEmpty.fempty() shouldBe true
            errAll.fempty() shouldBe false
            errAll.fsize() shouldBe fl.fsize()
            val djErrs = timpl.traverse<Int,Exception>(::opInvalid)
            djErrs.isRight() shouldBe false
            djErrs.right() shouldBe null
            djErrs.isLeft() shouldBe true
            djErrs.left()!!.fsize() shouldBe errAll.fsize()

            // all succeed

            val (errEmpty, resAll) = timpl.grossTraverse<Int,Exception>(::opValid)
            resAll.fempty() shouldBe false
            resAll.fsize() shouldBe fl.fsize()
            errEmpty.fempty() shouldBe true
            val djRes = timpl.traverse<Int,Exception>(::opValid)
            djRes.isRight() shouldBe true
            djRes.right()!!.fsize() shouldBe resAll.fsize()
            djRes.isLeft() shouldBe false
            djRes.left() shouldBe null

        }

        val timpl = TraversalImpl(::boom, emptyIMList())

        val (errMaybe, resMaybe) = timpl.grossTraverse<Int,Exception>(::opMaybe)
        val djMaybe = timpl.traverse<Int,Exception>(::opMaybe)
        resMaybe.fempty() shouldBe true
        errMaybe.fempty() shouldBe true
        djMaybe.isRight() shouldBe false
        djMaybe.isLeft() shouldBe false

    }

})
