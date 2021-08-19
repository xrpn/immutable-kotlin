package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.immutable.FMap.Companion.of
import com.xrpn.immutable.FMap.Companion.add
import com.xrpn.immutable.FMap.Companion.contains
import com.xrpn.immutable.FMap.Companion.emptyFMap
import com.xrpn.immutable.FMap.Companion.equal
import com.xrpn.immutable.FMap.Companion.get
import com.xrpn.immutable.FMap.Companion.getOrElse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int

class FMapTest : FunSpec({

    val repeatsLow = Pair(2, 40000)

    beforeTest {
    }

    test("keys, values, entries") {
        val bulkMapWiki = of(frbWikiPreorder)
        val szw = frbWikiPreorder.size
        val bkw = bulkMapWiki.keys()
        bkw.size shouldBe szw
        for(key in FListIteratorFwd(frbWikiPreorder.fmap { it.getk() })) bkw.contains(key) shouldBe true
        bulkMapWiki.values() shouldBe frbWikiPreorder.fmap { it.getv() }.freverse()
        bulkMapWiki.entries() shouldBe frbWikiPreorder.freverse()
        val bulkMapSs = of(frbSlideSharePreorder)
        val szss = frbSlideSharePreorder.size
        val bkss = bulkMapSs.keys()
        bkss.size shouldBe szss
        for(key in FListIteratorFwd(frbSlideSharePreorder.fmap { it.getk() })) bkss.contains(key) shouldBe true
        bulkMapSs.values() shouldBe frbSlideSharePreorder.fmap { it.getv() }.freverse()
        bulkMapSs.entries() shouldBe frbSlideSharePreorder.freverse()
    }

    //
    // ================ companion object
    //

    test("co.==") {
        (emptyFMap<Int, Int>() == emptyFMap<Int, Int>()) shouldBe true
        emptyFMap<Int, Int>().equal(emptyFMap<Int, Int>()) shouldBe true
        of(frbWikiBreadthFirst).equal(of(frbWikiPostorder)) shouldBe true
        of(frbSlideSharePostorder).equal(of(frbSlideSharePreorder)) shouldBe true
        (of(frbWikiBreadthFirst) == of(frbSlideSharePreorder)) shouldBe false
        of(frbWikiBreadthFirst).equal(of(frbSlideSharePreorder)) shouldBe false
    }

    test("co general workout (property) random int-int").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            shuffled.shuffle()
            val bulkMap = of(shuffled.iterator())
            var mapkv = emptyFMap<Int, Int>()
            var mapp = emptyFMap<Int, Int>()
            val mapv = emptyFMap<Int, Int>().add(FList.of(shuffled.iterator()).fmap { Pair(it.getk(), it.getv()) })
            for (item in sorted) {
                mapkv = mapkv.add(item.getk(), item.getv())
                mapp = mapp.add(Pair(item.getk(), item.getv()))
            }
            bulkMap.size() shouldBe n
            mapkv.size() shouldBe n
            mapp.size() shouldBe n
            mapv.size() shouldBe n
            bulkMap.get(-1) shouldBe null
            (-1 in bulkMap) shouldBe false
            mapkv.get(-1) shouldBe null
            (-1 in mapkv) shouldBe false
            mapp.get(-1) shouldBe null
            (-1 in mapp) shouldBe false
            mapv.get(-1) shouldBe null
            (-1 in mapv) shouldBe false
            for (k in 0 until n) {
                bulkMap.get(k) shouldBe k
                (k in bulkMap) shouldBe true
                mapkv.get(k) shouldBe k
                (k in mapkv) shouldBe true
                mapp.get(k) shouldBe k
                (k in mapp) shouldBe true
                mapv.get(k) shouldBe k
                (k in mapv) shouldBe true
                bulkMap.get(k) shouldBe bulkMap.getOrElse(k, -1)
                mapkv.get(k) shouldBe mapkv.getOrElse(k, -1)
                mapp.get(k) shouldBe mapp.getOrElse(k, -1)
                mapv.get(k) shouldBe mapv.getOrElse(k, -1)
            }
            bulkMap.get(n) shouldBe null
            mapkv.get(n) shouldBe null
            mapp.get(n) shouldBe null
            mapv.get(n) shouldBe null
        }
    }

    test("co general workout (property) random str-str").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i.toString(), i.toString()) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i.toString(), i.toString()) })
            shuffled.shuffle()
            val bulkMap = of(shuffled.iterator())
            var mapkv = emptyFMap<String, String>()
            var mapp = emptyFMap<String, String>()
            val mapv = emptyFMap<String, String>().add(FList.of(shuffled.iterator()).fmap { Pair(it.getk(), it.getv()) })
            for (item in sorted) {
                mapkv = mapkv.add(item.getk(), item.getv())
                mapp = mapp.add(Pair(item.getk(), item.getv()))
            }
            bulkMap.size() shouldBe n
            mapkv.size() shouldBe n
            mapp.size() shouldBe n
            mapv.size() shouldBe n
            bulkMap.get((-1).toString()) shouldBe null
            ((-1).toString() in bulkMap) shouldBe false
            mapkv.get((-1).toString()) shouldBe null
            ((-1).toString() in mapkv) shouldBe false
            mapp.get((-1).toString()) shouldBe null
            ((-1).toString() in mapp) shouldBe false
            mapv.get((-1).toString()) shouldBe null
            ((-1).toString() in mapv) shouldBe false
            for (k in 0..n-1) {
                val ks = k.toString()
                bulkMap.get(ks) shouldBe ks
                (ks in bulkMap) shouldBe true
                mapkv.get(ks) shouldBe ks
                (ks in mapkv) shouldBe true
                mapp.get(ks) shouldBe ks
                (ks in mapp) shouldBe true
                mapv.get(ks) shouldBe ks
                (ks in mapv) shouldBe true
                bulkMap.get(ks) shouldBe bulkMap.getOrElse(ks, (-1).toString())
                mapkv.get(ks) shouldBe mapkv.getOrElse(ks, (-1).toString())
                mapp.get(ks) shouldBe mapp.getOrElse(ks, (-1).toString())
                mapv.get(ks) shouldBe mapv.getOrElse(ks, (-1).toString())
            }
            val ns = n.toString()
            bulkMap.get(ns) shouldBe null
            mapkv.get(ns) shouldBe null
            mapp.get(ns) shouldBe null
            mapv.get(ns) shouldBe null
        }
    }
})
