package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.IMBTreeTraversable.Companion.equal

sealed class FBSTree<out A, out B: Any>: Collection<B>, IMBTree<A, B> where A: Any, A: Comparable<@UnsafeVariance A> {

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

    override fun contains(element: @UnsafeVariance B): Boolean = when(this) {
        is FBSTNil -> false
        is FBSTNode<A, B> -> when (this.root()!!.getk()) {
            is Int -> @Suppress("UNCHECKED_CAST") (containsIntKey((this as FBSTree<Int, B>), element))
            is String -> @Suppress("UNCHECKED_CAST") (containsStrKey((this as FBSTree<String, B>), element))
            else -> {
                val itr = iterator() as FListIteratorFwd<B>
                var found = false
                while (! found) {
                    itr.nullableNext()?.let{ found = it == element } ?: break
                }
                found
            }
        }
    }

    override fun containsAll(elements: Collection<@UnsafeVariance B>): Boolean {
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    override fun iterator(): Iterator<B> = FListIteratorFwd(this.inorder().fmap { kv -> kv.getv() })

    /*
        A Binary search tree allows duplicates, but may become pathologically unbalanced.  In the latter case,
        a recursive implementation is not stack safe.  This implementation optionally allows duplicates and
        is fully stack safe.  Search may become ~O(n) when the tree is very unbalanced.  Insertion and deletion
        are not as expensive as for a Balanced Binary search tree.  This is an immutable stack-safe implementation
        of a Binary Search Tree.
     */

    override fun reverseIterator(): Iterator<B> = FListIteratorFwd(this.inorder(reverse = false).fmap { kv -> kv.getv() })

    override fun fsize(): Int = size

    override fun popAndReminder(): Pair<TKVEntry<A,B>?, FBSTree<A, B>> {
        val pop: TKVEntry<A,B>? = this.fpick()
        val reminder: FBSTree<A, B> = pop?.let { delete(this, it) } ?: FBSTNil
        return Pair(pop, reminder)
    }

    override fun root(): TKVEntry<A,B>? = when(this) {
        is FBSTNil -> null
        is FBSTNode -> this.entry
    }

    override fun fpick(): TKVEntry<A,B>? = this.root() // this.leftMost() ?: this.root()

    override fun leftMost(): TKVEntry<A, B>? {
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

    override fun rightMost(): TKVEntry<A, B>? {
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

    // returns the maximum path length from the root of a tree to any node.
    override fun maxDepth(): Int {

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
    override fun minDepth(): Int {

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

    private tailrec fun <C> unwindStack(stack: FStack<FBSTNode<A, B>>,
                                        acc: C,
                                        accrue: (FStack<FBSTNode<A, B>>, C) -> Pair<C, FStack<FBSTNode<A, B>>>): C =
        if (stack.isEmpty()) acc
        else {
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

        tailrec fun leftDescent(t: FBSTree<A, B>, stack: FStack<FBSTNode<A, B>>): FStack<FBSTNode<A, B>> =
            when (t) {
                is FBSTNil -> stack
                is FBSTNode -> leftDescent(t.bLeft, FStack.push(stack, t))
            }

        tailrec fun changeDirection(stack: FStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FStack<FBSTNode<A, B>>> =
            when (stack.isEmpty()) {
                true -> Pair(acc, stack)
                false -> {
                    val (node, shortStack) = stack.pop()
                    val newAcc = visit(node, acc)
                    val remaining = leftDescent(node.bRight, shortStack)
                    changeDirection(remaining, newAcc)
                }
            }

        tailrec fun accrue(stack: FStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FStack<FBSTNode<A, B>>> {
            if (stack.isEmpty()) return Pair(acc, stack)
            val (current, shortStack) = stack.pop()
            val accruedAtLeft = leftDescent(current, shortStack)
            return when (accruedAtLeft.isEmpty()) {
                true -> Pair(acc, accruedAtLeft)
                false -> {
                    val (newAcc, toDo) = changeDirection(accruedAtLeft, acc)
                    accrue(toDo, newAcc)
                }
            }
        }

        return if (this.fempty()) FLNil else when(reverse) {
            false -> unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FLNil, ::accrue).freverse()
            true -> unwindStack(FStack.push(FStack.emptyFStack(),this as FBSTNode), FLNil, ::accrue)
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

//    fun <A, B> fold(ta: FBTree<A, B>, l: (A) -> B, b: (B, B) -> B): B = TODO()

    fun <C: Any> mapi(f: (B) -> C): FBSTree<Int, C> =
        this.preorder(reverse = true).ffoldLeft(nul()) { t: FBSTree<Int, C>, e: TKVEntry<A, B> ->
            mapAppender<A, B, Int, C>(TKVEntry.ofIntKey(f(e.getv())))(t, e)
        }

    fun <C: Any> maps(f: (B) -> C): FBSTree<String, C> =
        this.preorder(reverse = true).ffoldLeft(nul()) { t: FBSTree<String, C>, e: TKVEntry<A, B> ->
            mapAppender<A, B, String, C>(TKVEntry.ofStrKey(f(e.getv())))(t, e)
        }

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

    companion object {

        private data class Trace<A, B: Any>(val treeStub: FBSTNode<A, B>, val direction: FIT, val duplicate: Boolean = false) where A: Any, A: Comparable<A>

        internal enum class FIT {
            LEFT, RIGHT,
        }

        fun <A, B: Any> nul(): FBSTree<A, B> where A: Any, A: Comparable<A> = FBSTNil

        private fun <A, B: Any> fitKey(a: A, b: FBSTNode<A, B>): FIT where A: Any, A: Comparable<A> = when {
            a < b.entry.getk() -> FIT.LEFT
            else -> FIT.RIGHT
        }

        internal fun <A, B: Any> isChildMatch(node: FBSTNode<A, B>, match: TKVEntry<A, B>): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
            val leftChildMatch = (node.bLeft is FBSTNode) && node.bLeft.entry == match
            val rightChildMatch = (node.bRight is FBSTNode) && node.bRight.entry == match
            return Pair(leftChildMatch, rightChildMatch)
        }

        // assert the BST property on this node
        internal fun <A, B: Any> fbtAssert(n: FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> {
            if (n.bLeft is FBSTNode) assert(FIT.RIGHT == fitKey(n.entry.getk(), n.bLeft))
            if (n.bRight is FBSTNode) assert (FIT.LEFT == fitKey(n.entry.getk(), n.bRight))
            return n
        }

        // find the first tree stub matching value; the returned stub may have other nodes that match value
        tailrec fun <A, B: Any> find(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> = when (treeStub) {
            is FBSTNil -> treeStub
            is FBSTNode -> {
                val next: FBSTree<A, B>? = when {
                    item.getk() < treeStub.entry.getk() -> treeStub.bLeft
                    treeStub.entry.getk() < item.getk() -> treeStub.bRight
                    else -> null
                }
                // unfortunately this is not tail-recursive
                // next?.let { find(next, value) } ?: treeStub
                if (next == null) treeStub else find(next, item)
            }
        }

        fun<A, B: Any> equal2(rhs: FBSTree<A, B>, lhs: FBSTree<A, B>): Boolean where A: Any, A: Comparable<A> = when(Pair(lhs.fempty(), rhs.fempty())) {
            Pair(false, false) -> if (rhs === lhs) true else equal(rhs, lhs)
            Pair(true, true) -> true
            else -> false
        }

        fun<A, B: Any> FBSTree<A, B>.equal(rhs: FBSTree<A, B>): Boolean where A: Any, A: Comparable<A> = equal2(this, rhs)

        // find the last tree stub matching value; the returned stub will not have other nodes that match value
        tailrec fun <A, B: Any> findLast(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

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
                    if (next == null) treeStub else findLast(next, item)
                }
            }
        }

        // the immediate parent (and all that parent's descendants) of the child having childValue
        fun <A, B: Any> parent(treeStub: FBSTree<A, B>, childItem: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

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

            return if (contains2(treeStub, childItem)) go(treeStub, childItem, Pair(FBSTNil, FBSTNil)).first else FBSTNil

        }

        fun <A, B: Any> contains2(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): Boolean where A: Any, A: Comparable<A> = find(treeStub, item) is FBSTNode

        operator fun <A, B: Any> FBSTree<A, B>.contains(item: TKVEntry<A, B>): Boolean where A: Any, A: Comparable<A> = find(this, item) is FBSTNode

        fun <B: Any> containsIntKey(treeStub: FBSTree<Int, B>, value: B): Boolean = find(treeStub, TKVEntry.ofIntKey(value)) is FBSTNode

        fun <B: Any> containsStrKey(treeStub: FBSTree<String, B>, value: B): Boolean = find(treeStub, TKVEntry.ofStrKey(value)) is FBSTNode

        // clip off the whole branch starting at, and inclusive of, a node with value clipMatch
        internal fun <A, B: Any> prune(treeStub: FBSTree<A, B>, clipMatch: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

            tailrec fun copy(stack: FStack<FBSTNode<A, B>>, acc: FBSTree<A, B>): Pair<FBSTree<A, B>, FStack<FBSTNode<A, B>>> {
                return if (stack.isEmpty()) Pair(acc, stack) else {
                    val (node, shortStack) = stack.pop()
                    val (newStack, newAcc) = when (isChildMatch(node, clipMatch)) {
                        Pair(false, false) -> {
                            val na = insert(acc, node.entry, allowDups = true)
                            val auxStack = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
                            val ns = if (node.bLeft is FBSTNode) FStack.push(auxStack, node.bLeft) else auxStack
                            Pair(ns, na)
                        }
                        Pair(true,  false) -> {
                            val na = insert(acc, node.entry, allowDups = true)
                            val ns = if (node.bRight is FBSTNode) FStack.push(shortStack, node.bRight) else shortStack
                            Pair(ns, na)
                        }
                        Pair(false,  true) -> {
                            val na = insert(acc, node.entry, allowDups = true)
                            val ns = if (node.bLeft is FBSTNode) FStack.push(shortStack, node.bLeft) else shortStack
                            Pair(ns, na)
                        }
                        else -> throw RuntimeException("impossible code path")
                    }
                    copy(newStack, newAcc)
                }
            }

            return when {
                ! contains2(treeStub, clipMatch) -> /* nothing to prune */ treeStub
                treeStub.fempty() -> /* nothing to match */ FBSTNil
                else -> when(parent(treeStub, clipMatch)) {
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
                    when (cur.branch(pos)) {
                        is FBSTNil -> FLCons(Trace(cur, pos, dup), trace).ffoldLeft(item, op)
                        is FBSTNode -> reassemble(item, cur.branch(pos), FLCons(Trace(cur, pos, dup), trace), op)
                    }
                }
            }

        // insert item into treeStub at the correct position
        fun <A, B: Any> insert(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> {

            fun emplace(node: FBSTNode<A, B>, tree: Trace<A, B>): FBSTNode<A, B> = when {
                allowDups -> enrichNode(node, tree)
                node.entry == tree.treeStub.entry -> tree.treeStub
                else -> enrichNode(node, tree)
            }

            return when (treeStub) {
                is FBSTNil -> FBSTNode(item)
                is FBSTNode -> reassemble(FBSTNode(item), treeStub, FLNil, ::emplace)
            }
        }

        // insert items into treeStub at the correct position
        tailrec fun <A, B: Any> insert(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A, B>>, allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> =
            when (items) {
                is FLNil -> treeStub
                is FLCons -> insert(insert(treeStub, items.head, allowDups), items.tail, allowDups)
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
            { treeStub: FBSTree<A, B>, item: TKVEntry<A, B> -> insert(treeStub, item, allowDups = withDups)}

        private fun <A, B: Any, C: Comparable<C>, D: Any> mapAppender(
                kf: (A) -> (C),
                vf: (B) -> (D),
                withDups: Boolean): (FBSTree<C, D>, TKVEntry<A, B>) -> FBSTree<C, D> where A: Any, A: Comparable<A> =
            { treeStub: FBSTree<C, D>, item: TKVEntry<A, B> -> insert(treeStub, TKVEntryK(kf(item.getk()), vf(item.getv())), allowDups = withDups)}

        private fun <A, B: Any, C, D: Any> mapAppender(
            mappedItem: TKVEntry<C, D>,
            withDups: Boolean = false): (FBSTree<C, D>, TKVEntry<A, B>) -> FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> =
            { treeStub: FBSTree<C, D>, _: TKVEntry<A, B> -> insert(treeStub, mappedItem, allowDups = withDups)}

        fun <A, B: Any> delete(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, onlyOneIfDuplicate: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> {

            fun buildReplacement(
                eureka: FBSTNode<A, B>,
                current: FBSTree<A, B>
            ): Pair<FBSTree<A, B>, FBSTNode<A, B>> {
                val newValue = /* find replacement value */ eureka.bRight.leftMost()!!
                val graftRight =  /* build new right subtree */
                    if ((eureka.bRight as FBSTNode).entry == newValue) eureka.bRight.bRight
                    else prune(eureka.bRight, newValue)
                val stub = /* remove all A */ prune(treeStub, eureka.entry)
                val graftLeft = /* there may be duplicates */ (find(current, item) as FBSTNode).bLeft
                val replacement = /* build replacement */
                    FBSTNode(newValue, graftLeft, graftRight)
                return Pair(stub, replacement)
            }

            tailrec fun splice(item: TKVEntry<A, B>, current: FBSTree<A, B>, trace: FList<Trace<A, B>>): FBSTree<A, B> =
                when (current) {
                    is FBSTNil -> trace.ffoldLeft(FBSTNode(item), ::enrichNode)
                    is FBSTNode -> {
                        when (val last = findLast(current, item)) {
                            is FBSTNode -> {
                                val first = find(current, item) as FBSTNode
                                val noDuplicates = last === first || onlyOneIfDuplicate
                                val found = if (onlyOneIfDuplicate) first else last
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

            return when (contains2(treeStub, item)) {
                false -> treeStub
                true -> splice(item, treeStub, FLNil)
            }
        }

        fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> =
            of(items.iterator(), allowDups)

        fun <A, B: Any> of(fl: FList<TKVEntry<A, B>>, allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> =
            fl.ffoldLeft(nul(), appender(allowDups))

        fun <A, B: Any> of(fl: Iterator<TKVEntry<A, B>>, allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> =
            FList.of(fl).ffoldLeft(nul(), appender(allowDups))

        fun <A, B: Any> ofValues(fl: Iterator<B>, allowDups: Boolean = false): FBSTree<Int, B> where A: Any, A: Comparable<A> =
            FList.of(fl).fmap{TKVEntry.ofIntKey(it)}.ffoldLeft(nul(), appender(allowDups))
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


