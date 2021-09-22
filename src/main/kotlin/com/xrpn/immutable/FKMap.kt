package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.rbtFindValueOFKey
import com.xrpn.immutable.FRBTree.Companion.rbtInsert

//
// W       W  I  P P P
// W       W  I  P    P
// W   W   W  I  P P P
//  W W W W   I  P
//   W   W    I  P
//

sealed class FKMap<out K, out V: Any>: IMMap<K, V>, Map <@UnsafeVariance K, V> where K: Any, K: Comparable<@UnsafeVariance K> {

    // filtering

    override fun fcontains(key: @UnsafeVariance K): Boolean = when (this) {
        is FKMapEmpty -> false
        is FKMapNotEmpty -> rbtFindValueOFKey(body, key) != null
    }

    override fun fget(key: @UnsafeVariance K): V? = when (this) {
        is FKMapEmpty -> null
        is FKMapNotEmpty -> rbtFindValueOFKey(body, key)
    }

    override fun fpick(): TKVEntry<K, V>? = when (this) {
        is FKMapEmpty -> null
        is FKMapNotEmpty -> body.froot()
    }

    // grouping

    override fun fentries(): IMRSet<TKVEntry<K,V>> = (@Suppress("UNCHECKED_CAST") (entries as IMRSet<TKVEntry<K, V>>))

    override fun fkeys(): IMRSet<K> = (@Suppress("UNCHECKED_CAST") (keys as IMRSet<K>))

    override fun fsize(): Int = size

    override fun fvalues(): FList<V> = values as FList<V>

    // altering

    override fun fputkv(key: @UnsafeVariance K, value: @UnsafeVariance V): FKMap<K,V> = when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(TKVEntry.of(key, value)) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(rbtInsert(body, TKVEntry.of(key, value)))
    }

    override fun fputPair(p: Pair<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(TKVEntry.of(p)) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(rbtInsert(body, TKVEntry.of(p)))
    }

    override fun fputList(l: FList<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): FKMap<K,V> = if (l.fempty()) this else when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(l as IMList<TKVEntry<K,V>>) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(body.finserts(l.fmap { TKVEntry.of(it) }) as FRBTNode<K, V>)
    }

    override fun fputTree(t: IMBTree<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> t.toIMMap() as FKMap<K,V>
        is FKMapNotEmpty -> if (t.fempty()) this else ofFKMapNotEmpty(body.finsertt(t) as FRBTNode<K, V>)
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
            items.forEach { acc = acc.finsert(TKVEntry.of(it)) }
            return ofFKMapBody(acc)
        }

        override fun <K, V : Any> of(items: IMList<Pair<K, V>>): FKMap<K, V> where K: Any, K : Comparable<K> =
            if (items.fempty()) FKMapEmpty.empty() else ofFKMapBody(FRBTree.of(items.fmap { TKVEntry.of(it) }))

        override fun <K, V : Any> of(items: IMBTree<K, V>): FKMap<K, V> where K: Any, K : Comparable<K> = when (items) {
            is FRBTree -> ofFKMapBody(items)
            is FBSTree -> items.toIMMap() as FKMap<K, V>
            else -> throw RuntimeException("unknown tree type ${items::class}")
        }

        override fun <K, V : Any> Collection<V>.toIMMap(keyMaker: (V) -> K): FKMap<K, V> where K: Any, K : Comparable<K> {
            var acc: FRBTree<K, V> = nul()
            this.forEach { acc = acc.finsert(TKVEntry.of(keyMaker(it), it)) }
            return ofFKMapBody(acc)
        }

        fun<K: Comparable<K>, V: Any> equal2(lhs: FKMap<K,V>, rhs: FKMap<K,V>): Boolean = when(Pair(lhs.isEmpty(), rhs.isEmpty())) {
            Pair(false, false) -> if (lhs === rhs) true else (lhs as FKMapNotEmpty) == rhs
            Pair(true, true) -> true
            else -> false
        }

        fun<K: Comparable<K>, V: Any> FKMap<K,V>.fequal(rhs: FKMap<K,V>): Boolean = equal2(this, rhs)

        fun <K: Comparable<K>, V: Any> of(_body: FList<TKVEntry<K,V>>): FKMap<K, V> =
            if (_body is FLNil) emptyIMMap() else ofFKMapNotEmpty(FRBTree.of(_body) as FRBTNode)

    }
}

internal class FKMapEmpty<K, V: Any> private constructor (
    val body: FRBTree<K, V>
): FKMap<K, V>() where K: Any, K: Comparable<@UnsafeVariance K> {

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
        const val msg = "never to be implemented (impossible path)"
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
    override fun get(key: K): V? = null
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
            this.fpick()!!.getk()::class != other.fpick()!!.getk()::class -> false
            this.fpick()!!.getv()::class != other.fpick()!!.getv()::class -> false
            else ->  @Suppress("UNCHECKED_CAST") IMMapEqual2(this, other as IMMap<K, V>)
        }
        other is Map<*, *> -> if (other.isEmpty()) false else {
            val nnSample = (@Suppress("UNCHECKED_CAST") (other.entries.first() as Map.Entry<Any, Any>))
            when {
                this.fpick()!!.getk()::class != nnSample.key::class -> false
                this.fpick()!!.getv()::class != nnSample.value::class -> false
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
        val (item: TKVEntry<K, V>?, remainder: FRBTree<K, V>) = body.fpopAndReminder()
        item as TKVEntry<K, V>
        val res: IMRSet<TKVEntry<K, V>> = when(item.getk()) {
            is String -> remainder
                .ffold(@Suppress("UNCHECKED_CAST") (FKSet.ofs(item) as IMSetNotEmpty<String, TKVEntry<K, V>>)) { acc, tkv -> acc.faddItem(tkv) }
            else -> remainder
                .ffold(@Suppress("UNCHECKED_CAST") (FKSet.ofi(item) as IMSetNotEmpty<Int, TKVEntry<K, V>>)) { acc, tkv -> acc.faddItem(tkv) }
        }
        @Suppress("UNCHECKED_CAST") (res as Set<Map.Entry<K, V>>)
    }
    override val keys: Set<K> by lazy {
        val item = body.froot() as TKVEntry<K, V>
        val res: IMRSet<K> = when(item.getk()) {
            is String -> body.ffold(nul<String, K>()) { acc, tkv -> acc.finsert(@Suppress("UNCHECKED_CAST") (tkv as TKVEntry<String, K>)) }.toIMSet(String::class)
            is Int -> body.ffold(nul<Int, K>()) { acc, tkv -> acc.finsert(@Suppress("UNCHECKED_CAST")(tkv as TKVEntry<Int, K>)) }.toIMSet(Int::class)
            else -> body.ffold(nul<Int, K>()) { acc, tkv -> acc.finsert(TKVEntry.ofIntKey(tkv.getk())) }.toIMSet(Int::class)
        }
        @Suppress("UNCHECKED_CAST") (res as Set<K>)
    }
    override val values: Collection<V> by lazy { body.fmapvToList { it } as FList<V> }
    override fun containsKey(key: @UnsafeVariance K): Boolean = body.fcontainsKey(key)
    override fun containsValue(value: @UnsafeVariance V): Boolean = body.fcontainsValue(value)
    override fun get(key: @UnsafeVariance K): V? = body.ffindKey(key)?.froot()?.getv()

    companion object {
        internal fun <K, V: Any> of(b: FRBTNode<K, V>): FKMap<K, V> where K: Any, K: Comparable<K> = FKMapNotEmpty(b)
    }

}

fun <K, V : Any> V.toMap(keyMaker: (V) -> K): FKMap<K, V> where K: Any, K: Comparable<K> =
    ofFKMapNotEmpty(FRBTree.of(TKVEntry.of(keyMaker(this), this)) as FRBTNode)

internal fun <K, V: Any> ofFKMapBody(b: FRBTree<K, V>): FKMap<K, V> where K: Any, K: Comparable<K> = when(b) {
    is FRBTNode -> FKMapNotEmpty.of(b)
    else -> FKMapEmpty.empty()
}

internal fun <K, V: Any> ofFKMapNotEmpty(b: FRBTNode<K, V>): FKMap<K, V> where K: Any, K: Comparable<K> = FKMapNotEmpty.of(b)
