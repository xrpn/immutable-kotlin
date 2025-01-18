package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FList.Companion.emptyIMList

internal interface FBTreeRetrieval<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun original(): IMBTree<A,B>
}

internal interface ApplicativeTraversal<T: Any, RES: Any, ERR: Any> {
    fun traversal(candidates: ITApp<T>): IMDj<IMList<ERR>, IMList<RES>> {
        val aux = grossTraversal(candidates)
        return if (aux.first.fempty() && aux.second.fempty()) TDJ.empty() else refine(aux)
    }
    fun grossTraversal(candidates: ITApp<T>): Pair<IMList<ERR>, IMList<RES>>
    // return all if all are valid, or only the errors if there are any errors
    fun refine(allPairs: Pair<IMList<ERR>, IMList<RES>>): IMDj<IMList<ERR>, IMList<RES>>
}

//interface IM_Traversal<out V: Any, out U: ITApp<V>, out D: Any> {
//    fun <E: Any, S: Any> traverse(
//        operation: (V) -> TSDJ<E, @UnsafeVariance V>,
//        fail: (E?) -> @UnsafeVariance D,
//        pass: (V?) -> S
//    ): TSDJ<IMCommon<D>, IMCommon<S>>
//}

// same applicative -- different operations
internal data class TraversalImpl<T: Any, ER: Any> (
    val fail: (T?) -> ER,
    val candidate: ITApp<T>
) {

    private fun <R: Any, E: Any> gross(
        op: (T) -> R
    ): Pair<FSingleOperationApplicativeTraversal<T, Pair<T,R>, E, R, ErrExReport<E>>, Pair<IMList<ErrExReport<E>>, IMList<R>>> {

        fun tryOp(v:T): TDJ<E, Pair<T,R>> = try {
            val r = op(v)
            TSDJValid(Pair(v,r))
        } catch (ex: Exception) {
            @Suppress("UNCHECKED_CAST") (ErrorReportTrap(ErrExReport(fail(v), ex).longMsg()) as TSDJ<E, Pair<T,R>>)
        }

        fun <E: Any> ffail(e: E?): ErrExReport<E> = ErrExReport(e, null).shortMsg()

        val worker: FSingleOperationApplicativeTraversal<T, Pair<T,R>, E, R, ErrExReport<E>> = FSingleOperationApplicativeTraversal(::tryOp, ::ffail) { it!!.second }
        val aux: Pair<IMList<ErrExReport<E>>, IMList<R>> = worker.grossTraversal(candidate)
        return Pair(worker, aux)
    }

    fun <R: Any, E: Any> traverse(op: (T) -> R): IMDj<IMCommon<ER>, IMCommon<R>> {
        val intermediate = gross<R,E>(op)
        val emptyness = intermediate.second.first.fempty() && intermediate.second.second.fempty()
        val aux = if (emptyness) TDJ.empty() else intermediate.first.refine(intermediate.second)
        val res = @Suppress("UNCHECKED_CAST") (aux as IMDj<IMCommon<ER>, IMCommon<R>>)
        return res
    }

    fun <R: Any, E: Any> grossTraverse(op: (T) -> R): Pair<IMCommon<ER>, IMCommon<R>> {
        val intermediate: Pair<IMList<ErrExReport<E>>, IMList<R>> = gross<R,E>(op).second
        val res = @Suppress("UNCHECKED_CAST") (intermediate as Pair<IMCommon<ER>, IMCommon<R>>)
        return res
    }
}

// traverse executing a single identical operation on each and all traversed elements
// same operation -- different applicatives
data class FSingleOperationApplicativeTraversal<T: Any, S: Any, E: Any, TR: Any, ER: Any> (
    val operation: (T) -> TDJ<E,S>,
    val fail: (E?) -> ER,
    val pass: (S?) -> TR
): ApplicativeTraversal<T,TR,ER> by FMultipleOperationsApplicativeTraversal(FList.of(operation), fail, pass)

// traverse executing the same and all multiple operation on each and all traversed elements
data class FMultipleOperationsApplicativeTraversal<T: Any, R: Any, E: Any, TR: Any, ER: Any> (
    val operations: IMOrdered<(T) -> TDJ<E,R>>,
    val fail: (E?) -> ER,
    val pass: (R) -> TR
): ApplicativeTraversal<T,TR,ER> {

    private fun run(candidate: T): IMList<IMDj<ER, TR>> = operations.ffold(emptyIMList()) { resAcc, singleOp ->
        resAcc.fprepend(
            try {
                singleOp(candidate).bimap(fail, pass)
            } catch (ex: Exception) {
                TSDJInvalid(fail(null))
            }
        )
    }

    private fun accumulate(src: ITApp<T>): Pair<IMList<ER>, IMList<TR>> {

        fun f4fold(lrAcc: Pair<IMList<ER>, IMList<TR>>, item: T): Pair<IMList<ER>, IMList<TR>> {
            val aux: IMList<IMDj<ER, TR>> = run(item)
            val obs = IMDj.bifold(aux, lrAcc)({ it }, { it })
            return obs
        }

        val lSeed = emptyIMList<ER>()
        val rSeed = emptyIMList<TR>()
        return src.ffold(Pair(lSeed, rSeed), ::f4fold)
    }

    override fun refine(allPairs: Pair<IMList<ER>, IMList<TR>>): IMSdj<IMList<ER>, IMList<TR>> {
        val (fail: IMList<ER>, pass: IMList<TR>) = allPairs
        return if (fail.fempty()) TSDJValid(pass.freverse()) else  /* errors are already in reverse order */ TSDJInvalid(fail)
    }

//    private fun collect(src: ITMap<T>): IMSdj<IMList<ER>, IMList<TR>> =
//        refine(accumulate(src))
//
//    private fun process(candidates: ITApp<T>): IMSdj<IMList<ER>, IMList<TR>> = candidates.fapp(::collect) as IMSdj<IMList<ER>, IMList<TR>>
//
//    override fun traversal(candidates: ITApp<T>): IMSdj<IMList<ER>, IMList<TR>> =
//        if (candidates.fempty()) IMAppOp.flift2App(TSDJValid(emptyIMList<TR>())) as TSDJ<IMList<ER>, IMList<TR>>
//        else {
//            val fmappOut: IMSdj<IMList<ER>, IMList<TR>> = process(candidates)
//            check(1 == fmappOut.fsize())
//            fmappOut
//        }

    override fun grossTraversal(candidates: ITApp<T>): Pair<IMList<ER>, IMList<TR>> =
        if (candidates.fempty()) Pair(emptyIMList(), emptyIMList())
        else accumulate(candidates)

}

internal data class ZipTraversalImpl<T: Any, ER: Any> (
    val fail: (T?) -> ER,
    val candidate: ITApp<T>
) {

    private fun <R: Any, E: Any> zipper(
        ops: IMOrdered<(T) -> R>
    ): Pair<FZipOpApplicativeTraversal<T, Pair<T,R>, E, R, ErrExReport<E>>, Pair<IMList<ErrExReport<E>>, IMList<R>>> {

        fun tryOp(v:T, op: (T) -> R): TDJ<E, Pair<T,R>> = try {
            val r = op(v)
            TSDJValid(Pair(v,r))
        } catch (ex: Exception) {
            @Suppress("UNCHECKED_CAST") (ErrorReportTrap(ErrExReport(fail(v), ex).longMsg()) as TSDJ<E, Pair<T,R>>)
        }

        val zipOps: IMOrdered<(T) -> TDJ<E,Pair<T,R>>> = ops.ffold(emptyIMList()) { acc, it -> acc.fprepend { t: T -> tryOp(t, it) } }

        fun <E: Any> ffail(e: E?): ErrExReport<E> = ErrExReport(e, null).shortMsg()

        val worker: FZipOpApplicativeTraversal<T, Pair<T,R>, E, R, ErrExReport<E>> = FZipOpApplicativeTraversal(zipOps, ::ffail) { it!!.second }
        val aux: Pair<IMList<ErrExReport<E>>, IMList<R>> = worker.grossTraversal(candidate)
        return Pair(worker, aux)
    }

    fun <R: Any, E: Any> zipTraverse(op: IMOrdered<(T) -> R>): TSDJ<IMOrdered<ER>, IMOrdered<R>> {
        val intermediate = zipper<R,E>(op)
        val aux = intermediate.first.refine(intermediate.second)
        val res = @Suppress("UNCHECKED_CAST") (aux as TSDJ<IMOrdered<ER>, IMOrdered<R>>)
        return res
    }

    fun <R: Any, E: Any> zipGrossTraverse(op: IMOrdered<(T) -> R>): Pair<IMOrdered<ER>, IMOrdered<R>> {
        val intermediate: Pair<IMList<ErrExReport<E>>, IMList<R>> = zipper<R,E>(op).second
        val res = @Suppress("UNCHECKED_CAST") (intermediate as Pair<IMOrdered<ER>, IMOrdered<R>>)
        return res
    }
}

// traverse executing, pairwise, a collection of different operations, one operation on each traversed elements (specific for that element), for all traversed element.
data class FZipOpApplicativeTraversal<T: Any, S: Any, E: Any, TR: Any, ER: Any> (
    val zipOp: IMOrdered<(T) -> TDJ<E,S>>,
    val fail: (E?) -> ER,
    val pass: (S?) -> TR
): ApplicativeTraversal<T,TR,ER> by FZipOpsApplicativeTraversal(FList.of(zipOp), fail, pass)

// traverse executing, pairwise, a different collection of different operation on each traversed elements (specific for that element), for all traversed element.
data class FZipOpsApplicativeTraversal<T: Any, R: Any, E: Any, TR: Any, ER: Any> (
    val zipOps: IMOrdered<IMOrdered<(T) -> TDJ<E,R>>>,
    val fail: (E?) -> ER,
    val pass: (R) -> TR
): ApplicativeTraversal<T,TR,ER> {

    private fun accumulate(item: ITApp<T>): Pair<IMList<ER>, IMList<TR>> {
        val aux: FList<Pair<IMList<ER>, IMList<TR>>> =  zipOps.ffold(emptyIMList<Pair<IMList<ER>, IMList<TR>>>()){ listOfPairs, ops ->
            val engine = FMultipleOperationsApplicativeTraversal(ops, fail, pass)
            val partial: Pair<IMList<ER>, IMList<TR>> = engine.grossTraversal(item)
            listOfPairs.fprepend(partial)
        }
        val seed: Pair<IMList<ER>, IMList<TR>> = Pair(emptyIMList(), emptyIMList())
        val res: Pair<IMList<ER>, IMList<TR>> = aux.ffold(seed) { acc: Pair<IMList<ER>, IMList<TR>>, it: Pair<IMList<ER>, IMList<TR>> -> Pair(
            acc.first.tibList<ER>()!!.fprependAll(it.first, acc.first),
            acc.second.tibList<TR>()!!.fprependAll(it.second, acc.second)
        )}
        return res
    }


    override fun refine(allPairs: Pair<IMList<ER>, IMList<TR>>): IMSdj<IMList<ER>, IMList<TR>> {
        val (fail: IMList<ER>, pass: IMList<TR>) = allPairs
        return if (fail.fempty()) TSDJValid(pass.freverse()) else /* errors are already in reverse order */ TSDJInvalid(fail)
    }

//    private fun collect(src: ITApp<T>): IMSdj<IMList<ER>, IMList<TR>> =
//        refine(accumulate(src))
//
//    private fun process(candidates: ITApp<T>): IMSdj<IMList<ER>, IMList<TR>> = collect(candidates)
//
//    override fun traversal(candidates: ITApp<T>): IMSdj<IMList<ER>, IMList<TR>> =
//        if (candidates.fempty()) IMAppOp.flift2App(TSDJValid(emptyIMList<TR>())) as TSDJ<IMList<ER>, IMList<TR>>
//        else {
//            val fmappOut: IMSdj<IMList<ER>, IMList<TR>> = process(candidates)
//            check(1 == fmappOut.fsize())
//            fmappOut
//        }

    override fun grossTraversal(candidates: ITApp<T>): Pair<IMList<ER>, IMList<TR>> =
        if (candidates.fempty()) Pair(emptyIMList(), emptyIMList())
        else accumulate(candidates)

}