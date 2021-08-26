package com.xrpn.immutable

import com.xrpn.bridge.FSetIterator
import com.xrpn.imapi.*
import com.xrpn.immutable.FRBTree.Companion.finsertIK
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.rbtDelete
import com.xrpn.immutable.FRBTree.Companion.rbtDeletes
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
import com.xrpn.immutable.FRBTree.Companion.rbtInserts
import com.xrpn.immutable.FSetOfOne.Companion.toSoO
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

    override val size: Int by lazy { this.toFRBTree().size }

    override fun contains(element: @UnsafeVariance A): Boolean = this.fcontains(FSetOfOne(element))

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!this.fcontains(FSetOfOne(it))) return false }
        return true
    }

    override fun iterator(): Iterator<A> = FSetIterator(this)

    // utility

    override fun equal(rhs: IMSet<@UnsafeVariance A>): Boolean =
        this.equals(rhs)

    override fun fforEach (f: (A) -> Unit) =
        this.toFRBTree().fforEach { tkv -> f(tkv.getv()) }

    override fun toIMBTree(): IMBTree<Int, A> =
        this.toFRBTree()

    override fun copy(): IMSet<A> =
        FSetBody(this.toFRBTree().ffold(nul()) { acc, tkv -> acc.finsert(tkv) })

    override fun copyToMutableSet(): MutableSet<@UnsafeVariance A> = this.toFRBTree()
        .ffold(mutableSetOf()) { acc, tkv -> acc.add(tkv.getv()); acc }

    // filtering

    override fun fcontains(item: FSetOfOne<@UnsafeVariance A>): Boolean =
        this.toFRBTree().fcontainsKey(TKVEntry.intKeyOf(item))

    override fun fcontainsAny(items: IMSet<@UnsafeVariance A>): Boolean = try {
        val outer = if (this.toIMBTree().fsize() < items.toIMBTree().fsize()) this.toIMBTree() else items.toIMBTree()
        val mark = if (this.toIMBTree().fsize() < items.toIMBTree().fsize()) items.toIMBTree() else this.toIMBTree()
        // TODO gross, need to think something better...
        outer.ffold(0) { acc, tkv -> if(mark.fcontains(tkv)) throw BreakoutException() else acc+1 }
        false
    } catch (ex: BreakoutException) {
        true
    }

    override fun fdropItem(item: FSetOfOne<@UnsafeVariance A>): FSet<A> {
        val aux: FRBTree<Int, A> = this.toFRBTree().fdropItem(item.one.toIAEntry())
        return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
    }

    override fun fdropAll(items: IMSet<@UnsafeVariance A>): FSet<A> {
        val aux = this.toFRBTree().fdropAlt(items.toIMBTree()) as FRBTree<Int, A>
        return if (aux.fempty()) FSetBody.empty else FSetBody(aux)
    }

    override fun ffilter(isMatch: (A) -> Boolean): FSet<A> =
        FSetBody(this.toFRBTree().ffilter { tkv -> isMatch(tkv.getv()) })

    override fun ffilterNot(isMatch: (A) -> Boolean): FSet<A> {
        val notMatch: (a: A) -> Boolean = { a -> !isMatch(a) }
        return ffilter(notMatch)
    }

    override fun ffind(isMatch: (A) -> Boolean): A? =
        this.toFRBTree().ffindDistinct{ tkv -> isMatch(tkv.getv()) }?.getv()

    override fun fisSubsetOf(rhs: IMSet<@UnsafeVariance A>): Boolean {
        val superset = rhs.toIMBTree()
        val maybeSubset = this.toIMBTree()
        return this.fsize() == maybeSubset.fcount(superset::fcontains)
    }

    override fun fpick(): A? = this.toIMBTree().fpick()?.getv()

    override fun fAND(items: IMSet<@UnsafeVariance A>): FSet<A> =
        FSetBody(this.toFRBTree().ffold(nul()) {
            stub, tkv -> if (items.fcontains(FSetOfOne(tkv.getv()))) stub.finsert(tkv) else stub
        })

    override fun fOR(items: IMSet<@UnsafeVariance A>): FSet<A> {
        val aux = this.toFRBTree().finsertt(items.toIMBTree()) as FRBTree<Int, A>
        return if (aux.fempty()) FSetBody.empty else FSetBody(aux)
    }

    override fun fXOR(items: IMSet<@UnsafeVariance A>): FSet<A> {
        val bothHave: FRBTree<Int, A> = this.fAND(items).toFRBTree()
        val thisOnly: FRBTree<Int, A> = this.toFRBTree().fdropAlt(bothHave) as FRBTree<Int, A>
        val itemsOnly = when(items) {
            is FSet -> items.toFRBTree().fdropAlt(bothHave) as FRBTree<Int, A>
            else -> items.fdropAll(FSetBody(bothHave)).toIMBTree() as FRBTree<Int, A>
        }
        return FSetBody(thisOnly.finsertt(itemsOnly) as FRBTree<Int, A>)

    }

    // grouping

    override fun <B: Any> fcartesian(rhs: IMSet<B>): FSet<Pair<A, B>> {

        tailrec fun go(shrink: FSet<A>, stay: IMSet<B>, acc: FSet<Pair<A, B>>): FSet<Pair<A, B>> = if (shrink.fempty()) acc else {
            val (pop, reminder) = shrink.fpopAndReminder()
            val newAcc = pop?.let{
                acc.fOR( stay.ffold(emptyIMSet()) {
                    set, a -> set.fadd(FSetOfOne(Pair(it, a)))
                }
            )} ?: acc
            go(reminder, stay, newAcc)
        }

        return go(this, rhs, emptyIMSet())
    }

    override fun fcombinations(size: Int): FSet<FSet<A>> {

        // all unique subsets up to "size" members from this set; order does not matter

        tailrec fun gogo(item: A, fatSet: FSet<FSet<A>>, acc: FSet<FSet<A>>) : FSet<FSet<A>> {
            val (pop, reminder) = fatSet.fpopAndReminder()
            return if (pop == null) acc else {
                val aux = pop.fadd(item.toSoO())
                if (size < aux.size) return acc
                val newAcc = acc.fadd(aux.toSoO())
                gogo(item, reminder, newAcc)
            }
        }

        tailrec fun go(shrink: FSet<A>, acc: FSet<FSet<A>>): FSet<FSet<A>> = if (shrink.fempty()) acc else {
            val (pop, reminder) = shrink.fpopAndReminder()
            val newAcc = pop?.let {
                val outer: FSet<FSet<A>> = acc.fadd(of(it).toSoO())
                gogo(it, outer, outer)
            } ?: acc
            go(reminder, newAcc)
        }

        return if (size < 1) emptyIMSet() else go(this, emptyIMSet())
    }

    override fun fcount(isMatch: (A) -> Boolean): Int =
        this.toFRBTree().fcount { tkv -> isMatch(tkv.getv()) }

    override fun <B> fgroupBy(f: (A) -> B): IMMap<B, FSet<A>> where B: Any, B: Comparable<B> {
        TODO("Not yet implemented")
    }

    override fun findexed(offset: Int): FSet<Pair<A, Int>> {
        var index = offset - 1
        return FSetBody(this.toFRBTree().fmap{ tkv -> index+=1; TKVEntry.of(tkv.getk(), Pair(tkv.getv(), index)) })
    }

    override fun fpartition(isMatch: (A) -> Boolean): Pair<FSet<A>, FSet<A>> {
        val (t, f) = this.toFRBTree().fpartition { tkv -> isMatch(tkv.getv()) }
        return Pair(FSetBody(t), FSetBody(f))
    }

    override fun fpermutations(size: Int): FSet<FList<A>> {
        // all groups of "size" members from this set; order does matter
        TODO()
    }

    override fun fpopAndReminder(): Pair<A?, FSet<A>> {
        val pop: A? = this.fpick()
        val reminder = pop?.let { this.fdropItem(FSetOfOne(it)) } ?: FSetBody.empty
        return Pair(pop, reminder)
    }

    override fun fsize(): Int = size

    // transforming

    override fun <B : Any> fflatMap(f: (A) -> IMSet<B>): FSet<B> {
        TODO("Not yet implemented")
    }

    override fun <C: Any> ffold(z: C, f: (acc: C, A) -> C): C =
        this.toFRBTree().ffoldv(z) { stub, tkv -> f(stub, tkv) }

    override fun <B : Any> fmap(f: (A) -> B): FSet<B> =
        FSetBody(this.toFRBTree().fmap { tkv -> f(tkv.getv()).toIAEntry() })

    override fun <B : Any> fmapToList(f: (A) -> B): FList<B> {
        TODO("Not yet implemented")
    }

    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? {
        TODO("Not yet implemented")
    }

    // altering

    override fun fadd(item: FSetOfOne<@UnsafeVariance A>): FSet<A> {
        val aux = this.toFRBTree().finsert(item.one.toIAEntry())
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

    protected abstract fun toFRBTree(): FRBTree<Int, A>

    // private

    companion object: IMSetCompanion {

        private class BreakoutException(): RuntimeException()

        override fun<A: Any> emptyIMSet(): FSet<A> = FSetBody.empty

        override fun <A: Any> of(vararg items: A): FSet<A> = of(items.iterator())

        override fun <A: Any> of(items: Iterator<A>): FSet<A> {
            if (!items.hasNext()) return FSetBody.empty
            var acc: FRBTree<Int, A> = FRBTNil
            items.forEach {
                acc = rbtInsert(acc, it.toIAEntry())
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

        override fun <A : Any> Collection<A>.toIMSet(): IMSet<A> = when(this) {
            is FSet<A> -> this
            else -> of(this.iterator())
        }

        internal fun <A: Any> fsInsertOrReplace(src: FSet<A>, item: A): FSet<A> {
            val aux = rbtInsert(src.toFRBTree(), item.toIAEntry())
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        internal fun <A: Any> fsInsertsOrReplace(src: FSet<A>, items: FSet<A>): FSet<A> {
            val aux = rbtInserts(src.toFRBTree(), FList.of(items.toIMBTree().toIAList()))
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        internal fun <A: Any> fsDelete(src: FSet<A>, item: A): FSet<A> {
            val aux = rbtDelete(src.toFRBTree(), item.toIAEntry())
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        internal fun <A: Any> fsDeletes(src: FSet<A>, items: FSet<A>): FSet<A>  {
            val aux = rbtDeletes(src.toFRBTree(), FList.of(items.toIMBTree().toIAList()))
            return if (aux.isEmpty()) FSetBody.empty else FSetBody(aux)
        }

        /* TODO maybe
        collapse(S): given a set of sets, return the union.[6] For example, collapse({{1}, {2, 3}}) == {1, 2, 3}. May be considered a kind of sum.
        flatten(S): given a set consisting of sets and atomic elements (elements that are not sets), returns a set whose elements are the atomic elements of the original top-level set or elements of the sets it contains. In other words, remove a level of nesting – like collapse, but allow atoms. This can be done a single time, or recursively flattening to obtain a set of only atomic elements.[7] For example, flatten({1, {2, 3}}) == {1, 2, 3}.
         */

    }

}

class FSetOfOne<out A: Any> internal constructor (
    val one: A
): FSet<A>() {
    val body: FRBTree<Int, A> by lazy { FRBTree.ofvi(one) }
    override fun isEmpty(): Boolean = false
    override fun equals(other: Any?): Boolean = FSetBody(body).equals(other)
    val hash: Int by lazy { ((9151L * (this.body.hashCode().toLong() + 3121L)) / 9161L).toInt() }
    override fun hashCode(): Int = hash
    val show: String by lazy { FSet::class.simpleName+"($one)" }
    override fun toString(): String = show
    override val size: Int = 1
    override fun toFRBTree(): FRBTree<Int, A> = body

    companion object {
        fun <A: Any> A.toSoO() = FSetOfOne(this)
    }
}

internal class FSetBody<out A: Any> internal constructor (
    val body: FRBTree<Int, A>
): FSet<A>() {

    override fun isEmpty(): Boolean = this === empty

    // short of type erasure, this maintains reflexive, symmetric and transitive properties
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMSet<*> -> when {
            this.isEmpty() && other.fempty() -> true
            this.isEmpty() || other.fempty() -> false
            this.fpick()!!::class != other.fpick()!!::class -> false
            else -> @Suppress("UNCHECKED_CAST") IMSetEqual2(this, other as IMSet<A>)
        }
        other is FRBTree<*,*> -> when {
            this.isEmpty() && other.isEmpty() -> true // type erasure boo-boo
            this.isEmpty() || other.isEmpty() -> false
            other.froot()!!.getk() !is Int -> false
            this.toFRBTree().froot()!!.getv()::class != other.froot()!!.getv()::class -> false
            else -> @Suppress("UNCHECKED_CAST") IMBTreeEqual2(this.toFRBTree(), (other as IMBTree<Int, A>))
        }
        other is Set<*> -> when {
            this.isEmpty() && other.isEmpty() -> true // type erasure boo-boo
            this.isEmpty() || other.isEmpty() -> false
            this.fpick()!!::class != other.first()!!::class -> false
            this.fsize() != other.size -> false
            else -> {
//                val foo = this.fpick()!!::class
//                val aux = other.first()
//                val bar = aux!!::class
//                print("$foo $bar")
                other.equals(this)
            }
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

    override fun toFRBTree(): FRBTree<Int, A> = this.body

    companion object {
        val empty = FSetBody(FRBTNil)
    }
}