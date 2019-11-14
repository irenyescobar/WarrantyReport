package com.ireny.randon.frasle.warrantyreport.repositories.listeners

interface GetErrorListener {
    fun onGetError(id:Long, error:Exception)
}