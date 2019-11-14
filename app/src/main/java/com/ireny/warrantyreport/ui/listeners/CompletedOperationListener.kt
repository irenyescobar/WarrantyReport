package com.ireny.warrantyreport.ui.listeners

interface CompletedOperationListener<T>{
    fun onCompletedOperation(entity:T, success:Boolean)
}