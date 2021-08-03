package com.xrpn.immutable

sealed class FList<out A: Any> {

    /*
        Stack-safe implementation.  May run out of heap memory, will not run
        out (on most reasonable general purpose computers) of stack frames.
     */

    fun tail(): FList<A> = when(this) {
        is FLNil -> FLNil
        is FLCons -> this.tail
    }

    fun head(): A? = when(this) {
        is FLNil -> null
        is FLCons -> this.head
    }

    fun init(): FList<A> {

        tailrec fun trimLast(src: FList<A>, sink: FList<A>): FList<A> =
            when (src) {
                is FLNil -> FLNil  // we start on empty
                is FLCons -> when(src.tail) {
                    is FLNil -> sink
                    is FLCons -> trimLast(src.tail, setLast(sink, src.head))
                }
            }

        return trimLast(this, FLNil)
    }

    fun last(): A? {

        tailrec fun getLast(src: FList<A>, sink: FList<A>): A? =
                when (src) {
                    is FLNil -> null  // we start on empty
                    is FLCons -> when(src.tail) {
                        is FLNil -> src.head
                        is FLCons -> getLast(src.tail, setLast(sink, src.head))
                    }
                }

        return getLast(this, FLNil)
    }

    fun len(): Int = if (this is FLNil) 0 else this.foldLeft(0) { b, _ -> b + 1 }

    fun size(): Int = this.len()

    fun isEmpty(): Boolean = this is FLNil

    fun drop(n: Int): FList<A> {

        tailrec fun dropNext(iter: Int, current: FList<A>): FList<A>  =
            when {
                iter > n -> current
                current is FLNil -> current
                else -> dropNext(iter+1, current.tail())
            }

        return dropNext(1, this)
    }

    fun findFirst(isMatch: (A) -> Boolean): Triple< /* before */ FList<A>, A?, /* after */ FList<A>> {

        tailrec fun traverseToMatch(
            match: (A) -> Boolean,
            pos: FList<A>,
            acc: FList<A>): Triple<FList<A>, A?, FList<A>> =
            when (pos) {
                is FLNil -> /* not found */ Triple(this, null, FLNil)
                is FLCons -> when {
                    match(pos.head) -> Triple(acc.reverse(), pos.head, pos.tail)
                    else -> traverseToMatch(match, pos.tail, FLCons(pos.head, acc))
                }
            }

        return traverseToMatch(isMatch, this, FLNil)

    }

    fun dropFirst(isMatch: (A) -> Boolean): FList<A> {
        val (before, _, after) = findFirst(isMatch)
        return append(before, after)
    }

    fun dropWhile(isMatch: (A) -> Boolean): FList<A> {

        tailrec fun dropWhileIsMatch(match:(A) -> Boolean, current: FList<A>): FList<A>  =
                when (current) {
                    is FLNil -> current
                    is FLCons -> when {
                        ! match(current.head) -> current
                        else -> dropWhileIsMatch(match, current.tail)
                    }
                }

        return dropWhileIsMatch(isMatch, this)
    }

    fun take(n: Int): FList<A> {

        tailrec fun takeNext(iter: Int, current: FList<A>, acc: FList<A>): FList<A>  =
            when {
                iter > n -> acc
                current is FLNil -> acc
                else -> takeNext(iter+1, current.tail(), FLCons(current.head()!!, acc))
            }

        return takeNext(1, this, FLNil).reverse()
    }

    fun takeWhile(isMatch: (A) -> Boolean): FList<A> {

        tailrec fun takeIfIsMatch(match:(A) -> Boolean, current: FList<A>, acc: FList<A>): FList<A>  =
            when (current) {
                is FLNil -> acc
                is FLCons -> when {
                    ! match(current.head) -> acc
                    else -> takeIfIsMatch(match, current.tail, FLCons(current.head, acc))
                }
            }

        return takeIfIsMatch(isMatch, this, FLNil).reverse()
    }

    fun <B> foldRight(z: B, f: (A, B) -> B): B {

        // NOT tail recursive
        // fun go(xs: FList<A>, z: B, f: (A, B) -> B): B =
        //     when (xs) {
        //         is FLNil -> z
        //         is FLCons -> f(xs.head, go(xs.tail, z, f))
        //     }

        val g: (B, A) -> B = { b, a -> f(a, b)}
        val reversed = this.reverse()
        return reversed.foldLeft(z, g)
    }


    fun <B> foldLeft(z: B, f: (B, A) -> B): B {

        tailrec fun go(xs: FList<A>, z: B, f: (B, A) -> B): B =
            when (xs) {
                is FLNil -> z
                is FLCons -> go(xs.tail, f(z, xs.head), f)
            }

        return go(this, z, f)
    }

    fun copy(): FList<A> = this.foldRight(emptyFList()) { a, b -> FLCons(a, b) }

    fun reverse(): FList<A> = this.foldLeft(emptyFList()) { b, a -> FLCons(a, b) }

    fun <B: Any> map(f: (A) -> B): FList<B> {

        tailrec fun go(xs: FList<A>, out: FList<B>): FList<B> =
            when(xs) {
                is FLNil -> out
                is FLCons -> go(xs.tail, FLCons<B>(f(xs.head), out))
            }

        return go(this, emptyFList<B>()).reverse()
    }

    fun <B: Any> flatMap(f: (A) -> FList<B>): FList<B> {

        tailrec fun go(xs: FList<A>, out: FList<B>): FList<B> =
            when (xs) {
                is FLNil -> out
                is FLCons -> go(xs.tail, f(xs.head).foldLeft(out,{list, element -> FLCons(element, list)}))
            }

        return go(this, emptyFList<B>()).reverse()
    }

    fun filter(f: (A) -> Boolean): FList<A> {

        fun go(xs: FList<A>, acc: FList<A>): FList<A> =
            when(xs) {
                is FLNil -> acc
                is FLCons -> go(xs.tail, if (f(xs.head)) FLCons(xs.head,acc) else acc)
            }

        return go(this, FLNil).reverse()
    }

    fun filterNot(f: (A) -> Boolean): FList<A> {

        fun go(xs: FList<A>, acc: FList<A>): FList<A> =
            when(xs) {
                is FLNil -> acc
                is FLCons -> go(xs.tail, if (f(xs.head)) acc else FLCons(xs.head,acc))
            }

        return go(this, FLNil).reverse()
    }

    fun <B: Any, C: Any> zipWith(xs: FList<B>, f: (A, B) -> C): FList<C> {

        tailrec fun go(xsa: FList<A>, xsb: FList<B>, acc: FList<C>):FList<C> =
            if ((xsa is FLNil) || (xsb is FLNil)) acc
            else go((xsa as FLCons).tail,
                    (xsb as FLCons).tail,
                    FLCons(f(xsa.head, xsb.head), acc))

        return go(this, xs, FLNil).reverse()
    }

    fun <B> zipWith(xs: Iterator<B>): FList<Pair<A,B>> {

        tailrec fun go(xsa: FList<A>, xsi: Iterator<B>, acc: FList<Pair<A,B>>):FList<Pair<A,B>> =
            if ((xsa is FLNil) || !xsi.hasNext()) acc
            else go((xsa as FLCons).tail, xsi,
                    FLCons(Pair(xsa.head, xsi.next()), acc))

        return go(this, xs, FLNil).reverse()
    }

    fun enumerate(start: Int = 0): FList<Pair<A, Int>> {
        return zipWith(IntRange(start, Int.MAX_VALUE).iterator())
    }

    override fun equals(other: Any?): Boolean = when (this) {
        is FLNil -> other is FLNil
        is FLCons -> when(other) {
            is FLCons<*> -> other == this
            else -> false
        }
    }

    override fun hashCode(): Int = when(this) {
        is FLNil -> FLNil.hashCode()
        is FLCons -> FLCons.hashCode(this)
    }

    fun iterator(): FListIterator<A> = FListIterator(this)

    companion object {

        fun <A: Any> emptyFList(): FList<A> = FLNil

        fun <A: Any> of(vararg items: A): FList<A> {
            if (items.isEmpty()) return FLNil
            var acc : FList<A> = FLNil
            items.reverse()
            items.forEach {
                acc = FLCons(it, acc)
            }
            return acc
        }

        fun <A: Any> of(items: Iterator<A>): FList<A> {
            if (! items.hasNext()) return FLNil
            var acc : FList<A> = FLNil
            items.forEach {
                acc = FLCons(it, acc)
            }
            return acc.reverse()
        }

        fun <A: Any> setHead(x: A, xs: FList<A>): FList<A> = FLCons(x, xs)

        // NOT tail recursive
        // fun <A> setLast(lead: FList<A>, after: A): FList<A> =
        //     when (lead) {
        //         is FLNil -> FLCons(after, FLNil)
        //         is FLCons -> FLCons(lead.head, setLast(lead.tail, after))
        //     }

        // XXXQQQXXX
        // inline fun <reified A> setLast(lead: FList<A>, after: A): FList<A> =
        //    append(lead,FList.of(*arrayOf(after)))

        fun <A: Any> setLast(lead: FList<A>, after: A): FList<A> =
           append(lead,FLCons(after, FLNil))

        // NOT tail recursive
        // fun <A> append(lead: FList<A>, after: FList<A>): FList<A> =
        //     when (lead) {
        //         is FLNil -> after
        //         is FLCons -> FLCons(lead.head, append(lead.tail, after))
        //     }

        fun <A: Any> append(lead: FList<A>, after: FList<A>): FList<A> =
            lead.foldRight(after,{element, list -> FLCons(element, list)})

        fun <A: Any> appendNested(rhs: FList<FList<A>>): FList<A> {

            tailrec fun appendLists(src: FList<FList<A>>, acc: FList<A>): FList<A> =
                when(src) {
                    is FLNil -> acc
                    is FLCons -> appendLists(src.tail(), append(acc, src.head))
                }

            return appendLists(rhs, FLNil)
        }

        fun <A: Any> equal2(lhs: FList<A>, rhs: FList<A>): Boolean {

            tailrec fun go(xsa: FList<A>, xsb: FList<A>, acc: FList<Pair<A,A>>):FList<Pair<A,A>> =
                if ((xsa is FLNil) || (xsb is FLNil)) acc
                else go((xsa as FLCons).tail,
                    (xsb as FLCons).tail,
                    FLCons(Pair(xsa.head, xsb.head), acc))

            val ll = lhs.len()
            val rl = rhs.len()
            return when {
                ll != rl -> false
                0 == ll -> true // i.e. they are both empty
                else -> go(lhs, rhs, FLNil).foldLeft(true) { predicate, pair -> predicate && (pair.first == pair.second) }
            }
        }

        fun <A: Any> FList<A>.equal(rhs: FList<A>): Boolean = equal2(this, rhs)

//        fun <A: Any, B: A> reduceLeft(xsa: FList<A>, f: (B, A) -> B): B? {
//
//            @SuppressWarnings("unchecked")
//            fun uncheckedCast(a: A): B = a as B
//
//            return when (xsa) {
//                is FLNil -> null
//                is FLCons -> when (xsa.tail) {
//                    is FLNil -> null
//                    is FLCons -> xsa.tail.tail.foldLeft(
//                        f((::uncheckedCast)(xsa.tail.head), xsa.head),
//                        f)
//                }
//            }
//        }

        fun <A: Any> hasSubsequence(xsa: FList<A>, sub: FList<A>): Boolean {

            tailrec fun go(xsa: FList<A>, sub: FList<A>): Boolean =
                when(sub) {
                    is FLNil -> true
                    is FLCons -> when(xsa) {
                        is FLNil -> false
                        is FLCons -> when {
                            xsa.head != sub.head -> go(xsa.tail, sub)
                            else -> go(xsa.tail, sub.tail)
                        }
                    }
                }

            if (sub is FLNil) return true
            if (xsa is FLNil) return false
            return go(xsa, sub)
        }

        internal inline fun <reified A: Any> toArray(fl: FList<A>): Array<A> =
            FListIterator.toArray(fl.size(), FListIterator(fl))
    }
}

object FLNil: FList<Nothing>() {
    override fun toString(): String = "FLNil"
}

data class FLCons<out A: Any>(
        val head: A,
        val tail: FList<A>
) : FList<A>() {

    // the data class built-in equals is not stack safe for recursive data structures
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FLCons<*> -> when {
            this.isEmpty() && other.isEmpty() -> true
            this.isEmpty() || other.isEmpty() -> false
            this.head()!!::class == other.head()!!::class -> equal2(this, other)
            else -> false
        }
        else -> false
    }

    // the data class built-in toString is not stack safe
    override fun toString(): String = (foldLeft("") { str, h -> "$str($h, #" }) + FLNil.toString() + (")".repeat(size()))

    // the data class built-in hashCode is not stack safe
    override fun hashCode(): Int {
        val aux: Long = foldLeft(0L) { code, h -> (h.hashCode() + code * 3L) }
        return if (Int.MIN_VALUE.toLong() < aux && aux < Int.MAX_VALUE.toLong()) aux.toInt()
        else /* may it even theoretically get here? */ TODO("must reduce range of FList.hashcode to Int")
    }

    companion object {
        fun <A: Any> hashCode(cons: FLCons<A>) = cons.hashCode()
    }

}