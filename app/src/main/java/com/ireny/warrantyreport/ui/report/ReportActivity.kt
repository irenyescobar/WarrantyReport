package com.ireny.warrantyreport.ui.report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Company
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.warrantyreport.repositories.CompanyRepository
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.TechnicalAdviceRepository
import com.ireny.warrantyreport.repositories.listeners.GetSuccessListener
import com.ireny.warrantyreport.ui.listeners.CompletedOperationListener
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.ui.report.comments.CommentsFragment
import com.ireny.warrantyreport.ui.report.company.CompanyFragment
import com.ireny.warrantyreport.ui.report.part01.ReportPart01Fragment
import com.ireny.warrantyreport.ui.report.part02.ReportPart02Fragment
import com.ireny.warrantyreport.ui.report.photos.PhotosFragment
import com.ireny.warrantyreport.ui.report.previewdocument.PreviewDocumentFragment
import com.ireny.warrantyreport.ui.report.reasonunfounded.ReasonUnfoundedFragment
import com.ireny.warrantyreport.ui.report.technicaladvice.TechnicalAdviceFragment
import com.ireny.warrantyreport.ui.report.technicalconsultant.TechnicalConsultantFragment
import com.ireny.warrantyreport.utils.customApp
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
            is PreviewDocumentFragment -> {
                showAndRefreshFragment(PhotosFragment.newInstance(viewModel.report.id))
            }
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

        if(currentFragment is PreviewDocumentFragment){
            (currentFragment as PreviewDocumentFragment).createDocument(viewModel.report)
            return
        }

        if(currentFragment is FragmentUpdateBase) {
            (currentFragment as FragmentUpdateBase).updateReport(viewModel.report)
             viewModel.save()
        }

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
                showAndRefreshFragment(PreviewDocumentFragment.newInstance())
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

    override fun onGetSuccess(entity: Report) {
        updateScreen(entity)
    }

    override fun onCompletedOperation(entity: Report, success: Boolean) {
        if(success){
            showSuccess("Salvo com sucesso")
        }else{
            showError("Operação completada com erros")
        }
    }

    fun showError(message:String?) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(message:String?) {
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
