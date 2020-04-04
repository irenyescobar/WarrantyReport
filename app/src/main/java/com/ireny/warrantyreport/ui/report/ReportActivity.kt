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
import com.ireny.warrantyreport.ui.report.company.CompanyFragment
import com.ireny.warrantyreport.ui.report.document.DocumentActivity
import com.ireny.warrantyreport.ui.report.interfaces.IShowPreviewButton
import com.ireny.warrantyreport.ui.report.part01.ReportPart01Fragment
import com.ireny.warrantyreport.ui.report.part02.ReportPart02Fragment
import com.ireny.warrantyreport.ui.report.photos.PhotosFragment
import com.ireny.warrantyreport.ui.report.technicaladvice.TechnicalAdviceFragment
import com.ireny.warrantyreport.ui.report.technicalconsultant.TechnicalConsultantFragment
import com.ireny.warrantyreport.utils.copy
import com.ireny.warrantyreport.utils.customApp
import com.library.NavigationBar
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
    private var currentPagePosition = 0
    private var titlePages: Array<String> = arrayOf()
    private var childFragments: MutableList<FragmentBase> = mutableListOf()
    private var companyChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        setContentView(R.layout.report_activity)
        val reportId = intent.getLongExtra(REPORT_ID,0)

        childFragments = mutableListOf(
            CompanyFragment.newInstance(),
            ReportPart01Fragment.newInstance(),
            ReportPart02Fragment.newInstance()
        )

        setupViewPager()
        setupSteps()
        setupNavButtons()
        setupViewModel()
        setupTitlePages()

        if(reportId > 0) {
            viewModel.loadModel(reportId)
        }
    }

    private fun completePages(reportId: Long){
        if(reportId > 0 && childFragments.size < 6){
            childFragments.addAll( arrayOf(
                TechnicalAdviceFragment.newInstance(reportId),
                TechnicalConsultantFragment.newInstance(),
                PhotosFragment.newInstance(reportId)
            ))
            setupViewPager()
            setupSteps()
        }

    }
    private fun setupViewPager(){
        sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        viewpager.setPageTransformer(true,DepthPageTransformer())
        viewpager.adapter = sectionsPagerAdapter
        viewpager.addOnPageChangeListener(PageChangeListener())
    }

    private fun setupSteps(){
        stepsBar.tabCount = sectionsPagerAdapter.count
        stepsBar.animateView(0)
        stepsBar.currentPosition = currentPagePosition
        stepsBar.resetItems()

        stepsBar.onTabClick = NavigationBar.OnTabClick { touchPosition, _, _ ->
            onValuesViewChanged()
            currentPagePosition = touchPosition
            viewpager.currentItem = currentPagePosition
        }
    }

    private fun setupTitlePages(){
        titlePages = arrayOf(
            getString(R.string.title_page_01),
            getString(R.string.title_page_02),
            getString(R.string.title_page_03),
            getString(R.string.title_page_04),
            getString(R.string.title_page_05),
            getString(R.string.title_page_06)
        )
        titlePage.text = titlePages[currentPagePosition]
    }
    private fun setupNavButtons(){
        previewButton.setOnClickListener{
            showPreview()
        }

        imageViewBack.setOnClickListener {
            if(currentPagePosition > 0){
                onValuesViewChanged()
                currentPagePosition--
                viewpager.currentItem = currentPagePosition
            }
        }
        imageViewNext.setOnClickListener {
            if(currentPagePosition < sectionsPagerAdapter.count - 1){
                onValuesViewChanged()
                currentPagePosition++
                viewpager.currentItem = currentPagePosition
            }
        }
    }

    private fun setupViewModel(){
        viewModel = ViewModelProviders.of(this, ReportViewModel.Companion.Factory(
            customApp,
            reportRepository)
        ).get(ReportViewModel::class.java)


        viewModel.model.observe(this, Observer { el ->
            el?.let {
                completePages(it.id)
                bindView(el)
            }
        })

        viewModel.loadingVisibility.observe(this, Observer { el->
            showProgress(el,null)
        })

        viewModel.message.observe(this, Observer { el ->
            if(el.isNotBlank()){
                showMessage(el)
            }
        })
    }

    private fun bindView(model:Report){
        supportActionBar?.run {
            var identify = "Item"
            if(model.company == null) {
                if(model.distributor != ""){
                    identify = model.distributor
                }else if(model.client != ""){
                    identify = model.client
                }
            }else {
                model.company?.run {
                    identify = description
                }

                nextPageAfterCompanySelected()
            }

            title =  "$identify - ${model.id}"
        }
        bind(model)
    }

    private fun nextPageAfterCompanySelected(){
        if(companyChanged){
            imageViewNext.performClick()
            companyChanged = false
        }
    }

    internal fun refresh(position: Int){
        currentPagePosition = position
        stepsBar.currentPosition = currentPagePosition
        titlePage.text = titlePages[currentPagePosition]
        viewModel.model.value?.run {
           bind(this)
        }
    }

    private fun bind(model:Report){
        childFragments[viewpager.currentItem].bindView(model)
    }

    override fun showProgress(show: Boolean, message: String?) {
        if(show){
            contentProgress.visibility = View.VISIBLE
        }else{
            contentProgress.visibility = View.GONE
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
        showProgress(true,null)
        viewModel.save(item)
    }

    override fun onChangedCompany(item: Company) {
        viewModel.model.value?.run {
            val copy = this.copy()
            copy.companyId = item.id
            copy.company = item
            viewModel.save(copy)

            companyChanged = true
        }
    }

    override fun onValuesViewChanged() {
        val fragment = childFragments[viewpager.currentItem]
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
            startActivity(DocumentActivity.newInstance(this,it.id,true))
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): FragmentBase {
            return childFragments[position]
        }

        override fun getCount(): Int {
            return childFragments.size
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
            refresh(position)
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
