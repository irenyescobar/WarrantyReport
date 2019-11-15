package com.ireny.warrantyreport.ui.report.technicaladvice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ireny.warrantyreport.entities.AssignedTechnicalAdvice
import com.ireny.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.warrantyreport.entities.TechnicalAdvice
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.TechnicalAdviceRepository

class TechnicalAdviceViewModel(application: Application,
                               technicalAdviceRepository: TechnicalAdviceRepository,
                               repository: ReportRepository,
                               val reportId: Long): AndroidViewModel(application){


    val technicalAdvices: LiveData<List<TechnicalAdvice>> = technicalAdviceRepository.getAll()
    val assignedTechnicalAdvices: LiveData<List<AssignedTechnicalAdvice>> = repository.getTechnicalAdvices(reportId)

    fun getData():List<ReportTechnicalAdvice>{
        val list :MutableList<ReportTechnicalAdvice> = mutableListOf()

        technicalAdvices.value?.forEach {
            val element = ReportTechnicalAdvice(it.id,it.description,isSelectioned(it.id))
            list.add(element)
        }

        return list
    }

    private fun isSelectioned(technicalAdviceId:Int):Boolean{
        assignedTechnicalAdvices.value?.run {
            return  find{ it.technicalAdviceId == technicalAdviceId } != null
        }
        return false
    }

    companion object{

        class Factory(private val application: Application,
                      private val technicalAdviceRepository: TechnicalAdviceRepository,
                      private val repository: ReportRepository,
                      private val reportId: Long)
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TechnicalAdviceViewModel(application,technicalAdviceRepository, repository,reportId) as T
            }
        }
    }
}