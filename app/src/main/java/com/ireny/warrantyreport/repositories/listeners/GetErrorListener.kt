package com.ireny.warrantyreport.repositories.listeners

interface GetErrorListener {
    fun onGetError(id:Long, error:Exception)
}