package com.ireny.warrantyreport.ui.report.document

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.ui.base.IProgressLoading
import com.ireny.warrantyreport.ui.base.IShowMessage
import com.ireny.warrantyreport.ui.report.ReportActivity.Companion.REPORT_ID
import com.ireny.warrantyreport.utils.customApp
import com.ireny.warrantyreport.utils.toDateTextFormatted
import kotlinx.android.synthetic.main.activity_document.*
import kotlinx.android.synthetic.main.custom_progress.*

class DocumentActivity : AppCompatActivity(), IProgressLoading, IShowMessage {

    private val component by lazy { customApp.component }
    private val reportRepository: ReportRepository by lazy { component.reportRepository() }

    private lateinit var currentFragment: PreviewDocumentFragment
    private lateinit var transaction: FragmentTransaction
    private lateinit var viewModel: DocumentViewModel

    private var menuSave: MenuItem? = null
    private var menuShare: MenuItem? = null
    private var isCreateMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val reportId = intent.getLongExtra(REPORT_ID,0)
        isCreateMode = intent.getBooleanExtra(CREATE_MODE,false)

        viewModel = ViewModelProviders.of(this, DocumentViewModel.Companion.Factory(
            customApp,
            reportRepository,
            reportId,
            component.api(),
            component.userAccountManager())
        ).get(DocumentViewModel::class.java)

        showFragment()

        viewModel.model.observe(this, Observer { el ->
            el?.let {
                showReportInFragment(el, isCreateMode)
            }
        })

        viewModel.loadingVisibility.observe(this, Observer { el->
            showProgress(el,null)
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
        menuSave = menu.findItem(R.id.action_save_report)
        menuShare = menu.findItem(R.id.action_share_files)
        menuSave?.isVisible = false
        menuShare?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_save_report -> {
                confirmSaveReport()
                return true
            }
            R.id.action_share_files -> {
                share()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showReportInFragment(model: Report, isCreateMode:Boolean){
        val codeIsNull = model.code == null

        menuSave?.isVisible = codeIsNull
        menuShare?.isVisible = !codeIsNull

        if(isCreateMode && !codeIsNull){
            currentFragment.createDocument(model)
        }
        else {
            currentFragment.bindView(model)
        }
    }

    private fun share() {

        viewModel.model.value?.let { model ->

            val files = currentFragment.files(model)

            val listOfUri:ArrayList<Uri> = arrayListOf()
            files.forEach {
                val uri = FileProvider.getUriForFile(
                        this,
                        FILE_PROVIDER,
                        it
                )
                listOfUri.add(uri)
            }


            val intent = Intent()

            intent.action = Intent.ACTION_SEND_MULTIPLE
            intent.type = "*/*"
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            intent.putExtra( Intent.EXTRA_STREAM, listOfUri )
            intent.putExtra( Intent.EXTRA_SUBJECT,"${getString(R.string.document_name)} ${model.code} - ${(model.code_generated_at?:model.created_at).toDateTextFormatted()}")

            if( intent.resolveActivity( packageManager ) != null ) {
                val intentChooser = Intent.createChooser( intent, "Compartilhar com:" )
                startActivity(intentChooser)
            }
        }
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

    override fun showProgress(show: Boolean, message: String?) {
        if(show){
            contentProgress.visibility = View.VISIBLE
            message?.let {
                textViewProgressMessage.text = it
            }
        }else{
            contentProgress.visibility = View.GONE
            textViewProgressMessage.text = getString(R.string.default_message_progress)
        }
    }

    override fun showMessage(message: String) {
        Snackbar.make(container,message,Snackbar.LENGTH_LONG).show()
    }

    companion object {
        const val CREATE_MODE = "CREATE_MODE"
        const val FILE_PROVIDER = "com.ireny.warrantyreport.fileprovider"
        @JvmStatic
        fun newInstance(context: Context, reportId:Long, createMode:Boolean): Intent {
            val intent = Intent(context, DocumentActivity::class.java)
            intent.putExtra(REPORT_ID,reportId)
            intent.putExtra(CREATE_MODE,createMode)
            return  intent
        }
    }
}
