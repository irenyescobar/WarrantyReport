package com.ireny.randon.frasle.warrantyreport.ui.listeners

interface CompletedOperationListener<T>{
    fun onCompletedOperation(entity:T, success:Boolean)
}