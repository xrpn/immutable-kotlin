package com.xrpn.imapi

import com.xrpn.immutable.FT
import com.xrpn.immutable.toUCon

private interface EmptyDiw<out A: Any>: IMCommonEmpty<A>, IMOrderedEmpty<A>, IMDisw<A, IMCommonEmpty<A>>  {
    override fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Nothing> = TODO("internal error")
}

private val emptyDiw: EmptyDiw<Any> = object: EmptyDiw<Nothing>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMDIW
}

internal data class /* Discardable Wrapper, Common */ DWCommon<out A: Any> constructor (val a: A): IMDisw<A, IMCommon<A>> {

    // IMCommon

    val strictness: Boolean by lazy { if (!FT.isContainer(this.a)) true else this.a.toUCon()!!.isStrict() }
    override val seal: IMSC = IMSC.IMDIW
    override fun fcontains(item: @UnsafeVariance A): Boolean = item == a
    override fun fcount(isMatch: (A) -> Boolean): Int = if (isMatch(this.a)) 1 else 0
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMCommon<A> = if (items.fcontains((this.a))) empty() else this
    override fun fdropItem(item: @UnsafeVariance A): IMCommon<A> = if (this.equals(item)) empty() else this
    override fun fdropWhen(isMatch: (A) -> Boolean): IMCommon<A> = ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMCommon<A> = if (isMatch(this.a)) this else empty()
    override fun ffilterNot(isMatch: (A) -> Boolean): IMCommon<A> = if (isMatch(this.a)) empty() else this
    override fun ffindAny(isMatch: (A) -> Boolean): A? = if (isMatch(this.a)) this.a else null
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = f(z,a)
    override fun fisStrict(): Boolean = strictness
    override fun fpick(): A = this.a
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(a, empty())
    override fun fsize(): Int = 1

    // IMOrdered

    override fun fdrop(n: Int): IMOrdered<A> = if (0==n) this else empty()
    override fun fnext(): Pair<A?, IMOrdered<A>> = Pair(a, empty())
    override fun freverse(): IMOrdered<A> = this
    override fun frotl(): IMOrdered<A> = this
    override fun frotr(): IMOrdered<A> = this
    override fun fswaph(): IMOrdered<A> = this
    override fun <B: Any> fzip(items: IMOrdered<B>): IMDisw<Pair<A, B>, IMCommon<Pair<A, B>>> {
        val (itn: B?, itns: IMOrdered<B>) = items.fnext()
        val aux: IMCommon<Any>? by lazy { itns.asIMCommon() }
        return when {
            null == itn -> empty()
            null == aux -> empty()
            1 != aux!!.fsize() -> empty()
            else -> of(Pair(a,itn)) as IMDisw<Pair<A, B>, IMCommon<Pair<A, B>>>
        }
    }

    companion object {
        fun <A: Any> of(a: IMCommon<A>): IMCommon<A> = a
        fun <A: Any> of(a: A): IMCommon<A> {
            check(a !is IMCommon<*>)
            return DWCommon(a)
        }
        fun <A: Any> empty(): IMDisw<A, IMCommonEmpty<A>> = @Suppress("UNCHECKED_CAST") (emptyDiw as IMDisw<A, IMCommonEmpty<A>>)
    }
}

private interface EmptyDmw<out A: Any, out B: IMCommonEmpty<A>>: IMCommonEmpty<A>, IMOrderedEmpty<A>, IMDimw<A, IMCommonEmpty<A>> {
    override fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Nothing> = TODO("internal error")
}

private val emptyDmw: EmptyDmw<Any,IMCommonEmpty<Any>> = object : EmptyDmw<Any,IMCommonEmpty<Any>>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMDMW
    override fun <T : Any> fmap(f: (Any) -> T): FMap<Nothing> = TODO("internal error")
}

internal data class /* Discardable Wrapper, FMap */ DWFMap<out A: Any, out B: IMCommon<A>> constructor (
        val a: A, val b: IMCommon<A>
    ): IMCommon<A> by b, IMOrdered<A> by (@Suppress("UNCHECKED_CAST") (b as IMOrdered<A>)), IMDimw<A, IMCommon<A>> {

    override val seal: IMSC = IMSC.IMDMW
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(this.a, empty())
    override fun fsize(): Int = 1

    override fun <T : Any> fmap(f: (A) -> T): IMMapOp<T, IMCommon<T>>
        = of(f(this.a))

    override fun <B: Any> fzip(items: IMOrdered<B>): IMDimw<Pair<A, B>, IMCommon<Pair<A, B>>> {
        val (itn: B?, itns: IMOrdered<B>) = items.fnext()
        val aux: IMCommon<Any>? by lazy { itns.asIMCommon() }
        return when {
            null == itn -> empty()
            null == aux -> empty()
            1 != aux!!.fsize() -> empty()
            else -> of(Pair(a,itn)) as IMDimw<Pair<A, B>, IMCommon<Pair<A, B>>>
        }
    }

    companion object {
        fun <A: Any> of(a: FMap<A>): IMMapOp<A, IMCommon<A>> = a
        fun <A: Any> of(a: IMDisw<A, IMCommon<A>>): IMMapOp<A, IMCommon<A>> {
            check (a !is IMMapOp<*,*>)
            return DWFMap((a as DWCommon<A>).a, a)
        }
        fun <A: Any> of(a: A): IMMapOp<A, IMCommon<A>> {
            check (a !is IMMapOp<*,*>)
            check (a !is IMDisw<*,*>)
            return DWFMap(a, DWCommon.of(a))
        }
        fun <A: Any> empty(): IMDimw<A, IMCommonEmpty<A>> = @Suppress("UNCHECKED_CAST") (emptyDmw as IMDimw<A, IMCommonEmpty<A>>)
    }

}

private interface EmptyDaw<out A: Any, out B: EmptyDmw<A, IMCommonEmpty<A>>>: IMCommonEmpty<A>, IMOrderedEmpty<A>, IMDiaw<A, IMCommonEmpty<A>> {
    override fun <T: Any> fapp(op: (FMap<A>) -> FMap<T>): FMapp<Nothing> = TODO("internal error")
}

private val emptyDaw: EmptyDaw<Any, EmptyDmw<Any,IMCommonEmpty<Any>>> = object: EmptyDaw<Any, EmptyDmw<Any,IMCommonEmpty<Any>>>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMDAW
    override fun <B : Any> fzip(items: IMOrdered<B>): IMOrdered<Nothing> = TODO("internal error")
    override fun <T : Any> fmap(f: (Any) -> T): FMap<Nothing> = TODO("internal error")
}

internal data class /* Discardable Wrapper, FMapp */ DWFMapp<out A: Any, out B: IMDimw<A, IMCommon<A>>> constructor (
    val a: A, val b: FMap<A>
): IMCommon<A> by b, IMOrdered<A> by (@Suppress("UNCHECKED_CAST") (b as IMOrdered<A>)), IMDiaw<A,FMap<A>>  {

    override val seal: IMSC = IMSC.IMDAW
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(this.a, empty())
    override fun fsize(): Int = 1

    override fun <T: Any> fmap(f: (A) -> T): FMap<T> = b.fmap(f)

    override fun <T : Any> fapp(op: (FMap<A>) -> FMap<T>): FMapp<T> {
        val aux: FMap<T> = op(b)
        return when {
            aux.fempty() -> empty()
            aux.fsize() == 1 -> of(aux.fpick()!!)
            else -> IMMappOp.flift2mapp(aux) ?: empty()
        }
    }

    override fun <B: Any> fzip(items: IMOrdered<B>): IMDiaw<Pair<A, B>, IMCommon<Pair<A, B>>> {
        val (itn: B?, itns: IMOrdered<B>) = items.fnext()
        val aux: IMCommon<Any>? by lazy { itns.asIMCommon() }
        return when {
            null == itn -> empty()
            null == aux -> empty()
            1 != aux!!.fsize() -> empty()
            else -> of(Pair(a, itn)) as IMDiaw<Pair<A, B>, IMCommon<Pair<A, B>>>
        }
    }

    companion object {
        fun <A: Any> of(a: FMapp<A>): FMapp<A> = a
        fun <A: Any> of(a: IMDimw<A, IMCommon<A>>): FMapp<A> {
            check(a !is IMMappOp<*,*>)
            (@Suppress("UNCHECKED_CAST")(a as DWFMap<A,IMCommon<A>>))
            return DWFMapp(a.a, a)
        }
        fun <A: Any> of(a: FMap<A>): FMapp<A> {
            check(a !is IMMappOp<*,*>)
            return when {
            a.fempty() -> empty()
            1 == a.fsize() -> DWFMapp(a.fpick()!!, a)
            else -> IMMappOp.flift2mapp(a) ?: empty()
        }}
        fun <A: Any> of(a: A): FMapp<A> {
            check((a !is IMMappOp<*,*>) && (a !is IMMapOp<*,*>))
            return DWFMapp(a, DWFMap.of(a))
        }
        fun <A: Any> empty(): IMDiaw<A, IMCommonEmpty<A>> = @Suppress("UNCHECKED_CAST") (emptyDmw as IMDiaw<A, IMCommonEmpty<A>>)
    }
}