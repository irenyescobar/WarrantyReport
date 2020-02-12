package com.ireny.warrantyreport.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ireny.warrantyreport.data.room.dao.TechnicalAdviceDao
import com.ireny.warrantyreport.entities.TechnicalAdvice

class TechnicalAdviceRepository(private val dao: TechnicalAdviceDao) {

    interface Listener{
        fun onLoadTechnicalAdvicesError(error: Exception)
    }

    private var listener:Listener? = null

    fun setListener(errorListener:Listener){
        listener = errorListener
    }

    fun getAll():LiveData<List<TechnicalAdvice>>{
        return try {
            dao.getAll()
        }catch (ex:Exception){
            listener?.onLoadTechnicalAdvicesError(ex)
            return MutableLiveData<List<TechnicalAdvice>>()
        }
    }
}