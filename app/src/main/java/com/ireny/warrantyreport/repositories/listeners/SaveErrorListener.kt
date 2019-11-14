package com.ireny.warrantyreport.repositories.listeners

interface SaveErrorListener<T> {
    fun onSaveError(entity:T, error:Exception)
}