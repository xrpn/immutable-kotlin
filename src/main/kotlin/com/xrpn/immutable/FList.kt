package com.xrpn.immutable
import com.xrpn.bridge.FListIteratorBidi
import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.hash.DigestHash.lChecksumHashCode
import com.xrpn.hash.DigestHash.uIntChecksumHashCode
import com.xrpn.hash.MurMur3at32
import com.xrpn.hash.to8ByteArray
import com.xrpn.imapi.*
import com.xrpn.imapi.IMListEqual2
import java.util.logging.Logger
import java.util.zip.CRC32C
import kotlin.reflect.KClass

sealed class FList<out A: Any>: List<A>, IMList<A> {

    // from Collection<A>

    override val size: Int by lazy { if (this is FLNil) 0 else this.ffoldLeft(0) { b, _ -> b + 1 } }

    override fun isEmpty(): Boolean = this is FLNil

    override operator fun contains(element: @UnsafeVariance A): Boolean = !ffilter { it == element }.isEmpty()

    override fun iterator(): Iterator<A> = FListIteratorFwd(this)

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!contains(it)) return false }
        return true
    }

    // from List <A>

    override fun get(index: Int): A = atWantedIxPosition(index, this.size,this, 0) ?: throw IndexOutOfBoundsException("index $index")

    override fun indexOf(element: @UnsafeVariance A): Int {
        val res: Triple<FList<A>, A?, FList<A>> = ffindFirst { it == element }
        return res.second?.let { res.first.size } ?: NOT_FOUND
    }

    override fun lastIndexOf(element: @UnsafeVariance A): Int =
        when (val rix = this.freverse().indexOf(element)) {
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

    // imcollection

    override val seal: IMSC = IMSC.IMLIST

    override fun fcontains(item: @UnsafeVariance A): Boolean =
        null != ffind { it == item }

    override fun fdropAll(items: IMCollection<@UnsafeVariance A>): FList <A> = if (items.fempty()) this else when(items) {
        is IMSet<A> -> this.ffoldLeft(emptyIMList()) { acc, element -> if (items.contains(element)) acc else FLCons(element, acc) }
        is IMKeyedValue<*,*> -> this.ffoldLeft(emptyIMList()) { acc, element ->
            element as TKVEntry<*,*>
            if (items.fcontainsKey(element.getk())) acc else FLCons(element, acc)
        }
        else -> this.ffoldLeft(emptyIMList()) { acc, element -> if (items.fcontains(element)) acc else FLCons(element, acc) }
    }

    override fun ffindAny(isMatch: (A) -> Boolean): A? =
        ffind(isMatch)

    private val strictness: Boolean by lazy { when {
        fempty() -> true
        fisNested()!! -> {
            val kc = fpickNotEmpty()?.let { it::class }
            kc?.let { itemKClass ->
                val ucKc = SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>()
                null == ffindAny { innerItem: A -> innerItem.isStrictlyNot(itemKClass) } &&
                null == ffindAny { maybeContainer: A -> !FT.itemStrictness(maybeContainer, maybeContainer::class, ucKc) }
            } ?: /* all nested containers, all are empty */ run {
                val auxv = fhead()!!::class
                fall { it.isStrictly(auxv) }
            }
        }
        else -> {
            val auxv = fhead()!!::class
            fall { it.isStrictly(auxv) }
        }
    }}

    override fun fisStrict(): Boolean = strictness

    override fun fpick(): A? = fhead()

    override fun fpopAndRemainder(): Pair<A?, FList<A>> = Pair(fhead(), ftail())

    // utility

    override fun equal(rhs: IMList<@UnsafeVariance A>): Boolean = this.equals(rhs)

    override fun fforEach (f: (A) -> Unit) {

        tailrec fun go(xs: FList<A>): Unit = when(xs) {
            is FLNil -> Unit
            is FLCons -> {
                f(xs.head)
                go(xs.tail)
            }
        }

        return go(this)
    }

    override fun fforEachReverse (f: (A) -> Unit) {

        val n = this.size
        tailrec fun go(xs: FList<A>, backwardIx: Int): Unit = if (backwardIx < 0) Unit else {
            val item: A? = atWantedIxPosition(backwardIx, n, xs, 0)
            item?.let{ f(it) }
            go(xs, backwardIx-1)
        }

        return go(this, n-1)
    }

    override fun copy(): FList<A> = this.ffoldRight(emptyIMList()) { a, b -> FLCons(a, b) }

    override fun copyToMutableList(): MutableList<@UnsafeVariance A> {
        val aux = arrayListOf<@UnsafeVariance A>()
        aux.ensureCapacity(fsize())
        return this.ffoldLeft(aux) { a, b -> a.add(b); a }
    }

    // filtering

    override fun fdrop(n: Int): FList<A> {

        tailrec fun dropNext(iter: Int, current: FList<A>): FList<A>  = when {
            n < iter -> current
            else -> dropNext(iter+1, current.ftail())
        }

        return when {
            n < 0 || size <= n -> FLNil
            else -> dropNext(1, this)
        }
    }

    override fun fdropItem(item: @UnsafeVariance A): FList<A> = this.ffilterNot { it == item }

    override fun fdropFirst(isMatch: (A) -> Boolean): FList<A> {
        val (before, _, after) = ffindFirst(isMatch)
        return flAppend(before, after)
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

        return go(this, FLNil).freverse()
    }

    override fun ffilterNot(isMatch: (A) -> Boolean): FList<A> {

        tailrec fun go(xs: FList<A>, acc: FList<A>): FList<A> = when(xs) {
            is FLNil -> acc
            is FLCons -> go(xs.tail, if (isMatch(xs.head)) acc else FLCons(xs.head,acc))
        }

        return go(this, FLNil).freverse()
    }

    override fun ffind(isMatch: (A) -> Boolean): A? {

        tailrec fun go(xs: FList<A>): A? = when (xs) {
            is FLNil -> null
            is FLCons -> when {
                isMatch(xs.fhead()!!) -> xs.fhead()
                else -> go(xs.tail)
            }
        }

        return go(this)
    }

    override fun ffindLast(isMatch: (A) -> Boolean): A? {
        return this.freverse().ffind(isMatch)
    }

    override fun fgetOrNull(ix: Int): A? = atWantedIxPosition(ix, this.size,this, 0)

    override fun fhasSubsequence(sub: IMList<@UnsafeVariance A>): Boolean = flHasSubsequence(this, sub)

    override fun fhead(): A? = when (this) {
        is FLNil -> null
        is FLCons -> this.head
    }

    val flistInit: FList<A> by lazy {
        ftake(this.size-1)
    }

    override fun finit(): FList<A> = flistInit

    override fun flast(): A? = atWantedIxPosition(this.size - 1, this.size, this, 0)

    override fun fslice(fromIndex: Int, toIndex: Int): /* [fromIndex, toIndex) */ FList<A> =
        when (val postfix = fdrop(fromIndex)) {
            is FLNil -> FLNil
            else -> postfix.ftake(toIndex - fromIndex)
        }

    override fun fselect(atIxs: IMList<Int>): FList<A> {

        tailrec fun go(ixs: IMList<Int>, acc: FList<A>): FList<A> = when (val currentIx = ixs.fhead()) {
            null -> acc
            else -> {
                val newAcc = if (currentIx < 0 || size <= currentIx) acc
                    else FLCons(get(currentIx), acc)
                go(ixs.ftail(), newAcc)
            }
        }

        return go(atIxs, FLNil).freverse()
    }

    override fun ftail(): FList<A> = when (this) {
        is FLNil -> FLNil
        is FLCons -> this.tail
    }

    override fun ftake(n: Int): FList<A> {

        tailrec fun takeNext(iter: Int, current: FList<A>, acc: FList<A>): FList<A> = when {
            n < iter -> acc
            else -> takeNext(iter+1, current.ftail(), FLCons(current.fhead()!!, acc))
        }

        return when {
            n < 0 -> FLNil
            size <= n -> this
            else -> takeNext(1, this, FLNil).freverse()
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

        return takeIfIsMatch(isMatch, this, FLNil).freverse()
    }

    // grouping

    override fun fsize(): Int = size

    override fun fcount(isMatch: (A) -> Boolean): Int {

        tailrec fun go(xs: FList<A>, counter: Int): Int = when(xs) {
            is FLNil -> counter
            is FLCons -> go(xs.tail, if (isMatch(xs.head)) counter + 1 else counter)
        }

        return go(this, 0)
    }

    override fun ffindFirst(isMatch: (A) -> Boolean): Triple< /* before */ FList<A>, A?, /* after */ FList<A>> {

        tailrec fun traverseToMatch(
            match: (A) -> Boolean,
            pos: FList<A>,
            acc: FList<A>): Triple<FList<A>, A?, FList<A>> = when (pos) {
                is FLNil -> /* not found */ Triple(this, null, FLNil)
                is FLCons -> when {
                    match(pos.head) -> Triple(acc.freverse(), pos.head, pos.tail)
                    else -> traverseToMatch(match, pos.tail, FLCons(pos.head, acc))
                }
            }

        return traverseToMatch(isMatch, this, FLNil)

    }

    override fun <B> fgroupBy(f: (A) -> B): IMMap<B, FList<A>> where B: Any, B: Comparable<B> {

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

        return go(this.freverse(), emptyMap<B, FList<A>>() as MutableMap<B, FList<A>>)
        */

        fun f4fl(acc: MutableMap<B, FList<A>>, element: A): MutableMap<B, FList<A>> {
            val key = f(element)
            acc[key] = FLCons(element, acc.getOrDefault(key, emptyIMList()))
            return acc
        }

        // return ffoldLeft(emptyMap<B, FList<A>>() as MutableMap<B, FList<A>>, ::f4fl)
        TODO("need FMap done to make this happen")
    }

    override fun findexed(offset: Int): FList<Pair<A, Int>> = fzipWith(IntRange(offset, Int.MAX_VALUE).iterator())

    override fun fpartition(isMatch: (A) -> Boolean): Pair<FList<A>, FList<A>> {

        fun f4fl(acc: Pair<FList<A>, FList<A>>, current: A): Pair<FList<A>, FList<A>> =
            if (isMatch(current)) Pair(FLCons(current, acc.first), acc.second)
            else Pair(acc.first, FLCons(current, acc.second))

        val p = ffoldLeft(Pair(emptyIMList(), emptyIMList()), ::f4fl)
        return Pair(p.first.freverse(), p.second.freverse())

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
            else -> go(this, FLNil).freverse()
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
            else -> go(this, FLNil).freverse()
        }
    }

    override fun fsplitAt(index: Int): Triple< /* before */ FList<A>, A?, /* after */ FList<A>> {

        tailrec fun traverseToIndex(pos: FList<A>, acc: FList<A>, ix: Int): Triple<FList<A>, A?, FList<A>> = when {
            index < 0 || size <= index -> Triple(this, null, FLNil)
            ix == index -> Triple(acc.freverse(), pos.fhead(), pos.ftail())
            else -> traverseToIndex(pos.ftail(), FLCons(pos.fhead()!!, acc), ix+1)
        }

        return traverseToIndex(this, FLNil, 0)
    }

    override fun <B: Any, C: Any> funzip(f: (A) -> Pair<B,C>): Pair<FList<B>, FList<C>> {

        fun f4fl(acc: Pair<FList<B>, FList<C>>, current: A): Pair<FList<B>, FList<C>> {
            val pair = f(current)
            return Pair(FLCons(pair.first, acc.first), FLCons(pair.second, acc.second))
        }

        val p = ffoldLeft(Pair(emptyIMList(), emptyIMList()), ::f4fl)
        return Pair(p.first.freverse(), p.second.freverse())
    }

    override fun <B: Any, C: Any> fzipWith(xs: IMList<B>, f: (A, B) -> C): FList<C> {

        tailrec fun go(xsa: FList<A>, xsb: IMList<B>, acc: FList<C>):FList<C> =
            if ((xsa is FLNil) || xsb.fempty()) acc
            else go((xsa as FLCons).tail,
                    xsb.ftail(),
                    FLCons(f(xsa.head, xsb.fhead()!!), acc))

        return go(this, xs, FLNil).freverse()
    }

    override fun <B: Any> fzipWhen(xs: IMList<B>, isMatch: (A, B) -> Boolean): FList<Pair<A, B>> {

        tailrec fun go(xsa: FList<A>, xsb: IMList<B>, acc: FList<Pair<A, B>>): FList<Pair<A, B>> =
            if ((xsa is FLNil) || xsb.fempty()) acc
            else go((xsa as FLCons).tail,
                xsb.ftail(),
                if (isMatch(xsa.head, xsb.fhead()!!)) FLCons(Pair(xsa.head, xsb.fhead()!!), acc) else acc)

        return go(this, xs, FLNil).freverse()
    }

    override fun <B: Any> fzipWhile(xs: IMList<B>, isMatch: (A, B) -> Boolean): FList<Pair<A, B>> {

        tailrec fun go(xsa: FList<A>, xsb: IMList<B>, acc: FList<Pair<A, B>>): FList<Pair<A, B>> = when {
            (xsa is FLNil) || xsb.fempty() -> acc
            else -> when {
                ! isMatch(xsa.fhead()!!, xsb.fhead()!!) -> acc
                else -> go(
                    (xsa as FLCons).tail,
                    xsb.ftail(),
                    FLCons(Pair(xsa.head, xsb.fhead()!!), acc)
                )
            }
        }

        return go(this, xs, FLNil).freverse()
    }

    override fun <B: Any> fzipWith(xs: Iterator<B>): FList<Pair<A,B>> {

        tailrec fun go(xsa: FList<A>, xsi: Iterator<B>, acc: FList<Pair<A,B>>):FList<Pair<A,B>> =
            if ((xsa is FLNil) || !xsi.hasNext()) acc
            else go((xsa as FLCons).tail, xsi,
                    FLCons(Pair(xsa.head, xsi.next()), acc))

        return go(this, xs, FLNil).freverse()
    }

    override fun fzipWithIndex(): FList<Pair<A, Int>> = findexed()

    override fun fzipWithIndex(startIndex: Int): FList<Pair<A, Int>> = fdrop(startIndex).findexed()

    // transforming

    override fun <B: Any> fflatMap(f: (A) -> IMList<B>): FList<B> {

        tailrec fun go(xs: FList<A>, out: FList<B>): FList<B> =
            when (xs) {
                is FLNil -> out
                is FLCons -> go(xs.tail, f(xs.head).ffoldLeft(out) { list, element -> FLCons(element, list) })
            }

        return go(this, emptyIMList<B>()).freverse()
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
        val reversed = this.freverse()
        return reversed.ffoldLeft(z, g)
    }

    override fun <B: Any> fmap(f: (A) -> B): FList<B> {

        tailrec fun go(xs: FList<A>, out: FList<B>): FList<B> =
            when(xs) {
                is FLNil -> out
                is FLCons -> go(xs.tail, FLCons<B>(f(xs.head), out))
            }

        return go(this, emptyIMList<B>()).freverse()
    }

    override fun freduceLeft(f: (acc: A, A) -> @UnsafeVariance A): A? = freduceLeft(this, f)

    override fun freduceRight(f: (A, acc: A) -> @UnsafeVariance A): A? {
        val xsar = this.freverse()
        fun g(acc:A, a:A) = f(a, acc)
        return freduceLeft(xsar, ::g)
    }

    override fun freverse(): FList<A> = fhead()?.let {
        if(ftail() is FLNil) this else ffoldLeft(emptyIMList()) { b, a -> FLCons(a, b) }
    } ?: this

    override fun frotr(): FList<A> = when(this) {
        is FLNil -> FLNil
        is FLCons -> if( 1 == this.size) this else {
            FLCons(this.flast()!!, this.fdropRight(1))
        }
    }

    override fun frotl(): FList<A> = when(this) {
        is FLNil -> FLNil
        is FLCons -> if( 1 == this.size) this else this.tail.fappend(this.head)
    }

    override fun fswaph(): FList<A> = when(this) {
        is FLNil -> FLNil
        is FLCons -> if (1 == this.size) this else FLCons(this.tail.fhead()!!, FLCons(this.head, this.tail.ftail()))
    }

    // ===== altering

    override fun fappend(item: @UnsafeVariance A): FList<A> = flSetLast(this, item)

    override fun fappendAll(elements: IMList<@UnsafeVariance A>): FList<A> = when(elements) {
        is FList -> flAppend(this, elements)
        else -> flAppend(this, of(elements))
    }

    override fun fprepend(item: @UnsafeVariance A): FList<A> = flSetHead(item, this)

    override fun fprependAll(elements: IMList<@UnsafeVariance A>): FList<A> = when (elements) {
        is FList -> flAppend(elements, this)
        else -> flAppend(of(elements), this)
    }

    override operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A> = this.fappendAll(of(rhs))
    override operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A> = this.fdropAll(of(rhs))

    companion object: IMListCompanion {

        val NOT_FOUND: Int = -1

        override fun <A: Any> emptyIMList(): FList<A> = FLNil

        override fun <A: Any> of(vararg items: A): FList<A> {
            var acc : FList<A> = FLNil
            if (items.isEmpty()) return acc
            items.reverse()
            items.forEach { acc = FLCons(it, acc) }
            return acc
        }

        override fun <A: Any> of(items: Iterator<A>): FList<A> {
            var acc : FList<A> = FLNil
            if (! items.hasNext()) return acc
            items.forEach { acc = FLCons(it, acc) }
            return acc.freverse()
        }

        override fun <A: Any> of(items: List<A>): FList<A> {
            var acc : FList<A> = FLNil
            if (items.isEmpty()) return acc
            val li = items.listIterator(items.size)
            while (li.hasPrevious()){ acc = FLCons(li.previous(), acc) }
            return acc
        }

        override fun <A: Any> of(items: IMList<A>): FList<A> = if (items.fempty()) FLNil else when (items) {
            is FList -> items
            else -> items.ffoldLeft(emptyIMList()) { acc, a -> FLCons(a, acc) }
        }

        override fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): FList<A> {
            var acc : FList<A> = FLNil
            if (! items.hasNext()) return acc
            items.forEach { acc = FLCons(f(it), acc) }
            return acc.freverse()
        }

        override fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): FList<A> {
            var acc : FList<A> = FLNil
            if (items.isEmpty()) return acc
            val li = items.listIterator(items.size)
            while (li.hasPrevious()){ acc = FLCons(f(li.previous()), acc) }
            return acc
        }

        // ==========

        override fun <A: Any> Collection<A>.toIMList(): IMList<A> = when(this) {
            is FList -> this
            is FKSet<*, A> -> this.copyToFList()
            is List -> of(this)
            else -> of(this.iterator())
        }

        // ========== implementation

//        TODO maybe...
//        inline fun <reified A: Any, reified B: Any > flatten(rhs: FList<B>): FList<A> =
//            @Suppress("UNCHECKED_CAST")
//            if (isNested(rhs) && !rhs.isEmpty()) when (val hd = firstNotEmpty(rhs as FList<*>)) {
//                is FLCons<*> -> if (hd.head is A) appendLists(rhs as FList<FList<A>>, FLNil) else FLNil
//                else -> FLNil
//            } else if (!rhs.isEmpty() && rhs.fhead()!! is A) rhs as FList<A>
//            else FLNil

        internal inline fun <reified A: Any, reified B: FList<A>> isNested(l: B): Boolean = when (l) {
            is FLCons<*> -> (l.head is FLNil) || (l.head is B)
            else -> false
        }

        internal inline fun <reified A: Any, reified B: FList<A>> firstNotEmpty(l: FList<A>): FList<A>? {
            val iter = l.iterator() as FListIteratorFwd<A>
            for ( el in iter) when (el) {
                is FLCons<*> -> if (el is B && !el.fempty()) return el
                else -> continue
            }
            return null
        }

        internal inline fun <reified A: Any, reified B: FList<A>> fflatten(rhs: FList<B>): FList<A> =
            if (isNested(rhs) && !rhs.isEmpty()) fappendLists(rhs, FLNil)
            else if (!rhs.isEmpty() && rhs.fhead()!! is A) rhs as B
            else FLNil

        internal fun <A: Any> fappendNested(rhs: FList<FList<A>>): FList<A> {
            @Suppress("UNCHECKED_CAST")
            return fappendLists(rhs, FLNil)
        }

        internal tailrec fun <A:Any> fappendLists(src: FList<FList<A>>, acc: FList<A>): FList<A> = when(src) {
            is FLNil -> acc
            is FLCons -> fappendLists(src.ftail(), flAppend(acc, src.head))
        }

        internal fun <A: Any> flAppend(lead: FList<A>, after: FList<A>): FList<A> =
            lead.ffoldRight(after) { leadElement: A, augmentedAfter -> FLCons(leadElement, augmentedAfter) }

        internal fun <A: Any> flHasSubsequence(xsa: FList<A>, sub: IMList<A>): Boolean {

            tailrec fun go(xsa: FList<A>, sub: FList<A>, partialMatch: Boolean): Boolean = when(sub) {
                is FLNil -> true
                is FLCons<A> -> when(xsa) {
                    is FLNil -> false
                    is FLCons<A> -> when {
                        partialMatch && xsa.fhead() != sub.fhead() -> false
                        xsa.fhead() != sub.fhead() -> go(xsa.ftail(), sub, false)
                        else -> go(xsa.ftail(), sub.ftail(), true)
                    }
                }
            }

            if (sub is FLNil) return true
            if (xsa is FLNil) return false
            return when (sub) {
                is FList -> go(xsa, sub, false)
                else -> go(xsa, of(sub), false)
            }
        }

        internal fun <A: Any> flSetHead(x: A, xs: FList<A>): FList<A> = when (xs) {
            is FLNil -> FLCons(x, FLNil)
            else -> FLCons(x, FLCons(xs.fhead()!!, xs.ftail()))
        }

        internal fun <A: Any> flSetLast(lead: FList<A>, after: A): FList<A> = flAppend(lead, FLCons(after, FLNil))

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

        private tailrec fun <A: Any> atWantedIxPosition(wantedIx: Int, stop: Int, l: FList<A>, ix: Int): A? = when {
            wantedIx < 0 || stop <= wantedIx -> null
            ix == wantedIx -> l.fhead()
            else -> atWantedIxPosition(wantedIx, stop, l.ftail(), ix + 1)
        }
    }
}

object FLNil: FList<Nothing>() {
    override fun toString(): String = "FLNil"
    override fun hashCode(): Int = toString().hashCode()
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMList<*> -> other.fempty()
        other is List<*> -> other.isEmpty()
        else -> false
    }
}

data class FLCons<out A: Any>(
        val head: A,
        val tail: FList<A>
) : FList<A>() {

    // the data class built-in equals is not stack safe for recursive data structures
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMList<*> -> when {
            other.fempty() -> false
            fhead()!!.isStrictlyNot(other.fhead()!!) -> false
            else -> @Suppress("UNCHECKED_CAST") IMListEqual2(this, other as IMList<A>)
        }
        other is List<*> -> when {
            other.isEmpty() -> false
            fhead()!!.isStrictlyNot(other.first()!!) -> false
            fsize() != other.size -> false
            else -> other.equals(this)
        }
        else -> false
    }

    val show: String by lazy { (ffoldLeft("${FList::class.simpleName}@{$size}:") { str, h -> "$str($h, #" }) + "*)".repeat(size) }

    // the data class built-in toString is not stack safe
    override fun toString(): String = show

    val hash: Int by lazy {

        /*
            Java's hashing (hash = 31 * hash + h.hashCode()) fails to distinguish among
            list permumutations in combinatorial set-ups; hence the following complications.
            The performance penalty is in the eye of the beholder, in case it matters.
         */

        val intKey: Boolean = head is TKVEntry<*,*> && (head.getk() is Int)
        val longKey: Boolean = head is TKVEntry<*,*> && (head.getk() is Long)
        when {
            head is Int -> {
                val thisInt = @Suppress("UNCHECKED_CAST")(this as FList<Int>)
                if (useMr) lChecksumHashCode(MurMur3at32(), thisInt) { it.toLong() }
                else uIntChecksumHashCode(CRC32C(), thisInt){ it }
            }
            head is Long -> {
                val thisLong = @Suppress("UNCHECKED_CAST") (this as FList<Long>)
                if (useMr) lChecksumHashCode(MurMur3at32(), thisLong) { it }
                else uIntChecksumHashCode(CRC32C(), thisLong) { it.to8ByteArray() }
            }
            intKey -> {
                val thisIntKey = @Suppress("UNCHECKED_CAST") (this as FList<TKVEntry<Int, *>>)
                if (useMr) lChecksumHashCode(MurMur3at32(), thisIntKey) { it.getk().toLong() }
                else uIntChecksumHashCode(CRC32C(), thisIntKey){ it.getk() }
            }
            longKey -> {
                val thisLongKey = @Suppress("UNCHECKED_CAST") (this as FList<TKVEntry<Long, *>>)
                if (useMr) lChecksumHashCode(MurMur3at32(), thisLongKey) { it.getk() }
                else uIntChecksumHashCode(CRC32C(), thisLongKey) { it.getk().to8ByteArray() }
            }
            else -> this.ffoldLeft(1549) { acc, h -> 31 * acc + h.hashCode() } /* fails the O(n!) tests */
        }
    }

    // the data class built-in hashCode is not stack safe
    override fun hashCode(): Int = hash

    companion object {
        // Note: about as good and as fast; will keep both for TODO: Bloom filters
        const val useCrc = false
        const val useMr = !useCrc
        //
        fun <A: Any> hashCode(cons: FLCons<A>) = cons.hash
    }

}