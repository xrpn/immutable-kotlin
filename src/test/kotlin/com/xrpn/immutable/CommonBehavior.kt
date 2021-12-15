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

interface CartesianLaw  {
    fun <S: Any, T: Any, X: Any> isZipEqual(lhs: ITZMap<S,T>, rhs: ITMap<X>): Boolean =
        IMZipMap.softEqual(lhs, rhs)
    fun <S: Any, T: Any, X: Any> isKartEqual(lhs: ITZMap<S,T>, rhs: ITMap<ITMap<X>>): Boolean {
        val laux = lhs.asMap().fmap { it._1() }
        val raux = rhs.fmap {
            @Suppress("UNCHECKED_CAST") (it as IMOrdered<S>)
            it.fnext()!!
        }
        @Suppress("UNCHECKED_CAST") (laux as IMOrdered<S>)
        @Suppress("UNCHECKED_CAST") (raux as IMOrdered<S>)
        return laux.fzip(raux).fall { it.first == it.second }
    }
    fun <S: Any, T: Any, X: Any> idpOp(s: S, t: T): X = @Suppress("UNCHECKED_CAST") (Pair(s,t) as X)
    fun <S: Any, T: Any, X: Any> idOp(): (S) -> (T) -> X = { s: S -> { t: T -> @Suppress("UNCHECKED_CAST") (Pair(s,t) as X) } }
    fun <S: Any, T: Any> zidentitypLaw(zm: ITZMap<S,T>): Boolean = isZipEqual(zm, zm.fzippMap(::idpOp))
    fun <S: Any, T: Any> zidentityLaw(zm: ITZMap<S,T>): Boolean = isZipEqual(zm, zm.fzipMap(idOp()))
    fun <S: Any, T: Any, U: Any> zassociativeLaw (
        s: ITMap<S>,
        t: ITMap<T>,
        u: ITMap<U>,
    ): Boolean {
        val lhs = s mapWith t mapWith u
        val rhsNatural = (s mapWith t) mapWith u
        val rhs = s mapWith (t mapWith u)
        return lhs.equals(rhsNatural) && lhs.equals(rhs)
    }
    fun <S: Any, T: Any, U: Any, V: Any> zassociativeLaw2 (
        s: ITMap<S>,
        t: ITMap<T>,
        u: ITMap<U>,
        v: ITMap<V>,
    ): Boolean {
        val lhs = s mapWith t mapWith u mapWith v
        val rhsNatural = ((s mapWith t) mapWith u) mapWith v
        val rhs1 = s mapWith t mapWith (u mapWith v)
        val rhs2 = s mapWith (t mapWith (u mapWith v))
        val rhs3 = (s mapWith t) mapWith (u mapWith v)
        val rn = lhs.equals(rhsNatural)
        val r1 = lhs.equals(rhs1)
        val r2 = lhs.equals(rhs2)
        val r3 = lhs.equals(rhs3)
        return rn && r1 && r2 && r3
    }
    fun <S: Any, T: Any, U: Any, V: Any, W: Any> zassociativeLaw3 (
        s: ITMap<S>,
        t: ITMap<T>,
        u: ITMap<U>,
        v: ITMap<V>,
        w: ITMap<W>,
    ): Boolean {
        val lhs = s mapWith t mapWith u mapWith v mapWith w
        val rhsNatural = (((s mapWith t) mapWith u) mapWith v) mapWith w
        val rhs1 = s mapWith t mapWith u mapWith (v mapWith w)
        val rhs2 = s mapWith t mapWith (u mapWith (v mapWith w))
        val rhs3 = s mapWith (t mapWith (u mapWith (v mapWith w)))
        val rn = lhs.equals(rhsNatural)
        val r1 = lhs.equals(rhs1)
        val r2 = lhs.equals(rhs2)
        val r3 = lhs.equals(rhs3)
        return rn && r1 && r2 && r3
    }

    fun <S: Any, T: Any> kidentityp(zm1: ITZMap<S,T>, zm2: ITZMap<T,S>): Boolean {
        val m1 = zm1.fkartpMap(::idpOp).fmap {
            @Suppress("UNCHECKED_CAST") (it as IMOrdered<Pair<S,T>>)
            DWFMap.of(it.fnext()!!.first)
        }
        val m2 = zm2.fkartpMap(::idpOp).fmap {
            @Suppress("UNCHECKED_CAST") (it as IMOrdered<Pair<T,S>>)
            DWFMap.of(it.fnext()!!.first)
        }

        return isKartEqual(zm1, m1) && isKartEqual(zm2, m2)
    }
    fun <S: Any, T: Any> kidentity(zm1: ITZMap<S,T>, zm2: ITZMap<T,S>): Boolean {
        val m1 = zm1.fkartMap(idOp()).fmap {
            @Suppress("UNCHECKED_CAST") (it as IMOrdered<Pair<S,T>>)
            DWFMap.of(it.fnext()!!.first)
        }
        val m2 = zm2.fkartMap(idOp()).fmap {
            @Suppress("UNCHECKED_CAST") (it as IMOrdered<Pair<T,S>>)
            DWFMap.of(it.fnext()!!.first)
        }
        return isKartEqual(zm1, m1) && isKartEqual(zm2, m2)
    }

//    fun <S: Any, T: Any, V: Any, W: Any> associativeLaw(mapOp: IMMapOp<V,IMCommon<V>>, f1: (V) -> S, f2: (S) -> T, f3: (T) -> W): Boolean =
//        isZipMppableEqual(mapOp.fmap(f2 fKompose f1).fmap(f3), mapOp.fmap(f1).fmap(f3 fKompose f2))
//    companion object {
//    }
}

val fdisjunctionCartesianLaw = object: CartesianLaw {}
val fwrapperCartesianLaw = object: CartesianLaw {}
val fstackCartesianLaw = object: CartesianLaw {}
val fqueueCartesianLaw = object: CartesianLaw {}
val flistCartesianLaw = object: CartesianLaw {}

interface ApplicativeLaw  {
    //
    // mostly after http://www.cs.ox.ac.uk/jeremy.gibbons/publications/iterator.pdf
    //
    fun <T: Any, V: Any> isMapplicativeEqual(
        lhs: ITMapp<V>,
        rhs: ITMapp<T>
    ): Boolean =
        rhs.equals(lhs)

    fun <T: Any, U: ITMap<T>> idOp(v: U): ITMap<T> {
        val aux = @Suppress("UNCHECKED_CAST") (v as ITMap<T>)
        return aux
    }

    /*
        if we lift the identity function and then apply it then the Mapplicative should be unchanged
     */
    fun <V: Any> identityLaw(
        value: ITMap<V>
    ): Boolean {
        val ref: ITMapp<V>? = IMMappOp.flift2mapp(value)
        return ref?.fapp(::idOp)?.equals(ref) ?: false
    }
    /*
        if we apply a function to a value inside or outside the context of an Applicative, the result should be the same.
    */
    fun <V: Any,W: Any> homomorphismLaw(
        value: ITMap<V>,
        op: (ITMap<V>) -> ITMap<W>
    ): Boolean {
        // lift and apply
        val aux1: ITMapp<V> = IMMappOp.flift2mapp(value)!!
        val aut: ITMapp<W> = aux1.fapp(op)
        // op and lift
        val aux2: ITMap<W> = op(value)
        val ref: ITMapp<W> = IMMappOp.flift2mapp(aux2)!!
        return isMapplicativeEqual(aut, ref)
    }
    /*
        lifting to Mapplicable is a left identity, as well as a right identity
     */
    fun <V: Any,W: Any,X: Any> liftSymmetryLaw(
        value: ITMap<V>,
        walue: ITMap<W>,
        aplyOp: (ITMap<W>) -> ITMap<X>
    ): Boolean {
        val aux1: ITMapp<W> = IMMappOp.flift2mapp(walue)!!
        val aut: ITMapp<X> = aux1.fapp(aplyOp)
        val aux2: (ITMap<W>) -> ITMapp<X> = { vv: ITMap<W> -> IMMappOp.flift2mapp(aplyOp(vv))!! }
        val ref: ITMapp<X> = aux2(walue)
        return isMapplicativeEqual(aut, ref)
    }
    /*
        applicative supports map operation
     */
    fun <V: Any,W: Any> functorialLaw(
        value: ITMap<V>,
        mapOp: (V) -> W
    ): Boolean {
        val ref: ITMapp<W> =  IMMappOp.flift2mapp(value)!!.fmapp(mapOp)
        val aut: ITMapp<W> = IMMappOp.flift2mapp(value.fmap(mapOp))!!
        return isMapplicativeEqual(aut, ref)
    }

    fun <V: Any,W: Any,X: Any> compositionLaw(
        walue: ITMap<W>,
        aplyW2X: (ITMap<W>) -> ITMap<X>,
        value: ITMap<V>,
        aplyV2W: (ITMap<V>) -> ITMap<W>,
    ): Boolean  {
        val ref: ITMapp<X> = IMMappOp.flift2mapp(value)!!.fapp(aplyV2W).fapp(aplyW2X)
        fun k(wx: (ITMap<W>) -> ITMap<X>, vw: (ITMap<V>) -> ITMap<W>): (ITMap<V>) -> ITMap<X> = wx fKompose vw
        val aut: ITMapp<X> = IMMappOp.flift2mapp(value)!!.fapp(k(aplyW2X, aplyV2W))
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

val fmapInt2String_I: (ITMap<Int>) -> ITMap<String> = { fx -> fx.fmap(mapInt2String_I) }
val fmapString2StrangeDouble: (ITMap<String>) -> ITMap<Double> = { fx -> fx.fmap(mapString2StrangeDouble) }
val fmapDouble2StrangeLong: (ITMap<Double>) -> ITMap<Long> = { fx -> fx.fmap(mapDouble2StrangeLong) }
val fmapLong2StrangeInt: (ITMap<Long>) -> ITMap<Int> = { fx -> fx.fmap(mapLong2StrangeInt) }

// ============

val fmapmInt2String_I: (ITMapp<Int>) -> ITMapp<String> = { fx: ITMapp<Int> -> fx.fapp(fmapInt2String_I) }
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


