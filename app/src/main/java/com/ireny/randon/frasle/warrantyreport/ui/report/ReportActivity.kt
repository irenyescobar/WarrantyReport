package com.ireny.randon.frasle.warrantyreport.ui.report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.ireny.randon.frasle.warrantyreport.R
import com.ireny.randon.frasle.warrantyreport.entities.Company
import com.ireny.randon.frasle.warrantyreport.entities.Report
import com.ireny.randon.frasle.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.randon.frasle.warrantyreport.repositorys.CompanyRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.ReportRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.TechnicalAdviceRepository
import com.ireny.randon.frasle.warrantyreport.repositorys.listeners.GetSuccessListener
import com.ireny.randon.frasle.warrantyreport.ui.listeners.CompletedOperationListener
import com.ireny.randon.frasle.warrantyreport.ui.report.base.FragmentBase
import com.ireny.randon.frasle.warrantyreport.ui.report.comments.CommentsFragment
import com.ireny.randon.frasle.warrantyreport.ui.report.company.CompanyFragment
import com.ireny.randon.frasle.warrantyreport.ui.report.part01.ReportPart01Fragment
import com.ireny.randon.frasle.warrantyreport.ui.report.part02.ReportPart02Fragment
import com.ireny.randon.frasle.warrantyreport.ui.report.photos.PhotosFragment
import com.ireny.randon.frasle.warrantyreport.ui.report.reasonunfounded.ReasonUnfoundedFragment
import com.ireny.randon.frasle.warrantyreport.ui.report.technicaladvice.TechnicalAdviceFragment
import com.ireny.randon.frasle.warrantyreport.ui.report.technicalconsultant.TechnicalConsultantFragment
import com.ireny.randon.frasle.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.report_activity.*

class ReportActivity : AppCompatActivity(),
    CompanyFragment.Listener,
    TechnicalAdviceFragment.Listener,
    CompletedOperationListener<Report>,
    GetSuccessListener<Report>
{
    val component by lazy { customApp.component }
    val companyRepository: CompanyRepository by lazy { component.companyRepository() }
    val technicalAdviceRepository: TechnicalAdviceRepository by lazy { component.technicalAdviceRepository() }
    val reportRepository: ReportRepository by lazy { component.reportRepository() }

    private lateinit var currentFragment: FragmentBase
    private lateinit var transaction: FragmentTransaction
    private lateinit var viewModel: ReportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        setContentView(R.layout.report_activity)

        val reportId = intent.getLongExtra(REPORT_ID,0)

        viewModel = ViewModelProviders.of(this, ReportViewModel.Companion.Factory(
            customApp,
            reportRepository,
            this,
            this)
        ).get(ReportViewModel::class.java)

        if (savedInstanceState == null) {
            showAndRefreshFragment(CompanyFragment.newInstance())
        }

        if(reportId > 0) {
            viewModel.load(reportId)
        }

        button_next.setOnClickListener {
            saveAndContinue()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        when (currentFragment) {
            is CompanyFragment -> {
                super.onBackPressed()
            }
            is ReportPart01Fragment -> {
                showAndRefreshFragment(CompanyFragment.newInstance())
            }
            is ReportPart02Fragment -> {
                showAndRefreshFragment(ReportPart01Fragment.newInstance())
            }
            is TechnicalAdviceFragment -> {
                showAndRefreshFragment(ReportPart02Fragment.newInstance())
            }
            is ReasonUnfoundedFragment -> {
                showAndRefreshFragment(TechnicalAdviceFragment.newInstance(viewModel.report.id))
            }
            is CommentsFragment -> {
                showAndRefreshFragment(ReasonUnfoundedFragment.newInstance())
            }
            is TechnicalConsultantFragment -> {
                showAndRefreshFragment(CommentsFragment.newInstance())
            }
            is PhotosFragment -> {
                showAndRefreshFragment(TechnicalConsultantFragment.newInstance())
            }
        }
    }

    override fun onGetSuccess(entity: Report) {
        updateScreen(entity)
    }

    override fun onCompletedOperation(entity: Report, success: Boolean) {
        if(success){
            updateScreen(entity)
            showSuccess("Salvo com sucesso")
        }else{
            showError("Operação completada com erros")
        }
    }

    private fun updateScreen(report: Report){
        currentFragment.refresh(report)
    }

    private fun showAndRefreshFragment(fragment: FragmentBase){
        currentFragment = fragment
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, currentFragment)
        transaction.commit()
        viewModel.refresh()
    }

    private fun saveAndContinue() {
        currentFragment.updateReport(viewModel.report)
        viewModel.save()
        next()
    }

    fun next() {
        when (currentFragment) {
            is CompanyFragment -> {
               showAndRefreshFragment(ReportPart01Fragment.newInstance())
            }
            is ReportPart01Fragment -> {
                showAndRefreshFragment(ReportPart02Fragment.newInstance())
            }
            is ReportPart02Fragment -> {
                showAndRefreshFragment(TechnicalAdviceFragment.newInstance(viewModel.report.id))
            }
            is TechnicalAdviceFragment -> {
                showAndRefreshFragment(ReasonUnfoundedFragment.newInstance())
            }
            is ReasonUnfoundedFragment -> {
                showAndRefreshFragment(CommentsFragment.newInstance())
            }
            is CommentsFragment -> {
                showAndRefreshFragment(TechnicalConsultantFragment.newInstance())
            }
            is TechnicalConsultantFragment -> {
                showAndRefreshFragment(PhotosFragment.newInstance(viewModel.report.id))
            }
            is PhotosFragment -> {
                showError("Função ainda sem fazer!")
            }
        }
    }

    override fun onItemChangedSelection(item: ReportTechnicalAdvice) {
        viewModel.save(item)
    }

    override fun onChangedCompany(item: Company){
        viewModel.report.company = item
        viewModel.save()
    }

    fun showError(message:String?) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    fun showSuccess(message:String?) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    companion object {

        const val REPORT_ID = "REPORT_ID"

        @JvmStatic
        fun newInstance(context: Context, reportId:Long?):Intent {
            val intent = Intent(context, ReportActivity::class.java)
            reportId?.let {
                intent.putExtra(REPORT_ID,it)
            }
            return  intent
        }
    }
}
