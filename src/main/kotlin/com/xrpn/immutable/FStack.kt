package com.xrpn.immutable

import com.xrpn.bridge.FStackIterator
import com.xrpn.imapi.*
import com.xrpn.imapi.IMStackEqual2
import com.xrpn.immutable.FList.Companion.toIMList

sealed class FStack<out A: Any>: IMStack<A> {

    val size: Int by lazy { this.toFList().size }

    fun isEmpty(): Boolean = this.toFList().isEmpty()

    fun iterator(): FStackIterator<A> = FStackIterator(this)

    // imcollection

    override val seal: IMSC = IMSC.IMSTACK

    override fun fcontains(item: @UnsafeVariance A): Boolean=
        this.toFList().fcontains(item)

    override fun fdropAll(items: IMCollection<@UnsafeVariance A>): FStack<A> {
        TODO("Not yet implemented")
    }

    override fun fdropItem(item: @UnsafeVariance A): IMStack<A> {
        TODO("Not yet implemented")
    }

    override fun ffilter(isMatch: (A) -> Boolean): FStack<A> =
        FStackBody.of(this.toFList().ffilter(isMatch))

    override fun ffilterNot(isMatch: (A) -> Boolean): FStack<A> =
        ffilter { !isMatch(it) }

    override fun ffindAny(isMatch: (A) -> Boolean): A? =
        this.toFList().ffindAny(isMatch)

    override fun fisStrict(): Boolean =
        this.toFList().fisStrict()

    override fun fpick(): A? =
        this.toFList().fhead()

    override fun fpopAndRemainder(): Pair<A?, FStack<A>> = fpop()

    // ============ filtering

    override fun fdrop(n: Int): FStack<A> =
        if (0 < n) FStackBody.of(this.toFList().fdrop(n)) else this


    override fun fdropIfTop(item: @UnsafeVariance A): FStack<A> = ftop()?.let {
        if (it.equals(item)) fpopOrThrow().second else this
    } ?: this

    override fun fdropTopWhen(isMatch: (A) -> Boolean): FStack<A> =
        if (ftopMatch(isMatch)) FStackBody.of(this.toFList().ftail()) else this

    override fun fdropWhile(isMatch: (A) -> Boolean): FStack<A> =
        FStackBody.of(this.toFList().fdropWhile(isMatch))

    override fun ftopMatch(isMatch: (A) -> Boolean): Boolean =
        ftop()?.let { isMatch(it) } ?: false

    override fun ftop(): A? = this.toFList().fhead()

    override fun ftopOrThrow(): A = ftop() ?: throw IllegalStateException("top of empty stack")

    // ============ grouping

    override fun fcount(isMatch: (A) -> Boolean): Int =
        this.toFList().fcount(isMatch)

    override fun fsize(): Int =
        this.toFList().size

    // ============ transforming

    // ============ transforming

    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R {
        TODO("Not yet implemented")
    }

    override fun <B : Any> fpopMap(f: (A) -> B): Pair<B?, IMStack<A>> =
        fpop().let { pit ->
            pit.first?.let {
                pit.pmap2({ a -> f(a!!) }, { it })
            } ?: Pair(null, pit.second)
        }

    override fun freverse(): FStack<A> =
        FStackBody.of(this.toFList().freverse())

    override fun <B : Any> ftopMap(f: (A) -> B): B? =
        ftop()?.let{ f(it) }

    // ============ altering

    override fun fpop(): Pair<A?, FStack<A>> =
        ftop()?.let { buildPair() } ?: Pair(null, this)

    override fun fpopOrThrow(): Pair<A, FStack<A>> =
        ftop()?.let { buildPair() } ?: throw IllegalStateException("pop from empty stack")

    override fun fpush(top: @UnsafeVariance A): FStack<A> =
        FStackBody.of(FLCons(top, this.toFList()))

    // ============ utility

    override fun equal(rhs: IMStack<@UnsafeVariance A>): Boolean =
        this.toFList().equal(rhs.toIMList())

    override fun fforEach(f: (A) -> Unit) =
        this.toFList().fforEach(f)

    override fun toIMList(): IMList<A> =
        this.toFList()

    override fun copy(): FStack<A> =
        FStackBody.of(this.toFList().copy())

    override fun copyToMutableList(): MutableList<@UnsafeVariance A> =
        this.toFList().copyToMutableList()

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
            FStackBody.of(this.toIMList() as FList<A>)
    }

}

internal class FStackBody<out A: Any> private constructor (
    val body: FList<A>
): FStack<A>() {
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FStackBody<*> -> when {
            this.fempty() && other.fempty() -> true
            this.fempty() || other.fempty() -> false
            this.ftop()!!::class == other.ftop()!!::class -> IMStackEqual2(this, other)
            else -> false
        }
        else -> false
    }

    override fun hashCode(): Int {
        return body.hashCode()
    }

    val show: String by lazy {
        if (this.fempty()) FStack::class.simpleName+"(*)"
        else this::class.simpleName+"("+body.ffoldLeft("") { str, h -> "$str{$h}" }+")"
    }

    override fun toString(): String {
        return show
    }

    companion object {
        val empty: FStackBody<Nothing> = FStackBody(FLNil)
        internal fun <A: Any> of(body: FList<A>? = FLNil): FStack<A> {
            return if (body is FLNil) emptyIMStack() else body?.let { FStackBody(body) } ?: empty
        }
        fun <A: Any> hashCode(s: FStackBody<A>) = s.hashCode()
    }

}