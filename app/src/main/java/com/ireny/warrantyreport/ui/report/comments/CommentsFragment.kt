package com.ireny.warrantyreport.ui.report.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.utils.copy
import kotlinx.android.synthetic.main.report_comments_fragment.*

class CommentsFragment : FragmentUpdateBase() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.report_comments_fragment, container, false)
    }

    override fun buildModel(model: Report): Report {
        val copy = model.copy()
        copy.comments = textComments.text.toString()
        return copy
    }

    override fun bindView(model: Report) {
        textComments.setText(model.comments)
    }

    companion object {
        fun newInstance() =
            CommentsFragment()
    }
}
