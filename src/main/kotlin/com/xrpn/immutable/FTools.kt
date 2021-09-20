package com.xrpn.immutable

// inline fun <reified A, reified B> isSameGeneric(a: A, b: B): Boolean = (a is B) && (b is A)
fun <A, B> isSameType(a: A, b: B): Boolean = a?.let{ outer -> outer::class == b?.let{ it::class } } ?: false
inline fun <reified A> fidentity(a: A): A = a

fun <A, B> Pair<A, A>.pmap1(f: (A) -> B): Pair<B, B> = Pair(f(this.first), f(this.second))
fun <A, B, C, D> Pair<A, B>.pmap2(f: (A) -> C, g: (B) -> D): Pair<C, D> = Pair(f(this.first), g(this.second))
fun <A: Any> Pair<A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLNil))

fun <A, B> Triple<A, A, A>.tmap1(f: (A) -> B): Triple<B, B, B> = Triple(f(this.first), f(this.second), f(this.third))
fun <A, B, C, D, E, F> Triple<A, B, C>.tmap3(f: (A) -> D, g: (B) -> E, h: (C) -> F): Triple<D, E, F> = Triple(f(this.first), g(this.second), h(this.third))
fun <A: Any> Triple<A, A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLCons(this.third, FLNil)))

internal enum class FBTFIT {
    LEFT, RIGHT, EQ
}
