package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.TKVEntry.Companion.ofIntKey
import com.xrpn.immutable.TKVEntry.Companion.ofk

//
// W       W  I  P P P
// W       W  I  P    P
// W   W   W  I  P P P
//  W W W W   I  P
//   W   W    I  P
//

sealed class FKMap<out K, out V: Any>: IMMap<K, V>, Map <@UnsafeVariance K, V> where K: Any, K: Comparable<@UnsafeVariance K> {

    // imcollection

    override val seal: IMSC = IMSC.IMMAP

    override fun fcontains(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>): Boolean  = when (this) {
        is FKMapEmpty -> false
        is FKMapNotEmpty -> body.fcontains(item)
    }

    override fun fdropAll(items: IMCollection<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun fdropItem(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> =
        fdropkv(item.getk(), item.getv())

    override fun ffilter(isMatch: (TKVEntry<K, V>) -> Boolean): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun ffilterNot(isMatch: (TKVEntry<K, V>) -> Boolean): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun ffindAny(isMatch: (TKVEntry<K, V>) -> Boolean): TKVEntry<K, V>? = when (this) {
        is FKMapEmpty -> null
        is FKMapNotEmpty -> body.ffindAny(isMatch)
    }

    override fun fisStrict(): Boolean = when (this) {
        is FKMapEmpty -> true
        is FKMapNotEmpty -> body.fisStrict()
    }

    override fun fcount(isMatch: (TKVEntry<K, V>) -> Boolean): Int = when (this) {
        is FKMapEmpty -> 0
        is FKMapNotEmpty -> body.fcount(isMatch)
    }

    override fun fsize(): Int = size

    // imkeyed

    override fun asIMBTree(): IMBTree<K,V> =  when (this) {
        is FKMapEmpty -> FRBTree.emptyIMBTree()
        is FKMapNotEmpty -> body
    }

    override fun fcontainsValue(value: @UnsafeVariance V): Boolean = when (this) {
        is FKMapEmpty -> false
        is FKMapNotEmpty -> body.fcontainsValue(value)
    }

    override fun fcountValue(isMatch: (V) -> Boolean): Int = when (this) {
        is FKMapEmpty -> 0
        is FKMapNotEmpty -> body.ffold(0) {acc, tkv -> if(isMatch(tkv.getv())) acc + 1 else acc }
    }

    override fun ffilterKey(isMatch: (K) -> Boolean): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun ffilterKeyNot(isMatch: (K) -> Boolean): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun ffilterValue(isMatch: (V) -> Boolean): IMMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun ffilterValueNot(isMatch: (V) -> Boolean): IMMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun ffindAnyValue(isMatch: (V) -> Boolean): TKVEntry<K, V>? {
        TODO("Not yet implemented")
    }

    override fun fpickEntry(): TKVEntry<K, V>? = fpick()


    // extras

    override operator fun contains(k: @UnsafeVariance K): Boolean = fcontainsKey(k)
    override operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance V): FKMap<K, V> = fputkv(k, v)

    // filtering

    override fun fcontainsKey(key: @UnsafeVariance K): Boolean = when (this) {
        is FKMapEmpty -> false
        is FKMapNotEmpty -> body.ffindValueOfKey(key) != null
    }

    override fun fdrop(key: @UnsafeVariance K): FKMap<K,V> = when (this) {
        is FKMapEmpty -> this
        is FKMapNotEmpty -> body.ffindKey(key)?.let { ofFKMapBody(body.fdropItem(it.froot()!!)) } ?: this
    }

    override fun fdropKeys(keys: IMSet<@UnsafeVariance K>): FKMap<K, V> = when (this) {
        is FKMapEmpty -> this
        is FKMapNotEmpty -> ofFKMapBody(keys.ffold(this.toFRBTree()){t, k ->
            t.ffindKey(k)?.let {tt ->
                t.fdropItem(tt.froot()!!)
            } ?: t}
        )
    }

    override fun fdropkv(key: @UnsafeVariance K, value: @UnsafeVariance V): FKMap<K,V> = when (this) {
        is FKMapEmpty -> this
        is FKMapNotEmpty -> ofFKMapBody(body.fdropItem(TKVEntry.ofkv(key, value)))
    }

    override fun fget(key: @UnsafeVariance K): V? = when (this) {
        is FKMapEmpty -> null
        is FKMapNotEmpty -> body.ffindValueOfKey(key)
    }

    override fun fpick(): TKVEntry<K, V>? = when (this) {
        is FKMapEmpty -> null
        is FKMapNotEmpty -> body.froot()
    }

    override fun fAND(items: IMMap<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun fOR(items: IMMap<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun fXOR(items: IMMap<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    // grouping

    override fun fentries(): IMSet<TKVEntry<K,V>> = (@Suppress("UNCHECKED_CAST") (entries as IMSet<TKVEntry<K, V>>))

    override fun fkeys(): IMSet<K> = (@Suppress("UNCHECKED_CAST") (keys as IMSet<K>))

    override fun <R : Comparable<R>> maxBy(f: (V) -> R): TKVEntry<K, V>? {
        TODO("Not yet implemented")
    }

    override fun <R : Comparable<R>> minBy(f: (V) -> R): TKVEntry<K, V>? {
        TODO("Not yet implemented")
    }

    override fun fpartition(isMatch: (TKVEntry<K, V>) -> Boolean): Pair<IMMap<K, V>, IMMap<K, V>> {
        TODO("Not yet implemented")
    }

    override fun fpopAndRemainder(): Pair<TKVEntry<K, V>?, IMMap<K, V>> {
        TODO("Not yet implemented")
    }

    override fun fvalues(): FList<V> = values as FList<V>

    // transforming

    override fun <C, D : Any> fflatMap(f: (TKVEntry<K, V>) -> IMMap<C, D>): IMMap<C, D> where C: Any, C : Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun <J> fmapKeys(f: (TKVEntry<K, V>) -> J): FKMap<J, V> where J: Any, J : Comparable<J> {
        TODO("Not yet implemented")
    }

    override fun <W : Any> fmapValues(f: (TKVEntry<K, V>) -> W): FKMap<K, W> {
        TODO("Not yet implemented")
    }

    override fun <C> ffold(z: C, f: (acc: C, TKVEntry<K, V>) -> C): C {
        TODO("Not yet implemented")
    }

    override fun <C, D : Any> fmap(f: (TKVEntry<K, V>) -> TKVEntry<C, D>): FKMap<C, D> where C: Any, C : Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun freducev(f: (acc: V, V) -> @UnsafeVariance V): V? {
        TODO("Not yet implemented")
    }

    // altering

    override fun fputkv(key: @UnsafeVariance K, value: @UnsafeVariance V): FKMap<K,V> = when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(TKVEntry.ofkv(key, value)) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(body.finsert(TKVEntry.ofkv(key, value)) as FRBTNode)
    }

    override fun fputPair(p: Pair<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(TKVEntry.ofp(p)) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(body.finsert(TKVEntry.ofp(p)) as FRBTNode)
    }

    override fun fputList(l: FList<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): FKMap<K,V> = if (l.fempty()) this else when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(l as IMList<TKVEntry<K,V>>) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(body.finserts(l) as FRBTNode<K, V>)
    }

    override fun fputTree(t: IMBTree<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> t.toIMMap() as FKMap<K,V>
        is FKMapNotEmpty -> if (t.fempty()) this else ofFKMapNotEmpty(body.finsertt(t) as FRBTNode<K, V>)
    }

    override fun fputMap(m: IMMap<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> when(m) {
            is FKMap<K,V> -> m
            else -> throw RuntimeException("unknown map: ${m::class}")
        }
        is FKMapNotEmpty -> fputTree(m.toIMBTree())
    }

    // utility

    override fun equal(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): Boolean =
        this.equals(rhs)

    override fun fforEach (f: (V) -> Unit) = when(this) {
        is FKMapEmpty -> Unit
        is FKMapNotEmpty -> body.fforEach { tkv -> f(tkv.getv()) }
    }

    override fun toIMBTree(): IMBTree<K, V> = when(this) {
        is FKMapEmpty -> nul()
        is FKMapNotEmpty -> body
    }

    override fun copy(): FKMap<K, V> = when (this) {
        is FKMapEmpty -> emptyIMMap()
        is FKMapNotEmpty -> body.ffold(nul<K, V>()) { acc, tkv -> acc.finsert(tkv) }.toIMMap()
    }

    override fun copyToMutableMap(): MutableMap<@UnsafeVariance K, @UnsafeVariance V> = when (this) {
        is FKMapEmpty -> mutableMapOf()
        is FKMapNotEmpty -> body.ffold(mutableMapOf()) { acc, tkv -> acc[tkv.getk()] = tkv.getv(); acc }
    }

    //
    // ========= implementation
    //

    internal fun toFRBTree(): FRBTree<K, V> = when (this) {
        is FKMapEmpty -> nul()
        is FKMapNotEmpty -> body
    }

    // private

    companion object: IMMapCompanion {

        override fun <K, V: Any> emptyIMMap(): FKMap<K, V> where K: Any, K: Comparable<K> = FKMapEmpty.empty()

        override fun <K, V : Any> of(vararg items: Pair<K, V>): FKMap<K, V> where K: Any, K : Comparable<K> =
            of(items.iterator())

        override fun <K, V : Any> of(items: Iterator<Pair<K, V>>): FKMap<K, V> where K: Any, K : Comparable<K> {
            var acc: FRBTree<K, V> = nul()
            items.forEach { acc = acc.finsert(TKVEntry.ofp(it)) }
            return ofFKMapBody(acc)
        }

        override fun <K, V : Any> of(items: IMList<Pair<K, V>>): FKMap<K, V> where K: Any, K : Comparable<K> =
            if (items.fempty()) FKMapEmpty.empty() else ofFKMapBody(FRBTree.of(items.fmap { TKVEntry.ofp(it) }))

        override fun <K, V : Any> of(items: IMBTree<K, V>): FKMap<K, V> where K: Any, K : Comparable<K> = when (items) {
            is FRBTree -> ofFKMapBody(items)
            is FBSTree -> items.toIMMap() as FKMap<K, V>
            else -> throw RuntimeException("unknown tree type ${items::class}")
        }

        override fun <K, V : Any> Collection<V>.toIMMap(keyMaker: (V) -> K): FKMap<K, V> where K: Any, K : Comparable<K> {
            var acc: FRBTree<K, V> = nul()
            this.forEach { acc = acc.finsert(TKVEntry.ofkv(keyMaker(it), it)) }
            return ofFKMapBody(acc)
        }

        fun <K: Comparable<K>, V: Any> of(_body: FList<TKVEntry<K,V>>): FKMap<K, V> =
            if (_body is FLNil) emptyIMMap() else ofFKMapNotEmpty(FRBTree.of(_body) as FRBTNode)

    }
}

internal class FKMapEmpty<K, V: Any> private constructor (
    val body: FRBTree<K, V>
): FKMap<K, V>() where K: Any, K: Comparable<@UnsafeVariance K> {

    // placate the compiler
    fun donotuse() = print(body)

    // ========== Any

    override fun equals(other: Any?): Boolean = when {
        singletonEmpty === other -> true
        other == null -> false
        other is IMMap<*, *> -> false
        other is Map<*,*> -> other.isEmpty()
        else -> false
    }
    val hash: Int by lazy { this::class.simpleName.hashCode() }
    override fun hashCode(): Int = hash
    val show: String by lazy { FKMap::class.simpleName + "(*->*)" }
    override fun toString(): String = show

    // ========== FKMap

    companion object {
        private val singletonEmpty = FKMapEmpty(FRBTNil)
        internal fun <K, V: Any> empty(): FKMap<K, V> where K: Any, K: Comparable<@UnsafeVariance K> = singletonEmpty
    }

    // ========== Map

    override fun isEmpty(): Boolean = true
    override val size: Int = 0
    override val entries: Set<Map.Entry<K, V>> = FKSetEmpty.empty<Int, TKVEntry<K,V>>()
    override val keys: Set<K> = FKSetEmpty.empty<Int, K>()
    override val values: Collection<V> = FList.emptyIMList()
    override fun containsKey(key: K): Boolean = false
    override fun containsValue(value: V): Boolean = false
    override operator fun get(key: K): V? = null
}

internal class FKMapNotEmpty<out K, out V: Any> private constructor (
    val body: FRBTNode<@UnsafeVariance K, @UnsafeVariance V>
): FKMap<K, V>() where K: Any, K: Comparable<@UnsafeVariance K> {

    // ========== Any

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMMap<*, *> -> when {
            other.fempty() -> false
            this.fpick()!!.getkKc() != other.fpick()!!.getkKc() -> false
            this.fpick()!!.getvKc() != other.fpick()!!.getvKc() -> false
            else ->  @Suppress("UNCHECKED_CAST") IMMapEqual2(this, other as IMMap<K, V>)
        }
        other is Map<*, *> -> if (other.isEmpty()) false else {
            val nnSample = (@Suppress("UNCHECKED_CAST") (other.entries.first() as Map.Entry<Any, Any>))
            when {
                this.fpick()!!.getkKc() != nnSample.key::class -> false
                this.fpick()!!.getvKc() != nnSample.value::class -> false
                else ->  other.equals(this)
            }
        }
        else -> false
    }

    val hash: Int by lazy {
        // hash of a FRBTree depends on the content AND on the shape of the tree;
        // for map hash, the shape of the tree is irrelevant, whence the following
        (127 * (this.body.inorder().hashCode() + 11)) / 109
    }
    override fun hashCode(): Int = hash
    val show: String by lazy {
        val spacerOpen = "("
        val spacerClose = "),"
        val cn: String = FKMap::class.simpleName!!
        "$cn(${body.inorder().ffoldLeft("") { acc, tkv -> acc + spacerOpen + tkv.getk().toString() + "->" + tkv.getv().toString() + spacerClose }.dropLast(1)})"
    }
    override fun toString(): String = show

    // ========== Map

    override fun isEmpty(): Boolean = false
    override val size: Int by lazy { body.size }
    override val entries: Set<Map.Entry<K, V>> by lazy {
        val (item: TKVEntry<K, V>?, remainder: FRBTree<K, V>) = body.fpopAndRemainder()
        item as TKVEntry<K, V>
        val res: IMSet<TKVEntry<K, V>> = when(item.getk()) {
            is String -> remainder
                .ffold(@Suppress("UNCHECKED_CAST") (FKSet.ofs(item) as IMRSetNotEmpty<TKVEntry<K, V>>)) { acc, tkv -> acc.faddItem(tkv) }
            else -> remainder // will use hashCode() as key, beware of collisions
                .ffold(@Suppress("UNCHECKED_CAST") (FKSet.ofi(item) as IMRSetNotEmpty<TKVEntry<K, V>>)) { acc, tkv -> acc.faddItem(tkv) }
        }
        @Suppress("UNCHECKED_CAST") (res as Set<Map.Entry<K, V>>)
    }

    override val keys: Set<K> by lazy {

        val item: TKVEntry<K, V>? = body.froot()

        fun assemblekk(): FKSet<@UnsafeVariance K, @UnsafeVariance K> {
            val (ei: TKVEntry<K, V>?, remainder: FRBTree<K, V>) = body.fpopAndRemainder()
            val seed: FRBTree<K, K> = FRBTree.of(ofk(ei!!.getk()))
            return remainder.ffold(seed) { acc, tkv -> acc.finsert(ofk(tkv.getk())) }.toIMRSet(seed.fkeyType())!!
        }

        if (null == item) TODO() else {
            val res: IMSet<K> = when (item) {
                is RTKVEntry -> when (item.getrk()) {
                    is DeratedCustomKeyType -> throw RuntimeException("internal error")
                    else -> assemblekk()
                }
                else -> when (item.getk()) {
                    is String, is Int -> assemblekk()
                    else -> {
                        val (ei: TKVEntry<K, V>?, remainder: FRBTree<K, V>) = body.fpopAndRemainder()
                        val seed: FRBTree<Int, K> = FRBTree.of(ofIntKey(ei!!.getk()))
                        remainder.ffold(seed) { acc, tkv -> acc.finsert(ofIntKey(tkv.getk())) }.toIMRSet(seed.fkeyType())!!
                    }
                }
            }
            @Suppress("UNCHECKED_CAST") (res as Set<K>)
        }
    }
    override val values: Collection<V> by lazy { body.fmapvToList { it } as FList<V> }
    override fun containsKey(key: @UnsafeVariance K): Boolean = body.fcontainsKey(key)
    override fun containsValue(value: @UnsafeVariance V): Boolean = body.fcontainsValue(value)
    override operator fun get(key: @UnsafeVariance K): V? = body.ffindKey(key)?.froot()?.getv()

    companion object {
        internal fun <K, V: Any> of(b: FRBTNode<K, V>): FKMap<K, V> where K: Any, K: Comparable<K> = FKMapNotEmpty(b)
    }
}

fun <K, V : Any> Pair<K,V>.toMap(): FKMap<K, V> where K: Any, K: Comparable<K> =
    ofFKMapNotEmpty(FRBTree.of(TKVEntry.ofp(this)) as FRBTNode)

fun <K, V : Any> V.toMap(keyMaker: (V) -> K): FKMap<K, V> where K: Any, K: Comparable<K> =
    ofFKMapNotEmpty(FRBTree.of(TKVEntry.ofkv(keyMaker(this), this)) as FRBTNode)

internal fun <K, V: Any> ofFKMapBody(b: FRBTree<K, V>): FKMap<K, V> where K: Any, K: Comparable<K> = when(b) {
    is FRBTNode -> FKMapNotEmpty.of(b)
    else -> FKMapEmpty.empty()
}

internal fun <K, V: Any> ofFKMapNotEmpty(b: FRBTNode<K, V>): FKMap<K, V> where K: Any, K: Comparable<K> = FKMapNotEmpty.of(b)
