package com.xrpn.immutable

import com.xrpn.bridge.FTreeIterator
import com.xrpn.hash.DigestHash.crc32ci
import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.IMBTreeCompanion
import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMSet
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import java.math.BigInteger

// this is NOT as Set as it MAY (legally!) have duplicates.  It's not a List either :)
sealed class FBSTree<out A, out B: Any>: Collection<TKVEntry<A, B>>, IMBTree<A, B> where A: Any, A: Comparable<@UnsafeVariance A> {

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

    override fun isEmpty(): Boolean = this is FBSTNil

    override val size: Int by lazy { if (this.fempty()) 0 else this.ffold(0) { acc, _ -> acc+1 } }

    override fun contains(element: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = when(this) {
        is FBSTNil -> false
        is FBSTNode<A, B> -> this.fcontains(element)
    }

    override fun containsAll(elements: Collection<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): Boolean {
        elements.forEach { if (!this.fcontains(it)) return false }
        return true
    }

    override fun iterator(): Iterator<TKVEntry<A, B>> = FTreeIterator(this)

    /*
        A Binary search tree allows duplicates, but may become pathologically unbalanced.  In the latter case,
        a recursive implementation is not stack safe.  This implementation optionally allows duplicates and
        is fully stack safe.  Search may become ~O(n) when the tree is very unbalanced.  Insertion and deletion
        are not as expensive as for a Balanced Binary search tree.  This is an immutable stack-safe implementation
        of a Binary Search Tree.
     */

    // =========== utility

    override fun equal(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): Boolean = when (this) {
        is FBSTNil -> rhs.fempty()
        is FBSTNode<A, B> -> when {
            this === rhs -> true
            rhs.fempty() -> false
            this.fsize() == rhs.fsize() -> equal2(this, rhs)
            else -> false
        }
    }

    override fun toIMSet(): FSet<B> = FSet.of(this)

    override fun copy(): FBSTree<A, B> = this.ffold(nul()) { acc, tkv -> acc.finsert(tkv) }

    // =========== traversable

    override fun preorder(reverse: Boolean): FList<TKVEntry<A, B>> {

        fun accrue(stack: FStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.pop()
            val newAcc: FList<TKVEntry<A, B>> = visit(node, acc)
            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
            val newStack = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
            return Pair(newAcc, newStack)
        }

        return if (this.fempty()) FLNil else when(reverse) {
            false -> unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FLNil, ::accrue).freverse()
            true -> unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FLNil, ::accrue)
        }
    }

    override fun inorder(reverse: Boolean): FList<TKVEntry<A, B>> {

        tailrec fun inoLeftDescent(t: FBSTree<A, B>, stack: FStack<FBSTNode<A, B>>): FStack<FBSTNode<A, B>> =
            when (t) {
                is FBSTNil -> stack
                is FBSTNode -> inoLeftDescent(t.bLeft, FStack.push(stack, t))
            }

        tailrec fun inoChangeDirection(stack: FStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FStack<FBSTNode<A, B>>> =
            when (stack.isEmpty()) {
                true -> Pair(acc, stack)
                false -> {
                    val (node, shortStack) = stack.pop()
                    val newAcc = visit(node, acc)
                    val remaining = inoLeftDescent(node.bRight, shortStack)
                    inoChangeDirection(remaining, newAcc)
                }
            }

        tailrec fun inoAccrue(stack: FStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FStack<FBSTNode<A, B>>> {
            if (stack.isEmpty()) return Pair(acc, stack)
            val (current, shortStack) = stack.pop()
            val accruedAtLeft = inoLeftDescent(current, shortStack)
            return when (accruedAtLeft.isEmpty()) {
                true -> Pair(acc, accruedAtLeft)
                false -> {
                    val (newAcc, toDo) = inoChangeDirection(accruedAtLeft, acc)
                    inoAccrue(toDo, newAcc)
                }
            }
        }

        return if (this.fempty()) FLNil else when(reverse) {
            false -> unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FLNil, ::inoAccrue).freverse()
            true -> unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FLNil, ::inoAccrue)
        }

    }

    override fun postorder (reverse: Boolean): FList<TKVEntry<A, B>> {

        fun accrue(stack: FStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.pop()
            val auxStack = if (node.bLeft is FBSTNode) FStack.push(shortStack, node.bLeft) else shortStack
            val newStack = if (node.bRight is FBSTNode) FStack.push(auxStack, node.bRight) else auxStack
            return Pair(visit(node,acc), newStack)
        }

        return if (this.fempty()) FLNil else when(reverse) {
            false -> unwindStack(FStack.push(FStack.emptyFStack(), this as FBSTNode), FLNil, ::accrue)
            true -> unwindStack(FStack.push(FStack.emptyFStack(), this as FBSTNode), FLNil, ::accrue).freverse()
        }

    }

    override fun breadthFirst(reverse: Boolean): FList<TKVEntry<A, B>> {

        fun accrue(q: FQueue<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FQueue<FBSTNode<A, B>>> {
            val (node, dequeued) = q.dequeue()
            val q1 = if (node.bLeft is FBSTNil) dequeued else FQueue.enqueue(dequeued, node.bLeft as FBSTNode)
            val q2 = if (node.bRight is FBSTNil) q1 else FQueue.enqueue(q1, node.bRight as FBSTNode)
            return Pair(FLCons(node.entry, acc), q2)
        }

        return if (this.fempty()) FLNil else when(reverse) {
            true -> unwindQueue(FQueue.enqueue(FQueue.emptyFQueue(), this as FBSTNode), FLNil, ::accrue)
            false -> unwindQueue(FQueue.enqueue(FQueue.emptyFQueue(), this as FBSTNode), FLNil, ::accrue).freverse()
        }
    }

    // =========== filtering

    override fun fcontains(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = bstFind(this, item) != null
    override fun fcontainsKey(key: @UnsafeVariance A): Boolean = bstFindKey(this, key) != null
    override fun fdropAll(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FBSTree<A, B> = bstDeletes(this, items as FList<TKVEntry<A,B>>)
    override fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> = bstDelete(this, item, atMostOne = true)
    override fun fdropItemAll(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> = bstDelete(this, item, atMostOne = false)

    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> {

        val f4ffilter: (acc: FBSTree<A, B>, item: TKVEntry<A, B>) -> FBSTree<A, B> =
            { acc, item -> if (isMatch(item)) bstInsert(acc, item, allowDups = false) else acc }

        return ffold(nul(), f4ffilter)
    }

    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> = ffilter { !isMatch(it) }

    override fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): FList<TKVEntry<A, B>> {

        val f4ffind: (FList<TKVEntry<A, B>>, TKVEntry<A, B>) -> FList<TKVEntry<A, B>> =
            { acc: FList<TKVEntry<A, B>>, item: TKVEntry<A, B> -> if (isMatch(item)) FLCons(item, acc) else acc }

        return ffold(FLNil, f4ffind)
    }

    override fun ffindItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B>? = bstFind(this, item)
    override fun ffindKey(key: @UnsafeVariance A): FBSTree<A, B>? = bstFindKey(this, key)
    override fun ffindLastItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B>? = bstFindLast(this, item)
    override fun ffindLastKey(key: @UnsafeVariance A): FBSTree<A, B>? = bstFindLastKey(this, key)
    override fun ffindValueOfKey(key: @UnsafeVariance A): B? = bstFindValueOFKey(this, key)

    override fun fleftMost(): TKVEntry<A, B>? {
        tailrec fun leftDescent(bt: FBSTree<A, B>): TKVEntry<A, B>? =
            when(bt) {
                is FBSTNil -> null
                is FBSTNode -> when (bt.bLeft) {
                    is FBSTNil -> bt.entry
                    is FBSTNode -> leftDescent(bt.bLeft)
                }
            }

        return leftDescent(this)
    }

    override fun fparentOf(child: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> = bstParent(this, child)

    override fun fpick(): TKVEntry<A,B>? = this.fleftMost() ?: this.froot()

    override fun frightMost(): TKVEntry<A, B>? {
        tailrec fun rightDescent(bt: FBSTree<A, B>): TKVEntry<A, B>? =
            when(bt) {
                is FBSTNil -> null
                is FBSTNode -> when (bt.bRight) {
                    is FBSTNil -> bt.entry
                    is FBSTNode -> rightDescent(bt.bRight)
                }
            }

        return rightDescent(this)
    }

    override fun froot(): TKVEntry<A,B>? = when(this) {
        is FBSTNil -> null
        is FBSTNode -> this.entry
    }

    // ================

    override fun fsize(): Int = size

    override fun fcount(isMatch: (TKVEntry<A, B>) -> Boolean): Int = ffold(0) { acc, item -> if(isMatch(item)) acc + 1 else acc }
    override fun <C: Any> fgroupBy(f: (TKVEntry<A, B>) -> C): Map<C, FBSTree<A, B>> = TODO() //	A map of collections created by the function f
    override fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair</* true */ FBSTree<A, B>, /* false */ FBSTree<A, B>> {

        fun f4fpartition(acc: Pair<FBSTree<A, B>, FBSTree<A, B>>, current: (TKVEntry<A, B>)): Pair<FBSTree<A, B>, FBSTree<A, B>> =
            if (isMatch(current)) Pair(bstInsert(acc.first, current), acc.second)
            else Pair(acc.first, bstInsert(acc.second, current))

        return ffold(Pair(nul(), nul()), ::f4fpartition)
    }

    override fun fpopAndReminder(): Pair<TKVEntry<A,B>?, FBSTree<A, B>> {
        val pop: TKVEntry<A,B>? = this.fpick()
        val reminder: FBSTree<A, B> = pop?.let { bstDelete(this, it) } ?: FBSTNil
        return Pair(pop, reminder)
    }


    // returns the maximum path length from the root of a tree to any node.
    override fun fmaxDepth(): Int {

        fun accrue(q: FQueue<FBSTNode<A, B>>, depth: Int): Pair<Int, FQueue<FBSTNode<A, B>>> {

            tailrec fun harvestThisLevel(q: FQueue<FBSTNode<A, B>>, r: FQueue<FBSTNode<A, B>>): FQueue<FBSTNode<A, B>> {
                val (node, shortQueue) = q.dequeue()
                // add non-nul children of node to queue
                val q1 = if (node.bLeft is FBSTNil) r else FQueue.enqueue(r, node.bLeft as FBSTNode)
                val q2 = if (node.bRight is FBSTNil) q1 else FQueue.enqueue(q1, node.bRight as FBSTNode)
                return if (shortQueue.isEmpty()) /* we are done with this level */ q2
                else /* more nodes on this level, keep harvesting */ harvestThisLevel(shortQueue, q2)
            }

            val newDepth = depth + 1
            val newQueue = harvestThisLevel(q, FQueue.emptyFQueue())
            return Pair(newDepth, newQueue)
        }

        return if (this.fempty()) 0
        else unwindQueue(FQueue.enqueue(FQueue.emptyFQueue(), this as FBSTNode), 0, ::accrue)
    }

    // returns the minimum path length from the root of a tree to the first node that is a leaf.
    override fun fminDepth(): Int {

        fun accrue(q: FQueue<FBSTNode<A, B>>, depth: Int): Pair<Int, FQueue<FBSTNode<A, B>>> {

            tailrec fun harvestThisLevel(q: FQueue<FBSTNode<A, B>>, r: FQueue<FBSTNode<A, B>>): FQueue<FBSTNode<A, B>> {
                val (node, shortQueue) = q.dequeue()
                return if (node.isLeaf()) /* early termination at this level */ FQueue.emptyFQueue() else {
                    // add non-nul children of node to queue
                    val q1 = if (node.bLeft is FBSTNil) r else FQueue.enqueue(r, node.bLeft as FBSTNode)
                    val q2 = if (node.bRight is FBSTNil) q1 else FQueue.enqueue(q1, node.bRight as FBSTNode)
                    if (shortQueue.isEmpty()) /* we are done with this level */ q2
                    else /* more nodes on this level, keep harvesting */ harvestThisLevel(shortQueue, q2)
                }
            }

            val newDepth = depth + 1
            val newQueue = harvestThisLevel(q, FQueue.emptyFQueue())
            return Pair(newDepth, newQueue)
        }

        return if (this.fempty()) 0
        else unwindQueue(FQueue.enqueue(FQueue.emptyFQueue(), this as FBSTNode), 0, ::accrue)
    }

    // =========== transforming

    override fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): FBSTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> = TODO()  // 	When working with sequences, it works like map followed by flatten

    override fun <C> ffold(z: C, f: (acc: C, TKVEntry<A, B>) -> C): C {

        // this is a modified preorder
        fun accrueForFold(stack: FStack<FBSTNode<A, B>>, acc: C): Pair<C, FStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.pop()
            val newAcc: C = visitForFold(node, acc, f)
            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
            val newStack = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
            return Pair(newAcc, newStack)
        }

        return if (this.fempty()) z
        else  unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), z, ::accrueForFold)
    }

    override fun <C, D: Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(nul()) { acc, tkv -> acc.finsert(f(tkv)) }

    override fun freduce(f: (acc: TKVEntry<A, B>, TKVEntry<A, B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): TKVEntry<A, B>? = when(this) {  // 	“Reduce” the elements of the list using the binary operator o, going from left to right
        is FBSTNil -> null
        is FBSTNode -> {
            val (seedTkv, stub) = this.fpopAndReminder()
            stub.ffold(seedTkv!!){ acc, tkv -> f(acc, tkv) }
        }
    }

    // =========== altering

    override fun finsert(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> = bstInsert(this, item, allowDups = false)
    override fun finsertDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>, allowDups: Boolean): FBSTree<A, B> = bstInsert(this, item, allowDups)
    override fun finserts(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FBSTree<A, B> = bstInserts(this, items as FList<TKVEntry<A, B>>, allowDups = false)
    override fun finsertsDups(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>, allowDups: Boolean): FBSTree<A, B> = bstInserts(this, items as FList<TKVEntry<A, B>>, allowDups)

    // =========== internals

    private fun visit(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> FLCons(t.entry, acc)
    }

    private fun visitForEach(t: FBSTree<A, B>, f: (TKVEntry<A, B>) -> Unit): Unit = when(t) {
        is FBSTNil -> Unit
        is FBSTNode -> f(t.entry)
    }

    private fun <C> visitForFold(t: FBSTree<A, B>, acc: C, f: (acc: C, TKVEntry<A, B>) -> C): C = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> f(acc, t.entry)
    }

    private tailrec fun unwindStackForEach(stack: FStack<FBSTNode<A, B>>, accrue: (FStack<FBSTNode<A, B>>) -> FStack<FBSTNode<A, B>>): Unit =
        if (stack.isEmpty()) Unit else {
            val newStack = accrue(stack)
            unwindStackForEach(newStack, accrue)
        }

    private tailrec fun <C> unwindStack(stack: FStack<FBSTNode<A, B>>,
                                        acc: C,
                                        accrue: (FStack<FBSTNode<A, B>>, C) -> Pair<C, FStack<FBSTNode<A, B>>>): C =
        if (stack.isEmpty()) acc else {
            val (newAcc, newStack) = accrue(stack, acc)
            unwindStack(newStack, newAcc, accrue)
        }

    private tailrec fun <C> unwindQueue(queue: FQueue<FBSTNode<A, B>>,
                                        acc: C,
                                        accrue: (FQueue<FBSTNode<A, B>>, C) -> Pair<C, FQueue<FBSTNode<A, B>>>): C =
        if (queue.isEmpty()) acc
        else {
            val (newAcc, newQueue) = accrue(queue, acc)
            unwindQueue(newQueue, newAcc, accrue)
        }

//    fun <A, B> fold(ta: FBTree<A, B>, l: (A) -> B, b: (B, B) -> B): B = TODO()

    fun <C: Any> mapi(f: (B) -> C): FBSTree<Int, C> =
        this.preorder(reverse = true).ffoldLeft(nul()) { t: FBSTree<Int, C>, e: TKVEntry<A, B> ->
            mapAppender<A, B, Int, C>(TKVEntry.ofIntKey(f(e.getv())))(t, e)
        }

//    fun <C: Any> maps(f: (B) -> C): FBSTree<String, C> =
//        this.preorder(reverse = true).ffoldLeft(nul()) { t: FBSTree<String, C>, e: TKVEntry<A, B> ->
//            mapAppender<A, B, String, C>(TKVEntry.ofStrKey(f(e.getv())))(t, e)
//        }

    fun <C: Any> mapiDup(allowDups: Boolean = true): ((B) -> (C)) -> (FBSTree<Int, C>) = { f ->
        this.preorder(reverse = true).ffoldLeft(nul()) { t: FBSTree<Int, C>, e: TKVEntry<A, B> ->
            mapAppender<A, B, Int, C>(TKVEntry.ofIntKey(f(e.getv())), allowDups)(t, e)
        }}

    fun <C: Any> mapsDup(allowDups: Boolean = true): ((B) -> (C)) -> (FBSTree<String, C>) = { f ->
        this.preorder(reverse = true).ffoldLeft(nul()) { t: FBSTree<String, C>, e: TKVEntry<A, B> ->
            mapAppender<A, B, String, C>(TKVEntry.ofStrKey(f(e.getv())), allowDups)(t, e)
        }}

    companion object: IMBTreeCompanion {

        const val NOT_FOUND = -1

        fun <A, B: Any> nul(): FBSTree<A, B> where A: Any, A: Comparable<A> = FBSTNil

        override fun <A, B : Any> emptyIMBTree(): IMBTree<A, B> where A: Any,  A : Comparable<A> = nul()

        // ===============

        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator(), false)
        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator(), allowDups)
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items, false)
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul()
            for(item in items) {
                res = bstInsert(res, item, allowDups)
            }
            return res
        }
        override fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), appender(false))
        override fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), appender(allowDups))

        // =================

        override fun <B : Any> ofvi(vararg items: B): FBSTree<Int, B> = ofvi(items.iterator(), false)
        override fun <B : Any> ofvi(vararg items: B, allowDups: Boolean): FBSTree<Int, B> = ofvi(items.iterator(), allowDups)
        override fun <B : Any> ofvi(items: Iterator<B>): FBSTree<Int, B> = ofvi(items, false)
        override fun <B : Any> ofvi(items: Iterator<B>, allowDups: Boolean): FBSTree<Int, B> {
            var res: FBSTree<Int, B> = nul()
            for(item in items) {
                res = bstInsert(res, TKVEntry.ofIntKey(item), allowDups)
            }
            return res
        }
        override fun <B: Any> ofvi(items: IMList<B>): FBSTree<Int, B> = items.ffoldLeft(nul(), appenderIntKey(false))
        override fun <B: Any> ofvi(items: IMList<B>, allowDups: Boolean): FBSTree<Int, B> = items.ffoldLeft(nul(), appenderIntKey(allowDups))

        // ===============

        override fun <B : Any> ofvs(vararg items: B): FBSTree<String, B> = ofvs(items.iterator(), false)
        override fun <B : Any> ofvs(vararg items: B, allowDups: Boolean): FBSTree<String, B> = ofvs(items.iterator(), allowDups)
        override fun <B: Any> ofvs(items: IMList<B>): FBSTree<String, B> =
            items.ffoldLeft(nul(), appenderStrKey(false))
        override fun <B: Any> ofvs(items: IMList<B>, allowDups: Boolean): FBSTree<String, B> =
            items.ffoldLeft(nul(), appenderStrKey(allowDups))
        override fun <B : Any> ofvs(items: Iterator<B>): FBSTree<String, B> = ofvs(items, false)
        override fun <B : Any> ofvs(items: Iterator<B>, allowDups: Boolean): FBSTree<String, B>  {
            var res: FBSTree<String, B> = nul()
            for(item in items) {
                res = bstInsert(res, TKVEntry.ofStrKey(item), allowDups)
            }
            return res
        }

        // ===============

        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> = ofMap(items, false, f)
        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> {
            var res: FBSTree<C, D> = nul()
            for(item in items) {
                res = bstInsert(res, f(item), allowDups)
            }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, f: (B) -> C): FBSTree<Int, C> = ofviMap(items, false, f)
        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<Int, C> {
            var res: FBSTree<Int, C> = nul()
            for(item in items) {
                res = bstInsert(res, TKVEntry.ofIntKey(f(item)), allowDups)
            }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, f: (B) -> C): FBSTree<String, C> = ofvsMap(items, false, f)
        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<String, C> {
            var res: FBSTree<String, C> = nul()
            for(item in items) {
                res = bstInsert(res, TKVEntry.ofStrKey(f(item)), allowDups)
            }
            return res
        }

        // =================

        override fun <A, B : Any> Collection<TKVEntry<A, B>>.toIMBTree(): FBSTree<A, B> where A: Any, A : Comparable<A> {
            val (ak, bk) = if (this.isEmpty()) return nul() else Pair(this.first().getk()::class, this.first().getv()::class)
            when(this) {
                is FBSTree<*, *> -> this as FBSTree<A, B>
                is FRBTree<*, *> -> @Suppress("UNCHECKED_CAST") of(this.postorder() as IMList<TKVEntry<A, B>>)
                is IMSet<*> -> (this.toIMBTree() as IMBTree<A, B>).ffold(nul()) { acc, tkv -> acc.finsert(tkv) }
                is List<*>, is Set<*> -> {
                    var res = nul<A, B>()
                    if (this.f)
                    for(item in this) { res = res.finsertDup(item, allowDups = true) }
                    res
                }
                else -> /* TODO this would be interesting */ throw RuntimeException(this::class.simpleName)
            }
        }

        override fun <A, B: Any> Map<A, B>.toIMBTree(): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul()
            for (entry in this) { res = res.finsertDup(TKVEntry.of(entry), allowDups = true) }
            return res
        }

        // =============== top level type-specific implementation

        // delete entry from treeStub.
        internal fun <A, B: Any> bstDelete(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, atMostOne: Boolean = false): FBSTree<A, B>
        where A: Any, A: Comparable<A> {

            // TODO this may be worth memoizing

            fun buildReplacement(
                eureka: FBSTNode<A, B>,
                current: FBSTree<A, B>
            ): Pair<FBSTree<A, B>, FBSTNode<A, B>> {
                val newValue = /* find replacement value */ eureka.bRight.fleftMost()!!
                val graftRight =  /* build new right subtree */
                    if ((eureka.bRight as FBSTNode).entry == newValue) eureka.bRight.bRight
                    else bstPrune(eureka.bRight, newValue)
                val stub = /* remove all A */ bstPrune(treeStub, eureka.entry)
                val graftLeft = /* there may be duplicates */ (bstFind(current, item) as FBSTNode).bLeft
                val replacement = /* build replacement */
                    FBSTNode(newValue, graftLeft, graftRight)
                return Pair(stub, replacement)
            }

            tailrec fun splice(item: TKVEntry<A, B>, current: FBSTree<A, B>, trace: FList<Trace<A, B>>): FBSTree<A, B> =
                when (current) {
                    is FBSTNil -> trace.ffoldLeft(FBSTNode(item), ::enrichNode)
                    is FBSTNode -> {
                        when (val last = bstFindLast(current, item)) {
                            is FBSTNode -> {
                                val first = bstFind(current, item) as FBSTNode
                                val noDuplicates = last === first || atMostOne
                                val found = if (atMostOne) first else last
                                when (Pair(found.bLeft is FBSTNil, found.bRight is FBSTNil)) {
                                    Pair(true, true) -> if (noDuplicates) /* just remove */ bstPrune(treeStub, item) else {
                                        when(fitKey(item.getk(), first)) {
                                            FBTFIT.LEFT -> when (first.bRight) {
                                                is FBSTNil -> bstPrune(treeStub, item)
                                                is FBSTNode -> addGraft(bstPrune(treeStub, item), first.bRight)
                                            }
                                            FBTFIT.RIGHT, FBTFIT.EQ -> when (first.bLeft) {
                                                is FBSTNil -> bstPrune(treeStub, item)
                                                is FBSTNode -> addGraft(bstPrune(treeStub, item), first.bLeft)
                                            }
                                        }
                                    }
                                    Pair(true, false) -> /* replace with right child */
                                        if (noDuplicates) addGraft(bstPrune(treeStub, item),found.bRight)
                                        else when(found.bRight) {
                                            is FBSTNil -> FBSTNil
                                            is FBSTNode -> {
                                                // TODO this is suboptimal, two-child replacement may not be necessary
                                                val (stub, replacement) = buildReplacement(found, current)
                                                addGraft(stub, replacement)
                                            }
                                        }
                                    Pair(false, true) -> /* replace with left child */
                                        if (noDuplicates) addGraft(bstPrune(treeStub, item),found.bLeft)
                                        else when (found.bLeft) {
                                            is FBSTNil -> FBSTNil
                                            is FBSTNode -> {
                                                // TODO this is suboptimal, two-child replacement may not be necessary
                                                val (stub, replacement) = buildReplacement(found, current)
                                                addGraft(stub, replacement)
                                            }
                                        }
                                    Pair(false, false) -> {
                                        val (stub, replacement) = buildReplacement(found, current)
                                        addGraft(stub, replacement)
                                    }
                                    else -> throw RuntimeException("impossible path")
                                }
                            }
                            else -> {
                                val next: FBSTree<A, B> = when {
                                    item.getk() < current.entry.getk() -> current.bLeft
                                    current.entry.getk() < item.getk() -> current.bRight
                                    else -> throw RuntimeException("impossible path")
                                }
                                splice(item, next, trace)
                            }
                        }
                    }
                }

            return if (!treeStub.fcontains(item)) treeStub
                else splice(item, treeStub, FLNil)
        }

        internal tailrec fun <A, B: Any> bstDeletes(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A,B>>): FBSTree<A, B>
        where A: Any, A: Comparable<A> = when (items) {
                is FLNil -> treeStub
                is FLCons -> bstDeletes(bstDelete(treeStub, items.head), items.tail)
            }

        // find the first tree stub matching item; the returned stub may have other nodes that match item
        internal fun <A, B: Any> bstFind(treeStub: FBSTree<A, B>, item: TKVEntry<A,B>): FBSTNode<A, B>?
        where A: Any, A: Comparable<A> = find(treeStub, item, ::fit)

        // find the first tree stub matching key; the returned stub may have other nodes that match key
        internal fun <A, B: Any> bstFindKey(treeStub: FBSTree<A, B>, key: A): FBSTNode<A, B>?
        where A: Any, A: Comparable<A> = find(treeStub, key, ::fitKey)

        internal fun <A, B: Any> bstFindValueOFKey(treeStub: FBSTree<A, B>, key: A): B?
        where A: Any, A: Comparable<A> = when(val found = find(treeStub, key, ::fitKey)) {
            is FBSTNode -> found.entry.getv()
            else -> null
        }

        // find the last tree stub matching value; the returned stub will not have other nodes that match value
        internal fun <A, B: Any> bstFindLast(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): FBSTNode<A, B>?
        where A: Any, A: Comparable<A> = findLast(treeStub, item, ::fit)

        // find the last tree stub matching value; the returned stub will not have other nodes that match value
        internal fun <A, B: Any> bstFindLastKey(treeStub: FBSTree<A, B>, key: A): FBSTNode<A, B>?
        where A: Any, A: Comparable<A> = findLast(treeStub, key, ::fitKey)

        // insert item into treeStub at the correct position
        internal fun <A, B: Any> bstInsert(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, allowDups: Boolean = false): FBSTNode<A, B>
        where A: Any, A: Comparable<A> {

            fun emplace(node: FBSTNode<A, B>, tree: Trace<A, B>): FBSTNode<A, B> = when {
                allowDups -> enrichNode(node, tree)
                (node.entry == tree.treeStub.entry) || tree.duplicate -> tree.treeStub
                else -> enrichNode(node, tree)
            }

            return when (treeStub) {
                is FBSTNil -> FBSTNode(item)
                is FBSTNode -> reassemble(FBSTNode(item), treeStub, FLNil, ::emplace)
            }
        }

        // insert items into treeStub at the correct position
        internal tailrec fun <A, B: Any> bstInserts(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A, B>>, allowDups: Boolean = false): FBSTree<A, B>
        where A: Any, A: Comparable<A> = when (items) {
                is FLNil -> treeStub
                is FLCons -> bstInserts(bstInsert(treeStub, items.head, allowDups), items.tail, allowDups)
            }

        // the immediate parent (and all that parent's descendants) of the child having childValue
        internal fun <A, B: Any> bstParent(treeStub: FBSTree<A, B>, childItem: TKVEntry<A, B>): FBSTree<A, B>
        where A: Any, A: Comparable<A> {

            tailrec fun go(
                treeStub: FBSTree<A, B>,
                child: TKVEntry<A, B>,
                items: Pair<FBSTree<A, B>, FBSTree<A, B>>): Pair<FBSTree<A, B>, FBSTree<A, B>> = when (treeStub) {
                is FBSTNil -> items
                is FBSTNode -> {
                    val next: Pair<FBSTree<A, B>, FBSTree<A, B>>? = when {
                        child.getk() < treeStub.entry.getk() -> Pair(treeStub, treeStub.bLeft)
                        treeStub.entry.getk() < child.getk() -> Pair(treeStub, treeStub.bRight)
                        else -> null
                    }
                    if (next == null) items else go(next.second, child, next)
                }
            }

            return if (treeStub.fcontains(childItem)) go(treeStub, childItem, Pair(FBSTNil, FBSTNil)).first else FBSTNil

        }

        // clip off the whole branch starting at, and inclusive of, a node with value clipMatch
        internal fun <A, B: Any> bstPrune(treeStub: FBSTree<A, B>, clipMatch: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

            tailrec fun copy(stack: FStack<FBSTNode<A, B>>, acc: FBSTree<A, B>): Pair<FBSTree<A, B>, FStack<FBSTNode<A, B>>> {
                return if (stack.isEmpty()) Pair(acc, stack) else {
                    val (node, shortStack) = stack.pop()
                    val (newStack, newAcc) = when (isChildMatch(node, clipMatch, ::fit)) {
                        Pair(false, false) -> {
                            val na = bstInsert(acc, node.entry, allowDups = true)
                            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
                            val ns = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
                            Pair(ns, na)
                        }
                        Pair(true,  false) -> {
                            val na = bstInsert(acc, node.entry, allowDups = true)
                            val ns = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
                            Pair(ns, na)
                        }
                        Pair(false,  true) -> {
                            val na = bstInsert(acc, node.entry, allowDups = true)
                            val ns = if (node.bLeft is FBSTNode) FStack.push(shortStack, node.bLeft) else shortStack
                            Pair(ns, na)
                        }
                        else -> throw RuntimeException("impossible code path")
                    }
                    copy(newStack, newAcc)
                }
            }

            return when {
                ! treeStub.fcontains(clipMatch) -> /* nothing to prune */ treeStub
                treeStub.fempty() -> /* nothing to match */ FBSTNil
                else -> when(bstParent(treeStub, clipMatch)) {
                    is FBSTNil -> /* root; not found was handled above */ FBSTNil
                    is FBSTNode -> copy(FStack.push(FStack.emptyFStack(), (treeStub as FBSTNode)), FBSTNil).first
                }
            }

        }

        internal inline fun <reified A, reified B: Any> toArray(fbst: FBSTree<A, B>): Array<TKVEntry<A, B>> where A: Any, A: Comparable<A> =
            FTreeIterator.toArray(fbst.size, FTreeIterator(fbst))

        // =============== internals

        // exposed for testing purposes ONLY
        internal fun <A, B: Any> addGraftTestingGremlin(treeStub: FBSTree<A, B>, graft: FBSTree<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            addGraft(treeStub, graft)

        private data class Trace<A, B: Any>(val treeStub: FBSTNode<A, B>, val direction: FBTFIT, val duplicate: Boolean = false) where A: Any, A: Comparable<A>

        // the sorting order
        private fun <A, B: Any> fit(a: TKVEntry<A, B>, b: FBSTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = when {
            a.getk() < b.entry.getk() -> FBTFIT.LEFT
            a.getk() == b.entry.getk() -> FBTFIT.EQ
            else -> FBTFIT.RIGHT
        }

        // the sorting order
        private fun <A, B: Any> fitKey(a: A, b: FBSTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = when {
            a < b.entry.getk() -> FBTFIT.LEFT
            a == b.entry.getk()-> FBTFIT.EQ
            else -> FBTFIT.RIGHT
        }

        private fun <A, B: Any, C: Any> isChildMatch(node: FBSTNode<A, B>, item: C, fitMode: (C, FBSTNode<A, B>) -> FBTFIT): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
            val leftChildMatch = (node.bLeft is FBSTNode) && fitMode(item, node.bLeft) == FBTFIT.EQ
            val rightChildMatch = (node.bRight is FBSTNode) && fitMode(item, node.bRight) == FBTFIT.EQ
            return Pair(leftChildMatch, rightChildMatch)
        }

        // assert the BST property on this node
        internal fun <A, B: Any> fbtAssert(n: FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> {
            if (n.bLeft is FBSTNode) check(FBTFIT.RIGHT == fitKey(n.entry.getk(), n.bLeft) || FBTFIT.EQ == fitKey(n.entry.getk(), n.bLeft))
            if (n.bRight is FBSTNode) check(FBTFIT.LEFT == fitKey(n.entry.getk(), n.bRight))
            return n
        }

        private tailrec fun <A, B: Any, C: Any> find(treeStub: FBSTree<A, B>, item: C, fitMode: (C, FBSTNode<A, B>) -> FBTFIT?): FBSTNode<A, B>?
        where A: Any, A: Comparable<A> = when (treeStub) {
            is FBSTNil -> null
            is FBSTNode -> {
                val next: FBSTree<A, B>? = when(fitMode(item, treeStub)) {
                    FBTFIT.LEFT -> treeStub.bLeft
                    FBTFIT.RIGHT -> treeStub.bRight
                    else -> null
                }
                if (next == null) treeStub else find(next, item, fitMode)
            }
        }

        private tailrec fun <A, B: Any, C: Any> findLast(treeStub: FBSTree<A, B>, item: C, fitMode: (C, FBSTNode<A, B>) -> FBTFIT): FBSTNode<A, B>?
                where A: Any, A: Comparable<A> {

            fun checkOneDeeper(node: FBSTNode<A, B>): FBSTNode<A, B>? = when (isChildMatch(node, item, fitMode)) {
                Pair(false, false) -> null
                Pair(true, false) -> node.bLeft as FBSTNode<A, B>
                Pair(false, true) -> node.bRight as FBSTNode<A, B>
                else -> throw IllegalStateException("broken BST - left and right values may not be equal")
            }

            return when (treeStub) {
                is FBSTNil -> null
                is FBSTNode -> {
                    val next: FBSTree<A, B>? = when (fitMode(item, treeStub)) {
                        FBTFIT.LEFT -> treeStub.bLeft
                        FBTFIT.RIGHT -> treeStub.bRight
                        FBTFIT.EQ -> checkOneDeeper(treeStub)
                    }
                    // unfortunately this is not tail-recursive
                    // next?.let { find(next, value) } ?: treeStub
                    if (next == null) treeStub else findLast(next, item, fitMode)
                }
            }
        }

        private fun <A, B: Any> enrichNode(node: FBSTNode<A, B>, tree: Trace<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> =
            if (tree.duplicate) when (tree.direction) {
                FBTFIT.RIGHT, FBTFIT.EQ -> FBSTNode(
                    tree.treeStub.entry,
                    tree.treeStub.bLeft,
                    FBSTNode(tree.treeStub.entry, FBSTNil, tree.treeStub.bRight)
                )
                FBTFIT.LEFT -> FBSTNode(
                    tree.treeStub.entry,
                    FBSTNode(tree.treeStub.entry, tree.treeStub.bLeft, FBSTNil),
                    tree.treeStub.bRight,
                )
            } else when (tree.direction) {
                FBTFIT.LEFT -> FBSTNode(tree.treeStub.entry, node, tree.treeStub.bRight)
                FBTFIT.RIGHT, FBTFIT.EQ -> FBSTNode(tree.treeStub.entry, tree.treeStub.bLeft, node)
            }

        private tailrec fun <A, B: Any> reassemble(
            item: FBSTNode<A, B>,
            cur: FBSTree<A, B>,
            trace: FList<Trace<A, B>>,
            op: (FBSTNode<A, B>, Trace<A, B>) -> FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> =
            when (cur) {
                is FBSTNil -> trace.ffoldLeft(item, op)
                is FBSTNode -> {
                    val pos = fitKey(item.entry.getk(), cur)
                    val dup = cur.entry == item.entry
                    when (val brn = cur.branch(pos)) {
                        is FBSTNil -> FLCons(Trace(cur, pos, dup), trace).ffoldLeft(item, op)
                        is FBSTNode -> reassemble(item, brn, FLCons(Trace(cur, pos, dup), trace), op)
                    }
                }
            }

        // treeStub is a tree from which a node has been removed; graft is the only child of the removed node
        // NOTE: reinserting graft into treeStub MUST ALWAYS maintain the BST property (see ::fit for details)
        // there is a tight relationship between treeStub and graft -- this is not a general purpose utility
        private fun <A, B: Any> addGraft(treeStub: FBSTree<A, B>, graft: FBSTree<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            when (Pair(treeStub.fempty(), graft.fempty())) {
                Pair(true, true) -> FBSTNil
                Pair(true, false) -> graft
                Pair(false, true) -> treeStub
                else -> reassemble(graft as FBSTNode, treeStub, FLNil, ::enrichNode)
            }

        private fun <A, B: Any> appender(withDups: Boolean): (FBSTree<A, B>, TKVEntry<A, B>) -> FBSTree<A, B> where A: Any, A: Comparable<A> =
            { treeStub: FBSTree<A, B>, item: TKVEntry<A, B> -> bstInsert(treeStub, item, allowDups = withDups)}

        private fun <B: Any> appenderIntKey(withDups: Boolean): (FBSTree<Int, B>, B) -> FBSTree<Int, B> =
            { treeStub: FBSTree<Int, B>, item: B -> bstInsert(treeStub, item.toIAEntry(), allowDups = withDups)}

        private fun <B: Any> appenderStrKey(withDups: Boolean): (FBSTree<String, B>, B) -> FBSTree<String, B> =
            { treeStub: FBSTree<String, B>, item: B -> bstInsert(treeStub, item.toSAEntry(), allowDups = withDups)}

        private fun <A, B: Any, C: Comparable<C>, D: Any> mapAppender(
                kf: (A) -> (C),
                vf: (B) -> (D),
                withDups: Boolean): (FBSTree<C, D>, TKVEntry<A, B>) -> FBSTree<C, D> where A: Any, A: Comparable<A> =
            { treeStub: FBSTree<C, D>, item: TKVEntry<A, B> -> bstInsert(treeStub, TKVEntryK(kf(item.getk()), vf(item.getv())), allowDups = withDups)}

        private fun <A, B: Any, C, D: Any> mapAppender(
            mappedItem: TKVEntry<C, D>,
            withDups: Boolean = false): (FBSTree<C, D>, TKVEntry<A, B>) -> FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> =
            { treeStub: FBSTree<C, D>, _: TKVEntry<A, B> -> bstInsert(treeStub, mappedItem, allowDups = withDups)}
    }
}

internal object FBSTNil: FBSTree<Nothing, Nothing>() {
    override fun toString(): String = "FBSTNil"
    override fun hashCode(): Int = toString().hashCode()
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMBTree<*, *> -> other.fempty()
        else -> false
    }
}

internal data class FBSTNode<out A, out B: Any> (
    val entry: TKVEntry<A, B>,
    val bLeft: FBSTree<A, B> = FBSTNil,
    val bRight: FBSTree<A, B> = FBSTNil
): FBSTree<A, B>() where A: Any, A: Comparable<@UnsafeVariance A> {

    internal fun branch(position: FBTFIT): FBSTree<A, B> = when (position) {
        FBTFIT.LEFT -> bLeft
        FBTFIT.RIGHT, FBTFIT.EQ -> bRight
    }

    internal fun isLeaf(): Boolean = bLeft is FBSTNil && bRight is FBSTNil

    val show: String by lazy {
        val sz: String = when (val ns = size) {
            0 -> ""
            else -> "{$ns}"
        }
        this.ffold("${FBSTree::class.simpleName}@$sz:") { acc, tkv -> "$acc($tkv)" }
    }

    override fun toString(): String = show

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FBSTNode<*, *> -> when {
            this.isEmpty() && other.isEmpty() -> true
            this.isEmpty() || other.isEmpty() -> false
            this.entry.getv()::class == other.entry.getv()::class &&
                    this.entry.getk()::class == other.entry.getk()::class -> {
                @Suppress("UNCHECKED_CAST") equal2(this, other as FBSTNode<A, B>)
            }
            else -> false
        }
        other is IMBTree<*, *> -> when {
            this.isEmpty() && other.fempty() -> true
            this.isEmpty() || other.fempty() -> false
            this.froot()?.getv()!!::class == other.froot()?.getv()!!::class &&
                    this.froot()?.getk()!!::class == other.froot()?.getk()!!::class ->
                @Suppress("UNCHECKED_CAST") this.equal(other as IMBTree<A, B>)
            else -> false
        }
        else -> false
    }

    val hash:Int by lazy {
        val aux: Long = this.ffold(0L) { acc, tkv -> when(val k = tkv.getk()) {
            is Int -> acc + 3L * k.toLong()
            is Long -> acc + 3L * k
            is BigInteger -> acc + 3L * crc32ci(k.toByteArray()).toLong()
            else -> acc + 3L * k.hashCode().toLong()
        }}
        if (Int.MIN_VALUE.toLong() < aux && aux < Int.MAX_VALUE.toLong()) aux.toInt()
        else crc32ci(aux.toBigInteger().toByteArray())
    }

    override fun hashCode(): Int = hash

    companion object {
        fun <A, B: Any> hashCode(n: FBSTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hashCode()
    }

}