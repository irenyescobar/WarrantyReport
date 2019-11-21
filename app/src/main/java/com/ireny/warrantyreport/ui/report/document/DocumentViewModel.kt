package com.ireny.warrantyreport.ui.report.document

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.services.LocalCodeGenerator
import com.ireny.warrantyreport.ui.report.base.ReportViewModelBase
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application,
                        repository: ReportRepository,
                        private val reportId:Long) :ReportViewModelBase(application,repository)  {

    private val localCodeGenerator = LocalCodeGenerator()

    fun loadModel(){
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

    fun saveCodeReport(){
        loadingVisibility.postValue(true)
        errors = mutableListOf()
        viewModelScope.launch {
            model.value?.let {

                if(it.code == null) {

                    val code = localCodeGenerator.generateNewCode()
                    repository.saveCode(code, it)

                    if (errors.count() > 0) {
                        message.postValue("Foram registrados ${errors.count()} erros durante a execução da operação.")
                    }

                    loadModel()
                }else{
                    loadingVisibility.postValue(false)
                    message.postValue("Este documento já foi salvo.")
                }
            }
        }
    }

    companion object{

        class Factory(private val application: Application,
                      private val repository: ReportRepository,
                      private val reportId:Long
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DocumentViewModel(application, repository,reportId) as T
            }
        }
    }
}