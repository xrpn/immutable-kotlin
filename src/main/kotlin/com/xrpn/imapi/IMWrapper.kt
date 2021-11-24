package com.xrpn.imapi

import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FT
import com.xrpn.immutable.toUCon

val emptyDiw = object : IMCommonEmpty<Nothing> {
    override val seal: IMSC = IMSC.IMDIW
}

internal data class /* Discardable Simple Wrapper */ DSW<out A: Any, out B: IMCommon<A>> constructor (val a: A): IMDisw<A, IMCommon<A>> {

    val strictness: Boolean by lazy { if (!FT.isContainer(this.a)) true else this.a.toUCon()!!.isStrict() }
    override val seal: IMSC = IMSC.IMDIW
    override fun fcontains(item: @UnsafeVariance A): Boolean = item == this
    override fun fcount(isMatch: (A) -> Boolean): Int = if (isMatch(this.a)) 1 else 0
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMCommon<A> = if (items.fcontains((this.a))) emptyDiw else this
    override fun fdropItem(item: @UnsafeVariance A): IMCommon<A> = if (this.equals(item)) emptyDiw else this
    override fun fdropWhen(isMatch: (A) -> Boolean): IMCommon<A> = ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMCommon<A> = if (isMatch(this.a)) this else emptyDiw
    override fun ffilterNot(isMatch: (A) -> Boolean): IMCommon<A> = if (isMatch(this.a)) emptyDiw else this
    override fun ffindAny(isMatch: (A) -> Boolean): A? = if (isMatch(this.a)) this.a else null
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = f(z,a)
    override fun fisStrict(): Boolean = strictness
    override fun fpick(): A = this.a
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(this.a, emptyDiw)
    override fun fsize(): Int = 1

    companion object {
        fun <A: Any> of(a: IMCommon<A>): IMCommon<A> = a
        fun <A: Any> of(a: A): IMCommon<A> {
            check(a !is IMCommon<*>)
            return DSW(a)
        }
    }
}

private interface EmptyDmw<out A: Any, out B: IMCommonEmpty<A>>: IMCommonEmpty<A>, IMMapOp<A, IMCommonEmpty<A>> {
    override fun <T : Any> fmap(f: (A) -> T): IMMapOp<T, IMCommon<T>> = TODO("internal error")
}

private val emptyDmw = object : EmptyDmw<Nothing,Nothing> {
    override val seal: IMSC = IMSC.IMDMW
}

internal data class /* Discardable Map Wrapper */ DMW<out A: Any, out B: IMCommon<A>> constructor (
        val a: A, val b: IMCommon<A>
    ): IMCommon<A> by b, IMDimw<A, IMCommon<A>> {

    override val seal: IMSC = IMSC.IMDMW
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(this.a, empty())
    override fun fsize(): Int = 1

    override fun <T : Any> fmap(f: (A) -> T): IMMapOp<T, IMCommon<T>>
        = of(f(this.a))

    companion object {
        fun <A: Any> of(a: FMap<A>): IMMapOp<A, IMCommon<A>> = a
        fun <A: Any> of(a: IMDisw<A, IMCommon<A>>): IMMapOp<A, IMCommon<A>> {
            check (a !is IMMapOp<*,*>)
            return DMW((a as DSW<A, IMCommon<A>>).a, a)
        }
        fun <A: Any> of(a: A): IMMapOp<A, IMCommon<A>> {
            check (a !is IMMapOp<*,*>)
            check (a !is IMDisw<*,*>)
            return DMW(a, DSW.of(a))
        }
        fun <A: Any> empty(): IMMapOp<A, IMCommon<A>> = emptyDmw
    }
}

private interface EmptyDaw<out A: Any, out B: EmptyDmw<A, IMCommonEmpty<A>>>: IMCommonEmpty<A>, IMMappOp<A, IMMapOp<A, IMCommonEmpty<A>>> {
    override fun <T : Any> fappro(op: (IMMapOp<A, IMCommonEmpty<A>>) -> FMap<T>): FMapp<T> = TODO("internal error")
}

private val emptyDaw = object : EmptyDaw<Nothing,Nothing>{
    override val seal: IMSC = IMSC.IMDAW
}

internal data class /* Discardable Mapp Wrapper */ DAW<out A: Any, out B: IMDimw<A, IMCommon<A>>> constructor (
    val a: A, val b: FMap<A>
): IMCommon<A> by b, IMDiaw<A,FMap<A>>  {

    override val seal: IMSC = IMSC.IMDAW
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(this.a, emptyDaw)
    override fun fsize(): Int = 1

    override fun <T: Any> fmap(f: (A) -> T): FMap<T> = b.fmap(f)

    override fun <T : Any> fappro(op: (FMap<A>) -> FMap<T>): FMapp<T> {
        val aux: FMap<T> = op(b)
        return when {
            aux.fempty() -> emptyDaw
            aux.fsize() == 1 -> of(aux.fpick()!!)
            else -> IMMappOp.flift2mapp(aux) ?: empty()
        }
    }

    companion object {
        fun <A: Any> of(a: FMapp<A>): FMapp<A> = a
        fun <A: Any> of(a: IMDimw<A, IMCommon<A>>): FMapp<A> {
            check(a !is IMMappOp<*,*>)
            (@Suppress("UNCHECKED_CAST")(a as DMW<A,IMCommon<A>>))
            return DAW(a.a, a)
        }
        fun <A: Any> of(a: FMap<A>): FMapp<A> {
            check(a !is IMMappOp<*,*>)
            return when {
            a.fempty() -> empty()
            1 == a.fsize() -> DAW(a.fpick()!!, a)
            else -> IMMappOp.flift2mapp(a) ?: empty()
        }}
        fun <A: Any> of(a: A): FMapp<A> {
            check((a !is IMMappOp<*,*>) && (a !is IMMapOp<*,*>))
            return DAW(a, DMW.of(a))
        }
        fun <A: Any> empty(): FMapp<A> = emptyDaw
    }

}