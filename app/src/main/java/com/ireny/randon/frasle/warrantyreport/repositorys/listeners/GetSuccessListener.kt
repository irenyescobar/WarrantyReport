package com.ireny.randon.frasle.warrantyreport.repositorys.listeners

interface GetSuccessListener<T> {
    fun onGetSuccess(entity:T)
}