package com.xrpn.immutable

import com.xrpn.imapi.ErrExReport
import com.xrpn.imapi.ErrorReportTrap
import com.xrpn.imapi.TSDJ
import com.xrpn.imapi.TSDJValid

data class TestProduct(val intNum: Int, val strMsg: String)

fun tpIntNumValidation(legalValueRange: IntRange): (TestProduct) -> TSDJ<ErrExReport<String>, TestProduct> =
    { tp -> if (legalValueRange.contains(tp.intNum)) TSDJValid(tp)
            else ErrorReportTrap(ErrExReport("bad int", IllegalArgumentException("out of range:'${tp.intNum}'")))
    }

fun tpStrMsgValidation(legalLengthRange: IntRange): (TestProduct) -> TSDJ<ErrExReport<String>, TestProduct> =
    { tp -> if (legalLengthRange.contains(tp.strMsg.length)) TSDJValid(tp)
            else ErrorReportTrap(ErrExReport("bad string", IllegalArgumentException("out of range:'${tp.strMsg}'")))
    }
