package com.ireny.randon.frasle.warrantyreport.repositories.listeners

interface GetSuccessListener<T> {
    fun onGetSuccess(entity:T)
}