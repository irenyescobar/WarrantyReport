package com.ireny.warrantyreport.repositories.listeners

interface DeleteErrorListener {
    fun onDeleteError(error:Exception)
}