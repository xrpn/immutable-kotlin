package com.xrpn.immutable

import com.xrpn.imapi.ErrExReport
import com.xrpn.imapi.ErrRTrap
import com.xrpn.imapi.TSDJ
import com.xrpn.imapi.TSDJValid

data class TestProduct(val intNum: Int, val strMsg: String)

fun tpIntNumValidation(r: IntRange): (TestProduct) -> TSDJ<ErrExReport, TestProduct> =
    { tp -> if (r.contains(tp.intNum)) TSDJValid(tp) else ErrRTrap(ErrExReport("bad int: $tp", IllegalArgumentException("out of range:${tp.intNum}")))}

fun tpStrMsgValidation(r: IntRange): (TestProduct) -> TSDJ<ErrExReport, TestProduct> =
    { tp -> if (r.contains(tp.strMsg.length)) TSDJValid(tp) else ErrRTrap(ErrExReport("bad string: $tp", IllegalArgumentException("out of range:${tp.strMsg}")))}
