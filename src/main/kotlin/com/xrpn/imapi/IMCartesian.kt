package com.xrpn.imapi

import com.xrpn.immutable.FList.Companion.emptyIMList

internal interface ZipWrapMarker

class FCartesian<out S: Any, out U: ITMap<S>, out T: Any> private constructor (private val u: U): IMCartesian<S, U, T, Pair<@UnsafeVariance S, @UnsafeVariance T>> {

    override fun mpro(t: ITMap<@UnsafeVariance T>): ITMap<Pair<S, T>>? = when {
        u.fempty() || t.fempty() -> DWFMap.empty()
        else -> when (val tc = t.asIMCommon<T>()) {
            null -> null
            is IMOrdered<*> -> try {
                @Suppress("UNCHECKED_CAST") (tc as IMOrdered<T>)
                ZipWrap(u, tc)
            } catch (ex: ClassCastException) {
                IMSystemErrLogging(this::class)
                    .emitUnconditionally(
                        "fail with  ${ex::class.simpleName}(${ex.message}) for ${t::class} as $t"
                    )
                null
            }
            else -> null
        }
    }

    override fun opro(t: IMOrdered<@UnsafeVariance T>): ITMap<Pair<S, T>>? = when {
        u.fempty() || t is IMOrderedEmpty<*> -> DWFMap.empty()
        else -> {
            val tmap = t.asIMCommon<T>()?.let { IMMapOp.flift2map(it) }
            tmap?.let { mpro(it) }
        }
    }


    companion object {
        fun <S : Any, T : Any> of(item: IMOrdered<S>): IMCartesian<S, ITMap<S>, T, Pair<S, T>> = when (item) {
//            is IMCommon<*> -> {
//                val tmap = (@Suppress("UNCHECKED_CAST") (IMMapOp.flift2map(item) as ITMap<S>?))!!
//                FCartesian(tmap)
//            }
            is IMZipMap<*, *, *> -> {
                val tmap = @Suppress("UNCHECKED_CAST") (item.asMap() as ITMap<S>)
                FCartesian(tmap)
            }
            else -> {
//                val tmap = (@Suppress("UNCHECKED_CAST") (IMMapOp.flift2map(item) as ITMap<S>?))!!
//                FCartesian(tmap)
                FCartesian(IMMapOp.flift2map(item)!!)
            }
        }

        fun <S : Any, T : Any, W : Pair<S, T>> asZMap(k: ITMap<W>?): ITZMap<S, T>? = when {
            k == null -> null
            k is ZipWrap<*, *, *, W> -> {
                fun placateWarn(a: Any): ZipWrap<S, ITMap<S>, T, W> =
                    @Suppress("UNCHECKED_CAST") (a as ZipWrap<S, ITMap<S>, T, W>)
                ZipWrap.toZMap(placateWarn(k))
            }
            else -> null
        }
    }
}

fun <S: Any, T: Any, W: Pair<S,T>> zipMaps(lead: ITMap<S>, after: ITMap<T>): ITMap<W> {
    val nameless = object : ITZMap<S, T> {
        val zm: ITZMap<S, T>? by lazy {
            val proxyLead: IMOrdered<S> = when(lead) {
                is ZipWrapMarker -> {
                    (@Suppress("UNCHECKED_CAST") (lead as ITMap<Pair<Any,Any>>))
                    val aux = FCartesian.asZMap(lead)!!.asIMOrdered()
                    @Suppress("UNCHECKED_CAST") (aux as IMOrdered<S>)
                }
                else -> @Suppress("UNCHECKED_CAST") (lead as IMOrdered<S>)
            }
            val fkart: ITMap<Pair<S, T>>? = IMCartesian.flift2kart<S, T>(proxyLead).mpro(after)
            IMCartesian.asZMap(fkart)
        }

        override fun asMap(): ITMap<Pair<S, T>> = zm?.asMap() ?: DWFMap.empty()
        override fun asIMOrdered(): IMOrdered<Pair<S, T>> = zm?.asIMOrdered() ?: DWFMap.empty()
        override fun <X : Any> fzipMap(f: (S) -> (T) -> X): ITMap<X> = zm?.fzipMap(f) ?: DWFMap.empty()
        override fun <X : Any> fkartMap(f: (S) -> (T) -> X): ITMap<ITMap<X>> = zm?.fkartMap(f) ?: DWFMap.empty()
    }.asMap()
    return (@Suppress("UNCHECKED_CAST") (nameless as ITMap<W>))
}

internal fun <S: Any, T: Any, W: Pair<S,T>> toEmptyZipMap(seed: ITMap<Any>): ITMap<W> {
    check(seed is IMOrdered<*>)
    val empty = seed.toEmpty()
    val nameless = object : ITZMap<S,T> {
        override fun asMap(): ITMap<Pair<S,T>> = @Suppress("UNCHECKED_CAST") (empty as ITMap<W>)
        override fun asIMOrdered(): IMOrdered<Pair<S,T>> = @Suppress("UNCHECKED_CAST") (empty as IMOrdered<Pair<S,T>>)
        override fun <X: Any> fzipMap(f: (S) -> (T) -> X): ITMap<X> = @Suppress("UNCHECKED_CAST") (empty as ITMap<X>)
        override fun <X: Any> fkartMap(f: (S) -> (T) -> X ): ITMap<ITMap<X>> = @Suppress("UNCHECKED_CAST") (empty as ITMap<ITMap<X>>)
    }.asMap()
    return (@Suppress("UNCHECKED_CAST") (nameless as ITMap<W>))
}

private data class ZipWrap<out S: Any, out U: ITMap<S>, out T: Any, out W: Pair<S,T>>(val s: U, val t: IMOrdered<T>): ITMap<W>, ZipWrapMarker {

    init {
        check(s is IMOrdered<*>)
        check(t is ITMap<*>)
    }

    val tmap: ITMap<T> = IMMapOp.flift2map(@Suppress("UNCHECKED_CAST") (t as IMCommon<T>))!!
    val sorder = @Suppress("UNCHECKED_CAST") (s as IMOrdered<S>)
    val product: IMOrdered<Pair<S, T>> by lazy { sorder.fzip(t) }
    val pcomm: IMCommon<Pair<S, T>> by lazy { product.asIMCommon<Pair<S, T>>()!! }

    private fun w2p(f: (W) -> Boolean): (Pair<S,T>) -> Boolean = { p: Pair<S,T> ->
        @Suppress("UNCHECKED_CAST") (p as W)
        f(p)
    }

    private fun cp2cw(cp: IMCommon<Pair<S,T>>): IMCommon<W> {
        @Suppress("UNCHECKED_CAST") (cp as IMCommon<W>)
        return cp
    }

    private fun p2w(p: Pair<S,T>?): W? = p?.let {
        @Suppress("UNCHECKED_CAST") (p as W)
        return p
    }

    private fun <X: Any> fop2cry (f:(W) -> X): (S) -> (T) -> X = { s: S -> { t:T -> f(p2w(Pair(s,t))!!) } }

    private fun crRecast(g: ((Pair<S,T>) -> Boolean) -> IMCommon<Pair<S,T>>): ((W) -> Boolean) -> IMCommon<W> = { h: (W) -> Boolean -> cp2cw(g(w2p(h))) }

    private fun rRecast(g: ((Pair<S,T>) -> Boolean) -> Pair<S,T>?): ((W) -> Boolean) -> W? = { h: (W) -> Boolean -> p2w(g(w2p(h))) }

    override val seal: IMSC = IMSC.IMKART

    override fun fcontains(item: @UnsafeVariance W): Boolean =
        pcomm.fcontains(item)

    override fun fcount(isMatch: (W) -> Boolean): Int =
        pcomm.fcount(w2p(isMatch))

    override fun fdropAll(items: IMCommon<@UnsafeVariance W>): IMCommon<W> =
        cp2cw(pcomm.fdropAll(items))

    override fun fdropItem(item: @UnsafeVariance W): IMCommon<W> =
        cp2cw(pcomm.fdropItem(item))

    override fun fdropWhen(isMatch: (W) -> Boolean): IMCommon<W> =
        crRecast(pcomm::fdropWhen)(isMatch)

    override fun ffilter(isMatch: (W) -> Boolean): IMCommon<W> =
        crRecast(pcomm::ffilter)(isMatch)

    override fun ffilterNot(isMatch: (W) -> Boolean): IMCommon<W> =
        crRecast(pcomm::ffilterNot)(isMatch)

    override fun ffindAny(isMatch: (W) -> Boolean): W? =
        rRecast(pcomm::ffindAny)(isMatch)

    override fun <R> ffold(z: R, f: (acc: R, W) -> R): R {
        val ff: (R) -> (Pair<S,T>) -> R  = { acc: R -> { st: Pair<S,T> -> f(acc, p2w(st)!! ) } }
        return pcomm.ffold(z) { acc, st: Pair<S, T> -> ff(acc)(st) }
    }

    override fun fisStrict(): Boolean =
        pcomm.fisStrict()

    override fun fpick(): W? =
        p2w(pcomm.fpick())

    override fun fpopAndRemainder(): Pair<W?, IMCommon<W>> {
        val (p, ps) = pcomm.fpopAndRemainder()
        return Pair(p2w(p), cp2cw(ps))
    }

    override fun fsize(): Int =
        pcomm.fsize()

    override fun <X: Any> fmap(f: (W) -> X): ITMap<X> =
        zmap.fzipMap(fop2cry(f))

    override fun equals(other: Any?): Boolean =
        if (this === other) true else product.equals(other)

    override fun hashCode(): Int =
        pcomm.hashCode()

    override fun toEmpty(): IMCommon<W> =
        DWFMap.empty()

    private val zmap: ITZMap<S,T> = object : ITZMap<S,T> {
        override fun asMap(): ITMap<W> = this@ZipWrap
        override fun asIMOrdered(): IMOrdered<Pair<S, T>> = product
        override fun <X: Any> fzipMap(f: (S) -> (T) -> X): ITMap<X> = when (val maybeAcc = pcomm.toEmpty() ) {
            is IMWritable<*> -> {
                val racc = @Suppress("UNCHECKED_CAST") (maybeAcc as IMWritable<X>)
                val res = @Suppress("UNCHECKED_CAST") (pcomm.ffold(racc) { acc, st -> acc.fadd(f(st.first)(st.second)) } as ITMap<X>)
                res
            }
            else -> {
                val res = pcomm.ffold(emptyIMList<X>()) { acc, st -> acc.fprepend(f(st.first)(st.second)) }
                res.freverse()
            }
        }
        override fun <X: Any> fkartMap(f: (S) -> (T) -> X ): ITMap<ITMap<X>> =
            s.fmap { sval -> f(sval) }.fmap { t2x -> tmap.fmap(t2x) }

    }

    companion object {
        fun <S: Any, T: Any> toZMap(zr: ZipWrap<S, ITMap<S>, T, Pair<S,T>>): ITZMap<S, T> = zr.zmap
    }

}

/*
class FCartesian<out S: Any, out U: FMap<S>, out T: Any, out V: FMap<T>> private constructor (private val u: U): IMCartesian<S, U, T, V, Pair<@UnsafeVariance U, @UnsafeVariance V>> {

    override fun mpro(t: FMap<@UnsafeVariance T>): FMap<Pair<U,V>>? = when {
        u.fempty() || t.fempty() -> DWFMap.empty()
        u.fsize() != t.fsize() -> null
        else -> when (val tc = t.asIMCommon<T>()) {
            null -> null
            is IMOrdered<*> -> try {
                @Suppress("UNCHECKED_CAST") (tc as IMOrdered<T>)
                ProductWrap(u, tc)
            } catch (ex: ClassCastException) {
                IMSystemErrLogging(this::class).emitUnconditionally("fail with  ${ex::class.simpleName}(${ex.message}) for ${t::class} as $t")
                null
            }
            else -> null
        }
    }

    override fun opro(t: IMOrdered<@UnsafeVariance T>): FMap<Pair<U,V>>? = when {
        u.fempty() || t is IMOrderedEmpty<*> -> DWFMap.empty()
        else -> {
            val tmap = t.asIMCommon<T>()?.let { IMMapOp.flift2map(it) }
            tmap?.let { mpro(it) }
        }
    }


    companion object {
        fun <S: Any, T:Any> of(item: IMOrdered<S>): IMCartesian<S, FMap<S>, T, FMap<T>, Pair<FMap<S>, FMap<T>>> {
            check(item is IMCommon<*>)
            val tmap = (@Suppress("UNCHECKED_CAST") (IMMapOp.flift2map(item) as FMap<S>?))!!
            return FCartesian(tmap)
        }
    }
}

*/

private data class MapWrap<out S: Any, out U: ITMap<S>, out T: Any, V: ITMap<T>, out W: Pair<U,V>>(val uOfS: U, val t: IMOrdered<T>): ITMap<W> {

    init {
        check(uOfS is IMOrdered<*>)
        check(t is ITMap<*>)
    }

    val tmap = IMMapOp.flift2map(@Suppress("UNCHECKED_CAST") (t as IMCommon<T>))
    val vOfT = @Suppress("UNCHECKED_CAST") (tmap as V)
//    val sorder = @Suppress("UNCHECKED_CAST") (uOfS as IMOrdered<S>)
//    val product: IMOrdered<Pair<U, V>> = @Suppress("UNCHECKED_CAST") (sorder.fzip(t) as IMOrdered<Pair<U,V>>)
//    val pcomm = product.asIMCommon<Pair<U, V>>()!!
    val pcomm = Pair(uOfS,vOfT)
    val w = @Suppress("UNCHECKED_CAST") (pcomm as W)

    private fun w2p(f: (W) -> Boolean): (Pair<U,V>) -> Boolean = { p: Pair<U,V> ->
        @Suppress("UNCHECKED_CAST") (p as W)
        f(p)
    }

    private fun cp2cw(cp: IMCommon<Pair<U,V>>): IMCommon<W> {
        @Suppress("UNCHECKED_CAST") (cp as IMCommon<W>)
        return cp
    }

    private fun p2w(p: Pair<U,V>?): W? = p?.let {
        @Suppress("UNCHECKED_CAST") (p as W)
        return p
    }

//    private fun crRecast(g: ((Pair<U,V>) -> Boolean) -> IMCommon<Pair<U,V>>): ((W) -> Boolean) -> IMCommon<W> = { h: (W) -> Boolean -> cp2cw(g(w2p(h))) }
//
//    private fun rRecast(g: ((Pair<U,V>) -> Boolean) -> Pair<U,V>?): ((W) -> Boolean) -> W? = { h: (W) -> Boolean -> p2w(g(w2p(h))) }

    override val seal: IMSC = IMSC.IMKART

    override fun fcontains(item: @UnsafeVariance W): Boolean = TODO()
//        pcomm.fcontains(item)

    override fun fcount(isMatch: (W) -> Boolean): Int = TODO()
//        pcomm.fcount(w2p(isMatch))

    override fun fdropAll(items: IMCommon<@UnsafeVariance W>): IMCommon<W> = TODO()
//        cp2cw(pcomm.fdropAll(items))

    override fun fdropItem(item: @UnsafeVariance W): IMCommon<W> = TODO()
//        cp2cw(pcomm.fdropItem(item))

    override fun fdropWhen(isMatch: (W) -> Boolean): IMCommon<W> = TODO()
//        crRecast(pcomm::fdropWhen)(isMatch)

    override fun ffilter(isMatch: (W) -> Boolean): IMCommon<W> = TODO()
//        crRecast(pcomm::ffilter)(isMatch)

    override fun ffilterNot(isMatch: (W) -> Boolean): IMCommon<W> = TODO()
//        crRecast(pcomm::ffilterNot)(isMatch)

    override fun ffindAny(isMatch: (W) -> Boolean): W? = TODO()
//        rRecast(pcomm::ffindAny)(isMatch)

    override fun <R> ffold(z: R, f: (acc: R, W) -> R): R {
        TODO("Not yet implemented")
    }

    override fun fisStrict(): Boolean =
        pcomm.first.fisStrict() && pcomm.second.fisStrict()

    override fun fpick(): W? =
        p2w(pcomm)

    override fun fpopAndRemainder(): Pair<W?, IMCommon<W>> {
        return Pair(p2w(pcomm), DWCommon.empty())
    }

    override fun fsize(): Int = 1

    override fun <X: Any> fmap(f: (W) -> X): ITMap<X> =
        IMMapOp.flift2map(f(w))

    fun <X: Any> fkmap(f: (S) -> (T) -> X ): ITMap<ITMap<X>> =
        pcomm.first.fmap { sval -> f(sval) }.fmap { t2x -> pcomm.second.fmap(t2x) }

    override fun equals(other: Any?): Boolean =
        if (this === other) true else pcomm.equals(other)

    override fun hashCode(): Int =
        pcomm.hashCode()

    override fun toEmpty(): IMCommon<W> = DWFMap.empty()

    fun asMap(): ITMap<W> = this

    fun asIMOrdered(): IMOrdered<W> = DWCommon.of(w)

}
