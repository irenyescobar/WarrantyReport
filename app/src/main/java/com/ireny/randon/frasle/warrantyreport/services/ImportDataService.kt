package com.ireny.randon.frasle.warrantyreport.services

import com.ireny.randon.frasle.warrantyreport.data.room.WarrantyReportRoomDatabase
import com.ireny.randon.frasle.warrantyreport.entites.Company
import com.ireny.randon.frasle.warrantyreport.entites.ReportType
import com.ireny.randon.frasle.warrantyreport.entites.TechnicalAdvice
import com.ireny.randon.frasle.warrantyreport.entites.WarrantReportData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


interface ImportDataServiceInterface {
    fun import(data: WarrantReportData, listener:ImportDataCompletedListener?)
}

interface ImportDataCompletedListener {
    fun onImportDataCompleted(errors:List<LogError>)
}

class LogError(val entity:Any, val error:String?)

class ImportDataService(private val database: WarrantyReportRoomDatabase):ImportDataServiceInterface, CoroutineScope {

    private var coroutineJob: Job = Job()

    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + coroutineJob

    private var errors: MutableList<LogError> = mutableListOf()

    override fun import(data: WarrantReportData, listener:ImportDataCompletedListener?) {
        launch {
            importReportTypes(data.reportTypes)
            importCompanys(data.companys)
            importTechnicalAdvices(data.technicalAdvices)

            listener?.also {
                withContext(Dispatchers.Main){
                    it.onImportDataCompleted(errors.toList())
                }
            }
        }
    }

    private suspend fun importReportTypes(reportTypes: ArrayList<ReportType>){
        reportTypes.forEach {
            try {
                database.reportTypeDao().insert(it)
            }catch (ex:Exception){
                errors.add(LogError(it,ex.localizedMessage))
            }
        }
    }

    private suspend fun importCompanys(companys: ArrayList<Company>){
        companys.forEach {
            try {
                database.companyDao().insert(it)
            }catch (ex:Exception){
                errors.add(LogError(it,ex.localizedMessage))
            }
        }
    }

    private suspend fun importTechnicalAdvices(technicalAdvices: ArrayList<TechnicalAdvice>){
        technicalAdvices.forEach {
            try {
                database.technicalAdviceDao().insert(it)
            }catch (ex:Exception){
                errors.add(LogError(it,ex.localizedMessage))
            }
        }
    }
}