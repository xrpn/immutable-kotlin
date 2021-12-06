package com.xrpn.imapi

import com.xrpn.immutable.*
import com.xrpn.immutable.FT
import com.xrpn.immutable.toUCon
import kotlin.reflect.KClass

enum class IMSC {
    IMLIST,
    IMSET,
    IMMAP,
    IMTREE,
    IMSTACK,
    IMQUEUE,
    IMENTRY,
    IMTSDJ,
    IMDIW,
    IMDMW,
    IMDAW,
    IMKART
}

fun Any?.asIMUniCo(): IMUniversalCommon? = this?.let { if(it is IMUniversalCommon) it else null }

fun <T: Any> Any.asIMCommon(): IMCommon<T>? = try {
        @Suppress("UNCHECKED_CAST") (this as IMCommon<T>)
    } catch(ex: ClassCastException) {
        IMSystemErrLogging(this::class).emitUnconditionally("fail with  ${ex::class.simpleName}(${ex.message}) for ${this::class} as $this")
        null
    }

interface IMUniversalCommon

interface IMSealed: IMUniversalCommon {
    val seal: IMSC
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
    // “Fold” element(s) using the binary operator f and using the initial seed z
    // The order of traversal is deterministic only if ordering is an intrinsic
    // property; if so, the traversal is from first to last.  Else, the order
    // of traversal is undetermined.  In the latter case, f MUST be commutative
    // or the outcome will also be undetermined and not necessarily repeatable.
    fun <R> ffold(z: R, f: (acc: R, A) -> R): R
    fun fisNested(): Boolean? = if (fempty()) null else FT.isContainer(fpick()!!)
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

    companion object {
        //fun <T: Any, S: Any> from(t: T): IMCommon<S>? = t.asIMUniCo()?.asIMCommon()
        //fun <T: Any> of(t: T): IMCommon<T> = DWCommon.of(t)
        //fun <T: Any> fliftToCommon(t: T): IMCommon<T> = from(t) ?: of(t)

        fun <T: Any> equal(lhs: IMCommon<T>, rhs: IMCommon<T>): Boolean = when {
            lhs === rhs -> true
            lhs.fempty() && rhs.fempty() -> true
            lhs.fsize() != rhs.fsize() -> false
            lhs.isStrictly(rhs) -> /* TODO consider taint from mutable content */ lhs.fisStrict() && rhs.fisStrict() && lhs.equals(rhs)
            else -> false
        }

//        internal open class IMCommonEquality: EqualsProxy, HashCodeProxy {
//            override fun equals(other: Any?): Boolean = other?.let { when(it) {
//                is IMCommon<*> -> equal(it)
//                is Collection<*> -> it.isEmpty()
//                else -> false
//            }} ?: false
//            override fun hashCode(): Int = javaClass.hashCode()
//        }

    }
}

interface IMWritable<out A: Any> {
    fun fadd(item: @UnsafeVariance A): IMWritable<A>
}

interface IMCommonEmpty<out A: Any>: IMCommon<A> {
    override fun fcount(isMatch: (A) -> Boolean): Int = 0
    override fun fcontains(item: @UnsafeVariance A): Boolean = false
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMCommon<@UnsafeVariance A> = this
    override fun fdropItem(item: @UnsafeVariance A): IMCommon<@UnsafeVariance A> = this
    override fun fdropWhen(isMatch: (A) -> Boolean): IMCommon<A> = this
    override fun ffilter(isMatch: (A) -> Boolean): IMCommon<A> = this
    override fun ffilterNot(isMatch: (A) -> Boolean): IMCommon<A> = this
    override fun ffindAny(isMatch: (A) -> Boolean): A? = null
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = z
    override fun fisStrict(): Boolean = true
    override fun fpick(): Nothing? = null
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(null, this)
    override fun fsize(): Int = 0
    companion object {
        fun equal(other: IMCommon<*>): Boolean = other.fempty()
        internal open class IMCommonEmptyEquality: EqualsProxy, HashCodeProxy {
            override fun equals(other: Any?): Boolean = other?.let { when(it) {
                is IMCommon<*> -> it.fempty()
                is Collection<*> -> it.isEmpty()
                else -> false
            }} ?: false
            override fun hashCode(): Int = javaClass.hashCode()
        }
    }
}

interface IMKeyed<out K>: IMUniversalCommon where K: Any, K: Comparable<@kotlin.UnsafeVariance K> {
    fun fcontainsKey(key: @UnsafeVariance K): Boolean
    fun fcountKey(isMatch: (K) -> Boolean): Int // count the values that match the predicate
    fun fdropKeys(keys: IMSet<@UnsafeVariance K>): IMKeyed<K>
    fun ffilterKey(isMatch: (K) -> Boolean): IMKeyed<K>
    fun ffilterKeyNot(isMatch: (K) -> Boolean): IMKeyed<K>
    fun fpickKey(): K?  // peekk at one random key
    fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?,KClass<Any>>): Boolean?
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

interface IMReducible<out A: Any>: IMUniversalCommon {
    fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A?
}

interface IMOrdered<out A: Any>: IMUniversalCommon {
    fun fdrop(n: Int): IMOrdered<A> // Return all elements after the first n elements
    fun fnext(): Pair<A?, IMOrdered<A>>
    fun freverse(): IMOrdered<A>
    fun frotl(): IMOrdered<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    fun frotr(): IMOrdered<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    fun fswaph(): IMOrdered<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
    fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Pair<A,B>>
}

interface IMOrderedEmpty<out A: Any>: IMOrdered<A> {
    override fun fdrop(n: Int): IMOrdered<A> = this
    override fun fnext(): Pair<A?, IMOrdered<A>> = Pair(null,this)
    override fun freverse(): IMOrdered<A> = this
    override fun frotl(): IMOrdered<A> = this
    override fun frotr(): IMOrdered<A> = this
    override fun fswaph(): IMOrdered<A> = this
}

typealias FMap<S> = IMMapOp<S, IMCommon<S>>

interface IMMapOp<out S: Any, out U: IMCommon<S>>: IMCommon<S> {

    fun <T: Any> fmap(f: (S) -> T): FMap<T>

    companion object {

//        fun <T: Any> equal(rhs: FMap<T>, lhs: FMap<T>): Boolean = TODO()

        fun <T: Any> flift2map(item: IMCommon<T>): FMap<T>? = IM.liftToIMMappable(item)
        fun <T: Any> flift2map(item: T): FMap<T> {
            check(item !is IMCommon<*>)
            return DWFMap.of(item)
        }
    }
}

interface IMKMappable<out K, out V: Any, out U: IMCommon<TKVEntry<K,V>>> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun <L, T: Any> fmap(f: (TKVEntry<K,V>) -> TKVEntry<L,T>): IMKMappable<L,T,IMCommon<TKVEntry<L,T>>> where L: Any, L: Comparable<@UnsafeVariance L>
}

typealias FMapp<S> = IMMappOp<S, IMMapOp<S, IMCommon<S>>>

interface IMMappOp<out S: Any, out U: FMap<S>>: IMCommon<S> {

    fun asFMap(): FMap<S> = (@Suppress("UNCHECKED_CAST") (this as FMap<S>))

    fun <T: Any> fmapp(f: (S) -> T): FMapp<T> =
        flift2mapp(asFMap().fmap(f))!!
// {
//        fun fLifted(u: FMapp<S>): (IMCommon<(S) -> T>) -> FMap<T> = { g: IMCommon<(S) -> T> ->
//            check(1 == g.fsize())
//            u.asFMap().fmap(g.fpick()!!)
//        }
//        return flift2mapp(IMCommon.fliftToCommon(f))!!.fapp(fLifted(this))
//  }

    fun <T: Any> fapp(op: (U) -> FMap<T>): FMapp<T>

    fun <T: Any, V: FMap<T>> fmaprod(f: (U) -> V): FMap<Pair<U,V>> {
        fun fLifted(u: FMapp<S>): ((FMap<S>) -> FMap<T>) -> Pair<FMap<S>,FMap<T>> = { g: (FMap<S>) -> FMap<T> ->
            val aux: FMap<T> = u.fapp(g).asFMap()
            Pair(u.asFMap(), aux)
        }
        val fmapp: FMapp<S> = flift2mapp(this)!!
        val faux: ((U) -> V) -> FMap<Pair<U,V>> = { _: (U) -> V ->
            val aux = @Suppress("UNCHECKED_CAST") (fLifted(fmapp) as ((U) -> V) -> FMap<Pair<U,V>>)
            IMMapOp.flift2map(aux(f))!!
        }
        return faux(f)
    }

    // will return: success, if all succeed; xor: only errors (all errors), if any
    fun <R: Any> ftraverse(op: (S) -> R): IMSdj<IMCommon<String>, IMCommon<R>> {
        val fail: (S?) -> String = { v:S? -> "op error during traversal, ${v?.let{ it::class }} item $v" }
        val impl: TraversalImpl<S, String> = TraversalImpl(fail, this)
        return impl.traverse<R, String>(op)
    }

    // will return any and all success, and any and all failures
    fun <R: Any> fgrossTraverse(op: (S) -> R): Pair<IMCommon<String>, IMCommon<R>> {
        val fail: (S?) -> String = { v:S? -> "op error during traversal, ${v?.let{ it::class }} item $v" }
        val impl: TraversalImpl<S, String> = TraversalImpl(fail, this)
        return impl.grossTraverse<R, String>(op)
    }

    fun <R: Any, E: Any> ftraverseWithError(op: (S) -> R, toError: ((String) -> E)): TSDJ<IMCommon<E>, IMCommon<R>> {
        val fail: (S?) -> E = { v:S? -> toError("for:${v?.let{ it::class }} item $v") }
        val impl: TraversalImpl<S, E> = TraversalImpl(fail, this)
        return impl.traverse<R,E>(op)
    }

    infix fun <B: Any, C> fmapprod(bk: FMapp<B>): FMapp<C> where C: Pair<@UnsafeVariance S, B> = if (fempty() || bk.fempty()) flift2mapp(DWFMap.empty())!! else TODO()

    infix fun  <T: Any> fmApply(fmapp: FMapp<T>): FMapp<FMapp<T>> = TODO()

    companion object {
        // fun <T: Any> equal(rhs: FMapp<T>, lhs: FMapp<T>): Boolean = TODO()

        fun <T: Any> flift2mapp(item: FMap<T>) = IM.liftToIMMapplicable(item)
        fun <T: Any> flift2mapp(item: IMCommon<T>): FMapp<T>? = IMMapOp.flift2map(item)?.let { mappable -> flift2mapp(mappable) }
        fun <T: Any> flift2mapp(item: T): FMapp<T>? {
            check(item !is IMCommon<*>)
            return IMMapOp.flift2map(item).let { mapOp: IMMapOp<T, IMCommon<T>> -> flift2mapp(mapOp) }
        }
    }
}

typealias FKart<S, T> = IMCartesian<S, FMap<S>, T, Pair<S,T>>

interface IMCartesian<out S: Any, out U: FMap<S>, out T: Any, out W:Pair<S,T>> {

    infix fun mpro(t: FMap<@UnsafeVariance T>): FMap<W>?
    infix fun opro(t: IMOrdered<@UnsafeVariance T>): FMap<W>?

    companion object {
        fun <S: Any, T: Any> flift2kart(item: IMOrdered<S>): FKart<S,T> = FCartesian.of(item)
    }
}

// simple disjunction
interface IMDj<out L, out R>: IMCommon<IMDj<L,R>>,
    IMDjFiltering<L, R> {
    companion object {
        fun <A, B, L:Any, R: Any> bifold(djs: IMCommon<IMDj<A,B>>, acc:Pair<IMList<L>, IMList<R>>? = null): ((A) -> L, (B) -> R) -> Pair<IMList<L>, IMList<R>> = { fl: (A) -> L, fr: (B) -> R ->
            val biAcc = acc ?: Pair(FList.emptyIMList(),FList.emptyIMList())
            fun apportion (acc:Pair<IMList<L>, IMList<R>>, item:IMDj<A,B>): Pair<IMList<L>, IMList<R>> =
                item.right()?.let { dr -> Pair(acc.first, acc.second.fprepend(fr(dr))) } ?: Pair(acc.first.fprepend(fl(item.left()!!)), acc.second)
            djs.ffold(biAcc, ::apportion)
        }
    }
}

// higher kind disjunction
interface IMSdj<out L, out R>: IMDj<L,R>,
    FMap<IMDj<L,R>>,
    FMapp<IMDj<L,R>> {
}

// Di-sposable x wrappers
interface IMDisw<out A: Any, out B: IMCommon<A>>:
    IMCommon<A>,
    IMOrdered<A>

interface IMDimw<out A: Any, out B: IMCommon<A>>:
    IMMapOp<A, IMDimw<A,B>>,
    IMOrdered<A>

interface IMDiaw<out A: Any, out B: IMCommon<A>>:
    IMMapOp<A, IMDiaw<A,B>>,
    IMMappOp<A, FMap<A>>,
    IMOrdered<A>

// collections

interface IMList<out A:Any>: IMCommon<A>,
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListAltering<A>,
    IMListUtility<A>,
    IMListExtras<A>,
    IMReducible<A>,
    IMOrdered<A>,
    IMMapOp<A, IMList<A>>,
    IMMappOp<A, IMList<A>>,
    IMListTyping<A> {
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = ffoldLeft(z, f)
    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? = freduceLeft(f)
}

interface IMStack<out A:Any>: IMCommon<A>,
    IMStackFiltering<A>,
    IMStackGrouping<A>,
    IMStackTransforming<A>,
    IMStackAltering<A>,
    IMStackUtility<A>,
    IMOrdered<A>,
    IMMapOp<A, IMStack<Nothing>>,
    IMMappOp<A, IMStack<A>>,
    IMStackTyping<A>

interface IMQueue<out A:Any>: IMCommon<A>,
    IMQueueFiltering<A>,
    IMQueueGrouping<A>,
    IMQueueTransforming<A>,
    IMQueueAltering<A>,
    IMQueueUtility<A>,
    IMOrdered<A>,
    IMMapOp<A, IMQueue<Nothing>>,
    IMMappOp<A, IMQueue<A>>,
    IMQueueTyping<A>

interface IMSet<out A: Any>: IMCommon<A>,
    IMRSetAltering<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetTransforming<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMReducible<A>,
    IMMapOp<A, IMSet<Nothing>>,
    IMMappOp<A, IMSet<A>>,
    IMSetTyping<A> {
    fun asIMSetNotEmpty(): IMSetNotEmpty<A>?
    fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K>
    fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = null
}

interface IMHeap<out A: Any>: IMCommon<A>,
    IMWritable<A>,
    IMMapOp<A, IMHeap<Nothing>>,
    IMMappOp<A, IMHeap<A>>,
    IMHeapTyping<A>

interface IMMap<out K, out V: Any>: IMCommon<TKVEntry<K,V>>,
    IMMapFiltering<K, V>,
    IMMapGrouping<K, V>,
    IMMapTransforming<K, V>,
    IMMapUtility<K, V>,
    IMMapAltering<K, V>,
    IMMapExtras<K, V>,
    IMKeyed<K>,
    IMKeyedValue<K, V>,
    IMReducible<V>,
//    IMFoldable<TKVEntry<K,V>>,
    IMKMappable<K, V, IMMap<Nothing,Nothing>>,
    IMMapTyping<K, V>
        where K: Any, K: Comparable<@UnsafeVariance K> {

    // IMCommon
    override fun fcount(isMatch: (TKVEntry<K,V>) -> Boolean): Int = // count the element that match the predicate
        asIMBTree().fcount(isMatch)
    override fun fisNested(): Boolean? = if (fempty()) null else FT.isContainer(fpick()!!.getv())
    // IMKeyed
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

interface IMBTree<out A, out B: Any>: IMCommon<TKVEntry<A,B>>,
    IMBTreeUtility<A, B>,
    IMBTreeTraversing<A, B>,
    IMBTreeFiltering<A, B>,
    IMBTreeGrouping<A, B>,
    IMBTreeTransforming<A,B>,
    IMBTreeAltering<A, B>,
    IMBTreeExtras<A, B>,
    IMKeyed<A>,
    IMKeyedValue<A, B>,
//    IMFoldable<TKVEntry<A,B>>,
    IMReducible<TKVEntry<A,B>>,
    IMKMappable<A, B, IMBTree<Nothing,Nothing>>,
    IMBTreeTyping<A, B>
        where A: Any, A: Comparable<@UnsafeVariance A> {

    // IMCommon
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

// Set derivatives

interface IMRSetNotEmpty<out A: Any>: IMCommon<A>,
    IMRSetAltering<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMSetTyping<A> {
    // fun <KK> edj(): TSDJ<IMSetNotEmpty<@UnsafeVariance A>, IMXSetNotEmpty<KK>> where KK: Any, KK: Comparable<KK>
    fun sxdj(): TSDJ<IMSetNotEmpty<@UnsafeVariance A>, IMXSetNotEmpty<*>>
}

interface IMSetNotEmpty<out A:Any>: IMSet<A>, IMRSetNotEmpty<A>,
    IMSetAltering<A>,
    IMSetTransforming<A> {
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = this
    override fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> = null
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = this
}

interface IMXSetNotEmpty<out A>: IMSet<A>, IMRSetNotEmpty<A>,
    IMXSetAltering<A>,
    IMXSetTransforming<A>
        where A: Any, A: Comparable<@UnsafeVariance A> {
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = null
    override fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> =
        @Suppress("UNCHECKED_CAST") (this as IMXSetNotEmpty<K>)
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = this
}

// ============ INTERNAL

internal interface IMKSet<out K, out A:Any>: IMSet<A>,
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

internal interface IMKSetNotEmpty<out K, out A:Any>: IMKSet<K,A>,
    IMKSetFiltering<K,A>,
    IMRSetNotEmpty<A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun toSetKey(a: @UnsafeVariance A): K
    override fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = this
}

internal interface IMKASetNotEmpty<out K, out A:Any>: IMKSetNotEmpty<K, A>,
    IMSetNotEmpty<A>,
    IMKASetAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    // override fun edj(): TSDJ<IMSetNotEmpty<A>, IMXSetNotEmpty<K>> = TSDL(this)
    override fun sxdj() = IMSetDJL(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>? = this
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = null
}

internal interface IMKKSetNotEmpty<out K>: IMKSetNotEmpty<K, K>,
    IMXSetNotEmpty<K>,
    IMKKSetTransforming<K>,
    IMKKSetAltering<K>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun sxdj() = IMXSetDJR(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, K>? = null
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = this
}
