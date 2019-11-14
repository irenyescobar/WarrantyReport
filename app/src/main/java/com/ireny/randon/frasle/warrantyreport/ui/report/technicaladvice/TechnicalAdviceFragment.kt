package com.ireny.randon.frasle.warrantyreport.ui.report.technicaladvice

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ireny.randon.frasle.warrantyreport.R
import com.ireny.randon.frasle.warrantyreport.entites.Report
import com.ireny.randon.frasle.warrantyreport.entites.ReportTechnicalAdvice
import com.ireny.randon.frasle.warrantyreport.ui.report.base.FragmentBase
import com.ireny.randon.frasle.warrantyreport.utils.customApp
import com.ireny.randon.frasle.warrantyreport.utils.reportActivity
import com.irenyescobar.mytasksapp.ui.listeners.CheckedChangeListener

class TechnicalAdviceFragment(private val reportId:Long) : FragmentBase(), CheckedChangeListener<ReportTechnicalAdvice> {

    private lateinit var viewModel: TechnicalAdviceViewModel
    private lateinit var adapter: TechnicalAdviceListAdapter
    private lateinit var recyclerView: RecyclerView
    private var listener: Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reportActivity.supportActionBar?.apply {
            title = "Parecer técnico"
        }
        val view = inflater.inflate(R.layout.report_technical_advice_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = TechnicalAdviceListAdapter(context!!,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context!!)

        viewModel = ViewModelProviders.of(this,TechnicalAdviceViewModel.Companion.Factory(
            reportActivity.customApp,
            reportActivity.technicalAdviceRepository,
            reportActivity.reportRepository,
            reportId)
        ).get(TechnicalAdviceViewModel::class.java)


        viewModel.technicalAdvices.observe(this, Observer { data ->
            data?.let { refresh() }
        })

        viewModel.assignedTechnicalAdvices.observe(this, Observer { data ->
            data?.let { refresh() }
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

    override fun refresh(entity: Report) {
       refresh()
    }

    private fun refresh(){
        adapter.setData(viewModel.getData())
    }

    override fun updateReport(entity: Report) {

    }

    override fun onCheckedChange(item: ReportTechnicalAdvice) {
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
