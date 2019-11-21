package com.ireny.warrantyreport.ui.report.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.listeners.GetErrorListener
import com.ireny.warrantyreport.repositories.listeners.SaveErrorListener

abstract class ReportViewModelBase(
    application: Application,
    internal val repository: ReportRepository) : AndroidViewModel(application) {

    val model = MutableLiveData<Report>().apply { value = Report(reportTypeId = 1) }
    val loadingVisibility = MutableLiveData<Boolean>().apply { value = false }
    val message = MutableLiveData<String>().apply { value = "" }
    var errors: MutableList<java.lang.Exception> = mutableListOf()

    private val getErrorListener = object : GetErrorListener {
        override fun onGetError(id: Long, error: Exception) {
            errors.add(error)
        }
    }

    private val saveErrorListener = object :
        SaveErrorListener<Report> {
        override fun onSaveError(entity: Report, error: Exception) {
            errors.add(error)
        }
    }

    init {
        repository.setGetErrorListener(getErrorListener)
        repository.setSaveListener(saveErrorListener)
    }
}