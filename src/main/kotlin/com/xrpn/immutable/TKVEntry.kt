package com.xrpn.immutable

interface TKVEntry<out A, out B: Any>: Comparable<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>> where A: Any, A: Comparable<@UnsafeVariance A> {

    val vc: Comparator<@UnsafeVariance A>?
        get() = null

    override operator fun compareTo(other: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Int = vc?.compare(this.getk(), other.getk()) ?: getk().compareTo(other.getk())

    fun getk(): A
    fun getkc(): Comparable<@UnsafeVariance A>
    fun getv(): B
    fun copy(): TKVEntry<A,B>

    companion object {
        fun <A: Comparable<A>, B: Any> of (key:A, value: B): TKVEntry<A, B> = TKVEntryK(key, value)
        fun <A: Comparable<A>, B: Any> of (key:A, value: B, cc: Comparator<A>): TKVEntry<A, B> = TKVEntryK(key, value, cc)
        fun <A: Comparable<A>, B: Any> of (p: Pair<A, B>): TKVEntry<A, B> = TKVEntryK(p.first, p.second)
        fun <A: Comparable<A>, B: Any> of (p: Pair<A, B>, cc: Comparator<A>): TKVEntry<A, B> = TKVEntryK(p.first, p.second, cc)
        fun <B: Any> ofIntKey (item: B): TKVEntry<Int, B> = TKVEntryK(item.hashCode(), item)
        fun <B: Any> ofStrKey (item: B): TKVEntry<String, B> = TKVEntryK(item.toString(), item)
        fun <T: Any> T.toIAEntry(): TKVEntry<Int, T> = ofIntKey(this)
        fun <T: Any> T.toSAEntry(): TKVEntry<String, T> = ofStrKey(this)
        fun <B: Any> FList<B>.toIAEntries(): FList<TKVEntry<Int, B>> = this.fmap { a -> ofIntKey(a) }
        fun <B: Any> FList<B>.toSAEntries(): FList<TKVEntry<String, B>> = this.fmap { a -> ofStrKey(a) }
        fun <B: Any> FSet<B>.toIAEntries(): FList<TKVEntry<Int, B>> = this.fmapToList { a -> ofIntKey(a) }
        fun <B: Any> FSet<B>.toSAEntries(): FList<TKVEntry<String, B>> = this.fmapToList { a -> ofStrKey(a) }
        fun <B: Any> Collection<B>.toIAEntries(): FList<TKVEntry<Int, B>> = FList.ofMap(this.iterator()){ a -> ofIntKey(a) }
        fun <B: Any> Collection<B>.toSAEntries(): FList<TKVEntry<String, B>> = FList.ofMap(this.iterator()) { a -> ofStrKey(a) }
    }
}
/*
interface TKVEntry1<out A, out B>: TKVEntry<A, B>, Comparable<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>
where A: Any, A: Comparable<@UnsafeVariance A>, B: Any, B: Comparable<@UnsafeVariance B> {
    val vc: Comparator<@UnsafeVariance A>
    override operator fun compareTo(other: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Int = vc?.compare(this.getv(), other.getv()) ?: getv().compareTo(other.getv())

    companion object {
        fun <A, B> of (key:A, value: B): TKVEntry1<A, B> where A: Any, A: Comparable<A>, B: Any, B: Comparable<B> = TKVEntryK1(key, value, null)
        fun <A, B> of (key:A, value: B, c: Comparator<B>): TKVEntry1<A, B> where A: Any, A: Comparable<A>, B: Any, B: Comparable<B> = TKVEntryK1(key, value, c)
        fun <B> of (item: B): TKVEntry1<B, B> where B: Any, B: Comparable<B> = TKVEntryK1(item, item, null)
        fun <B> of (item: B, c: Comparator<B>): TKVEntry1<B, B> where B: Any, B: Comparable<B> = TKVEntryK1(item, item, c)
        fun <A, B> of (p: Pair<A, B>): TKVEntry1<A, B> where A: Any, A: Comparable<A>, B: Any, B: Comparable<B> = TKVEntryK1(p.first, p.second, null)
        fun <B> ofIntKey (item: B): TKVEntry1<Int, B> where B: Any, B: Comparable<B> = TKVEntryK1(item.hashCode(), item, null)
        fun <B> ofIntKey (item: B, c: Comparator<B>): TKVEntry1<Int, B> where B: Any, B: Comparable<B> = TKVEntryK1(item.hashCode(), item, c)
        fun <B> ofStrKey (item: B): TKVEntry1<String, B> where B: Any, B: Comparable<B> = TKVEntryK1(item.toString(), item, null)
        fun <B> ofStrKey (item: B, c: Comparator<B>): TKVEntry1<String, B> where B: Any, B: Comparable<B> = TKVEntryK1(item.toString(), item,c)
        fun <T> T.toIAEntry1(): TKVEntry1<Int, T> where T: Any, T: Comparable<T> = ofIntKey(this)
        fun <T> T.toSAEntry1(): TKVEntry1<String, T> where T: Any, T: Comparable<T> = ofStrKey(this)
        fun <T> T.toIAEntry1(c: Comparator<T>): TKVEntry1<Int, T> where T: Any, T: Comparable<T> = ofIntKey(this, c)
        fun <T> T.toSAEntry1(c: Comparator<T>): TKVEntry1<String, T> where T: Any, T: Comparable<T> = ofStrKey(this, c)
        fun <B> FList<B>.toIAEntries1(): FList<TKVEntry<Int, B>> where B: Any, B: Comparable<B> = this.fmap { a -> ofIntKey(a) }
        fun <B> FList<B>.toIAEntries1(c: Comparator<B>): FList<TKVEntry<Int, B>> where B: Any, B: Comparable<B> = this.fmap { a -> ofIntKey(a, c) }
        fun <B> FList<B>.toSAEntries1(): FList<TKVEntry<String, B>> where B: Any, B: Comparable<B> = this.fmap { a -> ofStrKey(a) }
        fun <B> FList<B>.toSAEntries1(c: Comparator<B>): FList<TKVEntry<String, B>> where B: Any, B: Comparable<B> = this.fmap { a -> ofStrKey(a, c) }
        fun <B> FSet<B>.toIAEntries1(): FList<TKVEntry<Int, B>> where B: Any, B: Comparable<B> = this.fmapToList { a -> ofIntKey(a) }
        fun <B> FSet<B>.toIAEntries1(c: Comparator<B>): FList<TKVEntry<Int, B>> where B: Any, B: Comparable<B> = this.fmapToList { a -> ofIntKey(a, c) }
        fun <B> FSet<B>.toSAEntries1(): FList<TKVEntry<String, B>> where B: Any, B: Comparable<B> = this.fmapToList { a -> ofStrKey(a) }
        fun <B> FSet<B>.toSAEntries1(c: Comparator<B>): FList<TKVEntry<String, B>> where B: Any, B: Comparable<B> = this.fmapToList { a -> ofStrKey(a, c) }
        fun <B> Collection<B>.toIAEntries1(): FList<TKVEntry<Int, B>> where B: Any, B: Comparable<B> = FList.ofMap(this.iterator()){ a -> ofIntKey(a) }
        fun <B> Collection<B>.toIAEntries1(c: Comparator<B>): FList<TKVEntry<Int, B>> where B: Any, B: Comparable<B> = FList.ofMap(this.iterator()){ a -> ofIntKey(a, c) }
        fun <B> Collection<B>.toSAEntries1(): FList<TKVEntry<String, B>> where B: Any, B: Comparable<B> = FList.ofMap(this.iterator()) { a -> ofStrKey(a) }
        fun <B> Collection<B>.toSAEntries1(c: Comparator<B>): FList<TKVEntry<String, B>> where B: Any, B: Comparable<B> = FList.ofMap(this.iterator()) { a -> ofStrKey(a, c) }
    }
}
*/


internal data class TKVEntryK<A: Comparable<A>, B:Any> constructor (val k: A, val v: B, override val vc: Comparator<@UnsafeVariance A>? = null): TKVEntry<A,B> {

    override fun toString(): String = "[ $k:$v ]"

    override fun hashCode(): Int = when {
        k is Int -> k
        else -> k.hashCode()
    }

    private inline fun <reified Self: TKVEntryK<@UnsafeVariance A, @UnsafeVariance B>> equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is Self -> 0 == other.compareTo(this)
            else -> false
        }

    override fun equals(other: Any?): Boolean = equalsImpl<TKVEntryK<A,B>>(other)


    override fun getk(): A = k
    override fun getkc(): Comparable<A> = k
    override fun getv(): B = v
    override fun copy(): TKVEntry<A, B> = /* TODO */ this.copy(k=k, v=v)
}

//internal data class TKVEntryK1<A, B> constructor (val k: A, val v: B, override val vc: Comparator<@UnsafeVariance A>?): TKVEntry1<A,B>
//where A: Any, A: Comparable<@UnsafeVariance A>, B: Any, B: Comparable<@UnsafeVariance B> {
//
//    override fun toString(): String = "[ $k:*$v ]"
//
//    override fun hashCode(): Int = when {
//        k is Int -> k
//        else -> k.hashCode()
//    }
//
//    private inline fun <reified Self: TKVEntryK1<@UnsafeVariance A, @UnsafeVariance B>> equalsImpl(other: Any?): Boolean =
//        when {
//            this === other -> true
//            other == null -> false
//            other is Self -> 0 == other.compareTo(this)
//            else -> false
//        }
//
//    override fun equals(other: Any?): Boolean = equalsImpl<TKVEntryK1<A,B>>(other)
//
//
//    override fun getk(): A = k
//    override fun getkc(): Comparable<A> = k
//    override fun getv(): B = v
//    override fun copy(): TKVEntry<A, B> = /* TODO */ this.copy(k=k, v=v)
//}
