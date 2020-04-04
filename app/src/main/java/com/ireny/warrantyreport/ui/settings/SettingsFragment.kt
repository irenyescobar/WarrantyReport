package com.ireny.warrantyreport.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.ireny.warrantyreport.LoginActivity
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.data.retrofit.Api
import com.ireny.warrantyreport.services.DataHelperService
import com.ireny.warrantyreport.services.IReportDirectoryManager
import com.ireny.warrantyreport.services.UserAccountManager
import com.ireny.warrantyreport.ui.base.IProgressLoading
import com.ireny.warrantyreport.ui.base.IShowMessage
import com.ireny.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : Fragment(),
    SettingsViewModel.Listener,
    SettingsViewModel.LogoutListener,
    SettingsViewModel.RequestAuthorizationListener{

    private lateinit var viewModel: SettingsViewModel
    private val component by lazy { customApp.component }
    private val accountManager: UserAccountManager by lazy { component.userAccountManager() }
    private val dataHelperService: DataHelperService by lazy { component.dataHelperService() }
    private val directoryManager: IReportDirectoryManager by lazy { component.reportDirectoryManager()}
    private val api: Api by lazy { component.api() }
    private var progress: IProgressLoading? = null
    private var showMessage: IShowMessage? = null
    private var isLogoutWithSaveBackap = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this,
            SettingsViewModel.Companion.Factory(
                customApp,
                requireContext(),
                accountManager,
                dataHelperService,
                directoryManager,
                this,
                api,
                this)
        ).get(SettingsViewModel::class.java)

        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        signOut.setOnClickListener{

            AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.app_name))
                    .setMessage(getString(R.string.logout_with_save_backap_message))
                    .setPositiveButton(getString(R.string.logout_save_and_desconect_action)) { dialog, _ ->
                        dialog.cancel()
                        isLogoutWithSaveBackap = true
                        saveBackap()
                    }
                    .setNegativeButton(getString(R.string.logout_desconect_action)) { dialog, _ ->
                        dialog.cancel()
                        continueLogout()
                    }
                    .setNeutralButton(getString(R.string.cancel_action)) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()

        }

        backapData.setOnClickListener{
            saveBackap()
        }

        importData.setOnClickListener{
            fileSearch()
        }

        requestActivation.setOnClickListener {
            activation()
        }
    }

    private fun activation() {
        progress?.showProgress(true, "Solicitando autorização")
        viewModel.requestAuthorization()
    }

    override fun onStart() {
        super.onStart()

        val account = accountManager.getUserAccount()
        if(account != null) {

            Glide.with(requireActivity())
                .load(account.photoUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(CircleCrop())
                .into(profileAvatar)

            profileName.text = account.displayName
            profileEmail.text = account.email

        }else{

            profileAvatar.setImageResource(R.drawable.ic_account)
            profileName.text = getString(R.string.desconnected_profile_name)
            profileEmail.text = ""
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is IProgressLoading) {
            progress = context
        } else {
            throw RuntimeException("$context must implement IProgressLoading")
        }

        if (context is IShowMessage) {
            showMessage = context
        } else {
            throw RuntimeException("$context must implement IShowMessage")
        }
    }

    override fun onDetach() {
        super.onDetach()
        progress = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)

        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                READ_REQUEST_CODE -> {
                    resultData?.data?.also { uri ->
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        if(inputStream != null){
                            progress?.showProgress(true,null)
                            viewModel.importData(inputStream)
                        }else {
                            showMessage?.showMessage(getString(R.string.no_data_to_import))
                        }
                    }
                }
                GOOGLE_SIGNIN_REQUEST_CODE -> try {

                    val task = GoogleSignIn.getSignedInAccountFromIntent(resultData)
                    val account = task.getResult(ApiException::class.java)
                    account?.run {
                       saveBackap()
                    }

                } catch (e: ApiException) {
                    Snackbar.make(container,"Falha de login: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    override fun onLogout(){
        progress?.showProgress(false,null)
        val intent = Intent(context, LoginActivity::class.java)
        intent.run {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("EXIT", true)
        }
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onShowMessageDriveAccessDenied() {
        AlertDialog.Builder(requireContext())
                .setCancelable(false)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.drive_access_denied))
                .setNeutralButton("Não autorizar") { dialog, _ ->
                    dialog.cancel()
                }
                .setPositiveButton("Autorizar") { dialog, which ->
                    dialog.cancel()
                    startActivityForResult(accountManager.signIntent(), GOOGLE_SIGNIN_REQUEST_CODE)
                }
                .show()
    }

    override fun onUpdateMessage(message: String) {
        progress?.showProgress(true, message)
    }

    override fun onCompleteProccess(success: Boolean, message: String?) {

        progress?.showProgress(false, null)
        showMessage?.run {
            if(!success && message != null) {
                showMessage(message)
            }
        }

        if(isLogoutWithSaveBackap){
            isLogoutWithSaveBackap = false
            continueLogout()
        }
    }

    override fun onRequestAuthorizationComplete(message: String) {
        progress?.showProgress(false,null)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.app_name))
            .setMessage(message) 
            .setNeutralButton("Ok") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun fileSearch() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(Intent.createChooser(intent,getString(R.string.select_file_message)), READ_REQUEST_CODE)
    }

    private fun continueLogout(){
        progress?.showProgress(true,null)
        viewModel.logout(this)
    }

    private fun saveBackap(){
        progress?.showProgress(true,null)
        viewModel.saveBackap()
    }

    companion object {
        const val READ_REQUEST_CODE: Int = 42
        const val GOOGLE_SIGNIN_REQUEST_CODE: Int = 101
    }
}