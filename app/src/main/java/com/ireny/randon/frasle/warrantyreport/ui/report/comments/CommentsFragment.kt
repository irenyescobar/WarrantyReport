package com.ireny.randon.frasle.warrantyreport.ui.report.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ireny.randon.frasle.warrantyreport.R
import com.ireny.randon.frasle.warrantyreport.entities.Report
import com.ireny.randon.frasle.warrantyreport.ui.report.base.FragmentBase
import com.ireny.randon.frasle.warrantyreport.utils.reportActivity

class CommentsFragment: FragmentBase() {

    private lateinit var textView: TextView

     override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
     ): View {

          reportActivity.supportActionBar?.apply {
             title = "Observações"
          }
          val view = inflater.inflate(R.layout.report_comments_fragment, container, false)
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
