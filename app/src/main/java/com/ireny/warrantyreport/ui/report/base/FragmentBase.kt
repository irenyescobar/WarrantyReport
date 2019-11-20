package com.ireny.warrantyreport.ui.report.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.interfaces.IBindView
import com.ireny.warrantyreport.ui.report.interfaces.IShowPreviewButton

abstract class FragmentBase: Fragment(),IBindView<Report>{

    internal var previewButtonFunction: IShowPreviewButton? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        previewButtonFunction?.showPreviewButton(false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is IShowPreviewButton) {
            previewButtonFunction = context
        } else {
            throw RuntimeException("$context must implement IShowPreviewButtonFunction")
        }
    }

    override fun onDetach() {
        super.onDetach()
        previewButtonFunction = null
    }
}