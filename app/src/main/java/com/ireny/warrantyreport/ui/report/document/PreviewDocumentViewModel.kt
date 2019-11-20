package com.ireny.warrantyreport.ui.report.document

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.ui.report.base.ReportViewModelBase
import kotlinx.coroutines.launch

class PreviewDocumentViewModel(application: Application,
                               repository: ReportRepository,
                               private val reportId:Long) :ReportViewModelBase(application,repository)  {
    fun loadModel(){
        loadingVisibility.postValue(true)
        message.postValue("")
        errors = mutableListOf()
        viewModelScope.launch {
            model.postValue(repository.getById(reportId))

            if(errors.count() > 0) {
                message.postValue("Foram registrados ${errors.count()} erros durante a execução da operação.")
            }

            loadingVisibility.postValue(false)
        }
    }

    companion object{

        class Factory(private val application: Application,
                      private val repository: ReportRepository,
                      private val reportId:Long
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PreviewDocumentViewModel(application, repository,reportId) as T
            }
        }
    }
}