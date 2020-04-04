package com.ireny.warrantyreport.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.ireny.warrantyreport.entities.Report01
import com.ireny.warrantyreport.repositories.ReportRepository
import kotlinx.coroutines.launch

class HomeViewModel (application: Application,
                     val repository: ReportRepository): AndroidViewModel(application) {

    var all: LiveData<List<Report01>> = repository.getPendings()

    fun remove(reportId:Long){

        viewModelScope.launch {
            repository.remove(reportId)
        }
    }

    companion object{

        class Factory(private val application: Application,
                      private val repository: ReportRepository
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return HomeViewModel(application, repository) as T
            }
        }
    }
}