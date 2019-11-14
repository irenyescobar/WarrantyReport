package com.ireny.randon.frasle.warrantyreport.ui.report.part02

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.ireny.randon.frasle.warrantyreport.R
import com.ireny.randon.frasle.warrantyreport.entites.Report
import com.ireny.randon.frasle.warrantyreport.ui.report.base.FragmentBase
import com.ireny.randon.frasle.warrantyreport.utils.reportActivity
import com.ireny.randon.frasle.warrantyreport.utils.setOnClickDatePicker
import com.ireny.randon.frasle.warrantyreport.utils.toDate
import com.ireny.randon.frasle.warrantyreport.utils.toDateTextFormatted
import java.util.*

class ReportPart02Fragment : FragmentBase() {

    private lateinit var textSourceinvoice: EditText
    private lateinit var textInvoicedate: EditText
    private lateinit var textApplicationdate: EditText
    private lateinit var textWarrantydate: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        reportActivity.supportActionBar?.apply {
            title = "Laudo parte 02"
        }

        val view = inflater.inflate(R.layout.report_part02_fragment, container, false)

        textSourceinvoice = view.findViewById(R.id.text_sourceInvoice)
        textInvoicedate = view.findViewById(R.id.text_invoiceDate)
        textApplicationdate = view.findViewById(R.id.text_applicationDate)
        textWarrantydate = view.findViewById(R.id.text_warrantyDate)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        textInvoicedate.setOnClickDatePicker(requireContext(),year,month,day)
        textApplicationdate.setOnClickDatePicker(requireContext(),year,month,day)
        textWarrantydate.setOnClickDatePicker(requireContext(),year,month,day)

        return view
    }

    override fun refresh(entity: Report) {
        if(entity.sourceInvoice != null){
            textSourceinvoice.setText(entity.sourceInvoice.toString())
        }else{
            textSourceinvoice.setText("")
        }
        textInvoicedate.setText( if(entity.invoiceDate != null) entity.invoiceDate?.toDateTextFormatted() else "")
        textApplicationdate.setText( if(entity.applicationDate != null) entity.applicationDate?.toDateTextFormatted() else "")
        textWarrantydate.setText( if(entity.warrantyDate != null) entity.warrantyDate?.toDateTextFormatted() else "")
    }

    override fun updateReport(entity: Report) {
        entity.sourceInvoice =  textSourceinvoice.text.toString().toLongOrNull()
        entity.invoiceDate =  textInvoicedate.text.toString().toDate()
        entity.applicationDate = textApplicationdate.text.toString().toDate()
        entity.warrantyDate = textWarrantydate.text.toString().toDate()
    }

    companion object {
        fun newInstance() =
            ReportPart02Fragment()
    }

}
