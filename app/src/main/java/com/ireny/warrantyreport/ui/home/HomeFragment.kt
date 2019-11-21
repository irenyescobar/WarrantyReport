package com.ireny.warrantyreport.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.repositories.listeners.GetErrorListener
import com.ireny.warrantyreport.ui.adapters.ReportListAdapter
import com.ireny.warrantyreport.ui.listeners.SelectedListener
import com.ireny.warrantyreport.utils.customApp
import com.ireny.warrantyreport.utils.mainActivity

class HomeFragment : Fragment(),
    GetErrorListener, SelectedListener<Report> {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ReportListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity.supportActionBar?.apply {
            title = "Laudos"
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ReportListAdapter(context!!, this)
        recyclerView.adapter = adapter
        linearLayoutManager = LinearLayoutManager(context!!)
        dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            linearLayoutManager.orientation
        )
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)

        viewModel = ViewModelProviders.of(this, HomeViewModel.Companion.Factory(
            mainActivity.customApp,
            mainActivity.reportRepository)
        ).get(HomeViewModel::class.java)

        viewModel.all.observe(this, Observer { data ->
            data?.let { adapter.setData(it) }
        })
    }

    override fun onSelected(item: Report) {
        mainActivity.openReportActivity(item.id)
    }

    override fun onGetError(id: Long, error: Exception) {
        mainActivity.showMessage(error.localizedMessage)
    }
}