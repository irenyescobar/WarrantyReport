package com.ireny.warrantyreport.repositories.listeners

interface LoadDataErrorListener {
    fun onLoadDataError(error: Exception)
}