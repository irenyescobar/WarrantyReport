package com.ireny.warrantyreport.ui.report

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ireny.warrantyreport.entities.AssignedTechnicalAdvice
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.listeners.GetErrorListener
import com.ireny.warrantyreport.repositories.listeners.GetSuccessListener
import com.ireny.warrantyreport.repositories.listeners.SaveErrorListener
import com.ireny.warrantyreport.ui.listeners.CompletedOperationListener
import kotlinx.coroutines.launch

class ReportViewModel(application: Application,
                      private val repository: ReportRepository,
                      val listener: CompletedOperationListener<Report>?,
                      private val getSucessListener: GetSuccessListener<Report>?) : AndroidViewModel(application) {

    private val saveErrorListener = object :
        SaveErrorListener<Report> {
        override fun onSaveError(entity: Report, error: Exception) {
            errors.add(error)
        }
    }

    private val saveAssignedTechnicalAdviceErrorListener = object :
        SaveErrorListener<AssignedTechnicalAdvice> {
        override fun onSaveError(entity: AssignedTechnicalAdvice, error: Exception) {
            errors.add(error)
        }
    }

    private val getErrorListener = object :
        GetErrorListener {
        override fun onGetError(id: Long, error: Exception) {
            errors.add(error)
        }
    }

    var report: Report = Report(reportTypeId = 1)

    var errors: MutableList<java.lang.Exception> = mutableListOf()

    init {
        repository.setGetErrorListener(getErrorListener)
        repository.setSaveListener(saveErrorListener)
        repository.setSaveAssignedTechnicalAdviceListener(saveAssignedTechnicalAdviceErrorListener)
    }

    fun refresh(){
        load(report.id)
    }

    fun load(id: Long){
        viewModelScope.launch {
            get(id)
        }
    }

    private suspend fun get(id: Long){
        val el = repository.getById(id)
        el?.let {
            report = it
            getSucessListener?.onGetSuccess(report)
        }
    }

    fun save(item: ReportTechnicalAdvice){
        errors = mutableListOf()
        viewModelScope.launch {
            if (item.selectioned) {
                repository.assign(item.id, report.id)
            } else {
                repository.unassign(item.id, report.id)
            }
            complete(report)
        }
    }

    fun save(){
        errors = mutableListOf()
        viewModelScope.launch {
            repository.save(report)
            complete(report)
            get(report.id)
        }
    }

    private fun complete(entity: Report){
        if(errors.count() > 0) {
            listener?.onCompletedOperation(entity, false)
        }else{
            listener?.onCompletedOperation(entity,true)
        }
    }

    companion object{

        class Factory(private val application: Application,
                      private val repository: ReportRepository,
                      private val listener: CompletedOperationListener<Report>?,
                      private val getSucessListener: GetSuccessListener<Report>?
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ReportViewModel(application, repository,listener,getSucessListener) as T
            }
        }
    }
}