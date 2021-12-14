package com.xrpn.imapi

import com.xrpn.immutable.FT
import com.xrpn.immutable.toUCon

private interface EmptyDiw<out A: Any>: IMOrderedEmpty<A>, IMDisw<A, IMCommonEmpty<A>>  {
    override fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Nothing> = TODO("internal error")
}

private val emptyDiw: EmptyDiw<Any> = object: EmptyDiw<Any>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMDIW
    override fun fadd(item: Any): IMWritable<Nothing> = TODO("internal error")
}

internal data class /* Discardable Wrapper, Common */ DWCommon<out A: Any> constructor (val a: A): IMDisw<A, IMCommon<A>> {

    // IMCommon

    val strictness: Boolean by lazy { if (!FT.isContainer(this.a)) true else this.a.toUCon()!!.isStrict() }
    override val seal: IMSC = IMSC.IMDIW
    override fun fcontains(item: @UnsafeVariance A): Boolean = item == a
    override fun fcount(isMatch: (A) -> Boolean): Int = if (isMatch(this.a)) 1 else 0
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMOrdered<A> = if (items.fcontains((this.a))) empty() else this
    override fun fdropItem(item: @UnsafeVariance A): IMOrdered<A> = if (this.equals(item)) empty() else this
    override fun fdropWhen(isMatch: (A) -> Boolean): IMOrdered<A> = ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMOrdered<A> = if (isMatch(this.a)) this else empty()
    override fun ffilterNot(isMatch: (A) -> Boolean): IMOrdered<A> = if (isMatch(this.a)) empty() else this
    override fun ffindAny(isMatch: (A) -> Boolean): A? = if (isMatch(this.a)) this.a else null
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = f(z,a)
    override fun fisStrict(): Boolean = strictness
    override fun fpick(): A = this.a
    override fun fpopAndRemainder(): Pair<A?, IMOrdered<A>> = Pair(a, empty())
    override fun fsize(): Int = 1
    override fun toEmpty(): ITDsw<A> = empty()

    // IMOrdered

    override fun fdrop(n: Int): IMOrdered<A> = if (0==n) this else empty()
    override fun fnext(): A = a
    override fun freverse(): IMOrdered<A> = this
    override fun frotl(): IMOrdered<A> = this
    override fun frotr(): IMOrdered<A> = this
    override fun fswaph(): IMOrdered<A> = this
    override fun <B: Any> fzip(items: IMOrdered<B>): IMDisw<Pair<A, B>, IMCommon<Pair<A, B>>> {
        val itn: B? = items.fnext()
        return when (itn) {
            null -> empty()
            else -> of(Pair(a,itn))
        }
    }

    // IMWritable

    override fun fadd(item: @UnsafeVariance A): ITDsw<A> =
        of(a)

    companion object {
        fun <A: Any> of(a: IMCommon<A>): IMCommon<A> = a
        fun <A: Any> of(a: A): IMDisw<A, IMCommon<A>> {
            check(a !is IMCommon<*>)
            return DWCommon(a)
        }
        fun <A: Any> empty(): ITDsw<A> = @Suppress("UNCHECKED_CAST") (emptyDiw as IMDisw<A, IMCommonEmpty<A>>)
    }

    // equals() = TODO()
    // hashcode() = TODO()
    override fun softEqual(rhs: Any?): Boolean = TODO()
}

private interface EmptyDmw<out A: Any, out B: IMCommonEmpty<A>>: IMOrderedEmpty<A>, IMDimw<A, IMCommonEmpty<A>> {
    override fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Nothing> = TODO("internal error")
}

private val emptyDmw: EmptyDmw<Any,IMCommonEmpty<Any>> = object : EmptyDmw<Any,IMCommonEmpty<Any>>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMDMW
    override fun <T : Any> fmap(f: (Any) -> T): ITMap<Nothing> = TODO("internal error")
    override fun fadd(item: Any): IMWritable<Nothing> = TODO("internal error")
}

internal data class /* Discardable Wrapper, FMap */ DWFMap<out A: Any, out B: IMCommon<A>> constructor (
        val a: A, val b: IMCommon<A>
    ): IMOrdered<A> by (@Suppress("UNCHECKED_CAST") (b as IMOrdered<A>)), IMDimw<A, IMCommon<A>> {

    override val seal: IMSC = IMSC.IMDMW
    override fun fpopAndRemainder(): Pair<A?, IMOrdered<A>> = Pair(a, empty())
    override fun toEmpty(): ITDmw<A> = empty()
    override fun fadd(item: @UnsafeVariance A): ITDmw<A> =
        of(a)

    override fun <T : Any> fmap(f: (A) -> T): IMMapOp<T, IMCommon<T>>
        = of(f(this.a))

    override fun <B: Any> fzip(items: IMOrdered<B>): IMDimw<Pair<A, B>, IMCommon<Pair<A, B>>> {
        val itn: B? = items.fnext()
        return when (itn) {
            null -> empty()
            else -> of(Pair(a,itn))
        }
    }

    companion object {
        fun <A: Any> of(a: ITMap<A>): ITMap<A> = a
        fun <A: Any> of(a: IMDisw<A, IMCommon<A>>): IMDimw<A, IMCommon<A>> {
            check(a !is IMMapOp<*,*>)
            return if (a.fempty()) empty() else {
                (@Suppress("UNCHECKED_CAST") (a as DWCommon<A>))
                DWFMap(a.a, DWCommon.of(a.a))
            }
        }
        fun <A: Any> of(a: IMCommon<A>): ITMap<A> = when {
            a is IMMapOp<*,*> -> @Suppress("UNCHECKED_CAST") (a as ITMap<A>)
            a.fempty() -> empty()
            else -> IMMapOp.flift2map(a) ?: if (1 == a.fsize()) DWFMap(a.fpick()!!, a) else empty()
        }
        fun <A: Any> of(a: A): IMDimw<A, IMCommon<A>> {
            check((a !is IMMapOp<*,*>) && (a !is IMCommon<*>))
            return DWFMap(a, DWCommon.of(a))
        }
        fun <A: Any> empty(): IMDimw<A, IMCommonEmpty<A>> = @Suppress("UNCHECKED_CAST") (emptyDmw as IMDimw<A, IMCommonEmpty<A>>)
    }

}

private interface EmptyDaw<out A: Any, out B: EmptyDmw<A, IMCommonEmpty<A>>>: IMOrderedEmpty<A>, IMDiaw<A, IMCommonEmpty<A>> {
    override fun <T: Any> fapp(op: (ITMap<A>) -> ITMap<T>): ITMapp<Nothing> = TODO("internal error")
}

private val emptyDaw: EmptyDaw<Any, EmptyDmw<Any,IMCommonEmpty<Any>>> = object: EmptyDaw<Any, EmptyDmw<Any,IMCommonEmpty<Any>>>, IMCommonEmpty.Companion.IMCommonEmptyEquality() {
    override val seal: IMSC = IMSC.IMDAW
    override fun <B : Any> fzip(items: IMOrdered<B>): IMOrdered<Nothing> = TODO("internal error")
    override fun <T : Any> fmap(f: (Any) -> T): ITMap<Nothing> = TODO("internal error")
    override fun fadd(item: Any): IMWritable<Nothing> = TODO("internal error")
}

internal data class /* Discardable Wrapper, FMapp */ DWFMapp<out A: Any, out B: IMDiaw<A, IMCommon<A>>> constructor (
    val a: A, val b: ITMap<A>
): IMOrdered<A> by (@Suppress("UNCHECKED_CAST") (b as IMOrdered<A>)), IMDiaw<A,ITMap<A>>  {

    override val seal: IMSC = IMSC.IMDAW
    override fun fpopAndRemainder(): Pair<A?, IMOrdered<A>> = Pair(this.a, empty())
    override fun toEmpty(): ITDaw<A> = empty()
    override fun fadd(item: @UnsafeVariance A): ITDaw<A> = of(a)

    override fun <T: Any> fmap(f: (A) -> T): ITMap<T> = b.fmap(f)

    override fun <T : Any> fapp(op: (ITMap<A>) -> ITMap<T>): ITMapp<T> {
        val aux: ITMap<T> = op(b)
        return when {
            aux.fempty() -> empty()
            aux.fsize() == 1 -> of(aux.fpick()!!)
            else -> IMMappOp.flift2mapp(aux) ?: empty()
        }
    }

    override fun <B: Any> fzip(items: IMOrdered<B>): IMDiaw<Pair<A, B>, IMCommon<Pair<A, B>>> {

        return when (val itn: B? = items.fnext()) {
            null == itn -> empty()
            else -> {
                val res = of(Pair(a, itn))
                @Suppress("UNCHECKED_CAST") (res as IMDiaw<Pair<A, B>, IMCommon<Pair<A, B>>>)
            }
        }
    }

    companion object {
        fun <A: Any> of(a: ITMapp<A>): ITMapp<A> = a
        fun <A: Any> of(a: IMDimw<A, IMCommon<A>>): IMDiaw<A, IMCommon<A>> {
            check(a !is IMMappOp<*,*>)
            return if (a.fempty()) empty() else {
                (@Suppress("UNCHECKED_CAST") (a as DWFMap<A, IMCommon<A>>))
                DWFMapp(a.a, a)
            }
        }
        fun <A: Any> of(a: ITMap<A>): ITMapp<A> = when {
            a is IMMappOp<*,*> -> @Suppress("UNCHECKED_CAST") (a as ITMapp<A>)
            a.fempty() -> empty()
            else -> IMMappOp.flift2mapp(a) ?: if (1 == a.fsize()) DWFMapp(a.fpick()!!, a) else empty()
        }
        fun <A: Any> of(a: IMDisw<A, IMCommon<A>>): IMDiaw<A, IMCommon<A>> {
            check((a !is IMMappOp<*,*>) && (a !is IMMapOp<*,*>))
            return if (a.fempty()) empty() else {
                (@Suppress("UNCHECKED_CAST") (a as DWFMap<A, IMCommon<A>>))
                DWFMapp(a.a, DWFMap.of(a.a))
            }
        }
        fun <A: Any> of(a: IMCommon<A>): ITMapp<A> = when {
            a is IMMappOp<*,*> -> @Suppress("UNCHECKED_CAST") (a as ITMapp<A>)
            a is IMMapOp<*,*> -> {
                @Suppress("UNCHECKED_CAST") (a as ITMap<A>)
                IMMappOp.flift2mapp(a) ?: if (1 == a.fsize()) DWFMapp(a.fpick()!!, a) else empty()
            }
            a.fempty() -> empty()
            else -> IMMappOp.flift2mapp(a) ?: if (1 == a.fsize()) DWFMapp(a.fpick()!!, DWFMap.of(a)) else empty()
        }
        fun <A: Any> of(a: A): IMDiaw<A, IMCommon<A>> {
            check((a !is IMMappOp<*,*>) && (a !is IMMapOp<*,*>) && (a !is IMCommon<*>))
            return DWFMapp(a, DWFMap.of(a))
        }
        fun <A: Any> empty(): IMDiaw<A, IMCommonEmpty<A>> = @Suppress("UNCHECKED_CAST") (emptyDaw as IMDiaw<A, IMCommonEmpty<A>>)
    }
}