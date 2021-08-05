package com.xrpn.imapi

interface IMAdditive{}
interface IMMuliplicative{}

interface IMCumulating<out A> where A: Comparable<@UnsafeVariance A>, A: IMAdditive, A: IMMuliplicative {
    fun max(): A // 	The largest element in the list
    fun min(): A // 	The smallest element in the list
    fun product(): A // 	The result of multiplying the elements in the collection
    fun sum(): A // 	The sum of the elements in the list
}