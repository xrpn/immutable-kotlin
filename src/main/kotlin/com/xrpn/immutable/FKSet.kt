package com.xrpn.immutable

import com.xrpn.bridge.FKSetIterator
import com.xrpn.imapi.*
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.FList.Companion.toIMList
import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import com.xrpn.immutable.FRBTree.Companion.finsertIK
import com.xrpn.immutable.FRBTree.Companion.finsertSK
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.ofvi
import com.xrpn.immutable.FRBTree.Companion.ofvs
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
import com.xrpn.immutable.TKVEntry.Companion.ofStrKey
import com.xrpn.immutable.TKVEntry.Companion.ofIntKey
import com.xrpn.immutable.TKVEntry.Companion.toEntry
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toKKEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import kotlin.reflect.KClass

internal interface FKSetRetrieval<out K, out A: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun original(): FKSet<K,A>
}

sealed class FKSet<out K, out A: Any> constructor (protected val body: FRBTree<K, A>): IMKSet<K, A> where K: Any, K: Comparable<@UnsafeVariance K> {

    @Deprecated("Set has no ordering.", ReplaceWith("ffilterNot"))
    fun dropWhile(predicate: (A) -> Boolean): List<A> = throw RuntimeException(predicate.toString())
    @Deprecated("Set has no ordering.", ReplaceWith("ffilter()"))
    fun takeWhile(predicate: (A) -> Boolean): List<A> = throw RuntimeException(predicate.toString())
    @Deprecated("Set has no ordering.", ReplaceWith("fpermutations() or fcombinations()"))
    fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<A>> = throw RuntimeException("$size $step $partialWindows")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun <B> runningFold(initial: B, operation: (acc: B, A) -> B): List<B> = throw RuntimeException("$initial $operation")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun <B> runningFoldIndexed(initial: B, operation: (index: Int, acc: B, A) -> B): List<B> = throw RuntimeException("$initial $operation")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun runningReduce(operation: (acc: A, A) -> @UnsafeVariance A): List<A> = throw RuntimeException("$operation")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun runningReduceIndexed(operation: (index: Int, acc: A, A) -> @UnsafeVariance A): List<A> = throw RuntimeException("$operation")
    @Deprecated("Set has no ordering.", ReplaceWith("withIndex()"))
    fun <B, C> zip(other: Array<out B>, transform: (a: A, b: B) -> C): List<C> = throw RuntimeException("$other, $transform")
    @Deprecated("Set has no ordering.", ReplaceWith("withIndex()"))
    fun <B> zip(other: Iterable<B>): List<Pair<A, B>> = throw RuntimeException("$other")
    @Deprecated("Set has no ordering.", ReplaceWith("fpermutations() or fcombinations()"))
    fun zipWithNext(): List<Pair<A, A>> = throw RuntimeException()
    @Deprecated("Set has no ordering.", ReplaceWith("fpermutations().fmap() or fcombinations().fmap()"))
    fun <B> zipWithNext(transform: (a: A, b: A) -> B): List<B>  = throw RuntimeException("$transform")

    // from Any

    // short of type erasure, this must maintain reflexive, symmetric and transitive properties
    override fun equals(other: Any?): Boolean = asIMKSetNotEmpty()?.let { fksetNe -> when {
        fksetNe === other -> true
        other == null -> false
        other is Set<*> -> when(val iter = other.iterator()) {
            is FKSetIterator<*, *> -> equals(iter.retriever.original())
            else -> false
        }
        other !is IMKeyedValue<*,*> -> false
        other.fpickKey() == null /* i.e.empty */ -> false
        !(other.ftypeSample()?.isLikeValue(ftypeSample()?.vKc) ?: false) -> false
        else -> (other as? IMKSetNotEmpty<*,*>)?.let {
            IMRSetEqual2(this, it ) /* TODO remove in time */ && run { check(hashCode()==other.hashCode()); true }
        } ?: false
    }} ?: throw RuntimeException("internal error, ${this::class} (size:${this.fsize()}) must not be empty")

    override fun softEqual(rhs: Any?): Boolean = equals(rhs) || when (rhs) {
        is Set<*> -> when {
            rhs.isEmpty() -> fempty()
            rhs.size != fsize() -> false
            else -> rhs.equals(asSet())
        }
        else -> false
    }

    open val hash: Int by lazy {
        check(null != this.asIMKSetNotEmpty())
        // for set hash, only unordered A items (values) matter; the hash operation must be associative and commutative
        val aux = this.body.ffold(131 * fsize()) { hash, tkv -> hash + tkv.getv().hashCode() }
        17 * fsize() + aux
    }
    
    override fun hashCode(): Int = hash

    private val set: Set<A> by lazy { object : Set<A>, FKSetRetrieval<K,A> {
        // from Collection<A>
        override val size: Int by lazy { this@FKSet.fsize() }
        override operator fun contains(element: @UnsafeVariance A): Boolean = this@FKSet.fcontains(element)
        override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
            elements.forEach { if (!this@FKSet.fcontains(it)) return false }
            return true
        }
        override fun isEmpty(): Boolean = this@FKSet.fempty()
        override fun iterator(): FKSetIterator<K, A> = FKSetIterator(this@FKSet)
        override fun original(): FKSet<K, A> = this@FKSet
        override fun equals(other: Any?): Boolean = this@FKSet.softEqual(other)
        override fun hashCode(): Int = this@FKSet.hashCode()
    }}

    override fun asSet(): Set<A> = set

    override operator fun contains(element: @UnsafeVariance A): Boolean = when (this) {
        is FKSetEmpty -> false
        else -> this.fcontains(element)
    }

    // imcommon

    override val seal: IMSC = IMSC.IMSET

    override fun fcontains(item: @UnsafeVariance A?): Boolean = when (this) {
        is FKSetEmpty -> false
        else -> item?.let{ body.fcontainsKey(toKey(it)) } ?: false
    }

    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): FKSet<K,A> = if (items.fempty() || this.fempty()) this else
        items.ffold(this) { acc, a -> if (acc.fcontains(a)) acc.fdropItem(a) else acc }

    override fun fdropItem(item: @UnsafeVariance A): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> toTKVEntry(item)?.let { body.fdropItem(it).toIMSet(fkeyTypeOrNull()!!) } ?: throw RuntimeException("cannot drop `$item` from `$this`")
    }

    override fun ffindAny(isMatch: (A) -> Boolean): A? = when (this) {
        is IMKSetNotEmpty<*, *> -> body.ffindAnyValue(isMatch)
        else -> null
    }

    private val strictness: Boolean by lazy { when (this) {
        is IMKSetNotEmpty<*, *> -> body.fisStrict()
        else -> true
    }}

    override fun fisStrict(): Boolean = strictness

    override fun fpick(): A? = body.froot()?.getv()

    override fun fpopAndRemainder(): Pair<A?, FKSet<K, A>> = when (this) {
        is IMKSetNotEmpty<*,*> -> {
            val (item, items) = body.fpopAndRemainder()
            item?.let {
                if (items.fempty()) Pair(item.getv(), this.toEmpty())
                else Pair(item.getv(), ofBody(items)!!)
            } ?: Pair(null, this)
        }
        else -> Pair(null, this)
    }

    override fun toEmpty(): FKSet<K,A> = when (this) {
        is FIKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (FIKSetEmpty.empty<A>() as FKSet<K,A>)
        is FSKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (FSKSetEmpty.empty<A>() as FKSet<K,A>)
        is FKKSetNotEmpty<*> -> @Suppress("UNCHECKED_CAST") (FKKSetEmpty.empty<K>() as FKSet<K,A>)
        else -> this
    }

    fun fpickNested(): Any? = body.froot()?.let { rt -> rt.toUCon()?.pick() }

    // imkeyed

    open fun fkeyTypeOrNull(): RestrictedKeyType<K>? = when (this) {
        is IMKSetNotEmpty<*, *> -> @Suppress("UNCHECKED_CAST") (fkeyType() as? RestrictedKeyType<K>)
        else ->  null
    }

    override fun fcontainsKey(key: @UnsafeVariance K): Boolean = when (this) {
        is IMKSetNotEmpty<*, *> -> body.fcontainsKey(key)
        else -> false
    }

    override fun fcountKey(isMatch: (K) -> Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun fdropKeys(keys: IMSet<@UnsafeVariance K>): FKSet<K, A> {
        TODO("Not yet implemented")
    }

    override fun ffilterKey(isMatch: (K) -> Boolean): FKSet<K, A> {
        TODO("Not yet implemented")
    }

    override fun ffilterKeyNot(isMatch: (K) -> Boolean): FKSet<K, A> {
        TODO("Not yet implemented")
    }

    override fun fpickKey(): K? {
        TODO("Not yet implemented")
    }

    // imkeyedvalue

    override fun asIMMap(): IMMap<K, A> = when (this) {
        is FKSetEmpty -> FKMap.emptyIMMap()
        else -> body.toIMMap()
    }

    override fun asIMBTree(): IMBTree<K, A> = when (this) {
        is FKSetEmpty -> FRBTree.emptyIMBTree()
        is FIKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (body as IMBTree<K,A>)
        is FSKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (body as IMBTree<K,A>)
        is FKKSetNotEmpty<*> -> body
    }

    override fun fcontainsValue(value: @UnsafeVariance A): Boolean {
        TODO("Not yet implemented")
    }

    override fun fcountValue(isMatch: (A) -> Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun ffilterValue(isMatch: (A) -> Boolean): FKSet<K, A> {
        TODO("Not yet implemented")
    }

    override fun ffilterValueNot(isMatch: (A) -> Boolean): FKSet<K, A> {
        TODO("Not yet implemented")
    }

    override fun ffindAnyValue(isMatch: (A) -> Boolean): A? =
        ffindAny(isMatch)

    override fun fget(key: @UnsafeVariance K): A? {
        TODO("Not yet implemented")
    }

    override fun fpickValue(): A? {
        TODO("Not yet implemented")
    }

    // IMMappable

    override fun <B: Any> fmap(f: (A) -> B): IMSet<B> = when(this) {
        is FKSetEmpty -> toEmptyRetyped()
        is FIKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (this as FIKSetNotEmpty<A>).fmap(f)
        is FSKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (this as FSKSetNotEmpty<A>).fmap(f)
        is FKKSetNotEmpty<*> -> (@Suppress("UNCHECKED_CAST") (this as FKKSetNotEmpty<K>).fmap(f as (K) -> B))
    }

    // ============ IMMapplicable

    override fun <T : Any> fapp(op: (IMSet<A>) -> ITMap<T>): ITMapp<T> =
        IMMappOp.flift2mapp(op(this))!!

    // utility

    override fun fforEach (f: (A) -> Unit) = when(this) {
        is FKSetEmpty -> Unit
        else -> body.fforEach { tkv -> f(tkv.getv()) }
    }

    override fun toIMBTree(): IMBTree<K, A> = when(this) {
        is FKSetEmpty -> nul()
        else -> body
    }

    override fun ner(): IMRSetNotEmpty<A>? = asIMRSetNotEmpty()

    override fun ne(): IMSetNotEmpty<A>? = asIMSetNotEmpty()

    override fun <K> nex(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> = asIMXSetNotEmpty()

    override fun copy(): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> body.ffold(nul<K, A>()) { acc, tkv -> acc.finsertTkv(tkv) }.toIMSet(fkeyTypeOrNull()!!)!!
    }

    override fun copyToMutableSet(): MutableSet<@UnsafeVariance A> = when (this) {
        is FKSetEmpty -> mutableSetOf()
        else -> body.ffold(mutableSetOf()) { acc, tkv -> acc.add(tkv.getv()); acc }
    }

    override fun <B: Any> toEmptyRetyped(): FKSet<K,B> = when (this) {
        is FIKSetNotEmpty, is FIKSetEmpty -> @Suppress("UNCHECKED_CAST") (FIKSetEmpty.empty<B>() as FKSet<K,B>)
        is FSKSetNotEmpty, is FSKSetEmpty -> @Suppress("UNCHECKED_CAST") (FSKSetEmpty.empty<B>() as FKSet<K,B>)
        is FKKSetNotEmpty<*>, is FKKSetEmpty<*> -> @Suppress("UNCHECKED_CAST") (FKKSetEmpty.empty<K>() as FKSet<K,B>)
        else -> throw RuntimeException("internal error")
    }


    // filtering

    override fun fcontainsAny(items: IMSet<@UnsafeVariance A>): Boolean = when (this) {
        is FKSetEmpty -> false
        else -> if (items.fempty()) true else {
            val outerNes = ((if (this.fsize() < items.fsize()) this else items) as IMKSetNotEmpty<*,A>)
            val markNes = ((if (this.fsize() < items.fsize()) items else this) as IMKSetNotEmpty<*,A>)
            val outer: IMBTree<*, A> = outerNes.toIMBTree()
            val mark: IMBTree<*, A> = markNes.toIMBTree()
            val isMatch: (tkv: TKVEntry<*, A>) -> Boolean =
                if (outer.frestrictedKey() == mark.frestrictedKey() ){ { tkv -> (@Suppress("UNCHECKED_CAST") (mark.fcontains(tkv as? TKVEntry<Nothing, A>)))} }
                else {{ tkv -> mark.fcontains((@Suppress("UNCHECKED_CAST") (tkv.getv().toEntry(markNes.fkeyType()) as? TKVEntry<Nothing, A>))) }}
            outer.ffindAny { isMatch(it) } != null
        }
    }

    override fun ffilter(isMatch: (A) -> Boolean): FKSet<K, A> =when (this) {
        is FKSetEmpty -> this
        else -> body.ffilter { tkv -> isMatch(tkv.getv()) }.toIMSet(fkeyTypeOrNull()!!)!!
    }

    override fun ffilterNot(isMatch: (A) -> Boolean): FKSet<K, A> {
        val notMatch: (a: A) -> Boolean = { a -> !isMatch(a) }
        return ffilter(notMatch)
    }

    override fun ffind(isMatch: (A) -> Boolean): A? =when (this) {
        is FKSetEmpty -> null
        else -> body.ffindDistinct { tkv -> isMatch(tkv.getv()) }?.getv()
    }

    override fun fisSubsetOf(rhs: IMSet<@UnsafeVariance A>): Boolean = when (this) {
        is FKSetEmpty -> true
        else -> if (rhs.fempty()) false else {
            val superset = rhs
            val maybeSubset = this as IMSet<A>
            fsize() == maybeSubset.fcount(superset::fcontains)
        }
    }

    override fun fAND(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> if (null == items.fpickKey()) toEmpty() else treeWiseAND(body as FRBTNode, items).toIMSet(fkeyTypeOrNull()!!)!!
    }

//    override fun fOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when (this) {
//        is FKSetEmpty -> when (items) {
//            is FKSet -> items
//            else -> when(val t = items.asIMBTree()) {
//                is FRBTree -> ofBody(t)!!
//                is FBSTree -> ofBody(t.toFRBTree())!!
//                else -> throw RuntimeException("internal error")
//            }
//        }
//        else -> treeWiseOR(body as FRBTNode, items).toIMRSet(fkeyTypeOrNull()!!)!!
//    }

//    fun fORkeyed(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A>? = when {
    override fun fOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when {
        null == items.fpickKey() /* i.e. items is empty */ -> this
        else -> when (this) {
            is FIKSetEmpty -> {
                val aux: IMSet<Any> = if (items.fpickKey() is Int && items is IMSet<*>) items
                                      else items.asIMBTree().ffold(this) { acc: IMRSetAltering<A>, tkv -> acc.faddItem(tkv.getv()).sdj().left()!! }
                @Suppress("UNCHECKED_CAST") ( aux as FKSet<K, A>)
            }
            is FSKSetEmpty -> {
                val aux: IMSet<Any> = if (items.fpickKey() is String && items is IMSet<*>) items
                else items.asIMBTree().ffold(this) { acc: IMRSetAltering<A>, tkv -> acc.faddItem(tkv.getv()).sdj().left()!! }
                @Suppress("UNCHECKED_CAST") ( aux as FKSet<K, A>)
            }
            is FKKSetEmpty<*> -> {
                val aux = if (items.ftypeSample()!!.isSymRkc() && items is IMSet<*>) items
                else @Suppress("UNCHECKED_CAST") (items.asIMBTree().ffold(this as IMXSetAltering<K>) { acc: IMXSetAltering<K>, tkv ->
                    check(tkv.getk().equals(tkv.getv()))
                    acc.faddItem(tkv.getk()).sdj().right()!! as IMXSetAltering<K>
                })
                @Suppress("UNCHECKED_CAST") ( aux as FKSet<K, A> )
            }
            is FKSetEmpty -> throw RuntimeException("internal error") //  rktWiseOR(rkt, items)?.toIMRSet(rkt)
            else -> treeWiseOR(body as FRBTNode, items).toIMSet(fkeyTypeOrNull()!!)!!
        }
    }

    override fun fNOT(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> if (null == items.fpickKey()) this else treeWiseNOT(body as FRBTNode, items).toIMSet(fkeyTypeOrNull()!!)!!
    }

    override fun fXOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when {
        null == items.fpickKey() /* i.e. empty */ -> this
        else -> when (this) {
            is FKSetEmpty -> items as FKSet<K, A>
            else -> treeWiseXOR(body as FRBTNode, items).toIMSet(fkeyTypeOrNull()!!)!!
        }
    }

//    fun fXORkeyed(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>, rkt: RestrictedKeyType<@UnsafeVariance K>): FKSet<K, A>? = when {
//        null == items.fpickKey() /* i.e. empty */ -> this
//        else -> when (this) {
//            is FKSetEmpty -> /* if this is empty, OR == XOR */ rktWiseOR(rkt, items)?.toIMRSet(rkt)
//            else -> treeWiseXOR(body as FRBTNode, items).toIMRSet(rkt)
//        }
//    }

    // grouping

    override fun <B: Any> fcartesian(rhs: IMSet<B>): FKSet<*, Pair<A, B>> {

        tailrec fun <Z: Comparable<Z>> go(shrink: IMSet<A>, stay: IMSet<B>, acc: IMBTree<Z, Pair<A, B>>, toEntry: (Pair<A, B>) -> TKVEntry<Z, Pair<A, B>>): FKSet<Z, Pair<A, B>> =
            if (shrink.fempty()) (@Suppress("UNCHECKED_CAST") (acc.toIMSet(acc.frestrictedKey()) as FKSet<Z, Pair<A, B>>)) else {
                val (pop, remainder) = shrink.fpopAndRemainder()
                val newAcc: IMBTree<Z, Pair<A, B>> = pop?.let{
                    val aux: IMBTree<Z, Pair<A, B>> = if(stay.fempty()) acc
                        else IMBTree.finserts ( stay.ffold(nul()) { frb: FRBTree<Z, Pair<A, B>>, a -> frb.finsertTkv(toEntry(Pair(it, a))) }, acc)
                    aux
                } ?: acc
                go(remainder, stay, newAcc, toEntry)
            }

        return if (fempty() || rhs.fempty()) toEmptyRetyped<Pair<A, B>>() else
            if (fkeyTypeOrNull() == (rhs as FKSet<*,B>).fkeyTypeOrNull()) {
                val f: (p: Pair<A, B>) -> TKVEntry<K, Pair<A, B>> = kt2entry()
                go(this, rhs, nul(), f)
            } else {
                val f: (p: Pair<A,B>) -> TKVEntry<Int, Pair<A,B>> = { p -> ofIntKey(p) }
                go(this, rhs, nul(), f)
            }

    }

    override fun fcombinations(maxSize: Int): FKSet<Nothing, FKSet<K, A>> {

        // all unique subsets up to "size" members from this set; order does not matter

        tailrec fun <Z: Comparable<Z>> gogo(item: IMKSetNotEmpty<@UnsafeVariance K, @UnsafeVariance A>,
                                            fat: IMBTree<Z, FKSet<K, A>>, acc: IMBTree<Z, FKSet<K, A>>,
                                            setToEntry: (FKSet<K, A>) -> TKVEntry<Z, FKSet<K, A>>,
                                            rkt: RestrictedKeyType<K>
        ): IMBTree<Z, FKSet<K, A>> {
            val (pop: TKVEntry<Z, FKSet<K, A>>?, remainder: IMBTree<Z, FKSet<K, A>>) = fat.fpopAndRemainder()
            return if (pop == null) acc else {
                if (pop.getv().fsize() < maxSize) {
                    val aux: FKSet<K, A> = pop.getv().fOR(item)
                    val newAcc: IMBTree<Z, FKSet<K, A>> = IMBTree.fadd(setToEntry(aux), acc)
                    gogo(item, remainder, newAcc, setToEntry, rkt)
                } else gogo(item, remainder, acc, setToEntry, rkt)
            }
        }

        tailrec fun <Z: Comparable<Z>> go(shrink: FKSet<K, A>, acc: IMBTree<Z, FKSet<K, A>>,
                                          setToEntry: (FKSet<K, A>) -> TKVEntry<Z, FKSet<K, A>>,
                                          aToEntry: (A) -> TKVEntry<K, A>,
                                          rkt: RestrictedKeyType<K>
        ): IMBTree<Z, FKSet<K, A>> {
            val (pop: A?, remainder: FKSet<K, A>) = shrink.fpopAndRemainder()
            return if(pop == null) acc else {
                val newAcc: IMBTree<Z, FKSet<K, A>> = pop.let {
                    val setOfIt: FKSet<K, A> = FRBTree.of(aToEntry(it)).toIMSet(null)!!
                    val outer: IMBTree<Z, FKSet<K, A>> = IMBTree.fadd(setToEntry(setOfIt), acc)
                    check(!setOfIt.fempty())
                    @Suppress("UNCHECKED_CAST") (setOfIt as IMKSetNotEmpty<K, A>)
                    gogo(setOfIt, outer, outer, setToEntry, rkt)
                }
                go(remainder, newAcc, setToEntry, aToEntry, rkt)
            }
        }

        return if ((maxSize < 1) || fempty()) @Suppress("UNCHECKED_CAST") (toEmptyRetyped<FKSet<K, A>>() as FKSet<Nothing, FKSet<K, A>>) else {
            val s2e: (p: FKSet<K,A>) -> TKVEntry<K, FKSet<K,A>> = kt2entry()
            val a2e: (p: A) -> TKVEntry<K, A> = kt2entry()
            val t: IMBTree<*, FKSet<K, A>> = go(this, nul<Nothing,FKSet<K, A>>(), s2e, a2e, this.fkeyTypeOrNull()!!)
            val res = t.frestrictedKey()?.let { t.toIMSet(null) }
            @Suppress("UNCHECKED_CAST") (res as FKSet<Nothing, FKSet<K, A>>)
        }
    }

    override fun fcount(isMatch: (A) -> Boolean): Int = when (this) {
        is FKSetEmpty -> 0
        else -> body.fcount { tkv -> isMatch(tkv.getv()) }
    }

    override fun <B> fgroupBy(f: (A) -> B): IMMap<B, FKSet<K, A>> where B: Any, B: Comparable<B> {
        TODO("Not yet implemented")
    }

    override fun findexed(offset: Int): FKSet<Int, Pair<A, Int>> = when (this) {
        is FKSetEmpty -> FIKSetEmpty.empty()
        else -> {
            var index = offset - 1
            val t: FRBTree<Int, Pair<A, Int>> = body.fmap{ tkv ->
                    index+=1
                    TKVEntry.ofkv(index, Pair(tkv.getv(), index))
                }
            t.toIMSet(IntKeyType)!!
        }
    }

    override fun fpartition(isMatch: (A) -> Boolean): Pair<FKSet<K, A>, FKSet<K, A>> = when (this) {
        is FKSetEmpty -> Pair(toEmpty(), toEmpty())
        else -> {
            val (t: FRBTree<K, A>, f: FRBTree<K, A>) = body.fpartition { tkv -> isMatch(tkv.getv()) }
            Pair(t.toIMSet(fkeyTypeOrNull()!!)!!, f.toIMSet(fkeyTypeOrNull()!!)!!)
        }
    }

    override fun fpermutations(maxSize: Int): Collection<FList<A>> {

        // TODO consider memoization

        tailrec fun goSmall(shrink: FKSet<K, FKSet<K, A>>, acc: IMBTree<Int, FList<A>>): IMBTree<Int, FList<A>> = if (shrink.fempty()) acc else {
            val (pop: FKSet<K, A>?, remainder: FKSet<K, FKSet<K, A>>) = shrink.fpopAndRemainder()
            val newAcc: IMBTree<Int, FList<A>> = pop?.let { IMBTree.finserts(FRBTree.of(it.fpermute().map { p: FList<A> -> ofIntKey(p) }.iterator()), acc) } ?: acc
            goSmall(remainder, newAcc)
        }

        tailrec fun goLarge(shrink: FKSet<K, FKSet<K, A>>, acc: FList<FList<A>>): FList<FList<A>> = if (shrink.fempty()) acc else {
            val (pop, remainder) = shrink.fpopAndRemainder()
            val newAcc: FList<FList<A>> = if (pop == null) acc else {
                val aux: Collection<FList<A>> = pop.fpermute()
                val perms: FList<FList<A>> = aux.toIMList() as FList<FList<A>>
                perms.ffoldLeft(acc) { pacc, l -> FLCons(l, pacc) }
            }
            goLarge(remainder, newAcc)
        }

        val res: Collection<FList<A>> = run { //if (maxSize < 1 || fsize() < maxSize) toEmptyRetyped().asSet() else {
//            val sizedCmbs: FKSet<K, FKSet<K, A>> = this.fcombinations(maxSize).ffilter { it.size == maxSize }
//            if (this.size < PERMUTATIONCARDLIMIT) {
//                val aux: IMSet<FList<A>> = goSmall(sizedCmbs, nul()).toIMSet(IntKeyType)!!
//                (@Suppress("UNCHECKED_CAST") (aux as Collection<FList<A>>))
//            } else goLarge(sizedCmbs, emptyIMList())
            fun f(): Collection<FList<A>> = TODO()
            f()
        }
        
        return res
    }

    // not stack safe for "large" sets, but it will (probably) blow op anyway for different reasons
    private fun permuteRecursively(): FKSet<Int, FList<A>> = when (fsize()) {
        0 -> FIKSetEmpty.empty()
        1 -> FRBTree.of(ofIntKey(this.toFList())).toIMSet(IntKeyType)!!
        else -> {
            val allItems: FKSet<Int, FList<A>> = @Suppress("UNCHECKED_CAST") (this.fpermutations(1) as FKSet<Int, FList<A>>)
            allItems.ffold(FIKSetEmpty.empty()) { sol: FKSet<Int, FList<A>>, listOf1: FList<A> ->
                sol.fOR(this.fdropItem(listOf1.fhead()!!)
                    .permuteRecursively()
                    .ffold(FIKSetEmpty.empty<FList<A>>()) { psol, pl ->
                        psol.fOR((FRBTree.of(ofIntKey(FLCons(listOf1.fhead()!!, pl)))
                            .toIMSet(IntKeyType) as FKSet<Int, FList<A>>))
                    }
                )
            }
        }
    }

    val permutedFIKSet: Collection<FList<A>> by lazy {

        /*

            Permuting sets larger than, say, n=8 elements, is entangled in
            the mechanics of hashCode conflicts and key selection since it
            may require hashing _all_ possible distinct lists with n items,
            depending on how it is computed. If the hashCode is 32 bits,
            permutations of sets larger than 8 elements must, by necessity,
            be computed in a different way (e.g. other than selection by
            set membership) and cannot in any case be presented as a set
            (too many collisions).  A type disjunction (set | list) would be
            lovely, but Collection must do as a common parent class of either.
            (Pun intended). Explanation are provided in code comments with
            regard to execution.

            There is a hard implementation limit, at present: no permutations
            for size larger than 12 allowed.  Reason: 13! exceeds Int range.
            One may still run into OOM problems for size < 13 anyway.

        */

//        val res = if (this.size < PERMUTATIONCARDLIMIT) {
//
//            /*
//
//                The probability that 2 out of n items will have the same hashcode if
//                there are d available hashcode slots is (see for example
//                https://en.wikipedia.org/wiki/Birthday_problem#Approximations)
//
//                                                -n(n-1)
//                             P(n, d) ~ 1 - exp(---------)
//                                                  2d
//
//                A set of q elements has q! permutations; the probability of a collision
//                for a 32-bit hashCode (for which d is 4,294,967,295) is therefore
//
//                                                -(q!)((q!)-1)
//                                P(q) ~ 1 - exp(----------------)
//                                                 2*4294967295
//
//                for q = 7 then that probability is ~0.003
//                for q = 8 then that probability is ~0.17
//                for q = 9 then that probability is ~0.9999997
//
//                The following recursive solution computes, for a set of size s,
//                all permutations of size (s-1) for each s elements.
//
//             */
//
//            val fkset: FKSet<Int, FList<A>> = permuteRecursively()
//            fkset
//
//        } else {
//
//            /*
//
//                There are _many_ algorithms to compute permutations, see for
//                instance Sedgewick, Robert (1977),"Permutation generation methods",
//                ACM Comput. Surv., 9 (2): 137–164, doi:10.1145/356689.356692 or
//                Knuth, "Art of Computer Programming", vol. 4A.  Overall they are
//                all O(n!) with different coefficients (DUH!).  The iterative
//                Johnson-Trotter is efficient and relatively simple even if not, at
//                least in theory, the most-est efficient; but we are on a virtual
//                machine anyway, so no point in clock cycle counting.
//
//             */
//
//            val aryls: ArrayList<TKVEntry<K, A>> = ArrayList(body)
//            // this ends up being (_has_ to be, too many collisions for set) a FList
//            val flist: FList<FList<A>> = jtPermutations(aryls)
//                .fold(emptyIMList()) { l: FList<FList<A>>, aryl: ArrayList<TKVEntry<K, A>> ->
//                    FLCons(FList.ofMap(aryl) { tkv -> tkv.getv() }, l)
//                }
//            flist
//        }
//
//        val constrainedFactorial = { n: Int -> smallFact(n) } // blows up at 13!
//        check( this.isEmpty() || res.size == constrainedFactorial(size))
//        res

        fun f(): Collection<FList<A>> = TODO()
        f()
    }

    override fun fpermute(): Collection<FList<A>> = permutedFIKSet

    // transforming

    override fun <C> ffold(z: C, f: (acc: C, A) -> C): C = when (this) {
        is FKSetEmpty -> z
        else -> body.ffoldv(z) { stub, tkv -> f(stub, tkv) }
    }

    override fun <B : Any> fmapToList(f: (A) -> B): FList<B> = when (this) {
        is FKSetEmpty -> emptyIMList()
        else -> body.fmapvToList(f) as FList<B> //ffold(emptyIMList()) { l, tkv -> FLCons(f(tkv.getv()), l) }
    }

    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? = when (this) {
        is FKSetEmpty -> null
        else -> body.freduce { acc, tkv -> toTKVEntry(f(acc.getv(), tkv.getv())) ?: throw RuntimeException("internal error: cannot reduce $this") }?.getv()
    }

    override fun <B: Any> fflatMap(f: (A) -> IMSet<B>): IMSet<B> {

        fun <J, T: Any> flatten(acc: IMKeyedValue<J, T>, item: IMKeyedValue<Nothing, T>): IMKeyedValue<J, T> where J: Comparable<J> = acc.fOR(item)

        val notEmpties: FList<IMKSet<*, B>> = body.ffold(emptyIMList<IMKSet<*,B>>()) { acc: FList<IMKSet<*, B>>, tkv: TKVEntry<K, A> ->
            val newItem: IMKSet<*, B> = f(tkv.getv()) as IMKSet<*, B>
            val newItems: FList<IMKSet<*, B>> = when {
                newItem.fempty() -> acc
                acc.fempty() -> FLCons(newItem, FLNil)
                else -> {
                    check(acc.fhead()!!.fpickKey().isStrictly(newItem.fpickKey()))
                    acc.fprepend(newItem)
                }
            }
            newItems
        }
        // TODO this can be conflated in the previous code section
        return notEmpties.fhead()?.let {
            notEmpties.ffoldLeft(it) { acc: IMKSet<*, B>, item: IMKSet<*, B> -> @Suppress("UNCHECKED_CAST") (flatten(acc, item as IMKeyedValue<Nothing, B>) as IMKSet<*, B>) }
            TODO()
        } ?: TODO()

    }

    // altering

    override fun faddItem(item: @UnsafeVariance A): IMRSetNotEmpty<A> = when (this) {
        is FIKSetEmpty, is FSKSetEmpty -> ofBody(FRBTree.of(TKVEntry.ofkv(toKey(item),item)) as FRBTNode)!!.ner()!!
        is FIKSetNotEmpty, is FSKSetNotEmpty -> ofBody(body.finsertTkv(TKVEntry.ofkv(toKey(item),item)) as FRBTNode)!!.ner()!!
        is FKKSetEmpty<*> -> {
            check(compKc.isInstance(item))
            @Suppress("UNCHECKED_CAST") (item as Comparable<A>)
            ofFKKSBody(FRBTree.of(TKVEntry.ofkv(item,item)) as FRBTNode).ner()!!
        }
        is FKKSetNotEmpty<*> -> {
            check(compKc.isInstance(item))
            @Suppress("UNCHECKED_CAST") (item as K)
            ofBody(body.finsertTkv(TKVEntry.ofkv(item,item)) as FRBTNode<K, A>)!!.ner()!!
        }
        else -> throw RuntimeException("internal error")
    }

    // extras

    override fun set(k: @UnsafeVariance K, v: @UnsafeVariance A): IMSet<A> {
        TODO("Not yet implemented")
    }

    //
    // ========= implementation
    //

    val thisToList: FList<A> by lazy { this.fmapToList { it } }
    fun toFList(): FList<A> = when {
        fempty() -> FLNil
        else -> thisToList
    }

    protected abstract fun toFRBTree(k: KClass<@UnsafeVariance K>): FRBTree<K, A>

    abstract val toKey: (@UnsafeVariance A) -> K

    protected abstract fun <V: Any> toTKVEntry(v: @UnsafeVariance V): TKVEntry<K, V>?

    // private

    private fun <KK, V: Any> kt2entry(): (p: V) -> TKVEntry<KK, V> where KK: Comparable <KK> {
        val f: (p: V) -> TKVEntry<KK, V> = when (val kt = fkeyTypeOrNull()!!) {
            is IntKeyType -> { p -> @Suppress("UNCHECKED_CAST") (ofIntKey(p) as TKVEntry<KK, V>) }
            is StrKeyType -> { p -> @Suppress("UNCHECKED_CAST") (ofStrKey(p) as TKVEntry<KK, V>) }
            is SymKeyType -> when {
                kt.kc == intKc -> { p -> @Suppress("UNCHECKED_CAST") (ofIntKey(p) as TKVEntry<KK, V>) }
                kt.kc == strKc -> { p -> @Suppress("UNCHECKED_CAST") (ofStrKey(p) as TKVEntry<KK, V>) }
                else -> { p -> @Suppress("UNCHECKED_CAST") (ofIntKey(p) as TKVEntry<KK, V>) }
            }
            is DeratedCustomKeyType -> throw RuntimeException("internal error")
        }
        return f
    }


    companion object: IMKSetCompanion {

        internal const val unknownSetType = "unknown set type "

        fun <K, A: Any> hashCode(s: FKSet<K, A>) where K: Any, K: Comparable<K> = s.hashCode()

        const val NOT_FOUND: Int = -1

        internal fun <A: Any> emptyIMKISet(): FKSet<Int, A> = emptyIMKSet(IntKeyType)

        internal fun <K, A: Any> emptyIMKSet(rk: RestrictedKeyType<K>?): FKSet<K, A> where K: Any, K: Comparable<K> = when(rk) {
            is DeratedCustomKeyType -> throw RuntimeException("internal error")
            is IntKeyType -> @Suppress("UNCHECKED_CAST") (FIKSetEmpty.empty<A>() as FKSet<K, A>)
            is StrKeyType -> @Suppress("UNCHECKED_CAST") (FSKSetEmpty.empty<A>() as FKSet<K, A>)
            is SymKeyType -> @Suppress("UNCHECKED_CAST") (FKKSetEmpty.empty<K>() as FKSet<K, A>)
            null -> @Suppress("UNCHECKED_CAST") (FKKSetEmpty.empty<K>() as FKSet<K, A>)
        }

        internal fun fksetUncomparable(av: Any, bv: Any): Boolean = (av is FKSet<*,*> && bv is FKSet<*,*>) && av.fpickValue().isStrictlyNot(bv.fpickValue())
        internal fun fksetSoftUncomparable(av: Any, bv: Any): Boolean = ((av is FKSet<*,*> && bv is FKSet<*,*>) && av.fpickValue().isStrictlyNot(bv.fpickValue()))
                || ((av is FKSet<*,*> && bv is Set<*>) && av.fpickValue().isStrictlyNot(bv.firstOrNull()))
                || ((av is Set<*> && bv is FKSet<*,*>) && bv.fpickValue().isStrictlyNot(av.firstOrNull()))
        internal fun fksetComparable(av: Any, bv: Any): Boolean = (av is FKSet<*,*> && bv is FKSet<*,*>) && av.fpickValue().isStrictly(bv.fpickValue())
        internal fun fksetSoftComparable(av: Any, bv: Any): Boolean = ((av is FKSet<*,*> && bv is FKSet<*,*>) && av.fpickValue().isStrictly(bv.fpickValue()))
                || ((av is FKSet<*,*> && bv is Set<*>) && av.fpickValue().isStrictly(bv.firstOrNull()))
                || ((av is Set<*> && bv is FKSet<*,*>) && bv.fpickValue().isStrictly(av.firstOrNull()))

        // override fun <A: Any> emptyIMRSet(): IMSet<A> = FKSetEmpty.empty<Int, A>() // Int does not matter, it's throwaway

        // ========== IMISet

        override fun <A: Any> ofi(vararg items: A): FKSet<Int, A> = ofi(items.iterator())

        override fun <A: Any> ofi(items: Iterator<A>): FKSet<Int, A> = if (!items.hasNext()) FIKSetEmpty.empty() else {
            val first = items.next()
            when (first) {
                is Int -> {
                    var acc: FRBTree<Int, Int> = FRBTree.of(TKVEntry.ofkk(first, first))
                    items.forEach() {
                        it as Int
                        acc = rbtInsert(acc, it.toKKEntry())
                    }
                    (@Suppress("UNCHECKED_CAST") (ofFKKSBody(acc) as FKSet<Int, A>))
                }
                else -> {
                    var acc: FRBTree<Int, A> = ofvi(first)
                    items.forEach() {
                        acc = rbtInsert(acc, it.toIAEntry())
                    }
                    ofFIKSBody(acc)
                }
            }
        }

        override fun <A: Any> ofi(items: IMBTree<Int, A>): FKSet<Int, A>? = when {
            items.fempty() -> FIKSetEmpty.empty()
            items is FRBTINode<A> -> ofFIKSNotEmpty(items)
            items is FRBTKNode<*> -> (@Suppress("UNCHECKED_CAST") (items as? FRBTKNode<Int>))?.let { its ->
                @Suppress("UNCHECKED_CAST") (ofFKKSNotEmpty(its).let { it as? FKSet<Int, A> } )
            }
            items is FBSTNode<Int, A> -> items.toIMSet(null) // takes care of FBSTNode<Int, Int>
            else -> throw RuntimeException("impossible branch")
        }

        override fun <A: Any> ofi(items: IMList<A>): FKSet<Int, A> = if (items.fempty()) FIKSetEmpty.empty() else when (items.fhead()) {
            is Int -> {
                val appender: (FRBTree<Int, Int>, Int) -> FRBTree<Int, Int> = { stub, item -> stub.finsertTkv(TKVEntry.ofkk(item, item)) }
                val aux = @Suppress("UNCHECKED_CAST")(items as IMList<Int>).ffoldLeft(nul(), appender)
                @Suppress("UNCHECKED_CAST") (ofFKKSBody(aux) as FKSet<Int, A>)
            }
            else -> {
                val f: (FRBTree<Int, A>, A) -> FRBTree<Int, A> = { stub, item -> finsertIK(stub, item) as FRBTree<Int, A> }
                val aux: FRBTree<Int, A> = items.ffoldLeft(nul(), f)
                ofFIKSBody(aux)
            }
        }

        override fun <B, A: Any> ofiMap(items: Iterator<B>, f: (B) -> A): FKSet<Int, A> = if (!items.hasNext()) FIKSetEmpty.empty() else {
            val first = items.next()
            val fm: A = f(first)
            when (fm) {
                is Int -> {
                    var acc: FRBTree<Int, Int> = FRBTree.of(TKVEntry.ofkk(fm, fm))
                    items.forEach() {
                        val aux = f(it) as Int
                        acc = rbtInsert(acc, aux.toKKEntry())
                    }
                    (@Suppress("UNCHECKED_CAST") (ofFKKSBody(acc) as FKSet<Int, A>))
                }
                else -> {
                    var acc: FRBTree<Int, A> = ofvi(fm)
                    items.forEach() {
                        acc = rbtInsert(acc, f(it).toIAEntry())
                    }
                    ofFIKSBody(acc)
                }
            }
        }

        override fun <B: Any, A: Any> ofiMap(items: IMList<B>, f: (B) -> A): FKSet<Int, A> =
            if (items.fempty()) FIKSetEmpty.empty() else when (f(items.fhead()!!)) {
                is Int -> {
                    val mapInsert: (FRBTree<Int, Int>, B) -> FRBTKNode<Int> = { stub, item: B ->
                        val aux: Int = f(item) as Int
                        stub.finsertTkv(TKVEntry.ofkk(aux, aux)) as FRBTKNode
                    }
                    val aux = items.ffoldLeft(nul(), mapInsert)
                    @Suppress("UNCHECKED_CAST") (ofFKKSBody(aux) as FKSet<Int, A>)
                }
                else -> {
                    val mapInsert: (FRBTree<Int, A>, B) -> FRBTree<Int, A> = { stub, it -> finsertIK(stub, f(it)) as FRBTNode }
                    val aux: FRBTree<Int, A> = items.ffoldLeft(nul(), mapInsert)
                    ofFIKSNotEmpty(aux as FRBTINode<A>)
                }
            }

        // ========== IMSSet

        override fun <A: Any> ofs(vararg items: A): FKSet<String, A> = ofs(items.iterator())

        override fun <A: Any> ofs(items: Iterator<A>): FKSet<String, A> = if(!items.hasNext()) FSKSetEmpty.empty() else {
            val first = items.next()
            when (first) {
                is String -> {
                    var acc: FRBTree<String, String> = FRBTree.of(TKVEntry.ofkk(first, first))
                    items.forEach() {
                        it as String
                        acc = rbtInsert(acc, it.toKKEntry())
                    }
                    (@Suppress("UNCHECKED_CAST") (ofFKKSBody(acc) as FKSet<String, A>))
                }
                else -> {
                    var acc: FRBTree<String, A> = ofvs(first)
                    items.forEach() {
                        acc = rbtInsert(acc, it.toSAEntry())
                    }
                    ofFSKSBody(acc)
                }
            }
        }

        override fun <A: Any> ofs(items: IMBTree<String, A>): FKSet<String, A>? = when {
            items.fempty() -> FSKSetEmpty.empty()
            items is FRBTSNode<A> -> ofFSKSNotEmpty(items)
            items is FRBTKNode<*> -> (@Suppress("UNCHECKED_CAST") (items as? FRBTKNode<String>))?.let { its ->
                @Suppress("UNCHECKED_CAST") (ofFKKSNotEmpty(its) as? FKSet<String, A>)
            }
            items is FBSTNode<String, A> -> items.toIMSet(null) // takes care of FBSTNode<String, String>
            else -> throw RuntimeException("impossible branch")
        }

        override fun <A: Any> ofs(items: IMList<A>): FKSet<String, A> =
            if (items.fempty()) FSKSetEmpty.empty() else when (items.fhead()) {
                is String -> {
                    val f: (FRBTree<String, String>, String) -> FRBTKNode<String> = { stub, item -> stub.finsertTkv(TKVEntry.ofkk(item, item)) as FRBTKNode }
                    val aux: FRBTKNode<String> = @Suppress("UNCHECKED_CAST")(items as IMList<String>).ffoldLeft(nul(), f) as FRBTKNode
                    @Suppress("UNCHECKED_CAST") (ofFKKSBody(aux) as FKSet<String, A>)
                }
                else -> {
                    val f: (FRBTree<String, A>, A) -> FRBTree<String, A> = { stub, item -> finsertSK(stub, item) as FRBTree<String, A> }
                    val aux: FRBTree<String, A> = items.ffoldLeft(nul(), f)
                    ofFSKSNotEmpty(aux as FRBTSNode<A>)
                }
            }

        override fun <B, A: Any> ofsMap(items: Iterator<B>, f: (B) -> A): FKSet<String, A> = if (!items.hasNext()) FSKSetEmpty.empty() else {
            val first = items.next()
            val fm: A = f(first)
            when (fm) {
                is String -> {
                    var acc: FRBTree<String, String> = FRBTree.of(TKVEntry.ofkk(fm, fm))
                    items.forEach() {
                        val aux = f(it) as String
                        acc = rbtInsert(acc, aux.toKKEntry())
                    }
                    (@Suppress("UNCHECKED_CAST") (ofFKKSBody(acc) as FKSet<String, A>))
                }
                else -> {
                    var acc: FRBTree<String, A> = ofvs(fm)
                    items.forEach() {
                        acc = rbtInsert(acc, f(it).toSAEntry())
                    }
                    ofFSKSBody(acc)
                }
            }
        }

        override fun <B: Any, A: Any> ofsMap(items: IMList<B>, f: (B) -> A): FKSet<String, A> =
            if (items.fempty()) FSKSetEmpty.empty() else when (f(items.fhead()!!)) {
                is String -> {
                    val mapInsert: (FRBTree<String, String>, B) -> FRBTKNode<String> = { stub, item: B ->
                        val aux: String = f(item) as String
                        stub.finsertTkv(TKVEntry.ofkk(aux, aux)) as FRBTKNode
                    }
                    val aux = items.ffoldLeft(nul(), mapInsert)
                    @Suppress("UNCHECKED_CAST") (ofFKKSBody(aux) as FKSet<String, A>)
                }
                else -> {
                    val mapInsert: (FRBTree<String, A>, B) -> FRBTree<String, A> = { stub, it -> finsertSK(stub, f(it)) as FRBTNode }
                    val aux: FRBTree<String, A> = items.ffoldLeft(nul(), mapInsert)
                    ofFSKSNotEmpty(aux as FRBTSNode<A>)
                }
            }

        // ========== IMKSet

        override fun <K> ofk(vararg items: K): FKSet<K, K> where K: Any, K: Comparable<K> = ofk(items.iterator())

        override fun <K> ofk(items: Iterator<K>): FKSet<K, K> where K: Any, K: Comparable<K> = if(!items.hasNext()) FKKSetEmpty.empty() else {
            var acc: FRBTree<K, K> = nul()
            items.forEach() {
                acc = rbtInsert(acc, it.toKKEntry())
            }
            ofFKKSBody(acc)
        }

        override fun <K> ofk(items: IMBTree<K, K>): FKSet<K, K> where K: Any, K: Comparable<K> = when {
            items.fempty() -> FKKSetEmpty.empty()
            items is FRBTKNode<K> -> ofFKKSBody(items)
            items is FBSTNode<K, K> -> ofFKKSBody(items.toFRBTree())
            else -> throw RuntimeException("impossible branch")
        }

        override fun <K> ofk(items: IMList<K>): FKSet<K, K> where K: Any, K: Comparable<K> {
            val f: (FRBTree<K, K>, K) -> FRBTree<K, K> = { stub, item -> stub.finsertTkv(item.toKKEntry()) }
            val aux: FRBTree<K, K> = items.ffoldLeft(nul(), f)
            return ofFKKSBody(aux)
        }

        override fun <B, K> ofkMap(items: Iterator<B>, f: (B) -> K): FKSet<K, K>  where K: Any, K: Comparable<K> {
            var acc: FRBTree<K, K> = nul()
            items.forEach() {
                acc = rbtInsert(acc, f(it).toKKEntry())
            }
            return ofFKKSBody(acc)
        }

        override fun <B: Any, K> ofkMap(items: IMList<B>, f: (B) -> K): FKSet<K, K> where K: Any, K: Comparable<K> {
            val mapInsert: (FRBTree<K, K>, B) -> FRBTKNode<K> = { stub, it -> stub.finsertTkv(f(it).toKKEntry()) as FRBTKNode }
            val aux = items.ffoldLeft(nul(), mapInsert)
            return ofFKKSBody(aux)
        }

        // ==========

        override fun <K, A : Any> asKeyed(self: IMSet<@UnsafeVariance A>): FKSet<K,A> where K: Any, K: Comparable<K> = when(self) {
            is FKSetEmpty<*, *> -> @Suppress("UNCHECKED_CAST") (self as FKSet<K, A>)
            is FIKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (self as FKSet<K, A>)
            is FSKSetNotEmpty -> @Suppress("UNCHECKED_CAST") (self as FKSet<K, A>)
            is FKKSetNotEmpty<*> -> @Suppress("UNCHECKED_CAST") (self as FKSet<K, A>)
            else -> throw RuntimeException("internal error: cannot retype ${self::class} to ${this::class}")
        }

        override fun <K, B : Any> toTKVEntry(s: IMSet<B>, v: B): TKVEntry<K, B>? where K: Any, K: Comparable<K> = when(s) {
            is FKSetEmpty<*, *> -> null
            is FIKSetNotEmpty -> (@Suppress("UNCHECKED_CAST") (s as? FKSet<Int, *>))?.let { its ->
                @Suppress("UNCHECKED_CAST") (its.toTKVEntry(v) as? TKVEntry<K, B>)
            }
            is FSKSetNotEmpty -> (@Suppress("UNCHECKED_CAST") (s as? FKSet<String, *>))?.let { its ->
                @Suppress("UNCHECKED_CAST") (its.toTKVEntry(v) as? TKVEntry<K, B>)
            }
            is FKKSetNotEmpty -> (@Suppress("UNCHECKED_CAST") (s as? FKSet<K, K>))?.let { it.toTKVEntry(v) }
            else -> throw RuntimeException("$unknownSetType ${s::class}")
        }

        // ==========

        override fun <K, A: Any> Iterable<A>.toIMKSet(kType: RestrictedKeyType<K>): IMSet<A>? where K: Any, K: Comparable<K> {

            fun migrate() = when (kType) {
                is IntKeyType -> this.toIMISet()
                is StrKeyType -> this.toIMSSet()
                is SymKeyType -> @Suppress("UNCHECKED_CAST") ((this as? Iterable<K>)?.toIMKSet() as? FKSet<K, A>)
                is DeratedCustomKeyType -> throw RuntimeException("internal error")
            }

            val iter = this.iterator()
            val maybeIMSet = if (iter is FKSetIterator<*, A>) iter.retriever.original() else null
            val maybeKey: RestrictedKeyType<Any>? = maybeIMSet?.fkeyTypeOrNull()
            return maybeKey?.let { iterKey -> when {
                iterKey.isStrictly(kType) -> if (iterKey.kc == kType.kc) maybeIMSet else /* different SymKeyType */ null
                else -> migrate()
            }} ?: migrate()
        }

        override fun <A: Any> Iterable<A>.toIMISet(): FKSet<Int, A>?    {
            val iter = this.iterator()
            return if (iter.hasNext()) when (iter) {
                is FKSetIterator<*, A> -> {
                    val ori = iter.retriever.original()
                    when (ori.fkeyTypeOrNull()!!) {
                        is IntKeyType -> @Suppress("UNCHECKED_CAST") (this as? FKSet<Int, A>)
                        else -> ori.body.toIMBTree(IntKeyType)?.let { it as FRBTree<Int, A> }?.let { ofFIKSBody(it) }
                    }
                }
                else -> ofi(iter)
            }
            else FIKSetEmpty.empty()
        }

        override fun <A: Any> Iterable<A>.toIMSSet(): FKSet<String, A>? {
            val iter = this.iterator()
            return if (iter.hasNext()) when (iter) {
                is FKSetIterator<*, A> -> {
                    val ori = iter.retriever.original()
                    when (ori.fkeyTypeOrNull()!!) {
                        is StrKeyType -> @Suppress("UNCHECKED_CAST") (this as? FKSet<String, A>)
                        else -> ori.body.toIMBTree(StrKeyType)?.let { it as FRBTree<String, A> }?.let { ofFSKSBody(it) }
                    }
                }
                else -> ofs(iter)
            }
            else FSKSetEmpty.empty()
        }

        fun <A> Iterable<A>.toIMKSet(): FKSet<A, A>? where A: Any, A: Comparable<A> {
            val iter = this.iterator()
            return if (iter.hasNext()) when (iter) {
                is FKSetIterator<*, A> -> {
                    val ori = iter.retriever.original()
                    when (ori.fkeyTypeOrNull()!!) {
                        is SymKeyType<*> -> @Suppress("UNCHECKED_CAST") (this as? FKSet<A, A>)
                        else -> ofk(iter)
                    }
                }
                else -> ofk(iter)
            } else FKKSetEmpty.empty()
        }

        /* TODO maybe
        collapse(S): given a set of sets, return the union.[6] For example, collapse({{1}, {2, 3}}) == {1, 2, 3}. May be considered a kind of sum.
        flatten(S): given a set consisting of sets and atomic elements (elements that are not sets), returns a set whose elements are the atomic elements of the original top-level set or elements of the sets it contains. In other words, remove a level of nesting – like collapse, but allow atoms. This can be done a single time, or recursively flattening to obtain a set of only atomic elements.[7] For example, flatten({1, {2, 3}}) == {1, 2, 3}.
         */

        internal inline fun <reified K, reified A: Any> toArray(fset: FKSet<K, A>): Array<A> where K: Any, K: Comparable<K> =
            FKSetIterator.toArray(fset.fsize(), FKSetIterator(fset))

        internal fun <K, A: Any> asFKSet(s: IMSet<A>): FKSet<K, A> where K: Any, K: Comparable<K> =
            @Suppress("UNCHECKED_CAST")(s as? FKSet<K,A>)?.let { it } ?: throw RuntimeException("internal error, unknown ${s::class.simpleName}")

        private fun <K, A: Any> treeWiseOR(tn: FRBTNode<K, A>, items: IMKeyedValue<K, A>): FRBTree<K, A> where K: Any, K: Comparable<K> = when {
            null == items.fpickKey() /* i.e. empty */ -> tn
            items is IMKSetNotEmpty<K, A> -> items.toIMBTree(tn.frestrictedKey()!!)?.let {
                tn.finsertTkvs(it)
            } ?: throw RuntimeException("cannot treeWiseOR FRBTNode<K, A>:${tn::class} with IMSet<A>:${items::class}")
            else -> tn.fOR(items)
        }

        private fun <K, A: Any> rktWiseOR(kt: RestrictedKeyType<K>, items: IMKeyedValue<K, A>): FRBTree<K, A>? where K: Any, K: Comparable<K> = when {
            null == items.fpickKey() /* i.e. empty */ -> emptyIMBTree()
            items is IMKSetNotEmpty<K, A> -> items.toIMBTree(kt)?.let { it as FRBTree<K, A> }
            else -> {
                val aut = items.asIMBTree()
                if (aut.frestrictedKey() != kt) null else when (aut) {
                    is FRBTNode -> aut
                    is FBSTNode -> aut.toFRBTree()
                    else -> null
                }
            }
        }

        private fun <K, A: Any> treeWiseXOR(tn: FRBTNode<K, A>, items: IMKeyedValue<K, A>): FRBTree<K, A> where K: Any, K: Comparable<K> = when {
            null == items.fpickKey() /* i.e. empty */ -> tn
            items is IMKSetNotEmpty<K, A> -> {
                val bothHave: FRBTree<K, A> = treeWiseAND(tn, items)
                val thisOnly: IMBTree<K, A> = tn.fdropAlt(bothHave)
                val itemsOnly: IMBTree<K, A>? = items.toIMBTree(tn.frestrictedKey()!!)?.fdropAlt(bothHave)
                itemsOnly?.let {
                    val res: IMBTree<K, A> = when {
                        thisOnly.fempty() -> it
                        it.fempty() -> thisOnly
                        else -> IMBTree.finserts(it, thisOnly)
                    }
                    return when (res) {
                        is FRBTree<*,*> -> res as FRBTree<K, A>
                        is FBSTree<*,*> -> (res as FBSTree<K, A>).toFRBTree()
                        else -> throw RuntimeException("internal error")
                    }
                } ?: throw RuntimeException("cannot treeWiseXOR FRBTNode<K, A>:${tn::class} with IMSet<A>:${items::class}")
            }
            else -> tn.fXOR(items)
        }

        private fun <K, A: Any> treeWiseAND(tn: FRBTNode<K, A>, items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FRBTree<K, A> where K: Any, K: Comparable<K> = when {
            null == items.fpickKey() /* i.e. empty */ -> tn
            else -> tn.ffold(nul()) { stub, tkv -> if (items.fcontainsKey(tkv.getk())) stub.finsertTkv(tkv) else stub }
        }

        private fun <K, A: Any> treeWiseNOT(tn: FRBTNode<K, A>, items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): FRBTree<K, A> where K: Any, K: Comparable<K> = when {
            null == items.fpickKey() /* i.e. empty */ -> tn
            else -> tn.ffold(nul()) { stub, tkv -> if (items.fcontainsKey(tkv.getk())) stub else stub.finsertTkv(tkv) }
        }

        private const val PERMUTATIONCARDLIMIT = 6 // 9
    }
}

internal abstract class FKSetEmpty<K, A: Any> protected constructor (
    b: FRBTree<K, A>
): FKSet<K, A>(b) where K: Any, K: Comparable<@UnsafeVariance K> {

    // Any

    override fun equals(other: Any?): Boolean = when {
        other == null -> false
        other is IMKSet<*,*> -> other.fempty()
        other is Set<*> -> when(val iter = other.iterator()) {
            is FKSetIterator<*, *> -> !iter.hasNext()
            else -> false
        }
        else -> false
    }

    override fun softEqual(rhs: Any?): Boolean = equals(rhs) || when (rhs) {
        is Set<*> -> rhs.isEmpty()
        // is IMCommon<*> -> IMCommonEmpty.equal(rhs)
        else -> false
    }

    override val hash: Int by lazy { show.hashCode() }
    override fun hashCode(): Int = hash
    val show: String by lazy { "FKSet(*)" }
    override fun toString(): String = show

    override fun <KK> toIMKSetNotEmpty(kt: RestrictedKeyType<KK>): IMKSetNotEmpty<KK, A>? where KK: Any, KK: Comparable<KK> = null

    // FKSet

    override fun fpickKey(): K? = null
    override fun fpickValue(): A? = null
    override fun toFRBTree(k: KClass<K>): FRBTree<K, A> = nul()

    // IMSet

    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = null
    override fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> = null

    // IMKSet

    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>? = null
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = null

    // Collections

    override fun fempty(): Boolean = true
    override fun fsize() = 0
    override operator fun contains(element: @UnsafeVariance A): Boolean = false

    companion object {
        const val msg = "never to be implemented (internal error)"
    }
}

// =====================================================================================================================
// FIKSet implementation
// =====================================================================================================================

private class FIKSetEmpty<out A: Any> private constructor (
    b: FRBTree<Int,A> = FRBTNil
): FKSetEmpty<Int,@UnsafeVariance A>(b), IMSetAltering<A> {
    override val toKey: (value: @UnsafeVariance A) -> Int = { value -> TKVEntry.intKeyOf(value) }
    // IMSetAltering
    override fun faddItem(item: @UnsafeVariance A): IMSetNotEmpty<A> =
        super.faddItem(item) as IMSetNotEmpty<A>
    override fun <V : Any> toTKVEntry(v: V) = ofIntKey(v)
    companion object {
        private val singletonEmpty = FIKSetEmpty(FRBTNil)
        internal fun <A: Any> empty(): FKSet<Int, A> = singletonEmpty
    }
}

private class FIKSetNotEmpty<out A: Any> private constructor (
    b: FRBTINode<@UnsafeVariance A>
): FKSet<Int, A>(b), IMKASetNotEmpty<Int, A>, IMSetAltering<A> {
    override fun fempty(): Boolean = false
    val show: String by lazy {
        val spacerOpen = "{"
        val spacerClose = "},"
        val cn: String = this::class.simpleName!!.replace("Body","", ignoreCase = true).replace("NotEmpty","", ignoreCase = true)
        "$cn(${body.inorder().ffoldLeft("") { acc, tkv -> acc + spacerOpen + tkv.getv().toString() + spacerClose }.dropLast(1)})"
    }
    override fun toString(): String = show
    val size: Int by lazy { body.size }
    override fun fsize() = size

    // IMKeyed
    
    override fun fpickKey(): Int = body.froot()!!.getk()
    override fun fpickValue(): A = body.froot()!!.getv()
    override fun toSetKey(a: @UnsafeVariance A): Int = toKey(a)

    // IMKSetFiltering

    override fun fkeyType() = IntKeyType
    override fun fdropAllEntries(items: IMCommon<TKVEntry<Int, @UnsafeVariance A>>): FKSet<Int, A> {
        TODO("Not yet implemented")
    }

    // IMSetAltering

    override fun faddItem(item: @UnsafeVariance A): IMSetNotEmpty<A> =
        super.faddItem(item) as IMSetNotEmpty<A>
    
    // IMKASEtTransforming
    
    override fun <B : Any> fmap(f: (A) -> B): FKSet<Int, B> {
        val t = body.fmap { tkv -> ofIntKey(f(tkv.getv())) }
        (t as FRBTNode)
        return t.toIMSet(t.frbRKeyType)!!
    }

    // IMKASetUtility

    override fun <KK> toIMKSetNotEmpty(kt: RestrictedKeyType<KK>): IMKSetNotEmpty<KK, A>? where KK: Any, KK : Comparable<KK> = when (kt) {
        is IntKeyType -> forceKey<KK, Int, A>(this)
        is StrKeyType -> @Suppress("UNCHECKED_CAST") (toFSKSetNotEmpty() as? IMKSetNotEmpty<KK, A>)
        is SymKeyType -> when {
            kt.kc.isStrictly(body.froot()?.getkKc()) -> @Suppress("UNCHECKED_CAST") (this as? IMKSetNotEmpty<KK, A>)
            kt.kc.isStrictlyNot(body.froot()?.getvKc()) -> null
            else -> toFKKSetNotEmpty()?.let { @Suppress("UNCHECKED_CAST") (it as? IMKSetNotEmpty<KK, A>) }
        }
        is DeratedCustomKeyType -> kt.specialize<KK>()?.let { toIMKSetNotEmpty(it) }
    }

    // FKSet
    
    override fun toFRBTree(k: KClass<Int>): FRBTNode<Int, @UnsafeVariance A> = body as FRBTNode<Int, A>
    override val toKey: (v: @UnsafeVariance A) -> Int
        get() = { v -> TKVEntry.intKeyOf(v) }
    override fun <V : Any> toTKVEntry(v: V) =
        ofIntKey(v)

    // implementation
    
    fun toFKKSetNotEmpty(): IMSet<A>? = when {
        fpick()!! is Int -> throw RuntimeException("internal error") // FIKSetNotEmpty should have been FKKSetNotEmpty
        !(compKc.isInstance(fpick()!!)) -> null // rekey will fail at least once
        else -> toFKKSetNe(this, body)
    }

    fun toFSKSetNotEmpty(): FKSet<String, A> =
        ofFSKSBody(this.ffold(nul()) { acc, item -> acc.finsertTkv(ofStrKey(item)) })

    companion object {
        internal fun <A: Any> of(b: FRBTINode<A>): FKSet<Int, A> = FIKSetNotEmpty(b)
    }
}

internal fun <A: Any> ofFIKSBody(b: FRBTree<Int, A>): FKSet<Int, A> = when(b) {
    is FRBTINode -> FIKSetNotEmpty.of(b)
    is FRBTKNode<*> -> if (b.frestrictedKey()?.kc == intKc) (@Suppress("UNCHECKED_CAST") (FKKSetNotEmpty.of(b) as FKSet<Int, A>)) else FIKSetEmpty.empty()
    else -> FIKSetEmpty.empty()
}

internal fun <A: Any> ofFIKSNotEmpty(b: FRBTINode<A>): FKSet<Int, A> = FIKSetNotEmpty.of(b)

// =====================================================================================================================
// FSKSet implementation
// =====================================================================================================================

private class FSKSetEmpty<out A: Any> private constructor (
    b: FRBTree<String,A> = FRBTNil
): FKSetEmpty<String,@UnsafeVariance A>(b), IMSetAltering<A> {
    override val toKey: (value: @UnsafeVariance A) -> String = { value -> TKVEntry.strKeyOf(value) }
    // IMSetAltering
    override fun faddItem(item: @UnsafeVariance A): IMSetNotEmpty<A> =
        super.faddItem(item) as IMSetNotEmpty<A>
    override fun <V : Any> toTKVEntry(v: V) = ofStrKey(v)
    companion object {
        private val singletonEmpty = FSKSetEmpty(FRBTNil)
        internal fun <A: Any> empty(): FKSet<String, A> = singletonEmpty
    }
}

private class FSKSetNotEmpty<out A: Any> private constructor (
    b: FRBTSNode<@UnsafeVariance A>
): FKSet<String, A>(b), IMKASetNotEmpty<String, A>, IMSetAltering<A> {
    override fun fempty(): Boolean = false
    val show: String by lazy {
        val spacerOpen = "{"
        val spacerClose = "},"
        val cn: String = this::class.simpleName!!.replace("NotEmpty","", ignoreCase = true)
        "$cn(${body.inorder().ffoldLeft("") { acc, tkv -> acc + spacerOpen + tkv.getv().toString() + spacerClose }.dropLast(1)})"
    }
    override fun toString(): String = show
    val size: Int by lazy { body.size }
    override fun fsize() = size

    // IMSetTransforming

    override fun <B : Any> fmap(f: (A) -> B): FKSet<String, B> {
        val t = body.fmap { tkv -> ofStrKey(f(tkv.getv())) }
        (t as FRBTNode)
        return t.toIMSet(t.frbRKeyType)!!
    }

    // IMKeyed

    override fun fpickKey(): String = body.froot()!!.getk()
    override fun fpickValue(): A = body.froot()!!.getv()
    override fun toSetKey(a: @UnsafeVariance A): String = toKey(a)

    // IMKSetFiltering

    override fun fkeyType() = StrKeyType
    override fun fdropAllEntries(items: IMCommon<TKVEntry<String, @UnsafeVariance A>>): FKSet<String, A> {
        TODO("Not yet implemented")
    }

    // IMSetAltering

    override fun faddItem(item: @UnsafeVariance A): IMSetNotEmpty<A> =
        super.faddItem(item) as IMSetNotEmpty<A>

    // IMKASetUtility

    override fun <KK> toIMKSetNotEmpty(kt: RestrictedKeyType<KK>): IMKSetNotEmpty<KK, A>? where KK: Any, KK : Comparable<KK> = when (kt) {
        is StrKeyType -> forceKey<KK, String, A>(this)
        is IntKeyType -> @Suppress("UNCHECKED_CAST") (toFIKSetNotEmpty() as? IMKSetNotEmpty<KK, A>)
        is SymKeyType ->  when {
            kt.kc.isStrictly(body.froot()?.getkKc()) -> @Suppress("UNCHECKED_CAST") (this as? IMKSetNotEmpty<KK, A>)
            kt.kc.isStrictlyNot(body.froot()?.getvKc()) -> null
            else -> toFKKSetNotEmpty()?.let { @Suppress("UNCHECKED_CAST") (it as? IMKSetNotEmpty<KK, A>) }
        }
        is DeratedCustomKeyType -> kt.specialize<KK>()?.let { toIMKSetNotEmpty(it) }
    }

    // FKSet
    
    override fun toFRBTree(k: KClass<String>): FRBTNode<String, @UnsafeVariance A> = body as FRBTNode<String, A>
    override val toKey: (v: @UnsafeVariance A) -> String
        get() = { v -> TKVEntry.strKeyOf(v) }
    override fun <V : Any> toTKVEntry(v: V) =
        ofStrKey(v)
    
    // implementation

    fun toFKKSetNotEmpty(): IMSet<A>? =  when {
        fpick()!! is String -> throw RuntimeException("internal error") // FSKSetNotEmpty should have been FKKSetNotEmpty
        !(compKc.isInstance(fpick()!!)) -> null
        else -> toFKKSetNe(this, body)
    }

    fun toFIKSetNotEmpty(): FKSet<Int, A> =
        ofFIKSBody(this.ffold(nul()) { acc, item -> acc.finsertTkv(ofIntKey(item)) })

    companion object {
        internal fun <A: Any> of(b: FRBTSNode<A>): FKSet<String, A> = FSKSetNotEmpty(b)
    }
}

private fun <A : Any> toFKKSetNe(src: IMKASetNotEmpty<*, A>, body: FRBTree<*, @UnsafeVariance A>): FKSet<Comparable<Comparable<*>>, A>? {
    check(null != @Suppress("UNCHECKED_CAST") (src.fpick()!! as? Comparable<A>))
    fun reKey(aux: A): TKVEntry<Comparable<Comparable<*>>, A>? {
        val sentinel = @Suppress("UNCHECKED_CAST") (aux as? Comparable<A>)
        return sentinel?.let { @Suppress("UNCHECKED_CAST") (TKVEntry.ofk(aux) as? TKVEntry<Comparable<Comparable<*>>, A>) }
    }
    val aux: FRBTree<Comparable<Comparable<*>>, A> =
        body.ffold(nul()) { acc, tkv -> reKey(tkv.getv())?.let { acc.finsertTkv(it) } ?: acc }
    return if (aux is FRBTNode<*, *>) {
        check(body.fsize() == aux.fsize())
        ofBody(aux)
    } else null
}

internal fun <A: Any> ofFSKSBody(b: FRBTree<String, A>): FKSet<String, A> = when(b) {
    is FRBTSNode -> FSKSetNotEmpty.of(b)
    is FRBTKNode<*> -> if (b.frestrictedKey()?.kc == strKc) (@Suppress("UNCHECKED_CAST") (FKKSetNotEmpty.of(b) as FKSet<String, A>)) else FSKSetEmpty.empty()
    else -> FSKSetEmpty.empty()
}

internal fun <A: Any> ofFSKSNotEmpty(b: FRBTSNode<A>): FKSet<String, A> = FSKSetNotEmpty.of(b)

// =====================================================================================================================
// FKKSet implementation
// =====================================================================================================================

private class FKKSetEmpty<out A> private constructor (
    b: FRBTree<A,A> = FRBTNil
): FKSetEmpty<@UnsafeVariance A, @UnsafeVariance A>(b), IMXSetAltering<A> where A: Any, A: Comparable<@UnsafeVariance A> {
    override val toKey: (value: @UnsafeVariance A) -> A = { value -> value }
    // IMXSetAltering
    override fun faddItem(item: @UnsafeVariance A): IMXSetNotEmpty<A> =
        super.faddItem(item) as IMXSetNotEmpty<A>
    companion object {
        private val singletonEmpty = FKKSetEmpty(FRBTNil)
        internal fun <A> empty(): FKSet<A, A> where A: Any, A: Comparable<@UnsafeVariance A> = singletonEmpty
    }

    override fun <V : Any> toTKVEntry(v: V): TKVEntry<A, V>? {
        TODO("Not yet implemented")
    }
}

private class FKKSetNotEmpty<out A> private constructor (
    b: FRBTKNode<@UnsafeVariance A>
): FKSet<A, A>(b), IMKKSetNotEmpty<A>, IMXSetAltering<A> where A: Any, A: Comparable<@UnsafeVariance A> {
    override fun fempty(): Boolean = false
    val show: String by lazy {
        val spacerOpen = "{"
        val spacerClose = "},"
        val cn: String = this::class.simpleName!!.replace("NotEmpty","", ignoreCase = true)
        "$cn(${body.inorder().ffoldLeft("") { acc, tkv -> acc + spacerOpen + tkv.getv().toString() + spacerClose }.dropLast(1)})"
    }
    override fun toString(): String = show
    val size: Int by lazy { body.size }
    override fun fsize() = size

    // IMSetTransforming

    override fun <B: Any> fmap(f: (A) -> B): IMSet<B> {

        val (item, items) = body.fpopAndRemainder()
        val sample: B = f(item!!.getv())
        return when {
            !(compKc.isInstance(sample)) -> {
                val t = items.fmap{ tkv -> ofIntKey(f(tkv.getv())) }.finsertTkv(sample.toIAEntry())
                (t as FRBTNode)
                check(IntKeyType == t.frbRKeyType)
                t.toIMSet(t.frbRKeyType)!!
            }
            else -> {
                val caboose = (@Suppress("UNCHECKED_CAST") (sample as? Comparable<B>))
                val t = items.fmap { tkv ->
                    val aux = f(tkv.getv())
                    val sentinel = (@Suppress("UNCHECKED_CAST") (aux as? Comparable<B>))
                    sentinel?.let{ TKVEntry.ofk(aux) } ?: throw RuntimeException("internal error")
                }.finsertTkv(caboose?.let{ TKVEntry.ofk(sample) } ?: throw RuntimeException("internal error"))
                (t as FRBTNode)
                check(t.frbRKeyType is SymKeyType<*>)
                t.toIMSet(t.frbRKeyType)!!
            }
        }
//        fun reshape(b: B): TKVEntry<Comparable<Comparable<*>>, B> = if (compKc.isInstance(b)) {
//            @Suppress("UNCHECKED_CAST") (b as Comparable <B>)
//            @Suppress("UNCHECKED_CAST") (TKVEntry.ofk(b) as TKVEntry<Comparable<Comparable<*>>, B>)
//        } else @Suppress("UNCHECKED_CAST") (ofIntKey(b) as TKVEntry<Comparable<Comparable<*>>, B>)
//
//        fun treeOfBees(): FRBTree<Comparable<Comparable<*>>, B> = body.ffold(nul()) { acc, tkv ->
//            val newValue = f(tkv.getv())
//            acc.finsert((toTKVEntry(newValue)?.let { @Suppress("UNCHECKED_CAST") (it  as TKVEntry<Comparable<Comparable<*>>, B>) } ?: reshape(newValue)))
//        }
//
//        val tob = (treeOfBees() as FRBTNode)
//        return tob.frbRKeyType?.let { tob.toIMSet(it) } ?: throw RuntimeException("cannot fmap ${this::class}")
    }

    // IMKeyed

    override fun fpickKey(): A = body.froot()!!.getk()
    override fun fpickValue(): A = body.froot()!!.getv()
    override fun toSetKey(a: @UnsafeVariance A): A = toKey(a)

    // IMKSetFiltering

    override fun fkeyType() = rkt
    override fun fdropAllEntries(items: IMCommon<TKVEntry<@UnsafeVariance A, @UnsafeVariance A>>): FKSet<A, A> =
        ofk(this.body.fdropAll(items))

    // IMXSetAltering
    
    override fun faddItem(item: @UnsafeVariance A): IMXSetNotEmpty<A> =
        super.faddItem(item) as IMXSetNotEmpty<A>

    // IMKKSetTransforming

    override fun <B> fflatMapKK(f: (A) -> IMSet<B>): FKSet<B, B> where B: Any, B: Comparable<B> {
        val t: FRBTree<B, B> = body.fflatMap { tkv -> asFKSet<B, B>(f(tkv.getv())).toIMBTree() }
        (t as FRBTNode)
        return t.toIMSet(t.frbRKeyType!!)!!
    }

    override fun <B> fmapKK(f: (A) -> B): FKSet<B, B> where B: Any, B: Comparable<B> {
        fun treeOfBees(): FRBTree<B, B> = body.ffold(nul()) { acc, tkv ->
            acc.finsertTkv(f(tkv.getv()).toKKEntry())
        }
        val tob = (treeOfBees() as FRBTNode)
        return tob.frbRKeyType?.let { tob.toIMSet(it) } ?: throw RuntimeException("cannot fmapKK ${this::class}")
    }

    // IMKKSetUtility

    override fun <KK> toIMKSetNotEmpty(kt: RestrictedKeyType<KK>): IMKSetNotEmpty<KK, A>? where KK: Any, KK : Comparable<KK> = when (kt) {
        is StrKeyType -> if (fkeyType().kc == strKc) forceKey<KK, String, A>(this)
                         else @Suppress("UNCHECKED_CAST") (toFSKSetNotEmpty() as IMKSetNotEmpty<KK, A>)
        is IntKeyType -> if (fkeyType().kc == intKc) forceKey<KK, Int, A>(this)
                         else @Suppress("UNCHECKED_CAST") (toFIKSetNotEmpty() as IMKSetNotEmpty<KK, A>)
        is SymKeyType -> if (fkeyType() == kt) forceKey<KK, A, A>(this) else null
        is DeratedCustomKeyType -> kt.specialize<KK>()?.let { toIMKSetNotEmpty(it) }
    }

    // FKSet
    
    override fun toFRBTree(k: KClass<@UnsafeVariance A>): FRBTNode<@UnsafeVariance A, @UnsafeVariance A> = body as FRBTNode
    override val toKey: (v: @UnsafeVariance A) -> A
        get() = { v -> v }
    override fun <V : Any> toTKVEntry(v: V): TKVEntry<A, V>? {
        val aux: RTKVEntry<A,A>? = when {
            v is Int && rkt.kc == intKc -> (@Suppress("UNCHECKED_CAST") (v as? A))?.let{ it.toKKEntry() }
            v is String && rkt.kc == strKc -> (@Suppress("UNCHECKED_CAST") (v as? A))?.let { it.toKKEntry() }
            DeratedCustomKeyType(v::class).sameAs(rkt) -> (@Suppress("UNCHECKED_CAST") (v as? A))?.let { it.toKKEntry() }
            v is RKTKVEntry<*, *> -> {
                check(v.getrk() == rkt)
                @Suppress("UNCHECKED_CAST") (v as? RTKVEntry<A, A>)
            }
            else -> makeTKVEntry(this, v, body.froot()!!.getk(), body.froot()!!.getv())
        }
        @Suppress("UNCHECKED_CAST") (aux as TKVEntry<A, V>?)
        return aux
    }
    
    // implementation
    
    val rkt: RestrictedKeyType<A> by lazy { SymKeyType(fpickKey()::class) }
    
    fun toFIKSetNotEmpty(): FKSet<Int, A> = if (fpick()!! is Int) @Suppress("UNCHECKED_CAST") (this as FKSet<Int, A>)
        else ofFIKSBody(this.ffold(nul()) { acc, item -> acc.finsertTkv(ofIntKey(item)) })

    fun toFSKSetNotEmpty(): FKSet<String, A> = if (fpick()!! is String) @Suppress("UNCHECKED_CAST") (this as FKSet<String, A>)
        else ofFSKSBody(this.ffold(nul()) { acc, item -> acc.finsertTkv(ofStrKey(item)) })

    companion object {
        internal fun <A> of(b: FRBTKNode<A>): FKSet<A, A> where A: Any, A: Comparable<@UnsafeVariance A> = FKKSetNotEmpty(b)
    }
}

internal fun <A> ofFKKSBody(b: FRBTree<A, A>): FKSet<A, A> where A: Any, A: Comparable<@UnsafeVariance A> = when(b) {
    is FRBTKNode -> FKKSetNotEmpty.of(b)
    else -> FKKSetEmpty.empty()
}

internal fun <A> ofFKKSNotEmpty(b: FRBTKNode<A>): FKSet<A, A> where  A: Any, A: Comparable<@UnsafeVariance A> = FKKSetNotEmpty.of(b)

internal fun <K, A:Any> ofBody(b: FRBTree<K, A>): FKSet<K, A>? where K: Any, K: Comparable<@UnsafeVariance K> = when(b) {
    is FRBTINode -> (@Suppress("UNCHECKED_CAST") (FIKSetNotEmpty.of(b) as FKSet<K, A>))
    is FRBTSNode -> (@Suppress("UNCHECKED_CAST") (FSKSetNotEmpty.of(b) as FKSet<K, A>))
    is FRBTKNode<*> -> (@Suppress("UNCHECKED_CAST") (FKKSetNotEmpty.of(b) as FKSet<K, A>))
    else -> null
}

internal val compKc = Comparable::class
internal val intKc = Int::class
internal val strKc = String::class


private fun <KK, K, A: Any> forceKey(candidate: IMSet<A>): IMKSetNotEmpty<KK, A>? where KK: Any, KK: Comparable<KK>, K: Any, K: Comparable<K> =
    @Suppress("UNCHECKED_CAST") (candidate as? IMKSetNotEmpty<KK, A>)

private fun <K, V: Any> makeTKVEntry(s: FKSet<K, V>, maybev: Any, k: K, v: V): RTKVEntry<K, V>? where K: Any, K: Comparable<K> {
    val kc: KClass<out K> by lazy { k::class }
    val vc = v::class
    val mvc = maybev::class
    val aux: RTKVEntry<K, V>? = when (s) {
        is FIKSetNotEmpty -> if (mvc == vc) @Suppress("UNCHECKED_CAST") (ofIntKey(maybev) as? RTKVEntry<K, V>) else null
        is FSKSetNotEmpty -> if (mvc == vc) @Suppress("UNCHECKED_CAST") (ofStrKey(maybev) as? RTKVEntry<K, V>) else null
        is FKKSetNotEmpty<*> -> if ((kc == vc) && (mvc == kc)) {
            @Suppress("UNCHECKED_CAST") (maybev as? K)?.let{
            (@Suppress("UNCHECKED_CAST") (it.toKKEntry() as? RTKVEntry<K, V>))
        }} else null
        is FKSetEmpty -> throw RuntimeException("internal error")
    }
    return aux
}