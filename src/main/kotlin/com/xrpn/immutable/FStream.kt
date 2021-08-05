package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.immutable.FList.Companion.emptyFList
import com.xrpn.immutable.FStack.Companion.emptyFStack
import com.xrpn.immutable.FStack.Companion.push
//import kotlinx.coroutines.GlobalScope
//import kotlinx.coroutines.async
//import kotlinx.coroutines.delay
// import kotlin.math.max


sealed class FStream<out A: Any> {

    fun isEmpty(): Boolean = this is FSNil

    private tailrec fun <B: Any> unwind(stack: FStack<() -> B>, acc: FStream<B>): FStream<B> = when {
        stack.isEmpty() -> acc
        else -> {
            val (item, shortStack) = stack.pop()
            unwind(shortStack, fsCons(item, { acc }))
        }
    }

    fun <B: Any> foldLeftX(z: () -> B, f: (B, () -> A) -> B): B {

        tailrec fun go(xs: FStream<A>, z: () -> B, f: (B, () -> A) -> B): B =
            when (xs) {
                is FSNil -> z()
                is FSCons -> go(xs.tail(), { f(z(), xs.head) }, f)
            }

        return go(this, z, f)
    }

    fun reverseX(): FStream<A> {

        tailrec fun accrue(xs: FStream<A>, acc: FStack<() -> A>): FStack<() -> A> =
            when (xs) {
                is FSNil -> acc
                is FSCons -> accrue(xs.tail(), push(acc, xs.head))
            }

        return when (this) {
            is FSNil -> this
            is FSCons -> unwind(FStack.reverse(accrue(this, emptyFStack())), emptyFStream())
        }
    }

    fun toFList(): FList<A> = this.foldLeftX({ emptyFList<A>() },{ flist, item -> FLCons(item(), flist)}).reverse()

    fun <B> foldRight(
        z: () -> B,
        f: (A, () -> B) -> B
    ): B =
        when (this) {
            is FSCons -> f(this.head()) { tail().foldRight(z, f) }
            else -> z()
        }

    fun drop(n: Int): FStream<A> {

        tailrec fun dropNext(iter: Int, current: FStream<A>): FStream<A>  =
            when {
                iter > n -> current
                current is FSNil -> current
                else -> dropNext(iter+1, current.tail())
            }

        return dropNext(1, this)
    }

    fun dropWhile(p: (A) -> Boolean): FStream<A> {

        tailrec fun dropNext(current: FStream<A>): FStream<A>  =
            when {
                current is FSNil -> current
                p(current.head()!!) -> dropNext(current.tail())
                else -> current
            }

        return dropNext(this)
    }

    fun take(n: Int): FStream<A> {

        tailrec fun accrue(iter: Int, current: FStream<A>, acc: FStack<() -> A>): FStack<() -> A>  =
            when {
                iter > n -> acc
                current is FSNil -> acc
                else -> accrue(iter+1, current.tail(), push(acc, (current as FSCons).head))
            }

        return unwind(accrue(1, this, emptyFStack()), emptyFStream())
    }

    fun takeWhile_a(p: (A) -> Boolean): FStream<A> {

        tailrec fun accrue(current: FStream<A>, acc: FStack<() -> A>): FStack<() -> A>  =
            when {
                current is FSNil -> acc
                p(current.head()!!) -> accrue(current.tail(), push(acc, (current as FSCons).head))
                else -> acc
            }

        return unwind(accrue(this, emptyFStack()), emptyFStream())
    }

    fun takeWhile_b(p: (A) -> Boolean): FStream<A> =
        unfold(this, {stream -> stream.head()?.let { if (p(it)) Pair(it, stream.tail()) else null } })

    fun findFirst(isMatch: (A) -> Boolean): Triple</* before */ FStream<A>, A?, /* after */ FStream<A>> {

        tailrec fun traverseToMatch(
            match: (A) -> Boolean,
            pos: FStream<A>,
            acc: FStack<() -> A>): Triple<FStream<A>, A?, FStream<A>> =
            when (pos) {
                is FSNil -> Triple(unwind(acc, emptyFStream()), pos.head(), pos.tail())
                is FSCons -> when {
                    match(pos.head()) -> Triple(unwind(acc, emptyFStream()), pos.head(), pos.tail())
                    else -> traverseToMatch(match, pos.tail(), push(acc, pos.head))
                }
            }

        return traverseToMatch(isMatch, this, emptyFStack())
    }

    fun exists(p: (A) -> Boolean): Boolean =
        when (this) {
            is FSNil -> false
            is FSCons -> {
                val (_, item, _) = findFirst(p)
                item != null
            }
        }

    fun existsf(p: (A) -> Boolean): Boolean =
        when (this) {
            is FSCons -> p(this.head()) || this.tail().existsf(p)
            else -> false
        }



//    @ExperimentalStdlibApi
//    fun <B: Any> map (f: (A) -> B): FStream<B> {
//        fun scopedMapper(scope: CoroutineScope) {
//            val mapper = DeepRecursiveFunction<FStream<A>, FStream<B>> { p ->
//                when (p) {
//                    is FSNil -> emptyFStream()
//                    is FSCons -> fsCons<B>({ f(p.head()) }) { scope.run { callRecursive(p.tail()) } }
//                }
//            }
//            mapper(this)
//        }
//        return scopedMapper(GlobalScope).
//    }

    fun <B: Any> map_a(f: (A) -> B): FStream<B> =
        when (this) {
            is FSNil -> emptyFStream()
            is FSCons -> fsCons<B>({ f(this.head()) }, { this.tail().map_a(f) })
        }

    fun <B: Any> map_b(f: (A) -> B): FStream<B> =
        foldRight({ emptyFStream() }, { a, to_b -> fsCons( {f(a)}, { to_b() } ) })

    fun <B: Any> map_c(f: (A) -> B): FStream<B> =
        unfold(this, {stream -> stream.head()?.let { Pair(f(it), stream.tail()) } })

    fun <B: Any> flatMap(f: (A) -> FStream<B>): FStream<B> =
        foldRight({ emptyFStream() },
            { item_a, to_sb ->
              fsCons( { f(item_a).head()!! },
                  { f(item_a).tail().foldRight( to_sb,
                                        { item_b, sb ->
                                          fsCons( { item_b }, sb ) })})})

//    fun filterX(p: (A) -> Boolean): FStream<A> {
//
//        fun go (src:FStream<A>, acc:FStream<A>): FStream<A> {
//            val (longAcc, shortSrc) = src.existing(acc, p, correctOrder = true) ?: return acc
//            return go(shortSrc, longAcc)
//        }
//
//        return go(this, emptyFStream())
//    }

    fun filter(p: (A) -> Boolean): FStream<A> {

        fun go (src:FStream<A>, acc:FStream<A>): FStream<A> {
            val (longAcc, shortSrc) = src.existing(acc, p, correctOrder = true) ?: return acc
            return go(shortSrc, longAcc)
        }

        return go(this, emptyFStream())
    }

    fun filterNot(p: (A) -> Boolean): FStream<A> {

        fun go (src:FStream<A>, acc:FStream<A>): FStream<A> {
            val (longAcc, shortSrc) = src.notExisting(acc, p, correctOrder = true) ?: return acc
            return go(shortSrc, longAcc)
        }

        return go(this, emptyFStream())
    }

    fun iterator(): FStreamIterator<A> = FStreamIterator(this)

    companion object {

        internal fun <A: Any> FStream<A>.existing(acc: FStream<A>, p: (A) -> Boolean, correctOrder: Boolean = false):
            Pair</*augmented*/FStream<A>, /*shortened*/FStream<A>>? =
            when (this) {
                is FSCons -> when (correctOrder) {
                    false -> if (p(this.head())) Pair(acc.prepend(this.head()), this.tail()) else this.tail().existing(acc, p, correctOrder)
                    true -> if (p(this.head())) Pair(acc.append(this.head()), this.tail()) else this.tail().existing(acc, p, correctOrder)
                }
                else -> null /* end of input stream */
            }

        internal fun <A: Any> FStream<A>.notExisting(acc: FStream<A>, p: (A) -> Boolean, correctOrder: Boolean = false):
            Pair</*augmented*/FStream<A>, /*shortened*/FStream<A>>? =
            when (this) {
                is FSCons -> when (correctOrder) {
                    false -> if (!p(this.head())) Pair(acc.prepend(this.head()), this.tail()) else this.tail().notExisting(acc, p, correctOrder)
                    true -> if (!p(this.head())) Pair(acc.append(this.head()), this.tail()) else this.tail().notExisting(acc, p, correctOrder)
                }
                else -> null /* end of input stream */
            }

        fun <A: Any> FStream<A>.prepend(item: A?): FStream<A> = item?.let{ return fsCons( { item }, { this } ) } ?: this

        internal fun <A: Any> FStream<A>.appendX(item: A?): FStream<A> = item?.let{ return fsCons( { item }, { this } ).reverseX() } ?: this

        fun <A: Any> FStream<A>.append(item: A?): FStream<A> = item?.let {
            when (this) {
                is FSNil -> fsCons({ item }, { emptyFStream() })
                is FSCons -> fsCons(this.head, { this.tail().append(item) })
            }
        } ?: this

        fun <A: Any, B: Any, C: Any> FStream<A>.zipWith_a(rhs: FStream<B>, f: (A, B) -> C): FStream<C> =
            when(Pair(this.head() == null, rhs.head() == null)) {
                Pair(false, false) ->  fsCons({ f(this.head()!!, rhs.head()!!) }, { this.tail().zipWith_a(rhs.tail(), f) })
                else -> emptyFStream()
            }

        fun <A: Any> FStream<A>.head(): A? =
            when (this) {
                is FSNil -> null
                is FSCons -> head()
            }

        fun <A: Any> FStream<A>.tail(): FStream<A> =
            when (this) {
                is FSNil -> FSNil
                is FSCons -> tail()
            }

        fun <A: Any> emptyFStream(): FStream<A> = FSNil

        internal fun <A: Any> fsCons(hd: () -> A, tl: () -> FStream<A>): FStream<A> {
            val head: A by lazy(hd)
            val tail: FStream<A> by lazy(tl)
            return FSCons({ head }, { tail })
        }

        fun <A: Any> constant(a: A): FStream<A> = fsCons({ a }, { constant(a) })

        fun <A: Any, S> unfold(z: S, f: (S) -> Pair<A, S>?): FStream<A> = f(z)?.let {
            return fsCons( { it.first  }, { unfold(it.second!!, f) } )
        } ?: emptyFStream()

        fun arithmeticSequence(start: Int, step: Int) = unfold(start){Pair(it, it+step)}

        fun geometricSequence(start: Int, step: Int) = unfold(start){Pair(it, it*step)}

        fun <A: Any> of(vararg xs: A): FStream<A> =
            if (xs.isEmpty()) emptyFStream()
            else fsCons({ xs[0] }, { of(*xs.sliceArray(1 until xs.size)) })

        fun <A: Any> of(xs: Iterator<A>): FStream<A> =
            if (!xs.hasNext()) emptyFStream()
            else fsCons({ xs.next() }, { of(xs) })

        fun <A: Any> of(xs: FListIteratorFwd<A>): FStream<A> =
            xs.nullableNext()?.let{fsCons({ it }, { of( xs )}) } ?: emptyFStream()

        @ExperimentalStdlibApi
        fun <A: Any, B:Any> FStream<A>.map_dr(f: (A) -> B): FStream<B> = DeepRecursiveFunction<FStream<A>, FStream<B>>
        { p: FStream<A> -> when (p) {
            is FSNil -> emptyFStream()
            is FSCons -> {
                val aux = callRecursive(p.tail())
                fsCons<B>({ f(p.head()) },  { aux } )
            }}
        }(this)

        @ExperimentalStdlibApi
        fun <A: Any, S> unfold_dr(z: S, f: (S) -> Pair<A, S>?): FStream<A> = DeepRecursiveFunction<Pair<S, (S) -> Pair<A, S>?>, FStream<A>>
        { p: Pair<S, (S) -> Pair<A, S>?> -> p.second(p.first)?.let {
            val aux = callRecursive(Pair(it.second!!, p.second))
            fsCons({ it.first }, { aux })
        } ?: emptyFStream() }(Pair(z,f))

//        class Tree<A>(val left: Tree<A>? = null, val right: Tree<A>? = null, val a: A? = null) {
//            companion object {
//                @ExperimentalStdlibApi
//                fun <A> depth() = DeepRecursiveFunction<Tree<A>?, Int> { t ->
//                    if (t == null) 0 else max(callRecursive(t.left), callRecursive(t.right)) + 1
//                }
//
//                @ExperimentalStdlibApi
//                fun <A, B: Tree<A>> Tree<A>.depth_b(f: (A) -> B) = DeepRecursiveFunction<Tree<A>?, B?> { t ->
//                    if (t == null) null else f(callRecursive(t.left)?.a!!)
//                }
//            }
//        }

//        suspend fun doSomethingUsefulOne(): Int {
//            delay(1000L) // pretend we are doing something useful here
//            return 13
//        }
//
//        fun somethingUsefulOneAsync() = GlobalScope.async {
//            doSomethingUsefulOne()
//        }
    }
}

object FSNil : FStream<Nothing>()

internal data class FSCons<out A: Any>(
    val head: () -> A,
    val tail: () -> FStream<A>
) : FStream<A>()


