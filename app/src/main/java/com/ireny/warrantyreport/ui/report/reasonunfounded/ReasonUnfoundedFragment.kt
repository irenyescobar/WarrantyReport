package com.ireny.warrantyreport.ui.report.reasonunfounded

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import com.ireny.warrantyreport.utils.reportActivity

class ReasonUnfoundedFragment : FragmentBase() {

    private lateinit var textView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        reportActivity.supportActionBar?.apply {
            title = "Motivo da improcedência"
        }

        val view = inflater.inflate(R.layout.report_reason_unfounded_fragment, container, false)
        textView = view.findViewById(R.id.text)

        return view
    }

    override fun refresh(entity: Report) {
        textView.text  = entity.reasonUnfounded
    }

    override fun updateReport(entity: Report) {
        entity.reasonUnfounded = textView.text.toString()
    }

    companion object {
        fun newInstance() =
            ReasonUnfoundedFragment()
    }
}
