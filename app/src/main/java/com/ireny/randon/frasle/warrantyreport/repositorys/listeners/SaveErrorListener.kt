package com.ireny.randon.frasle.warrantyreport.repositorys.listeners

interface SaveErrorListener<T> {
    fun onSaveError(entity:T, error:Exception)
}