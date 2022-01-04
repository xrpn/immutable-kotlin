package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FKSet.Companion.emptyIMKISet
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FList.Companion.emptyIMList
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
internal interface FKMapRetrieval<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun original(): FKMap<K,V>
}

sealed class FKMap<out K, out V: Any>: IMMap<K, V> where K: Any, K: Comparable<@UnsafeVariance K> {

    // imcommon

    override val seal: IMSC = IMSC.IMMAP

    // abstract fun asMap(): Map <@UnsafeVariance K, V>

    override fun fcontains(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>?): Boolean  = item?.let{ when (this) {
        is FKMapEmpty -> false
        is FKMapNotEmpty -> body.fcontains(item)
    }} ?: false

    override fun fdropAll(items: IMCommon<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): FKMap<K, V> {
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

    override fun fsize(): Int = when (this) {
        is FKMapEmpty -> 0
        is FKMapNotEmpty -> body.size
    }

    override fun toEmpty(): IMMap<K, V> = emptyIMMap()

    // imkeyed

    override fun asIMBTree(): IMBTree<K,V> = when (this) {
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

    override fun ffindAnyValue(isMatch: (V) -> Boolean): V? {
        TODO("Not yet implemented")
    }

    // extras

    override operator fun contains(k: @UnsafeVariance K): Boolean = fcontainsKey(k)
    override operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance V): FKMap<K, V> = fputkv(k, v)
    override operator fun get(key: @UnsafeVariance K): V? = when (this) {
        is FKMapEmpty -> null
        is FKMapNotEmpty -> this.body.get(key)
    }
    // filtering

    override fun fcontainsKey(key: @UnsafeVariance K): Boolean = when (this) {
        is FKMapEmpty -> false
        is FKMapNotEmpty -> body.ffindValueOfKey(key) != null
    }

    override fun fcountKey(isMatch: (K) -> Boolean): Int = when (this) {
        is FKMapEmpty -> 0
        is FKMapNotEmpty -> body.fcountKey(isMatch)
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

    override fun fAND(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun fNOT(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V> =
        of(this.asIMBTree().fdropAlt(items.asIMBTree()))

    override fun fOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun fXOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V> {
        TODO("Not yet implemented")
    }

    // grouping

    val fkmapEntries: FKSet<Int, TKVEntry<K, V>> by lazy { when(this) {
        is FKMapEmpty -> emptyIMKISet()
        is FKMapNotEmpty -> {
            val newBody: FRBTree<Int, TKVEntry<K, V>> = this.body.ffold(nul()) { acc, tkv -> acc.finsertTkv( ofIntKey(tkv) ) }
            ofBody(newBody)!!
        }
    }}
    override fun fentries(): IMSet<TKVEntry<K,V>> = fkmapEntries

    val fkmapKeys: FKSet<K,K> by lazy { when(this) {
        is FKMapEmpty -> emptyIMKSet(null)
        is FKMapNotEmpty -> {
            val newBody = this.body.ffold(nul<K,K>()) { acc, tkv -> acc.finsertTkv( ofk(tkv.getk()) ) }
            newBody as FRBTNode<K,K>
            ofFKKSBody(newBody)
        }
    }}
    override fun fkeys(): IMSet<K> = fkmapKeys

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

    val fkmapValues: FList<V> by lazy {  when(this) {
        is FKMapEmpty -> emptyIMList<V>()
        is FKMapNotEmpty -> this.body.fmapvToList { it } as FList<V>
    }}
    override fun fvalues(): FList<V> = fkmapValues

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

    override fun <C, D: Any> fmap(f: (TKVEntry<K, V>) -> TKVEntry<C, D>): FKMap<C, D> where C: Any, C : Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun freduce(f: (acc: V, V) -> @UnsafeVariance V): V? {
        TODO("Not yet implemented")
    }

    // altering

    override fun fputkv(key: @UnsafeVariance K, value: @UnsafeVariance V): FKMap<K,V> =
        fadd(TKVEntry.ofkv(key, value))

    override fun fputPair(p: Pair<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(TKVEntry.ofp(p)) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(body.finsertTkv(TKVEntry.ofp(p)) as FRBTNode)
    }

    override fun fputList(l: FList<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): FKMap<K,V> = if (l.fempty()) this else when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(l as IMList<TKVEntry<K,V>>) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(body.finsertTkvs(l) as FRBTNode<K, V>)
    }

    override fun fputTree(t: IMBTree<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> t.toIMMap() as FKMap<K,V>
        is FKMapNotEmpty -> if (t.fempty()) this else ofFKMapNotEmpty(body.finsertTkvs(t) as FRBTNode<K, V>)
    }

    override fun fputMap(m: IMMap<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K,V> = when (this) {
        is FKMapEmpty -> when(m) {
            is FKMap<K,V> -> m
            else -> throw RuntimeException("unknown map: ${m::class}")
        }
        is FKMapNotEmpty -> fputTree(m.toIMBTree())
    }

    internal fun fadd(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>): FKMap<K, V>  = when (this) {
        is FKMapEmpty -> ofFKMapNotEmpty(FRBTree.of(item) as FRBTNode<K, V>)
        is FKMapNotEmpty -> ofFKMapNotEmpty(body.finsertTkv(item) as FRBTNode)
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
        is FKMapNotEmpty -> body.ffold(nul<K, V>()) { acc, tkv -> acc.finsertTkv(tkv) }.toIMMap()
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
            items.forEach { acc = acc.finsertTkv(TKVEntry.ofp(it)) }
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
            this.forEach { acc = acc.finsertTkv(TKVEntry.ofkv(keyMaker(it), it)) }
            return ofFKMapBody(acc)
        }

        fun <K: Comparable<K>, V: Any> of(_body: FList<TKVEntry<K,V>>): FKMap<K, V> =
            if (_body is FLNil) emptyIMMap() else ofFKMapNotEmpty(FRBTree.of(_body) as FRBTNode)

    }
}

private class FKMapEmpty<K, V: Any> private constructor (
//    val body: FRBTree<K, V>
): FKMap<K, V>() where K: Any, K: Comparable<@UnsafeVariance K> {

    // placate the compiler
    //fun donotuse() = print(body)

    // ========== Any

    override fun equals(other: Any?): Boolean = when {
        singletonEmpty === other -> true
        other == null -> false
        other is FKMapEmpty<*,*> -> true
        other is IMMap<*, *> -> false
        else -> false
    }

    override fun softEqual(rhs: Any?): Boolean = equals(rhs) || when (rhs) {
        is Map<*,*> -> rhs.isEmpty()
        is IMCommon<*> -> IMCommonEmpty.equal(rhs)
        else -> false
    }

    val hash: Int by lazy { this::class.simpleName.hashCode() }
    override fun hashCode(): Int = hash
    val show: String by lazy { FKMap::class.simpleName + "(*->*)" }
    override fun toString(): String = show

    // ========== FKMap

    companion object {
        private val singletonEmpty = FKMapEmpty<Nothing,Nothing>() //FKMapEmpty(FRBTNil)
        internal fun <K, V: Any> empty(): FKMap<K, V> where K: Any, K: Comparable<@UnsafeVariance K> = singletonEmpty
    }

    // ========== Map

    /* TODO start
    private val kmap: Map <@UnsafeVariance K, V> = object : Map <@UnsafeVariance K, V> {
        override fun isEmpty(): Boolean = true
        override val size: Int = 0
        override val entries: Set<Map.Entry<K, V>> = emptySet()
        override val keys: Set<K> = emptySet()
        override val values: Collection<V> = emptyList()
        override fun containsKey(key: K): Boolean = false
        override fun containsValue(value: V): Boolean = false
        override operator fun get(key: K): V? = null
    }

    override fun asMap(): Map <@UnsafeVariance K, V> = kmap
    TODO end */

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
            other.fsize() != fsize() -> false
            fpick()!!.strictlyNot(other.fpick()!!.untype()) -> false
            else ->  (@Suppress("UNCHECKED_CAST")(other as? IMMap<K, V>))?.let{ IMMapEqual2(this, it) } ?: false
        }
        else -> false
    }

    override fun softEqual(rhs: Any?): Boolean = equals(rhs) || when(rhs) {
        is Map<*, *> -> if (rhs.isEmpty() || (fsize() != rhs.size)) false else {
            val nnSample = (@Suppress("UNCHECKED_CAST") (rhs.entries.first() as? Map.Entry<Any, Any>))
            nnSample?.let { when {
                this.fpick()!!.getkKc().isStrictlyNot(it.key) -> false
                this.fpick()!!.getvKc().isStrictlyNot(it.value) -> false
                else -> rhs.equals(this)
            }} ?: false
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
    /* TODO start
    private val kmap: Map <@UnsafeVariance K, V> by lazy { object : Map <@UnsafeVariance K, V> {
        override fun isEmpty(): Boolean = false
        override val size: Int by lazy { body.size }
        override val entries: Set<Map.Entry<K, V>> by lazy {
            if (fkmapEntries.fempty()) emptySet() else fkmapEntries.asSet()
        }
        override val keys: Set<K> by lazy {
            if (fkmapKeys.fempty()) emptySet() else fkmapKeys.asSet()
        }
        override val values: Collection<V> by lazy {
            // TODO OOM_ERR if (fkmapValues.fempty()) emptyList() else fkmapValues.asList()
            emptyList()
        }
        override fun containsKey(key: @UnsafeVariance K): Boolean = body.fcontainsKey(key)
        override fun containsValue(value: @UnsafeVariance V): Boolean = body.fcontainsValue(value)
        override operator fun get(key: @UnsafeVariance K): V? = body.ffindValueOfKey(key)
    }}

    override fun asMap(): Map <@UnsafeVariance K, V> = kmap
    TODO end */

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
