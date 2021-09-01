package com.xrpn.immutable

import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.IMList
import kotlin.reflect.KClass


internal fun <A, B:Any> TKVEntry<A, B>.fitKeyOnly(key: A): FBTFIT where A: Any, A: Comparable<A> = fitKeyToEntry(key,this)

internal fun <A, B:Any> fitKeyToEntry(key: A, entry: TKVEntry<A, B>): FBTFIT where A: Any, A: Comparable<A> {
    val outcome = entry.vc?.compare(key, entry.getk()) ?: key.compareTo(entry.getk())
    return when {
        outcome == 0 -> FBTFIT.EQ
        outcome < 0 -> FBTFIT.LEFT
        else -> FBTFIT.RIGHT
    }
}

interface TKVEntry<out A, out B: Any>: Comparable<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>, Map.Entry<A, B> where A: Any, A: Comparable<@UnsafeVariance A> {

    val vc: Comparator<@UnsafeVariance A>?
        get() = null

    // the natural sorting order is Comparable<A>, unless vc (a comparator for A) is given.
    // To sort by B, create a TKVEntryK<B, B> giving it a Comparator<B>.  It is trivial to make B Comparable if a Comparator exists. (And vice-versa)
    // The current setup does not require B to be comparable -- only to have a key.
    override operator fun compareTo(other: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Int {
        val localOutcome: Int = vc?.compare(this.getk(), other.getk()) ?: this.getk().compareTo(other.getk())
        return if (vc === other.vc) localOutcome else {
            val remoteOutcome = other.vc?.compare(other.getk(), this.getk()) ?: other.getk().compareTo(this.getk())
            if (localOutcome == -remoteOutcome) localOutcome else
                throw IllegalStateException(TKVEntryK.CANNOT_COMPARE)
        }
    }

    fun getk(): A
    fun getkc(): Comparable<@UnsafeVariance A>
    fun getv(): B
    fun copy(): TKVEntry<A,B>

    companion object {
        fun <A: Comparable<A>, B: Any> of (key:A, value: B): TKVEntry<A, B> = TKVEntryK(key, value)
        fun <A: Comparable<A>, B: Any> of (key:A, value: B, cc: Comparator<A>): TKVEntry<A, B> = TKVEntryK(key, value, cc)
        fun <A: Comparable<A>, B: Any> of (p: Pair<A, B>): TKVEntry<A, B> = TKVEntryK(p.first, p.second)
        fun <A: Comparable<A>, B: Any> of (p: Pair<A, B>, cc: Comparator<A>): TKVEntry<A, B> = TKVEntryK(p.first, p.second, cc)
        fun <A: Comparable<A>, B: Any> of (me: Map.Entry<A, B>): TKVEntry<A, B> = if(me is TKVEntry<A,B>) me else TKVEntryK(me.key, me.value)
        fun <A: Comparable<A>, B: Any> of (me: Map.Entry<A, B>, cc: Comparator<A>): TKVEntry<A, B> = TKVEntryK(me.key, me.value, cc)
        fun <B: Any> ofIntKey (item: B): TKVEntry<Int, B> = TKVEntryK(item.hashCode(), item)
        fun <B: Any> ofStrKey (item: B): TKVEntry<String, B> = TKVEntryK(item.toString(), item)
        fun <T: Any> T.toIAEntry(): TKVEntry<Int, T> = ofIntKey(this)
        fun <T: Any> T.toSAEntry(): TKVEntry<String, T> = ofStrKey(this)
        fun <B: Any> intKeyOf(item: B): Int = item.hashCode()
        fun <B: Any> strKeyOf(item: B): String = item.toString()
        fun <B: Any> IMList<B>.toIAEntries(): IMList<TKVEntry<Int, B>> = this.fmap { a -> ofIntKey(a) }
        fun <B: Any> IMList<B>.toSAEntries(): IMList<TKVEntry<String, B>> = this.fmap { a -> ofStrKey(a) }
        fun <B: Any> IMBTree<Int, B>.toIAList(): IMList<TKVEntry<Int, B>> = this.preorder()
        fun <B: Any> IMBTree<String, B>.toSAList(): IMList<TKVEntry<String, B>> = this.preorder()
        fun <B: Any> Collection<B>.toIAEntries(): FList<TKVEntry<Int, B>> = FList.ofMap(this.iterator()){ a -> ofIntKey(a) }
        fun <B: Any> Collection<B>.toSAEntries(): FList<TKVEntry<String, B>> = FList.ofMap(this.iterator()) { a -> ofStrKey(a) }
    }
}

internal data class TKVEntryK<A: Comparable<A>, B:Any> constructor (val k: A, val v: B, override val vc: Comparator<@UnsafeVariance A>? = null): TKVEntry<A,B> {

    override fun toString(): String = "[ $k:$v ]"

    override fun hashCode(): Int = when {
        k is Int -> k
        else -> k.hashCode()
    }

    private val kClass: KClass<out A> by lazy { k::class }
    private val vClass: KClass<out B> by lazy { v::class }

    private inline fun <reified Self: TKVEntryK<@UnsafeVariance A, @UnsafeVariance B>> equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is Self && this.kClass == other.kClass && this.vClass == other.vClass -> 0 == other.compareTo(this)
            else -> false
        }

    override fun equals(other: Any?): Boolean = equalsImpl<TKVEntryK<A,B>>(other)

    override fun getk(): A = k
    override fun getkc(): Comparable<A> = k
    override fun getv(): B = v
    override fun copy(): TKVEntry<A, B> = /* TODO */ this.copy(k=k, v=v)
    override val key: A
        get() = k
    override val value: B
        get() = v

    companion object {
        const val CANNOT_COMPARE = "incompatible comparators"
    }
}