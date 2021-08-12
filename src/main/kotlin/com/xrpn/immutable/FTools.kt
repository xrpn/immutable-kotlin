package com.xrpn.immutable

inline fun <reified A, reified B> isSameType(a: A, b: B): Boolean = (a is B) && (b is A)
inline fun <reified A> fidentity(a: A): A = a

fun <A, B> Pair<A, A>.pmap1(f: (A) -> B): Pair<B, B> = Pair(f(this.first), f(this.second))
fun <A, B, C, D> Pair<A, B>.pmap2(f: (A) -> C, g: (B) -> D): Pair<C, D> = Pair(f(this.first), g(this.second))
fun <A: Any> Pair<A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLNil))

fun <A, B> Triple<A, A, A>.tmap1(f: (A) -> B): Triple<B, B, B> = Triple(f(this.first), f(this.second), f(this.third))
fun <A, B, C, D, E, F> Triple<A, B, C>.tmap3(f: (A) -> D, g: (B) -> E, h: (C) -> F): Triple<D, E, F> = Triple(f(this.first), g(this.second), h(this.third))
fun <A: Any> Triple<A, A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLCons(this.third, FLNil)))

//abstract class DeepRecursiveScope<T, R> {
//    abstract suspend fun callRecursive(value: T): R
//}
//
//class DeepRecursiveFunction<T, R>(
//    val block: suspend DeepRecursiveScope<T, R>.(T) -> R
//)

data class TKVEntryK<A: Comparable<A>, B:Any> constructor (val k: A, val v: B):
        Comparable<TKVEntryK<A,B>>,
        TKVEntry<A,B> {

    override operator fun compareTo(other: TKVEntryK<A, B>): Int = k.compareTo(other.k)

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

interface TKVEntry<out A, out B: Any> where A: Any, A: Comparable<@kotlin.UnsafeVariance A> {
    fun getk(): A
    fun getkc(): Comparable<@UnsafeVariance A>
    fun getv(): B
    fun copy(): TKVEntry<A,B>

    companion object {
        fun <A: Comparable<A>, B: Any> TKVEntry<A, B>.compare(other: TKVEntry<A, B>): Int = this.getkc().compareTo(other.getk())
        fun <A: Comparable<A>, B: Any> of (key:A, value: B): TKVEntry<A, B> = TKVEntryK(key, value)
        fun <A: Comparable<A>, B: Any> of (p: Pair<A, B>): TKVEntry<A, B> = TKVEntryK(p.first, p.second)
        fun <B: Any> ofIntKey (item: B): TKVEntry<Int, B> = TKVEntryK(item.hashCode(), item)
        fun <B: Any> ofStrKey (item: B): TKVEntry<String, B> = TKVEntryK(item.toString(), item)
        fun <A: Any> A.toIAEntry(): TKVEntry<Int, A> = ofIntKey(this)
        fun <A: Any> A.toSAEntry(): TKVEntry<String, A> = ofStrKey(this)
        fun <A: Any> FList<A>.toIAEntries(): FList<TKVEntry<Int, A>> = this.fmap { a -> ofIntKey(a) }
        fun <A: Any> FList<A>.toSAEntries(): FList<TKVEntry<String, A>> = this.fmap { a -> ofStrKey(a) }
        fun <A: Any> FSet<A>.toIAEntries(): FList<TKVEntry<Int, A>> = this.fmapToList { a -> ofIntKey(a) }
        fun <A: Any> FSet<A>.toSAEntries(): FList<TKVEntry<String, A>> = this.fmapToList { a -> ofStrKey(a) }
        fun <A: Any> Collection<A>.toIAEntries(): FList<TKVEntry<Int, A>> = FList.ofMap(this.iterator()){ a -> ofIntKey(a) }
        fun <A: Any> Collection<A>.toSAEntries(): FList<TKVEntry<String, A>> = FList.ofMap(this.iterator()) { a -> ofStrKey(a) }
    }
}

