package com.xrpn.immutable
import com.xrpn.bridge.FListIteratorBidi
import com.xrpn.bridge.FListIteratorFwd
import kotlin.collections.List
import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMListCompanion

sealed class FList<out A: Any>: List<A>, IMList<A> {

    override fun equals(other: Any?): Boolean = when (this) {
        is FLNil -> when (other) {
            is FLNil -> true
            is FLCons<*> -> false
            is List<*> -> other.isEmpty()
            is IMList<*> -> other.fempty()
            else -> false
        }
        is FLCons -> when (other) {
            is FLNil -> false
            is FLCons<*> -> other == this
            is List<*> -> other == this
            is IMList<*> -> other == this
            else -> false
        }
    }

    override fun hashCode(): Int = when (this) {
        is FLNil -> FLNil.hashCode()
        is FLCons -> FLCons.hashCode(this)
    }

    // from Collection<A>

    override val size: Int by lazy { if (this is FLNil) 0 else this.ffoldLeft(0) { b, _ -> b + 1 } }

    override fun isEmpty(): Boolean = this is FLNil

    override fun contains(element: @UnsafeVariance A): Boolean = !ffilter { it == element }.isEmpty()

    override fun iterator(): Iterator<A> = FListIteratorFwd(this)

    // from List <A>

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    override fun get(index: Int): A = atWantedIxPosition(index, this.size,this, 0) ?: throw IndexOutOfBoundsException("index $index")

    override fun indexOf(element: @UnsafeVariance A): Int {
        val res: Triple<FList<A>, A?, FList<A>> = ffindFirst { it == element }
        return res.second?.let { res.first.size } ?: NOT_FOUND
    }

    override fun lastIndexOf(element: @UnsafeVariance A): Int =
        when (val rix = this.reverse().indexOf(element)) {
            NOT_FOUND -> rix
            else -> size - rix - 1
        }

    override fun listIterator(): ListIterator<A> = FListIteratorBidi(this)

    override fun listIterator(index: Int): ListIterator<A> = when {
        index < 0 || size < index -> throw IndexOutOfBoundsException()
        else -> FListIteratorBidi(this, index)
    }

    override fun subList(fromIndex: Int, toIndex: Int): /* [fromIndex, toIndex) */ List<A> = fslice(fromIndex, toIndex)

    /*
        Stack-safe implementation.  May run out of heap memory, will not run
        out (on most reasonable general purpose computers) of stack frames.
     */

    // filtering

    override fun fdistinct(): FList<A> {
        TODO("Not yet implemented")
    }

    override fun fdrop(n: Int): FList<A> {

        tailrec fun dropNext(iter: Int, current: FList<A>): FList<A>  = when {
            iter > n -> current
            current is FLNil -> current
            else -> dropNext(iter+1, current.ftail())
        }

        return when {
            n < 0 || size <= n -> FLNil
            else -> dropNext(1, this)
        }
    }

    override fun fdropFirst(isMatch: (A) -> Boolean): FList<A> {
        val (before, _, after) = ffindFirst(isMatch)
        return append(before, after)
    }

    override fun fdropRight(n: Int): FList<A> = when {
        n < 0 || size <= n -> FLNil
        else -> fsplitAt(size - n).first
    }

    override fun fdropWhile(isMatch: (A) -> Boolean): FList<A> {

        tailrec fun dropWhileIsMatch(match:(A) -> Boolean, current: FList<A>): FList<A>  = when (current) {
            is FLNil -> current
            is FLCons -> when {
                ! match(current.head) -> current
                else -> dropWhileIsMatch(match, current.tail)
            }
        }

        return dropWhileIsMatch(isMatch, this)
    }


    override fun ffilter(isMatch: (A) -> Boolean): FList<A> {

        tailrec fun go(xs: FList<A>, acc: FList<A>): FList<A> = when(xs) {
            is FLNil -> acc
            is FLCons -> go(xs.tail, if (isMatch(xs.head)) FLCons(xs.head,acc) else acc)
        }

        return go(this, FLNil).reverse()
    }

    override fun ffilterNot(isMatch: (A) -> Boolean): FList<A> {

        tailrec fun go(xs: FList<A>, acc: FList<A>): FList<A> = when(xs) {
            is FLNil -> acc
            is FLCons -> go(xs.tail, if (isMatch(xs.head)) acc else FLCons(xs.head,acc))
        }

        return go(this, FLNil).reverse()
    }

    override fun ffindFromLeft(isMatch: (A) -> Boolean): A? {

        tailrec fun go(xs: FList<A>): A? = when (xs) {
            is FLNil -> null
            is FLCons -> when {
                isMatch(xs.fhead()!!) -> xs.fhead()
                else -> go(xs.tail)
            }
        }

        return go(this)
    }

    override fun ffindFromRight(isMatch: (A) -> Boolean): A? {
        return this.reverse().ffindFromLeft(isMatch)
    }

    override fun fgetOrNull(ix: Int): A? = atWantedIxPosition(ix, this.size,this, 0)

    override fun fhead(): A? = when (this) {
        is FLNil -> null
        is FLCons -> this.head
    }

    override fun finit(): FList<A> = ftake(this.size-1)

    override fun flast(): A? = atWantedIxPosition(this.size - 1, this.size, this, 0)

    override fun fslice(fromIndex: Int, toIndex: Int): /* [fromIndex, toIndex) */ FList<A> =
        when (val postfix = fdrop(fromIndex)) {
            is FLNil -> FLNil
            else -> postfix.ftake(toIndex - fromIndex)
        }

    override fun fslice(atIxs: IMList<Int>): FList<A> {

        tailrec fun go(ixs: IMList<Int>, acc: FList<A>): FList<A> = when (val currentIx = ixs.fhead()) {
            null -> acc
            else -> {
                val newAcc = if (currentIx < 0 || size <= currentIx) acc
                    else FLCons(get(currentIx), acc)
                go(ixs.ftail(), newAcc)
            }
        }

        return go(atIxs, FLNil).reverse()
    }

    override fun ftail(): FList<A> = when (this) {
        is FLNil -> FLNil
        is FLCons -> this.tail
    }

    override fun ftake(n: Int): FList<A> {

        tailrec fun takeNext(iter: Int, current: FList<A>, acc: FList<A>): FList<A> = when {
            iter > n -> acc
            current is FLNil -> acc
            else -> takeNext(iter+1, current.ftail(), FLCons(current.fhead()!!, acc))
        }

        return when {
            n < 0 -> FLNil
            size <= n -> this
            else -> takeNext(1, this, FLNil).reverse()
        }
    }

    override fun ftakeRight(n: Int): FList<A> = when {
        n < 0 -> FLNil
        size <= n -> this
        else -> fdrop(size - n)
    }

    override fun ftakeWhile(isMatch: (A) -> Boolean): FList<A> {

        tailrec fun takeIfIsMatch(match:(A) -> Boolean, current: FList<A>, acc: FList<A>): FList<A> = when (current) {
            is FLNil -> acc
            is FLCons -> when {
                ! match(current.head) -> acc
                else -> takeIfIsMatch(match, current.tail, FLCons(current.head, acc))
            }
        }

        return takeIfIsMatch(isMatch, this, FLNil).reverse()
    }

    // grouping

    override fun fcount(isMatch: (A) -> Boolean): Int {

        tailrec fun go(xs: FList<A>, counter: Int): Int = when(xs) {
            is FLNil -> counter
            is FLCons -> go(xs.tail, if (isMatch(xs.head)) counter + 1 else counter)
        }

        return go(this, 0)
    }

    override fun fenumerate(offset: Int): FList<Pair<A, Int>> = fzipWith(IntRange(offset, Int.MAX_VALUE).iterator())

    override fun ffindFirst(isMatch: (A) -> Boolean): Triple< /* before */ FList<A>, A?, /* after */ FList<A>> {

        tailrec fun traverseToMatch(
            match: (A) -> Boolean,
            pos: FList<A>,
            acc: FList<A>): Triple<FList<A>, A?, FList<A>> = when (pos) {
                is FLNil -> /* not found */ Triple(this, null, FLNil)
                is FLCons -> when {
                    match(pos.head) -> Triple(acc.reverse(), pos.head, pos.tail)
                    else -> traverseToMatch(match, pos.tail, FLCons(pos.head, acc))
                }
            }

        return traverseToMatch(isMatch, this, FLNil)

    }

    override fun <B> fgroupBy(f: (A) -> B): Map<B, FList<A>> {

        /*
        tailrec fun go(xs: FList<A>, acc: MutableMap<B, FList<A>>): MutableMap<B, FList<A>> =
            when(xs) {
                is FLNil -> acc
                is FLCons -> {
                    val element: B = f(xs.head)
                    val innerAcc = acc.getOrDefault(element, emptyFList())
                    acc[element] = FLCons(xs.head()!!, innerAcc)
                    go(xs.tail, acc)
                }
            }

        return go(this.reverse(), emptyMap<B, FList<A>>() as MutableMap<B, FList<A>>)
        */

        fun f4fl(acc: MutableMap<B, FList<A>>, element: A): MutableMap<B, FList<A>> {
            val key = f(element)
            acc[key] = FLCons(element, acc.getOrDefault(key, emptyFList()))
            return acc
        }

        // return ffoldLeft(emptyMap<B, FList<A>>() as MutableMap<B, FList<A>>, ::f4fl)
        TODO("need FMap done to make this happen")
    }

    override fun fpartition(isMatch: (A) -> Boolean): Pair<FList<A>, FList<A>> {

        fun f4fl(acc: Pair<FList<A>, FList<A>>, current: A): Pair<FList<A>, FList<A>> =
            if (isMatch(current)) Pair(FLCons(current, acc.first), acc.second)
            else Pair(acc.first, FLCons(current, acc.second))

        val p = ffoldLeft(Pair(emptyFList(), emptyFList()), ::f4fl)
        return Pair(p.first.reverse(), p.second.reverse())

    }

    override fun fslidingWindow(size: Int, step: Int): FList<FList<A>> {

        tailrec fun go(l: FList<A>, acc: FList<FList<A>>): FList<FList<A>> = when (l) {
            is FLNil -> acc
            is FLCons<A> -> {
                val newAcc = FLCons(l.fslice(0, size), acc)
                go(l.fdrop(step), newAcc)
            }
        }

        return when {
            size < 1 -> FLNil
            step < 1 -> FLNil
            else -> go(this, FLNil).reverse()
        }
    }

    override fun fslidingFullWindow(size: Int, step: Int): FList<FList<A>> {

        tailrec fun go(l: FList<A>, acc: FList<FList<A>>): FList<FList<A>> = when (l) {
            is FLNil -> acc
            is FLCons<A> -> {
                val newSlice = l.fslice(0, size)
                if (newSlice.size != size) acc else {
                    val newAcc = FLCons(newSlice, acc)
                    go(l.fdrop(step), newAcc)
                }
            }
        }

        return when {
            size < 1 -> FLNil
            step < 1 -> FLNil
            else -> go(this, FLNil).reverse()
        }
    }

    override fun fsplitAt(index: Int): Triple< /* before */ FList<A>, A?, /* after */ FList<A>> {

        tailrec fun traverseToIndex(pos: FList<A>, acc: FList<A>, ix: Int): Triple<FList<A>, A?, FList<A>> = when {
            index < 0 || size <= index -> Triple(this, null, FLNil)
            ix == index -> Triple(acc.reverse(), pos.fhead(), pos.ftail())
            else -> traverseToIndex(pos.ftail(), FLCons(pos.fhead()!!, acc), ix+1)
        }

        return traverseToIndex(this, FLNil, 0)
    }

    override fun <B: Any, C: Any> funzip(f: (A) -> Pair<B,C>): Pair<FList<B>, FList<C>> {

        fun f4fl(acc: Pair<FList<B>, FList<C>>, current: A): Pair<FList<B>, FList<C>> {
            val pair = f(current)
            return Pair(FLCons(pair.first, acc.first), FLCons(pair.second, acc.second))
        }

        val p = ffoldLeft(Pair(emptyFList(), emptyFList()), ::f4fl)
        return Pair(p.first.reverse(), p.second.reverse())
    }

    override fun <B: Any, C: Any> fzipWith(xs: IMList<B>, f: (A, B) -> C): FList<C> {

        tailrec fun go(xsa: FList<A>, xsb: IMList<B>, acc: FList<C>):FList<C> =
            if ((xsa is FLNil) || xsb.fempty()) acc
            else go((xsa as FLCons).tail,
                    xsb.ftail(),
                    FLCons(f(xsa.head, xsb.fhead()!!), acc))

        return go(this, xs, FLNil).reverse()
    }

    override fun <B: Any> fzipWith(xs: Iterator<B>): FList<Pair<A,B>> {

        tailrec fun go(xsa: FList<A>, xsi: Iterator<B>, acc: FList<Pair<A,B>>):FList<Pair<A,B>> =
            if ((xsa is FLNil) || !xsi.hasNext()) acc
            else go((xsa as FLCons).tail, xsi,
                    FLCons(Pair(xsa.head, xsi.next()), acc))

        return go(this, xs, FLNil).reverse()
    }

    override fun fzipWithIndex(): FList<Pair<A, Int>> = fenumerate()

    override fun fzipWithIndex(startIndex: Int): FList<Pair<A, Int>> = fdrop(startIndex).fenumerate()

    // transforming

    override fun <B: Any> fflatMap(f: (A) -> IMList<B>): FList<B> {

        tailrec fun go(xs: FList<A>, out: FList<B>): FList<B> =
            when (xs) {
                is FLNil -> out
                is FLCons -> go(xs.tail, f(xs.head).ffoldLeft(out) { list, element -> FLCons(element, list) })
            }

        return go(this, emptyFList<B>()).reverse()
    }

    override fun <B> ffoldLeft(z: B, f: (acc: B, A) -> B): B {

        tailrec fun go(xs: FList<A>, z: B, f: (B, A) -> B): B =
            when (xs) {
                is FLNil -> z
                is FLCons -> go(xs.tail, f(z, xs.head), f)
            }

        return go(this, z, f)
    }

    override fun <B> ffoldRight(z: B, f: (A, acc: B) -> B): B {

        val g: (B, A) -> B = { b, a -> f(a, b)}
        val reversed = this.reverse()
        return reversed.ffoldLeft(z, g)
    }

    override fun <B: Any> fmap(f: (A) -> B): FList<B> {

        tailrec fun go(xs: FList<A>, out: FList<B>): FList<B> =
            when(xs) {
                is FLNil -> out
                is FLCons -> go(xs.tail, FLCons<B>(f(xs.head), out))
            }

        return go(this, emptyFList<B>()).reverse()
    }

    override fun freduceLeft(f: (acc: A, A) -> @UnsafeVariance A): A? = freduceLeft(this, f)

    override fun freduceRight(f: (A, acc: A) -> @UnsafeVariance A): A? {
        val xsar = this.reverse()
        fun g(acc:A, a:A) = f(a, acc)
        return freduceLeft(xsar, ::g)
    }

    // =====

    fun copy(): FList<A> = this.ffoldRight(emptyFList(), { a, b -> FLCons(a,b)})

    fun copyToList(): List<A> = this.ffoldRight<MutableList<A>>(mutableListOf()) { a, b ->
        b.add(a)
        b
    }.reversed()

    fun reverse(): FList<A> = this.ffoldLeft(emptyFList(), { b, a -> FLCons(a,b)})

    companion object: IMListCompanion {

        val NOT_FOUND: Int = -1

        override fun <A: Any> emptyFList(): FList<A> = FLNil

        override fun <A: Any> of(vararg items: A): FList<A> {
            if (items.isEmpty()) return FLNil
            var acc : FList<A> = FLNil
            items.reverse()
            items.forEach {
                acc = FLCons(it, acc)
            }
            return acc
        }

        override fun <A: Any> of(items: Iterator<A>): FList<A> {
            if (! items.hasNext()) return FLNil
            var acc : FList<A> = FLNil
            items.forEach {
                acc = FLCons(it, acc)
            }
            return acc.reverse()
        }

        override fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): FList<A> {
            if (! items.hasNext()) return FLNil
            var acc : FList<A> = FLNil
            items.forEach {
                acc = FLCons(f(it), acc)
            }
            return acc.reverse()
        }

        override fun <A: Any> of(items: List<A>): FList<A> {
            if (items.isEmpty()) return FLNil
            var acc : FList<A> = FLNil
            val li = items.listIterator(items.size)
            while (li.hasPrevious()){
                acc = FLCons(li.previous(), acc)
            }
            return acc
        }

        override fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): FList<A> {
            if (items.isEmpty()) return FLNil
            var acc : FList<A> = FLNil
            val li = items.listIterator(items.size)
            while (li.hasPrevious()){
                acc = FLCons(f(li.previous()), acc)
            }
            return acc
        }

        override fun <A: Any> append(lead: IMList<A>, after: IMList<A>): FList<A> =
            // TODO this works as long as FList is the only implementation of IMList
            (lead.ffoldRight(after) { element, list -> FLCons(element, list as FList<A>) }) as FList<A>

        override fun <A: Any> appendNested(rhs: IMList<IMList<A>>): FList<A> {
            // TODO this works as long as FList is the only implementation of IMList
            @Suppress("UNCHECKED_CAST")
            return appendLists(rhs as FList<FList<A>>, FLNil)
        }

        override fun <A: Any> hasSubsequence(xsa: IMList<A>, sub: IMList<A>): Boolean {

            tailrec fun go(xsa: IMList<A>, sub: IMList<A>, partialMatch: Boolean): Boolean =
                when(sub.fempty()) {
                    true -> true
                    false -> when(xsa.fempty()) {
                        true -> false
                        false -> when {
                            partialMatch && xsa.fhead() != sub.fhead() -> false
                            xsa.fhead() != sub.fhead() -> go(xsa.ftail(), sub, false)
                            else -> go(xsa.ftail(), sub.ftail(), true)
                        }
                    }
                }

            if (sub is FLNil) return true
            if (xsa is FLNil) return false
            return go(xsa, sub, false)
        }

        override fun <A: Any> setHead(x: A, xs: IMList<A>): FList<A> =
            if (xs.fempty()) FLCons(x, FLNil)
            else FLCons(x, FLCons(xs.fhead()!!, xs.ftail() as FList<A>))

        override fun <A: Any> setLast(lead: IMList<A>, after: A): FList<A> =
            append(lead as FList<A>,FLCons(after, FLNil))

        override fun <A: Any> IMList<A>.equal(rhs: IMList<A>): Boolean = equal2(this as FList<A>, rhs as FList<A>)

        override fun <A: Any> Collection<A>.toIMList(): IMList<A> = this.toFList()

        tailrec fun <A:Any> appendLists(src: FList<FList<A>>, acc: FList<A>): FList<A> =
            when(src) {
                is FLNil -> acc
                is FLCons -> appendLists(src.ftail(), append(acc, src.head))
            }

        fun <A: Any> equal2(lhs: FList<A>, rhs: FList<A>): Boolean {

            tailrec fun go(xsa: FList<A>, xsb: FList<A>, acc: FList<Pair<A,A>>):FList<Pair<A,A>> =
                if ((xsa is FLNil) || (xsb is FLNil)) acc
                else go((xsa as FLCons).tail,
                    (xsb as FLCons).tail,
                    FLCons(Pair(xsa.head, xsb.head), acc))

            return if (lhs.size != rhs.size) false
            else if (0 == lhs.size) true // i.e. they are both empty
            else go(lhs, rhs, FLNil).ffoldLeft(true,{ predicate, pair -> predicate && (pair.first == pair.second)})
        }

//        TODO maybe...
//        inline fun <reified A: Any, reified B: Any > flatten(rhs: FList<B>): FList<A> =
//            @Suppress("UNCHECKED_CAST")
//            if (isNested(rhs) && !rhs.isEmpty()) when (val hd = firstNotEmpty(rhs as FList<*>)) {
//                is FLCons<*> -> if (hd.head is A) appendLists(rhs as FList<FList<A>>, FLNil) else FLNil
//                else -> FLNil
//            } else if (!rhs.isEmpty() && rhs.fhead()!! is A) rhs as FList<A>
//            else FLNil

        inline fun <reified A: Any, reified B: FList<A>> isNested(l: B): Boolean = when (l) {
            is FLCons<*> -> (l.head is FLNil) || (l.head is B)
            else -> false
        }

        inline fun <reified A: Any, reified B: FList<A>> firstNotEmpty(l: FList<A>): FList<A>? {
            val iter = l.iterator() as FListIteratorFwd<A>
            for ( el in iter) when (el) {
                is FLCons<*> -> if (el is B && !el.fempty()) return el
                else -> continue
            }
            return null
        }

        inline fun <reified A: Any, reified B: FList<A>> fflatten(rhs: FList<B>): FList<A> =
            if (isNested(rhs) && !rhs.isEmpty()) appendLists(rhs, FLNil)
            else if (!rhs.isEmpty() && rhs.fhead()!! is A) rhs as B
            else FLNil

        internal tailrec fun <A: Any> freduceLeft(xsa: FList<A>, f: (acc: A, A) -> A): A? = when (xsa) {
            is FLNil -> null
            is FLCons -> when (xsa.tail) {
                is FLNil -> xsa.fhead()
                is FLCons -> {
                    val prevAcc: A = xsa.head
                    val newAcc = f(prevAcc, xsa.tail.head)
                    val nextList = FLCons(newAcc, xsa.tail.tail)
                    freduceLeft(nextList, f)
                }
            }
        }

        internal inline fun <reified A: Any> toArray(fl: FList<A>): Array<A> =
            FListIteratorFwd.toArray(fl.size, FListIteratorFwd(fl))

        fun <A: Any> Collection<A>.toFList(): FList<A> = when(this) {
            is FList -> this
            is List -> of(this)
            else -> of(this.iterator())
        }

        private tailrec fun <A: Any> atWantedIxPosition(wantedIx: Int, stop: Int, l: FList<A>, ix: Int): A? = when {
            wantedIx < 0 || stop <= wantedIx -> null
            ix == wantedIx -> l.fhead()
            else -> atWantedIxPosition(wantedIx, stop, l.ftail(), ix + 1)
        }

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
            this.isEmpty() && other.isEmpty() -> true // type erasure boo-boo
            this.isEmpty() || other.isEmpty() -> false
            this.fhead()!!::class == other.fhead()!!::class -> equal2(this, other)
            else -> false
        }
        other is List<*> -> when {
            // necessary if FList is-a List to maintain reflexive, symmetric, transitive equality
            this.isEmpty() && other.isEmpty() -> true // type erasure boo-boo
            this.isEmpty() || other.isEmpty() -> false
            this.fhead()!!::class == other.first()!!::class -> other == this
            else -> false
        }
        other is IMList<*> -> when {
            // foot in the door in case of additional implementations for IMList
            this.isEmpty() && other.fempty() -> true // type erasure boo-boo
            this.isEmpty() || other.fempty() -> false
            this.fhead()!!::class == other.fhead()!!::class -> other.equal(this)
            else -> false
        }
        else -> false
    }

    // the data class built-in toString is not stack safe
    override fun toString(): String = (ffoldLeft("") { str, h -> "$str($h, #" }) + FLNil.toString()+")".repeat(size)

    // the data class built-in hashCode is not stack safe
    override fun hashCode(): Int {
        val aux: Long = ffoldLeft(0L) { code, h -> (h.hashCode() + code * 3L) }
        return if (Int.MIN_VALUE.toLong() < aux && aux < Int.MAX_VALUE.toLong()) aux.toInt()
               else (aux % Int.MAX_VALUE.toLong()).toInt()
    }

    companion object {
        fun <A: Any> hashCode(cons: FLCons<A>) = cons.hashCode()
    }
}