package com.xrpn.imapi

import com.xrpn.imapi.FCartesian.Companion.emptyZipMap
import com.xrpn.imapi.FCartesian.Companion.reportException
import com.xrpn.immutable.FList
//import com.xrpn.immutable.FList
import com.xrpn.immutable.TKVEntry
import com.xrpn.immutable.TraversalImpl

interface SEq<T> {
    fun equal(actual: T, expected: T): Throwable?
    fun softEqual(actual: T, expected: Any?): Throwable?
}

typealias ITKart<S, T> = IMCartesian<S, ITMap<S>, T, IMZPair<S,T>>

interface IMCartesian<out S: Any, out U: ITMap<S>, out T: Any, out W:IMZPair<S,T>> {

    infix fun mpro(t: ITMap<@UnsafeVariance T>): ITMap<W>?
    infix fun opro(t: IMOrdered<@UnsafeVariance T>): ITMap<W>?

    companion object {
        fun <S: Any, T: Any, W:IMZPair<S,T>> asZMap(k: ITMap<W>?): ITZMap<S,T>? = try {
            k?.let { try {
                FCartesian.asZMap(it as ITMap<IMZPair<S, T>>) as ITZMap<S, T>
            } catch (ex: NullPointerException) {
                reportException(ex)
                null
            }}
        } catch (ex: ClassCastException) {
            reportException(ex,k)
            null
        }
        fun <S: Any, T: Any> flift2kart(item: IMOrdered<S>): ITKart<S,T> = FCartesian.of(item)
    }
}

// =============================================== Map

interface IMZPair<out A: Any, out B: Any> {
    fun _1():A
    fun _2():B
    fun toPair(): Pair<A,B>
    fun equal(rhs: Pair<@UnsafeVariance A, @UnsafeVariance B>) = (_1().equals(rhs.first) && _2().equals(rhs.second))
    fun equal(rhs: IMZPair<@UnsafeVariance A, @UnsafeVariance B>) = (_1().equals(rhs._1()) && _2().equals(rhs._2()))
}

typealias ITZMap<S,T> = IMZipMap<S,T,IMZPair<S,T>>

interface IMZipWrap<out S: Any, out T: Any> {
    fun asZMap(): ITZMap<S,T>
}

interface IMZipMap<out S: Any, out T: Any, out W: IMZPair<S,T>>: IMZipWrap<S,T> {

    fun <X: Any> fzipMap(f: (S) -> (T) -> X): ITMap<X>
    fun <X: Any> fzippMap(f: (S, T) -> X): ITMap<X> {
        val pf: (S) -> (T) -> X = { s: S -> { t: T -> f(s, t) } }
        return fzipMap(pf)
    }
    fun <X: Any> fkartMap(f: (S) -> (T) -> X ): ITMap<ITMap<X>>
    fun <X: Any> fkartpMap(f: (S, T) -> X ): ITMap<ITMap<X>> {
        val pf: (S) -> (T) -> X = { s: S -> { t: T -> f(s, t) } }
        return fkartMap(pf)
    }
    fun asMap(): ITMap<W>
    fun asIMOrdered(): IMOrdered<W>
    fun equal(rhs: ITZMap<@UnsafeVariance S, @UnsafeVariance T>): Boolean = equals(rhs)
    fun softEqual(rhs: Any?): Boolean = equals(rhs) || softEqual(this, rhs)

    companion object {
        fun <S: Any, T: Any, X: Any, W:IMZPair<S,T>> ITMap<W>.fmap2p(f: (S, T) -> X): ITMap<X> =
            IMCartesian.asZMap(this)!!.fzippMap(f)
        fun <S: Any, T: Any, X: Any, W:IMZPair<S,T>> ITMap<W>.fmap2(f: (S) -> (T) -> X): ITMap<X> =
            IMCartesian.asZMap(this)!!.fzipMap(f)
        fun <S: Any, T: Any, U: Any, X: Any> ITMap<IMZPair<IMZPair<S,T>,U>>.fmap3p(f: (S, T, U) -> X): ITMap<X> =
            this.fmap { f(it._1()._1(), it._1()._2(), it._2()) }
        fun <S: Any, T: Any, U: Any, X: Any> ITMap<IMZPair<IMZPair<S,T>,U>>.fmap3(f: (S) -> (T) -> (U) -> X): ITMap<X> =
            this.fmap {  it.partial3map(f) }
        fun <S: Any, T: Any, U: Any, V: Any, X: Any> ITMap<IMZPair<IMZPair<IMZPair<S,T>,U>,V>>.fmap4p(f: (S, T, U, V) -> X): ITMap<X> =
            this.fmap { f(it._1()._1()._1(), it._1()._1()._2(), it._1()._2(), it._2()) }
        fun <S: Any, T: Any, U: Any, V: Any, X: Any> ITMap<IMZPair<IMZPair<IMZPair<S,T>,U>,V>>.fmap4(f: (S) -> (T) -> (U) -> (V) -> X): ITMap<X> =
            this.fmap { it.partial4map(f) }
        // TODO: more?

        fun <S: Any, T: Any> softEqual(lhs: IMZipMap<S, T, IMZPair<S,T>>, rhs: Any?): Boolean = lhs.equals(rhs) || when (rhs) {
            is IMOrdered<*> -> when {
                lhs.asMap().fempty() -> rhs.fempty()
                rhs.fempty() -> lhs.asMap().fempty()
                rhs.fsize() != lhs.asMap().fsize() -> false
                rhs.fpick() is Pair<*,*> -> {

                    @Suppress("UNCHECKED_CAST") (rhs as IMOrdered<Pair<Any,Any>>)

                    tailrec fun goPairwise(lhs: IMOrdered<IMZPair<Any,Any>>, rhs: IMOrdered<Pair<Any,Any>>): Boolean = when {
                        lhs.fempty() -> true
                        !(lhs.fnext()!!.equal(rhs.fnext()!!)) -> false
                        else -> goPairwise(lhs.fdrop(1), rhs.fdrop(1))
                    }

                    goPairwise(lhs.asIMOrdered(),rhs)

                }
                rhs.fpick() is IMZPair<*,*> -> {
                    val aux = @Suppress("UNCHECKED_CAST") (rhs as? IMOrdered<IMZPair<S,T>>)
                    aux?.let{ lhs.asIMOrdered().tibOrdered<IMZPair<S,T>>()?.equal(lhs.asIMOrdered(), it) ?: false } ?: false
                }
                else -> false
            }
            is IMCommon<*> -> lhs.asMap().fempty() && rhs.fempty()
            else -> false
        }
    }
}


// ITMappable, really.  ITMap for brevity
typealias ITMap<S> = IMMapOp<S, IMCommon<S>>

interface IMMapOp<out S: Any, out U: IMCommon<S>>: IMCommon<S> {

    fun <T: Any> fmap(f: (S) -> T): ITMap<T>

    infix fun <T: Any, V: ITMap<T>, W: IMZPair<@UnsafeVariance S,T>> mapWith(tmap: V): ITMap<W> = when {
        this.fempty() -> @Suppress("UNCHECKED_CAST") (this.toEmpty() as ITMap<W>)
        tmap.fempty() -> @Suppress("UNCHECKED_CAST") (tmap.toEmpty() as ITMap<W>)
        this is IMZipWrap<*,*> -> { // S is at least a Pair<*,*>
            (@Suppress("UNCHECKED_CAST") (this as ITMap<IMZPair<Any,Any>>))
            val zm: ITZMap<Any, Any> = FCartesian.asZMap(this)!!
            zipMaps(zm.asMap(), tmap)
        }
        tmap is IMZipWrap<*,*> -> { // T is at least a Pair<*,*>
            (@Suppress("UNCHECKED_CAST") (tmap as ITMap<IMZPair<Any,Any>>))
            val zm: ITZMap<Any, Any> = FCartesian.asZMap(tmap)!!
            zipMaps(this, zm.asMap())
        }
        this !is IMOrdered<*> && tmap !is IMOrdered<*> -> throw RuntimeException("internal error")
        tmap !is IMOrdered<*> -> emptyZipMap()
        this !is IMOrdered<*> -> emptyZipMap()
        else -> zipMaps(this, tmap)
    }


    companion object {

        fun <T: Any> softEqual(lhs: ITMap<T>, rhs: Any?): Boolean = lhs.equals(rhs) || when (lhs) {
            is ITZMap<*,*> -> IMZipMap.softEqual(lhs, rhs)
            is IMOrdered<*> -> when (rhs) {
                is IMOrdered<*> -> {
                    val lhsAux = @Suppress("UNCHECKED_CAST") (lhs as? IMOrdered<T>)
                    val rhsAux = @Suppress("UNCHECKED_CAST") (rhs as? IMOrdered<T>)
                    when {
                        lhsAux != null && rhsAux != null -> lhsAux.tibOrdered<T>()?.equal(lhsAux, rhsAux) ?: false
                        else -> false
                    }
                }
                else -> TODO()
            }
            else -> TODO()
        }

        fun <T: Any> flift2Map(item: IMCommon<T>): ITMap<T>? = IM.liftToIMMappable(item)
        fun <T: Any> flift2Map(item: T): ITMap<T> {
            check(item !is IMCommon<*>)
            return DWFMap.of(item)
        }
    }
}

typealias ITOMap<S> = IMOrderedMapOp<S, IMOrdered<S>>

interface IMOrderedMapOp<out S: Any, out U: IMOrdered<S>>: IMOrdered<S>, IMMapOp<S,U>

// ========================== KMap

typealias ITKZMap<S,T> = IMKZipMap<S,T,TKVEntry<S,T>>

interface IMKZipWrap<out S, out T: Any> where S: Any, S: Comparable<@UnsafeVariance S> {
    fun asZMap(): ITKZMap<S,T>
}

interface IMKZipMap<out S, out T: Any, out W: TKVEntry<S,T>>: IMKZipWrap<S,T> where S: Any, S: Comparable<@UnsafeVariance S> {

    fun <X, Y: Any> fkzipMap(f: (S) -> (T) -> X): ITKMap<X,Y> where X: Any, X: Comparable<X>
    fun <X, Y: Any> fkzippMap(f: (S, T) -> X): ITKMap<X,Y> where X: Any, X: Comparable<X> {
        val pf: (S) -> (T) -> X = { s: S -> { t: T -> f(s, t) } }
        return fkzipMap(pf)
    }
//    fun <X: Any> fkkartMap(f: (S) -> (T) -> X ): ITKMap<ITKMap<X>>
//    fun <X: Any> fkkartpMap(f: (S, T) -> X ): ITKMap<ITKMap<X>> {
//        val pf: (S) -> (T) -> X = { s: S -> { t: T -> f(s, t) } }
//        return fkkartMap(pf)
//    }
    fun asKMap(): ITKMap<S,T>
    fun equal(rhs: ITKZMap<@UnsafeVariance S, @UnsafeVariance T>): Boolean = equals(rhs)
    fun softEqual(rhs: Any?): Boolean = equals(rhs) // TODO Companion.softEqual(this, rhs)

//    companion object { }
}

typealias ITKMap<K, S> = IMKMapOp<K, S, IMCommon<TKVEntry<K, S>>>

interface IMKMapOp<out K, out V: Any, out U: IMCommon<TKVEntry<K,V>>> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun <L, T: Any> fmap(f: (TKVEntry<K,V>) -> TKVEntry<L,T>): IMKMapOp<L,T,IMCommon<TKVEntry<L,T>>> where L: Any, L: Comparable<@UnsafeVariance L>
}

// =========================================================== IMApp

typealias ITApp<S> = IMAppOp<S, IMMapOp<S, IMCommon<S>>>

interface IMAppOp<out S: Any, out U: ITMap<S>>: IMCommon<S> {

    fun asITMap(): ITMap<S> = (@Suppress("UNCHECKED_CAST") (this as ITMap<S>))

    // maintains the container of 'this'
    fun <T: Any> fmapp(f: (S) -> T): ITApp<T> =
        flift2App(asITMap().fmap(f))!!

    // drops the container of 'this' and replaces with generic container
    fun <T: Any> fmappx(f: (S) -> T): ITApp<T> {
        fun fLifted(u: ITApp<S>): (IMCommon<(S) -> T>) -> ITMap<T> = { g: IMCommon<(S) -> T> ->
            check(1 == g.fsize())
            u.asITMap().fmap(g.fpick()!!)
        }
        return flift2App(DWCommon.of(f))!!.fapp(fLifted(this))
      }

    fun <T: Any> fapp(op: (U) -> ITMap<T>): ITApp<T>

    // TODO this needs be protected under type invariance
    fun <T: Any, X: Any> fmap2(v: ITApp<@UnsafeVariance T>, f:(S, T) -> X): ITApp<X> {
        val kart: IMMapOp<IMZPair<S, T>, IMCommon<IMZPair<S, T>>> = FCartesian.of<S,T>(asITMap()).mpro(v.asITMap())!!
        return flift2App(kart.fmap { f(it._1(), it._2())  })!!
    }

    // ap(map(fa)(a => (b: B) => f(a,b)))(fb)

    fun <T: Any, V: ITMap<T>> fmaprod(f: (U) -> V): ITMap<Pair<U,V>> {
        fun fLifted(u: ITApp<S>): ((ITMap<S>) -> ITMap<T>) -> Pair<ITMap<S>,ITMap<T>> = { g: (ITMap<S>) -> ITMap<T> ->
            val aux: ITMap<T> = u.fapp(g).asITMap()
            Pair(u.asITMap(), aux)
        }
        val fmapp: ITApp<S> = flift2App(this)!!
        val faux: ((U) -> V) -> ITMap<Pair<U,V>> = { _: (U) -> V ->
            val aux = @Suppress("UNCHECKED_CAST") (fLifted(fmapp) as ((U) -> V) -> ITMap<Pair<U,V>>)
            IMMapOp.flift2Map(aux(f))!!
        }
        return faux(f)
    }

    // will return: success, if all succeed; xor: only errors (all errors), if any
    fun <R: Any> ftraverse(op: (S) -> R): IMSdj<IMCommon<String>, IMCommon<R>> {
        val fail: (S?) -> String = { v:S? -> "op error during traversal, ${v?.let{ it::class }} item $v" }
        val impl: TraversalImpl<S, String> = TraversalImpl(fail, this)
        return impl.traverse<R, String>(op)
    }

    // will return any and all success, and any and all failures
    fun <R: Any> fgrossTraverse(op: (S) -> R): Pair<IMCommon<String>, IMCommon<R>> {
        val fail: (S?) -> String = { v:S? -> "op error during traversal, ${v?.let{ it::class }} item $v" }
        val impl: TraversalImpl<S, String> = TraversalImpl(fail, this)
        return impl.grossTraverse<R, String>(op)
    }

    fun <R: Any, E: Any> ftraverseWithError(op: (S) -> R, toError: ((String) -> E)): TSDJ<IMCommon<E>, IMCommon<R>> {
        val fail: (S?) -> E = { v:S? -> toError("for:${v?.let{ it::class }} item $v") }
        val impl: TraversalImpl<S, E> = TraversalImpl(fail, this)
        return impl.traverse<R,E>(op)
    }

//    infix fun <T: Any, V: FMapp<T>, W: Pair<@UnsafeVariance S,T>> kmapp(tmapp: V): FMapp<W> =
//        if (fempty() || tmapp.fempty()) DWFMapp.empty() else when(this) {
//            is IMZipMap<*,*> -> {
//                val bbb = this
//                print(bbb)
//                this.fapp
//                TODO()
//            }
//            !is IMOrdered<*> -> DWFMapp.empty()
//            else -> {
//                @Suppress("UNCHECKED_CAST") (this as IMOrdered<S>)
//                val fkart: FMap<Pair<S, T>>? = IMCartesian.flift2kart<S, T>(this).mpro(tmap)
//                fkart?.let { @Suppress("UNCHECKED_CAST") (it as FMap<W>) } ?: DWFMap.empty()
//                TODO()
//            }
//        }

    companion object {
        // fun <T: Any> equal(rhs: FMapp<T>, lhs: FMapp<T>): Boolean = TODO()

        fun <T: Any> flift2App(item: ITMap<T>) = IM.liftToIMApplicable(item)
        fun <T: Any> flift2App(item: IMCommon<T>): ITApp<T>? = IMMapOp.flift2Map(item)?.let { mappable -> flift2App(mappable) }
        fun <T: Any> flift2App(item: T): ITApp<T>? {
            check(item !is IMCommon<*>)
            return IMMapOp.flift2Map(item).let { mapOp: IMMapOp<T, IMCommon<T>> -> flift2App(mapOp) }
        }
        /*
         infix fun <S: Any, T: Any, W: Any> FMap<S>.kmap(tmap: FMap<T>): ((S) -> T) -> W = { s2t: (S) ->T -> { t2w: (T) -> W ->
            @Suppress("UNCHECKED_CAST") (this as IMOrdered<S>)
            val aux: FMap<Pair<S, T>>? = IMCartesian.flift2kart<S,T>(this).mpro(tmap)
            val foo = aux.fmap<W> { it: Pair<S, T> -> }
            TODO()

        }}

        infix fun <A: Any, B: Any, C: Any> ITMapp<A>.kmapp(bk: ITMapp<B>): ITMapp<C> = if (fempty() || bk.fempty()) flift2mapp(DWFMap.empty())!! else {
            @Suppress("UNCHECKED_CAST") (this as IMOrdered<A>)
            IMCartesian.flift2kart<A,B>(this).mpro(bk.asITMap())
            TODO()
        }

         */
    }
}

typealias ITOApp<S> = IMOrderedAppOp<S, IMMapOp<S, IMOrdered<S>>>

interface IMOrderedAppOp<out S: Any, out U: IMMapOp<S, IMOrdered<S>>>: IMOrdered<S>, IMAppOp<S,U> { }


/*
interface IMCartesian<out S: Any, out U: FMap<S>, out T: Any, out V: FMap<T>, out W:Pair<U,V>> {

    infix fun mpro(t: FMap<@UnsafeVariance T>): FMap<W>?
    infix fun opro(t: IMOrdered<@UnsafeVariance T>): FMap<W>?

    companion object {
        fun <S: Any, T: Any> flift2kart(item: IMOrdered<S>): FKart<S,T> = FCartesian.of(item)

    }
}

 */

// simple disjunction
interface IMDj<out L, out R>: IMOrdered<IMDj<L,R>>,
    IMDjFiltering<L, R> {
    companion object {
        fun <A, B, L:Any, R: Any> bifold(djs: IMCommon<IMDj<A,B>>, acc:Pair<IMList<L>, IMList<R>>? = null): ((A) -> L, (B) -> R) -> Pair<IMList<L>, IMList<R>> = { fl: (A) -> L, fr: (B) -> R ->
            val biAcc = acc ?: Pair(FList.emptyIMList(),FList.emptyIMList())
            fun apportion (acc:Pair<IMList<L>, IMList<R>>, item:IMDj<A,B>): Pair<IMList<L>, IMList<R>> =
                item.right()?.let { dr ->
                    Pair(acc.first, acc.second.tibList<R>()!!.fprepend(fr(dr), acc.second))
                } ?: Pair( acc.first.tibList<L>()!!.fprepend(fl(item.left()!!),acc.first), acc.second)
            djs.ffold(biAcc, ::apportion)
        }
    }
}

// higher kind disjunction
interface IMSdj<out L, out R>: IMDj<L,R>,
    ITMap<IMDj<L,R>>,
    ITApp<IMDj<L,R>> {
}

typealias ITDcw<A> = IMDicw<A, IMCommon<A>>

// Di-sposable wrappers

interface IMDicw<out A: Any, out B: IMCommon<A>>:
    IMCommon<A>,
    IMOrdered<A>

typealias ITDmw<A> = IMDimw<A, IMCommon<A>>

interface IMDimw<out A: Any, out B: IMCommon<A>>:
    IMMapOp<A, IMDimw<A,B>>,
    IMOrdered<A>

typealias ITDaw<A> = IMDiaw<A, IMCommon<A>>

interface IMDiaw<out A: Any, out B: IMCommon<A>>:
    IMMapOp<A, IMDiaw<A,B>>,
    IMAppOp<A, ITMap<A>>,
    IMOrdered<A>

fun interface EqualsProxy {
    override fun equals(other: Any?): Boolean
}

fun interface HashCodeProxy {
    override fun hashCode(): Int
}