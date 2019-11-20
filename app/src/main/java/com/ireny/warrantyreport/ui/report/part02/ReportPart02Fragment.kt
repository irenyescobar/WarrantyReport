package com.ireny.warrantyreport.ui.report.part02

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
import kotlinx.android.synthetic.main.report_part02_fragment.*
import java.util.*

class ReportPart02Fragment : FragmentUpdateBase() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.report_part02_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        textInvoiceDate.setOnClickDatePicker(requireContext(),year,month,day)
        textApplicationDate.setOnClickDatePicker(requireContext(),year,month,day)
        textWarrantyDate.setOnClickDatePicker(requireContext(),year,month,day)
    }

    override fun bindView(model: Report) {
        if(model.sourceInvoice != null){
            textSourceInvoice.setText(model.sourceInvoice.toString())
        }else{
            textSourceInvoice.setText("")
        }
        textSourceInvoice.setText( if(model.invoiceDate != null) model.invoiceDate?.toDateTextFormatted() else "")
        textApplicationDate.setText( if(model.applicationDate != null) model.applicationDate?.toDateTextFormatted() else "")
        textWarrantyDate.setText( if(model.warrantyDate != null) model.warrantyDate?.toDateTextFormatted() else "")
    }

    override fun buildModel(model: Report) :Report{
        val copy = model.copy()
        copy.sourceInvoice =  textSourceInvoice.text.toString().toLongOrNull()
        copy.invoiceDate =  textInvoiceDate.text.toString().toDate()
        copy.applicationDate = textApplicationDate.text.toString().toDate()
        copy.warrantyDate = textWarrantyDate.text.toString().toDate()
        return copy
    }

    companion object {
        fun newInstance() =
            ReportPart02Fragment()
    }

}
