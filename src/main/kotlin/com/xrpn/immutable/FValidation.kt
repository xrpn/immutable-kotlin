package com.xrpn.immutable

import com.xrpn.imapi.*

data class FValidation<T: Any, E: Any> (
    val criterium: (T) -> TSDJ<E,T>,
    val fail: (e: E?) -> E
) {

    fun assess(candidate: T): TSDJ<E, T> = criterium(candidate).bimap(fail) { id -> id }

    fun applicanda(src: IMMappable<T, IMCommon<T>>): IMMappable<TSDJ<E, T>, IMCommon<TSDJ<E, T>>> =
        src.fmap { assess(it) }

    companion object {

        fun <E: Any, T: Any> failAllOrPass(
            fail: (e: E?) -> E
        ): ((T) -> TSDJ<E, T>) ->
            (IMMapplicable<T, IMMappable<T, IMCommon<T>>>) ->
            IMMapplicable<TSDJ<E, T>, IMMappable<TSDJ<E, T>, IMCommon<TSDJ<E, T>>>> = { criterium ->
                { subject ->
                    subject.fmapply(FValidation(criterium, fail)::applicanda)
                }
            }
    }

}
