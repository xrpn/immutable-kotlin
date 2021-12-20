package com.xrpn.immutable.fbstreetest

import com.xrpn.imapi.IMKeyedValue
import com.xrpn.imapi.KeyedTypeSample
import com.xrpn.immutable.*
import com.xrpn.immutable.FKMap.Companion.emptyIMMap
import com.xrpn.immutable.TKVEntry.Companion.intKeyOf
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.xrpn.fbsItree
import io.kotest.property.checkAll
import io.kotest.xrpn.frbStree

private val iiTreeOfNone: IMKeyedValue<Int,Int> = FBSTree.nul<Int,Int>()
private val iiTreeOfTwo: IMKeyedValue<Int,Int> = FBSTree.of(1.toIAEntry(), 2.toIAEntry())
private val isTreeOfTwo: IMKeyedValue<Int,String> = FBSTree.of("1".toIAEntry(), "2".toIAEntry())
private val ssTreeOfTwo: IMKeyedValue<String,String> = FBSTree.of("1".toSAEntry(), "2".toSAEntry())
private val siTreeOfTwo: IMKeyedValue<String,Int> = FBSTree.of(1.toSAEntry(), 2.toSAEntry())
private val ixTreeOfTwo: IMKeyedValue<Int, FKSet<*, Int>> = FBSTree.of(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry())
private val ixxTreeOfTwo: FBSTree<Int, FKSet<Int, RTKVEntry<Int, FKSet<*, Int>>>> = FBSTree.of(
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry(),
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry()
)
private val ixxxTreeOfTwo: FBSTree<Int, Set<RTKVEntry<Int, FKSet<*, Int>>>> = FBSTree.of(
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry(),
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry()
)
private val izTreeOfTwo: FBSTree<Int, Set<RTKVEntry<Int, FKSet<Int, Int>>>> = FBSTree.of(
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofi(2).toIAEntry()).toIAEntry(),
  setOf(FKSet.ofi(3).toIAEntry(), FKSet.ofi(4).toIAEntry()).toIAEntry()
)

class FBSTreeIMKeyedTest : FunSpec({

  // val repeatsHigh = Pair(50, 100)
  val repeatsMid = Pair(25, 50)

  beforeTest {}

  test("fcontainsKey") {
    FBSTree.nul<Int, String>().fcontainsKey(zEntry.getk()) shouldBe false
    tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          val p = t.fcontainsKey(acc.head.getk())
          p shouldBe true
          go(t, acc.tail)
        }
      }
    go(wikiTree, wikiPreorder)
    wikiTree.fcontainsKey(zEntry.getk()) shouldBe false
    go(slideShareTree, slideShareBreadthFirst)
    slideShareTree.fcontainsKey(100) shouldBe false
  }

  test("fcountKey") {
    FBSTree.nul<Int, String>().fcountKey { true } shouldBe 0
    checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
      val source = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val frbTree = FBSTree.of(source.iterator())
      val threshold = source.size / 3
      frbTree.fcountKey { it < threshold  } shouldBe threshold
    }
  }

  test("ffilterKey") {
    FBSTree.nul<Int, String>().ffilterKey { true }.fempty() shouldBe true
    FBSTree.nul<Int, String>().ffilterKey { false }.fempty() shouldBe true
    checkAll(repeatsMid.first, Arb.fbsItree(Arb.int(), 5..repeatsMid.second)) { frbII ->
      val smallest = frbII.inorder().fhead()!!
      val small = frbII.ffilterKey { k: Int -> k.equals(smallest.getk()) }
      small.froot()?.equals(smallest) shouldBe true
      small.fsize() shouldBe 1
      val (evenKey: FBSTree<Int, Int>, _: FBSTree<Int, Int>) = frbII.fpartition { tkv -> tkv.getk().mod(2).equals(0) }
      val evenAut = frbII.ffilterKey { k: Int -> k.mod(2).equals(0) }
      evenKey.equals(evenAut) shouldBe true
    }
  }

  test("ffilterKeyNot") {
    FBSTree.nul<Int, String>().ffilterKeyNot { true }.fempty() shouldBe true
    FBSTree.nul<Int, String>().ffilterKeyNot { false }.fempty() shouldBe true
    checkAll(repeatsMid.first, Arb.fbsItree(Arb.int(), 5..repeatsMid.second)) { frbII ->
      val sorted = frbII.inorder()
      val smallest = sorted.fhead()!!
      val large = frbII.ffilterKeyNot { k: Int -> k.equals(smallest.getk()) }
      large.fcontains (smallest) shouldBe false
      sorted.ftail().fhead()?.let { nextSmallest -> large.fcontains(nextSmallest) shouldBe true }
      large.fsize() shouldBe frbII.size - 1
      val (_: FBSTree<Int, Int>, oddKey: FBSTree<Int, Int>) = frbII.fpartition { tkv -> tkv.getk().mod(2).equals(0) }
      val oddAut = frbII.ffilterKeyNot { k: Int -> k.mod(2).equals(0) }
      oddKey.equals(oddAut) shouldBe true
    }
  }

  test("fpickKey") {
    iiTreeOfNone.fpickKey() shouldBe null
    iiTreeOfTwo.fpickKey() shouldBe (iiTreeOfTwo as FBSTree<Int,Int>).froot()!!.getk()
    ixxxTreeOfTwo.fpickKey() shouldBe ixxxTreeOfTwo.froot()!!.getk()
    checkAll(repeatsMid.first, Arb.frbStree(Arb.string(8,20), 5..repeatsMid.second)) { frbSS ->
      frbSS.fpickKey() shouldBe frbSS.froot()!!.getk()
    }
  }

  test("fisStrictlyLike, ftypeSample") {
    iiTreeOfNone.ftypeSample() shouldBe null
    iiTreeOfTwo.ftypeSample() shouldBe KeyedTypeSample(Int::class, Int::class)
    iiTreeOfTwo.fisStrictlyLike(iiTreeOfTwo.ftypeSample()!!) shouldBe true
    isTreeOfTwo.ftypeSample() shouldBe KeyedTypeSample(Int::class, String::class)
    isTreeOfTwo.fisStrictlyLike(isTreeOfTwo.ftypeSample()!!) shouldBe true
    ssTreeOfTwo.ftypeSample() shouldBe KeyedTypeSample(String::class, String::class)
    ssTreeOfTwo.fisStrictlyLike(ssTreeOfTwo.ftypeSample()!!) shouldBe true
    siTreeOfTwo.ftypeSample() shouldBe KeyedTypeSample(String::class, Int::class)
    siTreeOfTwo.fisStrictlyLike(siTreeOfTwo.ftypeSample()!!) shouldBe true
    siTreeOfTwo.fisStrictlyLike(ssTreeOfTwo.ftypeSample()!!) shouldBe false

    ixTreeOfTwo.fisStrictlyLike(ixTreeOfTwo.ftypeSample()!!) shouldBe false
    val ixxRecursiveInternalStricture = ixxTreeOfTwo.fisStrict()
    ixxTreeOfTwo.fisStrictlyLike(ixxTreeOfTwo.ftypeSample()!!) shouldBe !ixxRecursiveInternalStricture
    val ixxxRecursiveInternalStricture = ixxxTreeOfTwo.fisStrict()
    ixxxTreeOfTwo.fisStrictlyLike(ixxxTreeOfTwo.ftypeSample()!!) shouldBe !ixxxRecursiveInternalStricture
    izTreeOfTwo.fisStrictlyLike(izTreeOfTwo.ftypeSample()!!) shouldBe true
  }

  test("asIMBTree") {
    (iiTreeOfNone.asIMBTree() === iiTreeOfNone) shouldBe true
    (iiTreeOfTwo.asIMBTree() === iiTreeOfTwo) shouldBe true
  }

  test("asIMMap") {
    (iiTreeOfNone.asIMMap() === emptyIMMap<Int,Int>()) shouldBe true
    (iiTreeOfTwo.asIMMap()::class === FKMapNotEmpty::class) shouldBe true
    iiTreeOfTwo.asIMMap().fsize() shouldBe 2
    (iiTreeOfTwo as FBSTree<Int,Int>).fdropAll(iiTreeOfTwo.asIMMap()).fempty() shouldBe true
    val autOf2 = iiTreeOfTwo.asIMMap().toIMBTree()
    (autOf2 === iiTreeOfTwo) shouldBe false
    (autOf2.equal(iiTreeOfTwo)) shouldBe false
    (autOf2.softEqual(iiTreeOfTwo)) shouldBe true
  }

  test("fcontainsValue") {
    FBSTree.nul<Int, String>().fcontainsValue(zEntry.getv()) shouldBe false
    tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          (t.fcontainsValue(acc.head.getv())) shouldBe true
          go(t, acc.tail)
        }
      }
    go(wikiTree, wikiPreorder)
    wikiTree.fcontainsValue(zEntry.getv()) shouldBe false
    go(slideShareTree, frbSlideShareBreadthFirst)
    slideShareTree.fcontainsValue(100) shouldBe false
  }

  test("fcountValue") {
    FBSTree.nul<Int, String>().fcountValue { true } shouldBe 0
    checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
      val source = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val frbTree = FBSTree.of(source.iterator())
      val threshold = source.size / 3
      frbTree.fcountValue { it < threshold  } shouldBe threshold
    }
  }

  test("ffilterValue") {
    FBSTree.nul<Int, String>().ffilterValue { true }.fempty() shouldBe true
    FBSTree.nul<Int, String>().ffilterValue { false }.fempty() shouldBe true
    checkAll(repeatsMid.first, Arb.fbsItree(Arb.string(8,20), 5..repeatsMid.second)) { frbIS ->
      val smallest = frbIS.inorder().fhead()!!
      val small = frbIS.ffilterValue { v: String -> v.equals(smallest.getv()) }
      small.froot()?.equals(smallest) shouldBe true
      small.fsize() shouldBe 1
      val (evenKey: FBSTree<Int, String>, _) = frbIS.fpartition { tkv -> tkv.getk().mod(2).equals(0) }
      val filtered = frbIS.ffilterValue { v: String -> intKeyOf(v).mod(2).equals(0) }
      evenKey.equals(filtered) shouldBe true
    }
  }

  test("ffilterValueNot") {
    FBSTree.nul<Int, String>().ffilterValueNot { true }.fempty() shouldBe true
    FBSTree.nul<Int, String>().ffilterValueNot { false }.fempty() shouldBe true
    checkAll(repeatsMid.first, Arb.fbsItree(Arb.string(8,20), 5..repeatsMid.second)) { frbIS ->
      val sorted = frbIS.inorder()
      val smallest = sorted.fhead()!!
      val large = frbIS.ffilterValueNot { v: String -> v.equals(smallest.getv()) }
      large.fcontains (smallest) shouldBe false
      sorted.ftail().fhead()?.let { nextSmallest -> large.fcontains(nextSmallest) shouldBe true }
      large.fsize() shouldBe frbIS.size - 1
      val (_, oddKey: FBSTree<Int, String>) = frbIS.fpartition { tkv -> tkv.getk().mod(2).equals(0) }
      val oddAut = frbIS.ffilterValueNot { v: String -> intKeyOf(v).mod(2).equals(0) }
      oddKey.equals(oddAut) shouldBe true
    }
  }

  test("ffindAnyValue") {
    FBSTree.nul<Int, String>().ffindAnyValue { true } shouldBe null
    FBSTree.nul<Int, String>().ffindAnyValue { false } shouldBe null
    val maxValueLen = 20
    checkAll(repeatsMid.first, Arb.fbsItree(Arb.string(8, maxValueLen), 5..repeatsMid.second)) { frbIS ->
      val impossibleString = "wefvsopeivnsd;kfjvnerpiuvnsd;fkjvnwepiurvhnnd;kasf"
      impossibleString.length shouldBeGreaterThan maxValueLen
      val sorted = frbIS.inorder()
      val nextSmallest: TKVEntry<Int, String>? = sorted.ftail().fhead()
      val largest: TKVEntry<Int, String>? = sorted.flast()
      nextSmallest?.let { wanted -> frbIS.ffindAnyValue { it.equals(wanted.getv()) } shouldBe nextSmallest.getv() }
      largest?.let { wanted -> frbIS.ffindAnyValue { it.equals(wanted.getv()) } shouldBe largest.getv() }
      frbIS.ffindAnyValue { it.equals(impossibleString) } shouldBe null
    }
  }

  test("fget") {
    FBSTree.nul<Int, String>().fget(1) shouldBe null
    checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
      val source = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val frbTree = FBSTree.of(source.iterator())
      for (ix in 0 until n) { (frbTree.fget(ix) == ix) shouldBe true }
      frbTree.fget(-1) shouldBe null
      frbTree.fget(n) shouldBe null
    }
  }

  test("fgetOrElse") {
    FBSTree.nul<Int, String>().fget(1) shouldBe null
    checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
      val source = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val frbTree = FBSTree.of(source.iterator())
      for (ix in 0 until n) { (frbTree.fgetOrElse(ix) { -1 } == ix) shouldBe true }
      frbTree.fgetOrElse(-1) { -10 } shouldBe -10
      frbTree.fgetOrElse(n) { -1 } shouldBe -1
    }
  }

  test("fpickValue") {
    iiTreeOfNone.fpickValue() shouldBe null
    iiTreeOfTwo.fpickValue() shouldBe (iiTreeOfTwo as FBSTree<Int,Int>).froot()!!.getv()
    ixxxTreeOfTwo.fpickValue() shouldBe ixxxTreeOfTwo.froot()!!.getv()
    checkAll(repeatsMid.first, Arb.frbStree(Arb.string(8,20), 5..repeatsMid.second)) { frbSS ->
      frbSS.fpickValue() shouldBe frbSS.froot()!!.getv()
    }
  }

})
