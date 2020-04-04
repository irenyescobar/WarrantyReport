package com.ireny.warrantyreport.ui.reportscompleted

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ireny.warrantyreport.entities.Report01
import com.ireny.warrantyreport.repositories.ReportRepository

class ReportsCompletedViewModel(application: Application, repository: ReportRepository) : AndroidViewModel(application) {

    var all: LiveData<List<Report01>> = repository.getCompleteds()

    companion object{

        class Factory(private val application: Application,
                      private val repository: ReportRepository
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ReportsCompletedViewModel(application, repository) as T
            }
        }
    }
}