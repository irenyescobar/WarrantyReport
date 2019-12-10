package com.ireny.warrantyreport.ui.report

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SCROLL_STATE_DRAGGING
import com.google.android.material.snackbar.Snackbar
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Company
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.warrantyreport.repositories.CompanyRepository
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.TechnicalAdviceRepository
import com.ireny.warrantyreport.ui.animations.DepthPageTransformer
import com.ireny.warrantyreport.ui.base.IProgressLoading
import com.ireny.warrantyreport.ui.base.IShowMessage
import com.ireny.warrantyreport.ui.listeners.ValuesViewChangedListener
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import com.ireny.warrantyreport.ui.report.base.FragmentUpdateBase
import com.ireny.warrantyreport.ui.report.comments.CommentsFragment
import com.ireny.warrantyreport.ui.report.company.CompanyFragment
import com.ireny.warrantyreport.ui.report.document.DocumentActivity
import com.ireny.warrantyreport.ui.report.interfaces.IShowPreviewButton
import com.ireny.warrantyreport.ui.report.part01.ReportPart01Fragment
import com.ireny.warrantyreport.ui.report.part02.ReportPart02Fragment
import com.ireny.warrantyreport.ui.report.photos.PhotosFragment
import com.ireny.warrantyreport.ui.report.reasonunfounded.ReasonUnfoundedFragment
import com.ireny.warrantyreport.ui.report.technicaladvice.TechnicalAdviceFragment
import com.ireny.warrantyreport.ui.report.technicalconsultant.TechnicalConsultantFragment
import com.ireny.warrantyreport.utils.copy
import com.ireny.warrantyreport.utils.customApp
import com.ireny.warrantyreport.utils.toDateTextFormatted
import kotlinx.android.synthetic.main.report_activity.*

class ReportActivity : AppCompatActivity(),
    IProgressLoading,
    IShowMessage,
    CompanyFragment.Listener,
    TechnicalAdviceFragment.Listener,
    ValuesViewChangedListener,
    IShowPreviewButton
{


    private val component by lazy { customApp.component }
    val companyRepository: CompanyRepository by lazy { component.companyRepository() }
    val technicalAdviceRepository: TechnicalAdviceRepository by lazy { component.technicalAdviceRepository() }
    val reportRepository: ReportRepository by lazy { component.reportRepository() }

    private lateinit var viewModel: ReportViewModel
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }

        setContentView(R.layout.report_activity)

        val reportId = intent.getLongExtra(REPORT_ID,0)

        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager, reportId)
        viewpager.setPageTransformer(true,DepthPageTransformer())
        viewpager.adapter = sectionsPagerAdapter
        viewpager.addOnPageChangeListener(PageChangeListener())
        tablayout.setupWithViewPager(viewpager)

        previewButton.setOnClickListener{
            showPreview()
        }

        viewModel = ViewModelProviders.of(this, ReportViewModel.Companion.Factory(
            customApp,
            reportRepository)
        ).get(ReportViewModel::class.java)


        viewModel.model.observe(this, Observer { el ->
            el?.let {
                bindView(el)
            }
        })

        viewModel.loadingVisibility.observe(this, Observer { el->
            showProgress(el)
        })

        viewModel.message.observe(this, Observer { el ->
            if(el.isNotBlank()){
               showMessage(el)
            }
        })

        if(reportId > 0) {
            viewModel.loadModel(reportId)
        }
    }

    private fun bindView(model:Report){
        supportActionBar?.run {
            title =  "LAUDO ${model.id} - ${model.created_at.toDateTextFormatted()}"
        }
        bind(model)
    }

    internal fun refresh(){
        viewModel.model.value?.run {
           bind(this)
        }
    }

    private fun bind(model:Report){
        sectionsPagerAdapter.childFragments[viewpager.currentItem].bindView(model)
    }

    override fun showProgress(show:Boolean){
        if(show){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }
    }

    override fun showMessage(message: String) {
        Snackbar.make(root,message,Snackbar.LENGTH_LONG).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onValuesViewChanged()
        onBackPressed()
        return true
    }

    override fun onItemChangedSelection(item: ReportTechnicalAdvice) {
        progressBar.visibility = View.VISIBLE
        viewModel.save(item)
    }

    override fun onChangedCompany(item: Company) {
        viewModel.model.value?.run {
            val copy = this.copy()
            copy.companyId = item.id
            copy.company = item
            viewModel.save(copy)
        }
    }

    override fun onValuesViewChanged() {
        val fragment = sectionsPagerAdapter.childFragments[viewpager.currentItem]
        if(fragment is FragmentUpdateBase){
            viewModel.model.value?.run {
                val model = fragment.buildModel(this)
                viewModel.save(model)
            }
        }
    }

    override fun showPreviewButton(show:Boolean) {
        if(show){
            previewButton.visibility = View.VISIBLE
        }else {
            previewButton.visibility = View.GONE
        }
    }

    private fun showPreview(){
        viewModel.model.value?.let {
            startActivity(DocumentActivity.newInstance(this,it.id))
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager,
                                     reportId: Long) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        val childFragments: Array<FragmentBase> = arrayOf(
            CompanyFragment.newInstance(),
            ReportPart01Fragment.newInstance(),
            ReportPart02Fragment.newInstance(),
            TechnicalAdviceFragment.newInstance(reportId),
            ReasonUnfoundedFragment.newInstance(),
            CommentsFragment.newInstance(),
            TechnicalConsultantFragment.newInstance(),
            PhotosFragment.newInstance(reportId)
        )

        override fun getItem(position: Int): FragmentBase {
            return childFragments[position]
        }

        override fun getCount(): Int {
            return 8
        }
    }

    inner class PageChangeListener:ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(state: Int) {
            if(state == SCROLL_STATE_DRAGGING){
                onValuesViewChanged()
            }
        }
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) { }

        override fun onPageSelected(position: Int) {
            refresh()
        }
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
