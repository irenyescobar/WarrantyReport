package com.ireny.warrantyreport.ui.report.part01

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import com.ireny.warrantyreport.utils.reportActivity

class ReportPart01Fragment : FragmentBase() {

    private lateinit var textDistributor: TextView
    private lateinit var textClient: TextView
    private lateinit var textPartreference: TextView
    private lateinit var textCitystate: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        reportActivity.supportActionBar?.apply {
            title = "Laudo parte 01"
        }

        val view = inflater.inflate(R.layout.report_part01_fragment, container, false)

        textDistributor = view.findViewById(R.id.text_distributor)
        textClient = view.findViewById(R.id.text_client)
        textPartreference = view.findViewById(R.id.text_partReference)
        textCitystate = view.findViewById(R.id.text_cityState)

        return view
    }

    override fun refresh(entity: Report){
        textDistributor.text = entity.distributor
        textClient.text = entity.client
        textPartreference.text = entity.partReference
        textCitystate.text = entity.cityState
     }

    override fun updateReport(entity: Report) {
        entity.distributor = textDistributor.text.toString()
        entity.client = textClient.text.toString()
        entity.partReference = textPartreference.text.toString()
        entity.cityState = textCitystate.text.toString()
    }

    companion object {
        fun newInstance() =
            ReportPart01Fragment()
    }
}
