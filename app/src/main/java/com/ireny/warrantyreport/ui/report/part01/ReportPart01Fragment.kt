package com.ireny.warrantyreport.ui.report.part01

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.utils.copy
import kotlinx.android.synthetic.main.report_part01_fragment.*

class ReportPart01Fragment : FragmentUpdateBase() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.report_part01_fragment, container, false)
    }

    override fun bindView(model: Report) {
        textDistributor.setText(model.distributor)
        textClient.setText(model.client)
        textPartReference.setText(model.partReference)
        textCityState.setText(model.cityState)
    }
    override fun buildModel(model: Report):Report {
        val copy = model.copy()
        copy.distributor = textDistributor.text.toString()
        copy.client = textClient.text.toString()
        copy.partReference = textPartReference.text.toString()
        copy.cityState = textCityState.text.toString()
        return copy
    }

    companion object {
        fun newInstance() =
            ReportPart01Fragment()
    }
}
