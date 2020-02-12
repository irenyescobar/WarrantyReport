package com.ireny.warrantyreport.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ireny.warrantyreport.data.room.dao.ReportTypeDao
import com.ireny.warrantyreport.entities.ReportType

class ReportTypeRepository(private val dao: ReportTypeDao) {

    interface Listener{
        fun onLoadReportTypesError(error: Exception)
    }

    private var listener:Listener? = null

    fun setListener(errorListener:Listener){
        listener = errorListener
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