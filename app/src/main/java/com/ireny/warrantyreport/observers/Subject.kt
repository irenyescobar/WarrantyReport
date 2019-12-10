package com.ireny.warrantyreport.observers

class Subject:ISubject {

    private val _observers: ArrayList<IObserver> = arrayListOf()

    override fun add(observer: IObserver) {
        _observers.add(observer)
    }

    override fun remove(observer: IObserver) {
        _observers.remove(observer)
    }

    override fun notify(result: IResult) {
        _observers.forEach {
            it.completed(result)
        }

    }
}