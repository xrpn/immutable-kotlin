package com.xrpn.imapi

import com.xrpn.immutable.*
import java.io.PrintStream
import kotlin.reflect.KClass

private fun <A: Any, B: Any> isSameType(a: A?, b: B?): Boolean = a?.let{ outer: A -> outer::class == b?.let{ it::class } } ?: false
private fun <A: Any, B: Any> isSameTypeLike(a: A?, b: KClass<B>?): Boolean = a?.let{ outer: A -> outer::class == b } ?: false
private fun <A: Any, B> isLikeSameType(a: KClass<A>, b: B): Boolean = a == b?.let{ it::class }
private fun <A: Any, B: KClass<Any>?> isLikeSameTypeLike(a: KClass<A>, b: B?): Boolean = a == b

fun <A, B> Pair<A, A>.pmap1(f: (A) -> B): Pair<B, B> = Pair(f(this.first), f(this.second))
fun <A, B, C, D> Pair<A, B>.pmap2(f: (A) -> C, g: (B) -> D): Pair<C, D> = Pair(f(this.first), g(this.second))
fun <A, B, C> Pair<A, B>.partial2map(f: (A) -> (B) -> C): C = f(this.first)(this.second)
fun <A, B, C, D> Pair<Pair<A, B>, C>.partial3map(f: (A) -> (B) -> (C) -> D): D = f(this.first.first)(this.first.second)(this.second)
fun <A, B, C, D, E> Pair<Pair<Pair<A, B>, C>, D>.partial4map(f: (A) -> (B) -> (C) -> (D) -> E): E = f(this.first.first.first)(this.first.first.second)(this.first.second)(this.second)

fun <A: Any, B: Any> IMZPair<A, A>.pmap1(f: (A) -> B): IMZPair<B, B> = ZW(f(this._1()), f(this._2()))
fun <A: Any, B: Any, C: Any, D: Any> IMZPair<A, B>.pmap2(f: (A) -> C, g: (B) -> D): IMZPair<C, D> = ZW(f(this._1()), g(this._2()))
fun <A: Any, B: Any, C: Any> IMZPair<A, B>.partial2map(f: (A) -> (B) -> C): C = f(this._1())(this._2())
fun <A: Any, B: Any, C: Any, D: Any> IMZPair<IMZPair<A, B>, C>.partial3map(f: (A) -> (B) -> (C) -> D): D = f(this._1()._1())(this._1()._2())(this._2())
fun <A: Any, B: Any, C: Any, D: Any, E: Any> IMZPair<IMZPair<IMZPair<A, B>, C>, D>.partial4map(f: (A) -> (B) -> (C) -> (D) -> E): E = f(this._1()._1()._1())(this._1()._1()._2())(this._1()._2())(this._2())

fun <A: Any> Pair<A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLNil))
fun <K, A: Any> Pair<K, A>.toTKVEntry(): TKVEntry<K, A> where K: Any, K: Comparable<K> = TKVEntry.ofkv(this.first, this.second)
fun <K, A: Any> Pair<K, A>.toTKVEntry(cc: Comparator<K>): TKVEntry<K, A> where K: Any, K: Comparable<K> = TKVEntry.ofkvc(this.first, this.second, cc)
fun <A, B> Pair<A, B>.fne() = this.first?.let { it } ?: this.second!!

fun <A, B> Triple<A, A, A>.tmap1(f: (A) -> B): Triple<B, B, B> = Triple(f(this.first), f(this.second), f(this.third))
fun <A, B, C, D, E, F> Triple<A, B, C>.tmap3(f: (A) -> D, g: (B) -> E, h: (C) -> F): Triple<D, E, F> = Triple(f(this.first), g(this.second), h(this.third))
fun <A: Any> Triple<A, A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLCons(this.third, FLNil)))


fun <A, B> A.isStrictly(b: B): Boolean = when(this) {
    is KClass<*> -> null != b && when(b) {
        is KClass<*> -> isLikeSameTypeLike(this, @Suppress("UNCHECKED_CAST") (b as? KClass<Any>))
        else -> isLikeSameType(this, b)
    }
    null -> false
    else -> null != b && when(b) {
        is KClass<*> -> isSameTypeLike(this, b)
        else -> isSameType(this, b)
    }
}

fun <A, B> A.isStrictlyNot(b: B): Boolean = when(this) {
    is KClass<*> -> null != b && when(b) {
        is KClass<*> -> !isLikeSameTypeLike(this, @Suppress("UNCHECKED_CAST") (b as? KClass<Any>))
        else -> !isLikeSameType(this, b)
    }
    null -> true
    else -> null != b && when(b) {
        is KClass<*> -> {
            val foo = !isSameTypeLike(this, b)
            foo }
        else -> !isSameType(this, b)
    }
}

data class KeyedTypeSample<K: KClass<*>?, V: KClass<*>>(val kKc: K, val vKc: V) {
    fun hasKey(): Boolean = null != kKc
    fun isSymRkc(): Boolean = hasKey() && kKc == vKc
    fun isIntRkc(): Boolean = hasKey() && kKc == Int::class
    fun isStrRkc(): Boolean = hasKey() && kKc == String::class
    fun isLikeIfLooselyKey(kClass: KClass<*>?, vClass: KClass<*>): Boolean =( !hasKey() || kKc == kClass ) && vKc == vClass
    fun isLike(kClass: KClass<*>?, vClass: KClass<*>): Boolean = kKc == kClass && vKc == vClass
    fun <KK: KClass<*>?, VV: KClass<*>> isStrictly(other: KeyedTypeSample<KK, VV>): Boolean = (kKc == other.kKc && vKc == other.vKc)
    fun isLikeKey(kClass: KClass<*>?): Boolean = kKc == kClass
    fun isLikeValue(vClass: KClass<*>?): Boolean = vKc == vClass
}

fun <T: Any> IMCommon<T>?.toIMMapplicable(): ITMapp<T>? =
    this?.let { IMMappOp.flift2mapp(it) }

inline infix fun <B, C, A> ((B) -> C).fKompose(crossinline f: (A) -> B): (A) -> C = { a: A -> this(f(a)) }

//infix fun <B : Any, C : Any, A : Any> (FMapp<A>).fmappKompose(fmapp: FMapp<B>): ((FMap<A>) -> FMap<B>) -> FMapp<C> {
//    fun foo (arg1: ((FMap<B>) -> FMap<C>) -> FMapp<C>, arg2: ((FMap<A>) -> FMap<B>) -> FMapp<B>): ((FMap<A>) -> FMap<B>) -> FMapp<C> = { op: (FMap<A>) -> FMap<B> ->
//        fmapp.fapp(this.fapp(op).asFMap())
//    }
//    return foo(fmapp::fapp, this::fapp)
//}

object IM {

    fun <A, B: Any> accept(src: TKVEntry<A, B>, dest: IMBTree<A, B>): Boolean where A: Any, A: Comparable<A> {
        val goodness: Pair<Boolean, TKVEntry<A, B>?> = when {
            dest.fempty() -> Pair(true, null)
            else -> {
                val entry = dest.froot()!!
                Pair(src.strictly(entry.untype())
                        && src.getvKc().isStrictly(entry.untype().getvKc())
                        && src.getkKc().isStrictly(entry.untype().getkKc()), entry)
            }
        }
        if (!goodness.first) {
            val atDest = goodness.second!!
            val newEntry = "new entry: ${src.getv().hashCode()}:${src.getv()} AS <$src>"
            val treeData = "tree data: ${atDest.getv().hashCode()}:${atDest.getv()} AS <${atDest}>"
            src.errLog().emitUnconditionally("no acceptance when inserting $newEntry as $treeData")
        }
        return goodness.first
    }


    fun <A: Any, B: Any> maybe2(a: A?, b: B?): Pair<A,B>? = a?.let { b?.let { Pair(a,b) } }
    fun <A, B, C> maybe3(a: A, b: B, c: C): Triple<A,B,C>? = a?.let { b?.let {  c?.let { Triple(a,b,c) } } }

    fun <S: Any, T: Any> fmapOfCommon(
        u: ITMap<S>,
    ): (IMCommon<(S) -> T>) -> ITMap<T> = { g: IMCommon<(S) -> T> -> u.fmap(g.fpick()!!) }

    fun <B: Any> liftToIMMappable(item: IMCommon<B>): IMMapOp<B, IMCommon<B>>? = when(item) {
        is IMList -> item
        is IMSet -> item.asIMSetNotEmpty()?.let { it -> it.vcvdj().bireduce(
            { id -> id },
            { id -> @Suppress("UNCHECKED_CAST") (id as IMSet<B>) })
        } ?: item
        is IMStack -> item
        is IMQueue -> item
        is IMBTree<*, *> -> TODO()
        is IMMap<*, *> -> TODO()
        is IMHeap -> item
        is IMDisw<B, IMCommon<B>> -> DWFMap.of(item)
        is IMDimw<B, IMCommon<B>> -> item
        is IMSdj<*,*> -> @Suppress("UNCHECKED_CAST") (item as IMMapOp<B, IMCommon<B>>)
        is IMZipMap<*,*,*> -> @Suppress("UNCHECKED_CAST") (item.asMap() as IMMapOp<B, IMCommon<B>>)
        else -> if (!(IMMapOp::class.isInstance(item))) null
                else throw RuntimeException("internal error, unknown IMCommon:'${item::class}'")

    }

    fun <A: Any> liftToIMMapplicable(item: IMMapOp<A, IMCommon<A>>): IMMappOp<A, IMMapOp<A, IMCommon<A>>>? = when(item) {
        is IMList -> item
        is IMSet -> item
        is IMStack -> item
        is IMQueue -> item
        is IMHeap -> item
        is IMBTree<*, *> -> TODO()
        is IMMap<*, *> -> TODO()
        is IMDimw<A, IMCommon<A>> -> DWFMapp.of(item)
        is IMDiaw<A, IMCommon<A>> -> item
        is IMSdj<*,*> -> @Suppress("UNCHECKED_CAST") (item as IMMappOp<A, IMMapOp<A, IMCommon<A>>>)
        else -> if (!(IMMappOp::class.isInstance(item))) null
                else throw RuntimeException("internal error, unknown IMMappable:'${item::class}'")
    }
}

interface IMLogging  {
    val logStream: PrintStream
    val emitterClass: String
    val emitterString: String?
    fun emitUnconditionally(msg: String) = logStream.println("$emitterClass${emitterString?.let {"'$it'"}} : $msg")
    fun emitUnconditionally(e: Throwable) = e.printStackTrace(logStream)
}