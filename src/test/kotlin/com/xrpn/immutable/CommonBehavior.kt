package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe

tailrec fun <A: Comparable<A>, B: Any> goDropItemAll(t: IMBTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
    when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
            val delenda = acc.head
            val deleted: IMBTree<A, B> = t.fdropItemAll(delenda)
            val oracle = inorder.ffilterNot { it == delenda }
            val aut = deleted.inorder()
            when (deleted) {
                is FBSTNode -> aut shouldBe oracle
                is FBSTUnique -> fail("empty tree should not be empty")
            }
            goDropItemAll(t, acc.tail, inorder)
        }
    }

internal tailrec fun <A: Comparable<A>, B: Any> goDropItemTele(t: IMBTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
    when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
            val delenda = acc.head
            val deleted: IMBTree<A, B> = t.fdropItem(delenda)
            val oracle = inorder.fdropFirst { it == delenda }
            val aut = deleted.inorder()
            when (deleted) {
                is FBSTNode -> aut shouldBe oracle
                is FBSTUnique -> deleted.size shouldBe 0
            }
            goDropItemTele(deleted, acc.tail, oracle)
        }
    }

inline infix fun <B, C, A> ((B) -> C).kompose(crossinline f: (A) -> B): (A) -> C = { a: A -> this(f(a)) }

interface FunctorLaw  {
    fun <T: Any, V: Any> isMappableEqual(lhs: IMMappable<V,IMCommon<V>>, rhs: IMMappable<T,IMCommon<T>>): Boolean =
        rhs.equals(lhs)
    fun <T> idOp(v: T) = v
    fun <V: Any> identityLaw(mappable: IMMappable<V,IMCommon<V>>): Boolean = isMappableEqual(mappable, mappable.fmap(::idOp))
    fun <S: Any, T: Any, V: Any, W: Any> associativeLaw(mappable: IMMappable<V,IMCommon<V>>, f1: (V) -> S, f2: (S) -> T, f3: (T) -> W): Boolean =
        isMappableEqual(mappable.fmap(f2 kompose f1).fmap(f3), mappable.fmap(f1).fmap(f3 kompose f2))
}

val flistFunctorLaw = object: FunctorLaw {}
val fksetFunctorLaw = object: FunctorLaw {}
val fqueueFunctorLaw = object: FunctorLaw {}
val fstackFunctorLaw = object: FunctorLaw {}

interface FunctorKLaw {
    fun <K, T: Any, L, V: Any> isMappableEqual(lhs: IMKMappable<L,V,IMCommon<TKVEntry<L,V>>>, rhs: IMKMappable<K,T,IMCommon<TKVEntry<K,T>>>): Boolean
    where K: Any, K: Comparable<K>, L: Any, L: Comparable<L> =
        lhs.equals(rhs)
    fun <T> idOp(v: T) = v
    fun <L, V: Any> identityLaw(mappable: IMKMappable<L,V,IMCommon<TKVEntry<L,V>>>): Boolean where L: Any, L: Comparable<L> =
        isMappableEqual(mappable, mappable.fmap(::idOp))
    fun <J, S: Any, K, T: Any, L, V: Any, M, W: Any> associativeLaw(
        mappable: IMKMappable<L,V,IMCommon<TKVEntry<L,V>>>,
        f1: (TKVEntry<L,V>) -> TKVEntry<J,S>,
        f2: (TKVEntry<J,S>) -> TKVEntry<K,T>,
        f3: (TKVEntry<K,T>) -> TKVEntry<M,W>): Boolean
    where J: Any, J: Comparable<J>, K: Any, K: Comparable<K>, L: Any, L: Comparable<L> , M: Any, M: Comparable<M> {
        val aux1 = mappable.fmap(f2 kompose f1)
        val aux2 = aux1.fmap(f3)
        val xua1 = mappable.fmap(f1)
        val xua2 = xua1.fmap(f3 kompose f2)
        return isMappableEqual(aux2, xua2)
    }
}

interface ApplicativeLaw  {
    fun <T: Any, V: Any> isMapplicativeEqual(
        lhs: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        rhs: IMMapplicable<T, IMMappable<T, IMCommon<T>>>
    ): Boolean = rhs.equals(lhs)
    fun <T> idOp(v: T) = v
    fun <V: Any> identityLaw(
        mapplicative: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        value: IMMappable<V, IMCommon<V>>
    ): Boolean = mapplicative.flift2maply(value).fmapply(::idOp).equals(value)
    fun <S: Any, T: Any, V: Any, W: Any> associativeLaw(mappable: IMMappable<V,IMCommon<V>>, f1: (V) -> S, f2: (S) -> T, f3: (T) -> W): Boolean =
        TODO() // isMapplicativeEqual(mappable.fmap(f2 kompose f1).fmap(f3), mappable.fmap(f1).fmap(f3 kompose f2))
}

val imbtreeFunctorKLaw = object: FunctorKLaw {}
val immapFunctorKLaw = object: FunctorKLaw {}

val mapInt2String: (Int) -> String = { x -> x.toString()  }
val mapString2Double: (String) -> Double = { x -> Double.fromBits(x.hashCode().toLong()) }
val mapDouble2Long: (Double) -> Long = { x -> x.toBits() }

val mapIInt2IString: (TKVEntry<Int,Int>) -> TKVEntry<Int,String> = { x -> x.getv().toString().toIAEntry() }
val mapIString2IDouble: (TKVEntry<Int,String>) -> TKVEntry<Int,Double> = { x -> Double.fromBits(x.getv().hashCode().toLong()).toIAEntry() }
val mapIDouble2ILong: (TKVEntry<Int,Double>) -> TKVEntry<Int,Long> = { x -> x.getv().toBits().toIAEntry() }



val mapInt2Int: (Int) -> Int = { x -> x.toString().hashCode() }
val mapInt2Double: (Int) -> Double = { x -> Double.fromBits(x.toString().hashCode().toLong()) }
val mapString2Long: (String) -> Long = { x -> x.hashCode().toLong() }
val mapLong2Double: (Long) -> Double = { x -> Double.fromBits(x) }
val mapDouble2String: (Double) -> String = { x -> x.toString() }
val mapString2String: (String) -> String = { x -> x.hashCode().toString() }
val mapLong2String: (Long) -> String = { x -> x.toString() }

