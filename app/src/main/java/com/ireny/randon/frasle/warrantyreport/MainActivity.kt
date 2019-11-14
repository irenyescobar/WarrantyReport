package com.ireny.randon.frasle.warrantyreport

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.ireny.randon.frasle.warrantyreport.entities.WarrantReportData
import com.ireny.randon.frasle.warrantyreport.repositories.ReportRepository
import com.ireny.randon.frasle.warrantyreport.services.ImportDataCompletedListener
import com.ireny.randon.frasle.warrantyreport.services.ImportDataService
import com.ireny.randon.frasle.warrantyreport.services.LogError
import com.ireny.randon.frasle.warrantyreport.ui.report.ReportActivity
import com.ireny.randon.frasle.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStream


class MainActivity: AppCompatActivity(), ImportDataCompletedListener {

    private val component by lazy { customApp.component }
    private val importDataService: ImportDataService by lazy { component.importDataService() }
    val reportRepository: ReportRepository by lazy { component.reportRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fab.setOnClickListener {
            showNewReport(null)
        }
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
                    Toast.makeText(this,"Sem dados para importar.",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun showNewReport(reportId:Long?){
        startActivity(ReportActivity.newInstance(this,reportId))
    }

    private fun performFileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(Intent.createChooser(intent,"Selecione o arquivo"), READ_REQUEST_CODE)
    }

    private fun importData(inputStream: InputStream){
        val bufferedReader: BufferedReader = inputStream.bufferedReader()
        val inputString = bufferedReader.use { it.readText() }
        val data: WarrantReportData = Gson().fromJson(inputString, WarrantReportData::class.java)
        importDataService.import(data,this)
    }

    override fun onImportDataCompleted(errors: List<LogError>) {
        Toast.makeText(this, "Importação finalizada com ${errors.count()} erros",Toast.LENGTH_LONG).show()
    }

    fun showError(message:String?) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    fun showSuccess(message:String?) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
    }

    companion object {
        const val READ_REQUEST_CODE: Int = 42
    }
}
