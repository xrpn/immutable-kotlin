package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FList.Companion.emptyIMList

interface MappValidation<T: Any, E: Any> {
    fun validation(candidates: FMapp<T>): IMSdj<IMList<E>, IMList<T>>
}

interface IM_Validation<V: Any, U: FMapp<V>, D: Any>: IM_Traversal<V,U,D> {
    fun <E: Any> validate (
        criterium: (V) -> TSDJ<E,V>,
        fail: (E?) -> D
    ): TSDJ<IMCommon<D>, IMCommon<V>> = traverse(criterium, fail) { it!! }
}

data class FSingleMappValidation<T: Any, E: Any> (
    val criterium: (T) -> TSDJ<E,T>,
    val fail: (E?) -> E
): MappValidation<T, E> by FMultiMappValidation(FList.of(criterium), fail)

data class FMultiMappValidation<T: Any, E: Any> (
    val criteria: IMCommon<(T) -> IMSdj<E,T>>,
    val fail: (e: E?) -> E
): MappValidation<T,E> {

    private fun assess(candidate: T): IMList<IMSdj<E, T>> = criteria.ffold(emptyIMList()) { resAcc, test ->
        resAcc.fprepend(test(candidate).bimap(fail) { id -> id } as IMSdj<E, T>)
    }

    private fun accumulate(src: FMap<T>): IMSdj<IMList<E>, IMList<T>> {

        fun f4innerFold(lrAcc: Pair<IMList<E>, IMList<T>>, disj: IMSdj<E, T>): Pair<IMList<E>, IMList<T>> {
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

    fun process(candidates: FMapp<T>): IMSdj<IMList<E>, IMList<T>> = candidates.fapp(::accumulate) as IMSdj<IMList<E>, IMList<T>>

    override fun validation(candidates: FMapp<T>): IMSdj<IMList<E>, IMList<T>> {
        val fmapp = process(candidates)
        check(1 == fmapp.fsize())
        return fmapp
    }
}
