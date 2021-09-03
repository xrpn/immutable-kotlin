package com.xrpn.hash

object JohnsonTrotter {

    /*
        freely adapted with thanks from Sagar Shukla public code at
        https://www.geeksforgeeks.org/johnson-trotter-algorithm/
     */

    // also, 1 represents left to right.
    private const val L2R = true
    // also, 0 represents right to left
    private const val R2L = false

    private fun<A: Comparable<A>> swap (i: Int, j: Int, a: ArrayList<A>, aIx: Array<Int>) {
        val t = a[i]
        val tIx = aIx[i]
        a[i] = a[j]
        aIx[i] = aIx[j]
        a[j] = t
        aIx[j] = tIx
    }

    private fun getMobileIx(aIx: Array<Int>, n: Int, mobile: Int): Int {
        for (i in (0 until n)) if (aIx[i] == mobile) return i + 1
        return 0
    }

    // An item is mobile if it is greater than its immediate neighbor in the direction it is looking at.
    private fun<A: Comparable<A>> findMobile(a: ArrayList<A>, aIx: Array<Int>, dir: Array<Boolean>, n: Int): Int {
        var mobilePrev: Int = 0
        var mobile: Int = 0

        for (i in (0 until n)) {
            if (dir[aIx[i] - 1] == R2L && i != 0) {
                if (a[i] > a[i - 1] && aIx[i] > mobilePrev) {
                    mobile = aIx[i]
                    mobilePrev = mobile
                }
            } else if (dir[aIx[i] - 1] == L2R && i != n -1) {
                if (aIx[i] > aIx[i + 1] && aIx[i] > mobilePrev) {
                    mobile = aIx[i]
                    mobilePrev = mobile
                }
            }
        }

        return if (mobile == 0 && mobilePrev == 0) 0 else mobile
    }

    private fun<A: Comparable<A>> single(a: ArrayList<A>, aIx: Array<Int>, dir: Array<Boolean>): ArrayList<A> {

        val n = a.size
        val mobile: Int = findMobile (a, aIx, dir, n)
        val pos: Int = getMobileIx (aIx, n, mobile)

        // swapp according to direction
        if (R2L == dir[aIx[pos - 1] - 1]) swap(pos-1, pos-2, a, aIx)
        else if (L2R == dir[aIx[pos - 1] - 1]) swap(pos, pos-1, a, aIx)

        // toggle directions for elements greater than mobile
        for (i in(0 until n)) {
            if (aIx[i] > mobile) {
                if (L2R == dir[aIx[i] - 1]) dir[aIx[i] - 1] = R2L
                else if (R2L == dir[aIx[i] - 1]) dir[aIx[i] - 1] = L2R
            }
        }

        // a holds the permutation; return a copy, since a is mutable
        // if it is not necessary to keep all permutations, change here!
        val res = ArrayList<A>(a.size)
        for(item in a) res.add(item)
        return res
    }

    // 13! == 6706022400 exceeds Int range; 12! == 479001600 is probably
    // OK as a limit; larger can be accommodated by special request :)
    fun smallFact(n: Int): Int { check (n < 13); var res = 1; for (i in (1..n)) res *= i; return res }

    // compute all permutations of input data "a"
    fun <A: Comparable<A>> jtPermutations(a: ArrayList<A>): List<ArrayList<A>> {

        val n = a.size
        val end = smallFact(n) // will blow up with IllegalStateException
        val dir: Array<Boolean> = Array(n){ R2L }
        val aIx: Array<Int> = Array(n){it+1}
        val res: MutableList<ArrayList<A>> = mutableListOf()

        // the original is the first permutation; return a copy, since a is mutable
        // if it is not necessary to keep all permutations, change here!
        val pres = ArrayList<A>(a.size)
        for(item in a) pres.add(item)
        res.add(pres)
        // the algorithm does not terminate naturally
        for (i in (2..end)) { res.add(single(a, aIx, dir)) }

        return res
    }

    @JvmStatic
    fun main(args: Array<String>)
    {
        val n: Int = 4
        val items: ArrayList<String> = ArrayList(n)
        val a = Array(n){('a'+it).toString()}
        for (item in a) items.add(item)
        val res: List<ArrayList<String>> = jtPermutations(items)
        var count = 0
        for (perm in res) {
            print("$count\t")
            count+=1
            for (j in perm) print(j)
            println()
        }
    }
}
