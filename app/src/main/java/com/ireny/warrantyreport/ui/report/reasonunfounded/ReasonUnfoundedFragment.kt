package com.ireny.warrantyreport.ui.report.reasonunfounded

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.utils.copy
import kotlinx.android.synthetic.main.report_reason_unfounded_fragment.*

class ReasonUnfoundedFragment : FragmentUpdateBase(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return  inflater.inflate(R.layout.report_reason_unfounded_fragment, container, false)
    }

    override fun buildModel(model: Report):Report {
        val copy = model.copy()
        copy.reasonUnfounded = textReasonUnfounded.text.toString()
        return copy
    }

    override fun bindView(model: Report) {
        textReasonUnfounded.setText(model.reasonUnfounded)
    }

    companion object {
        fun newInstance() =
            ReasonUnfoundedFragment()
    }
}
