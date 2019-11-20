package com.ireny.warrantyreport.ui.report.interfaces

interface IBuildModel<T> {
    fun buildModel(model: T):T
}