package com.ireny.randon.frasle.warrantyreport.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ireny.randon.frasle.warrantyreport.repositorys.listeners.GetErrorListener
import com.ireny.randon.frasle.warrantyreport.R
import com.ireny.randon.frasle.warrantyreport.ui.listeners.SelectedListener
import com.ireny.randon.frasle.warrantyreport.entites.Report
import com.ireny.randon.frasle.warrantyreport.utils.customApp
import com.ireny.randon.frasle.warrantyreport.utils.mainActivity

class HomeFragment : Fragment(),
    GetErrorListener, SelectedListener<Report> {

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ReportListAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity.supportActionBar?.apply {
            title = "Laudos"
        }

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.homerecyclerview)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ReportListAdapter(context!!,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context!!)

        viewModel = ViewModelProviders.of(this, HomeViewModel.Companion.Factory(
            mainActivity.customApp,
            mainActivity.reportRepository)
        ).get(HomeViewModel::class.java)

        viewModel.all.observe(this, Observer { data ->
            data?.let { adapter.setData(it) }
        })
    }

    override fun onSelected(item: Report) {
        mainActivity.showNewReport(item.id)
    }

    override fun onGetError(id: Long, error: Exception) {
        mainActivity.showError(error.localizedMessage)
    }
}