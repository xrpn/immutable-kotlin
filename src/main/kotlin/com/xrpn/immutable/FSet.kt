package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.imapi.BTreeTraversable
import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMSet
import com.xrpn.imapi.IMSetCompanion
import com.xrpn.immutable.FList.Companion.toFList
import com.xrpn.immutable.FRBTree.Companion.contains
import com.xrpn.immutable.FRBTree.Companion.insertIntKey

sealed class FSet<out A: Any>: Set<A>, IMSet<A> {

    // from Collection<A>

    override fun isEmpty(): Boolean = this === FSetBody.empty

    override val size: Int by lazy { (this as FSetBody).body.size() }

    override fun contains(element: @UnsafeVariance A): Boolean = this.holds(element)

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    override fun iterator(): Iterator<A> = FListIteratorFwd(this.toFList())

    override fun fdropWhile(isMatch: (A) -> Boolean): FSet<A> {
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

    override fun frandom(): A? {
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

    override fun <B : Any> fflatMap(f: (A) -> IMSet<B>): FSet<B> {
        TODO("Not yet implemented")
    }

    override fun <B> ffold(z: B, f: (acc: B, A) -> B): B {
        TODO("Not yet implemented")
    }

    override fun <B : Any> fmap(f: (A) -> B): FSet<B> {
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
        else -> (this as FSetBody).body.preorder(reverse = true).fmap { it.getv() }
    }

    companion object: IMSetCompanion {

        override fun<A: Any> emptyFSet(): FSet<A> = FSetBody.empty

        override fun <A: Any> of(vararg items: A): IMSet<A> = of(FList.of(*items))

        override fun <A: Any> of(items: Iterator<A>): FSet<A> = of(FList.of(items))

        override fun <B, A : Any> ofMap(items: Iterator<B>, f: (B) -> A): IMSet<A> = of(FList.ofMap(items, f))

        override fun <A: Any> of(items: IMList<A>): FSet<A> = FSetBody(items.ffoldLeft(FRBTree.nul(), ::insertIntKey))

        override fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): IMSet<A> =  of(FList.ofMap(items, f))

        fun<A: Any> equal2(lhs: FSet<A>, rhs: FSet<A>): Boolean = when(Pair(lhs.isEmpty(), rhs.isEmpty())) {
            Pair(true, true) -> true
            Pair(false, false) -> if (lhs === rhs) true else (lhs as FSetBody) == rhs
            else -> false
        }

        override fun<A: Any> FSet<A>.equal(rhs: FSet<A>): Boolean = equal2(this, rhs)
        override fun <A : Any> Collection<A>.toFSet(): IMSet<A> = TODO("Not yet implemented")

        internal fun<A: Any> FSet<A>.holds(value: A): Boolean = FRBTree.containsIntKey((this as FSetBody).body, value)

        override fun <A: Any> IMSet<A>.add(item: A): IMSet<A> = insertOrReplace(this as FSet<A>, item)
        override fun <A: Any> IMSet<A>.addAll(elements: Collection<A>): IMSet<A> = insertsOrReplace(this as FSet<A>, elements.toFList())
        override fun <A: Any> IMSet<A>.clear(): IMSet<A> = FSetBody.empty
        override fun <A: Any> IMSet<A>.remove(item: A): IMSet<A> = delete(this as FSet<A>, item)
        override fun <A: Any> IMSet<A>.removeAll(elements: Collection<A>): IMSet<A> = deletes(this as FSet<A>, elements.toFList())
        override fun <A: Any> IMSet<A>.retainsOnly(elements: Collection<A>): IMSet<A> = retains(this as FSet<A>, elements.toFList(), symmetricDifference = false)
        override fun <A: Any> IMSet<A>.symmetricDifference(elements: Collection<A>): IMSet<A> = retains(this as FSet<A>, elements.toFList(), symmetricDifference = true)

        override fun <A: Any>  IMSet<A>.isSubsetOf(rhs: IMSet<A>): Boolean = TODO()

        internal fun <A: Any> insertOrReplace(dest: FSet<A>, item: A): FSet<A> = FSetBody(FRBTree.insert((dest as FSetBody).body, TKVEntry.ofIntKey(item)))
        internal fun <A: Any> insertsOrReplace(dest: FSet<A>, items: IMList<A>): FSet<A> =
            FSetBody(FRBTree.inserts((dest as FSetBody).body, (items.fmap { TKVEntry.ofIntKey(it) } as FList<TKVEntry<Int,A>>)))
        internal fun <A: Any> delete(dest: FSet<A>, item: A): FSet<A> = FSetBody(FRBTree.delete((dest as FSetBody).body, TKVEntry.ofIntKey(item)))
        internal fun <A: Any> deletes(dest: FSet<A>, items: IMList<A>): FSet<A> =
            FSetBody(FRBTree.deletes((dest as FSetBody).body, (items.fmap { TKVEntry.ofIntKey(it) } as FList<TKVEntry<Int,A>>)))

        internal fun <A: Any> retains(dest: FSet<A>, items: IMList<A>, symmetricDifference: Boolean): FSet<A> {

            val examinanda: FList<TKVEntry<Int, A>> = (items.fmap { TKVEntry.ofIntKey(it) } as FList<TKVEntry<Int,A>>)
            val underExam = FRBTree.inserts(FRBTNil, examinanda)

            tailrec fun goAnd(l: FList<TKVEntry<Int, A>>, acc: FRBTree<Int, A>): FRBTree<Int, A> = when(l) {
                is FLNil -> acc
                is FLCons -> {
                    val newAcc = if (underExam.contains(l.head.getk())) FRBTree.insert(acc, l.head) else acc
                    goAnd(l.tail, newAcc)
                }
            }

            val inventory: FList<TKVEntry<Int, A>> = (dest as FSetBody).body.inorder()
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


    }

    /* TODO
    pick(S): returns an arbitrary element of S.[2][3][4] Functionally, the mutator pop can be interpreted as the pair of selectors (pick, rest), where rest returns the set consisting of all elements except for the arbitrary element.[5] Can be interpreted in terms of iterate.[a]
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
            this.body.isEmpty() && other.body.isEmpty() -> true
            this.body.isEmpty() || other.body.isEmpty() -> false
            this.body.root()!!::class == other.body.root()!!::class ->
                BTreeTraversable.equal(this.body, other.body)
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