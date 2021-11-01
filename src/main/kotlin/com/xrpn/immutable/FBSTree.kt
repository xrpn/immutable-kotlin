package com.xrpn.immutable

import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.*
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
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

    override fun fdropAll(items: IMCollection<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FBSTree<A, B> = when (items) {
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

        return ffold(nul(), f4ffilter)
    }

    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): FBSTree<A, B> =
        ffilter { !isMatch(it) }

    override fun ffindAny(isMatch: (TKVEntry<A, B>) -> Boolean): TKVEntry<A, B>? {

        val f: (previous: Pair<Boolean, TKVEntry<A, B>>, entry: TKVEntry<A, B>) -> Pair<Boolean, TKVEntry<A, B>> = { previous, entry -> if (previous.first) previous else Pair(isMatch(entry), entry) }
        fun accrueForFold(stack: IMStack<FBSTNode<A, B>>, acc: Pair<Boolean, TKVEntry<A, B>>): Pair<Pair<Boolean, TKVEntry<A, B>>, IMStack<FBSTNode<A, B>>> =
            if (acc.first) Pair(acc, FStack.emptyIMStack()) else {
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
            val kc: KClass<out B>? = maybeNotEmpty?.let { it.getvKc() }
            kc?.let { valueKClass ->
                val ucKc = SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>()
                null == ffindAny { tkv -> !FT.entryStrictness(tkv, tkvClass!!, valueKClass, ucKc) }
            } ?: /* nested, but all empty */ run {
                val aux = fpick()!!::class
                fall {
                    check(it.toUCon()?.let { uc -> uc.isEmpty() } ?: false )
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

    override fun ffilterKey(isMatch: (A) -> Boolean): FBSTree<A, B> {

        val f4ffilterKey: (acc: FBSTree<A, B>, item: TKVEntry<A, B>) -> FBSTree<A, B> =
            { acc, item -> if (isMatch(item.getk())) bstInsert(acc, item, allowDups = true) else acc }

        return ffold(nul(), f4ffilterKey)
    }

    override fun ffilterKeyNot(isMatch: (A) -> Boolean): FBSTree<A, B> =
        ffilterKey { !isMatch(it) }

    override fun ffilterValue(isMatch: (B) -> Boolean): FBSTree<A, B> {

        val f4ffilterValue: (acc: FBSTree<A, B>, item: TKVEntry<A, B>) -> FBSTree<A, B> =
            { acc, item -> if (isMatch(item.getv())) bstInsert(acc, item, allowDups = true) else acc }

        return ffold(nul(), f4ffilterValue)
    }

    override fun ffilterValueNot(isMatch: (B) -> Boolean): FBSTree<A, B> =
        ffilterValue { !isMatch(it) }

    override fun ffindAnyValue(isMatch: (B) -> Boolean): B? {
        TODO("Not yet implemented")
    }

    override fun fget(key: @UnsafeVariance A): B? = when (this) {
        is FBSTNil -> null
        is FBSTNode -> ffindValueOfKey(key)
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

    override fun toIMRSet(kType: RestrictedKeyType<@UnsafeVariance A>?): FKSet<A, B>? = toFKSetImpl(this, kType)

    override fun <K> toIMBTree(kType: RestrictedKeyType<@UnsafeVariance K>): IMBTree<K, B>? where K: Any, K: Comparable<K> =
        toFBSTreeImpl(this, kType)

    override fun toIMMap(): IMMap<A, B> = ofFKMapBody(this.toFRBTree())

    override fun copy(): FBSTree<A, B> = this.ffold(nul()) { acc, tkv -> acc.finsert(tkv) }

    // =========== traversable

    override fun preorder(reverse: Boolean): FList<TKVEntry<A, B>> {
        val fl = this.ffold(FList.emptyIMList<TKVEntry<A,B>>()) { acc, item -> FLCons(item, acc) }
        return if(reverse) fl else fl.freverse()
    }

    override fun inorder(reverse: Boolean): FList<TKVEntry<A, B>> {

        tailrec fun inoLeftDescent(t: FBSTree<A, B>, stack: IMStack<FBSTNode<A, B>>): IMStack<FBSTNode<A, B>> =
            when (t) {
                is FBSTNil -> stack
                is FBSTNode -> inoLeftDescent(t.bLeft, stack.fpush(t))
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
            true -> unwindQueue(FQueue.emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue(this as FBSTNode), FLNil, ::accrue)
            false -> unwindQueue(FQueue.emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue(this as FBSTNode), FLNil, ::accrue).freverse()
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

    override fun fkeyType(): RestrictedKeyType<A>? = when (this) {
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
                }
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
                }
            }

        return rightDescent(this)
    }

    override fun froot(): TKVEntry<A,B>? = when(this) {
        is FBSTNil -> null
        is FBSTNode -> this.entry
    }

    override fun fAND(items: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fOR(items: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fXOR(items: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> {
        TODO("Not yet implemented")
    }

    // =========== grouping

    override fun <C> fgroupBy(f: (TKVEntry<A, B>) -> C): IMMap<C, FBSTree<A, B>> where C: Any, C: Comparable<C> =
        TODO() //	A map of collections created by the function f

    override fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair</* true */ FBSTree<A, B>, /* false */ FBSTree<A, B>> {

        fun f4fpartition(acc: Pair<FBSTree<A, B>, FBSTree<A, B>>, current: (TKVEntry<A, B>)): Pair<FBSTree<A, B>, FBSTree<A, B>> =
            if (isMatch(current)) Pair(bstInsert(acc.first, current, allowDups = true), acc.second)
            else Pair(acc.first, bstInsert(acc.second, current, allowDups = true))

        return ffold(Pair(nul(), nul()), ::f4fpartition)
    }

    override fun fpopAndRemainder(): Pair<TKVEntry<A,B>?, FBSTree<A, B>> {
        val pop: TKVEntry<A,B>? = this.fpeek()
        // computing the remainder can be very expensive; if traversing
        // the full tree, .inorder() or .forEach() may be cheaper
        val remainder: FBSTree<A, B> = pop?.let { this.fdropItem(it) } ?: FBSTNil
        return Pair(pop, remainder)
    }

    // returns the maximum path length from the root of a tree to any node.

    val maxBstDepth: Int by lazy {
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
            val newQueue = harvestThisLevel(q, FQueue.emptyIMQueue())
            return Pair(newDepth, newQueue)
        }

        if (this.fempty()) 0
        else unwindQueue(FQueue.emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue(this as FBSTNode), 0, ::accrue)
    }

    override fun fmaxDepth(): Int = maxBstDepth

    val minBstDepth: Int by lazy {

        fun accrue(q: FQueue<FBSTNode<A, B>>, depth: Int): Pair<Int, FQueue<FBSTNode<A, B>>> {

            tailrec fun harvestThisLevel(q: FQueue<FBSTNode<A, B>>, r: FQueue<FBSTNode<A, B>>): FQueue<FBSTNode<A, B>> {
                val (node, shortQueue) = q.fdequeueOrThrow()
                return if (node.isLeaf()) /* early termination at this level */ FQueue.emptyIMQueue() else {
                    // add non-nul children of node to queue
                    val q1 = if (node.bLeft is FBSTNil) r else r.fenqueue(node.bLeft as FBSTNode)
                    val q2 = if (node.bRight is FBSTNil) q1 else q1.fenqueue(node.bRight as FBSTNode)
                    if (shortQueue.isEmpty()) /* we are done with this level */ q2
                    else /* more nodes on this level, keep harvesting */ harvestThisLevel(shortQueue, q2)
                }
            }

            val newDepth = depth + 1
            val newQueue = harvestThisLevel(q, FQueue.emptyIMQueue())
            return Pair(newDepth, newQueue)
        }

        if (this.fempty()) 0
        else unwindQueue(FQueue.emptyIMQueue<FBSTNode<@UnsafeVariance A, @UnsafeVariance B>>().fenqueue (this as FBSTNode), 0, ::accrue)
    }

    // returns the minimum path length from the root of a tree to the first node that is a leaf.
    override fun fminDepth(): Int = minBstDepth

    // =========== transforming

    override fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): FBSTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> =  // 	When working with sequences, it works like map followed by flatten
        this.ffold(nul()) { acc, tkv -> mergeAppender(acc, (f(tkv) as FBSTree<C, D>), allowDups = false) }

    override fun <C, D: Any> fflatMapDup(allowDups: Boolean, f: (TKVEntry<A, B>) -> IMBTree<C, D>): FBSTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> =  // 	When working with sequences, it works like map followed by flatten
        this.ffold(nul()) { acc, tkv -> mergeAppender(acc, (f(tkv) as FBSTree<C, D>), allowDups = true) }

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

    override fun <C, D: Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(nul()) { acc, tkv -> acc.finsert(f(tkv)) }

    override fun <C, D: Any> fmapDup(allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(nul()) { acc, tkv -> acc.finsertDup(f(tkv), allowDups) }

    override fun freduce(f: (acc: TKVEntry<A, B>, TKVEntry<A, B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): TKVEntry<A, B>? = when(this) {  // 	“Reduce” the elements of the list using the binary operator o, going from left to right
        is FBSTNil -> null
        is FBSTNode -> {
            val (seedTkv, stub) = this.fpopAndRemainder()
            stub.ffold(seedTkv!!){ acc, tkv -> f(acc, tkv) }
        }
    }

    // =========== altering

    override fun finsert(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): FBSTree<A, B> =
        bstInsert(this, item, allowDups = false)

    override fun finsertDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>, allowDups: Boolean): FBSTree<A, B> =
        bstInsert(this, item, allowDups)

    override fun finserts(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): FBSTree<A, B> =
        bstInserts(this, items as FList<TKVEntry<A, B>>, allowDups = false)

    override fun finsertsDup(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>, allowDups: Boolean): FBSTree<A, B> =
        bstInserts(this, items as FList<TKVEntry<A, B>>, allowDups)

    // =========== internals

    private fun visit(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> FLCons(t.entry, acc)
    }

    private fun <C> visitForFold(t: FBSTree<A, B>, acc: C, f: (acc: C, TKVEntry<A, B>) -> C): C = when(t) {
        is FBSTNil -> acc
        is FBSTNode -> f(acc, t.entry)
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

    companion object: IMBTreeCompanion {

        const val NOT_FOUND = -1

        fun <A, B: Any> nul(): FBSTree<A, B> where A: Any, A: Comparable<A> = FBSTNil

        override fun <A, B : Any> emptyIMBTree(): FBSTree<A, B> where A: Any,  A : Comparable<A> = nul()

        // ===============

        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator(), false)
        override fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items.iterator(), allowDups)
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>): FBSTree<A, B> where A: Any, A: Comparable<A> = of(items, false)
        override fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul()
            items.forEach{ res = bstInsert(res, it, allowDups) }
            return res
        }
        override fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>): FBSTree<A, B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), appender(false))
        override fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> =
            items.ffoldLeft(nul(), appender(allowDups))

        // ===============

        override fun <A, B: Any> ofc(cc: Comparator<A>, vararg items: TKVEntry<A,B>): FBSTree<A, B> where A: Any, A: Comparable<A> = ofc(cc, items.iterator(), false)
        override fun <A, B: Any> ofc(cc: Comparator<A>, vararg items: TKVEntry<A,B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = ofc(cc, items.iterator(), allowDups)
        override fun <A, B: Any> ofc(cc: Comparator<A>, items: Iterator<TKVEntry<A, B>>): FBSTree<A, B> where A: Any, A: Comparable<A> = ofc(cc, items, false)
        override fun <A, B: Any> ofc(cc: Comparator<A>, items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul()
            items.forEach { res = bstInsert(res, TKVEntry.ofkvc(it.getk(), it.getv(), cc), allowDups) }
            return res
        }

        // =================

        override fun <B : Any> ofvi(vararg items: B): FBSTree<Int, B> = ofvi(items.iterator(), false)
        override fun <B : Any> ofvi(vararg items: B, allowDups: Boolean): FBSTree<Int, B> = ofvi(items.iterator(), allowDups)
        override fun <B : Any> ofvi(items: Iterator<B>): FBSTree<Int, B> = ofvi(items, false)
        override fun <B : Any> ofvi(items: Iterator<B>, allowDups: Boolean): FBSTree<Int, B> {
            var res: FBSTree<Int, B> = nul()
            items.forEach { res = bstInsert(res, TKVEntry.ofIntKey(it), allowDups) }
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
            items.forEach { res = bstInsert(res, TKVEntry.ofStrKey(it), allowDups) }
            return res
        }

        // ===============

        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C> = ofMap(items, false, f)
        override fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): FBSTree<C, D> where A: Any, A : Comparable<A>, C: Any, C : Comparable<C> {
            var res: FBSTree<C, D> = nul()
            items.forEach { res = bstInsert(res, f(it), allowDups) }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, f: (B) -> C): FBSTree<Int, C> = ofviMap(items, false, f)
        override fun <B : Any, C : Any> ofviMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<Int, C> {
            var res: FBSTree<Int, C> = nul()
            items.forEach { res = bstInsert(res, TKVEntry.ofIntKey(f(it)), allowDups) }
            return res
        }

        // ===============

        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, f: (B) -> C): FBSTree<String, C> = ofvsMap(items, false, f)
        override fun <B : Any, C : Any> ofvsMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): FBSTree<String, C> {
            var res: FBSTree<String, C> = nul()
            items.forEach { res = bstInsert(res, TKVEntry.ofStrKey(f(it)), allowDups) }
            return res
        }

        // =================

//        override fun <A, B : Any> Collection<TKVEntry<A, B>>.toIMBTree(): FBSTree<A, B> where A: Any, A : Comparable<A> =
//            if (this.isEmpty()) nul() else when(this) {
//                is FBSTree<*, *> -> this as FBSTree<A, B>
//                is FRBTree<*, *> -> @Suppress("UNCHECKED_CAST") (this as IMBTree<A, B>).ffold(nul()) { acc, tkv -> acc.finsert(tkv) }
//                is IMKSet<*,*> -> this.toIMBTree().ffold(nul()) { acc: FBSTree<A, B>, item -> acc.finsert(item) }
//                is List<*> -> of(this.iterator(), allowDups = true)
//                is Set<*> -> of(this.iterator())
//                else -> /* TODO this would be interesting */ throw RuntimeException(this::class.simpleName)
//            }

        override fun <A, B: Any> Map<A, B>.toIMBTree(): FBSTree<A, B> where A: Any, A: Comparable<A> {
            var res: FBSTree<A, B> = nul()
            for (entry in this) { res = res.finsertDup(TKVEntry.ofme(entry), allowDups = true) }
            return res
        }

        // =============== top level type-specific implementation

        // delete entry from treeStub.
        internal fun <A, B: Any> bstDelete(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, atMostOne: Boolean = false): FBSTree<A, B>
        where A: Any, A: Comparable<A> {

            fun buildReplacement(
                eureka: FBSTNode<A, B>,
                current: FBSTree<A, B>
            ): Pair<FBSTree<A, B>, FBSTNode<A, B>> {
                val newValue = /* find replacement value */ eureka.bRight.fleftMost()!!
                val graftRight =  /* build new right subtree */
                    if ((eureka.bRight as FBSTNode).entry == newValue) eureka.bRight.bRight
                    else {
                        val newValueRoot: FBSTree<A, B> = treeStub.ffindItem(newValue)!!
                        val aux = if (newValueRoot.frightMost()?.let { it.equals(newValue) } == true) bstPrune(eureka.bRight, newValue)
                        else bstPrune(eureka.bRight, newValue).finsertt((newValueRoot as FBSTNode).bRight)
                        aux as FBSTree
                    }
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
                        when (val first = bstFind(current, item)) {
                            is FBSTNode -> {
                                val last = bstFindLast(first, item) as FBSTNode
                                val noDuplicates = last === first || atMostOne
                                val found = if (atMostOne) first else last
                                when {
                                    found.isLeaf() -> if (noDuplicates) /* just remove */ bstPrune(treeStub, item) else {
                                        when(fitKey(item.getk(), first)) {
                                            FBTFIT.LEFT -> when (first.bRight) {
                                                is FBSTNil -> bstPrune(treeStub, item)
                                                is FBSTNode -> addGraft(bstPrune(treeStub, item), first.bRight)
                                            }
                                            FBTFIT.RIGHT, FBTFIT.EQ -> /* EQ goes to the right */ when (first.bLeft) {
                                                is FBSTNil -> bstPrune(treeStub, item)
                                                is FBSTNode -> addGraft(bstPrune(treeStub, item), first.bLeft)
                                            }
                                        }
                                    }
                                    found.bLeft.isEmpty() -> /* replace with right child */
                                        if (noDuplicates) addGraft(bstPrune(treeStub, item),found.bRight)
                                        else when(found.bRight) {
                                            is FBSTNil -> FBSTNil
                                            is FBSTNode -> {
                                                // TODO this is suboptimal, two-child replacement may not be necessary
                                                val (stub, replacement) = buildReplacement(found, current)
                                                addGraft(stub, replacement)
                                            }
                                        }
                                    found.bRight.isEmpty() -> /* replace with left child */
                                        if (noDuplicates) addGraft(bstPrune(treeStub, item),found.bLeft)
                                        else when (found.bLeft) {
                                            is FBSTNil -> FBSTNil
                                            is FBSTNode -> {
                                                // TODO this is suboptimal, two-child replacement may not be necessary
                                                val (stub, replacement) = buildReplacement(found, current)
                                                addGraft(stub, replacement)
                                            }
                                        }
                                    else /* neither is empty */ -> {
                                        val (stub, replacement) = buildReplacement(found, current)
                                        addGraft(stub, replacement)
                                    }
                                }
                            }
                            else -> {
                                val next: FBSTree<A, B> = when (fit(item, current)){
                                    FBTFIT.LEFT -> current.bLeft
                                    FBTFIT.RIGHT -> current.bRight
                                    else -> throw RuntimeException("impossible path")
                                }
                                splice(item, next, trace)
                            }
                        }
                    }
                }

            return if (!treeStub.fcontains(item)) treeStub
            else {
                val res: FBSTree<A, B> = splice(item, treeStub, FLNil)
                val pred = deltaRemovalInvariant(res, treeStub, item, 1)
                check(!atMostOne || pred)
                res
            }
        }

        internal tailrec fun <A, B: Any> bstDeletes(treeStub: FBSTree<A, B>, items: IMCollection<TKVEntry<A,B>>): FBSTree<A, B>
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

        // insert item into treeStub at the correct position
        internal fun <A, B: Any> bstInsert(treeStub: FBSTree<A, B>, item: TKVEntry<A, B>, allowDups: Boolean = false): FBSTNode<A, B>
        where A: Any, A: Comparable<A> {

            fun emplace(node: FBSTNode<A, B>, tree: Trace<A, B>): FBSTNode<A, B> {
                val isDup = tree.direction == FBTFIT.EQ
                val res = when {
                    allowDups -> enrichNode(node, tree)
                    isDup -> tree.treeStub
                    else -> enrichNode(node, tree)
                }
                return res
            }

            val res = when (treeStub) {
                is FBSTNil -> FBSTNode(item)
                is FBSTNode -> reshapeWithItem(FBSTNode(item), treeStub, FLNil, ::emplace)
            }

            return res
        }

        // insert items into treeStub at the correct position
        internal tailrec fun <A, B: Any> bstInserts(treeStub: FBSTree<A, B>, items: FList<TKVEntry<A, B>>, allowDups: Boolean = false): FBSTree<A, B>
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

            // TODO common code

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
            }

            return when(treeStub) {
                is FBSTNil -> null
                is FBSTNode -> when {
                    treeStub.froot()!! == childItem -> nul()
                    treeStub.fcontains(childItem) -> go(treeStub, Pair(FBSTNil, FBSTNil)).first
                    else -> null
                }
            }
        }

        // clip off the whole branch starting at, and inclusive of, a node with entry valued clipMatch
        internal fun <A, B: Any> bstPrune(treeStub: FBSTree<A, B>, clipMatch: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> {

            tailrec fun copy(stack: IMStack<FBSTNode<A, B>>, acc: FBSTree<A, B>): Pair<FBSTree<A, B>, IMStack<FBSTNode<A, B>>> {
                return if (stack.fempty()) Pair(acc, stack) else {
                    val (node, shortStack) = stack.fpopOrThrow()
                    val (newStack, newAcc) = when (isChildMatch(node, clipMatch, ::fit)) {
                        Pair(noLeftMatch, noRightMatch) -> {
                            val na = bstInsert(acc, node.entry, allowDups = true)
                            val auxStack = if (node.bRight is FBSTNode) shortStack.fpush(node.bRight) else shortStack
                            val ns = if (node.bLeft is FBSTNode) auxStack.fpush(node.bLeft) else auxStack
                            Pair(ns, na)
                        }
                        Pair(haveLeftMatch,  noRightMatch) -> {
                            val na = bstInsert(acc, node.entry, allowDups = true)
                            val ns = if (node.bRight is FBSTNode) shortStack.fpush(node.bRight) else shortStack
                            Pair(ns, na)
                        }
                        Pair(noLeftMatch,  haveRightMatch) -> {
                            val na = bstInsert(acc, node.entry, allowDups = true)
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
                treeStub.fempty() -> /* nothing to match */ FBSTNil
                else -> {
                    val clipParent = bstParent(treeStub, clipMatch)!!
                    when(clipParent) {
                        is FBSTNil -> /* root; not found was handled above */ FBSTNil
                        is FBSTNode -> {
                            val res: FBSTree<A, B> = copy(FStack.of(treeStub as FBSTNode), FBSTNil).first
                            val pred = deltaRemovalInvariant(res, treeStub, clipMatch, matchingChild(clipParent, clipMatch, ::fit)!!.size)
                            check(pred)
                            res
                        }
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
        internal fun <A, B: Any> fbtAssert(n: FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> {
            if (n.bLeft is FBSTNode) check(FBTFIT.RIGHT == fitKey(n.entry.getk(), n.bLeft) || FBTFIT.EQ == fitKey(n.entry.getk(), n.bLeft))
            if (n.bRight is FBSTNode) check(FBTFIT.LEFT == fitKey(n.entry.getk(), n.bRight))
            return n
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
            }
        }

        private fun <A, B: Any> enrichNode(node: FBSTNode<A, B>, treePath: Trace<A, B>): FBSTNode<A, B>
        where A: Any, A: Comparable<A> = when (treePath.direction) {
                FBTFIT.EQ -> FBSTNode( // EQ goes to the right
                    treePath.treeStub.entry,
                    treePath.treeStub.bLeft,
                    FBSTNode(treePath.treeStub.entry, FBSTNil, treePath.treeStub.bRight))
                FBTFIT.RIGHT -> FBSTNode(treePath.treeStub.entry,treePath.treeStub.bLeft,node)
                FBTFIT.LEFT -> FBSTNode(treePath.treeStub.entry,node,treePath.treeStub.bRight)
            }

        // Takes an existing tree stub ("descent"), and a new node ("item").
        // Rebuilds a (new) tree with "item" placed in its position, according
        // to the tree shaping criteria established with "reshape"
        private tailrec fun <A, B: Any> reshapeWithItem(
            item: FBSTNode<A, B>,
            descent: FBSTNode<A, B>,
            trace: FList<Trace<A, B>>,
            reshape: (FBSTNode<A, B>, Trace<A, B>) -> FBSTNode<A, B>): FBSTNode<A, B> where A: Any, A: Comparable<A> {
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
                }
            }


        // treeStub is a tree from which a node has been removed; graft is the only child of the removed node
        // NOTE: reinserting graft into treeStub MUST ALWAYS maintain the BST property (see ::fit for details)
        // there is a tight relationship between treeStub and graft -- this is not a general purpose utility
        private fun <A, B: Any> addGraft(treeStub: FBSTree<A, B>, graft: FBSTree<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A> = when {
            treeStub.fempty() && graft.fempty() -> FBSTNil
            treeStub.fempty() -> graft
            graft.fempty() -> treeStub
            else -> reshapeWithItem(graft as FBSTNode, treeStub as FBSTNode, FLNil, ::enrichNode)
        }

        private tailrec fun <A, B: Any> mergeAppender(t1: FBSTree<A, B>, t2: FBSTree<A, B>, allowDups: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A> = when {
            t1 is FBSTNil -> t2
            t2 is FBSTNil -> t1
            t1.size < t2.size -> {
                val (entry, stub) = t1.fpopAndRemainder()
                mergeAppender(t2.finsertDup(entry!!, allowDups), stub, allowDups)
            }
            else -> {
                val (entry, stub) = t2.fpopAndRemainder()
                mergeAppender(t1.finsertDup(entry!!, allowDups), stub, allowDups)
            }
        }

        private fun <A, B: Any> appender(withDups: Boolean): (FBSTree<A, B>, TKVEntry<A, B>) -> FBSTree<A, B> where A: Any, A: Comparable<A> =
            { treeStub: FBSTree<A, B>, item: TKVEntry<A, B> -> bstInsert(treeStub, item, allowDups = withDups)}

        private fun <B: Any> appenderIntKey(withDups: Boolean): (FBSTree<Int, B>, B) -> FBSTree<Int, B> =
            { treeStub: FBSTree<Int, B>, item: B -> bstInsert(treeStub, item.toIAEntry(), allowDups = withDups)}

        private fun <B: Any> appenderStrKey(withDups: Boolean): (FBSTree<String, B>, B) -> FBSTree<String, B> =
            { treeStub: FBSTree<String, B>, item: B -> bstInsert(treeStub, item.toSAEntry(), allowDups = withDups)}

        private fun <A, B: Any> toFKSetImpl(t: FBSTree<A, B>, kType: RestrictedKeyType<@UnsafeVariance A>?): FKSet<A, B>? where A: Any, A: Comparable<A> = when {
            t.fempty() -> FKSetEmpty.empty()
            null == kType -> t.fkeyType()?.let { toFKSetImpl(t, it) }
            t.fkeyType() == kType -> ofBody(t.toFRBTree())
            (kType is IntKeyType) && (t.fkeyType()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (ofFIKSBody(t.toFRBTree() as FRBTree<Int,A>) as FKSet<A, B>)
            (kType is StrKeyType) && (t.fkeyType()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (ofFSKSBody(t.toFRBTree() as FRBTree<String,A>) as FKSet<A, B>)
            else -> null
        }

        private fun <K, A, B:Any> toFBSTreeImpl(t: FBSTree<A, B>, kType: RestrictedKeyType<K>): FBSTree<K, B>? where A: Any, A: Comparable<A>, K: Any, K: Comparable<K> = when {
            t.fempty() -> nul()
            t.fkeyType() == kType -> @Suppress("UNCHECKED_CAST") (t as FBSTree<K, B>)
            (kType is IntKeyType) && (t.fkeyType()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (t as FBSTree<K, B>)
            (kType is StrKeyType) && (t.fkeyType()?.let { it.kc == kType.kc } ?: false ) -> @Suppress("UNCHECKED_CAST") (t as FBSTree<K, B>)
            else ->  when (kType) {
                is IntKeyType ->   {
                    val res: FBSTree<Int, B> = t.ffold(nul()) { acc, tkv -> acc.finsert(tkv.getv().toIAEntry()) }
                    @Suppress("UNCHECKED_CAST") (res as FBSTree<K, B>)
                }
                is StrKeyType -> {
                    val res: FBSTree<String, B> = t.ffold(FBSTree.nul()) { acc, tkv -> acc.finsert(tkv.getv().toSAEntry()) }
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
        fun <A, B: Any> hashCode(n: FBSTNode<A,B>): Int where A: Any, A: Comparable<A> = n.hashCode()
    }
}

private fun <A, B: Any> deltaRemovalInvariant(outcome: FBSTree<A, B>, initialState: FBSTree<A, B>, deltaItem: TKVEntry<A, B>, deltaCount: Int): Boolean
where A: Any, A: Comparable<A> = if ((outcome.size+deltaCount) == initialState.size) true else {
    println("${ initialState.size - outcome.size } ($deltaCount expected) missing after deleting $deltaItem from tree of ${initialState.size}")
    if (initialState.froot()!!.equals(deltaItem)) print("which was root")
    initialState.ffindItem(deltaItem)?.let { it: FBSTree<A, B> -> println("found at $it") } ?: print("WHICH SHOULD NOT BE MISSING")
    val inParent = initialState.fparentOf(deltaItem)
    inParent?.let { it: FBSTree<A, B> -> println("with parent $it") } ?: print("with missing parent")
    inParent?.let { it.froot()?.let { asEntry ->
        outcome.ffindItem( asEntry )?.let {
            println("(==> now  $it)")
        } ?: print("(==> now  MISSING)")
    } ?: print("which after removal is now empty") }
    println("the removed items:")
    initialState.forEach{
        if (!outcome.contains(it)) println(it)
    }
    false
}
