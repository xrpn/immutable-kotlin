package com.xrpn.hash

object JohnsonTrotter {

    /*
        Algorithm: see for instance Sedgewick, Robert (1977), "Permutation
        generation methods", ACM Comput. Surv., 9 (2): 137–164,
        doi:10.1145/356689.356692.  Also see, for a general description,
        https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm
        The following freely adapted with thanks from Sagar Shukla public code at
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

    private fun <A> shallowCopy(al: ArrayList<A>): ArrayList<A> {
        val res = ArrayList<A>(al.size)
        for(item in al) res.add(item)
        return res
    }

    private fun getMobileIx(aIx: Array<Int>, n: Int, mobile: Int): Int {
        for (i in (0 until n)) if (aIx[i] == mobile) return i + 1
        return 0
    }

    // An item is "mobile" if it is greater than its immediate neighbor in the direction it is looking at.
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

    private fun<A: Comparable<A>> nextPermutation(al: ArrayList<A>, alIx: Array<Int>, dir: Array<Boolean>): ArrayList<A> {

        val n = al.size
        val mobile: Int = findMobile (al, alIx, dir, n)
        val mobIx: Int = getMobileIx (alIx, n, mobile)

        // swap according to direction
        if (R2L == dir[alIx[mobIx - 1] - 1]) swap(mobIx-1, mobIx-2, al, alIx)
        else if (L2R == dir[alIx[mobIx - 1] - 1]) swap(mobIx, mobIx-1, al, alIx)

        // toggle directions for elements greater than mobile
        for (i in(0 until n)) {
            if (alIx[i] > mobile) {
                if (L2R == dir[alIx[i] - 1]) dir[alIx[i] - 1] = R2L
                else if (R2L == dir[alIx[i] - 1]) dir[alIx[i] - 1] = L2R
            }
        }

        // al now holds the new permutation; return a copy, since al is mutable
        return shallowCopy(al)
    }

    // NOT a general purpose factorial (range is way too limited.
    // 13! == 6706022400 exceeds Int range; 12! == 479001600 is probably
    // OK as a limit; larger can be accommodated by special request :)
    fun smallFact(n: Int): Int { check (n < 13); var res = 1; for (i in (2..n)) res *= i; return res }

    // compute all permutations of input data "a"
    fun <A: Comparable<A>> jtPermutations(input: ArrayList<A>): List<ArrayList<A>> {

        val n = input.size
        val endCardinality = smallFact(n)
        val dir: Array<Boolean> = Array(n){ R2L }
        val aIx: Array<Int> = Array(n){it+1}
        val res: MutableList<ArrayList<A>> = mutableListOf()

        // the original input is the first permutation; make a copy, since a is mutable.
        res.add(shallowCopy(input))
        // the algorithm does not terminate naturally
        for (i in (2..endCardinality)) { res.add(nextPermutation(input, aIx, dir)) }

        return res
    }

    @JvmStatic
    fun main(args: Array<String>)
    {
        check( 1 == smallFact(0))
        check( 1 == smallFact(1))
        check( 6 == smallFact(3))
        // example of driver
        val n: Int = 4
        val items: ArrayList<String> = ArrayList(n)
        val a: Array<String> = Array(n){('a'+it).toString()}
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
