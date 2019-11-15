package com.ireny.warrantyreport.ui.report.technicalconsultant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.utils.reportActivity
import com.ireny.warrantyreport.utils.setOnClickDatePicker
import com.ireny.warrantyreport.utils.toDate
import com.ireny.warrantyreport.utils.toDateTextFormatted
import java.util.*

class TechnicalConsultantFragment : FragmentUpdateBase() {

    private lateinit var textTechnicalConsultant: TextView
    private lateinit var textTechnicalConsultantContact: TextView
    private lateinit var textAnalysisDate: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        reportActivity.supportActionBar?.apply {
            title = "Consultor técnico"
        }

        val view = inflater.inflate(R.layout.report_technical_consultant_fragment, container, false)

        textTechnicalConsultant = view.findViewById(R.id.text_technicalConsultant)
        textTechnicalConsultantContact = view.findViewById(R.id.text_technicalConsultantContact)
        textAnalysisDate = view.findViewById(R.id.text_analysisDate)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        textAnalysisDate.setOnClickDatePicker(requireContext(),year,month,day)

        return view
    }
    override fun refresh(entity: Report) {
        textTechnicalConsultant.text = entity.technicalConsultant
        textTechnicalConsultantContact.text = entity.technicalConsultantContact
        textAnalysisDate.setText( if(entity.analysisDate != null) entity.analysisDate?.toDateTextFormatted() else "")
    }

    override fun updateReport(entity: Report) {
        entity.technicalConsultant = textTechnicalConsultant.text.toString()
        entity.technicalConsultantContact =  textTechnicalConsultantContact.text.toString()
        entity.analysisDate = textAnalysisDate.text.toString().toDate()
    }

    companion object {
        fun newInstance() =
            TechnicalConsultantFragment()
    }
}
