package com.xrpn.immutable

import com.xrpn.bridge.FTreeIterator
import com.xrpn.hash.DigestHash
import com.xrpn.imapi.*
import java.math.BigInteger
import kotlin.math.log2


sealed class FRBTree<out A, out B: Any>: Collection<TKVEntry<A, B>>, Set<TKVEntry<A, B>>, IMBTree<A, B> where A: Any, A: Comparable<@UnsafeVariance A> {

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

    override fun contains(element: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = when(this) {
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

    // =========== utility

    override fun toIMSet(): FSet<B> = FSet.of(this)

    override fun copy(): FRBTree<A, B> = this.ffold(nul()) { acc, tkv -> acc.finsert(tkv) }

    // =========== traversable

    override fun preorder(reverse: Boolean): FList<TKVEntry<A,B>> {

//        fun traverse(t: FRBTree<A, B>, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
//            when (t) {
//                is FRBTNil -> acc
//                is FRBTNode -> traverse(t.bRight, traverse(t.bLeft, FLCons(t.entry, acc)))
//            }
        val fl = this.ffold(FList.emptyIMList<TKVEntry<A,B>>()) { acc, item -> FLCons(item, acc) }
        return when(reverse) {
//            true -> traverse(this, FLNil)
            true -> fl
            false -> fl.freverse()
//            false -> traverse(this, FLNil).freverse()
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
            val (node, dequeued) = q.dequeue()
            val q1 = if (node.bLeft is FRBTNil) dequeued else FQueue.enqueue(dequeued, node.bLeft as FRBTNode)
            val q2 = if (node.bRight is FRBTNil) q1 else FQueue.enqueue(q1, node.bRight as FRBTNode)
            return Pair(FLCons(node.entry, acc), q2)
        }

        return if (this.fempty()) FLNil else when(reverse) {
            true -> unwindQueue(FQueue.enqueue(FQueue.emptyFQueue(), this as FRBTNode), FLNil, ::accrue)
            false -> unwindQueue(FQueue.enqueue(FQueue.emptyFQueue(), this as FRBTNode), FLNil, ::accrue).freverse()
        }
    }

    // =========== filtering

    override fun fcontains(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean =
        rbtFind(this, item) != null

    override fun fcontainsKey(key: @UnsafeVariance A): Boolean =
        rbtFindKey(this, key) != null

    override fun fdropAll(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FRBTree<A, B> {
        // TODO memoization
        return rbtDeletes(this, items as FList<TKVEntry<A,B>>)
    }

    override fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> {
        // TODO memoization
        return rbtDelete(this, item)
    }

    override fun fdropItemAll(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> {
        // TODO memoization
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

    override fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): FList<TKVEntry<A, B>> {

        // TODO common code
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
        rbtFindValueOFKey(this, key)

    override fun fhasDups(): Boolean = false

    override fun fisDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = false

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

    override fun fparentOf(child: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B>? =
        rbtParent(this, child)

    override fun fpick(): TKVEntry<A,B>? = this.fleftMost() ?: this.froot()

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

    // =========== grouping

    override fun fsize(): Int = size

    override fun <C> fgroupBy(f: (TKVEntry<A, B>) -> C): IMMap<C, FRBTree<A, B>> where C: Any, C: Comparable<C>  =
        TODO() //	A map of collections created by the function f

    override fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair</* true */ FRBTree<A, B>, /* false */ FRBTree<A, B>> {

        fun f4fpartition(acc: Pair<FRBTree<A, B>, FRBTree<A, B>>, current: (TKVEntry<A, B>)): Pair<FRBTree<A, B>, FRBTree<A, B>> =
            if (isMatch(current)) Pair(rbtInsert(acc.first, current), acc.second)
            else Pair(acc.first, rbtInsert(acc.second, current))

        return ffold(Pair(nul(), nul()), ::f4fpartition)
    }

    override fun fpopAndReminder(): Pair<TKVEntry<A,B>?, FRBTree<A, B>> {
        val pop: TKVEntry<A,B>? = this.fleftMost() ?: this.froot()
        // computing the reminder can be very expensive; if traversing
        // the full tree, .inorder() or .forEach() may be cheaper
        val reminder: FRBTree<A, B> = pop?.let { this.fdropItem(it) } ?: FRBTNil
        return Pair(pop, reminder)
    }

    // returns the max path length from the root of a tree to a leaf.
    override fun fmaxDepth(): Int = when(this) {
        is FRBTNode -> 1 + when(Pair(this.bLeft is FRBTNode, this.bRight is FRBTNode)) {
            Pair(true, true) -> Integer.max(this.bLeft.fmaxDepth(), this.bRight.fmaxDepth())
            Pair(false, true) -> this.bRight.fmaxDepth()
            Pair(true, false) -> this.bLeft.fmaxDepth()
            else /* leaf */ -> 0
        }
        is FRBTNil -> 0
    }

    // returns the minimum path length from the root of a tree to a leaf.
    override fun fminDepth(): Int = when (this) {
        is FRBTNode -> 1 + when (Pair(this.bLeft is FRBTNode, this.bRight is FRBTNode)) {
            Pair(true, true) -> Integer.min(this.bLeft.fminDepth(), this.bRight.fminDepth())
            Pair(false, true) -> this.bRight.fminDepth()
            Pair(true, false) -> this.bLeft.fminDepth()
            else /* leaf */ -> 0
        }
        is FRBTNil -> 0
    }

    // =========== transforming

    override fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): FRBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> =
        this.ffold(nul()) { acc, tkv -> mergeAppender(acc, (f(tkv) as FRBTree<C, D>)) }

    override fun <C, D: Any> fflatMapDup(allowDups: Boolean, f: (TKVEntry<A, B>) -> IMBTree<C, D>): FRBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> =
        fflatMap(f)

    override fun <C> ffold(z: C, f: (acc: C, TKVEntry<A, B>) -> C): C {

        // this is a generic preorder
        fun traverse(t: FRBTree<A, B>, acc: C): C = when (t) {
                is FRBTNil -> acc
                is FRBTNode -> traverse(t.bRight, traverse(t.bLeft, f(acc, t.entry)))
            }

        return traverse(this, z)
    }

    override fun <C, D: Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FRBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(nul()) { acc, tkv -> acc.finsert(f(tkv)) }

    override fun freduce(f: (acc: TKVEntry<A, B>, TKVEntry<A, B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): TKVEntry<A, B>? = when(this) {  // 	“Reduce” the elements of the list using the binary operator o, going from left to right
        is FRBTNil -> null
        is FRBTNode -> {
            val (seedTkv, stub) = this.fpopAndReminder()
            stub.ffold(seedTkv!!){ acc, tkv -> f(acc, tkv) }
        }
    }

    // =========== altering

    override fun finsert(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FRBTree<A, B> =
        rbtInsert(this, item)

    override fun finsertDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>, allowDups: Boolean): FRBTree<A, B> =
        rbtInsert(this, item)

    override fun finserts(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FRBTree<A, B> =
        rbtInserts(this, items as FList<TKVEntry<A,B>>)

    override fun finsertsDups(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>, allowDups: Boolean): FRBTree<A, B> =
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
            val minDepth = this.fminDepth()
            val maxDepth = this.fmaxDepth()
            val maxAllowed = rbMaxDepth(size)
            val p2 = maxDepth <= maxAllowed
            if (!p2) printErr("size:$size, fail: $minDepth <= $maxDepth <= $maxAllowed")
            p2
        }
    }

    companion object: IMBTreeCompanion {

        const val NOT_FOUND = -1

        fun <A, B: Any> nul(): FRBTree<A, B> where A: Any, A: Comparable<A> = FRBTNil

        override fun <A, B : Any> emptyIMBTree(): IMBTree<A, B> where A: Any, A : Comparable<A> = nul()

        // =================

        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>): FRBTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator())
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <A, B : Any> of(vararg items: TKVEntry<A, B>, allowDups: Boolean): FRBTree<A, B> where A: Any, A : Comparable<A> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else of(items.iterator())
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A,B>>): FRBTree<A, B> where A: Any, A: Comparable<A> {
            var res: FRBTree<A, B> = FRBTNil
            for (item in items)  { res = rbtInsert(res, item) }
            return res
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <A, B : Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): FRBTree<A, B> where A: Any, A : Comparable<A> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else of(items)
        override fun <A, B: Any> of(items: IMList<TKVEntry<A,B>>): FRBTree<A,B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), ::rbtInsert)
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <A, B : Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): FRBTree<A, B> where A: Any, A : Comparable<A> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else of(items)

        // =================

        override fun <B : Any> ofvi(vararg items: B): FRBTree<Int, B> = ofvi(items.iterator())
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <B : Any> ofvi(vararg items: B, allowDups: Boolean): FRBTree<Int, B> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofvi(items.iterator())
        override fun <B : Any> ofvi(items: Iterator<B>): FRBTree<Int, B> {
            var res: FRBTree<Int, B> = FRBTNil
            for (item in items) { res = rbtInsert(res, TKVEntry.ofIntKey(item))}
            return res
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <B : Any> ofvi(items: Iterator<B>, allowDups: Boolean): FRBTree<Int, B> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofvi(items)
        override fun <B : Any> ofvi(items: IMList<B>): FRBTree<Int, B> {
            val f: (IMBTree<Int, B>, B) -> IMBTree<Int, B> = { stub, item -> finsertIK(stub, item) }
            return items.ffoldLeft(emptyIMBTree(), f) as FRBTree<Int, B>
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <B : Any> ofvi(items: IMList<B>, allowDups: Boolean): FRBTree<Int, B> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofvi(items)

        // =================

        override fun <B : Any> ofvs(vararg items: B): FRBTree<String, B> = ofvs(items.iterator())
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <B : Any> ofvs(vararg items: B, allowDups: Boolean): FRBTree<String, B> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofvs(items.iterator())
        override fun <B : Any> ofvs(items: Iterator<B>): FRBTree<String, B> {
            var res: FRBTree<String, B> = FRBTNil
            for (item in items) { res = rbtInsert(res, TKVEntry.ofStrKey(item))}
            return res
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <B : Any> ofvs(items: Iterator<B>, allowDups: Boolean): FRBTree<String, B> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofvs(items)
        override fun <B : Any> ofvs(items: IMList<B>): FRBTree<String, B> {
            val f: (IMBTree<String, B>, B) -> IMBTree<String, B> = { stub, item -> finsertSK(stub, item) }
            return items.ffoldLeft(emptyIMBTree(), f) as FRBTree<String, B>
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("of(items)"))
        override fun <B : Any> ofvs(items: IMList<B>, allowDups: Boolean): FRBTree<String, B> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofvs(items)

        // =================

        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FRBTree<C, D> where A: Any, A : Comparable<A>, C: Any, C : Comparable<C> {
            var res: FRBTree<C, D> = FRBTNil
            for (item in items)  { res = rbtInsert(res, f(item)) }
            return res
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("ofMap(items, f)"))
        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FRBTree<C, D> where A: Any, A : Comparable<A>, C: Any, C : Comparable<C> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofMap(items, f)

        // =================

        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, f: (B) -> C): FRBTree<Int, C> {
            var res: FRBTree<Int, C> = FRBTNil
            for (item in items) { res = rbtInsert(res, TKVEntry.ofIntKey(f(item))) }
            return res
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("ofviMap(items, f)"))
        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FRBTree<Int, C> =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofviMap(items, f)

        // =================

        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, f: (B) -> C): FRBTree<String, C> {
            var res: FRBTree<String, C> = FRBTNil
            for (item in items) { res = rbtInsert(res, TKVEntry.ofStrKey(f(item))) }
            return res
        }
        @Deprecated("FRBTree does not allow duplicates", ReplaceWith("ofvsMap(items, f)"))
        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FRBTree<String, C>  =
            if (allowDups) throw RuntimeException("FRBTree does not allow duplicates") else ofvsMap(items, f)

        // =================

        override fun <A, B : Any> Collection<TKVEntry<A, B>>.toIMBTree(): FRBTree<A, B> where A: Any, A : Comparable<A> = when(this) {
            is FBSTree<*, *> -> @Suppress("UNCHECKED_CAST") of(this.postorder() as IMList<TKVEntry<A, B>>)
            is FRBTree<*, *> -> this as FRBTree<A, B>
            is Array<*> -> of(this.iterator())
            is List<*> -> of(this.iterator())
            is Set<*> -> of(this.iterator())
            else -> /* TODO this would be interesting */ throw RuntimeException(this::class.simpleName)
        }

        override fun <A, B: Any> Map<A, B>.toIMBTree(): FRBTree<A, B> where A: Any, A: Comparable<A> {
            var res: FRBTree<A, B> = nul()
            for (entry in this) { res = res.finsert(TKVEntry.of(entry.key, entry.value)) }
            return res
        }

        // =============== top level type-specific implementation

        // delete entry from treeStub.  Rebalance the tree maintaining immutability as part of deletion.
        internal fun <A, B: Any> rbtDelete(treeStub: FRBTree<A, B>, item: TKVEntry<A, B>): FRBTree<A, B>
        where A: Any, A: Comparable<A> {

            fun frbDelete(treeStub: FRBTree<A, B>, item: TKVEntry<A, B>): FRBTree<A, B> = when (treeStub) {
                is FRBTNil -> FRBTNil
                is FRBTNode -> {
                    val unbalanced: FRBTree<A, B> = when (fit(item, treeStub)) {
                        FBTFIT.LEFT -> /* delete left */ when (treeStub.bLeft) {
                            is FRBTNil -> treeStub
                            is FRBTNode -> {
                                val omove: FRBTNode<A, B> =
                                    if (!treeStub.bLeft.isRed() && !treeStub.bLeft.bLeft.isRed()) moveRedLeft(treeStub)
                                    else treeStub
                                FRBTNode(omove.entry, omove.color, frbDelete(omove.bLeft, item), omove.bRight)
                            }
                        }
                        FBTFIT.RIGHT, FBTFIT.EQ -> /* delete right or in place */ {
                            val o1 = if (treeStub.bLeft.isRed()) rightRotation(treeStub)
                            else treeStub
                            if (fit(item, o1) == FBTFIT.EQ && o1.bRight is FRBTNil) FRBTNil
                            else {
                                val o2 = if ((o1.bRight is FRBTNode) &&
                                    (!o1.bRight.isRed()) &&
                                    (!o1.bRight.bLeft.isRed())
                                ) moveRedRight(o1)
                                else o1
                                if (fit(item, o2) == FBTFIT.EQ) {
                                    o2.bRight as FRBTNode
                                    val o2rep = FRBTNode(o2.bRight.fleftMost()!!, o2.color, o2.bLeft, o2.bRight)
                                    o2rep.bRight as FRBTNode
                                    FRBTNode(o2rep.entry, o2rep.color, o2rep.bLeft, deleteMin(o2rep.bRight))
                                } else FRBTNode(o2.entry, o2.color, o2.bLeft, frbDelete(o2.bRight, item))
                            }
                        }
                    }
                    if (unbalanced is FRBTNode) lrf23(unbalanced) else FRBTNil
                }
            }

            return if (!treeStub.fcontains(item)) treeStub else {
                val clipped = frbDelete(treeStub, item)
                if (clipped is FRBTNode) {
                    val blackRoot = FRBTNode(clipped.entry, BLACK, clipped.bLeft, clipped.bRight)
                    // next line is very expensive
                    // assert(rbRootSane(blackRoot)) { "$item / $blackRoot" }
                    blackRoot
                } else FRBTNil
            }
        }

        internal tailrec fun <A, B: Any> rbtDeletes(treeStub: FRBTree<A, B>, items: FList<TKVEntry<A,B>>): FRBTree<A, B>
        where A: Any, A: Comparable<A> = when (items) {
                is FLNil -> treeStub
                is FLCons -> if (treeStub.fcontains(items.head)) rbtDeletes(rbtDelete(treeStub, items.head), items.tail)
                    else treeStub
            }

        // find the node with matching item
        internal fun <A, B: Any> rbtFind(treeStub: FRBTree<A, B>, item: TKVEntry<A,B>): FRBTNode<A, B>?
        where A: Any, A: Comparable<A> = find(treeStub, item, ::fit)

        // find the first tree stub matching key
        internal fun <A, B: Any> rbtFindKey(treeStub: FRBTree<A, B>, key: A): FRBTNode<A, B>?
        where A: Any, A: Comparable<A> = find(treeStub, key, ::fitKey)

        internal fun <A, B: Any> rbtFindValueOFKey(treeStub: FRBTree<A, B>, key: A): B?
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
                    is FRBTNil -> FRBTNode(item, RED)
                    is FRBTNode -> {
                        val ofit = when (fit(item, treeStub)) {
                            FBTFIT.EQ -> /* duplicate key */
                                FRBTNode(item, treeStub.color, treeStub.bLeft, treeStub.bRight)
                            FBTFIT.LEFT -> /* insert left */
                                FRBTNode(treeStub.entry, treeStub.color, copyInsert(treeStub.bLeft, item), treeStub.bRight)
                            FBTFIT.RIGHT -> /* insert right */
                                FRBTNode(treeStub.entry, treeStub.color, treeStub.bLeft, copyInsert(treeStub.bRight, item))
                        }
                        lrf23(ofit)
                    }
                }

            val grown: FRBTNode<A, B> = copyInsert(treeStub, item)
            return FRBTNode(grown.entry, BLACK, grown.bLeft, grown.bRight)
        }

        internal tailrec fun <A, B: Any> rbtInserts(treeStub: FRBTree<A, B>, items: FList<TKVEntry<A,B>>): FRBTree<A, B>
        where A: Any, A: Comparable<A> = when (items) {
                is FLNil -> treeStub
                is FLCons -> if(treeStub.fcontains(items.head)) rbtInserts(treeStub, items.tail)
                    else rbtInserts(rbtInsert(treeStub, items.head), items.tail)
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
        internal fun <A, B: Any> rbRootSane(root: FRBTree<A, B>): Boolean where A: Any, A: Comparable<A> {
            fun red() = isRedInvariant(root)
            fun bal() = isBalanced(root)
            fun dep() = root.isDepthInvariant() // this is a consequence of other assertions in this group
            fun i23() = is23(root)
            val isRC = !root.isRed()
            val sanity = isRC && dep() && i23() && red() && bal()
            if (!sanity) {
                println("INSANE, RC:$isRC red invariant:${red()}, balanced:${bal()}, depth invariant:${dep()}, 23:${i23()}")
                println("INSANE:\n$root")
            }
            return sanity
        }

        internal inline fun <reified A, reified B: Any> toArray(frbt: FRBTree<A, B>): Array<TKVEntry<A, B>> where A: Any, A: Comparable<A> =
            FTreeIterator.toArray(frbt.size, FTreeIterator(frbt))

        // ================= internals

        // the sorting order
        private fun <A, B: Any> fit(a: TKVEntry<A, B>, b: FRBTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = when {
            a.getk() == b.entry.getk() -> FBTFIT.EQ
            a.getk() < b.entry.getk() -> FBTFIT.LEFT
            else -> FBTFIT.RIGHT
        }

        // the sorting order
        private fun <A, B: Any> fitKey(k: A, b: FRBTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = when {
            k == b.entry.getk() -> FBTFIT.EQ
            k < b.entry.getk() -> FBTFIT.LEFT
            else -> FBTFIT.RIGHT
        }

        internal fun <A, B: Any> frbtPartAssert(n: FRBTNode<A, B>): FRBTNode<A, B> where A: Any, A: Comparable<A> {
            if (n.bLeft is FRBTNode) assert(isBstNode(n.bLeft) && isBalanced(n.bLeft) && n.bLeft.isDepthInvariant()) { "NOT L-SANE\n$n" }
            if (n.bRight is FRBTNode) assert(isBstNode(n.bRight) && isBalanced(n.bRight) && n.bRight.isDepthInvariant()) { "NOT R-SANE\n$n" }
            assert(isBstNode(n) && n.isDepthInvariant()) { "L-Sane and R-Sane, but NOT SANE\n$n" }
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
            FRBTNode(node.entry, !node.color,
                // left
                if (node.bLeft is FRBTNode) FRBTNode(node.bLeft.entry, !node.bLeft.color, node.bLeft.bLeft, node.bLeft.bRight)
                else node.bLeft,
                // right
                if (node.bRight is FRBTNode) FRBTNode(node.bRight.entry, !node.bRight.color, node.bRight.bLeft, node.bRight.bRight)
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
            val newLeft = FRBTNode(node.entry, RED, node.bLeft, node.bRight.bLeft)
            return FRBTNode(node.bRight.entry, node.color, newLeft, node.bRight.bRight)
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
            val newRight = FRBTNode(node.entry, RED, node.bLeft.bRight, node.bRight)
            return FRBTNode(node.bLeft.entry, node.color, node.bLeft.bLeft, newRight)
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
                val newFlipped = FRBTNode(node.entry, flipped.color, flipped.bLeft, newRight)
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
                    val onew = if (omove.bLeft is FRBTNode) FRBTNode(omove.entry, omove.color, deleteMin(omove.bLeft), omove.bRight) else omove
                    onew
                }
            }
            return if (nmin is FRBTNode) lrf23(nmin) else nmin
        }

        private tailrec fun <A, B: Any> mergeAppender(t1: FRBTree<A, B>, t2: FRBTree<A, B>): FRBTree<A, B> where A: Any, A: Comparable<A> = when {
            t1 is FRBTNil -> t2
            t2 is FRBTNil -> t1
            else -> if (t1.size < t2.size) {
                    val (entry, stub) = t1.fpopAndReminder()
                    mergeAppender(t2.finsert(entry!!), stub)
                } else {
                    val (entry, stub) = t2.fpopAndReminder()
                    mergeAppender(t1.finsert(entry!!), stub)
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
        other is Set<*> -> other.isEmpty()
        else -> false
    }
}

internal data class FRBTNode<A, B: Any> (
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

    // short of type erasure, this maintains reflexive, symmetric and transitive properties
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FRBTNode<*, *> -> when {
            other.isEmpty() -> false
            this.color != other.color -> false
            this.entry.getk()::class != other.entry.getk()::class -> false
            this.entry.getv()::class != other.entry.getv()::class -> false
            else -> @Suppress("UNCHECKED_CAST") equal2(this, other as FRBTree<A, B>)
        }
        other is IMBTree<*, *> -> when {
            other.fempty() -> false
            this.froot()?.getk()!!::class != other.froot()?.getk()!!::class -> false
            this.froot()?.getv()!!::class != other.froot()?.getv()!!::class -> false
            else -> @Suppress("UNCHECKED_CAST") equal2(this, other as IMBTree<A, B>)
        }
        other is IMSet<*> -> when {
            other.fempty() -> false
            this.froot()?.getk()!!::class != other.toIMBTree().froot()?.getk()!!::class -> false
            this.froot()?.getv()!!::class != other.fpick()!!::class -> false
            else -> @Suppress("UNCHECKED_CAST") equal2(this, other.toIMBTree() as IMBTree<A, B>)
        }
        other is Set<*> -> when {
            other.isEmpty() -> false
            this.froot()!!::class != other.first()!!::class -> false
            this.froot()!!.getk()::class != (other.first()!! as TKVEntry<*, *>).getk()::class -> false
            this.froot()!!.getv()::class != (other.first()!! as TKVEntry<*, *>).getv()::class -> false
            else -> this.size == other.size && other == this
        }
        else -> false
    }

    val hash:Int by lazy {
        val aux: Long = this.ffold(this.color.hashCode().toLong()) { acc, tkv -> when(val k = tkv.getk()) {
            is Int -> acc + 3L * k.toLong()
            is Long -> acc + 3L * k
            is BigInteger -> acc + 3L * DigestHash.crc32ci(k.toByteArray()).toLong()
            else -> acc + 3L * k.hashCode().toLong()
        }}
        if (Int.MIN_VALUE.toLong() < aux && aux < Int.MAX_VALUE.toLong()) aux.toInt()
        else DigestHash.crc32ci(aux.toBigInteger().toByteArray())
    }

    override fun hashCode(): Int = hash

    companion object {
        fun <A, B: Any> hashCode(n: FRBTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hash
    }
}