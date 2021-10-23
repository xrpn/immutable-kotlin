package com.xrpn.imapi

import com.xrpn.immutable.*
import kotlin.reflect.KClass

enum class IMSC {
    IMLIST, IMSET, IMMAP, IMTREE, IMSTACK, IMQUEUE, IMENTRY
}

interface IMSealed {
    val seal: IMSC
    fun asIMCollection(): IMCollection<*> = this as IMCollection<*>
}

interface IMCollection<out A: Any>: IMSealed {
    fun fall(predicate: (A) -> Boolean): Boolean = fempty() || fcount(predicate) == fsize()
    fun fany(predicate: (A) -> Boolean): Boolean = fempty() || ffindAny(predicate) != null
    fun fcount(isMatch: (A) -> Boolean): Int // count the element that match the predicate
    fun fcontains(item: @UnsafeVariance A): Boolean
    fun fdropWhen(isMatch: (A) -> Boolean): IMCollection<A> = this.ffilterNot(isMatch)
    fun fempty(): Boolean = fpick() == null
    fun ffilter(isMatch: (A) -> Boolean): IMCollection<A> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMCollection<A> // 	Return all elements that do not match the predicate p
    fun ffindAny(isMatch: (A) -> Boolean): A? // Return some element that matches the predicate p
    fun fisStrict(): Boolean
    fun fnone(predicate: (A) -> Boolean): Boolean = fempty() || ffindAny(predicate) == null
    fun fpick(): A? // peek at one random element
    fun fpickNotEmpty(): A? = if (!fisNested()) fpick() else ffindAny {
        !FT.isContainer(it) ||
        it.toUCon()?.let { uc -> !(uc.isEmpty()) } ?:
        throw RuntimeException("internal error, $it:[${it::class}] unknown nested")
    }
    fun fsize(): Int
    fun fisNested(): Boolean  = if (fempty()) false else FT.isContainer(fpick()!!)
}

interface IMKeyed<out K, out A: Any> where K: Any, K: Comparable<@kotlin.UnsafeVariance K> {
    fun fcontainsKey(key: @UnsafeVariance K): Boolean
    fun fcontainsValue(value: @UnsafeVariance A): Boolean
    fun fcountValue(isMatch: (A) -> Boolean): Int // count the values that match the predicate
    fun ffilterKey(isMatch: (K) -> Boolean): IMKeyed<K,A>
    fun ffilterKeyNot(isMatch: (K) -> Boolean): IMKeyed<K,A>
    fun ffilterValue(isMatch: (A) -> Boolean): IMKeyed<K,A>
    fun ffilterValueNot(isMatch: (A) -> Boolean): IMKeyed<K,A>
    fun ffindAnyValue(isMatch: (A) -> Boolean): TKVEntry<K, A>?
    fun fget(key: @UnsafeVariance K): A?
    fun fgetOrElse(key: @UnsafeVariance K, default: () -> @UnsafeVariance A): A = fget(key) ?: default()
    fun fgetOrThrow(key: @UnsafeVariance K): A = fget(key) ?: throw NoSuchElementException("no value for key $key")
    fun fpickKey(): K? = fpickEntry()?.let { it.getk() }  // peekk at one random key
    fun fpickValue(): A? = fpickEntry()?.let { it.getv() }  // peekk at one random key
    fun fpickEntry(): TKVEntry<K, A>?  // peekk at one random key
    fun fisStrictlyLike(sample: KeyedTypeSample<KClass<@UnsafeVariance K>?,KClass<@UnsafeVariance A>>): Boolean?
    fun asIMBTree(): IMBTree<K,A>?
    fun asIMMap(): IMMap<K,A>?
}

interface IMList<out A:Any>:
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListAltering<A>,
    IMListUtility<A>,
    IMListExtras<A>,
    IMCollection<A>

interface IMSet<out A: Any>:
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetTransforming<A>,
    IMSetAltering<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMCollection<A> {
    fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>?
    fun asIMRRSetNotEmpty(): IMRRSetNotEmpty<A>?
    fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = null
}

interface IMSetNotEmpty<out A: Any>:
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetTransforming<A>,
    IMSetAltering<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMCollection<A> {
    fun edj(): TSDJ<IMRSetNotEmpty<@UnsafeVariance A>, IMRRSetNotEmpty<@UnsafeVariance A>>
}

interface IMRSetNotEmpty<out A:Any>:
    IMSet<A>,
    IMSetNotEmpty<A>,
    IMRSetAltering<A> {
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = this
    override fun asIMRRSetNotEmpty(): IMRRSetNotEmpty<A>? = null
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = this
}

interface IMRRSetNotEmpty<out A:Any>:
    IMSet<A>,
    IMSetNotEmpty<A>,
    IMRRSetTransforming<A>,
    IMRRSetAltering<A> {
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = null
    override fun asIMRRSetNotEmpty(): IMRRSetNotEmpty<A>? = this
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = this
}

interface IMMap<out K, out V: Any>:
    IMMapFiltering<K, V>,
    IMMapGrouping<K, V>,
    IMMapTransforming<K, V>,
    IMMapUtility<K, V>,
    IMMapAltering<K, V>,
    IMMapExtras<K, V>,
    IMKeyed<K, V>,
    IMCollection<TKVEntry<K,V>>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun fisNested(): Boolean  = if (fempty()) false else FT.isContainer(fpick()!!.getv())
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<@UnsafeVariance K>?,KClass<@UnsafeVariance V>>): Boolean? =
        if (fempty()) null else null == this.toIMBTree().ffindAny { tkv -> !(tkv.strictlyLike(sample)) }
    override fun asIMMap(): IMMap<K,V>? = this
}

interface IMBTree<out A, out B: Any>:
    IMBTreeTraversing<A, B>,
    IMBTreeFiltering<A, B>,
    IMBTreeGrouping<A, B>,
    IMBTreeTransforming<A,B>,
    IMBTreeAltering<A, B>,
    IMBTreeUtility<A, B>,
    IMKeyed<A, B>,
    IMCollection<TKVEntry<A,B>>
        where A: Any, A: Comparable<@UnsafeVariance A> {

    override fun fcount(isMatch: (TKVEntry<A, B>) -> Boolean): Int =
        this.ffold(0) { acc, item -> if(isMatch(item)) acc + 1 else acc }
    override fun fisNested(): Boolean  = if (fempty()) false else FT.isContainer(fpick()!!.getv())
    override fun fcontainsValue(value: @UnsafeVariance B): Boolean = if (this.fempty()) false else {
        fun isMatch(entry: TKVEntry<A, B>): Boolean = entry.getv() == value
        ffindAny { isMatch(it) } != null
    }
    override fun fcountValue(isMatch: (B) -> Boolean): Int =  if (this.fempty()) 0 else {
        ffold(0) { acc, tkv -> if(isMatch(tkv.getv())) acc + 1 else acc }
    }
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<@UnsafeVariance A>?,KClass<@UnsafeVariance B>>): Boolean? =
        if (fempty()) null else null == this.ffindAny { tkv -> !(tkv.strictlyLike(sample)) }
    override fun asIMBTree(): IMBTree<A,B>? = this
}

interface IMStack<out A:Any>:
    IMStackFiltering<A>,
    IMStackGrouping<A>,
    IMStackTransforming<A>,
    IMStackAltering<A>,
    IMStackUtility<A>,
    IMCollection<A>

interface IMQueue<out A:Any>:
    IMQueueFiltering<A>,
    IMQueueGrouping<A>,
    IMQueueTransforming<A>,
    IMQueueAltering<A>,
    IMQueueUtility<A>,
    IMCollection<A>

// ============ INTERNAL

internal interface IMKSet<out K, out A:Any>:
    IMSet<A>,
    IMKeyed<K, A>,
    IMKSetGrouping<K, A>,
    IMKSetTransforming<K, A>,
    IMKSetExtras<K, A>,
    IMKSetUtility<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<@UnsafeVariance K>?,KClass<@UnsafeVariance A>>): Boolean? =
        this.asIMKSetNotEmpty()?.let { null == it.toIMBTree().ffindAny { tkv -> tkv.strictlyLike(sample) }}
    fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = null
    fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>?
    fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>?
 }

internal interface IMKSetNotEmpty<out K, out A:Any>:
    IMKSet<K,A>,
    IMKSetFiltering<K,A>,
    IMSetNotEmpty<A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun toSetKey(a: @UnsafeVariance A): K
    override fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = this
}

internal interface IMKASetNotEmpty<out K, out A:Any>:
    IMKSetNotEmpty<K, A>,
    IMRSetNotEmpty<A>,
    IMKASetAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun edj() = TSDL(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>? = this
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = null
}

internal interface IMKKSetNotEmpty<out K>:
    IMKSetNotEmpty<K, K>,
    IMRRSetNotEmpty<K>,
    IMKKSetTransforming<K>,
    IMKKSetAltering<K>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun edj() = TSDR(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, K>? = null
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = this
}
