package com.xrpn.immutable

data class TestProduct(val intNum: Int, val strMsg: String)

fun tpIntNumValidation(r: IntRange): (TestProduct) -> Pair<Boolean, Exception?> =
    { tp -> if (r.contains(tp.intNum)) Pair(true,null) else Pair(false,IllegalStateException("out of range:${tp.intNum}"))}

fun tpStrMsgValidation(r: IntRange): (TestProduct) -> Pair<Boolean, Exception?> =
    { tp -> if (r.contains(tp.strMsg.length)) Pair(true,null) else Pair(false,IllegalStateException("out of range:${tp.strMsg}"))}
