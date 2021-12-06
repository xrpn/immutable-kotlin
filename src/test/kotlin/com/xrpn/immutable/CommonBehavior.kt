package com.xrpn.immutable

import com.xrpn.hash.HashFast
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

interface FunctorLaw  {
    fun <T: Any, V: Any> isMappableEqual(lhs: IMMapOp<V,IMCommon<V>>, rhs: IMMapOp<T,IMCommon<T>>): Boolean =
        rhs.equals(lhs)
    fun <T> idOp(v: T) = v
    fun <V: Any> identityLaw(mapOp: IMMapOp<V,IMCommon<V>>): Boolean = isMappableEqual(mapOp, mapOp.fmap(::idOp))
    fun <S: Any, T: Any, V: Any, W: Any> associativeLaw(mapOp: IMMapOp<V,IMCommon<V>>, f1: (V) -> S, f2: (S) -> T, f3: (T) -> W): Boolean =
        isMappableEqual(mapOp.fmap(f2 fKompose f1).fmap(f3), mapOp.fmap(f1).fmap(f3 fKompose f2))
}

val fdisjunctionFunctorLaw = object: FunctorLaw {}
val fwrapperFunctorLaw = object: FunctorLaw {}
val fstackFunctorLaw = object: FunctorLaw {}
val fqueueFunctorLaw = object: FunctorLaw {}
val flistFunctorLaw = object: FunctorLaw {}
val fksetFunctorLaw = object: FunctorLaw {}

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
        val aux1 = mappable.fmap(f2 fKompose f1)
        val aux2 = aux1.fmap(f3)
        val xua1 = mappable.fmap(f1)
        val xua2 = xua1.fmap(f3 fKompose f2)
        return isMappableEqual(aux2, xua2)
    }
}

val imbtreeFunctorKLaw = object: FunctorKLaw {}
val immapFunctorKLaw = object: FunctorKLaw {}

interface ApplicativeLaw  {
    //
    // mostly after http://www.cs.ox.ac.uk/jeremy.gibbons/publications/iterator.pdf
    //
    fun <T: Any, V: Any> isMapplicativeEqual(
        lhs: FMapp<V>,
        rhs: FMapp<T>
    ): Boolean =
        rhs.equals(lhs)

    fun <T: Any, U: FMap<T>> idOp(v: U): FMap<T> {
        val aux = @Suppress("UNCHECKED_CAST") (v as FMap<T>)
        return aux
    }

    /*
        if we lift the identity function and then apply it then the Mapplicative should be unchanged
     */
    fun <V: Any> identityLaw(
        value: FMap<V>
    ): Boolean {
        val ref: FMapp<V>? = IMMappOp.flift2mapp(value)
        return ref?.fapp(::idOp)?.equals(ref) ?: false
    }
    /*
        if we apply a function to a value inside or outside the context of an Applicative, the result should be the same.
    */
    fun <V: Any,W: Any> homomorphismLaw(
        value: FMap<V>,
        op: (FMap<V>) -> FMap<W>
    ): Boolean {
        // lift and apply
        val aux1: FMapp<V> = IMMappOp.flift2mapp(value)!!
        val aut: FMapp<W> = aux1.fapp(op)
        // op and lift
        val aux2: FMap<W> = op(value)
        val ref: FMapp<W> = IMMappOp.flift2mapp(aux2)!!
        return isMapplicativeEqual(aut, ref)
    }
    /*
        lifting to Mapplicable is a left identity, as well as a right identity
     */
    fun <V: Any,W: Any,X: Any> liftSymmetryLaw(
        value: FMap<V>,
        walue: FMap<W>,
        aplyOp: (FMap<W>) -> FMap<X>
    ): Boolean {
        val aux1: FMapp<W> = IMMappOp.flift2mapp(walue)!!
        val aut: FMapp<X> = aux1.fapp(aplyOp)
        val aux2: (FMap<W>) -> FMapp<X> = { vv: FMap<W> -> IMMappOp.flift2mapp(aplyOp(vv))!! }
        val ref: FMapp<X> = aux2(walue)
        return isMapplicativeEqual(aut, ref)
    }
    /*
        applicative supports map operation
     */
    fun <V: Any,W: Any> functorialLaw(
        value: FMap<V>,
        mapOp: (V) -> W
    ): Boolean {
        val ref: FMapp<W> =  IMMappOp.flift2mapp(value)!!.fmapp(mapOp)
        val aut: FMapp<W> = IMMappOp.flift2mapp(value.fmap(mapOp))!!
        return isMapplicativeEqual(aut, ref)
    }

    fun <V: Any,W: Any,X: Any> compositionLaw(
        walue: FMap<W>,
        aplyW2X: (FMap<W>) -> FMap<X>,
        value: FMap<V>,
        aplyV2W: (FMap<V>) -> FMap<W>,
    ): Boolean  {
        val ref: FMapp<X> = IMMappOp.flift2mapp(value)!!.fapp(aplyV2W).fapp(aplyW2X)
        fun k(wx: (FMap<W>) -> FMap<X>, vw: (FMap<V>) -> FMap<W>): (FMap<V>) -> FMap<X> = wx fKompose vw
        val aut: FMapp<X> = IMMappOp.flift2mapp(value)!!.fapp(k(aplyW2X, aplyV2W))
        return isMapplicativeEqual(aut, ref)
    }

}

val fdisjunctionApplicativeLaw = object: ApplicativeLaw {}
val fwrapperApplicativeLaw = object: ApplicativeLaw {}
val fstackApplicativeLaw = object: ApplicativeLaw {}
val fqueueApplicativeLaw = object: ApplicativeLaw {}
val flistApplicativeLaw = object: ApplicativeLaw {}
val fksetApplicativeLaw = object: ApplicativeLaw {}


val mapInt2String_I: (Int) -> String = { x -> "${x}_I" }
val mapString2StrangeDouble: (String) -> Double = { x -> Double.fromBits(x.hashCode().toLong()) }
val mapDouble2StrangeLong: (Double) -> Long = { x -> x.toBits() }

val mapIInt2IString: (TKVEntry<Int,Int>) -> TKVEntry<Int,String> = { x -> x.getv().toString().toIAEntry() }
val mapIString2StrangeIDouble: (TKVEntry<Int,String>) -> TKVEntry<Int,Double> = { x -> Double.fromBits(x.getv().hashCode().toLong()).toIAEntry() }
val mapIDouble2StrangeILong: (TKVEntry<Int,Double>) -> TKVEntry<Int,Long> = { x -> x.getv().toBits().toIAEntry() }

//

val fmapInt2String_I: (FMap<Int>) -> FMap<String> = { fx -> fx.fmap(mapInt2String_I) }
val fmapString2StrangeDouble: (FMap<String>) -> FMap<Double> = { fx -> fx.fmap(mapString2StrangeDouble) }
val fmapDouble2StrangeLong: (FMap<Double>) -> FMap<Long> = { fx -> fx.fmap(mapDouble2StrangeLong) }
val fmapLong2StrangeInt: (FMap<Long>) -> FMap<Int> = { fx -> fx.fmap(mapLong2StrangeInt) }

// ============

val fmapmInt2String_I: (FMapp<Int>) -> FMapp<String> = { fx: FMapp<Int> -> fx.fapp(fmapInt2String_I) }
// val fmapmInt2String_I: (FMapp<Int>) -> FMapp<String> = {fz: (Int) -> String -> { fy: (FMap<Int>) -> FMap<String> -> { fx: FMapp<Int> -> fx.fapp(fy.map(fz) ) }}

// ============

val mapLong2String_L: (Long) -> String = { x -> "${x}_L" }
val mapLong2StrangeInt: (Long) -> Int = { x -> HashFast.hash32_1(x) }
val mapLong2StrangeDouble: (Long) -> Double = { x -> Double.fromBits(x) }

val mapInt2DifferingInt: (Int) -> Int = { x -> x.toString().hashCode() }
val mapInt2StrangeDouble: (Int) -> Double = { x -> Double.fromBits(x.toString().hashCode().toLong()) }

val mapString2HashLong: (String) -> Long = { x -> x.hashCode().toLong() }
val mapString2StrangeInt: (String) -> Int = { x -> x.hashCode() / 2 }
val mapString2DifferingString: (String) -> String = { x -> x.hashCode().toString() }

val mapDouble2StrangeInt: (Double) -> Int = { x -> HashFast.hash32_2(mapDouble2StrangeLong(x)) }
val mapDouble2String: (Double) -> String = { x -> x.toString() }


