package com.xrpn.imapi

import com.xrpn.immutable.*
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.atomic.AtomicBoolean

val emptyTsdj = object : IMCommonEmpty<TSDJ<Nothing, Nothing>> {
    override val seal: IMSC = IMSC.IMTSDJ
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

    fun <C> bireduce(fl: (A) -> C, fr: (B) -> C): C = when (this) {
        is TSDL -> fl(left()!!)
        is TSDR -> fr(right()!!)
    }

    fun <C:Any, D:Any> bimap(fl: (A) -> C, fr: (B) -> D): TSDJ<C,D> = when (this) {
        is Invalid -> when (val aux = fl(left()!!)) {
            is String -> @Suppress("UNCHECKED_CAST") (ErrSTrap(aux) as TSDL<C>)
            is ErrExReport -> @Suppress("UNCHECKED_CAST") (ErrRTrap(aux) as TSDL<C>)
            else -> @Suppress("UNCHECKED_CAST") (ErrATrap(aux) as TSDL<C>)
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
    override fun fcontains(item: TSDJ<@UnsafeVariance A, @UnsafeVariance B>): Boolean = item == this
    override fun fcount(isMatch: (TSDJ<A, B>) -> Boolean): Int = if (isMatch(this)) 1 else 0
    override fun fdropAll(items: IMCommon<TSDJ<@UnsafeVariance A, @UnsafeVariance B>>): IMCommon<TSDJ<A, B>> = if (items.fcontains((this))) emptyTsdj else this
    override fun fdropItem(item: TSDJ<@UnsafeVariance A, @UnsafeVariance B>): IMCommon<TSDJ<A, B>> = if (this.equals(item)) emptyTsdj else this
    override fun fdropWhen(isMatch: (TSDJ<A, B>) -> Boolean): IMCommon<TSDJ<A, B>> = ffilterNot(isMatch)
    override fun ffilter(isMatch: (TSDJ<A, B>) -> Boolean): IMCommon<TSDJ<A, B>> = if (isMatch(this)) this else emptyTsdj
    override fun ffilterNot(isMatch: (TSDJ<A, B>) -> Boolean): IMCommon<TSDJ<A, B>> = if (isMatch(this)) emptyTsdj else this
    override fun ffindAny(isMatch: (TSDJ<A, B>) -> Boolean): TSDJ<A, B>? = if (isMatch(this)) this else null
    override fun <R> ffold(z: R, f: (acc: R, TSDJ<A,B>) -> R): R = f(z,this)
    override fun fisStrict(): Boolean = strictness
    override fun fpick(): TSDJ<A, B>? = this
    override fun fpopAndRemainder(): Pair<TSDJ<A, B>?, IMCommon<TSDJ<A, B>>> = Pair(this, emptyTsdj)
    override fun fsize(): Int = 1

    override fun <T: Any> fmap(f: (TSDJ<A,B>) -> T): FMap<T> = when(val tValue = f(this)) {
        is IMMapOp<*,*> -> @Suppress("UNCHECKED_CAST") (tValue as FMap<T>)
        else -> DMW.of(tValue)
    }

    override fun <T : Any> fappro(op: (FMap<TSDJ<A, B>>) -> FMap<T>): FMapp<T> {
        val arg = op(this)
        return IMMappOp.flift2mapp(arg) ?: DAW.of(arg)
    }
}

abstract class TSDL<out A>(open val l: A): TSDJ<A, Nothing>()
abstract class TSDR<out B>(open val r: B): TSDJ<Nothing, B>()

abstract class Invalid<out A>(override val l: A): TSDL<A>(l)
abstract class Valid<out B>(override val r: B): TSDR<B>(r)

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

data class TSDJInvalid<out A>(override val l: A): Invalid<A>(l)
data class ErrATrap(override val l: Any): Invalid<Any>(l)
data class ErrSTrap(override val l: String): Invalid<String>(l)
data class ErrRTrap(override val l: ErrExReport): Invalid<ErrExReport>(l)
data class TSDJValid<out A>(override val r: A): Valid<A>(r)

data class IMSetDJL<out A: Any>(override val l: IMSetNotEmpty<A>): TSDL<IMSetNotEmpty<A>>(l)
data class IMXSetDJR<out A>(override val r: IMXSetNotEmpty<A>): TSDR<IMXSetNotEmpty<A>>(r) where A: Any, A:Comparable<@UnsafeVariance A>
