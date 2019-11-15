package com.ireny.warrantyreport.ui.report.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.utils.reportActivity

class CommentsFragment: FragmentUpdateBase() {

    private lateinit var textView: TextView

     override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
     ): View {

          reportActivity.supportActionBar?.apply {
             title = "Observações"
          }
          val view = inflater.inflate(R.layout.edittext_multiline_fragment, container, false)
          textView = view.findViewById(R.id.text)
          return view
     }

    override fun refresh(entity: Report) {
        textView.text = entity.comments
    }

    override fun updateReport(entity: Report) {
        entity.comments = textView.text.toString()
    }

    companion object {
        fun newInstance() =
            CommentsFragment()
    }
}
