package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FList.Companion.emptyIMList

interface MappTraversal<T: Any, S: Any, D: Any> {
    fun traversal(candidates: ITMapp<T>): IMSdj<IMList<D>, IMList<S>>
    fun grossTraversal(candidates: ITMapp<T>): Pair<IMList<D>, IMList<S>>
    fun refine(allPairs: Pair<IMList<D>, IMList<S>>): IMSdj<IMList<D>, IMList<S>>
}

interface IM_Traversal<out V: Any, out U: ITMapp<V>, out D: Any> {
    fun <E: Any, S: Any> traverse(
        operation: (V) -> TSDJ<E, @UnsafeVariance V>,
        fail: (E?) -> @UnsafeVariance D,
        pass: (V?) -> S
    ): TSDJ<IMCommon<D>, IMCommon<S>>
}

internal data class TraversalImpl<T: Any, ER: Any> (
    val fail: (T?) -> ER,
    val candidate: ITMapp<T>
) {

    private fun <R: Any, E: Any> gross(
        op: (T) -> R
    ): Pair<FSingleMappTraversal<T, Pair<T,R>, E, R, ErrExReport<E>>, Pair<IMList<ErrExReport<E>>, IMList<R>>> {

        fun tryOp(v:T): TSDJ<E, Pair<T,R>> = try {
            val r = op(v)
            TSDJValid(Pair(v,r))
        } catch (ex: Exception) {
            @Suppress("UNCHECKED_CAST") (ErrorReportTrap(ErrExReport(fail(v), ex).longMsg()) as TSDJ<E, Pair<T,R>>)
        }

        fun <E: Any> ffail(e: E?): ErrExReport<E> = ErrExReport(e, null).shortMsg()

        val worker: FSingleMappTraversal<T, Pair<T,R>, E, R, ErrExReport<E>> = FSingleMappTraversal(::tryOp, ::ffail) { it!!.second }
        val aux: Pair<IMList<ErrExReport<E>>, IMList<R>> = worker.grossTraversal(candidate)
        return Pair(worker, aux)
    }


    fun <R: Any, E: Any> traverse(op: (T) -> R): TSDJ<IMCommon<ER>, IMCommon<R>> {
        val intermediate = gross<R,E>(op)
        val aux: IMSdj<IMList<ErrExReport<E>>, IMList<R>> = intermediate.first.refine(intermediate.second)
        val res = @Suppress("UNCHECKED_CAST") (aux as TSDJ<IMCommon<ER>, IMCommon<R>>)
        return res
    }

    fun <R: Any, E: Any> grossTraverse(op: (T) -> R): Pair<IMCommon<ER>, IMCommon<R>> {
        val intermediate: Pair<IMList<ErrExReport<E>>, IMList<R>> = gross<R,E>(op).second
        val res = @Suppress("UNCHECKED_CAST") (intermediate as Pair<IMCommon<ER>, IMCommon<R>>)
        return res
    }
}

data class FSingleMappTraversal<T: Any, S: Any, E: Any, TR: Any, ER: Any> (
    val operation: (T) -> TSDJ<E,S>,
    val fail: (E?) -> ER,
    val pass: (S?) -> TR
): MappTraversal<T,TR,ER> by FMultiMappTraversal(FList.of(operation), fail, pass)

data class FMultiMappTraversal<T: Any, R: Any, E: Any, TR: Any, ER: Any> (
    val operations: IMCommon<(T) -> TSDJ<E,R>>,
    val fail: (E?) -> ER,
    val pass: (R) -> TR
): MappTraversal<T,TR,ER> {

    private fun run(candidate: T): IMList<IMSdj<ER, TR>> = operations.ffold(emptyIMList()) { resAcc, singleOp ->
        resAcc.fprepend(
            try {
                singleOp(candidate).bimap(fail, pass)
            } catch (ex: Exception) {
                TSDJInvalid(fail(null))
            }
        )
    }

    private fun accumulate(src: ITMap<T>): Pair<IMList<ER>, IMList<TR>> {

        fun f4fold(lrAcc: Pair<IMList<ER>, IMList<TR>>, item: T): Pair<IMList<ER>, IMList<TR>> {
            val aux: IMList<IMSdj<ER, TR>> = run(item)
            val obs = IMDj.bifold(aux, lrAcc)({ it }, { it })
            return obs
        }

        val lSeed = emptyIMList<ER>()
        val rSeed = emptyIMList<TR>()
        return src.ffold(Pair(lSeed, rSeed), ::f4fold)
    }

    override fun refine(allPairs: Pair<IMList<ER>, IMList<TR>>): IMSdj<IMList<ER>, IMList<TR>> {
        val (fail: IMList<ER>, pass: IMList<TR>) = allPairs
        return if (fail.fempty()) TSDJValid(pass.freverse()) else /* errors are in reverse order */ TSDJInvalid(fail)
    }

    private fun collect(src: ITMap<T>): IMSdj<IMList<ER>, IMList<TR>> =
        refine(accumulate(src))

    private fun process(candidates: ITMapp<T>): IMSdj<IMList<ER>, IMList<TR>> = candidates.fapp(::collect) as IMSdj<IMList<ER>, IMList<TR>>

    override fun traversal(candidates: ITMapp<T>): IMSdj<IMList<ER>, IMList<TR>> =
        if (candidates.fempty()) IMMappOp.flift2mapp(TSDJValid(emptyIMList<TR>())) as TSDJ<IMList<ER>, IMList<TR>>
        else {
            val fmappOut: IMSdj<IMList<ER>, IMList<TR>> = process(candidates)
            check(1 == fmappOut.fsize())
            fmappOut
        }

    override fun grossTraversal(candidates: ITMapp<T>): Pair<IMList<ER>, IMList<TR>> =
        if (candidates.fempty()) Pair(emptyIMList(), emptyIMList())
        else accumulate(candidates.asFMap())

}