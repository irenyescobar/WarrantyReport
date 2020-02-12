package com.ireny.warrantyreport.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ireny.warrantyreport.data.room.dao.CompanyDao
import com.ireny.warrantyreport.entities.Company

class CompanyRepository(private val dao: CompanyDao) {

    interface ErrorListener{
        fun onCompanyError(error: Exception)
    }

    private var listener:ErrorListener? = null

    fun setListener(errorListener:ErrorListener){
        listener = errorListener
    }

    fun getAll():LiveData<List<Company>>{
        return try {
            dao.getAll()
        }catch (ex:Exception){
            listener?.onCompanyError(ex)
            return MutableLiveData<List<Company>>()
        }
    }

}