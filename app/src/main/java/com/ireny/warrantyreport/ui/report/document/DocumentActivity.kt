package com.ireny.warrantyreport.ui.report.document

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.ui.base.IProgressLoading
import com.ireny.warrantyreport.ui.base.IShowMessage
import com.ireny.warrantyreport.ui.report.ReportActivity.Companion.REPORT_ID
import com.ireny.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.activity_document.*

class DocumentActivity : AppCompatActivity(),
    IProgressLoading,
    IShowMessage {

    private val component by lazy { customApp.component }
    private val reportRepository: ReportRepository by lazy { component.reportRepository() }

    private lateinit var currentFragment: PreviewDocumentFragment
    private lateinit var transaction: FragmentTransaction
    private lateinit var viewModel: DocumentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val reportId = intent.getLongExtra(REPORT_ID,0)

        viewModel = ViewModelProviders.of(this, DocumentViewModel.Companion.Factory(
            customApp,
            reportRepository,
            reportId)
        ).get(DocumentViewModel::class.java)

        showFragment()

        viewModel.model.observe(this, Observer { el ->
            el?.let {
                currentFragment.bindView(el)
            }
        })

        viewModel.loadingVisibility.observe(this, Observer { el->
            showProgress(el)
        })

        viewModel.message.observe(this, Observer { el ->
            if(el.isNotBlank()){
                Snackbar.make(container,el, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.document_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_save_report -> {
                confirmSaveReport()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun confirmSaveReport(){
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage(getString(R.string.confirm_save_report_message))
            .setPositiveButton(getString(R.string.dialog_button_confirm_text)){ dialog, _ ->
                dialog.dismiss()
                saveReport()
            }.setNeutralButton(getString(R.string.dialog_button_cancel_text)){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()

    }

    private fun saveReport(){
        viewModel.saveCodeReport()
    }

    private fun showFragment(){
        currentFragment = PreviewDocumentFragment.newInstance()
        transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, currentFragment)
        transaction.commit()
    }

    override fun showProgress(show: Boolean) {
        if(show){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }
    }

    override fun showMessage(message: String) {
        Snackbar.make(container,message,Snackbar.LENGTH_LONG).show()
    }

    companion object {
        @JvmStatic
        fun newInstance(context: Context, reportId:Long): Intent {
            val intent = Intent(context, DocumentActivity::class.java)
            intent.putExtra(REPORT_ID,reportId)
            return  intent
        }
    }
}
