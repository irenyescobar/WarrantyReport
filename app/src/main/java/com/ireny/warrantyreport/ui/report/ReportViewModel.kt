package com.ireny.warrantyreport.ui.report

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ireny.warrantyreport.entities.AssignedTechnicalAdvice
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.listeners.SaveErrorListener
import com.ireny.warrantyreport.ui.report.base.ReportViewModelBase
import kotlinx.coroutines.launch

class ReportViewModel(application: Application,
                      repository: ReportRepository) : ReportViewModelBase(application,repository)  {

    private val saveAssignedTechnicalAdviceErrorListener = object :
        SaveErrorListener<AssignedTechnicalAdvice> {
        override fun onSaveError(entity: AssignedTechnicalAdvice, error: Exception) {
            errors.add(error)
        }
    }

    init {
        repository.setSaveAssignedTechnicalAdviceListener(saveAssignedTechnicalAdviceErrorListener)
    }

    fun loadModel(reportId: Long){
        loadingVisibility.postValue(true)
        errors = mutableListOf()
        viewModelScope.launch {
            model.postValue(repository.getById(reportId))

            if(errors.count() > 0) {
                message.postValue("Foram registrados ${errors.count()} erros durante a execução da operação.")
            }

            loadingVisibility.postValue(false)
        }
    }

    fun save(item: ReportTechnicalAdvice){
        model.value?.let {
            loadingVisibility.postValue(true)
            errors = mutableListOf()
            viewModelScope.launch {
                if (item.selectioned) {
                    repository.assign(item.id, it.id)
                } else {
                    repository.unassign(item.id, it.id)
                }

                if(errors.count() > 0) {
                    message.postValue("Foram registrados ${errors.count()} erros durante a execução da operação.")
                }
                loadModel(it.id)
            }
        }
    }


    fun save(model:Report){
        loadingVisibility.postValue(true)
        message.postValue("")
        errors = mutableListOf()
        viewModelScope.launch {
            repository.save(model)
            loadModel(model.id)
        }
    }

    companion object{

        class Factory(private val application: Application,
                      private val repository: ReportRepository
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ReportViewModel(application, repository) as T
            }
        }
    }
}