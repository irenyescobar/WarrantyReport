package com.ireny.warrantyreport.ui.report.technicalconsultant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.utils.copy
import com.ireny.warrantyreport.utils.setOnClickDatePicker
import com.ireny.warrantyreport.utils.toDate
import com.ireny.warrantyreport.utils.toDateTextFormatted
import kotlinx.android.synthetic.main.report_technical_consultant_fragment.*
import java.util.*

class TechnicalConsultantFragment : FragmentUpdateBase() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.report_technical_consultant_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        textAnalysisDate.setOnClickDatePicker(requireContext(),year,month,day)
    }

    override fun buildModel(model: Report):Report {
        val copy = model.copy()
        copy.technicalConsultant = textTechnicalConsultant.text.toString()
        copy.technicalConsultantContact =  textTechnicalConsultantContact.text.toString()
        copy.analysisDate = textAnalysisDate.text.toString().toDate()
        return copy
    }

    override fun bindView(model: Report) {
        textTechnicalConsultant.setText(model.technicalConsultant)
        textTechnicalConsultantContact.setText(model.technicalConsultantContact)
        textAnalysisDate.setText( if(model.analysisDate != null) model.analysisDate?.toDateTextFormatted() else "")
    }

    companion object {
        fun newInstance() =
            TechnicalConsultantFragment()
    }
}
