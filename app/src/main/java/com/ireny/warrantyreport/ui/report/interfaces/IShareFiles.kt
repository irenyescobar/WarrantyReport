package com.ireny.warrantyreport.ui.report.interfaces

import java.io.File

interface IShareFiles<T> {
    fun files(model:T):ArrayList<File>
}