package com.xrpn.imapi

import com.xrpn.immutable.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicBoolean

private interface EmptyIMDj<out L: Any, out R: Any>: IMOrderedEmpty<IMDj<L,R>>, IMDj<L,R>  {
    override fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Nothing> = TODO("internal error")
}

private val emptyIMDj: EmptyIMDj<Any, Any> =
    object: EmptyIMDj<Any, Any>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMTSDJ
        override fun left(): Nothing? = null
        override fun right(): Nothing? = null
        override fun isLeft(): Boolean = false
        override fun isRight(): Boolean = false
        override fun <C> bireduce(fl: (Any) -> C, fr: (Any) -> C): Nothing = TODO("internal error")
        override fun <C: Any, D: Any> bimap(fl: (Any) -> C, fr: (Any) -> D): IMDj<C, D> = TODO("internal error")
    }

sealed class /* Trivially Simple DisJunction */ TSDJ<out A, out B>: IMDj<A,B>, IMSdj<A,B> {

    override fun left(): A? = when (this) {
        is TSDL -> l
        else -> null
    }

    override fun right(): B? = when (this) {
        is TSDR -> r
        else -> null
    }

    override fun isLeft(): Boolean = this is TSDL

    override fun isRight(): Boolean = this is TSDR

    override fun <C> bireduce(fl: (A) -> C, fr: (B) -> C): C = when (this) {
        is TSDL -> fl(left()!!)
        is TSDR -> fr(right()!!)
    }

    override fun <C:Any, D:Any> bimap(fl: (A) -> C, fr: (B) -> D): TSDJ<C,D> = when (this) {
        is Invalid -> when (val aux = fl(left()!!)) {
            is Exception -> @Suppress("UNCHECKED_CAST") (ErrorTrap(aux) as TSDL<C>)
            is String -> @Suppress("UNCHECKED_CAST") (ErrorMsgTrap(aux) as TSDL<C>)
            is ErrExReport<*> -> @Suppress("UNCHECKED_CAST") (ErrorReportTrap(aux) as TSDL<C>)
            else -> @Suppress("UNCHECKED_CAST") (TSDJInvalid(l) as TSDL<C>)
        }
        is Valid -> TSDJValid(fr(right()!!))
        is TSDL -> when(l) {
            is IMSetNotEmpty<*> -> @Suppress("UNCHECKED_CAST") (TSDJValid(fl(l)) as TSDR<D>)
            else -> @Suppress("UNCHECKED_CAST") (TSDJInvalid(l) as TSDL<C>)
        }
        is TSDR -> TSDJValid(fr(r))
    }

    // IMCommon
    
    val strictness: Boolean by lazy { if (!FT.isContainer(this)) true else this.toUCon()!!.isStrict() }
    override val seal: IMSC = IMSC.IMTSDJ
    override fun fcontains(item: IMDj<@UnsafeVariance A, @UnsafeVariance B>): Boolean = item == this
    override fun fcount(isMatch: (IMDj<A, B>) -> Boolean): Int = if (isMatch(this)) 1 else 0
    override fun fdropAll(items: IMCommon<IMDj<@UnsafeVariance A, @UnsafeVariance B>>): IMDj<A, B> = if (items.fcontains((this))) empty() else this
    override fun fdropItem(item: IMDj<@UnsafeVariance A, @UnsafeVariance B>): IMDj<A, B> = if (this.equals(item)) empty() else this
    override fun fdropWhen(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B> = ffilterNot(isMatch)
    override fun ffilter(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B> = if (isMatch(this)) this else empty()
    override fun ffilterNot(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B> = if (isMatch(this)) empty() else this
    override fun ffindAny(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B>? = if (isMatch(this)) this else null
    override fun <R> ffold(z: R, f: (acc: R, IMDj<A,B>) -> R): R = f(z,this)
    override fun fisStrict(): Boolean = strictness
    override fun fpick(): IMDj<A, B>? = this
    override fun fpopAndRemainder(): Pair<IMDj<A, B>?, IMOrdered<IMDj<A, B>>> = Pair(this, empty())
    override fun fsize(): Int = 1
    override fun toEmpty(): IMCommon<IMDj<A, B>> = empty()
    
    // IMOrdered

    override fun fdrop(n: Int): IMOrdered<IMDj<A, B>> = if ( n < 1) this else empty()
    override fun fnext(): IMDj<A, B> = this
    override fun freverse(): IMOrdered<IMDj<A, B>> = this
    override fun frotl(): IMOrdered<IMDj<A, B>> = this
    override fun frotr(): IMOrdered<IMDj<A, B>> = this
    override fun fswaph(): IMOrdered<IMDj<A, B>> = this
    override fun <C: Any> fzip(items: IMOrdered<C>): IMOrdered<Pair<IMDj<A, B>, C>> =
        if (items.fempty()) @Suppress("UNCHECKED_CAST") (items.toEmpty() as IMOrdered<Pair<IMDj<A, B>, C>>)
        else DWCommon.of(Pair(this,items.fnext()!!))

    // IMMapOp

    override fun <T: Any> fmap(f: (IMDj<A,B>) -> T): ITMap<T> = when(val tValue = f(this)) {
        is IMMapOp<*,*> -> @Suppress("UNCHECKED_CAST") (tValue as ITMap<T>)
        else -> DWFMap.of(tValue)
    }

    // IMMappOp

    override fun <T : Any> fapp(op: (ITMap<IMDj<A, B>>) -> ITMap<T>): ITMapp<T> {
        val arg = op(this)
        return IMMappOp.flift2mapp(arg) ?: DWFMapp.of(arg)
    }

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is TSDJ<*, *> -> false
        hashCode() == other.hashCode() -> true
        else -> false
    }

    override fun softEqual(rhs: Any?): Boolean = equals(rhs) || when (rhs) {
        is TSDJ<*,*> -> when {
            isLeft() -> rhs.isLeft()
            isRight() -> rhs.isRight()
            else -> false
        }
        else -> false
    }

    override fun hashCode(): Int =
        left()?.hashCode() ?: right()!!.hashCode()

    companion object {
        fun <L: Any, R: Any> empty(): IMDj<L,R> = @Suppress("UNCHECKED_CAST") (emptyIMDj as IMDj<L,R>)
    }
}

abstract class TSDL<out A>(open val l: A): TSDJ<A, Nothing>()
abstract class TSDR<out B>(open val r: B): TSDJ<Nothing, B>()

abstract class Invalid<out A>(override val l: A): TSDL<A>(l)
abstract class Valid<out B>(override val r: B): TSDR<B>(r)

data class ErrExReport<out T>(val errData:T?, val ex:Exception?, val separator: String = " :: ") {
    private var verbose: AtomicBoolean = AtomicBoolean(false)
    val baseMessage: String by lazy { ex?.let {"at '$errData' found ${ex::class.simpleName}('${it.message}')" } ?: errData.toString() }
    fun longMsg(): ErrExReport<T> { verbose.set(true); return this }
    fun shortMsg(): ErrExReport<T> { verbose.set(false); return this }
    fun exDetails(): String = ex?.let {
            val w = StringWriter()
            w.append("${separator}details follow")
            w.append('\n')
            it.printStackTrace(PrintWriter(w))
            w.toString()
        } ?: "$separator(no further details available)"
    override fun toString(): String = ex?.let { if(verbose.get()) toLongString() else baseMessage } ?: baseMessage
    fun toLongString(): String = ex?.let { "$baseMessage${exDetails()}" } ?: errData.toString()
}

data class TSDJInvalid<out A>(override val l: A): Invalid<A>(l)
data class ErrorTrap(override val l: Exception): Invalid<Exception>(l)
data class ErrorMsgTrap(override val l: String): Invalid<String>(l)
data class ErrorReportTrap<out T>(override val l: ErrExReport<T>): Invalid<ErrExReport<T>>(l)
data class TSDJValid<out A>(override val r: A): Valid<A>(r)

data class IMSetDJL<out A: Any>(override val l: IMSetNotEmpty<A>): TSDL<IMSetNotEmpty<A>>(l)
data class IMXSetDJR<out A>(override val r: IMXSetNotEmpty<A>): TSDR<IMXSetNotEmpty<A>>(r) where A: Any, A:Comparable<@UnsafeVariance A>
