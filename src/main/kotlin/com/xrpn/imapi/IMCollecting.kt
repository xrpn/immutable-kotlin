package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListCollecting<out A: Any> {
//    fun holds(element: @UnsafeVariance A): Boolean // 	True if the list contains the element e
//    fun superListOf(list: IMList<@UnsafeVariance A>): Boolean // 	True if the list contains the sequence s
//    fun count(isMatch: (A) -> Boolean): Int // 	The number of elements in the list for which the predicate is true
//    fun endsWith(list: IMList<@UnsafeVariance A>): Boolean // True if the list ends with the sequence s
//    fun exists(isMatch: (A) -> Boolean): Boolean // 	True if the predicate returns true for at least one element in the list
//    fun find(isMatch: (A) -> Boolean): Boolean // 	The first element that matches the predicate
//    fun forall(isMatch: (A) -> Boolean): Boolean // 	True if the predicate p is true for all elements in the list
//    fun hasDefiniteSize() : Boolean // 	True if the list has a finite size
//    fun indexOf(element: @UnsafeVariance A): Int // 	The index of the first occurrence of the element e in the list
//    fun indexOf(element: @UnsafeVariance A, offset: Int) // The index of the first occurrence of the element e in the list, searching only from the value of the offset index
//    fun indexOfSlice(list: IMList<@UnsafeVariance A>): Int  //	The index of the first occurrence of the sequence s in the list
//    fun indexOfSlice(list: IMList<@UnsafeVariance A>, offset: Int): Int // 	The index of the first occurrence of the sequence s in the list, searching only from the value of the start index i
//    fun indexWhere(isMatch: (A) -> Boolean): Int // 	The index of the first element where the predicate p returns true
//    fun indexWhere(isMatch: (A) -> Boolean, offset: Int): Int // 	The index of the first element where the predicate p returns true, searching only from the value of the start index i
//    fun isDefinedAt(index: Int): Boolean // 	True if the list contains the index i
//    fun isEmpty(): Boolean // 	True if the list contains no elements
//    fun lastIndexOf(element: @UnsafeVariance A): Int // 	The index of the last occurrence of the element e in the list
//    fun lastIndexOf(element: @UnsafeVariance A, offset: Int): Int // 	The index of the last occurrence of the element e in the list, occurring before or at the index i
//    fun lastIndexOfSlice(list: IMList<@UnsafeVariance A>): Int // 	The index of the last occurrence of the sequence s in the list
//    fun lastIndexOfSlice(list: IMList<@UnsafeVariance A>, offset: Int): Int // 	The index of the last occurrence of the sequence s in the list, occurring before or at the index i
//    fun lastIndexWhere(isMatch: (A) -> Boolean): Int // 	The index of the first element where the predicate p returns true
//    fun lastIndexWhere(isMatch: (A) -> Boolean, offset: Int): Int // 	The index of the first element where the predicate p returns true, occurring before or at the index i
//    fun nonEmpty(): Boolean // 	True if the list is not empty (i.e., if it contains 1 or more elements)
//    fun segmentLength(isMatch: (A) -> Boolean, offset: Int): Int // 	The length of the longest segment for which the predicate p is true, starting at the index i
//    fun len(): Int // 	The number of elements in the list
//    fun startsWith(list: IMList<@UnsafeVariance A>): Boolean // 	True if the list begins with the elements in the sequence s
//    fun startsWith(list: IMList<@UnsafeVariance A>, offset: Int): Boolean // 	True if the list has the sequence s starting at the index i
}