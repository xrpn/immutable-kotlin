package com.xrpn.immutable

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMQueue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FQueueCompanionTest : FunSpec({

  beforeTest {}

  test("equals") {}

  test("toString() hashCode()") {}

  // IMQueueCompanion

  test("co.emptyIMQueue"){}
  test("co.of vararg") {}
  test("co.of Iterator") {}
  test("co.of List") {}
  test("co.of IMList") {}
  test("co.ofMap Iterator") {}
  test("co.ofMap List") {}

  test("co.Collection.toIMQueue()") {}

  // implementation

})
