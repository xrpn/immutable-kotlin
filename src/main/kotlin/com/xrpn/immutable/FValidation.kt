package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FList.Companion.emptyIMList

interface IMMappValidation<T: Any, E: Any> {
    fun validation(candidates: FMapp<T>): TSDJ<IMList<E>, IMList<T>>
}

data class FSingleValidation<T: Any, E: Any> (
    val criterium: (T) -> TSDJ<E,T>,
    val fail: (e: E?) -> E
): IMMappValidation<T, E> by FCommonValidation(FList.of(criterium), fail) {}
//    private fun assess(candidate: T): TSDJ<E, T> = criterium(candidate).bimap(fail) { id -> id }
//    private fun tabulate(src: FMap<T>): IMSdj<IMList<E>, IMList<T>> {
//
//        fun f4fold(lrAcc: Pair<IMList<E>,IMList<T>>, item: T):  Pair<IMList<E>,IMList<T>> {
//            val (lAcc, rAcc) = lrAcc
//            return assess(item).bireduce({ ld -> Pair(lAcc.fprepend(ld), rAcc) },{ rd -> Pair(lAcc, rAcc.fprepend(rd)) })
//        }
//
//        val lSeed = emptyIMList<E>()
//        val rSeed = emptyIMList<T>()
//        val (fail: IMList<E>, pass: IMList<T>) = src.ffold(Pair(lSeed,rSeed), ::f4fold)
//        return if (fail.fempty()) TSDJValid(pass) else TSDJInvalid(fail)
//    }
//
//    fun on(candidates: FMapp<T>): FMapp<TSDJ<IMList<E>, IMList<T>>> = candidates.fappro(::tabulate)
//
//     fun <T: Any, E: Any> validate(candidates: FMapp<T>, test:(T) -> TSDJ<E,T>, onError: (e: E?) -> E): TSDJ<IMList<E>, IMList<T>> {
//        val fmapp = FSingleValidation(test, onError).on(candidates)
//        check(1 == fmapp.fsize())
//        return fmapp.asIMCommon<TSDJ<IMList<E>, IMList<T>>>()!!.fpick()!!
//    }

data class FCommonValidation<T: Any, E: Any> (
    val criteria: IMCommon<(T) -> TSDJ<E,T>>,
    val fail: (e: E?) -> E
): IMMappValidation<T,E> {

    private fun assess(candidate: T): IMList<TSDJ<E, T>> = criteria.ffold(emptyIMList()) { resAcc, test ->
        resAcc.fprepend(test(candidate).bimap(fail) { id -> id })
    }

    private fun tabulate(src: FMap<T>): IMSdj<IMList<E>, IMList<T>> {

        fun f4innerFold(lrAcc: Pair<IMList<E>, IMList<T>>, disj: TSDJ<E, T>): Pair<IMList<E>, IMList<T>> {
            val (lAcc, rAcc) = lrAcc
            return disj.bireduce({ ld -> Pair(lAcc.fprepend(ld), rAcc) }, { rd -> Pair(lAcc, rAcc.fprepend(rd)) })
        }

        fun f4fold(lrAcc: Pair<IMList<E>, IMList<T>>, item: T): Pair<IMList<E>, IMList<T>> =
            assess(item).ffold(lrAcc, ::f4innerFold)

        val lSeed = emptyIMList<E>()
        val rSeed = emptyIMList<T>()
        val (fail: IMList<E>, pass: IMList<T>) = src.ffold(Pair(lSeed, rSeed), ::f4fold)
        return if (fail.fempty()) TSDJValid(pass) else TSDJInvalid(fail)
    }

    fun on(candidates: FMapp<T>): FMapp<IMSdj<IMList<E>, IMList<T>>> = candidates.fappro(::tabulate)

    override fun validation(candidates: FMapp<T>): TSDJ<IMList<E>, IMList<T>> {
        val fmapp = this.on(candidates)
        check(1 == fmapp.fsize())
        return fmapp.asIMCommon<TSDJ<IMList<E>, IMList<T>>>()!!.fpick()!!
    }
}
