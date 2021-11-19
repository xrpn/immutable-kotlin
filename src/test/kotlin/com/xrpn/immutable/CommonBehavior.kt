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

val imbtreeFunctorKLaw = object: FunctorKLaw {}
val immapFunctorKLaw = object: FunctorKLaw {}

interface ApplicativeLaw  {
    fun <T: Any, V: Any> isMapplicativeEqual(
        lhs: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        rhs: IMMapplicable<T, IMMappable<T, IMCommon<T>>>
    ): Boolean = rhs.equals(lhs)
    fun <T> idOp(v: T) = v
    /*
        if we lift the identity function and then apply it then the Mapplicative should be unchanged
     */
    fun <V: Any,W: Any> identityLaw(
        mapplicative: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        value: IMMappable<W, IMCommon<W>>
    ): Boolean {
        val ref: IMMapplicable<W, IMMappable<W, IMCommon<W>>>? = mapplicative.flift2maply(value)
        return ref?.fmapply(::idOp)?.equals(ref) ?: false
    }
    /*
        if we apply a function to a value inside or outside the context of an Applicative, the result should be the same.
    */
    fun <V: Any,W: Any,X: Any> homomorphismLaw(
        mapplicative: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        value: IMMappable<W, IMCommon<W>>,
        op: (IMMappable<W, IMCommon<W>>) -> IMMappable<X, IMCommon<X>>
    ): Boolean {
        // lift and apply
        val aux1: IMMapplicable<W, IMMappable<W, IMCommon<W>>> = mapplicative.flift2maply(value)!!
        val aut: IMMapplicable<X, IMMappable<X, IMCommon<X>>> = aux1.fmapply(op)
        // apply and lift
        val aux2: IMMappable<X, IMCommon<X>> = op(value)
        val ref: IMMapplicable<X, IMMappable<X, IMCommon<X>>> = mapplicative.flift2maply(aux2)!!
        return isMapplicativeEqual(aut, ref)
    }
    /*
        lifting to Mapplicable is a left identity, as well as a right identity
     */
    fun <V: Any,W: Any,X: Any> liftSymmetryLaw(
        mapplicative: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        value: IMMappable<W, IMCommon<W>>,
        aplyOp: (IMMappable<W, IMCommon<W>>) -> IMMappable<X, IMCommon<X>>
    ): Boolean {
        val aux1: IMMapplicable<W, IMMappable<W, IMCommon<W>>> = mapplicative.flift2maply(value)!!
        val aut: IMMapplicable<X, IMMappable<X, IMCommon<X>>> = aux1.fmapply(aplyOp)
        val aux2: (IMMappable<W, IMCommon<W>>) -> IMMapplicable<X, IMMappable<X, IMCommon<X>>> = { vv: IMMappable<W, IMCommon<W>> -> mapplicative.flift2maply(aplyOp(vv))!! }
        val ref: IMMapplicable<X, IMMappable<X, IMCommon<X>>> = aux2(value)
        return isMapplicativeEqual(aut, ref)
    }
    /*
        applicative supports map operation
     */
    fun <V: Any,W: Any> functorialLaw(
        mapplicative: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        value: IMMappable<V, IMCommon<V>>,
        mapOp: (V) -> W
    ): Boolean {
        val ref: IMMapplicable<W, IMMappable<W, IMCommon<W>>> = mapplicative.flift2maply(mapplicative.fapmap(mapOp))!!
        val fMapOp: ((V) -> W) -> IMMapplicable<W, IMMappable<W, IMCommon<W>>> = { f -> mapplicative.flift2maply(value.fmap(f))!! }
        val aut: IMMapplicable<W, IMMappable<W, IMCommon<W>>> = fMapOp(mapOp)
        return isMapplicativeEqual(aut, ref)
    }

    fun <V: Any,W: Any,X: Any> compositionLaw(
        mapplicativew: IMMapplicable<W, IMMappable<W, IMCommon<W>>>,
        aplyOpW2X: (IMMappable<W, IMCommon<W>>) -> IMMappable<X, IMCommon<X>>
        mapplicativev: IMMapplicable<V, IMMappable<V, IMCommon<V>>>,
        aplyOpV2W: (IMMappable<V, IMCommon<V>>) -> IMMappable<W, IMCommon<W>>,
    ): Boolean  {
        val ref: IMMapplicable<X, IMMappable<X, IMCommon<X>>> = mapplicativev.fmapply(aplyOpV2W).fmapply(aplyOpW2X)
        fun <T: Any> aplyOpV2T(f:(V) -> T): ((V) -> T) -> IMMappable<T, IMCommon<T>> = { f:(V) -> T -> mapplicativev.fapmap(f) }
        fun <T: Any> aplyOpX2T(f:(W) -> T): ((W) -> T) -> IMMappable<T, IMCommon<T>> = { f:(W) -> T -> mapplicativew.fapmap(f) }
//        val ref: IMMapplicable<X, IMMappable<X, IMCommon<X>>> = mapplicativev.fmapply(mapplicativew.flift2maply(mapplicativev.asIMMappable().flift2map(mapOpV2W)).fmapply(aplyOpW2X)
        fun fff(wx: (W) -> X, vw: (V) -> W): (V) -> X = wx kompose vw
    }

}

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

