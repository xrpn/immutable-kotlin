package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMListFiltering
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))

class FListExtrasTest : FunSpec({

  beforeTest {}

  test("plus") {
    val ilon: IMList<Int> = intListOfNone
    val ilo1: IMList<Int> = intListOfOne
    val ilo2: IMList<Int> = intListOfTwo

    (ilon + ilo1) shouldBe intListOfOne
    (ilo1 + ilon) shouldBe intListOfOne
    (ilo1 + ilo2) shouldBe FList.of(1, 1, 2)
    (ilo2 + ilo1) shouldBe FList.of(1, 2, 1)
  }

  test("minus") {
    val ilon: IMList<Int> = intListOfNone
    val ilo1: IMList<Int> = intListOfOne
    val ilo2: IMList<Int> = intListOfTwo

    (ilon - ilo1) shouldBe intListOfNone
    (ilo1 - ilon) shouldBe intListOfOne
    (ilo1 - ilo2) shouldBe intListOfNone
    (ilo2 - ilo1) shouldBe FList.of(2)
  }

})
