package com.ireny.randon.frasle.warrantyreport.repositorys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ireny.randon.frasle.warrantyreport.data.room.dao.TechnicalAdviceDao
import com.ireny.randon.frasle.warrantyreport.entities.TechnicalAdvice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TechnicalAdviceRepository(private val dao: TechnicalAdviceDao) {

    interface Listener{
        fun onLoadTechnicalAdvicesError(error: Exception)
        fun onSaveTechnicalAdviceError(entity: TechnicalAdvice, error: Exception)
    }

    private var listener:Listener? = null

    fun setListener(errorListener:Listener){
        listener = errorListener
    }

    suspend fun save(entity:TechnicalAdvice) {
        try {
            dao.insert(entity)
        }catch (ex:Exception){
            withContext(Dispatchers.Main){
                listener?.onSaveTechnicalAdviceError(entity,ex)
            }
        }
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