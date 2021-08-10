package com.xrpn.immutable

import com.xrpn.bridge.FSetIterator
import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.IMBTreeTraversable
import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMSet
import com.xrpn.imapi.IMSetCompanion
import com.xrpn.immutable.FList.Companion.equal
import com.xrpn.immutable.FList.Companion.toFList
import com.xrpn.immutable.FRBTree.Companion.contains
import com.xrpn.immutable.FRBTree.Companion.insertIntKey
import com.xrpn.immutable.FSet.Companion.remove
import com.xrpn.immutable.FSet.Companion.toFRBTree
import com.xrpn.immutable.TKVEntry.Companion.toIAEntries
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry

sealed class FSet<out A: Any>: Set<A>, IMSet<A> {

    // from Collection<A>

    override fun isEmpty(): Boolean = this === FSetBody.empty

    override val size: Int by lazy { this.toFRBTree().size }

    override fun contains(element: @UnsafeVariance A): Boolean = this.holds(element)

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    override fun iterator(): Iterator<A> = FSetIterator(this)

    override fun fpick(): A? = this.toFRBTree().fpick()?.getv()

    override fun fdropWhen(isMatch: (A) -> Boolean): FSet<A> {
        TODO("Not yet implemented")
    }

    override fun ffilter(isMatch: (A) -> Boolean): FSet<A> {
        TODO("Not yet implemented")
    }

    override fun ffilterNot(isMatch: (A) -> Boolean): FSet<A> {
        TODO("Not yet implemented")
    }

    override fun ffind(isMatch: (A) -> Boolean): A? {
        TODO("Not yet implemented")
    }

    override fun fsize(): Int = size

    override fun popAndReminder(): Pair<A?, FSet<A>> {
        val pop: A? = this.fpick()
        val reminder: FSet<A> = pop?.let { this.remove(it).toFSet() } ?: FSetBody.empty
        return Pair(pop, reminder)
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

    //
    // =========
    //

    fun copyToFList(): FList<A> = when {
        isEmpty() -> FLNil
        else -> this.toFRBTree().preorder(reverse = true).fmap { it.getv() }
    }

    companion object: IMSetCompanion {

        override fun<A: Any> emptyIMSet(): FSet<A> = FSetBody.empty

        override fun <A: Any> of(vararg items: A): FSet<A> = of(FList.of(*items))

        override fun <A: Any> of(items: Iterator<A>): FSet<A> = of(FList.of(items))

        override fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): FSet<A> = of(FList.ofMap(items, f))

        override fun <A: Any> of(items: IMList<A>): FSet<A> = FSetBody(items.ffoldLeft(FRBTree.nul(), ::insertIntKey))

        override fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): FSet<A> =  of(FList.ofMap(items, f))

        // ==========

        override fun <A: Any> IMSet<A>.add(item: A): IMSet<A> =
            FSetBody(FRBTree.insert(this.toFRBTree(), item.toIAEntry()))

        override fun <A: Any> IMSet<A>.addAll(elements: Collection<A>): IMSet<A> =
            FSetBody(FRBTree.inserts(this.toFRBTree(), elements.toIAEntries()))

        override fun <A: Any> IMSet<A>.remove(item: A): IMSet<A> =
            FSetBody(FRBTree.delete(this.toFRBTree(), item.toIAEntry()))

        override fun <A: Any> IMSet<A>.removeAll(elements: Collection<A>): IMSet<A> =
            FSetBody(FRBTree.deletes(this.toFRBTree(), elements.toIAEntries()))

        override fun<A: Any> IMSet<A>.holds(item: A): Boolean = FRBTree.containsIntKey(this.toFRBTree(), item)

        // ==========

        override fun <A: Any> IMSet<A>.retainsOnly(elements: Collection<A>): IMSet<A> =
            retainImpl(this.toFSet(), elements.toFList(), symmetricDifference = false)

        override fun <A: Any> IMSet<A>.symmetricDifference(elements: Collection<A>): IMSet<A> =
            retainImpl(this.toFSet(), elements.toFList(), symmetricDifference = true)

        override fun <A: Any>  IMSet<A>.isSubsetOf(rhs: IMSet<A>): Boolean {
            for (item in this.toFSet()) {
                if (!(rhs.toFSet().holds(item))) return false
            }
            return true
        }

        // ==========

        override fun<A: Any> IMSet<A>.equal(rhs: IMSet<A>): Boolean = equal2(this.toFSet(), rhs.toFSet())

        override fun <A : Any> Collection<A>.toIMSet(): IMSet<A> = of(this.iterator())

        override fun <A: Any> finsertOrReplace(src: IMSet<A>, item: A): IMSet<A> =
            FSetBody(FRBTree.insert(src.toFRBTree(), item.toIAEntry()))

        override fun <A: Any> finsertsOrReplace(src: IMSet<A>, items: IMSet<A>): IMSet<A> =
            FSetBody(FRBTree.inserts(src.toFRBTree(), items.toFSet().toIAEntries()))

        override  fun <A: Any> fdelete(src: IMSet<A>, item: A): IMSet<A> =
            FSetBody(FRBTree.delete(src.toFRBTree(), item.toIAEntry()))

        override fun <A: Any> fdeletes(src: IMSet<A>, items: IMSet<A>): IMSet<A> =
            FSetBody(FRBTree.deletes(src.toFRBTree(), items.toFSet().toIAEntries()))

        override fun <A: Any> fretain(src: IMSet<A>, items: IMSet<A>): IMSet<A> {

            tailrec fun go(l: FList<TKVEntry<Int, A>>, mark: IMSet<A>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when(l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (mark.holds(l.head.getk())) FRBTree.insert(acc, l.head) else acc
                    go(l.tail, mark, newAcc)
                }
            }

            return if (src.fsize() < items.fsize()) FSetBody(go(src.toFSet().toIAEntries(), items, FRBTNil))
                   else FSetBody(go(items.toFSet().toIAEntries(), src, FRBTNil))
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

        private fun<A: Any> equal2(lhs: FSet<A>, rhs: FSet<A>): Boolean = when(Pair(lhs.isEmpty(), rhs.isEmpty())) {
            Pair(true, true) -> true
            Pair(false, false) -> if (lhs === rhs) true else (lhs as FSetBody) == rhs
            else -> false
        }

        private fun <A: Any> retainImpl(dest: FSet<A>, items: FList<A>, symmetricDifference: Boolean): FSet<A> {

            val examinanda: FList<TKVEntry<Int, A>> = items.toIAEntries()
            val underExam = FRBTree.inserts(FRBTNil, examinanda)

            tailrec fun goAnd(l: FList<TKVEntry<Int, A>>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when(l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (underExam.contains(l.head.getk())) FRBTree.insert(acc, l.head) else acc
                    goAnd(l.tail, newAcc)
                }
            }

            val inventory: FList<TKVEntry<Int, A>> = dest.toFRBTree().inorder()
            val bothHave: FRBTree<Int, A> = goAnd(inventory, FRBTNil)

            tailrec fun goXor(l: FList<TKVEntry<Int, A>>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when (l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (bothHave.contains(l.head.getk())) acc else FRBTree.insert(acc, l.head)
                    goXor(l.tail, newAcc)
                }
            }

            return if (symmetricDifference) {
                val partial = goXor(examinanda, FRBTNil)
                val simDiff = goXor(inventory, partial)
                FSetBody(simDiff)
            } else {
                FSetBody(bothHave)
            }
        }

        // ==========

        // TODO make this extensible
        private fun <A: Any> IMSet<A>.toFSet(): FSet<A> = when (this) {
            is FSet<A> -> this
            else -> throw RuntimeException("cannot cast ${this::class.simpleName} to FSet")
        }

        // TODO make this extensible
        private fun <A: Any> IMSet<A>.toFRBTree(): FRBTree<Int, A> = (this.toFSet() as FSetBody).body

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
        other is FSetBody<*> -> when {
            this.body.fempty() && other.body.fempty() -> true
            this.body.fempty() || other.body.fempty() -> false
            this.body.root()!!::class == other.body.root()!!::class ->
                IMBTreeTraversable.equal(this.body, other.body)
            else -> false
        }
        other is Set<*> -> when {
            // necessary if FSet is-a Set to maintain reflexive, symmetric, transitive equality
            this.isEmpty() && other.isEmpty() -> true // type erasure boo-boo
            this.isEmpty() || other.isEmpty() -> false
            this.fpick()!!::class == other.first()!!::class -> other == this
            else -> false
        }
        other is IMSet<*> -> when {
            // foot in the door in case of additional implementations for IMSet
            this.isEmpty() && other.fempty() -> true // type erasure boo-boo
            this.isEmpty() || other.fempty() -> false
            this.fpick()!!::class == other.fpick()!!::class -> other.equal(this)
            else -> false
        }
        else -> false
    }

    val hash: Int by lazy { this.body.inorder().hashCode() }

    override fun hashCode(): Int = hash

    val show: String by lazy {
        if (this.isEmpty()) FSet::class.simpleName+"(EMPTY)"
        else this::class.simpleName+"("+body.toString()+")"
    }

    override fun toString(): String = show

    companion object {
        val empty = FSetBody(FRBTNil)
    }
}