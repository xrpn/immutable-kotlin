package com.xrpn.imapi

class FCartesian<out S: Any, out U: FMap<S>, out T: Any> private constructor (private val u: U): IMCartesian<S, U, T, Pair<@UnsafeVariance S, @UnsafeVariance T>> {

    override fun mpro(t: FMap<@UnsafeVariance T>): FMap<Pair<S,T>>? = when {
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

    override fun opro(t: IMOrdered<@UnsafeVariance T>): FMap<Pair<S,T>>? = when {
        u.fempty() || t is IMOrderedEmpty<*> -> DWFMap.empty()
        else -> {
            val tmap = t.asIMCommon<T>()?.let { IMMapOp.flift2map(it) }
            tmap?.let { mpro(it) }
        }
    }


    companion object {
        fun <S: Any, T:Any> of(item: IMOrdered<S>): IMCartesian<S, FMap<S>, T, Pair<S,T>> {
            check(item is IMCommon<*>)
            val tmap = (@Suppress("UNCHECKED_CAST") (IMMapOp.flift2map(item) as FMap<S>?))!!
            return FCartesian(tmap)
        }
    }
}

private data class ProductWrap<out S: Any, out U: FMap<S>, out T: Any, out W: Pair<S,T>>(val s: U, val t: IMOrdered<T>): FMap<W> {

    init {
        check(s is IMOrdered<*>)
        check(t is FMap<*>)
    }

    val tmap = IMMapOp.flift2map(@Suppress("UNCHECKED_CAST") (t as IMCommon<T>))
    val sorder = @Suppress("UNCHECKED_CAST") (s as IMOrdered<S>)
    val product: IMOrdered<Pair<S, T>> = sorder.fzip(t)
    val pcomm = product.asIMCommon<Pair<S, T>>()!!

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
        TODO("Not yet implemented")
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

    override fun <X: Any> fmap(f: (W) -> X): FMap<X> =
        IMMapOp.flift2map(pcomm)!!.fmap { it: Pair<S, T> ->
            @Suppress("UNCHECKED_CAST") (it as W)
            f(it)
        }

    override fun equals(other: Any?): Boolean =
        if (this === other) true else product.equals(other)

    override fun hashCode(): Int {
        return pcomm.hashCode()
    }


}

