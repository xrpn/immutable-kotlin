package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.fbtAssert
import com.xrpn.immutable.FBSTree.Companion.isChildMatch
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.parent
import com.xrpn.immutable.FBSTree.Companion.prune
import com.xrpn.immutable.FBSTree.Companion.find
import com.xrpn.immutable.FBSTree.Companion.findLast
import com.xrpn.immutable.FBSTree.Companion.addGraft
import com.xrpn.immutable.FBSTree.Companion.contains
import com.xrpn.immutable.FBSTree.Companion.contains2
import com.xrpn.immutable.FBSTree.Companion.insert
import com.xrpn.immutable.FBSTree.Companion.delete
import com.xrpn.immutable.FBSTree.Companion.equal
import io.kotest.assertions.fail
import io.kotest.property.Arb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FBSTreeFilteringTest : FunSpec({

    beforeTest {}

    test("fempty") {
        fail("not implemented")
    }

    test("ffilter") {
        fail("not implemented")
    }

    test("ffilterNot") {
        fail("not implemented")
    }

    test("ffindDistinct") {
        fail("not implemented")
    }

    test("ffind") {
        fail("not implemented")
    }

    test("fleftMost") {
        FBSTNil.fleftMost() shouldBe null
    }

    test("fpick") {
        fail("not implemented")
    }

    test("frightMost") {
        FBSTNil.frightMost() shouldBe null
    }

    test("fleftMost frightMost int") {
        for (size in IntRange(1, 20)) {
            val ary: IntArray = IntArray(size) {nextInt()}
            val max = ary.maxOrNull()!!
            val min = ary.minOrNull()!!
            FBSTree.of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).fleftMost() shouldBe TKVEntry.ofIntKey(min)
            FBSTree.of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).frightMost() shouldBe TKVEntry.ofIntKey(max)
        }
    }

    test("froot") {
        FBSTNil.froot() shouldBe null
        for (size in IntRange(1, 20)) {
            val ary: IntArray = IntArray(size) {nextInt()}
            FBSTree.of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).froot() shouldBe TKVEntry.ofIntKey(ary[0])
        }
    }
})
