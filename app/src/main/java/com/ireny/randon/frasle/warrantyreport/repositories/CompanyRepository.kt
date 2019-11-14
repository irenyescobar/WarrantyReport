package com.ireny.randon.frasle.warrantyreport.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ireny.randon.frasle.warrantyreport.data.room.dao.CompanyDao
import com.ireny.randon.frasle.warrantyreport.entities.Company
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CompanyRepository(private val dao: CompanyDao) {

    interface ErrorListener{
        fun onCompanyError(error: Exception)
    }

    private var listener:ErrorListener? = null

    fun setListener(errorListener:ErrorListener){
        listener = errorListener
    }

    suspend fun save(entity:Company) {
        try {
            dao.insert(entity)
        }catch (ex:Exception){
            withContext(Dispatchers.Main){
                listener?.onCompanyError(ex)
            }
        }
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