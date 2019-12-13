package com.ireny.warrantyreport

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.ireny.warrantyreport.ui.base.IProgressLoading
import com.ireny.warrantyreport.ui.base.IShowMessage
import com.ireny.warrantyreport.ui.home.HomeFragment
import com.ireny.warrantyreport.ui.report.ReportActivity
import com.ireny.warrantyreport.ui.report.document.DocumentActivity
import com.ireny.warrantyreport.ui.reportscompleted.ReportsCompletedFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_progress.*


class MainActivity: AppCompatActivity(),
        IProgressLoading,
        IShowMessage,
        HomeFragment.Listener,
        ReportsCompletedFragment.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_home))
        setupActionBarWithNavController(navController, appBarConfiguration)
        setup(navView,navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showProgress(show: Boolean, message:String?) {
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

    @SuppressLint("RestrictedApi")
    private fun setup(navView: BottomNavigationView, navController: NavController){

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

    private fun openReportActivity(reportId:Long?){
        startActivity(ReportActivity.newInstance(this,reportId))
    }
}
