package com.xrpn.imapi

import com.xrpn.immutable.FList.Companion.emptyIMList

/* A ZipMap factory */
class FCartesian<out S: Any, out U: ITMap<S>, out T: Any, W:IMZPair<@UnsafeVariance S, @UnsafeVariance T>> private constructor (private val u: U): IMCartesian<S, U, T, W> {

    override fun mpro(t: ITMap<@UnsafeVariance T>): ITMap<W>? = when {
        u.fempty() || t.fempty() -> emptyZipMap()
        else -> when (val tc = t.asIMCommon<T>()) {
            null -> null
            is IMOrdered<*> -> try {
                @Suppress("UNCHECKED_CAST") (tc as IMOrdered<T>)
                ZipWrap(u, tc)
            } catch (ex: ClassCastException) {
                IMSystemErrLogging(this::class).emitUnconditionally(
                "fail with  ${ex::class.simpleName}(${ex.message}) for ${t::class} as $t"
                )
                null
           }
           is IMZipWrap<*,*> -> {
               (@Suppress("UNCHECKED_CAST") (t as ITMap<IMZPair<S,T>>))
               val zm = asZMap(t)!!
               ZipWrap(u, zm.asIMOrdered())
           }
           else -> null
        }
    }

    override fun opro(t: IMOrdered<@UnsafeVariance T>): ITMap<W>? = when {
        u.fempty() || t is IMOrderedEmpty<*> ->  emptyZipMap()
        else -> {
            val tmap = t.asIMCommon<T>()?.let { IMMapOp.flift2map(it) }
            tmap?.let { mpro(it) }
        }
    }


    companion object {
        fun <S : Any, T : Any> of(item: IMOrdered<S>): IMCartesian<S, ITMap<S>, T, IMZPair<S, T>> = when (item) {
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

        fun <S: Any, T: Any, W: IMZPair<S,T>> emptyZipMap(): ITMap<W> =
            (@Suppress("UNCHECKED_CAST") (defaultEmptyZipMap.asMap() as ITMap<W>))

        fun <S: Any, T: Any> emptyZMap(): ITZMap<S,T> = defaultEmptyZipMap

        fun <S: Any, T: Any, W: IMZPair<S, T>> asZMap(k: ITMap<W>?): ITZMap<S, T>? = when {
            k == null -> null
            k is ZipWrap<*, *, *, W> -> try {
                fun placateWarn(a: Any): ZipWrap<S, ITMap<S>, T, W> =
                    @Suppress("UNCHECKED_CAST") (a as ZipWrap<S, ITMap<S>, T, W>)
                val res = ZipWrap.toZMap(placateWarn(k))
                res
            } catch (ex: ClassCastException) {
                IMSystemErrLogging(this::class).emitUnconditionally("fail with  ${ex::class.simpleName}(${ex.message}) for ${this::class} as $this")
                null
            }
            k.equals(defaultEmptyZipMap) ->
                defaultEmptyZipMap
            k.equals(defaultEmptyZipMap.empty) ->
                defaultEmptyZipMap
            else -> null
        }
    }
}

private fun <S: Any, T: Any, X: Any> stepUnwind(l: ITMap<S>, zp: ITMap<IMZPair<T,X>>): ITMap<IMZPair<IMZPair<S,T>, X>> {
    val ffm: ITMap<T> = zp.fmap { it._1() }
    val ssm: ITMap<X> = zp.fmap { it._2() }
    val aux: ITMap<IMZPair<S, T>> = zipMaps(l, ffm)
    val res: ITMap<IMZPair<IMZPair<S, T>, X>> = zipMaps(aux,ssm)
    return res
}


private fun <S: Any, T: Any, X: Any, W: IMZPair<IMZPair<S,T>, X>> zipMapWrap(
    leader: ITMap<S>,
    follower: ZipWrap<T,ITMap<T>,X,IMZPair<T,X>>
): ITMap<W> {

    val flw = if (follower.scndMap.fpick() is /* X */ IMZPair<*, *>) {
        @Suppress("UNCHECKED_CAST") (follower.scndMap as ITMap<IMZPair<*, *>>)
        //                                         v     v      formerly X
        val newFollower: ITMap<IMZPair<IMZPair<T, Any>, Any>> = stepUnwind(follower.frstMap, follower.scndMap)
        newFollower
    } else follower
    flw as ZipWrap<*,*,*,*>
    val unw = if (flw.frstMap.fpick() is IMZPair<*, *>) {
        @Suppress("UNCHECKED_CAST") (flw.frstMap as ITMap<IMZPair<*, *>>)
        val unwound: ITMap<IMZPair<IMZPair<S, Any>, Any>> = stepUnwind(leader, flw.frstMap)
        zipMaps(unwound, flw.scndMap)
    } else stepUnwind(leader, flw)

    return (@Suppress("UNCHECKED_CAST") (unw as ITMap<W>))
}


fun <S: Any, T: Any, W: IMZPair<S,T>> zipMaps(leader: ITMap<S>, follower: ITMap<T>): ITMap<W> {
    check(!(leader.fempty() || follower.fempty()))

    val nameless = object : ITZMap<S, T> {
        val zm: ITZMap<S, T>? by lazy {

            val newLeader = if (leader is ZipWrap<*, *, *, *>) {

                if (leader.scndMap.fpick() is IMZPair<*, *>) {
                    @Suppress("UNCHECKED_CAST") (leader.scndMap as ITMap<IMZPair<*, *>>)
                    stepUnwind(leader.frstMap, leader.scndMap)
                } else leader

            } else leader

            val fkart: ITMap<IMZPair<S,T>>? = when {

                follower is ZipWrap<*, *, *, *> -> zipMapWrap(newLeader, follower)
                else -> {
                    val proxyLead: IMOrdered<S> = when(newLeader) {
                        is ZipWrap<*, *, *, *> -> @Suppress("UNCHECKED_CAST") (ZipWrap.toZMap(newLeader).asIMOrdered() as IMOrdered<S>)
                        else -> @Suppress("UNCHECKED_CAST") (newLeader as IMOrdered<S>)
                    }
                    IMCartesian.flift2kart<S, T>(proxyLead).mpro(follower)
                }
            }
            IMCartesian.asZMap(fkart)
        }

        private val em = FCartesian.emptyZipMap<S,T, IMZPair<S,T>>()
        private val ezm = FCartesian.emptyZMap<S,T>()
        override fun asMap(): ITMap<IMZPair<S, T>> = zm?.asMap() ?: em
        override fun asIMOrdered(): IMOrdered<IMZPair<S, T>> = zm?.asIMOrdered() ?: ezm.asIMOrdered()
        override fun <X : Any> fzipMap(f: (S) -> (T) -> X): ITMap<X> = zm?.fzipMap(f) ?: ezm.fzipMap(f)
        override fun <X : Any> fkartMap(f: (S) -> (T) -> X): ITMap<ITMap<X>> = zm?.fkartMap(f) ?: ezm.fkartMap(f)
        override fun asZMap(): ITZMap<S, T> = this
    }.asMap()
    return (@Suppress("UNCHECKED_CAST") (nameless as ITMap<W>))
}

private interface EmptyZipWrap<out S: Any, out U: ITMap<S>, out T: Any, out W: IMZPair<S,T>>: IMMapOp<W, IMCommonEmpty<W>>, IMOrderedEmpty<W>

private val _emptyZipWrap = object : EmptyZipWrap<Nothing, ITMap<Nothing>, Nothing, IMZPair<Nothing,Nothing>> {
    override val seal: IMSC = IMSC.IMKART
    override fun <B : Any> fzip(items: IMOrdered<B>): IMOrdered<Pair<IMZPair<Nothing, Nothing>, B>> =
        @Suppress("UNCHECKED_CAST") (this as IMOrdered<Pair<IMZPair<Nothing, Nothing>, B>>)
    override fun <T : Any> fmap(f: (IMZPair<Nothing, Nothing>) -> T): ITMap<T> =
        @Suppress("UNCHECKED_CAST") (this as ITMap<T>)
    val show = "${ZipWrap::class.simpleName}'@'(*)"
}

private val defaultEmptyZipMap = object : ITZMap<Nothing,Nothing> {
    val empty = _emptyZipWrap
    val show = _emptyZipWrap.show
    val hash = show.hashCode()
    override fun asMap(): ITMap<IMZPair<Nothing,Nothing>> = empty
    override fun asIMOrdered(): IMOrdered<IMZPair<Nothing,Nothing>> = empty
    override fun <X: Any> fzipMap(f: (Nothing) -> (Nothing) -> X): ITMap<Nothing> =
        @Suppress("UNCHECKED_CAST") (empty as ITMap<Nothing>)
    override fun <X: Any> fkartMap(f: (Nothing) -> (Nothing) -> X): ITMap<ITMap<Nothing>> =
        @Suppress("UNCHECKED_CAST") (empty as ITMap<Nothing>)
    override fun equals(other: Any?): Boolean = other?.equals(this) ?: false
    override fun hashCode(): Int = hash
    override fun toString(): String = show
    override fun asZMap(): ITZMap<Nothing, Nothing> = this
}

internal data class ZW<out A: Any, out B: Any>(val ff: A, val ss: B): IMZPair<A,B> {
    override fun _1(): A = ff
    override fun _2(): B = ss
    override fun toPair(): Pair<A,B> = Pair(ff,ss)

    companion object {
        fun <A: Any, B: Any> of(p: Pair<A,B>): ZW<A,B> = ZW(p.first, p.second)
    }
}

private data class ZipWrap<out S: Any, out U: ITMap<S>, out T: Any, out W: IMZPair<S,T>>(val frstMap: U, val scndOrd: IMOrdered<T>): ITMap<W>, IMZipWrap<S,T> {

    init {
        check(frstMap is IMOrdered<*>)
        check(scndOrd is ITMap<*>)
    }

    val scndMap: ITMap<T> = IMMapOp.flift2map(scndOrd)!!
    val frstOrd = @Suppress("UNCHECKED_CAST") (frstMap as IMOrdered<S>)
    private val product: IMOrdered<Pair<S, T>> by lazy { frstOrd.fzip(scndOrd) }
    val iproduct: IMOrdered<ZW<S, T>> by lazy {
        val aux: ITMap<ZW<S, T>> = IMMapOp.flift2map(product)!!.fmap { ZW(it.first, it.second) }
        @Suppress("UNCHECKED_CAST") ((if (aux.fempty()) defaultEmptyZipMap.asMap() else aux) as IMOrdered<ZW<S, T>>)
    }

    private fun w2zwf(f: (W) -> Boolean): (ZW<S,T>) -> Boolean = { p: ZW<S,T> ->
        @Suppress("UNCHECKED_CAST") (p as W)
        f(p)
    }

    private fun czw2cw(cp: IMCommon<ZW<S,T>>): ITMap<W> =
        @Suppress("UNCHECKED_CAST") ((if (cp.fempty()) defaultEmptyZipMap.asMap() else cp) as ITMap<W>)

    private fun zw2w(p: ZW<S,T>?): W? = p?.let {
        @Suppress("UNCHECKED_CAST") (p as W)
        return p
    }

    private fun cw2czw(cp: IMCommon<W>): ITMap<ZW<S,T>> =
        @Suppress("UNCHECKED_CAST") ((if (cp.fempty()) defaultEmptyZipMap.asMap() else cp) as ITMap<ZW<S,T>>)

    private fun w2zw(w: W?): ZW<S,T>? = w?.let {
        @Suppress("UNCHECKED_CAST") (w as ZW<S,T>)
        return w
    }

    private fun <X: Any> fop2cry (f:(W) -> X): (S) -> (T) -> X = { s: S -> { t:T -> f(zw2w(ZW(s,t))!!) } }

    private fun crRecast(g: ((ZW<S,T>) -> Boolean) -> IMCommon<ZW<S,T>>): ((W) -> Boolean) -> ITMap<W> = { h: (W) -> Boolean -> czw2cw(g(w2zwf(h))) }

    private fun rRecast(g: ((ZW<S,T>) -> Boolean) -> ZW<S,T>?): ((W) -> Boolean) -> W? = { h: (W) -> Boolean -> zw2w(g(w2zwf(h))) }

    override val seal: IMSC = IMSC.IMKART

    override fun fcontains(item: @UnsafeVariance W?): Boolean =
        item?.let{ iproduct.fcontains(w2zw(it)!!) } ?: false

    override fun fcount(isMatch: (W) -> Boolean): Int =
        iproduct.fcount(w2zwf(isMatch))

    override fun fdropAll(items: IMCommon<@UnsafeVariance W>): ITMap<W> =
        czw2cw(iproduct.fdropAll(cw2czw(items)))

    override fun fdropItem(item: @UnsafeVariance W): ITMap<W> =
        czw2cw(iproduct.fdropItem(w2zw(item)!!))

    override fun fdropWhen(isMatch: (W) -> Boolean): ITMap<W> =
        crRecast(iproduct::fdropWhen)(isMatch).run {
            if (fempty()) @Suppress("UNCHECKED_CAST") (defaultEmptyZipMap.asMap() as ITMap<W>) else this
        }

    override fun ffilter(isMatch: (W) -> Boolean): ITMap<W> =
        crRecast(iproduct::ffilter)(isMatch).run {
            if (fempty()) @Suppress("UNCHECKED_CAST") (defaultEmptyZipMap.asMap() as ITMap<W>) else this
        }

    override fun ffilterNot(isMatch: (W) -> Boolean): ITMap<W> =
        crRecast(iproduct::ffilterNot)(isMatch).run {
            if (fempty()) @Suppress("UNCHECKED_CAST") (defaultEmptyZipMap.asMap() as ITMap<W>) else this
        }

    override fun ffindAny(isMatch: (W) -> Boolean): W? =
        rRecast(iproduct::ffindAny)(isMatch)

    override fun <R> ffold(z: R, f: (acc: R, W) -> R): R {
        val ff: (R) -> (ZW<S,T>) -> R  = { acc: R -> { st: ZW<S,T> -> f(acc, zw2w(st)!!) } }
        return iproduct.ffold(z) { acc, st: ZW<S, T> -> ff(acc)(st) }
    }

    override fun fisStrict(): Boolean =
        iproduct.fisStrict()

    override fun fpick(): W? =
        zw2w(iproduct.fpick())

    override fun fpopAndRemainder(): Pair<W?, IMCommon<W>> {
        val (p, ps) = iproduct.fpopAndRemainder()
        return Pair(zw2w(p), czw2cw(ps))
    }

    override fun fsize(): Int =
        iproduct.fsize()

    override fun <X: Any> fmap(f: (W) -> X): ITMap<X> =
        zmap.fzipMap(fop2cry(f))

    override fun equals(other: Any?): Boolean = when {
        other is IMZipWrap<*,*> -> zmap.equals(other)
        else -> false
    }

    override fun softEqual(rhs: Any?): Boolean =
        equals(rhs) || zmap.softEqual(rhs)

    override fun hashCode(): Int =
        zmap.hashCode()

    override fun toString(): String =
        zmap.toString()

    override fun toEmpty(): IMCommon<W> =
        czw2cw(iproduct.toEmpty())

    override fun asZMap(): ITZMap<S,T> = zmap

    private val zmap: ITZMap<S,T> = object : ITZMap<S,T> {
        override fun asMap(): ITMap<W> = this@ZipWrap
        override fun asZMap(): ITZMap<S, T> = this
        override fun asIMOrdered(): IMOrdered<ZW<S, T>> =
            if (asMap().fempty()) TODO()
            else iproduct

        override fun <X: Any> fzipMap(f: (S) -> (T) -> X): ITMap<X> = when (val maybeAcc = iproduct.toEmpty() ) {
                is IMWritable -> {
                    val res = @Suppress("UNCHECKED_CAST") (iproduct.ffold(maybeAcc) { acc, st ->
                        val x: X = f(st.ff)(st.ss)
                        val intermediate: IMCommon<Any>? = acc.fadd(x, acc)
                        val composite = if (intermediate is IMWritable) intermediate else throw RuntimeException("impossible branch")
                        TODO("the compiler freaks out here: ${composite}")
                    } as ITMap<X>)
                    if (res.fempty()) @Suppress("UNCHECKED_CAST") (defaultEmptyZipMap.asMap() as ITMap<X>) else res
                }
                else -> {
                    val res = iproduct.ffold(emptyIMList<X>()) { acc, st -> acc.fprepend(f(st.ff)(st.ss)) }
                    if (res.fempty()) @Suppress("UNCHECKED_CAST") (defaultEmptyZipMap.asMap() as ITMap<X>) else res.freverse()
                }
            }

        override fun <X: Any> fkartMap(f: (S) -> (T) -> X ): ITMap<ITMap<X>> =
            frstMap.fmap { sval -> f(sval) }.fmap { t2x -> scndMap.fmap(t2x) }.run {
                if (fempty()) @Suppress("UNCHECKED_CAST") (defaultEmptyZipMap.asMap() as ITMap<ITMap<X>>) else this
            }

        override fun equals(other: Any?): Boolean = when {
                this === other -> true
                other is ZipWrap<*, *, *, *> -> if (asMap().fempty()) other.fempty() else iproduct.equals(other.iproduct)
                else -> false
            }

        val hash = iproduct.hashCode()
        override fun hashCode(): Int = hash

        val show: String by lazy {
            val aux = iproduct.toString()
            "${this@ZipWrap::class.simpleName}${aux.dropWhile{ it != '@' }}"
        }
        override fun toString(): String = show

    }

    companion object {
        fun <S: Any, T: Any> toZMap(zr: ZipWrap<S, ITMap<S>, T, IMZPair<S,T>>): ITZMap<S, T> = zr.zmap
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
*/
