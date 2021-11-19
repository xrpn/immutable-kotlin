package com.xrpn.imapi

import com.xrpn.immutable.FT
import com.xrpn.immutable.toUCon
import com.xrpn.immutable.FList
import com.xrpn.immutable.TKVEntry
import kotlin.reflect.KClass

enum class IMSC {
    IMLIST, IMSET, IMMAP, IMTREE, IMSTACK, IMQUEUE, IMENTRY, IMTSDJ
}

interface IMSealed {
    val seal: IMSC
    fun asIMCollection(): IMCommon<*> = this as IMCommon<*>
}

// One or more A
interface IMCommon<out A: Any>: IMSealed {
    fun fall(predicate: (A) -> Boolean): Boolean = fempty() || fcount(predicate) == fsize()
    fun fany(predicate: (A) -> Boolean): Boolean = fempty() || ffindAny(predicate) != null
    fun fcontains(item: @UnsafeVariance A): Boolean
    fun fcount(isMatch: (A) -> Boolean): Int // count the element that match the predicate
    fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMCommon<A>
    fun fdropItem(item: @UnsafeVariance A): IMCommon<A>
    fun fdropWhen(isMatch: (A) -> Boolean): IMCommon<A>
    fun fempty(): Boolean = fpick() == null
    fun ffilter(isMatch: (A) -> Boolean): IMCommon<A> // return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMCommon<A> // Return all elements that do not match the predicate p
    fun ffindAny(isMatch: (A) -> Boolean): A? // Return some element, if any, that matches the predicate p
    fun fisStrict(): Boolean
    fun fnone(predicate: (A) -> Boolean): Boolean = fempty() || ffindAny(predicate) == null
    fun fpick(): A? // peek at one random, easy-to-get i.e. cheap to get element
    fun fpickNotEmpty(): A? = when {
        this.fempty() -> null
        !FT.isContainer(fpick()!!) -> fpick()
        else -> ffindAny {
            it.toUCon()?.let { uc -> !(uc.isEmpty()) } ?:
            throw RuntimeException("internal error, $it:[${it::class}] unknown nested")
        }
    }
    fun fpopAndRemainder(): Pair<A?, IMCommon<A>>
    fun fsize(): Int
    fun fisNested(): Boolean? = if (fempty()) null else FT.isContainer(fpick()!!)
}

interface IMCommonEmpty<out A: Any>: IMCommon<A>

interface IMKeyed<out K> where K: Any, K: Comparable<@kotlin.UnsafeVariance K> {
    fun fcontainsKey(key: @UnsafeVariance K): Boolean
    fun fcountKey(isMatch: (K) -> Boolean): Int // count the values that match the predicate
    fun fdropKeys(keys: IMSet<@UnsafeVariance K>): IMKeyed<K>
    fun ffilterKey(isMatch: (K) -> Boolean): IMKeyed<K>
    fun ffilterKeyNot(isMatch: (K) -> Boolean): IMKeyed<K>
    fun fpickKey(): K?  // peekk at one random key
    fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?,KClass<Any>>): Boolean?
    fun asIMCollection(): IMCommon<*> = this as IMCommon<*>
}

interface IMKeyedValue<out K, out A: Any>: IMKeyed<K> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun asIMBTree(): IMBTree<K,A>
    fun asIMMap(): IMMap<K,A>
    fun fcontainsValue(value: @UnsafeVariance A): Boolean
    fun fcountValue(isMatch: (A) -> Boolean): Int // count the values that match the predicate
    fun ffilterValue(isMatch: (A) -> Boolean): IMKeyedValue<K,A>
    fun ffilterValueNot(isMatch: (A) -> Boolean): IMKeyedValue<K,A>
    fun ffindAnyValue(isMatch: (A) -> Boolean): A?
    fun fget(key: @UnsafeVariance K): A?
    fun fgetOrElse(key: @UnsafeVariance K, default: () -> @UnsafeVariance A): A = fget(key) ?: default()
    fun ftypeSample(): KeyedTypeSample<KClass<Any>?,KClass<Any>>? = fpickValue()?.let { value ->
        (@Suppress("UNCHECKED_CAST") (KeyedTypeSample(fpickKey()!!::class, value::class) as KeyedTypeSample<KClass<Any>?,KClass<Any>>))
    }
    fun fpickValue(): A?  // peek at one random value

    fun fAND(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>
    fun fNOT(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>
    fun fOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>
    fun fXOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>

}

interface IMFoldable<out A: Any> {
    // “Fold” elements using the binary operator o, using an initial seed s
    // The order of traversal is deterministic only if ordering is an intrinsic
    // property; if so, the traversal is from first to last.  Else, the order
    // of traversal is undetermined.  In the latter case, f MUST be commutative
    // or the outcome will also be undetermined and not necessarily repeatable.
    fun <R> ffold(z: R, f: (acc: R, A) -> R): R

}

interface IMReducible<out A: Any> {
    fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A?
}

interface IMOrdered<out A: Any> {
    fun fdrop(n: Int): IMOrdered<A> // Return all elements after the first n elements
    fun freverse(): IMOrdered<A>
    fun frotl(): IMOrdered<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    fun frotr(): IMOrdered<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    fun fswaph(): IMOrdered<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
}

interface IMMappable<out S: Any, out U: IMCommon<S>>: IMCommon<S> {
    fun <T: Any> fmap(f: (S) -> T): IMMappable<T,IMCommon<T>>
    fun <T: Any> flift2map(item: IMCommon<T>): IMMappable<T, IMCommon<T>>? =
        IM.liftToIMMappable(item)
}

interface IMKMappable<out K, out V: Any, out U: IMCommon<TKVEntry<K,V>>> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun <L, T: Any> fmap(f: (TKVEntry<K,V>) -> TKVEntry<L,T>): IMKMappable<L,T,IMCommon<TKVEntry<L,T>>> where L: Any, L: Comparable<@UnsafeVariance L>
}

interface IMMapplicable<out S: Any, out U: IMMappable<S, IMCommon<S>>>: IMCommon<S> {
    fun asIMMappable(): IMMappable<S, IMCommon<S>> = (@Suppress("UNCHECKED_CAST") (this as IMMappable<S, IMCommon<S>>))
    fun <T: Any> fapmap(f: (S) -> T): IMMappable<T,IMCommon<T>> = asIMMappable().fmap(f)
    fun <T: Any> fmapply(op: (U) -> IMMappable<T, IMCommon<T>>): IMMapplicable<T, IMMappable<T, IMCommon<T>>>
    fun <T: Any> flift2maply(item: IMMappable<T, IMCommon<T>>): IMMapplicable<T,IMMappable<T, IMCommon<T>>>? =
        IM.liftToIMMapplicable(item)
}

interface IMSdj<out L, out R>:
    IMCommon<TSDJ<L,R>>

interface IMList<out A:Any>:
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListAltering<A>,
    IMListUtility<A>,
    IMListExtras<A>,
    IMCommon<A>,
    IMFoldable<A>,
    IMReducible<A>,
    IMOrdered<A>,
    IMMappable<A, IMList<A>>,
    IMMapplicable<A, IMList<A>>,
    IMListTyping<A> {
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = ffoldLeft(z, f)
    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? = freduceLeft(f)
}

interface IMStack<out A:Any>:
    IMStackFiltering<A>,
    IMStackGrouping<A>,
    IMStackTransforming<A>,
    IMStackAltering<A>,
    IMStackUtility<A>,
    IMCommon<A>,
    IMOrdered<A>,
    IMMappable<A, IMStack<Nothing>>,
    IMMapplicable<A, IMStack<A>>,
    IMStackTyping<A>

interface IMQueue<out A:Any>:
    IMQueueFiltering<A>,
    IMQueueGrouping<A>,
    IMQueueTransforming<A>,
    IMQueueAltering<A>,
    IMQueueUtility<A>,
    IMCommon<A>,
    IMOrdered<A>,
    IMMappable<A, IMQueue<Nothing>>,
    IMMapplicable<A, IMQueue<A>>,
    IMQueueTyping<A>

interface IMHeap<out A: Any>:
    IMCommon<A>,
    IMMappable<A, IMCommon<A>>,
    IMMapplicable<A, IMHeap<A>>

interface IMSet<out A: Any>:
    IMRSetAltering<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetTransforming<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMCommon<A>,
    IMFoldable<A>,
    IMReducible<A>,
    IMMappable<A, IMSet<Nothing>>,
    IMMapplicable<A, IMSet<A>>,
    IMSetTyping<A> {
    fun asIMSetNotEmpty(): IMSetNotEmpty<A>?
    fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K>
    fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = null
}

interface IMRSetNotEmpty<out A: Any>:
    IMRSetAltering<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMSetTyping<A>,
    IMCommon<A> {
    // fun <KK> edj(): TSDJ<IMSetNotEmpty<@UnsafeVariance A>, IMXSetNotEmpty<KK>> where KK: Any, KK: Comparable<KK>
    fun sxdj(): TSDJ<IMSetNotEmpty<@UnsafeVariance A>, IMXSetNotEmpty<*>>
}

interface IMSetNotEmpty<out A:Any>:
    IMSet<A>,
    IMRSetNotEmpty<A>,
    IMSetAltering<A>,
    IMSetTransforming<A> {
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = this
    override fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> = null
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = this
}

interface IMXSetNotEmpty<out A>:
    IMSet<A>,
    IMRSetNotEmpty<A>,
    IMXSetAltering<A>,
    IMXSetTransforming<A>
        where A: Any, A: Comparable<@UnsafeVariance A> {
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = null
    override fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> =
        @Suppress("UNCHECKED_CAST") (this as IMXSetNotEmpty<K>)
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = this
}

interface IMMap<out K, out V: Any>:
    IMMapFiltering<K, V>,
    IMMapGrouping<K, V>,
    IMMapTransforming<K, V>,
    IMMapUtility<K, V>,
    IMMapAltering<K, V>,
    IMMapExtras<K, V>,
    IMCommon<TKVEntry<K,V>>,
    IMKeyed<K>,
    IMKeyedValue<K, V>,
    IMReducible<V>,
    IMFoldable<TKVEntry<K,V>>,
    IMKMappable<K, V, IMMap<Nothing,Nothing>>,
    IMMapTyping<K, V>
        where K: Any, K: Comparable<@UnsafeVariance K> {

    // IMCollection
    override fun fcount(isMatch: (TKVEntry<K,V>) -> Boolean): Int = // count the element that match the predicate
        asIMBTree().fcount(isMatch)
    override fun fisNested(): Boolean? = if (fempty()) null else FT.isContainer(fpick()!!.getv())
    // IMKeyed
    override fun asIMCollection(): IMCommon<*> = this
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean? =
        if (fempty()) null else null == asIMBTree().ffindAny { tkv -> !(tkv.strictlyLike(sample)) }
    // IMKeyedValue
    override fun asIMMap(): IMMap<K,V> = this

    // derivatives

    // since order is an ambiguous property of Tree, f MUST be commutative
    fun <C> ffoldv(z: C, f: (acc: C, V) -> C): C = // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
        ffold(z) { acc, tkv -> f(acc, tkv.getv()) }
    fun <T: Any> fmapToList(f: (TKVEntry<K, V>) -> T): IMList<T> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv)) }
    fun <W: Any> fmapvToList(f: (V) -> W): IMList<W> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv.getv())) }

}

interface IMBTree<out A, out B: Any>:
    IMBTreeUtility<A, B>,
    IMBTreeTraversing<A, B>,
    IMBTreeFiltering<A, B>,
    IMBTreeGrouping<A, B>,
    IMBTreeTransforming<A,B>,
    IMBTreeAltering<A, B>,
    IMBTreeExtras<A, B>,
    IMCommon<TKVEntry<A,B>>,
    IMKeyed<A>,
    IMKeyedValue<A, B>,
    IMFoldable<TKVEntry<A,B>>,
    IMReducible<TKVEntry<A,B>>,
    IMKMappable<A, B, IMBTree<Nothing,Nothing>>,
    IMBTreeTyping<A, B>
        where A: Any, A: Comparable<@UnsafeVariance A> {

    // IMCollection
    override fun fcount(isMatch: (TKVEntry<A, B>) -> Boolean): Int =
        ffold(0) { acc, item -> if(isMatch(item)) acc + 1 else acc }
    override fun fisNested(): Boolean?  = if (fempty()) null else FT.isContainer(fpick()!!.getv())
    // IMKeyed
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?,KClass<Any>>): Boolean? =
        if (fempty()) null else null == this.ffindAny { tkv -> !(tkv.strictlyLike(sample)) }
    // IMKeyedValue
    override fun asIMBTree(): IMBTree<A,B> = this
    override fun fcontainsValue(value: @UnsafeVariance B): Boolean = if (this.fempty()) false else {
        fun isMatch(entry: TKVEntry<A, B>): Boolean = entry.getv() == value
        ffindAny { isMatch(it) } != null
    }
    override fun fcountValue(isMatch: (B) -> Boolean): Int =  if (this.fempty()) 0 else {
        ffold(0) { acc, tkv -> if(isMatch(tkv.getv())) acc + 1 else acc }
    }

    // derivatives

    // since order is an ambiguous property of Tree, f MUST be commutative
    fun <C> ffoldv(z: C, f: (acc: C, B) -> C): C = // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
        ffold(z) { acc, tkv -> f(acc, tkv.getv()) }
    fun <T: Any> fmapToList(f: (TKVEntry<A, B>) -> T): IMList<T> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv)) }
    fun <C: Any> fmapvToList(f: (B) -> C): IMList<C> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv.getv())) }
}


// ============ INTERNAL

internal interface IMKSet<out K, out A:Any>:
    IMSet<A>,
    IMKeyed<K>,
    IMKeyedValue<K, A>,
    IMKSetGrouping<K, A>,
    IMKSetTransforming<K, A>,
    IMKSetExtras<K, A>,
    IMKSetUtility<K, A>,
    IMKSetTyping<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun isKeyedAlike(rhs: IMSet<@UnsafeVariance A>): Boolean? = if (fempty()) null else {
        rhs as IMKSet<*, A>
        if (rhs.fempty()) null else fpickKey().isStrictly(rhs.fpickKey())
    }
    fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = null
    fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>?
    fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>?

    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean? =
        this.asIMKSetNotEmpty()?.let { null == it.toIMBTree().ffindAny { tkv -> tkv.strictlyLike(sample) }}
 }

internal interface IMKSetNotEmpty<out K, out A:Any>:
    IMKSet<K,A>,
    IMKSetFiltering<K,A>,
    IMRSetNotEmpty<A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun toSetKey(a: @UnsafeVariance A): K
    override fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = this
}

internal interface IMKASetNotEmpty<out K, out A:Any>:
    IMKSetNotEmpty<K, A>,
    IMSetNotEmpty<A>,
    IMKASetAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    // override fun edj(): TSDJ<IMSetNotEmpty<A>, IMXSetNotEmpty<K>> = TSDL(this)
    override fun sxdj() = IMSetDJL(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>? = this
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = null
}

internal interface IMKKSetNotEmpty<out K>:
    IMKSetNotEmpty<K, K>,
    IMXSetNotEmpty<K>,
    IMKKSetTransforming<K>,
    IMKKSetAltering<K>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun sxdj() = IMXSetDJR(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, K>? = null
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = this
}
