package com.ireny.warrantyreport.ui.report.company

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Company
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.repositories.CompanyRepository
import com.ireny.warrantyreport.ui.listeners.SelectedListener
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import com.ireny.warrantyreport.utils.customApp
import com.ireny.warrantyreport.utils.reportActivity

class CompanyFragment: FragmentBase(), CompanyRepository.ErrorListener, SelectedListener<Company> {

    private var listener: Listener? = null
    private  var adapter: CompanyListAdapter? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: CompanyViewModel
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recyclerview_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = CompanyListAdapter(context!!,this)
        recyclerView.adapter = adapter
        linearLayoutManager = LinearLayoutManager(context!!)
        dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            linearLayoutManager.orientation
        )
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addItemDecoration(dividerItemDecoration)

        reportActivity.companyRepository.setListener(this)

        viewModel = ViewModelProviders.of(this, CompanyViewModel.Companion.Factory(
            reportActivity.customApp,
            reportActivity.companyRepository)
        ).get(CompanyViewModel::class.java)

        viewModel.all.observe(this, Observer { data ->
            data?.let { adapter?.setData(it) }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement CompanyFragment.Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun bindView(model: Report) {
        adapter?.refreshSelection(model.companyId)
    }

    override fun onSelected(item: Company) {
        listener?.onChangedCompany(item)
    }

    override fun onCompanyError(error: Exception) {
        //reportActivity.showError(error.localizedMessage)
    }

    interface Listener{
        fun onChangedCompany(item: Company)
    }

    companion object {
        fun newInstance() = CompanyFragment()
    }
}

