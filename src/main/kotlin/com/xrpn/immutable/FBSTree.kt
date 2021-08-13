package com.xrpn.immutable

import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.IMBTreeCompanion
import com.xrpn.imapi.IMTraversable.Companion.equal
import com.xrpn.imapi.IMList
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry

sealed class FBSTree<out A, out B: Any>: Collection<TKVEntry<A, B>>, IMBTree<A, B> where A: Any, A: Comparable<@UnsafeVariance A> {

    // =========== Collection

    override fun isEmpty(): Boolean = this is FBSTNil

    override val size: Int by lazy {

        fun accrue(stack: FStack<FBSTNode<A, B>>, acc: Int): Pair<Int, FStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.pop()
            val newAcc: Int = acc + 1
            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
            val newStack = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
            return Pair(newAcc, newStack)
        }

        if (this.fempty()) 0 else unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), 0, ::accrue)
    }

    override fun contains(element: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = when(this) {
        is FBSTNil -> false
        is FBSTNode<A, B> -> this.fcontainsItem(element)
//            when (this.froot()!!.getk()) {
//            is Int -> @Suppress("UNCHECKED_CAST") (containsIntKey((this as FBSTree<Int, B>), element))
//            is String -> @Suppress("UNCHECKED_CAST") (containsStrKey((this as FBSTree<String, B>), element))
//            else -> {
//                val itr = iterator() as FListIteratorFwd<B>
//                var found = false
//                while (! found) {
//                    itr.nullableNext()?.let{ found = it == element } ?: break
//                }
//                found
//            }
//        }
    }

    override fun containsAll(elements: Collection<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): Boolean {
        elements.forEach { if (!this.fcontainsItem(it)) return false }
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

    // =========== traversable

    override fun forEach(f: (TKVEntry<A, B>) -> Unit): Unit {
        // this is a modified preorder traverse
        fun accrueForEach(stack: FStack<FBSTNode<A, B>>): FStack<FBSTNode<A, B>> {
            val (node, shortStack) = stack.pop()
            visitForEach(node, f)
            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
            val newStack = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
            return newStack
        }

        if (! this.fempty()) unwindStackForEach(FStack.push(FStack.emptyFStack(),this as FBSTNode), ::accrueForEach)

    }

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

    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> {

        fun accrueForFilter(stack: FStack<FBSTNode<A, B>>, acc: FBSTree<A, B>): Pair<FBSTree<A, B>, FStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.pop()
            val newAcc: FBSTree<A, B> = visitForSelect(node, acc, isMatch)
            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
            val newStack = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
            return Pair(newAcc, newStack)
        }

        return if (this.fempty()) FBSTNil
        else  unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FBSTNil, ::accrueForFilter)
    }

    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> = ffilter { !isMatch(it) }

    override fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): FList<TKVEntry<A, B>> {

        fun accrueForFind(stack: FStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.pop()
            val newAcc: FList<TKVEntry<A, B>> = visitForSelectAll(node, acc, isMatch)
            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
            val newStack = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
            return Pair(newAcc, newStack)
        }

        return if (this.fempty()) FLNil
        else unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FLNil, ::accrueForFind)
    }

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

    override fun fpopAndReminder(): Pair<TKVEntry<A,B>?, FBSTree<A, B>> {
        val pop: TKVEntry<A,B>? = this.fpick()
        val reminder: FBSTree<A, B> = pop?.let { fdelete(this, it) } ?: FBSTNil
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

    private fun visit(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> FLCons(t.entry, acc)
    }

    private fun visitForEach(t: FBSTree<A, B>, f: (TKVEntry<A, B>) -> Unit): Unit = when(t) {
        is FBSTNil -> Unit
        is FBSTNode -> f(t.entry)
    }

    private fun visitForSelect(t: FBSTree<A, B>, acc: FBSTree<A, B>, f: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> if (f(t.entry)) finsert(acc, t.entry, allowDups = false) else acc
    }

    private fun visitForSelectAll(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> Boolean): FList<TKVEntry<A, B>> = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> if (f(t.entry)) FLCons(t.entry, acc) else acc
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

    override fun equals(other: Any?): Boolean = when (this) {
        is FBSTNil -> other is FBSTNil
        is FBSTNode -> when(other) {
            is FBSTNode<*,*> -> other == this
            else -> false
        }
    }

    override fun hashCode(): Int = when (this) {
        is FBSTNil -> FBSTNil.hashCode()
        is FBSTNode -> FBSTNode.hashCode(this)
    }

    companion object: IMBTreeCompanion {

        fun <A, B: Any> nul(): FBSTree<A, B> where A: Any, A: Comparable<A> = FBSTNil

        override fun <A, B : Any> emptyIMBTree(): FBSTree<A, B> where A: Any,  A : Comparable<A> = nul()

        // ===============

        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator(), false)
        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator(), allowDups)
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items, false)
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var count = 0
            var res: FBSTree<A, B> = nul()
            for(item in items) {
                res = finsert(res, item, allowDups)
                println("inserted item $count as $item, res size is ${res.size}")
                count += 1
            }
            println("done inserted, size $count")
            return res
        }
        override fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), appender(false))
        override fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), appender(allowDups))

        // ===============

        override fun <B : Any> ofvi(vararg items: B): FBSTree<Int, B> = ofvi(items.iterator(), false)
        override fun <B : Any> ofvi(vararg items: B, allowDups: Boolean): FBSTree<Int, B> = ofvi(items.iterator(), allowDups)
        override fun <B : Any> ofvi(items: Iterator<B>): FBSTree<Int, B> = ofvi(items, false)
        override fun <B : Any> ofvi(items: Iterator<B>, allowDups: Boolean): FBSTree<Int, B> {
            var res: FBSTree<Int, B> = nul()
            for(item in items) {
                res = finsert(res, TKVEntry.ofIntKey(item), allowDups)
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
                res = finsert(res, TKVEntry.ofStrKey(item), allowDups)
            }
            return res
        }

        // ===============

        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> = ofMap(items, false, f)
        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> {
            var res: FBSTree<C, D> = nul()
            for(item in items) {
                res = finsert(res, f(item), allowDups)
            }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, f: (B) -> C): FBSTree<Int, C> = ofviMap(items, false, f)
        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<Int, C> {
            var res: FBSTree<Int, C> = nul()
            for(item in items) {
                res = finsert(res, TKVEntry.ofIntKey(f(item)), allowDups)
            }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, f: (B) -> C): FBSTree<String, C> = ofvsMap(items, false, f)
        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<String, C> {
            var res: FBSTree<String, C> = nul()
            for(item in items) {
                res = finsert(res, TKVEntry.ofStrKey(f(item)), allowDups)
            }
            return res
        }

        // ===============

        fun <A, B: Any> fcontains2(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): Boolean where A: Any, A: Comparable<A> = ffind(treeStub, item) is FBSTNode

        fun <B: Any> fcontainsIntKey(treeStub: FBSTree<Int, B>, value: B): Boolean = ffind(treeStub, TKVEntry.ofIntKey(value)) is FBSTNode

        fun <B: Any> fcontainsStrKey(treeStub: FBSTree<String, B>, value: B): Boolean = ffind(treeStub, TKVEntry.ofStrKey(value)) is FBSTNode

        fun <A, B: Any> fdelete(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, atMostOne: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> {

            fun buildReplacement(
                eureka: FBSTNode<A, B>,
                current: FBSTree<A, B>
            ): Pair<FBSTree<A, B>, FBSTNode<A, B>> {
                val newValue = /* find replacement value */ eureka.bRight.fleftMost()!!
                val graftRight =  /* build new right subtree */
                    if ((eureka.bRight as FBSTNode).entry == newValue) eureka.bRight.bRight
                    else prune(eureka.bRight, newValue)
                val stub = /* remove all A */ prune(treeStub, eureka.entry)
                val graftLeft = /* there may be duplicates */ (ffind(current, item) as FBSTNode).bLeft
                val replacement = /* build replacement */
                    FBSTNode(newValue, graftLeft, graftRight)
                return Pair(stub, replacement)
            }

            tailrec fun splice(item: TKVEntry<A, B>, current: FBSTree<A, B>, trace: FList<Trace<A, B>>): FBSTree<A, B> =
                when (current) {
                    is FBSTNil -> trace.ffoldLeft(FBSTNode(item), ::enrichNode)
                    is FBSTNode -> {
                        when (val last = ffindLast(current, item)) {
                            is FBSTNode -> {
                                val first = ffind(current, item) as FBSTNode
                                val noDuplicates = last === first || atMostOne
                                val found = if (atMostOne) first else last
                                when (Pair(found.bLeft is FBSTNil, found.bRight is FBSTNil)) {
                                    Pair(true, true) -> if (noDuplicates) /* just remove */ prune(treeStub, item) else {
                                        when(fitKey(item.getk(), first)) {
                                            FIT.LEFT -> when (first.bRight) {
                                                is FBSTNil -> prune(treeStub, item)
                                                is FBSTNode -> addGraft(prune(treeStub, item), first.bRight)
                                            }
                                            FIT.RIGHT -> when (first.bLeft) {
                                                is FBSTNil -> prune(treeStub, item)
                                                is FBSTNode -> addGraft(prune(treeStub, item), first.bLeft)
                                            }
                                        }
                                    }
                                    Pair(true, false) -> /* replace with right child */
                                        if (noDuplicates) addGraft(prune(treeStub, item),found.bRight)
                                        else when(found.bRight) {
                                            is FBSTNil -> FBSTNil
                                            is FBSTNode -> {
                                                // TODO this is suboptimal, two-child replacement may not be necessary
                                                val (stub, replacement) = buildReplacement(found, current)
                                                addGraft(stub, replacement)
                                            }
                                        }
                                    Pair(false, true) -> /* replace with left child */
                                        if (noDuplicates) addGraft(prune(treeStub, item),found.bLeft)
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
                            is FBSTNil -> {
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

            return when (fcontains2(treeStub, item)) {
                false -> treeStub
                true -> splice(item, treeStub, FLNil)
            }
        }

        tailrec fun <A, B: Any> fdeletes(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A,B>>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            when (items) {
                is FLNil -> treeStub
                is FLCons -> fdeletes(fdelete(treeStub, items.head), items.tail)
            }

        // find the first tree stub matching value; the returned stub may have other nodes that match value
        tailrec fun <A, B: Any> ffind(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> = when (treeStub) {
            is FBSTNil -> treeStub
            is FBSTNode -> {
                val next: FBSTree<A, B>? = when {
                    item.getk() < treeStub.entry.getk() -> treeStub.bLeft
                    treeStub.entry.getk() < item.getk() -> treeStub.bRight
                    else -> null
                }
                // unfortunately this is not tail-recursive
                // next?.let { find(next, value) } ?: treeStub
                if (next == null) treeStub else ffind(next, item)
            }
        }

        // find the last tree stub matching value; the returned stub will not have other nodes that match value
        tailrec fun <A, B: Any> ffindLast(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

            fun checkOneDeeper(node: FBSTNode<A, B>): FBSTNode<A, B>? = when (isChildMatch(node, item)) {
                Pair(false, false) -> null
                Pair(true, false) -> node.bLeft as FBSTNode<A, B>
                Pair(false, true) -> node.bRight as FBSTNode<A, B>
                else -> throw IllegalStateException("broken BST - left and right values may not be equal")
            }

            return when (treeStub) {
                is FBSTNil -> treeStub
                is FBSTNode -> {
                    val next: FBSTree<A, B>? = when {
                        item.getk() < treeStub.entry.getk() -> treeStub.bLeft
                        treeStub.entry.getk() < item.getk() -> treeStub.bRight
                        else -> checkOneDeeper(treeStub)
                    }
                    // unfortunately this is not tail-recursive
                    // next?.let { find(next, value) } ?: treeStub
                    if (next == null) treeStub else ffindLast(next, item)
                }
            }
        }

        // insert item into treeStub at the correct position
        fun <A, B: Any> finsert(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> {

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
        tailrec fun <A, B: Any> finserts(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A, B>>, allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> =
            when (items) {
                is FLNil -> treeStub
                is FLCons -> finserts(finsert(treeStub, items.head, allowDups), items.tail, allowDups)
            }


        // the immediate parent (and all that parent's descendants) of the child having childValue
        fun <A, B: Any> fparent(treeStub: FBSTree<A, B>, childItem: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

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

            return if (fcontains2(treeStub, childItem)) go(treeStub, childItem, Pair(FBSTNil, FBSTNil)).first else FBSTNil

        }

        // ===============

        fun<A, B: Any> FBSTree<A, B>.equal(rhs: FBSTree<A, B>): Boolean where A: Any, A: Comparable<A> = equal2(this, rhs)

        fun <A, B: Any> FBSTree<A, B>.fcontainsItem(item: TKVEntry<A, B>): Boolean where A: Any, A: Comparable<A> = ffind(this, item) is FBSTNode

        // ===============

        internal enum class FIT {
            LEFT, RIGHT,
        }

        private data class Trace<A, B: Any>(val treeStub: FBSTNode<A, B>, val direction: FIT, val duplicate: Boolean = false) where A: Any, A: Comparable<A>

        private fun <A, B: Any> fitKey(a: A, b: FBSTNode<A, B>): FIT where A: Any, A: Comparable<A> = when {
            a < b.entry.getk() -> FIT.LEFT
            else -> FIT.RIGHT
        }

        internal fun <A, B: Any> isChildMatch(node: FBSTNode<A, B>, match: TKVEntry<A, B>): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
            val leftChildMatch = (node.bLeft is FBSTNode) && node.bLeft.entry == match
            val rightChildMatch = (node.bRight is FBSTNode) && node.bRight.entry == match
            return Pair(leftChildMatch, rightChildMatch)
        }

        internal fun <A, B: Any> isChildMatch(node: FBSTNode<A, B>, match: Comparable<TKVEntry<A, B>>): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
            val leftChildMatch = (node.bLeft is FBSTNode) && match.compareTo(node.bLeft.entry) == 0
            val rightChildMatch = (node.bRight is FBSTNode) && match.compareTo(node.bRight.entry) == 0
            return Pair(leftChildMatch, rightChildMatch)
        }

        // assert the BST property on this node
        internal fun <A, B: Any> fbtAssert(n: FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> {
            if (n.bLeft is FBSTNode) check(FIT.RIGHT == fitKey(n.entry.getk(), n.bLeft))
            if (n.bRight is FBSTNode) check(FIT.LEFT == fitKey(n.entry.getk(), n.bRight))
            return n
        }

        fun<A, B: Any> equal2(rhs: FBSTree<A, B>, lhs: FBSTree<A, B>): Boolean where A: Any, A: Comparable<A> = when(Pair(lhs.fempty(), rhs.fempty())) {
            Pair(false, false) -> if (rhs === lhs) true else equal(rhs, lhs)
            Pair(true, true) -> true
            else -> false
        }

        // clip off the whole branch starting at, and inclusive of, a node with value clipMatch
        internal fun <A, B: Any> prune(treeStub: FBSTree<A, B>, clipMatch: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

            tailrec fun copy(stack: FStack<FBSTNode<A, B>>, acc: FBSTree<A, B>): Pair<FBSTree<A, B>, FStack<FBSTNode<A, B>>> {
                return if (stack.isEmpty()) Pair(acc, stack) else {
                    val (node, shortStack) = stack.pop()
                    val (newStack, newAcc) = when (isChildMatch(node, clipMatch)) {
                        Pair(false, false) -> {
                            val na = finsert(acc, node.entry, allowDups = true)
                            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
                            val ns = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
                            Pair(ns, na)
                        }
                        Pair(true,  false) -> {
                            val na = finsert(acc, node.entry, allowDups = true)
                            val ns = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
                            Pair(ns, na)
                        }
                        Pair(false,  true) -> {
                            val na = finsert(acc, node.entry, allowDups = true)
                            val ns = if (node.bLeft is FBSTNode) FStack.push(shortStack, node.bLeft) else shortStack
                            Pair(ns, na)
                        }
                        else -> throw RuntimeException("impossible code path")
                    }
                    copy(newStack, newAcc)
                }
            }

            return when {
                ! fcontains2(treeStub, clipMatch) -> /* nothing to prune */ treeStub
                treeStub.fempty() -> /* nothing to match */ FBSTNil
                else -> when(fparent(treeStub, clipMatch)) {
                    is FBSTNil -> /* root; not found was handled above */ FBSTNil
                    is FBSTNode -> copy(FStack.push(FStack.emptyFStack(), (treeStub as FBSTNode)), FBSTNil).first
                }
            }

        }

        private fun <A, B: Any> enrichNode(node: FBSTNode<A, B>, tree: Trace<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> =
            if (tree.duplicate) when (tree.direction) {
                FIT.RIGHT -> FBSTNode(
                    tree.treeStub.entry,
                    tree.treeStub.bLeft,
                    FBSTNode(tree.treeStub.entry, FBSTNil, tree.treeStub.bRight)
                )
                FIT.LEFT -> FBSTNode(
                    tree.treeStub.entry,
                    FBSTNode(tree.treeStub.entry, tree.treeStub.bLeft, FBSTNil),
                    tree.treeStub.bRight,
                )
            } else when (tree.direction) {
                FIT.LEFT -> FBSTNode(tree.treeStub.entry, node, tree.treeStub.bRight)
                FIT.RIGHT -> FBSTNode(tree.treeStub.entry, tree.treeStub.bLeft, node)
            }

        private tailrec fun <A, B: Any> reassemble(
            item: FBSTNode<A, B>,
            cur: FBSTree<A, B>,
            trace: FList<Trace<A, B>>,
            op: (FBSTNode<A, B>, Trace<A, B>) -> FBSTNode<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> =
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
        internal fun <A, B: Any> addGraft(treeStub: FBSTree<A, B>, graft: FBSTree<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            when (Pair(treeStub.fempty(), graft.fempty())) {
                Pair(true, true) -> FBSTNil
                Pair(true, false) -> graft
                Pair(false, true) -> treeStub
                else -> reassemble(graft as FBSTNode, treeStub, FLNil, ::enrichNode)
            }

        private fun <A, B: Any> appender(withDups: Boolean): (FBSTree<A, B>, TKVEntry<A, B>) -> FBSTree<A, B> where A: Any, A: Comparable<A> =
            { treeStub: FBSTree<A, B>, item: TKVEntry<A, B> -> finsert(treeStub, item, allowDups = withDups)}

        private fun <B: Any> appenderIntKey(withDups: Boolean): (FBSTree<Int, B>, B) -> FBSTree<Int, B> =
            { treeStub: FBSTree<Int, B>, item: B -> finsert(treeStub, item.toIAEntry(), allowDups = withDups)}

        private fun <B: Any> appenderStrKey(withDups: Boolean): (FBSTree<String, B>, B) -> FBSTree<String, B> =
            { treeStub: FBSTree<String, B>, item: B -> finsert(treeStub, item.toSAEntry(), allowDups = withDups)}

        private fun <A, B: Any, C: Comparable<C>, D: Any> mapAppender(
                kf: (A) -> (C),
                vf: (B) -> (D),
                withDups: Boolean): (FBSTree<C, D>, TKVEntry<A, B>) -> FBSTree<C, D> where A: Any, A: Comparable<A> =
            { treeStub: FBSTree<C, D>, item: TKVEntry<A, B> -> finsert(treeStub, TKVEntryK(kf(item.getk()), vf(item.getv())), allowDups = withDups)}

        private fun <A, B: Any, C, D: Any> mapAppender(
            mappedItem: TKVEntry<C, D>,
            withDups: Boolean = false): (FBSTree<C, D>, TKVEntry<A, B>) -> FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> =
            { treeStub: FBSTree<C, D>, _: TKVEntry<A, B> -> finsert(treeStub, mappedItem, allowDups = withDups)}
    }
}

internal object FBSTNil: FBSTree<Nothing, Nothing>() {
    override fun toString(): String = "FBSTNil"
    override fun hashCode(): Int = toString().hashCode()
}

internal data class FBSTNode<out A, out B: Any> (
    val entry: TKVEntry<A, B>,
    val bLeft: FBSTree<A, B> = FBSTNil,
    val bRight: FBSTree<A, B> = FBSTNil
): FBSTree<A, B>() where A: Any, A: Comparable<@UnsafeVariance A> {

    internal fun branch(position: FBSTree.Companion.FIT): FBSTree<A, B> = when (position) {
        FBSTree.Companion.FIT.LEFT -> bLeft
        FBSTree.Companion.FIT.RIGHT -> bRight
    }

    internal fun isLeaf(): Boolean = bLeft is FBSTNil && bRight is FBSTNil

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FBSTNode<*, *> -> when {
            this.entry::class == other.entry::class -> @Suppress("UNCHECKED_CAST") equal(this, other as FBSTNode<A, B> )
            else -> false
        }
        else -> false
    }

    override fun hashCode(): Int {
        var aux: Long = entry.hashCode().toLong()
        aux = 3L * aux + bLeft.preorder().hashCode()
        aux = 3L * aux + bRight.preorder().hashCode()
        return if (Int.MIN_VALUE.toLong() < aux && aux < Int.MAX_VALUE.toLong()) aux.toInt()
        else /* may it even theoretically get here? */ TODO("must reduce range of FBSTNode.hashcode to Int")
    }

    companion object {
        fun <A, B: Any> hashCode(n: FBSTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hashCode()
    }

}

//typealias Result = Pair<KFunction<*>?, Any?>
//typealias Func = KFunction<Result>
//
//tailrec fun trampoline(f: Func, arg: Any?): Any? {
//    val (f2,arg2) = f.call(arg)
//    @Suppress("UNCHECKED_CAST")
//    return if (f2 == null) arg2
//    else trampoline(f2 as Func, arg2)
//}
//
//fun odd(n: Int): Result =
//    if (n == 0) null to false
//    else ::even to n-1
//
//fun even(n: Int): Result =
//    if (n == 0) null to true
//    else ::odd to n-1
//
//fun mutualrecursion() {
//    System.out.println(trampoline(::even, 9999999))
//}


