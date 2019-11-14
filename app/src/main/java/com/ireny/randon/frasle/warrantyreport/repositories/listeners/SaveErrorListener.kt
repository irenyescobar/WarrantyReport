package com.ireny.randon.frasle.warrantyreport.repositories.listeners

interface SaveErrorListener<T> {
    fun onSaveError(entity:T, error:Exception)
}