package com.xrpn.immutable

import com.xrpn.bridge.FStackIterator
import com.xrpn.imapi.*
import com.xrpn.imapi.IMStackEqual2
import com.xrpn.immutable.FList.Companion.toIMList

sealed class FStack<out A: Any>: IMStack<A> {

    val size: Int by lazy { toFList().size }

    fun isEmpty(): Boolean = toFList().isEmpty()

    fun iterator(): FStackIterator<A> = FStackIterator(this)

    // imcommon

    override val seal: IMSC = IMSC.IMSTACK

    override fun fcontains(item: @UnsafeVariance A): Boolean=
        toFList().fcontains(item)


    override fun fcount(isMatch: (A) -> Boolean): Int =
        toFList().fcount(isMatch)

    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): FStack<A> =
        if (items.fempty()) this else FStackBody.of(toFList().fdropAll(items))

    override fun fdropItem(item: @UnsafeVariance A): IMStack<A> =
        FStackBody.of(toFList().fdropItem(item))

    override fun ffilter(isMatch: (A) -> Boolean): FStack<A> {
        val filtered = toFList().ffilter(isMatch)
        return if (filtered === toFList()) this else FStackBody.of(filtered)
    }

    override fun ffilterNot(isMatch: (A) -> Boolean): FStack<A> =
        ffilter { !isMatch(it) }

    override fun ffindAny(isMatch: (A) -> Boolean): A? =
        toFList().ffindAny(isMatch)

    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R =
        TODO("Not yet implemented")

    override fun fisStrict(): Boolean =
        toFList().fisStrict()

    override fun fpick(): A? =
        toFList().fhead()

    override fun fpopAndRemainder(): Pair<A?, FStack<A>> = fpop()

    override fun fsize(): Int =
        toFList().size

    override fun toEmpty(): IMStack<A> = FStackBody.empty

    // ============ IMOrdered

    override fun fdrop(n: Int): FStack<A> = when {
        0 < n -> FStackBody.of(toFList().fdrop(n))
        n <= 0 -> this
        else -> throw RuntimeException("internal error")
    }

    override fun fnext(): Pair<A?, IMStack<A>> =
        fpop()

    override fun freverse(): FStack<A> =
        FStackBody.of(toFList().freverse())

    override fun frotl(): IMStack<A> =
        FStackBody.of(toFList().frotl())

    override fun frotr(): IMStack<A> =
        FStackBody.of(toFList().frotr())

    override fun fswaph(): IMStack<A> =
        FStackBody.of(toFList().fswaph())

    override fun <B : Any> fzip(items: IMOrdered<B>): IMStack<Pair<A, B>> {
        val (itn, _) = items.fnext()
        return itn?.let {
            if (fempty()) FStackBody.empty
            else FStackBody.of(toFList().fzip(items))
        } ?: FStackBody.empty
    }

    // ============ IMMappable

    override fun <B: Any> fmap(f: (A) -> B): IMStack<B> =
        FStackBody.of(toFList().fmap(f))

    // ============ IMMapplicable

    override fun <T : Any> fapp(op: (IMStack<A>) -> ITMap<T>): ITMapp<T> =
        IMMappOp.flift2mapp(op(this))!!

    // ============ filtering

    override fun fdropIfTop(item: @UnsafeVariance A): FStack<A> = ftop()?.let {
        if (it.equals(item)) fpopOrThrow().second else this
    } ?: this

    override fun fdropTopWhen(isMatch: (A) -> Boolean): FStack<A> =
        if (ftopMatch(isMatch)) FStackBody.of(toFList().ftail()) else this

    override fun fdropWhile(isMatch: (A) -> Boolean): FStack<A> =
        FStackBody.of(toFList().fdropWhile(isMatch))

    override fun ftopMatch(isMatch: (A) -> Boolean): Boolean =
        ftop()?.let { isMatch(it) } ?: false

    override fun ftop(): A? = toFList().fhead()

    override fun ftopOrThrow(): A = ftop() ?: throw IllegalStateException("top of empty stack")

    // ============ grouping - NOP

    // ============ transforming

    override fun <B : Any> fpopMap(f: (A) -> B): Pair<B?, IMStack<A>> =
        fpop().let { pit ->
            pit.first?.let {
                pit.pmap2({ a -> f(a!!) }, { it })
            } ?: Pair(null, pit.second)
        }

    override fun <B : Any> ftopMap(f: (A) -> B): B? =
        ftop()?.let{ f(it) }

    // ============ altering

    override fun fpop(): Pair<A?, FStack<A>> =
        ftop()?.let { buildPair() } ?: Pair(null, this)

    override fun fpopOrThrow(): Pair<A, FStack<A>> =
        ftop()?.let { buildPair() } ?: throw IllegalStateException("pop from empty stack")

    override fun fpush(top: @UnsafeVariance A): FStack<A> =
        FStackBody.of(FLCons(top, toFList()))

    // ============ utility

    override fun equal(rhs: IMStack<@UnsafeVariance A>): Boolean =
        toFList().equal(rhs.toIMList())

    override fun fforEach(f: (A) -> Unit) =
        toFList().fforEach(f)

    override fun copy(): FStack<A> =
        FStackBody.of(toFList().copy())

    override fun copyToMutableList(): MutableList<@UnsafeVariance A> =
        toFList().copyToMutableList()

    override fun toIMList(): IMList<A> =
        toFList()

    // ============ implementation

    // the head of the list is the first item out (i.e. the top of the stack)
    internal fun toFList(): FList<A> = (this as FStackBody).body

    private fun buildPair(): Pair<A, FStack<A>> {
        this as FStackBody
        return Pair(body.fhead()!!, FStackBody.of(body.ftail()))
    }

    companion object: IMStackCompanion {

        override fun <A: Any> emptyIMStack(): FStack<A> = FStackBody.empty

        override fun <A : Any> of(vararg items: A): FStack<A> =
            FStackBody.of(FList.of(items.iterator()))

        override fun <A : Any> of(items: Iterator<A>): FStack<A> =
            FStackBody.of(FList.of(items))

        override fun <A : Any> of(items: List<A>): FStack<A> =
            FStackBody.of(FList.of(items))

        override fun <A : Any> of(items: IMList<A>): FStack<A> =
            FStackBody.of(items as FList<A>)

        override fun <B, A : Any> ofMap(items: Iterator<B>, f: (B) -> A): FStack<A> =
            FStackBody.of(FList.ofMap(items, f))

        override fun <A : Any, B> ofMap(items: List<B>, f: (B) -> A): FStack<A> =
            FStackBody.of(FList.ofMap(items, f))

        override fun <A : Any> Collection<A>.toIMStack(): FStack<A> =
            FStackBody.of(toIMList() as FList<A>)
    }

}

internal class FStackBody<out A: Any> private constructor (
    val body: FList<A>
): FStack<A>() {
    override fun equals(other: Any?): Boolean = if (fempty()) emptyEquals(other) else when {
        this === other -> true
        other == null -> false
        other is FStackBody<*> -> when {
            fempty() && other.fempty() -> true
            fempty() || other.fempty() -> false
            ftop()!!.isStrictly(other.ftop()!!) -> IMStackEqual2(this, other)
            else -> false
        }
        else -> false
    }

    override fun hashCode(): Int = if (fempty()) emptyHashCode else body.hashCode()

    val show: String by lazy {
        if (fempty()) FStack::class.simpleName+"(*)"
        else this::class.simpleName+"("+body.ffoldLeft("") { str, h -> "$str{$h}" }+")"
    }

    override fun toString(): String {
        return show
    }

    companion object {
        val empty: FStackBody<Nothing> = FStackBody(FLNil)
        private val emptyEquality = object { val proxy = IMCommonEmpty.Companion.IMCommonEmptyEquality() } // this gives me a distinct hashCode
        private val emptyEquals: (Any?) -> Boolean = { other -> emptyEquality.proxy.equals(other) }
        private val emptyHashCode: Int by lazy {  emptyEquality.proxy.hashCode() }

        internal fun <A: Any> of(body: FList<A>? = FLNil): FStack<A> {
            return if (body is FLNil) emptyIMStack() else body?.let { FStackBody(body) } ?: empty
        }
        fun <A: Any> hashCode(s: FStackBody<A>) = s.hashCode()
    }
}