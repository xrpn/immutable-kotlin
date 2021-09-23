package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.immutable.FKMap.Companion.of
import com.xrpn.immutable.FKMap.Companion.emptyIMMap
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int

class FMapTest : FunSpec({

    val repeatsLow = Pair(2, 40000)

    beforeTest {}

    test("keys, values, entries") {
        val bulkMapWiki = of(frbWikiPreorder)
        val szw = frbWikiPreorder.size
        val bkw = bulkMapWiki.fkeys()
        bkw.fsize() shouldBe szw
        for(key in FListIteratorFwd(frbWikiPreorder.fmap { it.getk() })) bkw.fcontains(key) shouldBe true
        bulkMapWiki.fvalues() shouldBe frbWikiPreorder.fmap { it.getv() }.freverse()
        bulkMapWiki.fentries() shouldBe frbWikiPreorder.toSet() // TODO fix this, .toSet() is suboptimal
        val bulkMapSs = of(frbSlideSharePreorder)
        val szss = frbSlideSharePreorder.size
        val bkss = bulkMapSs.fkeys()
        bkss.fsize() shouldBe szss
        for(key in FListIteratorFwd(frbSlideSharePreorder.fmap { it.getk() })) bkss.fcontains(key) shouldBe true
        bulkMapSs.fvalues() shouldBe frbSlideSharePreorder.fmap { it.getv() }.freverse()
        bulkMapSs.fentries() shouldBe frbSlideSharePreorder.toSet() // TODO fix this, .toSet() is suboptimal
    }

    //
    // ================ companion object
    //

    test("co.==") {
        (emptyIMMap<Int, Int>() == emptyIMMap<Int, Int>()) shouldBe true
        emptyIMMap<Int, Int>().equal(emptyIMMap<Int, Int>()) shouldBe true
        of(frbWikiBreadthFirst).equal(of(frbWikiPostorder)) shouldBe true
        of(frbSlideSharePostorder).equal(of(frbSlideSharePreorder)) shouldBe true
        (of(frbWikiBreadthFirst) == of(frbSlideSharePreorder)) shouldBe false
    }

    test("co general workout (property) random int-int").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val sorted = (Array(n) { i: Int -> Pair(i, i) })
            val shuffled = (Array(n) { i: Int -> Pair(i, i) })
            shuffled.shuffle()
            val bulkMap: FKMap<Int, Int> = of(shuffled.iterator())
            var mapkv = emptyIMMap<Int, Int>()
            var mapp = emptyIMMap<Int, Int>()
            val mapv = emptyIMMap<Int, Int>().fputList(FList.of(shuffled.iterator()).fmap{TKVEntry.of(it)})
            for (item in sorted) {
                mapkv = mapkv.fputkv(item.first, item.second)
                mapp = mapp.fputPair(Pair(item.first, item.second))
            }
            bulkMap.size shouldBe n
            mapkv.size shouldBe n
            mapp.size shouldBe n
            mapv.size shouldBe n
            bulkMap.get(-1) shouldBe null
            (bulkMap.fcontains(-1)) shouldBe false
            mapkv.get(-1) shouldBe null
            (mapkv.fcontains(-1)) shouldBe false
            mapp.get(-1) shouldBe null
            (mapp.fcontains(-1)) shouldBe false
            mapv.get(-1) shouldBe null
            (mapv.fcontains(-1)) shouldBe false
            for (k in 0 until n) {
                bulkMap.get(k) shouldBe k
                (bulkMap.fcontains(k)) shouldBe true
                mapkv.get(k) shouldBe k
                (mapkv.fcontains(k)) shouldBe true
                mapp.get(k) shouldBe k
                (mapp.fcontains(k)) shouldBe true
                mapv.get(k) shouldBe k
                (mapv.fcontains(k)) shouldBe true
                bulkMap.get(k) shouldBe bulkMap.fgetOrElse(k, {-1})
                mapkv.get(k) shouldBe mapkv.fgetOrElse(k, {-1})
                mapp.get(k) shouldBe mapp.fgetOrElse(k, {-1})
                mapv.get(k) shouldBe mapv.fgetOrElse(k, {-1})
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
            val shuffled: Array<TKVEntry<String, String>> = (Array(n) { i: Int -> TKVEntry.of(i.toString(), i.toString()) })
            shuffled.shuffle()
            val bulkMap = of(shuffled.map { it.toPair() }.iterator())
            var mapkv = emptyIMMap<String, String>()
            var mapp = emptyIMMap<String, String>()
            val mapv = emptyIMMap<String, String>().fputList(FList.of(shuffled.iterator()))
            for (item in sorted) {
                mapkv = mapkv.fputkv(item.getk(), item.getv())
                mapp = mapp.fputPair(Pair(item.getk(), item.getv()))
            }
            bulkMap.size shouldBe n
            mapkv.size shouldBe n
            mapp.size shouldBe n
            mapv.size shouldBe n
            bulkMap.get((-1).toString()) shouldBe null
            (bulkMap.fcontains((-1).toString())) shouldBe false
            mapkv.get((-1).toString()) shouldBe null
            (mapkv.fcontains((-1).toString())) shouldBe false
            mapp.get((-1).toString()) shouldBe null
            (mapp.fcontains((-1).toString())) shouldBe false
            mapv.get((-1).toString()) shouldBe null
            (mapv.fcontains((-1).toString())) shouldBe false
            for (k in 0..n-1) {
                val ks = k.toString()
                bulkMap.get(ks) shouldBe ks
                (bulkMap.fcontains(ks)) shouldBe true
                mapkv.get(ks) shouldBe ks
                (mapkv.fcontains(ks)) shouldBe true
                mapp.get(ks) shouldBe ks
                (mapp.fcontains(ks)) shouldBe true
                mapv.get(ks) shouldBe ks
                (mapv.fcontains(ks)) shouldBe true
                bulkMap.get(ks) shouldBe bulkMap.fgetOrElse(ks) { (-1).toString() }
                mapkv.get(ks) shouldBe mapkv.fgetOrElse(ks) { (-1).toString() }
                mapp.get(ks) shouldBe mapp.fgetOrElse(ks) { (-1).toString() }
                mapv.get(ks) shouldBe mapv.fgetOrElse(ks) { (-1).toString() }
            }
            val ns = n.toString()
            bulkMap.get(ns) shouldBe null
            mapkv.get(ns) shouldBe null
            mapp.get(ns) shouldBe null
            mapv.get(ns) shouldBe null
        }
    }
})
