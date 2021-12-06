package com.xrpn.imapi

import com.xrpn.immutable.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicBoolean

val emptyTsdj: IMDj<Nothing, Nothing> =
    object: IMDj<Nothing, Nothing>, IMCommonEmpty<IMDj<Nothing, Nothing>>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMTSDJ
        override fun left(): Nothing? = null
        override fun right(): Nothing? = null
        override fun isLeft(): Boolean = false
        override fun isRight(): Boolean = false
        override fun <C> bireduce(fl: (Nothing) -> C, fr: (Nothing) -> C): Nothing = TODO()
        override fun <C: Any, D: Any> bimap(fl: (Nothing) -> C, fr: (Nothing) -> D): IMDj<C, D> = this
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

    val strictness: Boolean by lazy { if (!FT.isContainer(this)) true else this.toUCon()!!.isStrict() }
    override val seal: IMSC = IMSC.IMTSDJ
    override fun fcontains(item: IMDj<@UnsafeVariance A, @UnsafeVariance B>): Boolean = item == this
    override fun fcount(isMatch: (IMDj<A, B>) -> Boolean): Int = if (isMatch(this)) 1 else 0
    override fun fdropAll(items: IMCommon<IMDj<@UnsafeVariance A, @UnsafeVariance B>>): IMDj<A, B> = if (items.fcontains((this))) emptyTsdj else this
    override fun fdropItem(item: IMDj<@UnsafeVariance A, @UnsafeVariance B>): IMDj<A, B> = if (this.equals(item)) emptyTsdj else this
    override fun fdropWhen(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B> = ffilterNot(isMatch)
    override fun ffilter(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B> = if (isMatch(this)) this else emptyTsdj
    override fun ffilterNot(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B> = if (isMatch(this)) emptyTsdj else this
    override fun ffindAny(isMatch: (IMDj<A, B>) -> Boolean): IMDj<A, B>? = if (isMatch(this)) this else null
    override fun <R> ffold(z: R, f: (acc: R, IMDj<A,B>) -> R): R = f(z,this)
    override fun fisStrict(): Boolean = strictness
    override fun fpick(): IMDj<A, B>? = this
    override fun fpopAndRemainder(): Pair<IMDj<A, B>?, IMCommon<IMDj<A, B>>> = Pair(this, emptyTsdj)
    override fun fsize(): Int = 1

    override fun <T: Any> fmap(f: (IMDj<A,B>) -> T): FMap<T> = when(val tValue = f(this)) {
        is IMMapOp<*,*> -> @Suppress("UNCHECKED_CAST") (tValue as FMap<T>)
        else -> DWFMap.of(tValue)
    }

    override fun <T : Any> fapp(op: (FMap<IMDj<A, B>>) -> FMap<T>): FMapp<T> {
        val arg = op(this)
        return IMMappOp.flift2mapp(arg) ?: DWFMapp.of(arg)
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
