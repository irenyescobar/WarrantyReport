package com.ireny.warrantyreport.ui.reportscompleted

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.repositories.listeners.GetErrorListener
import com.ireny.warrantyreport.ui.adapters.ReportListAdapter
import com.ireny.warrantyreport.ui.listeners.SelectedListener
import com.ireny.warrantyreport.utils.customApp
import com.ireny.warrantyreport.utils.mainActivity
import kotlinx.android.synthetic.main.fragment_reports_completed.*

class ReportsCompletedFragment : Fragment() , GetErrorListener, SelectedListener<Report> {

    private lateinit var viewModel: ReportsCompletedViewModel
    private lateinit var adapter: ReportListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private var listener:Listener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity.supportActionBar?.apply {
            title = "Laudos finalizados"
        }
        return inflater.inflate(R.layout.fragment_reports_completed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ReportListAdapter(context!!, this)
        recyclerview.adapter = adapter
        linearLayoutManager = LinearLayoutManager(context!!)
        dividerItemDecoration = DividerItemDecoration(
            recyclerview.context,
            linearLayoutManager.orientation
        )
        recyclerview.layoutManager = linearLayoutManager
        recyclerview.addItemDecoration(dividerItemDecoration)

        viewModel = ViewModelProviders.of(this, ReportsCompletedViewModel.Companion.Factory(
            mainActivity.customApp,
            mainActivity.reportRepository)
        ).get(ReportsCompletedViewModel::class.java)

        viewModel.all.observe(this, Observer { data ->
            data?.let { adapter.setData(it) }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ReportsCompletedFragment.Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSelected(item: Report) {
        listener?.openCompletedReport(item.id)
    }

    override fun onGetError(id: Long, error: Exception) {
        listener?.showError(error.localizedMessage)
    }

    interface Listener{
        fun showError(error:String)
        fun openCompletedReport(reportId:Long)
    }
}