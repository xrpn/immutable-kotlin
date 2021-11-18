package com.xrpn.imapi

import com.xrpn.immutable.FT
import com.xrpn.immutable.toUCon
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicBoolean


val emptyTsdj = object : IMCommonEmpty<TSDJ<Nothing, Nothing>> {
    override val seal: IMSC = IMSC.IMTSDJ
    override fun fcount(isMatch: (TSDJ<Nothing, Nothing>) -> Boolean): Int = 0
    override fun fcontains(item: TSDJ<Nothing, Nothing>): Boolean = false
    override fun fdropAll(items: IMCommon<TSDJ<Nothing, Nothing>>): IMCommon<TSDJ<Nothing, Nothing>> = this
    override fun fdropItem(item: TSDJ<Nothing, Nothing>): IMCommon<TSDJ<Nothing, Nothing>> = this
    override fun fdropWhen(isMatch: (TSDJ<Nothing, Nothing>) -> Boolean): IMCommon<TSDJ<Nothing, Nothing>> = this
    override fun ffilter(isMatch: (TSDJ<Nothing, Nothing>) -> Boolean): IMCommon<TSDJ<Nothing, Nothing>> = this
    override fun ffilterNot(isMatch: (TSDJ<Nothing, Nothing>) -> Boolean): IMCommon<TSDJ<Nothing, Nothing>> = this
    override fun ffindAny(isMatch: (TSDJ<Nothing, Nothing>) -> Boolean): TSDJ<Nothing, Nothing>? = null
    override fun fisStrict(): Boolean = true
    override fun fpick(): Nothing? = null
    override fun fsize(): Int = 0
    override fun fpopAndRemainder(): Pair<TSDJ<Nothing, Nothing>?, IMCommon<TSDJ<Nothing, Nothing>>> = Pair(null, this)
}

sealed class /* Trivially Simple DisJunction */ TSDJ<out A, out B>: IMSdj<A,B> {
    fun left(): A? = when (this) {
        is TSDL -> l
        else -> null
    }

    fun right(): B? = when (this) {
        is TSDR -> r
        else -> null
    }

    fun <C> bicmap(fl: (A) -> C, fr: (B) -> C): C = when (this) {
        is TSDL -> fl(left()!!)
        is TSDR -> fr(right()!!)
    }

    fun <C:Any, D:Any> bimap(fl: (A) -> C, fr: (B) -> D): TSDJ<C,D> = when (this) {
        is TSDL -> when (val aux = fl(left()!!)) {
            is String -> @Suppress("UNCHECKED_CAST") (ErrSTrap(aux) as TSDL<C>)
            is ErrExReport -> @Suppress("UNCHECKED_CAST") (ErrRTrap(aux) as TSDL<C>)
            else -> @Suppress("UNCHECKED_CAST") (ErrATrap(aux) as TSDL<C>)
        }
        is TSDR -> TSDJPass(fr(right()!!))
    }

    val strictness: Boolean by lazy { if (!FT.isContainer(this)) true else this.toUCon()!!.isStrict() }
    override val seal: IMSC = IMSC.IMTSDJ
    override fun fcontains(item: TSDJ<@UnsafeVariance A, @UnsafeVariance B>): Boolean = item == this
    override fun fcount(isMatch: (TSDJ<A, B>) -> Boolean): Int = if (isMatch(this)) 1 else 0
    override fun fdropAll(items: IMCommon<TSDJ<@UnsafeVariance A, @UnsafeVariance B>>): IMCommon<TSDJ<A, B>> = if (items.fcontains((this))) emptyTsdj else this
    override fun fdropItem(item: TSDJ<@UnsafeVariance A, @UnsafeVariance B>): IMCommon<TSDJ<A, B>> = if (this.equals(item)) emptyTsdj else this
    override fun fdropWhen(isMatch: (TSDJ<A, B>) -> Boolean): IMCommon<TSDJ<A, B>> = ffilterNot(isMatch)
    override fun ffilter(isMatch: (TSDJ<A, B>) -> Boolean): IMCommon<TSDJ<A, B>> = if (isMatch(this)) this else emptyTsdj
    override fun ffilterNot(isMatch: (TSDJ<A, B>) -> Boolean): IMCommon<TSDJ<A, B>> = if (isMatch(this)) emptyTsdj else this
    override fun ffindAny(isMatch: (TSDJ<A, B>) -> Boolean): TSDJ<A, B>? = if (isMatch(this)) this else null
    override fun fisStrict(): Boolean = strictness
    override fun fpick(): TSDJ<A, B>? = this
    override fun fpopAndRemainder(): Pair<TSDJ<A, B>?, IMCommon<TSDJ<A, B>>> = Pair(this, emptyTsdj)
    override fun fsize(): Int = 1
}

abstract class TSDL<out A>(open val l: A): TSDJ<A, Nothing>()
abstract class TSDR<out B>(open val r: B): TSDJ<Nothing, B>()

data class ErrExReport(val errMsg:String, val ex:Exception?) {
    private var verbose: AtomicBoolean = AtomicBoolean(false)
    fun longMsg(): ErrExReport { verbose.set(true); return this }
    fun shortMsg(): ErrExReport { verbose.set(false); return this }
    fun exDetails(): String? = ex?.let {
            val w = StringWriter()
            it.printStackTrace(PrintWriter(w))
            w.toString()
        }
    override fun toString(): String = ex?.let { "$errMsg :: ${it.message}${if(verbose.get()) exDetails() else ' '}" } ?: errMsg
}

data class ErrATrap(override val l: Any): TSDL<Any>(l)
data class ErrSTrap(override val l: String): TSDL<String>(l)
data class ErrRTrap(override val l: ErrExReport): TSDL<ErrExReport>(l)
data class TSDJPass<out A>(override val r: A): TSDR<A>(r)

data class IMSetDJL<out A: Any>(override val l: IMSetNotEmpty<A>): TSDL<IMSetNotEmpty<A>>(l)
data class IMXSetDJR<out A>(override val r: IMXSetNotEmpty<A>): TSDR<IMXSetNotEmpty<A>>(r) where A: Any, A:Comparable<@UnsafeVariance A>
