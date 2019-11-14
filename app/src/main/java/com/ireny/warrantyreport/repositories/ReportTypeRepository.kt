package com.ireny.warrantyreport.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ireny.warrantyreport.data.room.dao.ReportTypeDao
import com.ireny.warrantyreport.entities.ReportType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportTypeRepository(private val dao: ReportTypeDao) {

    interface Listener{
        fun onLoadReportTypesError(error: Exception)
        fun onSaveReportTypeError(entity:ReportType,error: Exception)
    }

    private var listener:Listener? = null

    fun setListener(errorListener:Listener){
        listener = errorListener
    }

    suspend fun save(entity:ReportType) {
        try {
            dao.insert(entity)
        }catch (ex:Exception){
            withContext(Dispatchers.Main){
                listener?.onSaveReportTypeError(entity,ex)
            }
        }
    }

    fun getAll():LiveData<List<ReportType>>{
        return try {
            dao.getAll()
        }catch (ex:Exception){
            listener?.onLoadReportTypesError(ex)
            return MutableLiveData<List<ReportType>>()
        }
    }
}