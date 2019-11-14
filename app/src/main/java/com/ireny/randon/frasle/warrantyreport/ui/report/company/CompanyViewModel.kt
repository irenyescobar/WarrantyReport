package com.ireny.randon.frasle.warrantyreport.ui.report.company

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ireny.randon.frasle.warrantyreport.entites.Company
import com.ireny.randon.frasle.warrantyreport.repositorys.CompanyRepository

class CompanyViewModel(application: Application,
                       repository: CompanyRepository) : AndroidViewModel(application){

    val all: LiveData<List<Company>> =  repository.getAll()

    companion object{

        class Factory(private val application: Application,
                      private val repository: CompanyRepository)
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return CompanyViewModel(application, repository) as T
            }
        }
    }
}
