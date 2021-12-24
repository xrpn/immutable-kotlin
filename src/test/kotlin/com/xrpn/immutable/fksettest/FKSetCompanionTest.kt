package com.xrpn.immutable

import com.xrpn.bridge.FKSetIterator
import com.xrpn.imapi.*
import com.xrpn.immutable.FKSet.Companion.NOT_FOUND
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FKSet.Companion.emptyIMKISet
import com.xrpn.immutable.FKSet.Companion.ofi
import com.xrpn.immutable.FKSet.Companion.ofiMap
import com.xrpn.immutable.FKSet.Companion.ofk
import com.xrpn.immutable.FKSet.Companion.ofkMap
import com.xrpn.immutable.FKSet.Companion.ofs
import com.xrpn.immutable.FKSet.Companion.ofsMap
import com.xrpn.immutable.FKSet.Companion.toIMKSet
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.TKVEntry.Companion.toKKEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.fiset

private val intKKSetOfNone: FKSet<Int, Int> = FKSet.ofk(*emptyArrayOfInt)
private val intKKSetOfOne = FKSet.ofk(1)
private val intKKSetOfTwo = FKSet.ofk(1, 2)
private val intKKSetOfTwoOfst1 =  /* deliberate ofi */ FKSet.ofi(2, 3)
private val intKKSetOfThree: FKSet<Int, Int> = /* deliberate ofi */ FKSet.ofi(1, 2, 3)

private val strKKSetOfNone: FKSet<String, String> = FKSet.ofk(*emptyArrayOfStr)
private val strKKSetOfOne = FKSet.ofs("1")
private val strKKSetOfTwo = FKSet.ofk("1", "2")
private val strKKSetOfTwoOfst1 =  /* deliberate ofs */ FKSet.ofi("2", "3")
private val strKKSetOfThree: FKSet<String, String> = /* deliberate ofi */ FKSet.ofs("1", "2", "3")

private val longKKSetOfNone: FKSet<Long, Long> = FKSet.ofk(*emptyArrayOfLong)
private val longKKSetOfOne = FKSet.ofk(1L)
private val longKKSetOfTwo = FKSet.ofk(1L, 2L)
private val longKKSetOfTwoOfst1 =  /* deliberate ofi */ FKSet.ofk(2L, 3L)
private val longKKSetOfThree: FKSet<Long, Long> = FKSet.ofk(1L, 2L, 3L)

private val intSSetOfNone: FKSet<String, Int> = FKSet.ofs(*arrayOf())
private val intSSetOfOne = FKSet.ofs(1)
private val intSSetOfTwo = FKSet.ofs(1, 2)
private val intSSetOfTwoOfst1 = FKSet.ofs(2, 3)
private val intSSetOfThree: FKSet<String, Int> = FKSet.ofs(1, 2, 3)

private val strISetOfNone: FKSet<Int, String> = FKSet.ofi(*arrayOf())
private val strISetOfOne = FKSet.ofi("1")
private val strISetOfTwo = FKSet.ofi("1", "2")
private val strISetOfTwoOfst1 = FKSet.ofi("2", "3")
private val strISetOfThree = FKSet.ofi("1", "2", "3")

private val knSetOfNone: Set<Int> = setOf(*arrayOf())
private val ksSetOfNone: Set<String> = setOf(*arrayOf())
private val kiSetOfOne = setOf(1)
private val ksSetOfOne = setOf("1")
private val kiSetOfTwo = setOf(1, 2)
private val ksSetOfTwo = setOf("1", "2")
private val kiSetOfTwoOfst1 = setOf(2, 3)
private val ksSetOfTwoOfst1 = setOf("2", "3")
private val kiSetOfThree = setOf(1, 2, 3)
private val ksSetOfThree = setOf("1", "2", "3")

private val longISetOfThree: IMRSetNotEmpty<Long> = strISetOfThree.fmap { it.toLong() }.ner()!!

inline fun <reified R, reified S> reifiedGenericIsSame(r: R, s: S): Boolean = (r is S) && (s is R)
interface FooBar<out A: Any, out B: Any>
typealias FBOI<A> = FooBar<Int,A>
typealias FBOS<A> = FooBar<String,A>

class FKSetCompanionTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("reified types 1") {
        open class Base<out A: Any, out B: Any>(val a: A, val b: B): Set<B>, FooBar<A, B> {
            override val size: Int = 1
            override fun contains(element: @UnsafeVariance B): Boolean = element == b
            override fun containsAll(elements: Collection<@UnsafeVariance B>): Boolean = elements.size == 1 && elements.first() == b
            override fun isEmpty(): Boolean = false
            override fun iterator(): Iterator<B> = object : Iterator<B> {
                override fun hasNext(): Boolean = true
                override fun next(): B = b
            }
        }

        class SpecialOfInt<out B: Any>(b: B) : Base<Int, B>(1, b)
        class SpecialOfStr<out B: Any>(b: B) : Base<String, B>("1", b)

        val soitd: FBOI<Int> = SpecialOfInt(2)
        val sostd: FBOS<Int> = SpecialOfStr(2)
        val soi = SpecialOfInt(2)
        val sos = SpecialOfStr(2)

        reifiedGenericIsSame(soi, sos) shouldBe false
        reifiedGenericIsSame(soitd, sostd) shouldBe true

    }

    test("reified types 2") {
        open class Base<out A: Any, out B: Any>(val a: A, val b: B): FooBar<A, B>

        class SpecialOfInt<out B: Any>(b: B) : Base<Int, B>(1, b)
        class SpecialOfStr<out B: Any>(b: B) : Base<String, B>("1", b)

        val soi = SpecialOfInt(2)
        val sos = SpecialOfStr(2)
        val soitd: FBOI<Int> = soi
        val sostd: FBOS<Int> = sos

        reifiedGenericIsSame(soi, sos) shouldBe false
        // reifiedGenericIsSame(soi, sos) shouldBe true
        // reifiedGenericIsSame(soitd, sostd) shouldBe false
        reifiedGenericIsSame(soitd, sostd) shouldBe true

    }

    test("equals KSet") {
        intKKSetOfNone.softEqual(knSetOfNone) shouldBe true
        (intKKSetOfNone === knSetOfNone) shouldBe false
        intKKSetOfNone.softEqual(kiSetOfOne) shouldBe false
        intKKSetOfOne.softEqual(knSetOfNone) shouldBe false
        intKKSetOfOne.softEqual(kiSetOfOne) shouldBe true
        intKKSetOfOne.softEqual(kiSetOfTwo) shouldBe false
        intKKSetOfTwo.softEqual(kiSetOfOne) shouldBe false
        intKKSetOfTwo.softEqual(kiSetOfTwo) shouldBe true
        intKKSetOfTwo.softEqual(kiSetOfTwoOfst1) shouldBe false
        intKKSetOfTwoOfst1.softEqual(kiSetOfTwo) shouldBe false
        intKKSetOfTwo.softEqual(kiSetOfThree) shouldBe false
        intKKSetOfTwoOfst1.softEqual(kiSetOfThree) shouldBe false
        intKKSetOfThree.softEqual(kiSetOfTwo) shouldBe false
        intKKSetOfThree.softEqual(kiSetOfTwoOfst1) shouldBe false
        intKKSetOfThree.softEqual(kiSetOfThree) shouldBe true

        knSetOfNone.equals(intKKSetOfNone) shouldBe false
        knSetOfNone.equals(intKKSetOfNone.asSet()) shouldBe true
        knSetOfNone.equals(intKKSetOfOne) shouldBe false
        kiSetOfOne.equals(intKKSetOfNone) shouldBe false
        kiSetOfOne.equals(intKKSetOfOne.asSet()) shouldBe true
        kiSetOfOne.equals(intKKSetOfOne) shouldBe false
        kiSetOfOne.equals(intKKSetOfTwo.asSet()) shouldBe false
        kiSetOfTwo.equals(intKKSetOfOne) shouldBe false
        kiSetOfTwo.equals(intKKSetOfTwo.asSet()) shouldBe true
        kiSetOfTwo.equals(intKKSetOfTwo) shouldBe false
        kiSetOfTwo.equals(intKKSetOfTwoOfst1.asSet()) shouldBe false
        knSetOfNone.equals(strISetOfNone.asSet()) shouldBe true
        ksSetOfNone.equals(intKKSetOfNone.asSet()) shouldBe true

        // prefer "equal" (vs. equals)
        intKKSetOfNone.equals(strISetOfNone) shouldBe true
        strISetOfNone.equals(intKKSetOfNone) shouldBe true
    }

    test("equals iSSet") {
        intSSetOfNone.softEqual(knSetOfNone) shouldBe true
        (intSSetOfNone === knSetOfNone) shouldBe false
        intSSetOfNone.softEqual(kiSetOfOne) shouldBe false
        intSSetOfOne.softEqual(knSetOfNone) shouldBe false
        intSSetOfOne.softEqual(kiSetOfOne) shouldBe true
        intSSetOfOne.softEqual(kiSetOfTwo) shouldBe false
        intSSetOfTwo.softEqual(kiSetOfOne) shouldBe false
        intSSetOfTwo.softEqual(kiSetOfTwo) shouldBe true
        intSSetOfTwo.softEqual(kiSetOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.softEqual(kiSetOfTwo) shouldBe false
        intSSetOfTwo.softEqual(kiSetOfThree) shouldBe false
        intSSetOfTwoOfst1.softEqual(kiSetOfThree) shouldBe false
        intSSetOfThree.softEqual(kiSetOfTwo) shouldBe false
        intSSetOfThree.softEqual(kiSetOfTwoOfst1) shouldBe false
        intSSetOfThree.softEqual(kiSetOfThree) shouldBe true

        knSetOfNone.equals(intSSetOfNone) shouldBe false
        knSetOfNone.equals(intSSetOfOne.asSet()) shouldBe false
        kiSetOfOne.equals(intSSetOfNone.asSet()) shouldBe false
        kiSetOfOne.equals(intSSetOfOne) shouldBe false
        kiSetOfOne.equals(intSSetOfOne.asSet()) shouldBe true
        kiSetOfOne.equals(intSSetOfTwo.asSet()) shouldBe false

        knSetOfNone.equals(strKKSetOfNone) shouldBe false
        knSetOfNone.equals(strKKSetOfNone.asSet()) shouldBe true
        ksSetOfNone.equals(intSSetOfNone) shouldBe false

        intSSetOfNone.equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.equals(intSSetOfNone) shouldBe true
    }

    test("equals sISet") {
        strISetOfNone.softEqual(knSetOfNone) shouldBe true
        (strISetOfNone === knSetOfNone) shouldBe false
        strISetOfNone.softEqual(ksSetOfOne) shouldBe false
        strISetOfOne.softEqual(knSetOfNone) shouldBe false
        strISetOfOne.softEqual(ksSetOfOne) shouldBe true
        strISetOfOne.softEqual(ksSetOfTwo) shouldBe false
        strISetOfTwo.softEqual(ksSetOfOne) shouldBe false
        strISetOfTwo.softEqual(ksSetOfTwo) shouldBe true
        strISetOfTwo.softEqual(ksSetOfTwoOfst1) shouldBe false
        strISetOfTwoOfst1.softEqual(ksSetOfTwo) shouldBe false
        strISetOfTwo.softEqual(ksSetOfThree) shouldBe false
        strISetOfTwoOfst1.softEqual(ksSetOfThree) shouldBe false
        strISetOfThree.softEqual(ksSetOfTwo) shouldBe false
        strISetOfThree.softEqual(ksSetOfTwoOfst1) shouldBe false
        strISetOfThree.softEqual(ksSetOfThree) shouldBe true

        knSetOfNone.equals(strISetOfNone) shouldBe false
        knSetOfNone.equals(strISetOfNone.asSet()) shouldBe true
        ksSetOfTwo.equals(strISetOfTwo) shouldBe false
        ksSetOfTwo.equals(strISetOfTwo.asSet()) shouldBe true
        val foo = strISetOfTwo.asSet()
            foo.equals(ksSetOfTwo) shouldBe true

        knSetOfNone.equals(strKKSetOfNone.asSet()) shouldBe true
        ksSetOfNone.equals(strISetOfNone.asSet()) shouldBe true

        strISetOfNone.equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.equals(strISetOfNone) shouldBe true
    }

    test("equals iKSet iSSet") {
        intKKSetOfNone.equals(intSSetOfNone) shouldBe true
        (intKKSetOfNone === intSSetOfNone) shouldBe false
        intKKSetOfNone.equals(intSSetOfOne) shouldBe false
        intKKSetOfOne.equals(intSSetOfNone) shouldBe false
        intKKSetOfOne.equals(intSSetOfOne) shouldBe true
        intKKSetOfOne.equals(intSSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(intSSetOfOne) shouldBe false
        intKKSetOfTwo.equals(intSSetOfTwo) shouldBe true
        intKKSetOfTwo.equals(intSSetOfTwoOfst1) shouldBe false
        intKKSetOfTwoOfst1.equals(intSSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(intSSetOfThree) shouldBe false
        intKKSetOfTwoOfst1.equals(intSSetOfThree) shouldBe false
        intKKSetOfThree.equals(intSSetOfTwo) shouldBe false
        intKKSetOfThree.equals(intSSetOfTwoOfst1) shouldBe false
        intKKSetOfThree.equals(intSSetOfThree) shouldBe true

        intSSetOfNone.equals(intKKSetOfNone) shouldBe true
        (intSSetOfNone === intKKSetOfNone) shouldBe false
        intSSetOfNone.equals(intKKSetOfOne) shouldBe false
        intSSetOfOne.equals(intKKSetOfNone) shouldBe false
        intSSetOfOne.equals(intKKSetOfOne) shouldBe true
        intSSetOfOne.equals(intKKSetOfTwo) shouldBe false
        intSSetOfTwo.equals(intKKSetOfOne) shouldBe false
        intSSetOfTwo.equals(intKKSetOfTwo) shouldBe true
        intSSetOfTwo.equals(intKKSetOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.equals(intKKSetOfTwo) shouldBe false
        intSSetOfTwo.equals(intKKSetOfThree) shouldBe false
        intSSetOfTwoOfst1.equals(intKKSetOfThree) shouldBe false
        intSSetOfThree.equals(intKKSetOfTwo) shouldBe false
        intSSetOfThree.equals(intKKSetOfTwoOfst1) shouldBe false
        intSSetOfThree.equals(intKKSetOfThree) shouldBe true

        intSSetOfNone.equals(strISetOfNone) shouldBe true
        ksSetOfNone.equals(intKKSetOfNone) shouldBe false
    }

    test("equals sKSet sISet") {
        strKKSetOfNone.equals(strISetOfNone) shouldBe true
        (strKKSetOfNone === strISetOfNone) shouldBe false
        strKKSetOfNone.equals(strISetOfOne) shouldBe false
        strKKSetOfOne.equals(strISetOfNone) shouldBe false
        strKKSetOfOne.equals(strISetOfOne) shouldBe true
        strKKSetOfOne.equals(strISetOfTwo) shouldBe false
        strKKSetOfTwo.equals(strISetOfOne) shouldBe false
        strKKSetOfTwo.equals(strISetOfTwo) shouldBe true
        strKKSetOfTwo.equals(strISetOfTwoOfst1) shouldBe false
        strKKSetOfTwoOfst1.equals(strISetOfTwo) shouldBe false
        strKKSetOfTwo.equals(strISetOfThree) shouldBe false
        strKKSetOfTwoOfst1.equals(strISetOfThree) shouldBe false
        strKKSetOfThree.equals(strISetOfTwo) shouldBe false
        strKKSetOfThree.equals(strISetOfTwoOfst1) shouldBe false
        strKKSetOfThree.equals(strISetOfThree) shouldBe true

        strISetOfNone.equals(strKKSetOfNone) shouldBe true
        (strISetOfNone === strKKSetOfNone) shouldBe false
        strISetOfNone.equals(strKKSetOfOne) shouldBe false
        strISetOfOne.equals(strKKSetOfNone) shouldBe false
        strISetOfOne.equals(strKKSetOfOne) shouldBe true
        strISetOfOne.equals(strKKSetOfTwo) shouldBe false
        strISetOfTwo.equals(strKKSetOfOne) shouldBe false
        strISetOfTwo.equals(strKKSetOfTwo) shouldBe true
        strISetOfTwo.equals(strKKSetOfTwoOfst1) shouldBe false
        strISetOfTwoOfst1.equals(strKKSetOfTwo) shouldBe false
        strISetOfTwo.equals(strKKSetOfThree) shouldBe false
        strISetOfTwoOfst1.equals(strKKSetOfThree) shouldBe false
        strISetOfThree.equals(strKKSetOfTwo) shouldBe false
        strISetOfThree.equals(strKKSetOfTwoOfst1) shouldBe false
        strISetOfThree.equals(strKKSetOfThree) shouldBe true

        strISetOfNone.equals(strISetOfNone) shouldBe true
        ksSetOfNone.equals(strKKSetOfNone) shouldBe false
    }

    test("equals KKSet ISet") {
        intKKSetOfNone.equals(null) shouldBe false

        intKKSetOfOne.equals(strISetOfOne) shouldBe false
        strISetOfOne.equals(intKKSetOfOne) shouldBe false
        intKKSetOfTwo.equals(strISetOfOne) shouldBe false
        strISetOfOne.equals(intKKSetOfTwo) shouldBe false

        kiSetOfOne.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(intKKSetOfOne) shouldBe false
        kiSetOfTwo.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(intKKSetOfTwo) shouldBe false

        strISetOfOne.equals(kiSetOfOne) shouldBe false
        intKKSetOfOne.equals(ksSetOfOne) shouldBe false
        strISetOfOne.equals(kiSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(ksSetOfOne) shouldBe false

        intKKSetOfOne.equals(1) shouldBe false
        strISetOfOne.equals("a") shouldBe false
    }

    test("equals KKSet SSet") {
        intSSetOfNone.equals(null) shouldBe false

        intSSetOfOne.equals(strKKSetOfOne) shouldBe false
        strKKSetOfOne.equals(intSSetOfOne) shouldBe false
        intSSetOfTwo.equals(strKKSetOfOne) shouldBe false
        strKKSetOfOne.equals(intSSetOfTwo) shouldBe false

        kiSetOfOne.equals(strKKSetOfOne) shouldBe false
        ksSetOfOne.equals(intSSetOfOne) shouldBe false
        kiSetOfTwo.equals(strKKSetOfOne) shouldBe false
        ksSetOfOne.equals(intSSetOfTwo) shouldBe false

        strKKSetOfOne.equals(kiSetOfOne) shouldBe false
        intSSetOfOne.equals(ksSetOfOne) shouldBe false
        strKKSetOfOne.equals(kiSetOfTwo) shouldBe false
        intSSetOfTwo.equals(ksSetOfOne) shouldBe false

        intSSetOfOne.equals(1) shouldBe false
        strKKSetOfOne.equals("a") shouldBe false
    }

    test("toString() hashCode()") {
        emptyIMKSet<Int, Int>(IntKeyType).toString() shouldBe "FKSet(*)"
    }

    test("toString() hashCode() ISet") {

        val aux = emptyIMKSet<Int, Int>(IntKeyType).hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMKSet<Int, Int>(IntKeyType).hashCode()
        }
        strISetOfThree.toString() shouldStartWith "FIKSet("
        val aux2 = strISetOfThree.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe strISetOfThree.hashCode()
        }
        for (i in (1..100)) {
            FKSet.hashCode(strISetOfThree) shouldBe strISetOfThree.hashCode()
        }
    }

    test("toString() hashCode() SSet") {

        val aux = emptyIMKSet<String, Int>(StrKeyType).hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMKSet<String, Int>(StrKeyType).hashCode()
        }
        intSSetOfTwo.toString() shouldStartWith "FSKSet("
        val aux2 = intSSetOfTwo.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe intSSetOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FKSet.hashCode(intSSetOfTwo) shouldBe intSSetOfTwo.hashCode()
        }
    }

    test("toString() hashCode() KSet") {

        val aux = emptyIMKSet<Int, Int>(IntKeyType).hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMKSet<Int, Int>(IntKeyType).hashCode()
        }
        intKKSetOfTwo.toString() shouldStartWith "FKKSet("
        val aux2 = intKKSetOfTwo.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe intKKSetOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FKSet.hashCode(intKKSetOfTwo) shouldBe intKKSetOfTwo.hashCode()
        }
    }

    test("toString() hashCode() ISet SSet") {
        (emptyIMKSet<Int, Int>(IntKeyType).hashCode() == emptyIMKSet<String, Int>(StrKeyType).hashCode()) shouldBe true
        (intSSetOfTwo.hashCode() != intKKSetOfTwo.hashCode()) shouldBe true
    }

    // IMKSetCompanion

    test("co.emptyIMKSet"){
        (emptyIMKSet<Int, Int>(IntKeyType) === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        (emptyIMKSet<String, Int>(StrKeyType) === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        emptyIMKSet<String, Int>(StrKeyType).equals(emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (emptyIMKSet<Int, Int>(IntKeyType) as IMKKSetNotEmpty<Int>)
        }
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (emptyIMKSet<String, Int>(StrKeyType) as IMKASetNotEmpty<String, Int>)
        }
    }

    // IMSet TODO needs more for delta btw ISet and SSet and KSet

    test("co.ofi vararg"){
        ofi(*arrayOf()) shouldBe emptyIMKISet()
        (ofi(*arrayOf()) === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
    }

    test("co.ofi Iterator"){
        ofi(emptyArrayOfInt.iterator()) shouldBe emptyIMKISet()
        (ofi(emptyArrayOfInt.iterator()) === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofi(arrayOf(1, 2, 3).iterator()) as IMKKSetNotEmpty<Int>)).equal(intKKSetOfThree) shouldBe true
    }

    test("co.ofi FBSTree<K, A>"){
        ofi(FRBTree.nul<Int, Int>()) shouldBe emptyIMKISet()
        (ofi(FRBTree.nul<Int, Int>()) === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        ofi(FBSTree.nul<Int, Int>()) shouldBe emptyIMKISet()
        (ofi(FBSTree.nul<Int, Int>()) === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        ofi(FBSTree.nul<Int, Int>(true)) shouldBe emptyIMKISet()
        (ofi(FBSTree.nul<Int, Int>(true)) === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofi(FRBTree.ofvi(1, 2, 3)) as IMKKSetNotEmpty<Int>)) shouldBe intKKSetOfThree
        (@Suppress("UNCHECKED_CAST") (ofi(FBSTree.ofvi(1, 2, 3)) as IMKKSetNotEmpty<Int>)) shouldBe intKKSetOfThree
        ofi(FBSTree.ofvi(1, 2, 3, allowDups = true)) shouldBe null
    }

    test("co.ofi IMList"){
        ofi(emptyIMList()) shouldBe emptyIMKISet()
        (ofi(emptyIMList()) === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        ofi(FLCons(2, FLCons(3, FLCons(1, FLNil)))).equals(intKKSetOfThree) shouldBe true
        ofi(FLCons("2", FLCons("3", FLCons("1", FLNil)))).equals(strISetOfThree) shouldBe true
    }

    test("co.ofiMap Iterator"){
        ofiMap(emptyArrayOfInt.iterator()){ it.toString() } shouldBe emptyIMKISet()
        (ofiMap(emptyArrayOfInt.iterator()){ it.toString() } === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofiMap(arrayOf(1, 2, 3).iterator()){ it.toString() } as IMKASetNotEmpty<Int, Int>)) shouldBe strISetOfThree
        ofiMap(arrayOf(1, 2, 3).iterator()){ it }.equals(intKKSetOfThree) shouldBe true
    }

    test("co.ofiMap IMList"){
        ofiMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } shouldBe emptyIMKISet()
        (ofiMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } === emptyIMKSet<Int, Int>(IntKeyType)) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofiMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toString() } as IMKASetNotEmpty<Int, Int>)) shouldBe strISetOfThree
        ofiMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it }.equals(intKKSetOfThree) shouldBe true
    }

    // IMSet TODO needs more for delta btw ISet and SSet and KSet

    test("co.ofs vararg"){
        ofs(*arrayOf()) shouldBe emptyIMKISet()
        (ofs(*arrayOf()) === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
    }

    test("co.ofs Iterator"){
        ofs(emptyArrayOfInt.iterator()) shouldBe emptyIMKISet()
        (ofs(emptyArrayOfInt.iterator()) === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        val aut: IMKSet<String, Int> = ofs(arrayOf(1, 2, 3).iterator())
        aut.equal(intSSetOfThree) shouldBe true
        aut.equals(intKKSetOfThree) shouldBe true
        aut.isStrictly(intKKSetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (aut as IMKASetNotEmpty<Int, Int>)).equal(intKKSetOfThree) shouldBe true
        intKKSetOfThree.equal(@Suppress("UNCHECKED_CAST") (aut as IMKASetNotEmpty<Int, Int>)) shouldBe true
    }

    test("co.ofs FRBTree<K, A>") {
        ofs(FRBTree.nul<String, Int>()) shouldBe emptyIMKISet()
        (ofs(FRBTree.nul<String, Int>()) === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        val autfrb: IMKSet<String, Int> = ofs(FRBTree.ofvs(1, 2, 3))!!
        autfrb.equal(intSSetOfThree) shouldBe true
        autfrb.equals(intKKSetOfThree) shouldBe true
        autfrb.isStrictly(intKKSetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (autfrb as IMKASetNotEmpty<Int, Int>)).equal(intKKSetOfThree) shouldBe true
        intKKSetOfThree.equal(@Suppress("UNCHECKED_CAST") (autfrb as IMKASetNotEmpty<Int, Int>)) shouldBe true
    }

    test("co.ofs FBSTree<K, A>"){
        ofs(FBSTree.nul<String, Int>()) shouldBe emptyIMKISet()
        (ofs(FBSTree.nul<String, Int>()) === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        (ofs(FBSTree.nul<String, Int>(true)) === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        ofs(FBSTree.ofvs(1, 2, 3, allowDups = true)) shouldBe null
        val autfbs: FKSet<String, Int> = ofs(FBSTree.ofvs(1, 2, 3))!!
        autfbs.equal(intSSetOfThree) shouldBe true
        autfbs.equals(intKKSetOfThree) shouldBe true
        autfbs.isStrictly(intKKSetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (autfbs as IMKASetNotEmpty<Int, Int>)).equal(intKKSetOfThree) shouldBe true
        intKKSetOfThree.equal(@Suppress("UNCHECKED_CAST") (autfbs as IMKASetNotEmpty<Int, Int>)) shouldBe true
    }

    test("co.ofs IMList"){
        ofs(emptyIMList()) shouldBe emptyIMKISet()
        (ofs(emptyIMList()) === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        val auti = ofs(FLCons(2, FLCons(3, FLCons(1, FLNil))))
        auti.equal(intSSetOfThree) shouldBe true
        auti.equals(intKKSetOfThree) shouldBe true
        auti.isStrictly(intKKSetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (auti as IMKASetNotEmpty<Int, Int>)).equals(intKKSetOfThree) shouldBe true
        intKKSetOfThree.equals(@Suppress("UNCHECKED_CAST") (auti as IMKASetNotEmpty<Int, Int>)) shouldBe true
        val auts = ofs(FLCons("1", FLCons("2", FLCons("3", FLNil))))
        auts.equal(strKKSetOfThree) shouldBe true
        auts.equals(strISetOfThree) shouldBe true
        auts shouldBe strKKSetOfThree
    }

    test("co.ofsMap Iterator"){
        ofsMap(emptyArrayOfInt.iterator()){ it.toString() } shouldBe emptyIMKISet()
        (ofsMap(emptyArrayOfInt.iterator()){ it.toString() } === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        val auts = ofsMap(arrayOf(1, 2, 3).iterator()){ it.toString() }
        strISetOfThree.equal((@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>))) shouldBe true
        (@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>)).equal(strISetOfThree) shouldBe true
        val autl: FKSet<String, Long> = ofsMap(arrayOf(1, 2, 3).iterator()){ it.toLong() }
        autl.equal(longISetOfThree.asIMSet()) shouldBe true
    }

    test("co.ofsMap IMList"){
        ofsMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } shouldBe emptyIMKISet()
        (ofsMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        val auts = ofsMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toString() }
        strISetOfThree.equal((@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>))) shouldBe true
        (@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>)).equal(strISetOfThree) shouldBe true
        val autl: FKSet<String, Long> = ofsMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toLong() }
        autl.equal(longISetOfThree.asIMSet()) shouldBe true
    }

    // IMSet TODO needs more for delta btw ISet and SSet and KSet

    test("co.ofk vararg"){
        (ofk(*emptyArrayOfLong) === emptyIMKSet<Long, Long>(SymKeyType(Long::class))) shouldBe true
    }

    test("co.ofk Iterator"){
        ofk(emptyArrayOfLong.iterator()) shouldBe emptyIMKISet()
        (ofk(emptyArrayOfLong.iterator()) === emptyIMKSet<Long, Long>(SymKeyType(Long::class))) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofk(arrayOf(1L, 2L, 3L).iterator()) as IMKKSetNotEmpty<Long>)).equal(longKKSetOfThree) shouldBe true
    }

    test("co.ofk IMBTree<K, A>"){
        ofk(FRBTree.nul<Long, Long>()) shouldBe emptyIMKSet(SymKeyType(Long::class))
        (ofk(FRBTree.nul<Long, Long>()) === emptyIMKSet<Long, Long>(SymKeyType(Long::class))) shouldBe true
        ofk(FBSTree.nul<Long, Long>()) shouldBe emptyIMKISet()
        (@Suppress("UNCHECKED_CAST") (ofk(FRBTree.of(1L.toKKEntry() , 2L.toKKEntry(), 3L.toKKEntry())) as IMKKSetNotEmpty<Long>)) shouldBe longKKSetOfThree
        (@Suppress("UNCHECKED_CAST") (ofk(FBSTree.of(1L.toKKEntry(), 2L.toKKEntry(), 3L.toKKEntry())) as IMKKSetNotEmpty<Long>)) shouldBe longKKSetOfThree
    }

    test("co.ofk IMList"){
        ofk(emptyIMList<Long>()) shouldBe emptyIMKISet()
        (ofk(emptyIMList<Long>()) === emptyIMKSet<Long, Long>(SymKeyType(Long::class))) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofk(FLCons(2L, FLCons(3L, FLCons(1L, FLNil)))) as IMKKSetNotEmpty<Long>)) shouldBe longKKSetOfThree
    }

    test("co.ofkMap Iterator"){
        ofkMap(emptyArrayOfLong.iterator()){ it.toString() } shouldBe emptyIMKISet()
        (ofkMap(emptyArrayOfLong.iterator()){ it.toString() } === emptyIMKSet<Long, Long>(SymKeyType(Long::class))) shouldBe true
        ofkMap(arrayOf(1L, 2L, 3L).iterator()){ it.toString() }.equals(strKKSetOfThree) shouldBe true
    }

    test("co.ofkMap IMList"){
        ofkMap(emptyIMList<Long>() as IMList<Long>){ it.toString() } shouldBe emptyIMKISet()
        (ofkMap(emptyIMList<Long>() as IMList<Long>){ it.toString() } === emptyIMKSet<Long, Long>(SymKeyType(Long::class))) shouldBe true
        ofkMap(FLCons(2L, FLCons(3L, FLCons(1L, FLNil))) as IMList<Long>){ it.toString() }.equals(strKKSetOfThree) shouldBe true
    }

    test("co.toIMKSet() IK"){
        Arb.list(Arb.int()).checkAll(repeats) { kl ->
            val ks = kl.toSet()
            val fs1 = kl.toIMKSet(IntKeyType)
            val fs2 = ks.toIMKSet(IntKeyType)
            fs1!!.equals(ks) shouldBe false
            ks.equals(fs2!!) shouldBe false
            fs1.softEqual(ks) shouldBe true
            ks.equals(fs2.asSet()) shouldBe true
        }
    }

    test("co.toIMKSet() SK"){
        Arb.list(Arb.int()).checkAll(repeats) { kl ->
            val ks = kl.toSet()
            val fs1 = kl.toIMKSet(StrKeyType)
            val fs2 = ks.toIMKSet(StrKeyType)
            fs1!!.equals(ks) shouldBe false
            ks.equals(fs2!!) shouldBe false
            fs1.softEqual(ks) shouldBe true
            ks.equals(fs2.asSet()) shouldBe true
        }
    }

    // implementation

    test("co.NOT_FOUND"){
        NOT_FOUND shouldBe -1
    }

    test("co.toArray"){
        Arb.fiset<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
            val ary: Array<Int> = FKSet.toArray(fs)
            fs.equal(FKSet.ofi(ary.iterator())) shouldBe true
            fs.equal(FKSet.ofi(*ary)) shouldBe true
        }
    }

})