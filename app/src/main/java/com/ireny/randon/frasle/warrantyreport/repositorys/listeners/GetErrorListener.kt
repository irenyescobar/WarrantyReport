package com.ireny.randon.frasle.warrantyreport.repositorys.listeners

interface GetErrorListener {
    fun onGetError(id:Long, error:Exception)
}