package com.xrpn.immutable

import com.xrpn.bridge.FSetIterator
import com.xrpn.imapi.IMBTree
import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMSet
import com.xrpn.imapi.IMSetCompanion
import com.xrpn.immutable.FList.Companion.toFList
import com.xrpn.immutable.FRBTree.Companion.fcontainsIK
import com.xrpn.immutable.FRBTree.Companion.finsertIK
import com.xrpn.immutable.FRBTree.Companion.rbtDelete
import com.xrpn.immutable.FRBTree.Companion.rbtDeletes
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
import com.xrpn.immutable.FRBTree.Companion.rbtInserts
import com.xrpn.immutable.TKVEntry.Companion.toIAEntries
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toIAList
import kotlinx.coroutines.runBlocking

sealed class FSet<out A: Any>: Set<A>, IMSet<A> {

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

    // from Collection<A>

    override fun isEmpty(): Boolean = this === FSetBody.empty

    override val size: Int by lazy { this.toFRBTree().size }

    override fun contains(element: @UnsafeVariance A): Boolean = this.fcontains(element)

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    override fun iterator(): Iterator<A> = FSetIterator(this)

    // traversing

    override fun equal(rhs: IMSet<@UnsafeVariance A>): Boolean = when {
        this.fempty() -> rhs.fempty()
        else -> when {
            this === rhs -> true
            rhs.fempty() -> false
            this.fsize() == rhs.fsize() -> FSet.equal2(this, rhs)
            else -> false
        }
    }

    override fun fforEach (f: (A) -> Unit): Unit {
        this.toFRBTree().fforEach { tkv -> f(tkv.getv()) }
    }

    override fun toIMBTree(): IMBTree<Int, A> = this.toFRBTree()

    // filtering

    override fun fcontains(item: @UnsafeVariance A): Boolean = fcontainsIK(this.toFRBTree(), item)

    override fun fdropItem(item: @UnsafeVariance A): IMSet<A> {
        val aux = rbtDelete(this.toFRBTree(), item.toIAEntry())
        return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
    }

    override fun fdropAll(items: FSet<@UnsafeVariance A>): IMSet<A> {
        val aux = rbtDeletes(this.toFRBTree(), items.toIAList())
        return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
    }

    override fun ffilter(isMatch: (A) -> Boolean): FSet<A> = of( ffilterToList(isMatch) )

    override fun ffilterNot(isMatch: (A) -> Boolean): FSet<A> = of( ffilterToList { a -> !isMatch(a) } )

    override fun ffind(isMatch: (A) -> Boolean): A? {
        val search: FList<A> = ffilterToList(isMatch)
        return if (1 == search.size) search.fhead()!! else null
    }

    override fun fisSubsetOf(rhs: IMSet<@UnsafeVariance A>): Boolean {
        val superset = rhs.toFSet().toFRBTree()
        val subset = this.toFRBTree()
        return this.fsize() == subset.fcount(superset::fcontains)
    }

    override fun fpick(): A? = this.toFRBTree().fpick()?.getv()

    override fun fretainsOnly(items: IMSet<@UnsafeVariance A>): FSet<A>  =
        retainImpl(this, items.toFList(), symmetricDifference = false)

    override fun fsymmetricDifference(items: IMSet<@UnsafeVariance A>): FSet<A> = TODO()

    // ==========

    override fun <A: Any> IMSet<A>.fretainsOnly(elements: Collection<A>): IMSet<A>

    override fun <A: Any> IMSet<A>.fsymmetricDifference(elements: Collection<A>): IMSet<A> =
        retainImpl(this.toFSet(), elements.toFList(), symmetricDifference = true)



    // grouping

    override fun fcombinations(size: Int): IMSet<IMSet<A>> {
        // all groups of "size" members from this set; order does not matter
        TODO("Not yet implemented")
    }

    override fun fcount(isMatch: (A) -> Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun <B> fgroupBy(f: (A) -> B): Map<B, FSet<A>> {
        TODO("Not yet implemented")
    }

    override fun findexed(offset: Int): FSet<Pair<A, Int>> {
        TODO("Not yet implemented")
    }

    override fun fpartition(isMatch: (A) -> Boolean): Pair<FSet<A>, FSet<A>> {
        TODO("Not yet implemented")
    }

    override fun fpermutations(size: Int): IMSet<IMList<A>> {
        // all groups of "size" members from this set; order does matter
        TODO("Not yet implemented")
    }

    override fun fpopAndReminder(): Pair<A?, FSet<A>> {
        val pop: A? = this.fpick()
        val reminder: FSet<A> = pop?.let { this.fdropItem(it).toFSet() } ?: FSetBody.empty
        return Pair(pop, reminder)
    }

    override fun fsize(): Int = size

    // transforming

    override fun <B : Any> fflatMap(f: (A) -> IMSet<B>): FSet<B> {
        TODO("Not yet implemented")
    }

    override fun <B> ffold(z: B, f: (acc: B, A) -> B): B {
        TODO("Not yet implemented")
    }

    override fun <B : Any> fmap(f: (A) -> B): FSet<B> {
        TODO("Not yet implemented")
    }

    override fun <B : Any> fmapToList(f: (A) -> B): FList<B> {
        TODO("Not yet implemented")
    }

    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? {
        TODO("Not yet implemented")
    }

    // altering

    override fun fadd(item: @UnsafeVariance A): FSet<A> {
        val aux = rbtInsert(this.toFRBTree(), item.toIAEntry())
        return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
    }

    override fun faddAll(items: IMSet<@UnsafeVariance A>): IMSet<A> {
        val aux = rbtInserts(this.toFRBTree(), items.toFSet().toIAList())
        return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
    }

    // ==========

    //
    // =========
    //

    fun copyToFList(): FList<A> = when {
        isEmpty() -> FLNil
        else -> this.toFRBTree().preorder(reverse = true).fmap { it.getv() }
    }

    private fun ffilterToList(isMatch: (A) -> Boolean): FList<A> {
        val tisMatch: (TKVEntry<Int, A>) -> Boolean = { tkv -> isMatch(tkv.getv()) }
        val treeStub = this.toFRBTree()
        var tkvMatches: FList<TKVEntry<Int, A>>
        runBlocking(ioScope("FSet").coroutineContext) { tkvMatches = treeStub.ffind(tisMatch) }
        return tkvMatches.fmap { tkv -> tkv.getv() }
    }

    // private
    private fun toFRBTree(): FRBTree<Int, A> = (this as FSetBody).body

    companion object: IMSetCompanion {

        override fun<A: Any> emptyIMSet(): FSet<A> = FSetBody.empty

        override fun <A: Any> of(vararg items: A): FSet<A> = of(items.iterator())

        override fun <A: Any> of(items: Iterator<A>): FSet<A> {
            if (!items.hasNext()) return FSetBody.empty
            var acc: FRBTree<Int, A> = FRBTNil
            items.forEach {
                acc = FRBTree.rbtInsert(acc, it.toIAEntry())
            }
            return FSetBody(acc)
        }

        override fun <K, A: Any> of(items: IMBTree<K, A>): FSet<A> where K: Any, K: Comparable<K> = when {
            items.fempty() -> FSetBody.empty
            items.froot()!!.getk() is Int && items is FRBTree<K, A> -> @Suppress("UNCHECKED_CAST") FSetBody(items as FRBTree<Int, A>)
            else -> FSetBody(FRBTree.ofvi(items.preorderValues()))
        }

        override fun <A: Any> of(items: IMList<A>): FSet<A> =
            if (items.fempty()) FSetBody.empty else {
                val f: (IMBTree<Int, A>, A) -> IMBTree<Int, A> = { stub, item -> finsertIK(stub, item) }
                val aux: IMBTree<Int, A> = items.ffoldLeft(FRBTree.emptyIMBTree(), f)
                if (aux.fempty()) FSetBody.empty else FSetBody(aux as FRBTree<Int, A>)
            }

        override fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): FSet<A> {
            if (!items.hasNext()) return FSetBody.empty
            var acc: FRBTree<Int, A> = FRBTNil
            items.forEach {
                acc = FRBTree.rbtInsert(acc, f(it).toIAEntry())
            }
            return if (acc.isEmpty()) FSetBody.empty else FSetBody(acc)
        }

        override fun <B: Any, A: Any> ofMap(items: IMList<B>, f: (B) -> A): FSet<A> = if (items.fempty()) FSetBody.empty else {
            val mapInsert: (IMBTree<Int, A>, B) -> IMBTree<Int, A> = { stub, it -> finsertIK(stub, f(it)) }
            val aux: IMBTree<Int, A> = items.ffoldLeft(FRBTree.nul(), mapInsert)
            if (aux.fempty()) FSetBody.empty else FSetBody(aux as FRBTree<Int, A>)
        }

        override fun <B, A: Any> ofMap(items: List<B>, f: (B) -> A): FSet<A> = if (items.isEmpty()) FSetBody.empty else {
            val mapInsert: (IMBTree<Int, A>, B) -> IMBTree<Int, A> = { stub, it -> finsertIK(stub, f(it)) }
            val aux = items.fold(FRBTree.nul(), mapInsert)
            if (aux.fempty()) FSetBody.empty else FSetBody(aux as FRBTree<Int, A>)
        }

        // ==========

        override fun <A : Any> Collection<A>.toIMSet(): IMSet<A> = of(this.iterator())

        override fun <A: Any> finsertOrReplace(src: IMSet<A>, item: A): IMSet<A> {
            val aux = FRBTree.rbtInsert(src.toFRBTree(), item.toIAEntry())
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        override fun <A: Any> finsertsOrReplace(src: IMSet<A>, items: IMSet<A>): IMSet<A> {
            val aux = FRBTree.rbtInserts(src.toFRBTree(), items.toFSet().toIAList())
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        override  fun <A: Any> fdelete(src: IMSet<A>, item: A): IMSet<A> {
            val aux = FRBTree.rbtDelete(src.toFRBTree(), item.toIAEntry())
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        override fun <A: Any> fdeletes(src: IMSet<A>, items: IMSet<A>): IMSet<A>  {
            val aux = FRBTree.rbtDeletes(src.toFRBTree(), items.toFSet().toIAList())
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        override fun <A: Any> fretain(src: IMSet<A>, items: IMSet<A>): IMSet<A> {

            tailrec fun go(l: FList<TKVEntry<Int, A>>, mark: IMSet<A>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when(l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (mark.fcontains(l.head.getv())) FRBTree.rbtInsert(acc, l.head) else acc
                    go(l.tail, mark, newAcc)
                }
            }

            return if (src.fsize() < items.fsize()) {
                val aux = go(src.toFSet().toIAList(), items, FRBTNil)
                if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
            } else {
                val aux = go(items.toFSet().toIAList(), src, FRBTNil)
                if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
            }
        }

        override fun <A: Any> fxordiff(src1: IMSet<A>, src2: IMSet<A>,): IMSet<A> {
//            val itr1 = FTreeIterator(src1.toFRBTree())
//            val itr2 = FTreeIterator(src2.toFRBTree())
//            while(true) {
//                val item1: TKVEntry<Int, A>? = itr1.nullableNext()
//                val item2: TKVEntry<Int, A>? = itr1.nullableNext()
//                TODO()
//            }
            TODO()
        }

        // ========== implementation

        private fun <A: Any> retainImplSS(dest: FSet<A>, items: FList<A>, symmetricDifference: Boolean): FSet<A> {

            val examinanda: FList<TKVEntry<Int, A>> = items.toIAEntries()
            val underExam: FRBTree<Int, A> = rbtInserts(FRBTNil, examinanda)

            tailrec fun goAnd(l: FList<TKVEntry<Int, A>>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when(l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (underExam.fcontainsKey(l.head.getk())) rbtInsert(acc, l.head) else acc
                    goAnd(l.tail, newAcc)
                }
            }

            val inventory: FList<TKVEntry<Int, A>> = dest.toFRBTree().inorder()
            val bothHave: FRBTree<Int, A> = goAnd(inventory, FRBTNil)

            tailrec fun goXor(l: FList<TKVEntry<Int, A>>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when (l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (bothHave.fcontainsKey(l.head.getk())) acc else FRBTree.rbtInsert(acc, l.head)
                    goXor(l.tail, newAcc)
                }
            }

            return if (symmetricDifference) {
                val partial = goXor(examinanda, FRBTNil)
                val simDiff = goXor(inventory, partial)
                if (simDiff.isEmpty()) FSetBody.empty else FSetBody(simDiff)
            } else if (bothHave.isEmpty()) FSetBody.empty else FSetBody(bothHave)

        }

        private fun <A: Any> retainImpl(dest: FSet<A>, items: FList<A>, symmetricDifference: Boolean): FSet<A> {

            val examinanda: FList<TKVEntry<Int, A>> = items.toIAEntries()
            val underExam: FRBTree<Int, A> = rbtInserts(FRBTNil, examinanda)

            tailrec fun goAnd(l: FList<TKVEntry<Int, A>>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when(l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (underExam.fcontainsKey(l.head.getk())) rbtInsert(acc, l.head) else acc
                    goAnd(l.tail, newAcc)
                }
            }

            val inventory: FList<TKVEntry<Int, A>> = dest.toFRBTree().inorder()
            val bothHave: FRBTree<Int, A> = goAnd(inventory, FRBTNil)

            tailrec fun goXor(l: FList<TKVEntry<Int, A>>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when (l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (bothHave.fcontainsKey(l.head.getk())) acc else FRBTree.rbtInsert(acc, l.head)
                    goXor(l.tail, newAcc)
                }
            }

            return if (symmetricDifference) {
                val partial = goXor(examinanda, FRBTNil)
                val simDiff = goXor(inventory, partial)
                if (simDiff.isEmpty()) FSetBody.empty else FSetBody(simDiff)
            } else if (bothHave.isEmpty()) FSetBody.empty else FSetBody(bothHave)

        }

        // ==========

        // TODO make this extensible
        internal fun <A: Any> Collection<A>.toFSet(): FSet<A> = when (this) {
            is FSet<A> -> this
            else -> of(this.iterator())
        }

        // TODO make this extensible
        internal fun <A: Any> IMSet<A>.toFSet(): FSet<A> = when (this) {
            is FSet<A> -> this
            else -> throw RuntimeException("cannot cast ${this::class.simpleName} to FSet")
        }

        // TODO make this extensible
        internal fun <A: Any> IMSet<A>.toFRBTree(): FRBTree<Int, A> = (this.toFSet() as FSetBody).body

    }

    /* TODO
    collapse(S): given a set of sets, return the union.[6] For example, collapse({{1}, {2, 3}}) == {1, 2, 3}. May be considered a kind of sum.
    flatten(S): given a set consisting of sets and atomic elements (elements that are not sets), returns a set whose elements are the atomic elements of the original top-level set or elements of the sets it contains. In other words, remove a level of nesting – like collapse, but allow atoms. This can be done a single time, or recursively flattening to obtain a set of only atomic elements.[7] For example, flatten({1, {2, 3}}) == {1, 2, 3}.
     */
}

internal class FSetBody<out A: Any> internal constructor (
    val body: FRBTree<Int, A>
): FSet<A>() {

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMSet<*> -> when {
            this.isEmpty() && other.fempty() -> true // type erasure boo-boo
            this.isEmpty() || other.fempty() -> false
            this.fpick()!!::class == other.fpick()!!::class ->
                @Suppress("UNCHECKED_CAST") this.equal(other as IMSet<A>)
            else -> false
        }
        other is Set<*> -> when {
            this.isEmpty() && other.isEmpty() -> true // type erasure boo-boo
            this.isEmpty() || other.isEmpty() -> false
            this.fpick()!!::class == other.first()!!::class -> this.fsize() == other.size && other.equals( this)
            else -> false
        }
        else -> false
    }

    val hash: Int by lazy { ((127L * (this.body.hashCode().toLong() + 17L)) / 131L).toInt() }

    override fun hashCode(): Int = hash

    val show: String by lazy {
        if (this.isEmpty()) FSet::class.simpleName+"(EMPTY)"
        else this::class.simpleName+"("+body.ffold("") { acc, tkv -> acc + "|" + tkv.getv().toString() }+")"
    }

    override fun toString(): String = show

    override val size: Int by lazy { body.size }

    companion object {
        val empty = FSetBody(FRBTNil)
    }
}