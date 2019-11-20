package com.ireny.warrantyreport.ui.report.interfaces

interface ICreateDocument<T> {
    fun createDocument(model:T)
}