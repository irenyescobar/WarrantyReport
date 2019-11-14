package com.ireny.randon.frasle.warrantyreport.ui.report.company

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
import com.ireny.randon.frasle.warrantyreport.entites.Company
import com.ireny.randon.frasle.warrantyreport.entites.Report
import com.ireny.randon.frasle.warrantyreport.repositorys.CompanyRepository
import com.ireny.randon.frasle.warrantyreport.ui.listeners.SelectedListener
import com.ireny.randon.frasle.warrantyreport.ui.report.base.FragmentBase
import com.ireny.randon.frasle.warrantyreport.utils.customApp
import com.ireny.randon.frasle.warrantyreport.utils.reportActivity

class CompanyFragment: FragmentBase(), CompanyRepository.ErrorListener, SelectedListener<Company> {

    private var listener: Listener? = null
    private lateinit var adapter: CompanyListAdapter
    private lateinit var recyclerView: RecyclerView
    lateinit var viewModel: CompanyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        reportActivity.supportActionBar?.apply {
            title = "Empresa"
        }
        val view = inflater.inflate(R.layout.report_company_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = CompanyListAdapter(context!!,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context!!)

        reportActivity.companyRepository.setListener(this)

        viewModel = ViewModelProviders.of(this, CompanyViewModel.Companion.Factory(
            reportActivity.customApp,
            reportActivity.companyRepository)
        ).get(CompanyViewModel::class.java)

        viewModel.all.observe(this, Observer { data ->
            data?.let { adapter.setData(it) }
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

    override fun refresh(entity: Report) {
        adapter.refreshSelection(entity.companyId)
    }

    override fun updateReport(entity: Report) {

    }

    override fun onSelected(item: Company) {
        listener?.onChangedCompany(item)
    }

    override fun onCompanyError(error: Exception) {
        reportActivity.showError(error.localizedMessage)
    }

    interface Listener{
        fun onChangedCompany(item: Company)
    }

    companion object {
        fun newInstance() = CompanyFragment()
    }
}

