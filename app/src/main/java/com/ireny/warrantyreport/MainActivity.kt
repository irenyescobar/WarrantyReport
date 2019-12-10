package com.ireny.warrantyreport

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.ireny.warrantyreport.entities.WarrantReportData
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.services.ImportDataCompletedListener
import com.ireny.warrantyreport.services.ImportDataService
import com.ireny.warrantyreport.services.LogError
import com.ireny.warrantyreport.ui.base.IProgressLoading
import com.ireny.warrantyreport.ui.base.IShowMessage
import com.ireny.warrantyreport.ui.home.HomeFragment
import com.ireny.warrantyreport.ui.report.ReportActivity
import com.ireny.warrantyreport.ui.report.document.DocumentActivity
import com.ireny.warrantyreport.ui.reportscompleted.ReportsCompletedFragment
import com.ireny.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStream



class MainActivity: AppCompatActivity(),
    ImportDataCompletedListener ,
    IProgressLoading,
    IShowMessage,
    HomeFragment.Listener,
    ReportsCompletedFragment.Listener{

    private val component by lazy { customApp.component }
    private val importDataService: ImportDataService by lazy { component.importDataService() }
    val reportRepository: ReportRepository by lazy { component.reportRepository() }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    fab.visibility = View.VISIBLE
                }
                R.id.navigation_reportscompleted,
                R.id.navigation_settings -> {
                    fab.visibility = View.GONE
                }
            }
        }

        fab.setOnClickListener {
            openReportActivity(null)
        }
    }

    override fun onStart() {
        super.onStart()
        checkIsFirstRun()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_import_data -> {
                performFileSearch()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            resultData?.data?.also { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                if(inputStream != null){
                    importData(inputStream)
                }else {
                   showMessage(getString(R.string.no_data_to_import))
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun openReportActivity(reportId:Long?){
        startActivity(ReportActivity.newInstance(this,reportId))
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(Intent.createChooser(intent,getString(R.string.select_file_message)), READ_REQUEST_CODE)
    }

    private fun checkIsFirstRun(){
        showProgress(true)
        var mboolean = false
        var settings = getSharedPreferences(SETTINGS_INSTALLATION, 0)
        mboolean = settings.getBoolean(FIRST_RUN, false)

        if (!mboolean) {
            importRawData()

            settings = getSharedPreferences(SETTINGS_INSTALLATION, 0)
            val editor = settings.edit()
            editor.putBoolean(FIRST_RUN, true)
            editor.commit()
        }
        showProgress(false)
    }

    private fun importRawData(){
        val databaseInputStream = resources.openRawResource(R.raw.data)
        importData(databaseInputStream)
    }


    private fun importData(inputStream: InputStream){
        val bufferedReader: BufferedReader = inputStream.bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        val data: WarrantReportData = Gson().fromJson(inputString, WarrantReportData::class.java)
        importDataService.import(data,this)
    }

    override fun onImportDataCompleted(errors: List<LogError>) {
        if(errors.count() > 0) {
            showMessage(getString(R.string.import_data_error_message))
        }
    }

    override fun showProgress(show: Boolean) {
        if(show){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }
    }

    override fun showMessage(message: String) {
        Snackbar.make(container,message, Snackbar.LENGTH_LONG).show()
    }

    override fun showError(error: String) {
        showMessage(error)
    }

    override fun openReport(reportId: Long) {
        openReportActivity(reportId)
    }

    override fun openCompletedReport(reportId: Long) {
        startActivity(DocumentActivity.newInstance(this,reportId,false))
    }

    companion object {
        const val SETTINGS_INSTALLATION ="SETTINGS_INSTALLATION"
        const val FIRST_RUN ="FIRST_RUN"
        const val READ_REQUEST_CODE: Int = 42
    }
}
