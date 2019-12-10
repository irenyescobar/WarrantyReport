package com.ireny.warrantyreport.observers

interface ISubject {
    fun add(observer:IObserver)
    fun remove(observer:IObserver)
    fun notify(result:IResult)
}