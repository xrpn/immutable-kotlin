package com.xrpn.imapi

import com.xrpn.bridge.FTreeIterator
import com.xrpn.immutable.*
import kotlin.reflect.KClass

interface IMListInvariant<A:Any>: IMListWritable<A>

interface IMList<out A:Any>: IMOrdered<A>,
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListUtility<A>,
    IMListExtras<A>,
    IMReducible<A>,
    IMOrderedMapOp<A, IMList<A>>,
    IMOrderedAppOp<A, IMList<A>>,
    IMListTyping<A> {
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = ffoldLeft(z, f)
    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? = freduceLeft(f)
    fun <B: Any> tibList(): IMListInvariant<B>?
    override fun <B: Any> tibWritable() = tibList<B>()

    companion object {
        internal fun <A: Any> typeInvariantBuilder(): IMListInvariant<A> =
            FList.typeInvariantBuilder()
    }
}

interface IMStackInvariant<A:Any>: IMStackWritable<A>

interface IMStack<out A:Any>: IMOrdered<A>,
    IMStackFiltering<A>,
    IMStackGrouping<A>,
    IMStackTransforming<A>,
    IMStackAltering<A>,
    IMStackUtility<A>,
    IMOrderedMapOp<A, IMStack<Nothing>>,
    IMOrderedAppOp<A, IMStack<A>>,
    IMStackTyping<A> {
    fun <B: Any> tibStack(): IMStackInvariant<B>?
    override fun <B: Any> tibWritable() = tibStack<B>()

    companion object {

        private val tibNone = FStack.typeInvariantBuilder<Any>()

        internal fun <A: Any> typeInvariantBuilder(): IMStackInvariant<A> =
            @Suppress("UNCHECKED_CAST") (tibNone as IMStackInvariant<A>) // FStack.typeInvariantBuilder()
    }
}

interface IMQueueInvariant<A:Any>: IMQueueWritable<A>

interface IMQueue<out A:Any>: IMOrdered<A>,
    IMQueueFiltering<A>,
    IMQueueGrouping<A>,
    IMQueueTransforming<A>,
    IMQueueAltering<A>,
    IMQueueUtility<A>,
    IMOrderedMapOp<A, IMQueue<Nothing>>,
    IMOrderedAppOp<A, IMQueue<A>>,
    IMQueueTyping<A> {
    fun <B: Any> tibQueue(): IMQueueInvariant<B>?
    override fun <B: Any> tibWritable() = tibQueue<B>()

    companion object {
        private val tibNone = FQueue.typeInvariantBuilder<Any>()

        internal fun <A: Any> typeInvariantBuilder(): IMQueueInvariant<A> =
            @Suppress("UNCHECKED_CAST") (tibNone as IMQueueInvariant<A>) // FQueue.typeInvariantBuilder()
    }
}

interface IMSetInvariant<A:Any>:
    IMSetWritable<A>,
    IMSetEquality<A>,
    IMSetLogic<A>

interface IMSet<out A: Any>: IMCommon<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMVSetTransforming<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMReducible<A>,
    IMMapOp<A, IMSet<Nothing>>,
    IMAppOp<A, IMSet<A>>,
    IMSetTyping<A> {
    fun asIMVSetNotEmpty(): IMVSetNotEmpty<A>?
    fun <K> asIMCVSetNotEmpty(): IMCVSetNotEmpty<K>? where K: Any, K: Comparable<K>
    fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = if (fempty()) null else (@Suppress("UNCHECKED_CAST") (this as? IMSetNotEmpty<A>))
    fun <B: Any> tibSet(): IMSetInvariant<B>?
    fun <B: Any> tibWritable(): IMWritable<B>? = tibSet()

    companion object {

        internal fun <A: Any> typeInvariantBuilder(): IMSetInvariant<A> = object : IMSetInvariant<A> {

            override fun fAND(src: IMSet<A>, origin: IMSet<A>): IMSet<A> {
                origin as FKSet<*,A>
                src as FKSet<*,A>
                return origin.fAND(src)
            }

            override fun fNOT(src: IMSet<A>, origin: IMSet<A>): IMSet<A> {
                origin as FKSet<*,A>
                src as FKSet<*,A>
                return origin.fNOT(src)
            }

            override fun fOR(src: IMSet<A>, origin: IMSet<A>): IMSet<A> {
                origin as FKSet<*,A>
                src as FKSet<*,A>
                return origin.fOR(src)
            }

            override fun fXOR(src: IMSet<A>, origin: IMSet<A>): IMSet<A> {
                origin as FKSet<*,A>
                src as FKSet<*,A>
                return origin.fXOR(src)
            }

            override fun fadd(src: A, dest: IMCommon<A>): IMSetNotEmpty<A>? {
                val aux = (dest as? FKSet<*, A>)?.faddUniq(src)
                return aux?.let { if (it.fempty()) null else it.asIMSetNotEmpty() }
            }

            override fun faddUniq(src: A, dest: IMSet<A>): Pair<Boolean, IMSetNotEmpty<A>> {
                val aux = dest.fsize()
                val added = (dest as FKSet<*, A>).faddUniq(src)
                return Pair(aux < added.fsize(), added.asIMSetNotEmpty()!!)
            }

            override fun faddUniqs(src: IMCommon<A>, dest: IMSet<A>): Pair<Int, IMSetNotEmpty<A>?> {
                val aux = src.ffold (dest as FKSet<*, A>) { acc, a -> acc.faddUniq(a) }
                return Pair(aux.fsize() - dest.fsize(), aux.asIMSetNotEmpty())
            }

//            override fun <C:A> faddcUniq(srck: Comparable<A>, srcv: A, dest: IMSet<C>): Pair<Boolean, IMSetNotEmpty<C>> = when {
//                dest.fempty() -> FRBTree.of(srcv.toKKEntry(srck) )
//            }
//
//            override fun <C> faddcUniqs(src: IMCommon<C>, dest: IMSet<C>): Pair<Int, IMSetNotEmpty<C>?> where C: Comparable<A> {
//                val aux = src.ffold (dest as FKSet<*, C>) { acc, a -> acc.faddcUniq(a) }
//                return Pair(aux.fsize() - dest.fsize(), aux.asIMSetNotEmpty()!!)
//            }

            override fun equal(lhs: IMSet<A>, rhs: IMSet<A>): Boolean =
                lhs.equal(rhs)

            override fun softEqual(lhs: IMSet<A>, rhs: Any?): Boolean {
                val aux = @Suppress("UNCHECKED_CAST") (lhs.asIMKCommon<Nothing,A>()!! as? FKSet<*,A>)
                return aux!!.softEqual(rhs)
            }
        }
    }
}

interface IMSetNotEmpty<out A: Any>: IMCommon<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMSetTyping<A> {
    fun <KK> xcvdj(): TSDJ<ErrorMsgTrap, IMCVSetNotEmpty<KK>> where KK : Any, KK : Comparable<KK>
    fun vcvdj(): TSDJ<IMVSetNotEmpty<@UnsafeVariance A>, IMCVSetNotEmpty<*>>
    fun <B: Any> tibSet(): IMSetInvariant<B>?
}

interface IMVSetNotEmpty<out A:Any>: IMSet<A>, IMSetNotEmpty<A>,
    IMVSetTransforming<A> {
    override fun asIMVSetNotEmpty(): IMVSetNotEmpty<A> = vcvdj().left()!!
    override fun <K> asIMCVSetNotEmpty(): IMCVSetNotEmpty<K>? where K: Any, K: Comparable<K> = xcvdj<K>().right()
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A> = this
}

interface IMCVSetNotEmpty<out A>: IMSet<A>, IMVSetNotEmpty<A>,
    IMCVSetTransforming<A>
        where A: Any, A: Comparable<@UnsafeVariance A> {
    override fun asIMVSetNotEmpty(): IMVSetNotEmpty<A> = this
    override fun <K> asIMCVSetNotEmpty(): IMCVSetNotEmpty<K>? where K: Any, K: Comparable<K> =
        @Suppress("UNCHECKED_CAST") (this as? IMCVSetNotEmpty<K>)
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A> = this
}

interface IMHeap<out A: Any>: IMCommon<A>,
    IMMapOp<A, IMHeap<Nothing>>,
    IMAppOp<A, IMHeap<A>>,
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
    IMKMapOp<K, V, IMMap<Nothing,Nothing>>,
    IMMapTyping<K, V>
        where K: Any, K: Comparable<@UnsafeVariance K> {

    // IMCommon
    override fun fcount(isMatch: (TKVEntry<K,V>) -> Boolean): Int = // count the element that match the predicate
        asIMBTree().fcount(isMatch)
    override fun fisNested(): Boolean? = if (fempty()) null else FT.isContainer(fpick()!!.getv())
    // IMKeyed
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>?): Boolean? = sample?.let {
        if (fempty()) null else null == asIMBTree().ffindAny { tkv -> !(tkv.strictlyLike(sample)) }
    }
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

interface IMBTreeInvariant<A,B:Any>:
    IMBTreeWritable<A,B>,
    IMBTreeLogic<A,B>,
    IMBTreeEquality<A, B> where A: Any, A:Comparable<A>

interface IMBTree<out A, out B: Any>: IMCommon<TKVEntry<A,B>>,
    IMBTreeUtility<A, B>,
    IMBTreeTraversing<A, B>,
    IMBTreeFiltering<A, B>,
    IMBTreeGrouping<A, B>,
    IMBTreeTransforming<A,B>,
    IMBTreeExtras<A, B>,
    IMKeyed<A>,
    IMKeyedValue<A, B>,
    IMReducible<TKVEntry<A,B>>,
    IMKMapOp<A, B, IMBTree<Nothing,Nothing>>,
    IMBTreeTyping<A, B>
        where A: Any, A: Comparable<@UnsafeVariance A> {

    override fun fcount(isMatch: (TKVEntry<A, B>) -> Boolean): Int =
        ffold(0) { acc, item -> if(isMatch(item)) acc + 1 else acc }
    override fun fisNested(): Boolean?  = if (fempty()) null else FT.isContainer(fpick()!!.getv())
    // IMKeyed
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?,KClass<Any>>?): Boolean? = sample?.let {
        if (fempty()) null else null == this.ffindAny { tkv -> !(tkv.strictlyLike(it)) }
    }
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

    fun <KK, AA: Any> tibBTree(): IMBTreeInvariant<KK,AA>? where KK: Any, KK: Comparable<@UnsafeVariance KK>
    override fun <KK, AA: Any> tibKWritable(): IMBTreeWritable<KK,AA>? where KK: Any, KK: Comparable<@UnsafeVariance KK> = tibBTree()

    companion object {

        internal fun <A, B: Any> typeInvariantBuilder(): IMBTreeInvariant<A,B> where A: Any, A:Comparable<A> = object : IMBTreeInvariant<A,B> {

            override fun equal(lhs: IMBTree<A, B>, rhs: IMBTree<A, B>): Boolean = when {
                (lhs is FRBTree<A, B>) && (rhs is FRBTree<A, B>) -> lhs.equals(rhs)
                (lhs is FBSTree<A, B>) && (rhs is FBSTree<A, B>) -> lhs.equals(rhs)
                else -> false
            }

            override fun softEqual(lhs: IMBTree<A, B>, rhs: Any?): Boolean = lhs.equals(rhs) || when (rhs) {
                is IMBTree<*, *> -> when {
                    lhs.fempty() -> rhs.fempty()
                    rhs.fempty() -> false
                    rhs.fsize() != lhs.fsize() -> false
                    rhs.froot()!!.strictlyNot(lhs.froot()!!.untype()) -> false
                    rhs.froot()!!.getvKc().isStrictlyNot(lhs.froot()!!.untype().getvKc()) -> false
                    rhs.froot()!!.getkKc().isStrictlyNot(lhs.froot()!!.untype().getkKc()) -> false
                    else -> (@Suppress("UNCHECKED_CAST") (rhs as? IMBTree<A, B>))?.let {
                        IMBTreeEqual2(lhs, it)
                    } ?: false
                }
                is Collection<*> -> when (val iter = rhs.iterator()) {
                    is FTreeIterator<*, *> -> softEqual(lhs, iter.retriever.original())
                    else -> false
                }
                else -> false
            }

            override fun fadd(src: TKVEntry<A, B>, dest: IMKeyedValue<A, B>): IMBTree<A, B>? = try {
                dest.tibKCommon<A,B>()?.fadd(src, dest)?.asIMBTree()
            } catch (ex: Exception) {
                dest.reportException(ex, this)
                null
            }

            override fun fadd(src: TKVEntry<A, B>, dest: IMBTree<A, B>): IMBTree<A, B> = when (dest) {
                is FRBTree -> dest.finsertTkv(src)
                is FBSTree -> dest.finsertTkv(src)
                else -> throw RuntimeException("internal error, unknown ${IMBTree::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
            }

            override fun faddAll(src: IMCommon<TKVEntry<A, B>>, dest: IMBTree<A, B>): IMBTree<A, B> =
                dest.tibKCommon<A,B>()?.faddAll(src, dest)?.asIMBTree() ?: dest

            override fun finserts(src: IMKeyedValue<A, B>, dest: IMBTree<A, B>): IMBTree<A, B> = when (dest) {
                is FRBTree<A, B> -> src.asIMCommon<TKVEntry<A, B>>()!!
                    .ffold(dest) { frbt, tkv -> fadd(tkv, frbt) as FRBTree<A, B> }
                is FBSTree<A, B> -> src.asIMCommon<TKVEntry<A, B>>()!!
                    .ffold(dest) { fbst, tkv -> fadd(tkv, fbst) as FBSTree<A, B> }
                else -> throw RuntimeException("internal error, unknown ${IMBTree::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
            }

            override fun fAND(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B> = when (origin) {
                is FRBTree<A, B> -> origin.fAND(src)
                is FBSTree<A, B> -> origin.fAND(src)
                else -> throw RuntimeException("internal error, cannot fAND ${src::class} to ${this::class}")
            }

            override fun fNOT(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B> = when (origin) {
                is FRBTree<A, B> -> origin.fNOT(src)
                is FBSTree<A, B> -> origin.fNOT(src)
                else -> throw RuntimeException("internal error, cannot fNOT ${src::class} to ${this::class}")
            }

            override fun fOR(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B> = when (origin) {
                is FRBTree<A, B> -> origin.fOR(src)
                is FBSTree<A, B> -> origin.fOR(src)
                else -> throw RuntimeException("internal error, cannot fOR ${src::class} to ${this::class}")
            }

            override fun fXOR(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B> = when (origin) {
                is FRBTree<A, B> -> origin.fXOR(src)
                is FBSTree<A, B> -> origin.fXOR(src)
                else -> throw RuntimeException("internal error, cannot fXOR ${src::class} to ${this::class}")
            }
        }
    }
}

interface IMBTreeNotEmpty<out A, out B: Any>: IMBTree<A,B> where A: Any, A: Comparable<@UnsafeVariance A>

// ============ INTERNAL

internal interface IMKSetInvariant<K,A:Any>:
    IMKSetWritable<K,A>,
    IMKSetLogic<K,A>
        where K: Any, K:Comparable<K>

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
    fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = if (fempty()) null else (@Suppress("UNCHECKED_CAST") (this as IMKSetNotEmpty<K,A>))
    fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>?
    fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>?
    // Keyed
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>?): Boolean? = sample?.let {
        this.asIMKSetNotEmpty()?.let { null == it.toIMBTree().ffindAny { tkv -> tkv.strictlyLike(sample) } }
    }

    fun <KK, AA: Any> tibKSet(): IMKSetInvariant<KK,AA>? where KK: Any, KK: Comparable<@UnsafeVariance KK>

    companion object {

        internal fun <K, A: Any> typeInvariantBuilder(): IMKSetInvariant<K,A> where K: Any, K:Comparable<K> = object : IMKSetInvariant<K,A> {

            override fun fadd(src: TKVEntry<K, A>, dest: IMKeyedValue<K, A>): IMKSetNotEmpty<K, A>? =
                (dest as? FKSet<K,A>)?.faddUniqTkv(src)?.asIMKASetNotEmpty()

            override fun faddUniq(src: TKVEntry<K,A>, dest: IMKSet<K, A>): Pair<Boolean, IMKSetNotEmpty<K, A>> {
                val aux = (dest as FKSet<K, A>).faddUniqTkv(src)
                return Pair(aux.fsize() > dest.fsize(), aux.asIMKSetNotEmpty()!!)
            }

            override fun faddUniqs(src: IMCommon<TKVEntry<K,A>>, dest: IMKSet<K, A>): Pair<Int, IMKSetNotEmpty<K, A>?> =
                if (src.fempty() && dest.fempty()) Pair(0, null) else {
                    val prior = dest.fsize()
                    val res = src.ffold(dest){ kset, tkv -> (kset as FKSet<K,A>).faddUniqTkv(tkv) }
                    Pair(res.fsize() - prior, res.asIMKSetNotEmpty())
                }

            override fun faddkUniq(src: TKVEntry<K,K>, dest: IMKSet<K, K>): Pair<Boolean, IMKSetNotEmpty<K, K>> {
                val aux = (dest as FKSet<K, K>).faddUniqTkv(src)
                return Pair(aux.fsize() > dest.fsize(), aux.asIMKSetNotEmpty()!!)
            }

            override fun faddkUniqs(src: IMCommon<TKVEntry<K,K>>, dest: IMKSet<K, K>): Pair<Int, IMKSetNotEmpty<K, K>?> =
                if (src.fempty() && dest.fempty()) Pair(0, null)
                else {
                    val prior = dest.fsize()
                    val res = src.ffold(dest){ kset, tkv -> (kset as FKSet<K,K>).faddcUniq(tkv.getv()) }
                    Pair(res.fsize() - prior, res.asIMKSetNotEmpty())
                }

            override fun fAND(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K, A> = when(origin) {
                is FKSet<K,A> -> origin.fAND(src)
                else -> throw RuntimeException("internal error, cannot fAND ${src::class} to ${this::class}")
            }

            override fun fNOT(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K, A> = when(origin) {
                is FKSet<K,A> -> origin.fNOT(src)
                else -> throw RuntimeException("internal error, cannot fNOT ${src::class} to ${this::class}")
            }

            override fun fOR(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K, A> = when(origin) {
                is FKSet<K,A> -> origin.fOR(src)
                else -> throw RuntimeException("internal error, cannot fOR ${src::class} to ${this::class}")
            }

            override fun fXOR(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K, A> = when(origin) {
                is FKSet<K,A> -> origin.fXOR(src)
                else -> throw RuntimeException("internal error, cannot fXOR ${src::class} to ${this::class}")
            }
        }
    }
}

internal interface IMKSetNotEmpty<out K, out A:Any>: IMKSet<K,A>,
    IMKSetFiltering<K,A>,
    IMSetNotEmpty<A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun toSetKey(a: @UnsafeVariance A): K
    override fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A> = this
}

internal interface IMKASetNotEmpty<out K, out A:Any>: IMKSetNotEmpty<K, A>,
    IMVSetNotEmpty<A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun vcvdj() = IMSetDJL(this)
    override fun <J> xcvdj(): TSDJ<ErrorMsgTrap, IMCVSetNotEmpty<J>> where J: Any, J:Comparable<J> =
        @Suppress("UNCHECKED_CAST") (ErrorMsgTrap("failure: ${this::class.simpleName}") as TSDJ<ErrorMsgTrap, IMCVSetNotEmpty<J>>)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A> = this
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = null
}

internal interface IMKKSetNotEmpty<out K>: IMKSetNotEmpty<K, K>,
    IMCVSetNotEmpty<K>,
    IMKKSetTransforming<K>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun vcvdj() = IMCVSetDJR(this)
    override fun <J> xcvdj(): TSDJ<ErrorMsgTrap, IMCVSetNotEmpty<J>> where J:Any, J:Comparable<J> =
        @Suppress("UNCHECKED_CAST") (IMCVSetDJR(this) as TSDJ<ErrorMsgTrap, IMCVSetNotEmpty<J>>)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, K>? = null
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K> = this
}
