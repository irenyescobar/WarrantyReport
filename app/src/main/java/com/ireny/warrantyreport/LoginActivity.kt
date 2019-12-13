package com.ireny.warrantyreport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.ireny.warrantyreport.entities.WarrantReportData
import com.ireny.warrantyreport.services.DataHelperService
import com.ireny.warrantyreport.services.UserAccountManager
import com.ireny.warrantyreport.services.interfaces.DownloadBackapDataCompletedListener
import com.ireny.warrantyreport.services.interfaces.ImportDataCompletedListener
import com.ireny.warrantyreport.ui.base.IProgressLoading
import com.ireny.warrantyreport.ui.base.IShowMessage
import com.ireny.warrantyreport.ui.settings.SettingsViewModel
import com.ireny.warrantyreport.utils.customApp
import com.ireny.warrantyreport.utils.toWarrantReportData
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.container
import kotlinx.android.synthetic.main.custom_progress.*

class LoginActivity : AppCompatActivity(),
        IProgressLoading,
        IShowMessage,
        SettingsViewModel.Listener {

    private lateinit var viewModel: SettingsViewModel
    private val component by lazy { customApp.component }
    private val accountManager: UserAccountManager by lazy { component.userAccountManager() }
    private val dataHelperService: DataHelperService by lazy { component.dataHelperService() }
    private var isRestoreBackap = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewModel = ViewModelProviders.of(this,
                SettingsViewModel.Companion.Factory(customApp,this,accountManager,dataHelperService,this)
        ).get(SettingsViewModel::class.java)

        sign_in_button.setOnClickListener{
            startActivityForResult(accountManager.signIntent(), GOOGLE_SIGNIN_REQUEST_CODE)
        }
    }

    override fun onUpdateMessage(message: String) {
        showProgress(true,message)
    }

    override fun onCompleteProccess(success: Boolean, message: String?) {
        if(isRestoreBackap){
            isRestoreBackap = false

            if(success){
                onLoggedIn()
            }else{
                viewModel.importRawData()
            }
        }else{
            onLoggedIn()
        }
    }

    public override fun onStart() {
        super.onStart()
        val account = accountManager.getUserAccount()
        if (account != null) {
            onLoggedIn()
        }else {
            viewModel.clearDatabase()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                GOOGLE_SIGNIN_REQUEST_CODE -> try {

                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    account?.run {
                        isRestoreBackap = true
                        showProgress(true,null)
                        viewModel.restoreBackap()
                    }

                } catch (e: ApiException) {
                    Snackbar.make(container,"Falha de login: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
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

    private fun onLoggedIn() {
        showProgress(false,null)
        val intent = Intent(this, MainActivity::class.java)
        intent.run {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("EXIT", true)
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val GOOGLE_SIGNIN_REQUEST_CODE: Int = 101
    }
}
