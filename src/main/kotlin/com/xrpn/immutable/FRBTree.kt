package com.xrpn.immutable

import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.*
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import kotlin.math.log2
import kotlin.reflect.KClass

sealed class FRBTree<out A, out B: Any>: Collection<TKVEntry<A, B>>, IMBTree<A, B> where A: Any, A: Comparable<@UnsafeVariance A> {

    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("ffilterNot"))
    fun dropWhile(predicate: (TKVEntry<A,B>) -> Boolean): List<TKVEntry<A,B>> = throw RuntimeException(predicate.toString())
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("ffilter()"))
    fun takeWhile(predicate: (TKVEntry<A,B>) -> Boolean): List<TKVEntry<A,B>> = throw RuntimeException(predicate.toString())
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<TKVEntry<A,B>>> = throw RuntimeException("$size $step $partialWindows")
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun <C> runningFold(initial: C, operation: (acc: C, TKVEntry<A,B>) -> C): List<C> = throw RuntimeException("$initial $operation")
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun <C> runningFoldIndexed(initial: C, operation: (index: Int, acc: C, TKVEntry<A,B>) -> C): List<C> = throw RuntimeException("$initial $operation")
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun runningReduce(operation: (acc: TKVEntry<A,B>, TKVEntry<A,B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): List<TKVEntry<A,B>> = throw RuntimeException("$operation")
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun runningReduceIndexed(operation: (index: Int, acc: TKVEntry<A,B>, TKVEntry<A,B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): List<TKVEntry<A,B>> = throw RuntimeException("$operation")
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun withIndex(): Iterable<IndexedValue<TKVEntry<A,B>>> = throw RuntimeException()
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun <C, D> zip(other: Array<out C>, transform: (a: TKVEntry<A,B>, b: C) -> D): List<D> = throw RuntimeException("$other, $transform")
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun <C> zip(other: Iterable<C>): List<Pair<A, C>> = throw RuntimeException("$other")
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun zipWithNext(): List<Pair<A, A>> = throw RuntimeException()
    @Deprecated("Tree has ambiguous ordering.", ReplaceWith("traverse first"))
    fun <C> zipWithNext(transform: (a: TKVEntry<A,B>, b: TKVEntry<A,B>) -> C): List<C>  = throw RuntimeException("$transform")

    // =========== Collection

    override fun isEmpty(): Boolean = this is FRBTNil

    override val size: Int by lazy { when (this) {
        is FRBTNil -> 0
        is FRBTNode -> 1 + this.bLeft.size + this.bRight.size
    }}

    override operator fun contains(element: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = when(this) {
        is FRBTNil -> false
        is FRBTNode<A, B> -> this.fcontains(element)
    }

    override fun containsAll(elements: Collection<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): Boolean {
        elements.forEach { if (!this.fcontains(it)) return false }
        return true
    }

    override fun iterator(): Iterator<TKVEntry<A, B>> = FTreeIterator(this)

    /*
        A balanced Red-Black binary search tree shall not allow duplicate keys, but provides guarantees on the
        tree shape at the cost of more expensive insertion and deletion operations than those of a plain Binary
        search tree.

        At any time the max height of a Red-Black tree is not larger than 2 times the log2 of the number of nodes
        in the tree. With these constraints, it follows in practice that a tree with ~1E10 nodes (10 giganodes)
        will have max depth of not more than ~66.  A tree with ~1E11 nodes will probably run out of memory on most
        current (2021) reasonable computers.

        A recursive implementation will be stack safe on most (reasonable or otherwise) computer for at least a while
        longer.  This is a mostly recursive implementation of an immutable Red-Black binary search tree, freely adapted
        from the paper "Left-leaning Red-Black Trees", by Robert Sedgewick.

        That paper retrieved from https://www.cs.princeton.edu/~rs/talks/LLRB/LLRB.pdf on Dec, 2020
     */

    // =========== imcommon

    override val seal = IMSC.IMTREE

    override fun fcontains(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean =
        rbtFind(this, item) != null

    override fun fdropAll(items: IMCommon<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FRBTree<A, B> = if (items.fempty()) this else when (items) {
        // TODO consider memoization
        is IMBTree -> this.fdropAlt(items) as FRBTree<A, B>
        is IMMap ->this.fdropAlt(items.asIMBTree()) as FRBTree<A, B>
        else -> rbtDeletes(this, items)
    }

    override fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> {
        // TODO consider memoization
        return rbtDelete(this, item)
    }

    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): FRBTree<A, B> {

        // TODO common code
        val ff4ffilter: (acc: FRBTree<A, B>, item: TKVEntry<A, B>) -> FRBTree<A, B> =
            { acc, item -> if (isMatch(item)) rbtInsert(acc, item) else acc }

        return ffold(nul(), ff4ffilter)
    }

    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): FRBTree<A, B> =
        ffilter { !isMatch(it) }

    override fun ffindAny(isMatch: (TKVEntry<A, B>) -> Boolean): TKVEntry<A, B>? {

        fun traverse(t: FRBTree<A, B>, acc: Pair<Boolean, TKVEntry<A, B>>): Pair<Boolean, TKVEntry<A, B>> = if (acc.first) acc else when (t) {
            is FRBTNil -> acc
            is FRBTNode -> traverse(t.bRight, traverse(t.bLeft, Pair(isMatch(t.entry), t.entry)))
        }

        return if (this.fempty()) null else {
            val start: Pair<Boolean, TKVEntry<A, B>> = Pair(false, this.froot()!!)
            val stop = traverse(this, start)
            if (stop.first) stop.second else null
        }
    }

    // TODO this cannot be lazy if tree contains mutable containers
    private val strictness: Boolean by lazy { when {
        fempty() -> true
        fisNested()!! -> {
            val maybeNotEmpty = fpickNotEmpty()
            val tkvClass: KClass<*>? = maybeNotEmpty?.let { it::class }
            val kc: KClass<out B>? = maybeNotEmpty?.getvKc()
            kc?.let { valueKClass ->
                val ucKc = SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>()
                null == ffindAny { tkv -> !FT.entryStrictness(tkv, tkvClass!!, valueKClass, ucKc) }
            } ?: /* nested, but all empty */ run {
                val aux = fpick()!!::class
                fall {
                    check(it.toUCon()?.isEmpty() ?: false )
                    it.isStrictly(aux) && it.fisStrict()
                }
            }
        }
        else -> {
            val aux = fpick()!!::class
            fall { tkv: TKVEntry<A, B> -> tkv.isStrictly(aux) && tkv.fisStrict() }
        }
    }}

    override fun fisStrict(): Boolean = strictness

    override fun fpick(): TKVEntry<A,B>? = froot()

    override fun fpopAndRemainder(): Pair<TKVEntry<A,B>?, FRBTree<A, B>> {
        val pop: TKVEntry<A,B>? = this.fpeek()
        // computing the remainder can be very expensive; if traversing
        // the full tree, .inorder() or .forEach() may be cheaper
        val remainder: FRBTree<A, B> = pop?.let { this.fdropItem(it) } ?: FRBTNil
        return Pair(pop, remainder)
    }

    override fun fsize(): Int = size

    // =========== imkeyed

    override fun fcontainsKey(key: @UnsafeVariance A): Boolean =
        rbtFindKey(this, key) != null

    override fun fcountKey(isMatch: (A) -> Boolean): Int {

        val f4fcountKey: (acc: Int, item: TKVEntry<A, B>) -> Int =
            { acc, item -> if (isMatch(item.getk())) acc + 1 else acc }

        return ffold(0, f4fcountKey)
    }

    override fun fdropKeys(keys: IMSet<@UnsafeVariance A>): FRBTree<A, B> = keys.ffold(nul()) { acc, key ->
        if (fcontainsKey(key)) acc else rbtFindKey(this, key)?.let {
            finsert(it.froot()!!)
        } ?: acc
    }

    override fun ffilterKey(isMatch: (A) -> Boolean): FRBTree<A, B> {

        val f4ffilterKey: (acc: FRBTree<A, B>, item: TKVEntry<A, B>) -> FRBTree<A, B> =
            { acc, item -> if (isMatch(item.getk())) rbtInsert(acc, item) else acc }

        return ffold(nul(), f4ffilterKey)
    }

    override fun ffilterKeyNot(isMatch: (A) -> Boolean): FRBTree<A, B> =
        ffilterKey { !isMatch(it) }


    // =========== imkeyedvalue

    override fun asIMMap(): IMMap<A, B> = toIMMap()

    override fun ffilterValue(isMatch: (B) -> Boolean): FRBTree<A, B> {

        val f4ffilterValue: (acc: FRBTree<A, B>, item: TKVEntry<A, B>) -> FRBTree<A, B> =
            { acc, item -> if (isMatch(item.getv())) rbtInsert(acc, item) else acc }

        return ffold(nul(), f4ffilterValue)
    }

    override fun ffilterValueNot(isMatch: (B) -> Boolean): FRBTree<A, B> =
        ffilterValue { !isMatch(it) }

    override fun ffindAnyValue(isMatch: (B) -> Boolean): B? {

        fun traverse(t: FRBTree<A, B>, acc: Pair<Boolean, TKVEntry<A, B>>): Pair<Boolean, TKVEntry<A, B>> = if (acc.first) acc else when (t) {
            is FRBTNil -> acc
            is FRBTNode -> traverse(t.bRight, traverse(t.bLeft, Pair(isMatch(t.entry.getv()), t.entry)))
        }

        return if (this.fempty()) null else {
            val start: Pair<Boolean, TKVEntry<A, B>> = Pair(false, this.froot()!!)
            val stop = traverse(this, start)
            if (stop.first) stop.second.getv() else null
        }
    }

    override fun fget(key: @UnsafeVariance A): B? = when (this) {
        is FRBTNil -> null
        is FRBTNode -> ffindValueOfKey(key)
    }

    // =========== extras

    override operator fun set(k: @UnsafeVariance A, v: @UnsafeVariance B): FRBTree<A, B> =
        finsert(TKVEntry.ofkv(k,v))

    override operator fun get(key: @UnsafeVariance A): B? =
        ffindValueOfKey(key)

    // =========== utility

    override fun equal(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): Boolean = this.equals(rhs)

    override fun toIMSet(kType: RestrictedKeyType<@UnsafeVariance A>?): FKSet<A, B>? = asFKSetImpl(this, kType)

    override fun <K> toIMBTree(kType: RestrictedKeyType<@UnsafeVariance K>): IMBTree<K, B>? where K: Any, K: Comparable<K> =
        toFRBTreeImpl(this, kType)

    override fun toIMMap(): FKMap<A, B> = ofFKMapBody(this)

    override fun copy(): FRBTree<A, B> = this.ffold(nul()) { acc, tkv -> acc.finsert(tkv) }

    // =========== traversable

    override fun breadthFirst(reverse: Boolean): FList<TKVEntry<A,B>> {

        tailrec fun <C> unwindQueue(
            queue: FQueue<FRBTNode<A,B>>,
            acc: C,
            accrue: (FQueue<FRBTNode<A,B>>, C) -> Pair<C, FQueue<FRBTNode<A,B>>>): C =
            if (queue.isEmpty()) acc else {
                val (newAcc, newQueue) = accrue(queue, acc)
                unwindQueue(newQueue, newAcc, accrue)
            }

        fun accrue(q: FQueue<FRBTNode<A,B>>, acc: FList<TKVEntry<A,B>>): Pair<FList<TKVEntry<A,B>>, FQueue<FRBTNode<A,B>>> {
            val (node, dequeued) = q.fdequeueOrThrow()
            val q1 = if (node.bLeft is FRBTNil) dequeued else dequeued.fenqueue(node.bLeft as FRBTNode)
            val q2 = if (node.bRight is FRBTNil) q1 else q1.fenqueue(node.bRight as FRBTNode)
            return Pair(FLCons(node.entry, acc), q2)
        }

        return if (this.fempty()) FLNil else when(reverse) {
            true -> unwindQueue(FQueue.emptyIMQueue<FRBTNode<A, B>>().fenqueue(this as FRBTNode), FLNil, ::accrue)
            false -> unwindQueue(FQueue.emptyIMQueue<FRBTNode<A, B>>().fenqueue(this as FRBTNode), FLNil, ::accrue).freverse()
        }
    }

    override fun inorder(reverse: Boolean): FList<TKVEntry<A,B>> {

        fun traverse(t: FRBTree<A, B>, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
            when (t) {
                is FRBTNil -> acc
                is FRBTNode -> traverse(t.bRight, FLCons(t.entry, traverse(t.bLeft, acc)))
            }

        fun reverseTraverse(t: FRBTree<A, B>, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
            when (t) {
                is FRBTNil -> acc
                is FRBTNode -> reverseTraverse(t.bLeft, FLCons(t.entry, reverseTraverse(t.bRight, acc)))
            }

        return when(reverse) {
            // FList<TKVEntry<A,B>> is assembled in reverse order during the traversal, so
            // this apparently perverse construct is, by virtue of that mechanism, correct
            true -> traverse(this, FLNil)
            false -> reverseTraverse(this, FLNil)
        }
    }

    override fun postorder(reverse: Boolean): FList<TKVEntry<A,B>> {

        fun traverse(t: FRBTree<A, B>, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
            when (t) {
                is FRBTNil -> acc
                is FRBTNode -> FLCons(t.entry, traverse(t.bRight, traverse(t.bLeft, acc)))
            }

        return when(reverse) {
            true -> traverse(this, FLNil)
            false -> traverse(this, FLNil).freverse()
        }
    }

    override fun preorder(reverse: Boolean): FList<TKVEntry<A,B>> {
        val fl = this.ffold(FList.emptyIMList<TKVEntry<A,B>>()) { acc, item -> FLCons(item, acc) }
        return if (reverse) fl else fl.freverse()
    }

    // =========== filtering

    override fun fdropItemAll(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> =
        fdropItem(item)

    override fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): FList<TKVEntry<A, B>> {

        // TODO common code
        // TODO distinguish between I, S, K nodes
        val f4ffind: (acc: FList<TKVEntry<A, B>>, item: TKVEntry<A, B>) -> FList<TKVEntry<A, B>> =
            { acc, item -> if (isMatch(item)) FLCons(item, acc) else acc }

        return ffold(FLNil, f4ffind)
    }

    override fun ffindItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B>? =
        rbtFind(this, item)

    override fun ffindKey(key: @UnsafeVariance A): FRBTree<A, B>? =
        rbtFindKey(this, key)

    override fun ffindLastItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B>? =
        rbtFind(this, item)

    override fun ffindLastKey(key: @UnsafeVariance A): FRBTree<A, B>? =
        rbtFindKey(this, key)

    override fun ffindValueOfKey(key: @UnsafeVariance A): B? =
        rbtFindValueOfKey(this, key)

    override fun fleftMost(): TKVEntry<A, B>? {

        tailrec fun leftDescent(bbt: FRBTree<A, B>): TKVEntry<A, B>? =
            when(bbt) {
                is FRBTNil -> null
                is FRBTNode -> when (bbt.bLeft) {
                    is FRBTNil -> bbt.entry
                    is FRBTNode -> leftDescent(bbt.bLeft)
                }

            }

        return leftDescent(this)
    }

    override fun fhasDups(): Boolean = false

    override fun fisDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = false

    override fun fparentOf(child: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B>? =
        rbtParent(this, child)

    override fun fpeek(): TKVEntry<A,B>? = this.fleftMost() ?: this.froot()

    override fun frestrictedKey(): RestrictedKeyType<A>? = when (this) {
        is FRBTNode -> frbRKeyType
        else -> null
    }

    override fun frightMost(): TKVEntry<A, B>? {

        tailrec fun rightDescent(bbt: FRBTree<A, B>): TKVEntry<A, B>? =
            when(bbt) {
                is FRBTNil -> null
                is FRBTNode -> when (bbt.bRight) {
                    is FRBTNil -> bbt.entry
                    is FRBTNode -> rightDescent(bbt.bRight)
                }
            }

        return rightDescent(this)
    }

    override fun froot(): TKVEntry<A, B>? = when(this) {
        is FRBTNil -> null
        is FRBTNode -> this.entry
    }

    override fun fAND(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> = when (this) {
        is FRBTNil -> this
        is FRBTNode -> if (items.asIMCommon<TKVEntry<A,B>>()!!.fempty()) emptyIMBTree() else ffold(nul()) { acc, tkv ->
            if (items.fcontainsKey(tkv.getk())) acc.finsert(tkv) else acc
        }
    }

    override fun fNOT(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> =
        fdropAlt(items.asIMBTree()) as FRBTree<A, B>

    override fun fOR(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> = when(this) {
        is FRBTNil -> when(val t = items.asIMBTree()) {
            is FRBTree -> t
            is FBSTree -> t.toFRBTree()
            else -> throw RuntimeException("internal error")
        }
        is FRBTNode -> if (items.asIMCommon<TKVEntry<A,B>>()!!.fempty()) this else finsertt(items.asIMBTree()) as FRBTree
    }

    override fun fXOR(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> {
        val t = items.asIMBTree()
        return when(this) {
            is FRBTNil -> when(t) {
                is FRBTree -> t
                is FBSTree -> t.toFRBTree()
                else -> throw RuntimeException("internal error")
            }
            is FRBTNode -> if (items.asIMCommon<TKVEntry<A,B>>()!!.fempty()) this else {
                val bothHave = fAND(t)
                val thisOnly = fNOT(bothHave)
                val itemsOnly = t.fNOT(bothHave)
                thisOnly.finsertt(itemsOnly) as FRBTree
            }
        }
    }

    // =========== grouping

    override fun <C> fgroupBy(f: (TKVEntry<A, B>) -> C): IMMap<C, FRBTree<A, B>> where C: Any, C: Comparable<C>  =
        TODO() //	A map of collections created by the function f

    override fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair</* true */ FRBTree<A, B>, /* false */ FRBTree<A, B>> {

        fun f4fpartition(acc: Pair<FRBTree<A, B>, FRBTree<A, B>>, current: (TKVEntry<A, B>)): Pair<FRBTree<A, B>, FRBTree<A, B>> =
            if (isMatch(current)) Pair(rbtInsert(acc.first, current), acc.second)
            else Pair(acc.first, rbtInsert(acc.second, current))

        return ffold(Pair(nul(), nul()), ::f4fpartition)
    }

    private val maxFrbDepth: Int by lazy { when(this) {
        is FRBTNode -> 1 + when(Pair(this.bLeft is FRBTNode, this.bRight is FRBTNode)) {
            Pair(true, true) -> Integer.max(this.bLeft.fmaxDepth(), this.bRight.fmaxDepth())
            Pair(false, true) -> this.bRight.fmaxDepth()
            Pair(true, false) -> this.bLeft.fmaxDepth()
            else /* leaf */ -> 0
        }
        is FRBTNil -> 0
    }}

    // returns the max path length from the root of a tree to a leaf.
    override fun fmaxDepth(): Int = maxFrbDepth

    val minFrbDepth: Int by lazy { when (this) {
        is FRBTNode -> 1 + when (Pair(this.bLeft is FRBTNode, this.bRight is FRBTNode)) {
            Pair(true, true) -> Integer.min(this.bLeft.fminDepth(), this.bRight.fminDepth())
            Pair(false, true) -> this.bRight.fminDepth()
            Pair(true, false) -> this.bLeft.fminDepth()
            else /* leaf */ -> 0
        }
        is FRBTNil -> 0
    }}

    // returns the minimum path length from the root of a tree to a leaf.
    override fun fminDepth(): Int = minFrbDepth

    // =========== transforming

    override fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): FRBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> =
        this.ffold(nul()) { acc, tkv -> mergeAppender(acc, (f(tkv) as FRBTree<C, D>)) }

    override fun <C> ffold(z: C, f: (acc: C, TKVEntry<A, B>) -> C): C {

        // this is a generic preorder
        fun traverse(t: FRBTree<A, B>, acc: C): C = when (t) {
                is FRBTNil -> acc
                is FRBTNode -> traverse(t.bRight, traverse(t.bLeft, f(acc, t.entry)))
            }

        return traverse(this, z)
    }

    override fun <C, D: Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FRBTree<C, D>
    where C: Any, C: Comparable<@UnsafeVariance C> = if (fempty()) nul<C,D>() else {
        val seed = of(f(froot()!!))
        // worry not, there will be no duplicates by contract
        ffold(seed) { acc, tkv -> acc.finsert(f(tkv)) }
    }

    override fun freduce(f: (acc: TKVEntry<A, B>, TKVEntry<A, B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): TKVEntry<A, B>? = when(this) {  // 	“Reduce” the elements of the list using the binary operator o, going from left to right
        is FRBTNil -> null
        is FRBTNode -> {
            val (seedTkv, stub) = this.fpopAndRemainder()
            stub.ffold(seedTkv!!){ acc, tkv -> f(acc, tkv) }
        }
    }

    // =========== altering

    override fun finsert(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> =
        rbtInsert(this, item)

    override fun finserts(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FRBTree<A, B> =
        rbtInserts(this, items as FList<TKVEntry<A,B>>)

    // =========== internals

    internal fun isRed(): Boolean = when(this) {
        is FRBTNil -> false
        is FRBTNode -> color == RED
    }

    internal fun isLeaf(): Boolean = when(this) {
        is FRBTNil -> false // not a node, hence not a leaf (a particular type of node)
        is FRBTNode -> this.leaf()
    }

    internal fun isDepthInvariant(): Boolean = when(this) {
        is FRBTNil -> true
        is FRBTNode -> {
            val size = this.size
            // val minDepth = this.fminDepth()
            val maxDepth = this.fmaxDepth()
            val maxAllowed = rbMaxDepth(size)
            val p2 = maxDepth <= maxAllowed
            // if (!p2) printErr("size:$size, fail: $minDepth <= $maxDepth <= $maxAllowed")
            p2
        }
    }

    internal fun toFBSTree(): FBSTree<A, B> = FBSTree.of(this.breadthFirst()) // will preserve the balance

    companion object: IMBTreeCompanion {

        internal const val unknownKeyType = "unknown frbtree key type "

        const val NOT_FOUND = -1

        fun <A, B: Any> nul(): FRBTree<A, B> where A: Any, A: Comparable<A> = FRBTNil

        override fun <A, B : Any> emptyIMBTree(): FRBTree<A, B> where A: Any, A : Comparable<A> = nul()

        // =================

        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>): FRBTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator())
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A,B>>): FRBTree<A, B> where A: Any, A: Comparable<A> {
            var res: FRBTree<A, B> = FRBTNil
            items.forEach  { res = rbtInsert(res, it) }
            return res
        }
        override fun <A, B: Any> of(items: IMList<TKVEntry<A,B>>): FRBTree<A,B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), ::rbtInsert)

        // ===============

        override fun <A, B: Any> ofc(cc: Comparator<A>, vararg items: TKVEntry<A,B>): FRBTree<A, B> where A: Any, A: Comparable<A> = ofc(cc, items.iterator())
        override fun <A, B: Any> ofc(cc: Comparator<A>, items: Iterator<TKVEntry<A, B>>): FRBTree<A, B> where A: Any, A: Comparable<A> {
            var res: FRBTree<A, B> = nul()
            items.forEach { res = rbtInsert(res, TKVEntry.ofkvc(it.getk(), it.getv(), cc)) }
            return res
        }

        // =================

        override fun <B : Any> ofvi(vararg items: B): FRBTree<Int, B> = ofvi(items.iterator())
        override fun <B : Any> ofvi(items: Iterator<B>): FRBTree<Int, B> {
            var res: FRBTree<Int, B> = FRBTNil
            items.forEach { res = rbtInsert(res, TKVEntry.ofIntKey(it))}
            return res
        }
        override fun <B : Any> ofvi(items: IMList<B>): FRBTree<Int, B> {
            val f: (IMBTree<Int, B>, B) -> IMBTree<Int, B> = { stub, item -> finsertIK(stub, item) }
            return items.ffoldLeft(emptyIMBTree(), f) as FRBTree<Int, B>
        }

        // =================

        override fun <B : Any> ofvs(vararg items: B): FRBTree<String, B> = ofvs(items.iterator())
        override fun <B : Any> ofvs(items: Iterator<B>): FRBTree<String, B> {
            var res: FRBTree<String, B> = FRBTNil
            items.forEach { res = rbtInsert(res, TKVEntry.ofStrKey(it)) }
            return res
        }
        override fun <B : Any> ofvs(items: IMList<B>): FRBTree<String, B> {
            val f: (IMBTree<String, B>, B) -> IMBTree<String, B> = { stub, item -> finsertSK(stub, item) }
            return items.ffoldLeft(emptyIMBTree(), f) as FRBTree<String, B>
        }

        // =================

        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FRBTree<C, D> where A: Any, A : Comparable<A>, C: Any, C : Comparable<C> {
            var res: FRBTree<C, D> = FRBTNil
            items.forEach { res = rbtInsert(res, f(it)) }
            return res
        }

        // =================

        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, f: (B) -> C): FRBTree<Int, C> {
            var res: FRBTree<Int, C> = FRBTNil
            items.forEach { res = rbtInsert(res, TKVEntry.ofIntKey(f(it))) }
            return res
        }

        // =================

        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, f: (B) -> C): FRBTree<String, C> {
            var res: FRBTree<String, C> = FRBTNil
            items.forEach { res = rbtInsert(res, TKVEntry.ofStrKey(f(it))) }
            return res
        }

        // =================

        override fun <A, B: Any> Map<A, B>.toIMBTree(): FRBTree<A, B> where A: Any, A: Comparable<A> {
            var res: FRBTree<A, B> = nul()
            for (entry in this) { res = res.finsert(TKVEntry.ofkv(entry.key, entry.value)) }
            return res
        }

        // =============== top level type-specific implementation

        internal fun <A, B: Any> rbtDelete(treeStub: FRBTree<A, B>, item: TKVEntry<A, B>): FRBTree<A, B>
        where A: Any, A: Comparable<A> = rbtDeleteKey(treeStub, item.getk())

        // delete entry from treeStub.  Rebalance the tree maintaining immutability as part of deletion.
        private fun <A, B: Any> rbtDeleteKey(treeStub: FRBTree<A, B>, key: A): FRBTree<A, B>
        where A: Any, A: Comparable<A> {

            fun frbDelete(treeStub: FRBTree<A, B>, key: A): FRBTree<A, B> = when (treeStub) {
                is FRBTNil -> FRBTNil
                is FRBTNode -> {
                    val unbalanced: FRBTree<A, B> = when (fitKey(key, treeStub)) {
                        FBTFIT.LEFT -> /* delete left */ when (treeStub.bLeft) {
                            is FRBTNil -> treeStub
                            is FRBTNode -> {
                                val omove: FRBTNode<A, B> =
                                    if (!treeStub.bLeft.isRed() && !treeStub.bLeft.bLeft.isRed()) moveRedLeft(treeStub)
                                    else treeStub
                                FRBTNode.of(omove.entry, omove.color, frbDelete(omove.bLeft, key), omove.bRight)
                            }
                        }
                        FBTFIT.RIGHT, FBTFIT.EQ -> /* delete right or in place */ {
                            val o1 = if (treeStub.bLeft.isRed()) rightRotation(treeStub)
                            else treeStub
                            if (fitKey(key, o1) == FBTFIT.EQ && o1.bRight is FRBTNil) FRBTNil
                            else {
                                val o2 = if ((o1.bRight is FRBTNode) &&
                                    (!o1.bRight.isRed()) &&
                                    (!o1.bRight.bLeft.isRed())
                                ) moveRedRight(o1)
                                else o1
                                if (fitKey(key, o2) == FBTFIT.EQ) {
                                    o2.bRight as FRBTNode
                                    val o2rep = FRBTNode.of(o2.bRight.fleftMost()!!, o2.color, o2.bLeft, o2.bRight)
                                    o2rep.bRight as FRBTNode
                                    FRBTNode.of(o2rep.entry, o2rep.color, o2rep.bLeft, deleteMin(o2rep.bRight))
                                } else FRBTNode.of(o2.entry, o2.color, o2.bLeft, frbDelete(o2.bRight, key))
                            }
                        }
                    }
                    if (unbalanced is FRBTNode) lrf23(unbalanced) else FRBTNil
                }
            }

            return if (!treeStub.fcontainsKey(key)) treeStub else {
                val clipped = frbDelete(treeStub, key)
                if (clipped is FRBTNode) {
                    val blackRoot = FRBTNode.of(clipped.entry, BLACK, clipped.bLeft, clipped.bRight)
                    // next line is very expensive
                    // assert(rbRootSane(blackRoot)) { "$item / $blackRoot" }
                    blackRoot
                } else FRBTNil
            }
        }

        internal tailrec fun <A, B: Any> rbtDeletes(treeStub: FRBTree<A, B>, items: IMCommon<TKVEntry<A,B>>): FRBTree<A, B>
        where A: Any, A: Comparable<A>  {
            val (nextItem: TKVEntry<A, B>?, collection: IMCommon<TKVEntry<A, B>>) = items.fpopAndRemainder()
            return if (null == nextItem) treeStub else rbtDeletes(rbtDelete(treeStub, nextItem), collection)
        }

        // find the node with matching item
        internal fun <A, B: Any> rbtFind(treeStub: FRBTree<A, B>, item: TKVEntry<A,B>): FRBTNode<A, B>?
        where A: Any, A: Comparable<A> = find(treeStub, item, ::fit)

        // find the first tree stub matching key
        internal fun <A, B: Any> rbtFindKey(treeStub: FRBTree<A, B>, key: A): FRBTNode<A, B>?
        where A: Any, A: Comparable<A> = find(treeStub, key, ::fitKey)

        internal fun <A, B: Any> rbtFindValueOfKey(treeStub: FRBTree<A, B>, key: A): B?
        where A: Any, A: Comparable<A> = when(val found = find(treeStub, key, ::fitKey)) {
            is FRBTNode -> found.entry.getv()
            else -> null
        }

        // insert entry into treeStub at the correct position; a duplicate key will replace a matching entry
        // key.  Overall, the resulting balanced tree may not have any entries anywhere with the same key.
        // Rebalance the tree maintaining immutability as part of the insertion process.
        internal fun <A, B: Any> rbtInsert(treeStub: FRBTree<A, B>, item: TKVEntry<A, B>): FRBTNode<A, B>
        where A: Any, A: Comparable<A> {

            fun <A : Comparable<A>, B: Any> copyInsert(treeStub: FRBTree<A, B>, item: TKVEntry<A, B>): FRBTNode<A, B> =
                when (treeStub) {
                    is FRBTNil -> FRBTNode.of(item, RED)
                    is FRBTNode -> {
                        val ofit = when (fit(item, treeStub)) {
                            FBTFIT.EQ -> /* duplicate key */ {
                                // replace?  but why...
                                // FRBTNode.of(item, treeStub.color, treeStub.bLeft, treeStub.bRight)
                                // TODO remove in time
                                check(item.getv().equals(treeStub.entry.getv()))
                                treeStub
                            }
                            FBTFIT.LEFT -> /* insert left */
                                FRBTNode.of(treeStub.entry, treeStub.color, copyInsert(treeStub.bLeft, item), treeStub.bRight)
                            FBTFIT.RIGHT -> /* insert right */
                                FRBTNode.of(treeStub.entry, treeStub.color, treeStub.bLeft, copyInsert(treeStub.bRight, item))
                        }
                        lrf23(ofit)
                    }
                }

            fun run(): FRBTNode<A, B> {
                val grown: FRBTNode<A, B> = copyInsert(treeStub, item)
                return FRBTNode.of(grown.entry, BLACK, grown.bLeft, grown.bRight)
            }

            val aux = rbtFind(treeStub, item)?.let{
                val res = treeStub as FRBTNode<A, B>
                // TODO remove in time
                check(item.getv().equals(it.entry.getv()))
                res
            } ?: run()
            return aux
        }

        internal tailrec fun <A, B: Any> rbtInserts(treeStub: FRBTree<A, B>, items: FList<TKVEntry<A,B>>): FRBTree<A, B>
        where A: Any, A: Comparable<A> = when (items) {
                is FLNil -> treeStub
                is FLCons -> rbtInserts(rbtInsert(treeStub, items.head), items.tail)
            }

        internal fun <A, B: Any> rbtParent(treeStub: FRBTree<A, B>, childItem: TKVEntry<A, B>): FRBTree<A, B>?
        where A: Any, A: Comparable<A> {

            // TODO common code

            tailrec fun go(
                stub: FRBTree<A, B>,
                family: Pair<FRBTree<A, B>, FRBTree<A, B>>
            ): Pair<FRBTree<A, B>, FRBTree<A, B>> = when (stub) {
                is FRBTNil -> family
                is FRBTNode -> {
                    val next: Pair<FRBTree<A, B>, FRBTree<A, B>>? = when (fit(childItem, stub)) {
                        FBTFIT.EQ -> null
                        FBTFIT.LEFT -> Pair(stub, stub.bLeft)
                        FBTFIT.RIGHT -> Pair(stub, stub.bRight)
                    }
                    if (next == null) family else go(next.second, next)
                }
            }

            return when(treeStub) {
                is FRBTNil -> null
                is FRBTNode -> when {
                    treeStub.froot()!! == childItem -> nul()
                    treeStub.fcontains(childItem) -> go(treeStub, Pair(FRBTNil, FRBTNil)).first
                    else -> null
                }
            }
        }

        // ================= used in tests

        internal const val RED = true
        internal const val BLACK = false

        internal fun printErr(errorMsg: String) {
            System.err.println(errorMsg)
        }

        internal fun rbMaxDepth(size: Int): Int = ((2.0 * log2(size.toDouble() + 1.0)) + 0.5).toInt()

        // check property invariants for tree
        internal fun <A, B: Any> rbRootInvariant(root: FRBTree<A, B>): Boolean where A: Any, A: Comparable<A> {
            fun red() = isRedInvariant(root)
            fun bal() = isBalanced(root)
            fun dep() = root.isDepthInvariant() // this is a consequence of other assertions in this group
            fun i23() = is23(root)
            val isRC = !root.isRed()
            val sanity = isRC && dep() && i23() && red() && bal()
//            if (!sanity) {
//                println("INSANE, RC:$isRC red invariant:${red()}, balanced:${bal()}, depth invariant:${dep()}, 23:${i23()}")
//                println("INSANE:\n$root")
//            }
            return sanity
        }

        internal inline fun <reified A: Comparable<A>, reified B: Any> toArray(frbt: FRBTree<A, B>): Array<TKVEntry<A, B>> = // where A: Any, A: Comparable<A> =
            FTreeIterator.toArray(frbt.size, FTreeIterator(frbt))

        // ================= internals

        private fun <A, B: Any> checkf(a: TKVEntry<A, B>, b: FRBTNode<A, B>, haze: Boolean): Boolean where A: Any, A: Comparable<A> {
            val res = a.getv().equals(b.entry.getv())
            val goodness = if (haze) !res else res
            if (!goodness) {
                println("${a.getv().hashCode()}:${a.getv()}, $a")
                println("${b.entry.getv().hashCode()}:${b.entry.getv()}, ${b.entry}")
            }
            return goodness
        }

        // the sorting order
        private fun <A, B: Any> fit(a: TKVEntry<A, B>, b: FRBTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = when {
            a == b.entry -> {
                // TODO remove later (hashcode conflict assertion)
                check(checkf(a, b, false))
                FBTFIT.EQ
            }
            a < b.entry -> {
                // TODO remove later (hashcode conflict assertion)
                check(checkf(a, b, true))
                FBTFIT.LEFT
            }
            else -> {
                // TODO remove later (hashcode conflict assertion)
                check(checkf(a, b, true))
                FBTFIT.RIGHT
            }
        }

        private fun <A, B: Any> fitKey(k: A, b: FRBTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = b.entry.fitKeyOnly(k)

        internal fun <A, B: Any> frbtPartAssert(n: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> {
            if (n.bLeft is FRBTNode) check(isBstNode(n.bLeft) && isBalanced(n.bLeft) && n.bLeft.isDepthInvariant()) { "NOT L-SANE\n$n" }
            if (n.bRight is FRBTNode) check(isBstNode(n.bRight) && isBalanced(n.bRight) && n.bRight.isDepthInvariant()) { "NOT R-SANE\n$n" }
            check(isBstNode(n) && n.isDepthInvariant()) { "L-Sane and R-Sane, but NOT SANE\n$n" }
            return n
        }

        // property invariant
        private fun <A, B: Any> isRedInvariant(treeStub: FRBTree<A, B>): Boolean where A: Any, A: Comparable<A> {

            fun redInvariant(x: FRBTree<A, B>?, twoConsecutive: Boolean): Boolean =
                if (twoConsecutive) false
                else if (x == null) true
                else if (x is FRBTNil) true
                else {
                    x as FRBTNode
                    if (!x.isRed()) redInvariant(x.bLeft, false) && redInvariant(x.bRight, false)
                    else redInvariant(x.bLeft, x.bLeft.isRed()) && redInvariant(x.bRight, x.bRight.isRed())
                }

            return when (treeStub) {
                is FRBTNil -> true
                is FRBTNode -> redInvariant(treeStub, treeStub.isRed())
            }
        }

        // property invariant
        private fun <A, B: Any> isBalanced(treeStub: FRBTree<A, B>): Boolean where A: Any, A: Comparable<A> {

            fun go(x: FRBTree<A, B>, countDown: Int): Boolean = when (x) {
                is FRBTNil -> countDown == 0
                is FRBTNode -> {
                    val newCount = if (!x.isRed()) countDown - 1 else countDown
                    go(x.bLeft, newCount) && go(x.bRight, newCount)
                }
            }

            return when (treeStub) {
                is FRBTNil -> true
                is FRBTNode -> {
                    val halfCount = generateSequence(treeStub) {
                        when (it.bLeft) {
                            is FRBTNil -> null
                            is FRBTNode -> it.bLeft
                        }
                    }.filter { !it.isRed() }.count()
                    return go(treeStub, halfCount)
                }
            }
        }

        // property invariant
        private fun <A, B: Any> isBstNode(node: FRBTNode<A, B>): Boolean where A: Any, A: Comparable<A> =
            when (Pair(node.bLeft is FRBTNode, node.bRight is FRBTNode)) {
                Pair(true, true) -> {
                    node.bLeft as FRBTNode
                    node.bRight as FRBTNode
                    fit(node.bLeft.entry, node) == FBTFIT.LEFT && fit(node.bRight.entry, node) == FBTFIT.RIGHT
                }
                Pair(true, false) -> {
                    node.bLeft as FRBTNode
                    fit(node.bLeft.entry, node) == FBTFIT.LEFT
                }
                Pair(false, true) -> {
                    node.bRight as FRBTNode
                    fit(node.bRight.entry, node) == FBTFIT.RIGHT
                }
                else -> true
            }

        // property invariant
        private fun <A, B: Any> is23(node: FRBTree<A, B>): Boolean where A: Any, A: Comparable<A> = when (node) {
            is FRBTNil -> true
            is FRBTNode -> {
                if (node.bRight.isRed()) false
                else if (node.isRed() && node.bLeft.isRed()) false
                else when (Pair(node.bLeft is FRBTNode, node.bRight is FRBTNode)) {
                    Pair(true, true) -> {
                        node.bLeft as FRBTNode
                        node.bRight as FRBTNode
                        if (isBstNode(node.bLeft) && isBstNode(node.bRight)) {
                            is23(node.bLeft) && is23(node.bRight)
                        } else false
                    }
                    Pair(true, false) -> {
                        node.bLeft as FRBTNode
                        if (isBstNode(node.bLeft)) is23(node.bLeft) else false
                    }
                    Pair(false, true) ->{
                        node.bRight as FRBTNode
                        if (isBstNode(node.bRight)) is23(node.bRight)
                        else false
                    }
                    else -> true
                }
            }
        }

        private tailrec fun <A, B: Any, C: Any> find(treeStub: FRBTree<A, B>, item: C, fitMode: (C, FRBTNode<A, B>) -> FBTFIT): FRBTNode<A, B>?
        where A: Any, A: Comparable<A> = when (treeStub) {
            is FRBTNil -> null
            is FRBTNode -> {
                val next: FRBTree<A, B>? = when (fitMode(item, treeStub)) {
                    FBTFIT.EQ -> null
                    FBTFIT.LEFT -> treeStub.bLeft
                    FBTFIT.RIGHT -> treeStub.bRight
                }
                if (next == null) treeStub else find(next, item, fitMode)
            }
        }

        private fun <A, B: Any> flipColor(node: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> =
            FRBTNode.of(node.entry, !node.color,
                // left
                if (node.bLeft is FRBTNode) FRBTNode.of(node.bLeft.entry, !node.bLeft.color, node.bLeft.bLeft, node.bLeft.bRight)
                else node.bLeft,
                // right
                if (node.bRight is FRBTNode) FRBTNode.of(node.bRight.entry, !node.bRight.color, node.bRight.bLeft, node.bRight.bRight)
                else node.bRight
            )

        /*
            Elementary left rotation.
            Promote the right child (B) to be the root of the subtree.
            Move the old root (A) to be the left child of the new root.
            If new root already had a left child then make it the right child of the new left child.
            Since the new root (B) was the right child of (A), the right child of A is guaranteed to be
            empty at this point: add a new node as the right child without any further consideration.
         */
        private fun <A, B: Any> leftRotation(node: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> {
            assert(node.bRight is FRBTNode) { "ERROR: bRight should be node, left rotation of:\n==>> $node" }
            node.bRight as FRBTNode
            assert(node.bRight.color == RED) { "ERROR: bRight should be red, left rotation of:\n==>> $node" }
            val newLeft = FRBTNode.of(node.entry, RED, node.bLeft, node.bRight.bLeft)
            return FRBTNode.of(node.bRight.entry, node.color, newLeft, node.bRight.bRight)
        }

        /*
            Elementary right rotation.
            Promote the left child (B) to be the root of the subtree.
            Move the old root (A) to be the right child of the new root.
            If the new root already had a right child then make it the left child of the new right child.
            Since the new root (B) was the left child of (A), the left child of (A) is guaranteed to be
            empty at this point: add a new node as the left child without any further consideration.
         */
        private fun <A, B: Any> rightRotation(node: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> {
            assert(node.bLeft is FRBTNode) { "ERROR: bLeft should be node, right rotation of:\n==>> $node" }
            node.bLeft as FRBTNode
            assert(node.bLeft.color == RED) { "ERROR: bLeft should be red, right rotation of:\n==>> $node" }
            val newRight = FRBTNode.of(node.entry, RED, node.bLeft.bRight, node.bRight)
            return FRBTNode.of(node.bLeft.entry, node.color, node.bLeft.bLeft, newRight)
        }

        private fun <A, B: Any> lrf23(node: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> {
            val oleft = if (node.bRight.isRed() && !node.bLeft.isRed()) leftRotation(node) else node
            val oright = if (oleft.bLeft.isRed() && (oleft.bLeft as FRBTNode).bLeft.isRed()) rightRotation(oleft) else oleft
            return if (oright.bLeft.isRed() && oright.bRight.isRed()) flipColor(oright) else oright
        }

        private fun <A, B: Any> moveRedLeft(node: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> {
            val flipped = flipColor(node)
            return if (flipped.bRight is FRBTNode && flipped.bRight.bLeft.isRed()) {
                val newRight = rightRotation(flipped.bRight)
                val newFlipped = FRBTNode.of(node.entry, flipped.color, flipped.bLeft, newRight)
                val aux = leftRotation(newFlipped)
                flipColor(aux)
            } else flipped
        }

        private fun <A, B: Any> moveRedRight(node: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> {
            val flipped = flipColor(node)
            return if (flipped.bLeft is FRBTNode && flipped.bLeft.bLeft.isRed()) {
                val aux = rightRotation(flipped)
                flipColor(aux)
            } else flipped
        }

        private fun <A, B: Any> deleteMin(node: FRBTNode<A, B>): FRBTree<A, B> where A: Any, A: Comparable<A> {
            val nmin = when(node.bLeft) {
                is FRBTNil -> FRBTNil
                is FRBTNode -> {
                    val omove = if (!node.bLeft.isRed() && !node.bLeft.bLeft.isRed()) moveRedLeft(node) else node
                    val onew = if (omove.bLeft is FRBTNode) FRBTNode.of(omove.entry, omove.color, deleteMin(omove.bLeft), omove.bRight) else omove
                    onew
                }
            }
            return if (nmin is FRBTNode) lrf23(nmin) else nmin
        }

        private tailrec fun <A, B: Any> mergeAppender(t1: FRBTree<A, B>, t2: FRBTree<A, B>): FRBTree<A, B> where A: Any, A: Comparable<A> = when {
            t1 is FRBTNil -> t2
            t2 is FRBTNil -> t1
            else -> if (t1.size < t2.size) {
                    val (entry, stub) = t1.fpopAndRemainder()
                    mergeAppender(t2.finsert(entry!!), stub)
                } else {
                    val (entry, stub) = t2.fpopAndRemainder()
                    mergeAppender(t1.finsert(entry!!), stub)
                }
            }

        private fun <A, B: Any> asFKSetImpl(t: FRBTree<A, B>, kType: RestrictedKeyType<A>?): FKSet<A, B>? where A: Any, A: Comparable<A> = when {
            t.fempty() -> kType?.let { emptyIMKSet(it) }
            null == kType -> t.frestrictedKey()?.let { asFKSetImpl(t, it) }
            t.frestrictedKey() == kType -> ofBody(t as FRBTNode)
            (kType is IntKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (ofFIKSBody(t as FRBTree<Int,A>) as FKSet<A, B>)
            (kType is StrKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (ofFSKSBody(t as FRBTree<String,A>) as FKSet<A, B>)
            else -> null
        }

        private fun <K, A, B:Any> toFRBTreeImpl(t: FRBTree<A, B>, kType: RestrictedKeyType<K>): FRBTree<K, B>? where A: Any, A: Comparable<A>, K: Any, K: Comparable<K> = when {
            t.fempty() -> nul()
            t.frestrictedKey() == kType -> @Suppress("UNCHECKED_CAST") (t as FRBTree<K, B>)
            (kType is IntKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (t as FRBTree<K, B>)
            (kType is StrKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (t as FRBTree<K, B>)
            else ->  when (kType) {
                is IntKeyType ->   {
                    val res: FRBTree<Int, B> = t.ffold(nul()) { acc, tkv -> acc.finsert(tkv.getv().toIAEntry()) }
                    @Suppress("UNCHECKED_CAST") (res as FRBTree<K, B>)
                }
                is StrKeyType -> {
                    val res: FRBTree<String, B> = t.ffold(nul()) { acc, tkv -> acc.finsert(tkv.getv().toSAEntry()) }
                    @Suppress("UNCHECKED_CAST") (res as FRBTree<K, B>)
                }
                is SymKeyType -> if (kType.kc != t.froot()!!.getvKc()) null else {
                    t.ffold(nul<K,B>()) { acc, tkv ->
                        val k = @Suppress("UNCHECKED_CAST") (tkv.getv() as K)
                        val entry = TKVEntry.ofkv(k, tkv.getv())
                        acc.finsert(entry)
                    }
                }
                is DeratedCustomKeyType -> kType.specialize<K>()?.let { toFRBTreeImpl(t, it) }
            }
        }

    }
}

internal object FRBTNil: FRBTree<Nothing, Nothing>() {
    override fun toString(): String = "*"
    override fun hashCode(): Int = FLNil.toString().hashCode()
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMBTree<*, *> -> other.fempty()
        else -> false
    }
}

internal interface FRBTRNode<out A: Any> {
    fun typedEqual(other: FRBTRNode<@UnsafeVariance A>): Boolean = when (this) {
        is FRBTINode<*> -> other is FRBTINode<*> && IMBTreeEqual2 (this, other)
        is FRBTSNode<*> -> other is FRBTSNode<*> && IMBTreeEqual2 (this, other)
        is FRBTKNode<*> -> other is FRBTKNode<*> && IMBTreeEqual2 (this, other)
        else -> false
    }
}

internal open class FRBTNode<A, B: Any> protected constructor (
    val entry: TKVEntry<A, B>,
    val color: Boolean = RED,
    val bLeft: FRBTree<A, B> = FRBTNil,
    val bRight: FRBTree<A, B> = FRBTNil,
): FRBTree<A, B>() where A: Any, A: Comparable<A> {

    internal fun leaf() = bLeft is FRBTNil && bRight is FRBTNil

    val show: String by lazy {
        val sz: String = when (val ns = size) {
            0 -> ""
            else -> "{$ns}"
        }
        val col = if(color) "r$sz" else "b$sz"
        if (leaf()) "($entry@$col)" else "($entry@$col, <$bLeft, >$bRight)"
    }

    override fun toString(): String = show


    // short of type erasure, this must maintain reflexive, symmetric and transitive properties
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FRBTNode<*,*> -> when {
            other.fempty() -> false
            // color != other.color -> false
            entry.getvKc().isStrictlyNot(other.entry.getvKc()) -> false
            this is FRBTRNode<*> -> typedEqual(other as FRBTRNode<Any>)
            entry.getkKc().isStrictlyNot(other.entry.getkKc()) -> false
            else -> @Suppress("UNCHECKED_CAST") IMBTreeEqual2 (this, other as FRBTNode<A,B>)
        }
        other is IMBTree<*, *> -> when {
            other.fempty() -> false
            entry.strictlyNot(other.froot()!!.untype()) -> false
            else -> @Suppress("UNCHECKED_CAST") IMBTreeEqual2 (this, other as IMBTree<A,B>)
        }
        else -> false
    }

    val hash:Int by lazy {
        val seed: Int = when (val rk = this.froot()?.getk()) {
            is Int -> 1 // + color.hashCode()
            is Long -> 1 // + color.hashCode()
            else -> rk.hashCode() // + color.hashCode()
        }
        this.ffold(seed) { acc: Int, tkv -> when(val k = tkv.getk()) {
                is Int -> 31 * acc + k
                is Long -> 31 * acc + k.toInt()
                else -> 31 * acc + k.hashCode()
            }
        }
    }

    override fun hashCode(): Int = hash

    private val frbKeyKClass: KClass<@UnsafeVariance A> by lazy { @Suppress("UNCHECKED_CAST") (froot()!!.getkKc() as KClass<A>) }

    internal val frbRKeyType: RestrictedKeyType<A>? by lazy { when(this) {
        is FRBTINode -> (@Suppress("UNCHECKED_CAST") (IntKeyType as RestrictedKeyType<A>))
        is FRBTSNode -> (@Suppress("UNCHECKED_CAST") (StrKeyType as RestrictedKeyType<A>))
        is FRBTKNode<*> -> SymKeyType(frbKeyKClass)
        else -> null
    }}

    companion object {
        fun <A, B: Any> of(entry: TKVEntry<A, B>,
                           color: Boolean = RED,
        ): FRBTNode<A, B> where A: Any, A: Comparable<@UnsafeVariance A> = when (entry) {
            is RITKVEntry -> @Suppress("UNCHECKED_CAST") (FRBTINode(entry, color) as FRBTNode<A, B>)
            is RSTKVEntry -> @Suppress("UNCHECKED_CAST") (FRBTSNode(entry, color) as FRBTNode<A, B>)
            is RKTKVEntry<*, *> -> {
                val aux = @Suppress("UNCHECKED_CAST") (entry as RKTKVEntry<A, A>)
                @Suppress("UNCHECKED_CAST") (FRBTKNode(aux, color) as FRBTNode<A, B>)
            }
            is TKVEntryK -> FRBTNode(entry, color)
            else -> throw RuntimeException("impossible branch")
        }

        fun <A> of(entry: TKVEntry<A, A>,
                           color: Boolean = RED,
                           bLeft: FRBTree<A, A> = FRBTNil,
                           bRight: FRBTree<A, A> = FRBTNil,
        ): FRBTKNode<A> where A: Any, A: Comparable<@UnsafeVariance A> = when (entry) {
            is RKTKVEntry<A, *> -> {
                val aux = @Suppress("UNCHECKED_CAST") (entry as RKTKVEntry<A, A>)
                FRBTKNode(aux, color, bLeft, bRight)
            }
            else -> throw RuntimeException("inconsistent tree node: entry:${entry::class} bLeft:${bLeft::class} bRight:${bLeft::class}")
        }

        fun <A, B: Any> of(entry: TKVEntry<A, B>,
                           color: Boolean = RED,
                           bLeft: FRBTree<A, B> = FRBTNil,
                           bRight: FRBTree<A, B> = FRBTNil,
        ): FRBTNode<A, B> where A: Any, A: Comparable<@UnsafeVariance A> = when (entry) {
            is RITKVEntry -> {
                check(bLeft.fempty() || bLeft is FRBTINode)
                @Suppress("UNCHECKED_CAST") (bLeft as FRBTree<Int, B>)
                check(bRight.fempty() || bRight is FRBTINode)
                @Suppress("UNCHECKED_CAST") (bRight as FRBTree<Int, B>)
                @Suppress("UNCHECKED_CAST") (FRBTINode(entry, color, bLeft, bRight) as FRBTNode<A, B>)
            }
            is RSTKVEntry -> {
                check(bLeft.fempty() || bLeft is FRBTSNode)
                check(bRight.fempty() || bRight is FRBTSNode)
                @Suppress("UNCHECKED_CAST") (bLeft as FRBTree<String, B>)
                @Suppress("UNCHECKED_CAST") (bRight as FRBTree<String, B>)
                @Suppress("UNCHECKED_CAST") (FRBTSNode(entry, color, bLeft, bRight) as FRBTNode<A, B>)
            }
            is RKTKVEntry<*, *> -> {
                check(bLeft.fempty() || bLeft is FRBTKNode<*>)
                check(bRight.fempty() || bRight is FRBTKNode<*>)
                @Suppress("UNCHECKED_CAST") (bLeft as FRBTree<A, A>)
                @Suppress("UNCHECKED_CAST") (bRight as FRBTree<A, A>)
                val aux = @Suppress("UNCHECKED_CAST") (entry as RKTKVEntry<A, A>)
                @Suppress("UNCHECKED_CAST") (FRBTKNode(aux, color, bLeft, bRight) as FRBTNode<A, B>)
            }
            is TKVEntryK -> FRBTNode(entry, color, bLeft, bRight)
            else -> throw RuntimeException("inconsistent tree node: entry:${entry::class} bLeft:${bLeft::class} bRight:${bLeft::class}")
        }

        fun <A, B: Any> hashCode(n: FRBTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hash
    }
}

internal class FRBTINode<out A: Any> internal constructor (
    e: RITKVEntry<@UnsafeVariance A>,
    c: Boolean = RED,
    bL: FRBTree<Int, A> = FRBTNil,
    bR: FRBTree<Int, A> = FRBTNil,
): FRBTNode<Int, @UnsafeVariance A>(e, c, bL, bR), FRBTRNode<A> {

    companion object {
        internal fun <A, B: Any> asIFRBTree(n: FRBTINode<B>): FRBTree<A, B> where A: Any, A: Comparable<A> =
            @Suppress("UNCHECKED_CAST") (n as FRBTree<A, B>)
        internal fun <B: Any> asIFRBTree(n: FRBTINode<*>, b: B): FRBTree<Int, B> =
            if ((n.froot()!! as RITKVEntry<*>).getvKc().isInstance(b)) @Suppress("UNCHECKED_CAST") (n as FRBTree<Int, B>) else emptyIMBTree()
        fun <A, B: Any> hashCode(n: FRBTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hash
    }
}

internal class FRBTSNode<out A: Any> internal constructor (
    e: RSTKVEntry<@UnsafeVariance A>,
    c: Boolean = RED,
    bL: FRBTree<String, A> = FRBTNil,
    bR: FRBTree<String, A> = FRBTNil,
): FRBTNode<String, @UnsafeVariance A>(e, c, bL, bR), FRBTRNode<A> {

    companion object {
        internal fun <A, B: Any> asSFRBTree(n: FRBTSNode<B>): FRBTree<A, B> where A: Any, A: Comparable<A> =
            @Suppress("UNCHECKED_CAST") (n as FRBTree<A, B>)
        internal fun <B: Any> asSFRBTree(n: FRBTSNode<*>, b: B): FRBTree<String, B> =
            if ((n.froot()!! as RSTKVEntry<*>).getvKc().isInstance(b)) @Suppress("UNCHECKED_CAST") (n as FRBTree<String, B>) else emptyIMBTree()
        fun <A, B: Any> hashCode(n: FRBTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hash
    }
}

internal class FRBTKNode<out A> internal constructor (
    e: RKTKVEntry<@UnsafeVariance A, @UnsafeVariance A>,
    c: Boolean = RED,
    bL: FRBTree<A, A> = FRBTNil,
    bR: FRBTree<A, A> = FRBTNil,
): FRBTNode<@UnsafeVariance A, @UnsafeVariance A>(e, c, bL, bR), FRBTRNode<A> where A: Any, A: Comparable<@UnsafeVariance A>{

    companion object {
        internal fun <A, B: Any> asKFRBTree(n: FRBTKNode<A>): FRBTree<A, B> where A: Any, A: Comparable<A> =
            @Suppress("UNCHECKED_CAST") (n as FRBTree<A, B>)
        internal fun <A, B: Any> asKFRBTree(n: FRBTKNode<*>, a: A): FRBTree<A, B> where A: Any, A: Comparable<A> =
            if ((n.froot()!! as RKTKVEntry<*, *>).getrk().kc.isInstance(a)) @Suppress("UNCHECKED_CAST") (n as FRBTree<A, B>) else emptyIMBTree()
        fun <A, B: Any> hashCode(n: FRBTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hash
    }
}