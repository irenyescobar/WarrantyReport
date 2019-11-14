package com.ireny.warrantyreport.repositories.listeners

interface GetSuccessListener<T> {
    fun onGetSuccess(entity:T)
}