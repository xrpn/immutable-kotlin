package com.xrpn.immutable

import com.xrpn.immutable.TKVEntry.Companion.intKeyOf
import com.xrpn.immutable.TKVEntry.Companion.strKeyOf
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


private val tkvInt1 = TKVEntry.ofIntKey(1)
private val tkvInt2 = TKVEntry.ofIntKey(2)
private val tkvStr1 = TKVEntry.ofStrKey(1)
private val tkvStr2 = TKVEntry.ofStrKey(2)
private val tkvInt1cr = TKVEntry.of(intKeyOf(1), 1, reverseIntCompare)
private val tkvInt2cr = TKVEntry.of(intKeyOf(2), 2, reverseIntCompare)
private val tkvStr1cr = TKVEntry.of(strKeyOf(1), 1, reverseStrCompare)
private val tkvStr2cr = TKVEntry.of(strKeyOf(2), 2, reverseStrCompare)

private val tkvIntA = TKVEntry.ofIntKey("A")
private val tkvIntB = TKVEntry.ofIntKey("B")
private val tkvStrA = TKVEntry.ofStrKey("A")
private val tkvStrB = TKVEntry.ofStrKey("B")
private val tkvIntAcr = TKVEntry.of(intKeyOf("A"), "A", reverseIntCompare)
private val tkvIntBcr = TKVEntry.of(intKeyOf("B"), "B", reverseIntCompare)
private val tkvStrAcr = TKVEntry.of(strKeyOf("A"), "A", reverseStrCompare)
private val tkvStrBcr = TKVEntry.of(strKeyOf("B"), "B", reverseStrCompare)

class FTKVentryTest : FunSpec({

    beforeTest {}

    test("compare equals intk intv") {
        tkvInt1.equals(null) shouldBe false
        tkvInt1.equals(tkvInt1) shouldBe true
        tkvInt1.equals(TKVEntry.ofIntKey(1)) shouldBe true
        TKVEntry.ofIntKey(1).equals(tkvInt1) shouldBe true
        tkvInt1.equals(tkvInt2) shouldBe false
        tkvInt2.equals(tkvInt1) shouldBe false
        tkvInt1.equals(TKVEntry.ofIntKey(2)) shouldBe false
        TKVEntry.ofIntKey(2).equals(tkvInt1) shouldBe false
    }

    test("compare equals strk strv") {
        tkvStrA.equals(tkvStrA) shouldBe true
        tkvStrA.equals(TKVEntry.ofStrKey("A")) shouldBe true
        TKVEntry.ofStrKey("A").equals(tkvStrA) shouldBe true
        tkvStrA.equals(tkvStrB) shouldBe false
        tkvStrB.equals(tkvStrA) shouldBe false
        tkvStrA.equals(TKVEntry.ofStrKey("B")) shouldBe false
        TKVEntry.ofIntKey(2).equals(tkvStrA) shouldBe false
    }

    test("compare equals mix") {
        tkvInt1.equals(tkvStr1) shouldBe false
        tkvStr1.equals(tkvInt1) shouldBe false

        tkvIntA.equals(tkvStr1) shouldBe false
        tkvStr1.equals(tkvIntA) shouldBe false
    }

    test("compare equals intk strv") {
        tkvIntA.equals(null) shouldBe false
        tkvIntA.equals(tkvIntA) shouldBe true
        tkvIntA.equals(TKVEntry.ofIntKey("A")) shouldBe true
        TKVEntry.ofIntKey("A").equals(tkvIntA) shouldBe true
        tkvIntA.equals(tkvIntB) shouldBe false
        tkvIntB.equals(tkvIntA) shouldBe false
        tkvIntA.equals(TKVEntry.ofIntKey("B")) shouldBe false
        TKVEntry.ofIntKey("B").equals(tkvIntA) shouldBe false
        tkvIntA.equals(tkvStrA) shouldBe false
        tkvStrA.equals(tkvIntA) shouldBe false
    }

    test("comparator equals intk intv ") {
        tkvInt1cr.equals(null) shouldBe false
        tkvInt1cr.equals(tkvInt1cr) shouldBe true
        tkvInt1cr.equals(tkvInt1) shouldBe true
        tkvInt1cr.equals(TKVEntry.of(intKeyOf(1), 1, reverseIntCompare)) shouldBe true
        tkvInt1cr.equals(TKVEntry.ofIntKey(1)) shouldBe true
        TKVEntry.of(intKeyOf(1), 1, reverseIntCompare).equals(tkvInt1cr) shouldBe true
        TKVEntry.ofIntKey(1).equals(tkvInt1cr) shouldBe true
        tkvInt1cr.equals(tkvInt2cr) shouldBe false
        tkvInt2cr.equals(tkvInt1cr) shouldBe false
        tkvInt1cr.equals(TKVEntry.of(intKeyOf(2), 2, reverseIntCompare)) shouldBe false
        val ex1 = shouldThrow<IllegalStateException> {
            tkvInt1cr.equals(TKVEntry.ofIntKey(2))
        }
        ex1.message shouldBe TKVEntryK.CANNOT_COMPARE
        TKVEntry.of(intKeyOf(2), 2, reverseIntCompare).equals(tkvInt1cr) shouldBe false
        val ex2 = shouldThrow<IllegalStateException> {
            TKVEntry.ofIntKey(2).equals(tkvInt1cr)
        }
        ex2.message shouldBe TKVEntryK.CANNOT_COMPARE
    }

    test("comparator equals intk strv ") {
        tkvIntAcr.equals(null) shouldBe false
        tkvIntAcr.equals(tkvIntAcr) shouldBe true
        tkvIntAcr.equals(TKVEntry.of(intKeyOf("A"), "A", reverseIntCompare)) shouldBe true
        tkvIntAcr.equals(TKVEntry.ofIntKey("A")) shouldBe true
        TKVEntry.of(intKeyOf("A"), "A", reverseIntCompare).equals(tkvIntAcr) shouldBe true
        TKVEntry.ofIntKey("A").equals(tkvIntAcr) shouldBe true
        tkvIntAcr.equals(tkvIntBcr) shouldBe false
        tkvIntBcr.equals(tkvIntAcr) shouldBe false
        tkvIntAcr.equals(TKVEntry.of(intKeyOf("B"), "B", reverseIntCompare)) shouldBe false
        val ex1 = shouldThrow<IllegalStateException> {
            tkvIntAcr.equals(TKVEntry.ofIntKey("B"))
        }
        ex1.message shouldBe TKVEntryK.CANNOT_COMPARE
        TKVEntry.of(intKeyOf("B"), "B", reverseIntCompare).equals(tkvIntAcr) shouldBe false
        val ex2 = shouldThrow<IllegalStateException> {
            TKVEntry.ofIntKey("B").equals(tkvIntAcr)
        }
        ex2.message shouldBe TKVEntryK.CANNOT_COMPARE
    }

    test("comparator equals mix ") {
        tkvInt1cr.equals(tkvStr1cr) shouldBe false
        tkvStr1cr.equals(tkvInt1cr) shouldBe false

        tkvIntAcr.equals(tkvStr1cr) shouldBe false
        tkvStr1cr.equals(tkvIntAcr) shouldBe false
    }

    test("compare > intk intv") {
        (tkvInt1 > tkvInt1) shouldBe false
        (tkvInt1 > TKVEntry.ofIntKey(1)) shouldBe false
        (TKVEntry.ofIntKey(1) > tkvInt1) shouldBe false
        (tkvInt1 > tkvInt2) shouldBe false
        (tkvInt2 > tkvInt1) shouldBe true
        (tkvInt1 > TKVEntry.ofIntKey(2)) shouldBe false
        (TKVEntry.ofIntKey(2) > tkvInt1) shouldBe true
    }

    test("compare > strk strvv") {
        (tkvStrA > tkvStrA) shouldBe false
        (tkvStrA > TKVEntry.ofStrKey("A")) shouldBe false
        (TKVEntry.ofStrKey("A") > tkvStrA) shouldBe false
        (tkvStrA > tkvStrB) shouldBe false
        (tkvStrB > tkvStrA) shouldBe true
        (tkvStrA > TKVEntry.ofStrKey("B")) shouldBe false
        (TKVEntry.ofStrKey("B") > tkvStrA) shouldBe true
    }

    test("compare > intk strv") {
        (tkvIntA > tkvIntA) shouldBe false
        (tkvIntA > TKVEntry.ofIntKey("A")) shouldBe false
        (TKVEntry.ofIntKey("A") > tkvIntA) shouldBe false
        (tkvIntA > tkvIntB) shouldBe false
        (tkvIntB > tkvIntA) shouldBe true
        (tkvIntA > TKVEntry.ofIntKey("B")) shouldBe false
        (TKVEntry.ofIntKey("B") > tkvIntA) shouldBe true
    }

    test("comparator > intk intv ") {
        (tkvInt1cr > tkvInt1cr) shouldBe false
        (tkvInt1cr > TKVEntry.of(intKeyOf(2), 2, reverseIntCompare)) shouldBe true
        val ex1 = shouldThrow<IllegalStateException> {
            tkvInt2 > tkvInt1cr
        }
        ex1.message shouldBe TKVEntryK.CANNOT_COMPARE
        val ex2 = shouldThrow<IllegalStateException> {
            tkvInt1cr > tkvInt2
        }
        ex2.message shouldBe TKVEntryK.CANNOT_COMPARE
        (tkvInt1cr > TKVEntry.ofIntKey(1)) shouldBe false
        (tkvInt1cr > TKVEntry.of(intKeyOf(1), 1, intCompare)) shouldBe false
        (TKVEntry.ofIntKey(1) > tkvInt1cr) shouldBe false
        (TKVEntry.of(intKeyOf(1), 1, intCompare) > tkvInt1cr) shouldBe false
        (tkvInt1cr > tkvInt2cr) shouldBe true
        (tkvInt2cr > tkvInt1cr) shouldBe false
    }

    test("comparator > strk strvv") {
        (tkvStrAcr > tkvStrAcr) shouldBe false
        (tkvStrAcr > TKVEntry.of(strKeyOf("B"), "B", reverseStrCompare)) shouldBe true
        (tkvStrAcr > tkvStrBcr) shouldBe true
        (tkvStrBcr > tkvStrAcr) shouldBe false
    }

    test("comparator > intk strv ") {
        (tkvIntAcr > tkvIntAcr) shouldBe false
        (tkvIntAcr > TKVEntry.of(intKeyOf("B"), "B", reverseIntCompare)) shouldBe true
        val ex1 = shouldThrow<IllegalStateException> {
            tkvIntB > tkvIntAcr
        }
        ex1.message shouldBe TKVEntryK.CANNOT_COMPARE
        val ex2 = shouldThrow<IllegalStateException> {
            tkvIntAcr > tkvIntB
        }
        ex2.message shouldBe TKVEntryK.CANNOT_COMPARE
        (tkvIntAcr > TKVEntry.ofIntKey("A")) shouldBe false
        (tkvIntAcr > TKVEntry.of(intKeyOf("A"), "A", intCompare)) shouldBe false
        (TKVEntry.ofIntKey("A") > tkvIntAcr) shouldBe false
        (TKVEntry.of(intKeyOf("A"), "A", intCompare) > tkvIntAcr) shouldBe false
        (tkvIntAcr > tkvIntBcr) shouldBe true
        (tkvIntBcr > tkvIntAcr) shouldBe false
    }

    test("comparator < intk intv ") {
        (tkvInt1cr < tkvInt1cr) shouldBe false
        (tkvInt1cr < tkvInt2cr) shouldBe false
        (tkvInt2cr < tkvInt1cr) shouldBe true
    }

    test("comparator < strk strvv") {
        (tkvStrAcr < tkvStrAcr) shouldBe false
        (tkvStrAcr < tkvStrBcr) shouldBe false
        (tkvStrBcr < tkvStrAcr) shouldBe true
    }

    test("comparator < intk strv ") {
        (tkvIntAcr < tkvIntAcr) shouldBe false
        (tkvIntAcr < tkvIntBcr) shouldBe false
        (tkvIntBcr < tkvIntAcr) shouldBe true
    }

    test("fitKeyOnly") {

        tkvInt2.fitKeyOnly(1) shouldBe FBTFIT.LEFT
        tkvInt2.fitKeyOnly(2) shouldBe FBTFIT.EQ
        tkvInt2.fitKeyOnly(3) shouldBe FBTFIT.RIGHT

        tkvIntB.fitKeyOnly(intKeyOf("A")) shouldBe FBTFIT.LEFT
        tkvIntB.fitKeyOnly(intKeyOf("B")) shouldBe FBTFIT.EQ
        tkvIntB.fitKeyOnly(intKeyOf("C")) shouldBe FBTFIT.RIGHT

        tkvStr2.fitKeyOnly(strKeyOf(1)) shouldBe FBTFIT.LEFT
        tkvStr2.fitKeyOnly(strKeyOf(2)) shouldBe FBTFIT.EQ
        tkvStr2.fitKeyOnly(strKeyOf(3)) shouldBe FBTFIT.RIGHT

        tkvStrB.fitKeyOnly(strKeyOf("A")) shouldBe FBTFIT.LEFT
        tkvStrB.fitKeyOnly(strKeyOf("B")) shouldBe FBTFIT.EQ
        tkvStrB.fitKeyOnly(strKeyOf("C")) shouldBe FBTFIT.RIGHT

    }

    test("fitKeyOnly comparator") {

        tkvInt2cr.fitKeyOnly(1) shouldBe FBTFIT.RIGHT
        tkvInt2cr.fitKeyOnly(2) shouldBe FBTFIT.EQ
        tkvInt2cr.fitKeyOnly(3) shouldBe FBTFIT.LEFT

        tkvIntBcr.fitKeyOnly(intKeyOf("A")) shouldBe FBTFIT.RIGHT
        tkvIntBcr.fitKeyOnly(intKeyOf("B")) shouldBe FBTFIT.EQ
        tkvIntBcr.fitKeyOnly(intKeyOf("C")) shouldBe FBTFIT.LEFT

        tkvStr2cr.fitKeyOnly(strKeyOf(1)) shouldBe FBTFIT.RIGHT
        tkvStr2cr.fitKeyOnly(strKeyOf(2)) shouldBe FBTFIT.EQ
        tkvStr2cr.fitKeyOnly(strKeyOf(3)) shouldBe FBTFIT.LEFT

        tkvStrBcr.fitKeyOnly(strKeyOf("A")) shouldBe FBTFIT.RIGHT
        tkvStrBcr.fitKeyOnly(strKeyOf("B")) shouldBe FBTFIT.EQ
        tkvStrBcr.fitKeyOnly(strKeyOf("C")) shouldBe FBTFIT.LEFT

    }

})
