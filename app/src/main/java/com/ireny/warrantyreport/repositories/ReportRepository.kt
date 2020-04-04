package com.ireny.warrantyreport.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ireny.warrantyreport.data.room.dao.ReportDao
import com.ireny.warrantyreport.entities.AssignedTechnicalAdvice
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.entities.Report01
import com.ireny.warrantyreport.repositories.listeners.DeleteErrorListener
import com.ireny.warrantyreport.repositories.listeners.GetErrorListener
import com.ireny.warrantyreport.repositories.listeners.LoadDataErrorListener
import com.ireny.warrantyreport.repositories.listeners.SaveErrorListener
import java.util.*

class ReportRepository(private val dao: ReportDao) {

    private var loadListener: LoadDataErrorListener? = null

    fun setLoadDataListener(listener: LoadDataErrorListener){
        loadListener = listener
    }

    private var getErrorListener: GetErrorListener? = null

    fun setGetErrorListener(listener: GetErrorListener){
        getErrorListener = listener
    }

    private var saveListener: SaveErrorListener<Report>? = null

    fun setSaveListener(listener: SaveErrorListener<Report>){
        saveListener = listener
    }

    private var deleteErrorListener: DeleteErrorListener? = null

    fun setDeleteErrorListener(listener: DeleteErrorListener){
        deleteErrorListener = listener
    }

    private var saveAssignedTechnicalAdviceListener: SaveErrorListener<AssignedTechnicalAdvice>? = null

    fun setSaveAssignedTechnicalAdviceListener(listener: SaveErrorListener<AssignedTechnicalAdvice>){
        saveAssignedTechnicalAdviceListener = listener
    }

    suspend fun save(entity: Report){
        try {
            entity.companyId = null
            entity.company?.let {
                entity.companyId = it.id
            }
            dao.save(entity)
        }catch (ex:Exception){
            saveListener?.onSaveError(entity,ex)
        }
    }

    private suspend fun get(id:Long): Report?{
        val entity = dao.getReportById(id)
        entity.also { rp ->
            load(rp)
        }
        return entity
    }

    private suspend fun load(rp:Report){
         dao.getAssignedTechnicalAdvices(rp.id).forEach {
            dao.getTechnicalAdvice(it.technicalAdviceId).let { tec ->
                tec?.let {
                    rp.tecnicalAdvices.add(it)
                }
            }
        }
        rp.companyId?.let {
            rp.company = dao.getCompany(it)
        }
        rp.reportTypeId?.let {
            rp.reportType = dao.getReportType(it)
        }
    }

    fun getPendings():LiveData<List<Report01>>{
        return try {
            dao.getPendingReports()
        }catch (ex:Exception){
            loadListener?.onLoadDataError(ex)
            return MutableLiveData<List<Report01>>()
        }
    }

    fun getCompleteds():LiveData<List<Report01>>{
        return try {
            dao.getCompletedReports()
        }catch (ex:Exception){
            loadListener?.onLoadDataError(ex)
            return MutableLiveData<List<Report01>>()
        }
    }

    suspend fun getById(reportId:Long):Report?{
        return try {
             get(reportId)
        }catch (ex:Exception){
            getErrorListener?.onGetError(reportId, ex)
            return null
        }
    }

    fun getTechnicalAdvices(reportId:Long):LiveData<List<AssignedTechnicalAdvice>>{
        return try {
            dao.getTechnicalAdvices(reportId)
        }catch (ex:Exception){
            getErrorListener?.onGetError(reportId,ex)
            return MutableLiveData<List<AssignedTechnicalAdvice>>()
        }
    }

    suspend fun assign(tecnicalAdviceId:Int, reportId:Long){
        val el = AssignedTechnicalAdvice(reportId,tecnicalAdviceId)
        try {
            dao.add(el)
        }catch (ex:Exception){
            saveAssignedTechnicalAdviceListener?.onSaveError(el,ex)
        }
    }

    suspend fun unassign(tecnicalAdviceId:Int, reportId:Long){
        val el = AssignedTechnicalAdvice(reportId,tecnicalAdviceId)
        try {
            dao.deleteAssigned(el.technicalAdviceId,el.reportId)
        }catch (ex:Exception){
            saveAssignedTechnicalAdviceListener?.onSaveError(el,ex)
        }
    }

    suspend fun saveCode(code:String,report: Report){
        try {
            val generatedAt = Calendar.getInstance().time
            dao.saveCode(code,generatedAt,report.id)

        }catch (ex:Exception){
            saveListener?.onSaveError(report,ex)
        }
    }

    suspend fun remove(reportId: Long) {
        try {
            dao.delete(reportId)
        }catch (ex:Exception){
            deleteErrorListener?.onDeleteError(ex)
        }
    }
}