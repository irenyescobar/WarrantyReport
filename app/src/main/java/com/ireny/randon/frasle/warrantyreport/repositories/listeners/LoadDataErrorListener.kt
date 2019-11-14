package com.ireny.randon.frasle.warrantyreport.repositories.listeners

interface LoadDataErrorListener {
    fun onLoadDataError(error: Exception)
}