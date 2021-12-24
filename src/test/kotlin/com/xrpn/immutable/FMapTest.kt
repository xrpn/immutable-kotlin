package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.imapi.IMSet
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
        val bkw: IMSet<Int> = bulkMapWiki.fkeys()
        bkw.fsize() shouldBe szw
        for(key in FListIteratorFwd(frbWikiPreorder.fmap { it.getk() })) bkw.fcontains(key) shouldBe true
        bulkMapWiki.fvalues() shouldBe frbWikiPreorder.fmap { it.getv() }.freverse()
        bulkMapWiki.fentries().equals(FKSet.ofi(frbWikiPreorder)) shouldBe true
        bulkMapWiki.fentries().equals(FKSet.ofs(frbWikiPreorder)) shouldBe true
        val bulkMapSs = of(frbSlideSharePreorder)
        val szss = frbSlideSharePreorder.size
        val bkss: IMSet<Int> = bulkMapSs.fkeys()
        bkss.fsize() shouldBe szss
        for(key in FListIteratorFwd(frbSlideSharePreorder.fmap { it.getk() })) bkss.fcontains(key) shouldBe true
        bulkMapSs.fvalues() shouldBe frbSlideSharePreorder.fmap { it.getv() }.freverse()
        bulkMapSs.fentries().equals(FKSet.ofi(frbSlideSharePreorder)) shouldBe true
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
            val mapv = emptyIMMap<Int, Int>().fputList(FList.of(shuffled.iterator()).fmap{TKVEntry.ofp(it)})
            for (item in sorted) {
                mapkv = mapkv.fputkv(item.first, item.second)
                mapp = mapp.fputPair(Pair(item.first, item.second))
            }
            bulkMap.fsize() shouldBe n
            mapkv.fsize() shouldBe n
            mapp.fsize() shouldBe n
            mapv.fsize() shouldBe n
            //TODO OOM_ERR bulkMap.asMap().get(-1) shouldBe null
            bulkMap[-1] shouldBe null
            (bulkMap.fcontainsKey(-1)) shouldBe false
            //TODO OOM_ERR mapkv.asMap().get(-1) shouldBe null
            mapkv[-1] shouldBe null
            (mapkv.fcontainsKey(-1)) shouldBe false
            //TODO OOM_ERR mapp.asMap().get(-1) shouldBe null
            mapp[-1] shouldBe null
            (mapp.fcontainsKey(-1)) shouldBe false
            //TODO OOM_ERR mapv.asMap().get(-1) shouldBe null
            mapv[-1] shouldBe null
            (mapv.fcontainsKey(-1)) shouldBe false
            for (k in 0 until n) {
                //TODO OOM_ERR bulkMap.asMap().get(k) shouldBe k
                bulkMap[k] shouldBe k
                (bulkMap.fcontainsKey(k)) shouldBe true
                mapkv[k] shouldBe k
                //TODO OOM_ERR mapkv.asMap().get(k) shouldBe k
                (mapkv.fcontainsKey(k)) shouldBe true
                //TODO OOM_ERR mapp.asMap().get(k) shouldBe k
                mapp[k] shouldBe k
                (mapp.fcontainsKey(k)) shouldBe true
                //TODO OOM_ERR mapv.asMap().get(k) shouldBe k
                mapv[k] shouldBe k
                (mapv.fcontainsKey(k)) shouldBe true
                //TODO OOM_ERR bulkMap.asMap().get(k) shouldBe bulkMap.fgetOrElse(k, {-1})
                bulkMap[k] shouldBe bulkMap.fgetOrElse(k, {-1})
                //TODO OOM_ERR mapkv.asMap().get(k) shouldBe mapkv.fgetOrElse(k, {-1})
                mapkv[k] shouldBe mapkv.fgetOrElse(k, {-1})
                //TODO OOM_ERR mapp.asMap().get(k) shouldBe mapp.fgetOrElse(k, {-1})
                mapp[k] shouldBe mapp.fgetOrElse(k, {-1})
                //TODO OOM_ERR mapv.asMap().get(k) shouldBe mapv.fgetOrElse(k, {-1})
                mapv[k] shouldBe mapv.fgetOrElse(k, {-1})
            }
            //TODO OOM_ERR bulkMap.asMap().get(n) shouldBe null
            bulkMap[n] shouldBe null
            //TODO OOM_ERR mapkv.asMap().get(n) shouldBe null
            mapkv[n] shouldBe null
            //TODO OOM_ERR mapp.asMap().get(n) shouldBe null
            mapp[n] shouldBe null
            //TODO OOM_ERR mapv.asMap().get(n) shouldBe null
            mapv[n] shouldBe null
        }
    }

    test("co general workout (property) random str-str").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.ofkk(i.toString(), i.toString()) })
            val shuffled: Array<TKVEntry<String, String>> = (Array(n) { i: Int -> TKVEntry.ofkk(i.toString(), i.toString()) })
            shuffled.shuffle()
            val bulkMap = of(shuffled.map { it.toPair() }.iterator())
            var mapkv = emptyIMMap<String, String>()
            var mapp = emptyIMMap<String, String>()
            val mapv = emptyIMMap<String, String>().fputList(FList.of(shuffled.iterator()))
            for (item in sorted) {
                mapkv = mapkv.fputkv(item.getk(), item.getv())
                mapp = mapp.fputPair(Pair(item.getk(), item.getv()))
            }
            bulkMap.fsize() shouldBe n
            mapkv.fsize() shouldBe n
            mapp.fsize() shouldBe n
            mapv.fsize() shouldBe n
            //TODO OOM_ERR bulkMap.asMap().get((-1).toString()) shouldBe null
            bulkMap[(-1).toString()] shouldBe null
            (bulkMap.fcontainsKey((-1).toString())) shouldBe false
            //TODO OOM_ERR mapkv.asMap().get((-1).toString()) shouldBe null
            mapkv[(-1).toString()] shouldBe null
            (mapkv.fcontainsKey((-1).toString())) shouldBe false
            //TODO OOM_ERR mapp.asMap().get((-1).toString()) shouldBe null
            mapp[(-1).toString()] shouldBe null
            (mapp.fcontainsKey((-1).toString())) shouldBe false
            //TODO OOM_ERR mapv.asMap().get((-1).toString()) shouldBe null
            mapv[(-1).toString()] shouldBe null
            (mapv.fcontainsKey((-1).toString())) shouldBe false
            for (k in 0..n-1) {
                val ks = k.toString()
                //TODO OOM_ERR bulkMap.asMap().get(ks) shouldBe ks
                bulkMap[ks] shouldBe ks
                (bulkMap.fcontainsKey(ks)) shouldBe true
                //TODO OOM_ERR mapkv.asMap().get(ks) shouldBe ks
                mapkv[ks] shouldBe ks
                (mapkv.fcontainsKey(ks)) shouldBe true
                //TODO OOM_ERR mapp.asMap().get(ks) shouldBe ks
                mapp[ks] shouldBe ks
                (mapp.fcontainsKey(ks)) shouldBe true
                //TODO OOM_ERR mapv.asMap().get(ks) shouldBe ks
                mapv[ks] shouldBe ks
                (mapv.fcontainsKey(ks)) shouldBe true
                //TODO OOM_ERR bulkMap.asMap().get(ks) shouldBe bulkMap.fgetOrElse(ks) { (-1).toString() }
                bulkMap[ks] shouldBe bulkMap.fgetOrElse(ks) { (-1).toString() }
                //TODO OOM_ERR mapkv.asMap().get(ks) shouldBe mapkv.fgetOrElse(ks) { (-1).toString() }
                mapkv[ks] shouldBe mapkv.fgetOrElse(ks) { (-1).toString() }
                //TODO OOM_ERR mapp.asMap().get(ks) shouldBe mapp.fgetOrElse(ks) { (-1).toString() }
                mapp[ks] shouldBe mapp.fgetOrElse(ks) { (-1).toString() }
                //TODO OOM_ERR mapv.asMap().get(ks) shouldBe mapv.fgetOrElse(ks) { (-1).toString() }
                mapv[ks] shouldBe mapv.fgetOrElse(ks) { (-1).toString() }
            }
            val ns = n.toString()
            //TODO OOM_ERR bulkMap.asMap().get(ns) shouldBe null
            bulkMap[ns] shouldBe null
            //TODO OOM_ERR mapkv.asMap().get(ns) shouldBe null
            mapkv[ns] shouldBe null
            //TODO OOM_ERR mapp.asMap().get(ns) shouldBe null
            mapp[ns] shouldBe null
            //TODO OOM_ERR mapv.asMap().get(ns) shouldBe null
            mapv[ns] shouldBe null
        }
    }
})
