package com.xrpn.immutable

sealed class FStack<out A: Any> {

    fun nullableTop(): A? = when (this.isEmpty()) {
        true -> null
        false -> (this as FStackBody).body.fhead()
    }

    fun top(): A = when (this.isEmpty()) {
        true -> throw IllegalStateException("nul stack")
        false -> {
            this as FStackBody
            when (body) {
                is FLNil -> throw IllegalStateException("empty stack")
                is FLCons -> body.head
            }
        }
    }

    fun nullablePop(): Pair<A?, FStack<A>> = when(this.isEmpty()) {
        true -> Pair(null, this)
        false -> {
            this as FStackBody
            Pair(body.fhead(), FStackBody.of(body.ftail()))
        }
    }

    fun pop(): Pair<A, FStack<A>> = when(this.isEmpty()) {
        true -> throw IllegalStateException("pop from empty stack")
        false ->{
            (this as FStackBody).body as FLCons
            Pair(body.fhead()!!, FStackBody.of(body.ftail()))
        }
    }

    fun isEmpty(): Boolean = this === FStackBody.EMPTY

    fun depth(): Int = if (this.isEmpty()) 0 else (this as FStackBody).body.size

    // the head of the list is the first item out (i.e. the top of the stack)
    fun asFList(): FList<A> = (this as FStackBody).body

    override fun equals(other: Any?): Boolean = when (this) {
        is FStackBody -> other is FStackBody<*> && this == other
    }

    override fun hashCode(): Int = when (this) {
        is FStackBody -> FStackBody.hashCode(this)
    }

    companion object {

        fun <A: Any> emptyFStack(): FStack<A> = FStackBody.EMPTY

        fun <A: Any> reverse(s: FStack<A>): FStack<A> = when (s) {
            is FStackBody -> s.freverse()
        }

        fun <A: Any> push(s: FStack<A>, item: A): FStack<A> = when (s.isEmpty()) {
            true -> FStackBody.of(FLCons(item, FLNil))
            false -> FStackBody.of(FLCons(item, (s as FStackBody).body))
        }

        fun<A: Any> equal2(lhs: FStack<A>, rhs: FStack<A>): Boolean = when(Pair(lhs.isEmpty(), rhs.isEmpty())) {
            Pair(false, false) -> if (lhs === rhs) true else {
                val ld = lhs.depth()
                val rd = rhs.depth()
                when {
                    ld != rd -> false
                    ld == 0 -> true
                    else -> (lhs as FStackBody).body == (rhs as FStackBody).body
                }
            }
            Pair(true, true) -> true
            else -> false
        }

        fun<A: Any> FStack<A>.equal(rhs: FStack<A>): Boolean = equal2(this, rhs)
    }
}

internal class FStackBody<out A: Any> internal constructor (
    val body: FList<A>
): FStack<A>() {
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FStackBody<*> -> when {
            this.isEmpty() && other.isEmpty() -> true
            this.isEmpty() || other.isEmpty() -> false
            this.nullableTop()!!::class == other.nullableTop()!!::class -> equal2(this, other)
            else -> false
        }
        else -> false
    }

    override fun hashCode(): Int {
        return body.hashCode()
    }

    override fun toString(): String {
        return if (this.isEmpty()) FStack::class.simpleName+"(EMPTY)"
        else this::class.simpleName+"("+body.toString()+")"
    }

    fun freverse(): FStack<A> = FStackBody(this.body.freverse())

    companion object {
        val EMPTY: FStackBody<Nothing> = FStackBody(FLNil)

        fun <A: Any> of(
            _body: FList<A> = FLNil,
        ): FStack<A> {
            return if (_body is FLNil) emptyFStack() else FStackBody(_body)
        }

        fun <A: Any> hashCode(s: FStackBody<A>) = s.hashCode()
    }

}
