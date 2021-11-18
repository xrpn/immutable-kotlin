package com.xrpn.immutable.fbstreetest

import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.IMKMappable
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbsItree
import io.kotest.xrpn.fbstreeWithDups

class FBSTreeLawsTest: FunSpec({

    val repeats = Triple(30, 10, 30)

    beforeTest {}

    test("fbstree (no dups) functor law") {
        checkAll(repeats.first, Arb.fbsItree(Arb.int(),repeats.second..repeats.third)) { fs: IMBTree<Int,Int> ->
            imbtreeFunctorKLaw.identityLaw(fs) shouldBe true
            imbtreeFunctorKLaw.associativeLaw(fs, mapIInt2IString, mapIString2IDouble, mapIDouble2ILong) shouldBe true
        }
    }

    test("fbstree (dups) functor law") {
        checkAll(repeats.first, Arb.fbstreeWithDups(Arb.int(),repeats.second..repeats.third)) { fs: IMBTree<Int,Int> ->
            imbtreeFunctorKLaw.identityLaw(fs) shouldBe true
            imbtreeFunctorKLaw.associativeLaw(fs, mapIInt2IString, mapIString2IDouble, mapIDouble2ILong) shouldBe true
        }
    }

})