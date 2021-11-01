package com.xrpn.imapi

sealed class /* Trivially Simple DisJunction */ TSDJ<out A, out B>() {
    fun left(): A? = when (this) {
        is TSDL -> a
        else -> null
    }
    fun right(): B? = when (this) {
        is TSDR -> b
        else -> null
    }
}

data class TSDL<out A>(val a: A): TSDJ<A, Nothing>()
data class TSDR<out B>(val b: B): TSDJ<Nothing, B>()

