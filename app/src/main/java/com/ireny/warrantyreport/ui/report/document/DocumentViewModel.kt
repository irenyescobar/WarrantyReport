package com.ireny.warrantyreport.ui.report.document

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ireny.warrantyreport.data.retrofit.Api
import com.ireny.warrantyreport.observers.IObserver
import com.ireny.warrantyreport.observers.IResult
import com.ireny.warrantyreport.observers.Subject
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.services.CodeGenerator
import com.ireny.warrantyreport.services.interfaces.IUserAccountManager
import com.ireny.warrantyreport.ui.report.base.ReportViewModelBase
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application,
                        repository: ReportRepository,
                        api: Api,
                        accountManager: IUserAccountManager,
                        private val reportId:Long,
                        private val keyApp: String) :ReportViewModelBase(application,repository)  {

    private val subject = Subject()

    private val codeGenerator = CodeGenerator(api,accountManager)

    private val observer = object : IObserver{
        override fun completed(result: IResult) {
            subject.remove(this)
            if(result.success){

                val code = result.data.toString()

                viewModelScope.launch {

                   model.value?.let {
                       repository.saveCode(code, it)

                       if (errors.count() > 0) {
                           message.postValue("Foram registrados ${errors.count()} erros durante a execução da operação.")
                       }else{
                           message.postValue("Operação realizada com sucesso.")
                       }

                       loadModel()
                   }

                }

            }else{
                loadingVisibility.postValue(false)
                message.postValue(result.message)
            }

        }
    }


    init {
        codeGenerator.addSubject(subject)
    }

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
                    subject.add(observer)
                    codeGenerator.generateNewCode(keyApp)
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
                      private val reportId:Long,
                      private val api: Api,
                      private val accountManager: IUserAccountManager,
                      private val keyApp: String
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DocumentViewModel(application, repository,api, accountManager,reportId, keyApp) as T
            }
        }
    }
}