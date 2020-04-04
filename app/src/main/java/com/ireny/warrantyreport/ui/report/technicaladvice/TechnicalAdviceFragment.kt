package com.ireny.warrantyreport.ui.report.technicaladvice

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.warrantyreport.ui.listeners.CheckedChangedListener
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.utils.copy
import com.ireny.warrantyreport.utils.customApp
import com.ireny.warrantyreport.utils.reportActivity
import kotlinx.android.synthetic.main.report_technical_advice_fragment.*

class TechnicalAdviceFragment(private val reportId:Long) : FragmentUpdateBase(),
    CheckedChangedListener<ReportTechnicalAdvice> {

    private lateinit var viewModel: TechnicalAdviceViewModel
    private lateinit var adapter: TechnicalAdviceListAdapter
    private var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_technical_advice_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = TechnicalAdviceListAdapter(context!!,this)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = GridLayoutManager(context!!,2)

        viewModel = ViewModelProviders.of(this,TechnicalAdviceViewModel.Companion.Factory(
            reportActivity.customApp,
            reportActivity.technicalAdviceRepository,
            reportActivity.reportRepository,
            reportId)
        ).get(TechnicalAdviceViewModel::class.java)


        viewModel.technicalAdvices.observe(this, Observer {
            it?.let { viewModel.loadData() }
        })

        viewModel.assignedTechnicalAdvices.observe(this, Observer {

            val isImprocedent = it.any{ item -> item.technicalAdviceId == 1}
            textReasonUnfounded.setText("")
            if(isImprocedent){
                textLayout_ReasonUnfounded.visibility = View.VISIBLE
            }else{
                textLayout_ReasonUnfounded.visibility = View.GONE
            }

            it?.let { viewModel.loadData() }
        })

        viewModel.model.observe(this, Observer {
            adapter.setData(it)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement TechnicalAdviceFragment.Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun bindView(model: Report) {
        viewModel.loadData()
        textReasonUnfounded.setText(model.reasonUnfounded)
        textComments.setText(model.comments)
    }

    override fun buildModel(model: Report): Report {
        val copy = model.copy()
        copy.comments = textComments.text.toString()
        copy.reasonUnfounded = textReasonUnfounded.text.toString()
        return copy
    }

    override fun onCheckedChanged(item: ReportTechnicalAdvice) {
        listener?.onItemChangedSelection(item)
    }

    interface Listener{
        fun onItemChangedSelection(item: ReportTechnicalAdvice)
    }

    companion object {
        @JvmStatic
        fun newInstance(reportId:Long) =  TechnicalAdviceFragment(reportId)
    }
}
