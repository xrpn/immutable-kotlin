package com.xrpn.immutable

import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.*
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
import com.xrpn.immutable.FStack.Companion.emptyIMStack
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import kotlin.reflect.KClass

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

    override operator fun contains(element: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean = when(this) {
        is FBSTNil -> false
        is FBSTNode<A, B> -> this.fcontains(element)
        else -> throw RuntimeException("internal error")
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

    // =========== imcollection

    override val seal = IMSC.IMTREE

    override fun fcontains(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean =
        bstFind(this, item) != null

    override fun fdropAll(items: IMCommon<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FBSTree<A, B> = when (items) {
        // TODO consider memoization
        is IMBTree -> this.fdropAlt(items) as FBSTree<A, B>
        is IMMap -> this.fdropAlt(items.asIMBTree()) as FBSTree<A, B>
        else -> bstDeletes(this, items)
    }

    override fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        // TODO consider memoization
        return bstDelete(this, item, atMostOne = true)
    }

    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> {

        val f4ffilter: (acc: FBSTree<A, B>, item: TKVEntry<A, B>) -> FBSTree<A, B> =
            { acc, item -> if (isMatch(item)) bstInsert(acc, item, allowDups = false) else acc }

        return ffold(nul(isAcceptDuplicates()), f4ffilter)
    }

    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> =
        ffilter { !isMatch(it) }

    override fun ffindAny(isMatch: (TKVEntry<A, B>) -> Boolean): TKVEntry<A, B>? {

        val f: (previous: Pair<Boolean, TKVEntry<A, B>>, entry: TKVEntry<A, B>) -> Pair<Boolean, TKVEntry<A, B>> = { previous, entry -> if (previous.first) previous else Pair(isMatch(entry), entry) }
        fun accrueForFold(stack: IMStack<FBSTNode<A, B>>, acc: Pair<Boolean, TKVEntry<A, B>>): Pair<Pair<Boolean, TKVEntry<A, B>>, IMStack<FBSTNode<A, B>>> =
            if (acc.first) Pair(acc, emptyIMStack()) else {
                val (node, shortStack) = stack.fpopOrThrow()
                val newAcc: Pair<Boolean, TKVEntry<A, B>> = visitForFold(node, acc, f)
                val auxStack = if (node.bRight is FBSTNode) shortStack.fpush(node.bRight) else shortStack
                val newStack = if (node.bLeft is FBSTNode) auxStack.fpush(node.bLeft) else auxStack
                Pair(newAcc, newStack)
            }

        return if (this.fempty()) null else {
            val start: Pair<Boolean, TKVEntry<A, B>> = Pair(false, this.froot()!!)
            val stop = unwindStack(FStack.of(this as FBSTNode), start, ::accrueForFold)
            if (stop.first) stop.second else null
        }
    }

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

    override fun fsize(): Int = size

    // =========== imkeyed

    override fun fcontainsKey(key: @UnsafeVariance A): Boolean =
        bstFindKey(this, key) != null

    override fun fcountKey(isMatch: (A) -> Boolean): Int {

        val f4fcountKey: (acc: Int, item: TKVEntry<A, B>) -> Int =
            { acc, item -> if (isMatch(item.getk())) acc + 1 else acc }

        return ffold(0, f4fcountKey)
    }

    override fun fdropKeys(keys: IMSet<@UnsafeVariance A>): FBSTree<A, B> = keys.ffold(nul()) { acc, key ->
        if (fcontainsKey(key)) acc else bstFindKey(this, key)?.let {
            finsert(it.froot()!!)
        } ?: acc
    }

    override fun ffilterKey(isMatch: (A) -> Boolean): FBSTree<A, B> {

        val f4ffilterKey: (acc: FBSTree<A, B>, item: TKVEntry<A, B>) -> FBSTree<A, B> =
            { acc, item -> if (isMatch(item.getk())) bstInsert(acc, item, allowDups = true) else acc }

        return ffold(nul(isAcceptDuplicates()), f4ffilterKey)
    }

    override fun ffilterKeyNot(isMatch: (A) -> Boolean): FBSTree<A, B> =
        ffilterKey { !isMatch(it) }

    override fun ffilterValue(isMatch: (B) -> Boolean): FBSTree<A, B> {

        val f4ffilterValue: (acc: FBSTree<A, B>, item: TKVEntry<A, B>) -> FBSTree<A, B> =
            { acc, item -> if (isMatch(item.getv())) bstInsert(acc, item, allowDups = true) else acc }

        return ffold(nul(isAcceptDuplicates()), f4ffilterValue)
    }

    override fun ffilterValueNot(isMatch: (B) -> Boolean): FBSTree<A, B> =
        ffilterValue { !isMatch(it) }

    override fun ffindAnyValue(isMatch: (B) -> Boolean): B? {

        val f: (acc: Pair<Boolean, TKVEntry<A, B>>, entry: TKVEntry<A, B>) -> Pair<Boolean, TKVEntry<A, B>> =
            { _, entry -> Pair(isMatch(entry.getv()), entry) }

        // this is a generic preorder
        fun accrueForFold(stack: IMStack<FBSTNode<A, B>>, acc: Pair<Boolean, TKVEntry<A, B>>): Pair<Pair<Boolean, TKVEntry<A, B>>, IMStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.fpopOrThrow()
            val newAcc: Pair<Boolean, TKVEntry<A, B>> = visitForFold(node, acc, f)
            return if (newAcc.first) Pair(newAcc, emptyIMStack()) else {
                val auxStack = if (node.bRight is FBSTNode) shortStack.fpush(node.bRight) else shortStack
                val newStack = if (node.bLeft is FBSTNode) auxStack.fpush(node.bLeft) else auxStack
                Pair(newAcc, newStack)
            }
        }

        return if (this.fempty()) null else {
            val startAcc: Pair<Boolean, TKVEntry<A, B>> = Pair(false, this.froot()!!)
            val endAcc = unwindStack(FStack.of(this as FBSTNode), startAcc, ::accrueForFold)
            if (endAcc.first) endAcc.second.getv() else null
        }
    }

    override fun fget(key: @UnsafeVariance A): B? = when (this) {
        is FBSTNil -> null
        is FBSTNode -> ffindValueOfKey(key)
        else -> throw RuntimeException("internal error")
    }

    override fun asIMMap(): IMMap<A, B> = toIMMap()

    // =========== extras

    override operator fun set(k: @UnsafeVariance A, v: @UnsafeVariance B): FBSTree<A, B> {
        TODO("Not yet implemented")
    }

    override operator fun get(key: @UnsafeVariance A): B? {
        TODO("Not yet implemented")
    }

    // =========== utility

    override fun equal(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): Boolean = this.equals(rhs)

    override fun toIMSet(kType: RestrictedKeyType<@UnsafeVariance A>?): FKSet<A, B>? = toFKSetImpl(this, kType)

    override fun <K> toIMBTree(kType: RestrictedKeyType<@UnsafeVariance K>): IMBTree<K, B>? where K: Any, K: Comparable<K> = toFBSTreeImpl(this, kType)

    override fun toIMMap(): IMMap<A, B> = ofFKMapBody(this.toFRBTree())

    override fun copy(): FBSTree<A, B> = this.ffold(nul(isAcceptDuplicates())) { acc, tkv -> acc.finsert(tkv) }

    // =========== traversable

    override fun preorder(reverse: Boolean): FList<TKVEntry<A, B>> {
        val seed = FList.emptyIMList<TKVEntry<A,B>>()
        val fl = this.ffold(seed) { acc: FList<TKVEntry<A, B>>, item -> FLCons(item, acc) }
        return if(reverse) fl else fl.freverse()
    }

    override fun inorder(reverse: Boolean): FList<TKVEntry<A, B>> {

        tailrec fun inoLeftDescent(t: FBSTree<A, B>, stack: IMStack<FBSTNode<A, B>>): IMStack<FBSTNode<A, B>> =
            when (t) {
                is FBSTNil -> stack
                is FBSTNode -> inoLeftDescent(t.bLeft, stack.fpush(t))
                else -> throw RuntimeException("internal error")
            }

        tailrec fun inoChangeDirection(stack: IMStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, IMStack<FBSTNode<A, B>>> =
            when (stack.fempty()) {
                true -> Pair(acc, stack)
                false -> {
                    val (node, shortStack) = stack.fpopOrThrow()
                    val newAcc = visit(node, acc)
                    val remaining = inoLeftDescent(node.bRight, shortStack)
                    inoChangeDirection(remaining, newAcc)
                }
            }

        tailrec fun inoAccrue(stack: IMStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, IMStack<FBSTNode<A, B>>> {
            if (stack.fempty()) return Pair(acc, stack)
            val (current, shortStack) = stack.fpopOrThrow()
            val accruedAtLeft = inoLeftDescent(current, shortStack)
            return when (accruedAtLeft.fempty()) {
                true -> Pair(acc, accruedAtLeft)
                false -> {
                    val (newAcc, toDo) = inoChangeDirection(accruedAtLeft, acc)
                    inoAccrue(toDo, newAcc)
                }
            }
        }

        return if (this.fempty()) FLNil else when(reverse) {
            false -> unwindStack(FStack.of(this as FBSTNode), FLNil, ::inoAccrue).freverse()
            true -> unwindStack(FStack.of(this as FBSTNode), FLNil, ::inoAccrue)
        }

    }

    override fun postorder (reverse: Boolean): FList<TKVEntry<A, B>> {

        fun accrue(stack: IMStack<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, IMStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.fpopOrThrow()
            val auxStack = if (node.bLeft is FBSTNode) shortStack.fpush(node.bLeft) else shortStack
            val newStack = if (node.bRight is FBSTNode) auxStack.fpush(node.bRight) else auxStack
            return Pair(visit(node,acc), newStack)
        }

        return if (this.fempty()) FLNil else when(reverse) {
            false -> unwindStack(FStack.of(this as FBSTNode), FLNil, ::accrue)
            true -> unwindStack(FStack.of(this as FBSTNode), FLNil, ::accrue).freverse()
        }

    }

    override fun breadthFirst(reverse: Boolean): FList<TKVEntry<A, B>> {

        fun accrue(q: FQueue<FBSTNode<A, B>>, acc: FList<TKVEntry<A, B>>): Pair<FList<TKVEntry<A, B>>, FQueue<FBSTNode<A, B>>> {
            val (node, dequeued) = q.fdequeueOrThrow()
            val q1 = if (node.bLeft is FBSTNil) dequeued else dequeued.fenqueue(node.bLeft as FBSTNode)
            val q2 = if (node.bRight is FBSTNil) q1 else q1.fenqueue(node.bRight as FBSTNode)
            return Pair(FLCons(node.entry, acc), q2)
        }

        return if (this.fempty()) FLNil else when(reverse) {
            true -> unwindQueue(emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue(this as FBSTNode), FLNil, ::accrue)
            false -> unwindQueue(emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue(this as FBSTNode), FLNil, ::accrue).freverse()
        }
    }

    // =========== filtering

    override fun fdropItemAll(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        // TODO consider memoization
        return bstDelete(this, item, atMostOne = false)
    }

    override fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): FList<TKVEntry<A, B>> {

        // TODO common code
        val f4ffind: (FList<TKVEntry<A, B>>, TKVEntry<A, B>) -> FList<TKVEntry<A, B>> =
            { acc: FList<TKVEntry<A, B>>, item: TKVEntry<A, B> -> if (isMatch(item)) FLCons(item, acc) else acc }

        return ffold(FLNil, f4ffind)
    }

    override fun ffindItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B>? =
        bstFind(this, item)

    override fun ffindKey(key: @UnsafeVariance A): FBSTree<A, B>? =
        bstFindKey(this, key)

    override fun ffindLastItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B>? =
        bstFindLast(this, item)

    override fun ffindLastKey(key: @UnsafeVariance A): FBSTree<A, B>? =
        bstFindLastKey(this, key)

    override fun ffindValueOfKey(key: @UnsafeVariance A): B? =
        bstFindValueOFKey(this, key)

    override fun frestrictedKey(): RestrictedKeyType<A>? = when (this) {
        is FBSTNode -> fbsRKeyType
        else -> null
    }

    override fun fhasDups(): Boolean =
        ffindAny { fisDup(it) } != null

    override fun fisDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean =
        ffindItem(item)?.let { first -> first.size != ffindLastItem(item)?.size } ?: false

    override fun fleftMost(): TKVEntry<A, B>? {

        tailrec fun leftDescent(bt: FBSTree<A, B>): TKVEntry<A, B>? =
            when(bt) {
                is FBSTNil -> null
                is FBSTNode -> when (bt.bLeft) {
                    is FBSTNil -> bt.entry
                    is FBSTNode -> leftDescent(bt.bLeft)
                    else -> throw RuntimeException("internal error")
                }
                else -> throw RuntimeException("internal error")
            }

        return leftDescent(this)
    }

    override fun fparentOf(child: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B>? =
        bstParent(this, child)

    override fun fpeek(): TKVEntry<A,B>? = this.fleftMost() ?: this.froot()

    override fun frightMost(): TKVEntry<A, B>? {
        tailrec fun rightDescent(bt: FBSTree<A, B>): TKVEntry<A, B>? =
            when(bt) {
                is FBSTNil -> null
                is FBSTNode -> when (bt.bRight) {
                    is FBSTNil -> bt.entry
                    is FBSTNode -> rightDescent(bt.bRight)
                    else -> throw RuntimeException("internal error")
                }
                else -> throw RuntimeException("internal error")
            }

        return rightDescent(this)
    }

    override fun froot(): TKVEntry<A,B>? = when(this) {
        is FBSTNode -> this.entry
        else -> null
    }

    override fun fAND(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fNOT(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = TODO()

    override fun fOR(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fXOR(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        TODO("Not yet implemented")
    }

    // =========== grouping

    override fun <C> fgroupBy(f: (TKVEntry<A, B>) -> C): IMMap<C, FBSTree<A, B>> where C: Any, C: Comparable<C> =
        TODO() //	A map of collections created by the function f

    override fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair</* true */ FBSTree<A, B>, /* false */ FBSTree<A, B>> {

        fun f4fpartition(acc: Pair<FBSTree<A, B>, FBSTree<A, B>>, current: (TKVEntry<A, B>)): Pair<FBSTree<A, B>, FBSTree<A, B>> =
            if (isMatch(current)) Pair(bstInsert(acc.first, current, allowDups = true), acc.second)
            else Pair(acc.first, bstInsert(acc.second, current, allowDups = true))

        return ffold(Pair(nul(isAcceptDuplicates()), nul(isAcceptDuplicates())), ::f4fpartition)
    }

    override fun fpopAndRemainder(): Pair<TKVEntry<A,B>?, FBSTree<A, B>> {
        val pop: TKVEntry<A,B>? = this.fpeek()
        // computing the remainder can be very expensive; if traversing
        // the full tree, .inorder() or .forEach() may be cheaper
        val remainder: FBSTree<A, B> = pop?.let { this.fdropItem(it) } ?: emptyFBSTreeKernel
        return Pair(pop, remainder)
    }

    // returns the maximum path length from the root of a tree to any node.

    private val maxBstDepth: Int by lazy {
        fun accrue(q: FQueue<FBSTNode<A, B>>, depth: Int): Pair<Int, FQueue<FBSTNode<A, B>>> {

            tailrec fun harvestThisLevel(q: FQueue<FBSTNode<A, B>>, r: FQueue<FBSTNode<A, B>>): FQueue<FBSTNode<A, B>> {
                val (node, shortQueue) = q.fdequeueOrThrow()
                // add non-nul children of node to queue
                val q1 = if (node.bLeft is FBSTNil) r else r.fenqueue(node.bLeft as FBSTNode)
                val q2 = if (node.bRight is FBSTNil) q1 else q1.fenqueue(node.bRight as FBSTNode)
                return if (shortQueue.isEmpty()) /* we are done with this level */ q2
                else /* more nodes on this level, keep harvesting */ harvestThisLevel(shortQueue, q2)
            }

            val newDepth = depth + 1
            val newQueue = harvestThisLevel(q, emptyIMQueue())
            return Pair(newDepth, newQueue)
        }

        if (this.fempty()) 0
        else unwindQueue(emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue(this as FBSTNode), 0, ::accrue)
    }

    override fun fmaxDepth(): Int = maxBstDepth

    private val minBstDepth: Int by lazy {

        fun accrue(q: FQueue<FBSTNode<A, B>>, depth: Int): Pair<Int, FQueue<FBSTNode<A, B>>> {

            tailrec fun harvestThisLevel(q: FQueue<FBSTNode<A, B>>, r: FQueue<FBSTNode<A, B>>): FQueue<FBSTNode<A, B>> {
                val (node, shortQueue) = q.fdequeueOrThrow()
                return if (node.isLeaf()) /* early termination at this level */ emptyIMQueue() else {
                    // add non-nul children of node to queue
                    val q1 = if (node.bLeft is FBSTNil) r else r.fenqueue(node.bLeft as FBSTNode)
                    val q2 = if (node.bRight is FBSTNil) q1 else q1.fenqueue(node.bRight as FBSTNode)
                    if (shortQueue.isEmpty()) /* we are done with this level */ q2
                    else /* more nodes on this level, keep harvesting */ harvestThisLevel(shortQueue, q2)
                }
            }

            val newDepth = depth + 1
            val newQueue = harvestThisLevel(q, emptyIMQueue())
            return Pair(newDepth, newQueue)
        }

        if (this.fempty()) 0
        else unwindQueue(emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue (this as FBSTNode), 0, ::accrue)
    }

    // returns the minimum path length from the root of a tree to the first node that is a leaf.
    override fun fminDepth(): Int = minBstDepth

    // =========== transforming

    override fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): FBSTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> = when(this) {
        is FBSTNodeGeneric -> ffold(nul(true)) { acc, tkv -> mergeAppender(acc, (f(tkv) as FBSTree<C, D>), allowDups = true) }
        is FBSTNodeUnique -> ffold(nul(false)) { acc, tkv -> mergeAppender(acc, (f(tkv) as FBSTree<C, D>), allowDups = false) }
        is FBSTGeneric -> this
        is FBSTUnique -> this
        else -> throw RuntimeException("internal error, unknown ${this::class}")
    }

    override fun <C> ffold(z: C, f: (acc: C, TKVEntry<A, B>) -> C): C {

        // this is a generic preorder
        fun accrueForFold(stack: IMStack<FBSTNode<A, B>>, acc: C): Pair<C, IMStack<FBSTNode<A, B>>> {
            val (node, shortStack) = stack.fpopOrThrow()
            val newAcc: C = visitForFold(node, acc, f)
            val auxStack = if (node.bRight is FBSTNode) shortStack.fpush(node.bRight) else shortStack
            val newStack = if (node.bLeft is FBSTNode) auxStack.fpush(node.bLeft) else auxStack
            return Pair(newAcc, newStack)
        }

        return if (this.fempty()) z
        else  unwindStack(FStack.of(this as FBSTNode), z, ::accrueForFold)
    }

    override fun <C, D: Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C,D> where C: Any, C: Comparable<@UnsafeVariance C> = when(this) {
        is FBSTNodeGeneric -> ffold(nul(true)) { acc, tkv -> acc.finsert(f(tkv)) }
        is FBSTNodeUnique -> ffold(nul(false)) { acc, tkv -> acc.finsert(f(tkv)) }
        is FBSTGeneric -> this
        is FBSTUnique -> this
        else -> throw RuntimeException("internal error, unknown ${this::class}")
    }

    override fun freduce(f: (acc: TKVEntry<A, B>, TKVEntry<A, B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): TKVEntry<A, B>? = when(this) {  // 	“Reduce” the elements of the list using the binary operator o, going from left to right
        is FBSTNode -> {
            val (seedTkv, stub) = this.fpopAndRemainder()
            stub.ffold(seedTkv!!){ acc, tkv -> f(acc, tkv) }
        }
        else -> null
    }

    // =========== altering

    override fun finsert(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> =
        bstInsert(this, item)

    override fun finserts(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FBSTree<A, B> =
        bstInserts(this, items as FList<TKVEntry<A, B>>)

    // =========== internals

    private fun visit(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> FLCons(t.entry, acc)
        else -> throw RuntimeException("internal error, unknown ${t::class}")
    }

    private fun <C> visitForFold(t: FBSTree<A, B>, acc: C, f: (acc: C, TKVEntry<A, B>) -> C): C = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> f(acc, t.entry)
        else -> throw RuntimeException("internal error, unknown ${t::class}")
    }

    private tailrec fun <C> unwindStack(stack: IMStack<FBSTNode<A, B>>,
                                        acc: C,
                                        accrue: (IMStack<FBSTNode<A, B>>, C) -> Pair<C, IMStack<FBSTNode<A, B>>>): C =
        if (stack.fempty()) acc else {
            val (newAcc, newStack) = accrue(stack, acc)
            unwindStack(newStack, newAcc, accrue)
        }

    internal tailrec fun <C> unwindQueue(queue: FQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>,
                                         acc: C,
                                         accrue: (FQueue<FBSTNode<A, B>>, C) -> Pair<C, FQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>>): C =
        if (queue.isEmpty()) acc
        else {
            val (newAcc, newQueue) = accrue(queue, acc)
            unwindQueue(newQueue, newAcc, accrue)
        }

    internal fun toFRBTree(): FRBTree<A, B> = this.ffold(FRBTree.nul(), ::rbtInsert)

    internal fun toEmptyFBSTree(): FBSTree<A, B> = if (fempty()) this else when(this) {
        is FBSTNodeGeneric<A,B> -> FBSTGeneric.empty
        is FBSTNodeUnique<A,B> -> FBSTUnique.empty
        else -> throw RuntimeException("internal error")
    }

    internal fun toGeneric(): FBSTree<A, B> = when (this) {
        is FBSTUnique -> FBSTGeneric.empty
        is FBSTNodeUnique -> breadthFirst().ffoldLeft(FBSTGeneric.empty as FBSTree<A, B>) { t, tkv -> bstInsert(t, tkv) }
        else -> this
    }

    internal fun toUnique(): FBSTree<A, B> = when (this) {
        is FBSTGeneric -> FBSTUnique.empty
        is FBSTNodeGeneric -> breadthFirst().ffoldLeft(FBSTUnique.empty as FBSTree<A, B>) { t, tkv -> bstInsert(t, tkv) }
        else -> this
    }

    internal fun isAcceptDuplicates(): Boolean = when(this) {
        is FBSTGeneric -> true
        is FBSTNodeGeneric -> true
        is FBSTUnique -> false
        is FBSTNodeUnique -> false
        else -> throw RuntimeException("internal error")
    }

    companion object: IMBTreeDupCompanion {

        const val NOT_FOUND = -1

        internal fun <A, B: Any> nul(allowDups: Boolean = false): FBSTree<A, B> where A: Any, A: Comparable<A> = if (allowDups) FBSTGeneric.empty else FBSTUnique.empty

        override fun <A, B : Any> emptyIMBTree(allowDups: Boolean): FBSTree<A, B> where A: Any,  A: Comparable<A> = nul(allowDups)

        // ===============

        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator(), allowDups)
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul(allowDups)
            items.forEach{ res = bstInsert(res, it, allowDups) }
            return res
        }
        override fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(allowDups), appender(allowDups))

        // ===============

        override fun <A, B: Any> ofc(cc: Comparator<A>, vararg items: TKVEntry<A,B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = ofc(cc, items.iterator(), allowDups)
        override fun <A, B: Any> ofc(cc: Comparator<A>, items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul(allowDups)
            items.forEach { res = bstInsert(res, TKVEntry.ofkvc(it.getk(), it.getv(), cc), allowDups) }
            return res
        }

        // =================

        override fun <B : Any> ofvi(vararg items: B, allowDups: Boolean): FBSTree<Int, B> = ofvi(items.iterator(), allowDups)
        override fun <B : Any> ofvi(items: Iterator<B>, allowDups: Boolean): FBSTree<Int, B> {
            var res: FBSTree<Int, B> = nul(allowDups)
            items.forEach {
                res = bstInsert(res, TKVEntry.ofIntKey(it), allowDups)
            }
            return res
        }
        override fun <B: Any> ofvi(items: IMList<B>, allowDups: Boolean): FBSTree<Int, B> = items.ffoldLeft(nul(allowDups), appenderIntKey(allowDups))

        // ===============

        override fun <B : Any> ofvs(vararg items: B, allowDups: Boolean): FBSTree<String, B> = ofvs(items.iterator(), allowDups)
        override fun <B: Any> ofvs(items: IMList<B>, allowDups: Boolean): FBSTree<String, B> = items.ffoldLeft(nul(allowDups), appenderStrKey(allowDups))
        override fun <B : Any> ofvs(items: Iterator<B>, allowDups: Boolean): FBSTree<String, B>  {
            var res: FBSTree<String, B> = nul(allowDups)
            items.forEach { res = bstInsert(res, TKVEntry.ofStrKey(it), allowDups) }
            return res
        }

        // ===============

        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> = ofMapImpl(items, false, f)
        override fun <A, B : Any, C, D : Any> ofMapNotUnique(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> = ofMapImpl(items, true, f)
        private fun <A, B : Any, C, D : Any> ofMapImpl(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A : Comparable<A>, C: Any, C : Comparable<C> {
            var res: FBSTree<C, D> = nul(allowDups)
            items.forEach { res = bstInsert(res, f(it), allowDups) }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, f: (B) -> C): FBSTree<Int, C> = ofviMapImpl(items, false, f)
        override fun <B : Any, C : Any> ofviMapNotUnique(items: Iterator<B>, f: (B) -> C): FBSTree<Int, C> = ofviMapImpl(items, true, f)
        private fun <B : Any, C : Any> ofviMapImpl(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<Int, C> {
            var res: FBSTree<Int, C> = nul(allowDups)
            items.forEach { res = bstInsert(res, TKVEntry.ofIntKey(f(it)), allowDups) }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, f: (B) -> C): FBSTree<String, C> = ofvsMapImpl(items, false, f)
        override fun <B : Any, C : Any> ofvsMapNotUnique(items: Iterator<B>, f: (B) -> C): FBSTree<String, C> = ofvsMapImpl(items, true, f)
        private fun <B : Any, C : Any> ofvsMapImpl(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<String, C> {
            var res: FBSTree<String, C> = nul(allowDups)
            items.forEach { res = bstInsert(res, TKVEntry.ofStrKey(f(it)), allowDups) }
            return res
        }

        override fun <A, B: Any> Map<A, B>.toIMBTree(): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul(false)
            for (entry in this) { res = res.finsert(TKVEntry.ofme(entry)) }
            return res
        }

        // =============== top level type-specific implementation

        // delete entry from treeStub.
        internal fun <A, B: Any> bstDelete(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, atMostOne: Boolean = false): FBSTree<A, B>
        where A: Any, A: Comparable<A> = when {
            treeStub.fempty() -> treeStub
            1 == treeStub.fsize() -> if (treeStub.froot()!!.equal(item)) nul(treeStub.isAcceptDuplicates()) else treeStub
            else -> {
                fun buildReplacement(
                    nodeOfInterest: FBSTNode<A, B>,
                    graftLeft: FBSTree<A, B>,
                ): FBSTNode<A, B> {
                    nodeOfInterest.bRight as FBSTNode
                    /*
                       we need the smallest of the larger entries in the tree as replacement;
                       this entry is the smallest (leftMost) of the larger (right) branch
                       TODO use implicit FIT ordering instead of left-right
                     */
                    val replacementEntry = nodeOfInterest.bRight.fleftMost()!!
                    return if (replacementEntry == nodeOfInterest.entry)
                        /* just drop one of the duplicates */
                        FBSTNode.of(treeStub.isAcceptDuplicates(), replacementEntry, graftLeft, nodeOfInterest.bRight.bRight)
                    else {
                        val replacementCount = nodeOfInterest.fcount{ it == replacementEntry }
                        check(0 < replacementCount)
                        if ( 1 == replacementCount) {
                            val replacementNode = nodeOfInterest.ffindItem(replacementEntry)!! as FBSTNode<A, B>
                            check(replacementNode.bLeft is FBSTNil)
                            val graftRight = bstPrune(nodeOfInterest.bRight, replacementEntry).finsertt(replacementNode.bRight) as FBSTree<A,B>
                            FBSTNode.of(treeStub.isAcceptDuplicates(), replacementEntry, graftLeft, graftRight)
                        } else {
                            /* I need to move ALL replacementCount */
                            val replacementNodeFirst = nodeOfInterest.ffindItem(replacementEntry)!! as FBSTNode<A, B>
                            check(replacementNodeFirst.bLeft is FBSTNil)
                            val replacementNodeLast = nodeOfInterest.ffindLastItem(replacementEntry)!! as FBSTNode<A, B>
                            check(replacementNodeLast.bLeft is FBSTNil)
                            when (val tail = replacementNodeLast.bRight) {
                                is FBSTNil -> {
                                    val graftRight = bstPrune(nodeOfInterest.bRight, replacementEntry)
                                    FBSTNode.of(treeStub.isAcceptDuplicates(), replacementEntry, graftLeft, graftRight).finsertt(replacementNodeFirst.bRight) as FBSTNode<A,B>
                                }
                                is FBSTNode -> {
                                    val graftRight = bstPrune(nodeOfInterest.bRight, replacementEntry).finsertt(tail) as FBSTree<A,B>
                                    val prunedReplacement = bstPrune(replacementNodeFirst, tail.entry) as FBSTNode
                                    FBSTNode.of(treeStub.isAcceptDuplicates(), replacementEntry, graftLeft, graftRight).finsertt(prunedReplacement.bRight) as FBSTNode<A,B>
                                }
                                else -> throw RuntimeException("internal error, unknown ${tail::class}")
                            }
                        }
                    }
                }

                fun replace(entryOfInterest: TKVEntry<A, B>): FBSTree<A, B> = when (val nodeItemFirst = bstFind(treeStub, entryOfInterest)) {
                    is FBSTNode -> {
                        val nodeItemLast = bstFindLast(nodeItemFirst, entryOfInterest) as FBSTNode
                        val nodeOfInterest = if (atMostOne) nodeItemFirst else nodeItemLast
                        val spliced: FBSTree<A, B> = when {
                            nodeOfInterest.isLeaf() -> /* just remove */ bstPrune(treeStub, entryOfInterest).finsertt(nodeItemFirst.bLeft) as FBSTree
                            nodeItemFirst.bLeft.isEmpty() -> /* replace with right child */ addGraft(bstPrune(treeStub, nodeOfInterest.entry), nodeOfInterest.bRight)
                            nodeOfInterest.bRight.isEmpty() -> /* replace with left child */ addGraft(bstPrune(treeStub, nodeOfInterest.entry), nodeItemFirst.bLeft)
                            else -> {
                                val replacement = buildReplacement(nodeOfInterest, nodeItemFirst.bLeft)
                                val stub = bstPrune(bstPrune(treeStub, entryOfInterest), replacement.entry)
                                addGraft(stub, replacement)
                            }
                        }
                        check(deltaRemovalInvariant(spliced, treeStub, item, atMostOne))
                        spliced
                    }
                    else -> treeStub
               }

               replace(item)

        }}

        internal tailrec fun <A, B: Any> bstDeletes(treeStub: FBSTree<A, B>, items: IMCommon<TKVEntry<A,B>>): FBSTree<A, B>
        where A: Any, A: Comparable<A> {
            val (nextItem, collection) = items.fpopAndRemainder()
            return if (null == nextItem) treeStub else bstDeletes(bstDelete(treeStub, nextItem), collection)
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

        internal fun <A, B: Any> bstInsert(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>): FBSTree<A, B>
        where A: Any, A: Comparable<A> = when (treeStub) {
            is FBSTNodeGeneric -> bstInsert(treeStub, item, allowDups = true)
            is FBSTNodeUnique -> bstInsert(treeStub, item, allowDups = false)
            is FBSTGeneric -> bstInsert(treeStub, item, allowDups = true)
            is FBSTUnique -> bstInsert(treeStub, item, allowDups = false)
            else -> throw RuntimeException("internal error")
        }

        // insert item into treeStub at the correct position
        private fun <A, B: Any> bstInsert(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, allowDups: Boolean): FBSTNode<A, B>
        where A: Any, A: Comparable<A> {

            fun emplace(node: FBSTNode<A, B>, tree: Trace<A, B>): FBSTNode<A, B> {
                val isDup = tree.direction == FBTFIT.EQ
                check(if (isDup) node is FBSTNodeGeneric else true)
                val res = when {
                    allowDups -> enrichNode(node, tree)
                    isDup -> tree.treeStub
                    else -> enrichNode(node, tree)
                }
                return res
            }

            val res: FBSTNode<A, B> = when (treeStub) {
                is FBSTNodeGeneric -> reshapeWithItem(FBSTNodeGeneric.of(item), treeStub, FLNil, ::emplace)
                is FBSTNodeUnique -> if (treeStub.fcontainsKey(item.getk())) treeStub
                    else reshapeWithItem(FBSTNodeUnique.of(item), treeStub, FLNil, ::emplace)
                else -> FBSTNode.of(allowDups, item)
            }

            return res
        }

        internal fun <A, B: Any> bstInserts(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A, B>>): FBSTree<A, B>
        where A: Any, A: Comparable<A> = if (items.fempty()) treeStub else when (treeStub) {
            is FBSTNodeGeneric -> bstInserts(treeStub, items, allowDups = true)
            is FBSTGeneric -> bstInserts(FBSTNodeGeneric.of(items.fhead()!!), items.ftail())
            is FBSTNodeUnique -> bstInserts(treeStub, items, allowDups = false)
            is FBSTUnique -> bstInserts(FBSTNodeUnique.of(items.fhead()!!), items.ftail())
            else -> throw RuntimeException("internal error")
        }

        // insert items into treeStub at the correct position
        private tailrec fun <A, B: Any> bstInserts(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B>
        where A: Any, A: Comparable<A> = when (items) {
                is FLNil -> treeStub
                is FLCons -> bstInserts(bstInsert(treeStub, items.head, allowDups), items.tail, allowDups)
            }

        // the immediate parent (and all that parent's descendants) of the child having childValue
        // null if child not in tree (also, if treeStub is empty...)
        // EMPTY if child is root
        // else immediate parent
        internal fun <A, B: Any> bstParent(treeStub: FBSTree<A, B>, childItem: TKVEntry<A, B>): FBSTree<A, B>?
        where A: Any, A: Comparable<A> {

            tailrec fun go(
                stub: FBSTree<A, B>,
                items: Pair<FBSTree<A, B>, FBSTree<A, B>>): Pair<FBSTree<A, B>, FBSTree<A, B>> = when (stub) {
                is FBSTNil -> items
                is FBSTNode -> {
                    val next: Pair<FBSTree<A, B>, FBSTree<A, B>>? = when(fit(childItem, stub)) {
                        FBTFIT.EQ -> null
                        FBTFIT.LEFT -> Pair(stub, stub.bLeft)
                        FBTFIT.RIGHT -> Pair(stub, stub.bRight)
                    }
                    if (next == null) items else go(next.second, next)
                }
                else -> throw RuntimeException("internal error")
            }

            return when(treeStub) {
                is FBSTNil -> null
                is FBSTNode -> when {
                    treeStub.froot()!! == childItem -> treeStub.toEmptyFBSTree()
                    treeStub.fcontains(childItem) -> go(treeStub, Pair(emptyFBSTreeKernel, emptyFBSTreeKernel)).first
                    else -> null
                }
                else -> throw RuntimeException("internal error")
            }
        }

        // clip off the whole branch starting at, and inclusive of, a node with entry valued clipMatch
        internal fun <A, B: Any> bstPrune(treeStub: FBSTree<A, B>, clipMatch: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

            tailrec fun copy(stack: IMStack<FBSTNode<A, B>>, acc: FBSTree<A, B>): Pair<FBSTree<A, B>, IMStack<FBSTNode<A, B>>> {
                return if (stack.fempty()) Pair(acc, stack) else {
                    val (node, shortStack) = stack.fpopOrThrow()
                    val (newStack, newAcc) = when (isChildMatch(node, clipMatch, ::fit)) {
                        Pair(noLeftMatch, noRightMatch) -> {
                            val na = bstInsert(acc, node.entry)
                            val auxStack = if (node.bRight is FBSTNode) shortStack.fpush(node.bRight) else shortStack
                            val ns = if (node.bLeft is FBSTNode) auxStack.fpush(node.bLeft) else auxStack
                            Pair(ns, na)
                        }
                        Pair(haveLeftMatch,  noRightMatch) -> {
                            val na = bstInsert(acc, node.entry)
                            val ns = if (node.bRight is FBSTNode) shortStack.fpush(node.bRight) else shortStack
                            Pair(ns, na)
                        }
                        Pair(noLeftMatch,  haveRightMatch) -> {
                            val na = bstInsert(acc, node.entry)
                            val ns = if (node.bLeft is FBSTNode) shortStack.fpush(node.bLeft) else shortStack
                            Pair(ns, na)
                        }
                        else -> throw RuntimeException("impossible code path")
                    }
                    copy(newStack, newAcc)
                }
            }

            return when {
                ! treeStub.fcontains(clipMatch) -> /* nothing to prune */ treeStub
                treeStub.fempty() -> /* nothing to match */ treeStub
                else -> {
                    val clipParent = bstParent(treeStub, clipMatch)!!
                    when(clipParent) {
                        is FBSTNil -> /* root; not found was handled above */ treeStub.toEmptyFBSTree()
                        is FBSTNode -> {
                            val res: FBSTree<A, B> = copy(FStack.of(treeStub as FBSTNode), treeStub.toEmptyFBSTree()).first
                            res
                        }
                        else -> throw RuntimeException("internal error")
                    }
                }
            }
        }

        internal inline fun <reified A, reified B: Any> toArray(fbst: FBSTree<A, B>): Array<TKVEntry<A, B>> where A: Any, A: Comparable<A> =
            FTreeIterator.toArray(fbst.size, FTreeIterator(fbst))

        // =============== internals

        const val noLeftMatch = false
        const val noRightMatch = false
        const val haveLeftMatch = true
        const val haveRightMatch = true

        // exposed for testing purposes ONLY
        internal fun <A, B: Any> addGraftTestingGremlin(treeStub: FBSTree<A, B>, graft: FBSTree<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            addGraft(treeStub, graft)

        private data class Trace<A, B: Any>(val treeStub: FBSTNode<A, B>, val direction: FBTFIT) where A: Any, A: Comparable<A>

        // the sorting order
        private fun <A, B: Any> fit(a: TKVEntry<A, B>, b: FBSTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = when {
            a == b.entry -> {
                // TODO remove later (hashcode conflict assertion)
                check(a.getv().equals(b.entry.getv()))
                FBTFIT.EQ
            }
            a < b.entry -> {
                // TODO remove later (hashcode conflict assertion)
                check(!a.getv().equals(b.entry.getv()))
                FBTFIT.LEFT
            }
            else -> {
                // TODO remove later (hashcode conflict assertion)
                check(!a.getv().equals(b.entry.getv()))
                FBTFIT.RIGHT
            }
        }

        private fun <A, B: Any> fitKey(k: A, b: FBSTNode<A, B>): FBTFIT where A: Any, A: Comparable<A> = fitKeyToEntry(k, b.entry)

        private fun <A, B: Any, C: Any> isChildMatch(node: FBSTNode<A, B>, item: C, fitMode: (C, FBSTNode<A, B>) -> FBTFIT): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
            val leftChildMatch = (node.bLeft is FBSTNode) && fitMode(item, node.bLeft) == FBTFIT.EQ
            val rightChildMatch = (node.bRight is FBSTNode) && fitMode(item, node.bRight) == FBTFIT.EQ
            return Pair(leftChildMatch, rightChildMatch)
        }

        private fun <A, B: Any, C: Any> matchingChild(node: FBSTNode<A, B>, item: C, fitMode: (C, FBSTNode<A, B>) -> FBTFIT): FBSTNode<A, B>? where A: Any, A: Comparable<A> = when {
            node.bLeft is FBSTNode && fitMode(item, node.bLeft) == FBTFIT.EQ -> node.bLeft
            node.bRight is FBSTNode && fitMode(item, node.bRight) == FBTFIT.EQ -> node.bRight
            else -> null
        }

        // assert the BST property on this node
        internal fun <A, B: Any> fbtAssertNodeInvariant(n: FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> {
            if (n.bLeft is FBSTNode) check(n.isStrictly(n.bLeft) && FBTFIT.RIGHT == fitKey(n.entry.getk(), n.bLeft)) {
                "${n.bLeft::class.simpleName} left ${n.bLeft} must be smaller than its ${n::class.simpleName} parent $n"
            }
            if (n.bRight is FBSTNodeGeneric) check( n is FBSTNodeGeneric && (FBTFIT.LEFT == fitKey(n.entry.getk(), n.bRight) || FBTFIT.EQ == fitKey(n.entry.getk(), n.bRight))) {
                "${n.bRight::class.simpleName} right ${n.bRight} must be larger than or equal to its ${n::class.simpleName} parent $n"
            }
            if (n.bRight is FBSTNodeUnique) check( n is FBSTNodeUnique && (FBTFIT.LEFT == fitKey(n.entry.getk(), n.bRight))) {
                "${n.bRight::class.simpleName} right ${n.bRight} must be larger than its ${n::class.simpleName} parent $n"
            }
            return n
        }

        internal fun <A, B: Any> fbtDeepInvariant(n: FBSTNode<A, B>, kl: A = n.entry.getk()): Boolean where A: Any, A: Comparable<A> {
            fbtAssertNodeInvariant(n)
            val leftInvariant: Boolean = if (n.bLeft is FBSTNode) {
                n.bLeft.ffold(kl) { localRootKey, tkv ->
                    check (localRootKey > tkv.getk()) { "left key ${tkv.getk()} must be smaller than local root $localRootKey at $n" }
                    fbtDeepInvariant(n.bLeft)
                    localRootKey
                }
                true
            } else true
            val rightInvariant: Boolean = if (n.bRight is FBSTNode) {
                n.bRight.ffold(kl) { localRootKey, tkv ->
                    check (localRootKey <= tkv.getk()) { "right key ${tkv.getk()} must be larger than or equal to local root $localRootKey at $n" }
                    fbtDeepInvariant(n.bRight)
                    localRootKey
                }
                true
            } else true
            return leftInvariant && rightInvariant
        }

        private tailrec fun <A, B: Any, C: Any> find(treeStub: FBSTree<A, B>, item: C, fitMode: (C, FBSTNode<A, B>) -> FBTFIT): FBSTNode<A, B>?
        where A: Any, A: Comparable<A> = when (treeStub) {
            is FBSTNil -> null
            is FBSTNode -> {
                val next: FBSTree<A, B>? = when(fitMode(item, treeStub)) {
                    FBTFIT.EQ -> null
                    FBTFIT.LEFT -> treeStub.bLeft
                    FBTFIT.RIGHT -> treeStub.bRight
                }
                if (next == null) treeStub else find(next, item, fitMode)
            }
            else -> throw RuntimeException("internal error")
        }

        private tailrec fun <A, B: Any, C: Any> findLast(treeStub: FBSTree<A, B>, item: C, fitMode: (C, FBSTNode<A, B>) -> FBTFIT): FBSTNode<A, B>? where A: Any, A: Comparable<A> {

            fun checkOneDeeper(node: FBSTNode<A, B>): FBSTNode<A, B>? = when (isChildMatch(node, item, fitMode)) {
                Pair(noLeftMatch, noRightMatch) -> null
                Pair(haveLeftMatch, noRightMatch) -> node.bLeft as FBSTNode<A, B>
                Pair(noLeftMatch, haveRightMatch) -> node.bRight as FBSTNode<A, B>
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
                else -> throw RuntimeException("internal error")
            }
        }

        private fun <A, B: Any> enrichNode(node: FBSTNode<A, B>, treePath: Trace<A, B>): FBSTNode<A, B>
        where A: Any, A: Comparable<A> = when (node) {
            is FBSTNodeGeneric -> {
                check(treePath.treeStub is FBSTNodeGeneric)
                when (treePath.direction) {
                    FBTFIT.EQ -> /* EQ goes to the right */ FBSTNodeGeneric.of(
                        treePath.treeStub.entry,
                        treePath.treeStub.bLeft,
                        FBSTNodeGeneric.of(
                            treePath.treeStub.entry,
                            treePath.treeStub.toEmptyFBSTree(),
                            treePath.treeStub.bRight)
                    )
                    FBTFIT.RIGHT -> FBSTNodeGeneric.of(treePath.treeStub.entry, treePath.treeStub.bLeft, node)
                    FBTFIT.LEFT -> FBSTNodeGeneric.of(treePath.treeStub.entry, node, treePath.treeStub.bRight)
                }
            }
            is FBSTNodeUnique -> {
                check(treePath.treeStub is FBSTNodeUnique)
                when (treePath.direction) {
                    FBTFIT.EQ -> throw RuntimeException("internal error") // treePath.treeStub
                    FBTFIT.RIGHT -> FBSTNodeUnique.of(treePath.treeStub.entry, treePath.treeStub.bLeft, node)
                    FBTFIT.LEFT -> FBSTNodeUnique.of(treePath.treeStub.entry, node, treePath.treeStub.bRight)
                }
            }
            else -> throw RuntimeException("internal error")
        }

        // Takes an existing tree stub ("descent"), and a new node ("item").
        // Rebuilds a (new) tree with "item" placed in its position, according
        // to the tree shaping criteria established with "reshape"
        private tailrec fun <A, B: Any> reshapeWithItem(
            item: FBSTNode<A, B>,
            descent: FBSTNode<A, B>,
            trace: FList<Trace<A, B>>,
            reshape: (FBSTNode<A, B>, Trace<A, B>) -> FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> {
                check(item.isAcceptDuplicates() == descent.isAcceptDuplicates())
                // which way do I go to find where item fits?
                val direction = fitKey(item.entry.getk(), descent)
                return when (val nextCheckPoint = descent.branch(direction)) {
                    is FBSTNil -> /* bumper, end of descent */ {
                        val last = Trace(descent, direction)
                        val breadCrumbs = FLCons(last, trace)
                        // retrace the breadcrumbs, reshaping; here, item is in its place
                        breadCrumbs.ffoldLeft(item, reshape)
                    }
                    is FBSTNode -> /* while descending, keep track of the descent path */{
                        val breadCrumb = Trace(descent, direction)
                        val breadCrumbs = FLCons(breadCrumb, trace)
                        reshapeWithItem(item, nextCheckPoint, breadCrumbs, reshape)
                    }
                    else -> throw RuntimeException("internal error, unknown ${nextCheckPoint::class}")
                }
            }


        // treeStub is a tree from which a node has been removed; graft is the only child of the removed node
        // NOTE: reinserting graft into treeStub MUST ALWAYS maintain the BST property (see ::fit for details)
        // there is a tight relationship between treeStub and graft -- this is not a general purpose utility
        private fun <A, B: Any> addGraft(treeStub: FBSTree<A, B>, graft: FBSTree<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> = when {
            treeStub.fempty() && graft.fempty() -> treeStub
            graft.fempty() -> treeStub
            treeStub.fempty() && (treeStub.isAcceptDuplicates() == graft.isAcceptDuplicates()) -> graft
            treeStub.fempty() -> if (treeStub.isAcceptDuplicates()) graft.toGeneric() else graft.toUnique()
            else -> {
                val aux = if (treeStub.isAcceptDuplicates()) graft.toGeneric() else graft.toUnique()
                reshapeWithItem(aux as FBSTNode, treeStub as FBSTNode, FLNil, ::enrichNode)
            }
        }

        private tailrec fun <A, B: Any> mergeAppender(t1: FBSTree<A, B>, t2: FBSTree<A, B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = when {
            t1 is FBSTNil -> t2
            t2 is FBSTNil -> t1
            t1.size < t2.size && (t1.isAcceptDuplicates() == t2.isAcceptDuplicates()) -> {
                val (entry, stub) = t1.fpopAndRemainder()
                mergeAppender(bstInsert(t2, entry!!), stub, allowDups)
            }
            else -> {
                val (entry, stub) = t2.fpopAndRemainder()
                mergeAppender(t1.finsert(entry!!), stub, allowDups)
            }
        }

        private fun <A, B: Any> appender(withDups: Boolean): (FBSTree<A, B>, TKVEntry<A, B>) -> FBSTree<A, B> where A: Any, A: Comparable<A> =
            { treeStub: FBSTree<A, B>, item: TKVEntry<A, B> -> bstInsert(treeStub, item, allowDups = withDups)}

        private fun <B: Any> appenderIntKey(withDups: Boolean): (FBSTree<Int, B>, B) -> FBSTree<Int, B> =
            { treeStub: FBSTree<Int, B>, item: B -> bstInsert(treeStub, item.toIAEntry(), allowDups = withDups)}

        private fun <B: Any> appenderStrKey(withDups: Boolean): (FBSTree<String, B>, B) -> FBSTree<String, B> =
            { treeStub: FBSTree<String, B>, item: B -> bstInsert(treeStub, item.toSAEntry(), allowDups = withDups)}

        private fun <A, B: Any> toFKSetImpl(t: FBSTree<A, B>, kType: RestrictedKeyType<@UnsafeVariance A>?): FKSet<A, B>? where A: Any, A: Comparable<A> = when {
            t.isAcceptDuplicates() -> null
            t.fempty() -> kType?.let { emptyIMKSet(it) }
            null == kType -> t.frestrictedKey()?.let { toFKSetImpl(t, it) }
            t.frestrictedKey() == kType -> ofBody(t.toFRBTree() as FRBTNode)
            (kType is IntKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (ofFIKSBody(t.toFRBTree() as FRBTree<Int,A>) as FKSet<A, B>)
            (kType is StrKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (ofFSKSBody(t.toFRBTree() as FRBTree<String,A>) as FKSet<A, B>)
            else -> null
        }

        private fun <K, A, B:Any> toFBSTreeImpl(t: FBSTree<A, B>, kType: RestrictedKeyType<K>): FBSTree<K, B>? where A: Any, A: Comparable<A>, K: Any, K: Comparable<K> = when {
            t.isAcceptDuplicates() -> null
            t.fempty() -> @Suppress("UNCHECKED_CAST") (t as FBSTree<K, B>)
            t.frestrictedKey() == kType -> @Suppress("UNCHECKED_CAST") (t as FBSTree<K, B>)
            (kType is IntKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (t as FBSTree<K, B>)
            (kType is StrKeyType) && (t.frestrictedKey()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (t as FBSTree<K, B>)
            else ->  when (kType) {
                is IntKeyType ->   {
                    val res: FBSTree<Int, B> = t.ffold(nul(true)) { acc, tkv -> acc.finsert(tkv.getv().toIAEntry()) }
                    @Suppress("UNCHECKED_CAST") (res as FBSTree<K, B>)
                }
                is StrKeyType -> {
                    val res: FBSTree<String, B> = t.ffold(nul(true)) { acc, tkv -> acc.finsert(tkv.getv().toSAEntry()) }
                    @Suppress("UNCHECKED_CAST") (res as FBSTree<K, B>)
                }
                is SymKeyType -> if (kType.kc != t.froot()!!.getvKc()) null else {
                    t.ffold(nul<K, B>()) { acc, tkv ->
                        val k = @Suppress("UNCHECKED_CAST") (tkv.getv() as K)
                        val entry = TKVEntry.ofkv(k, tkv.getv())
                        acc.finsert(entry)
                    }
                }
                is DeratedCustomKeyType -> kType.specialize<K>()?.let { toFBSTreeImpl(t, it) }
            }
        }

    }
}

internal abstract class FBSTNil(): FBSTree<Nothing, Nothing>() {
    override fun toString(): String = "FBSTNil"
    override fun hashCode(): Int = toString().hashCode()
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FBSTGeneric -> true
        other is FBSTUnique -> true
        other is IMBTree<*, *> -> other.fempty()
        else -> false
    }
}

internal class FBSTGeneric private constructor (private val krn:FBSTree<Nothing, Nothing>): FBSTNil() {
    companion object {
        val empty = FBSTGeneric(emptyFBSTreeKernel)
    }
}

internal class FBSTUnique private constructor(private val krn:FBSTree<Nothing, Nothing>): FBSTNil() {
    companion object {
        val empty = FBSTUnique(emptyFBSTreeKernel)
    }
}

private object emptyFBSTreeKernel: FBSTree<Nothing, Nothing>()

internal abstract class FBSTNode<out A, out B: Any> protected constructor (
    val entry: TKVEntry<A, B>,
    val bLeft: FBSTree<A, B>,
    val bRight: FBSTree<A, B>
): FBSTree<A, B>() where A: Any, A: Comparable<@UnsafeVariance A> {

    internal fun branch(position: FBTFIT): FBSTree<A, B> = when (position) {
        FBTFIT.LEFT -> bLeft
        FBTFIT.RIGHT, FBTFIT.EQ -> bRight // EQ goes to the right
    }

    internal fun isLeaf(): Boolean = bLeft is FBSTNil && bRight is FBSTNil

    val show: String by lazy {

        val leftMark = "<"
        val rightMark = ">"

        tailrec fun <C> unwindQueue(
            queue: FQueue< Pair<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>, String> >,
            acc: C,
            accrue: (FQueue< Pair<FBSTNode<A, B>, String> >, C) -> Pair<C, FQueue< Pair<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>, String >>>
        ): C = if (queue.isEmpty()) acc else {
                val (newAcc, newQueue) = accrue(queue, acc)
                unwindQueue(newQueue, newAcc, accrue)
            }

        val sz: String = when (val ns = size) {
            0 -> ""
            else -> "{$ns}"
        }
        // this.ffold("${FBSTree::class.simpleName}@$sz:") { acc, tkv -> "$acc($tkv)" }

        fun asString(): String {

            fun accrue(q: FQueue<Pair<FBSTNode<A, B>, String>>, acc: FList<String>): Pair<FList<String>, FQueue< Pair<FBSTNode<A, B>, String>>> {
                val (nodelor, dequeued) = q.fdequeueOrThrow()

                val maxl = nodelor.first.bLeft.fmaxDepth()
                val maxr = nodelor.first.bRight.fmaxDepth()
                val depthMsg = if (nodelor.first.isLeaf()) "(-|-)" else "(${if (0==maxl) '-' else maxl}|${if (0==maxr) '-' else maxr})"
                val showEntry = "{${nodelor.first.entry}:${nodelor.second}$depthMsg}"

                val newAcc = acc.fprepend(showEntry)
                val lq = if (nodelor.first.bLeft is FBSTNil) dequeued
                else dequeued.fenqueue(Pair(nodelor.first.bLeft as FBSTNode,"[$leftMark$maxl]"))
                val nq = if (nodelor.first.bRight is FBSTNil) lq
                else lq.fenqueue(Pair(nodelor.first.bRight as FBSTNode, "[$rightMark$maxr]"))
                return Pair(newAcc, nq)
            }

            val res = unwindQueue(emptyIMQueue<Pair<FBSTNode<A, B>, String>>().fenqueue(Pair(this, "")), FLNil, ::accrue)
            return res.ffoldLeft(""){ acc, item -> item + acc }
        }

        "${FBSTree::class.simpleName}@$sz:${asString()}"

    }

    override fun toString(): String = show

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FBSTNode<*, *> -> when {
            other.isEmpty() -> false
            entry.strictlyNot(other.entry.untype()) -> false
            else -> @Suppress("UNCHECKED_CAST") IMBTreeEqual2 (this, other as FBSTNode<A, B>)
        }
        other is IMBTree<*, *> -> when {
            other.fempty() -> false
            entry.strictlyNot(other.froot()!!.untype()) -> false
            else -> @Suppress("UNCHECKED_CAST") IMBTreeEqual2 (this, other as IMBTree<A, B>)
        }
        else -> false
    }

    val hash:Int by lazy {
        val seed: Int = when (val rk = this.froot()?.getk()) {
            null -> 9161
            is Int -> 1
            is Long -> 1
            else -> rk.hashCode()
        }
        this.ffold(seed) { acc, tkv -> when(val k = tkv.getk()) {
                is Int -> 31 * acc + k
                is Long -> 31 * acc + k.toInt()
                else -> 31 * acc + k.hashCode()
            }
        }
    }

    override fun hashCode(): Int = hash

    private val fbsKeyKClass: KClass<@UnsafeVariance A> by lazy { @Suppress("UNCHECKED_CAST") (froot()!!.getkKc() as KClass<A>) }

    internal val fbsRKeyType: RestrictedKeyType<A>? by lazy { when(froot()!!.getk()) {
        is Int -> (@Suppress("UNCHECKED_CAST") (IntKeyType as RestrictedKeyType<A>))
        is String -> (@Suppress("UNCHECKED_CAST") (StrKeyType as RestrictedKeyType<A>))
        else -> if (froot()!!.getkKc() == froot()!!.getvKc()) SymKeyType(fbsKeyKClass) else null
    }}

    companion object {
        fun <A, B: Any> emptyOf(allowDups: Boolean): FBSTNil = if (allowDups) FBSTGeneric.empty else FBSTUnique.empty
        fun <A, B: Any> of(allowDups: Boolean, entry: TKVEntry<A, B>, bLeft: FBSTree<A, B> = emptyOf<A,B>(allowDups), bRight: FBSTree<A, B> = emptyOf<A,B>(allowDups)): FBSTNode<A,B> where A: Any, A: Comparable<A> =
            if (allowDups) FBSTNodeGeneric.of(entry, bLeft, bRight) else FBSTNodeUnique.of(entry, bLeft, bRight) // fbtAssertNodeInvariant(FBSTNode(entry, bLeft, bRight))
        fun <A, B: Any> hashCode(n: FBSTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hashCode()
    }
}

internal class FBSTNodeGeneric<out A, out B: Any> private constructor (
    entry: TKVEntry<A, B>,
    bLeft: FBSTree<A, B>,
    bRight: FBSTree<A, B>
): FBSTNode<A, B>(entry, bLeft, bRight) where A: Any, A: Comparable<@UnsafeVariance A> {
    companion object {
        fun <A, B: Any> of(entry: TKVEntry<A, B>, bLeft: FBSTree<A, B> = FBSTGeneric.empty, bRight: FBSTree<A, B> = FBSTGeneric.empty): FBSTNode<A,B> where A: Any, A: Comparable<A> =
            //FBSTNodeGeneric(entry, bLeft, bRight)
            fbtAssertNodeInvariant(FBSTNodeGeneric(entry, bLeft, bRight))
    }
}

internal class FBSTNodeUnique<out A, out B: Any> private constructor (
    entry: TKVEntry<A, B>,
    bLeft: FBSTree<A, B>,
    bRight: FBSTree<A, B>
): FBSTNode<A, B>(entry, bLeft, bRight) where A: Any, A: Comparable<@UnsafeVariance A> {
    companion object {
        fun <A, B: Any> of(entry: TKVEntry<A, B>, bLeft: FBSTree<A, B> = FBSTUnique.empty, bRight: FBSTree<A, B> = FBSTUnique.empty): FBSTNode<A,B> where A: Any, A: Comparable<A> =
            // FBSTNodeUnique(entry, bLeft, bRight)
            fbtAssertNodeInvariant(FBSTNodeUnique(entry, bLeft, bRight))
    }
}

private fun <A, B: Any> deltaRemovalInvariant(outcome: FBSTree<A, B>, initialState: FBSTree<A, B>, deltaItem: TKVEntry<A, B>, atMostOne: Boolean): Boolean
where A: Any, A: Comparable<A> {
    val deltaCount = if (atMostOne) 1 else initialState.fcount { it == deltaItem }
    return if ((outcome.size+deltaCount) == initialState.size) true else {
        val maybeRootMsg = if (initialState.froot()!!.equals(deltaItem)) " (which was root)" else ""
        println("${ initialState.size - outcome.size } ($deltaCount expected) missing after deleting $deltaItem$maybeRootMsg from tree of ${initialState.size}")
        initialState.ffindItem(deltaItem)?.let { it: FBSTree<A, B> -> println("found at $it") } ?: print("WHICH SHOULD NOT BE MISSING")
        val inParent = initialState.fparentOf(deltaItem)
        inParent?.let { it: FBSTree<A, B> -> println("with parent $it") } ?: print("with missing parent")
        inParent?.let { it.froot()?.let { asEntry ->
            outcome.ffindItem( asEntry )?.let {
                println("(==> now  $it)")
            } ?: println("(==> now  MISSING)")
        } ?: println("which after removal is now empty") }
        println("the removed items:")
        initialState.forEach{
            if (!outcome.contains(it)) println(it)
        }
        false
    }
}
