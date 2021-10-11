package com.xrpn.immutable

sealed class NDJ2<out A: Any, out B: Any>(open val a: A?, open val b: B?) {

    fun <C: Any> to(c: C): (() -> C)? = when (this) {
        is NDA<*,*> -> {
            (@Suppress("UNCHECKED_CAST") (this as NDA<A,B>))
            if (aKc.isInstance(c)) @Suppress("UNCHECKED_CAST") {{(a as C)}} else { null }
        }
        is NDB<*,*> -> {
            (@Suppress("UNCHECKED_CAST") (this as NDB<A,B>))
            if (bKc.isInstance(c)) @Suppress("UNCHECKED_CAST") {{(b as C)}} else { null }
        }
    }

    fun <C: Any, D: Any> toOr(first: C, second: D): Pair<(() -> C)?,(() -> D)?>? {
        val aux = Pair(to(first),to(second))
        return if ((aux.first == null) && (aux.second == null)) null else aux
    }

    fun <C: Any, D: Any> toAnd(first: C, second: D): Pair<(() -> C)?,(() -> D)?>? {
        val aux = Pair(to(first),to(second))
        return if ((aux.first == null) || (aux.second == null)) null else aux
    }

    fun <C: Any, D: Any> toXor(first: C, second: D): Pair<(() -> C)?,(() -> D)?>? {
        val aux = Pair(to(first),to(second))
        if (null == aux.first) return aux.second?.let { aux }
        if (null == aux.second) return aux.first?.let { aux }
        return null
    }

    fun recover(): Pair<(() -> A)?,(() -> B)?> = when (this) {
        is NDA<*,*> -> {
            (@Suppress("UNCHECKED_CAST") (this as NDA<A,B>))
            Pair({ a } , null)
        }
        is NDB<*,*> -> {
            (@Suppress("UNCHECKED_CAST") (this as NDB<A,B>))
            Pair( null, { b })
        }
    }

    companion object {
        fun <A: Any, B: Any> of(aval: A?, bval: B?) = when {
            aval == null && bval == null -> null
            aval != null -> NDA(aval, bval)
            bval != null -> NDB(bval, aval)
            else -> null
        }
    }
}

data class NDA<out A: Any, out B:Any>(override val a: A, override val b: B? = null): NDJ2<A,B>(a, b) { internal val aKc = a::class }
data class NDB<out A: Any, out B:Any>(override val b: B, override val a: A? = null): NDJ2<A,B>(a, b) { internal val bKc = b::class }

sealed class EDJ2<out A,out B>() {
    fun getIfA(): A? = when (this) {
        is EDA -> a
        is EDB -> null
    }
    fun getIfB(): B? = when (this) {
        is EDA -> null
        is EDB -> b
    }
}

data class EDA<out A>(val a: A): EDJ2<A,Nothing>()
data class EDB<out B>(val b: B): EDJ2<Nothing, B>()