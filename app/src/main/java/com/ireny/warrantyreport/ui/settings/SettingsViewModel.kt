package com.ireny.warrantyreport.ui.settings

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.data.retrofit.Api
import com.ireny.warrantyreport.data.retrofit.RequestAuthResult
import com.ireny.warrantyreport.entities.WarrantReportData
import com.ireny.warrantyreport.observers.IObserver
import com.ireny.warrantyreport.observers.IResult
import com.ireny.warrantyreport.observers.Subject
import com.ireny.warrantyreport.services.AuthorizationService
import com.ireny.warrantyreport.services.IReportDirectoryManager
import com.ireny.warrantyreport.services.interfaces.*
import com.ireny.warrantyreport.utils.toWarrantReportData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class SettingsViewModel(application: Application,
                        private val context: Context,
                        private val accountManager: IUserAccountManager,
                        private val dataHelper: IDataHelper,
                        private val directoryManager: IReportDirectoryManager,
                        private val listener:Listener?,
                        api: Api?,
                        private val requestAuthorizationListener: RequestAuthorizationListener?): AndroidViewModel(application),
        ImportDataCompletedListener,
        ExportDataCompletedListener,
        UploadBackapListener,
        DownloadBackapListener,
        DriveAccessAuthDeniedListener {


    @SuppressLint("HardwareIds")
    private val keyApp: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    private val subject = Subject()
    private var authService: AuthorizationService? = null
    private val observer = object : IObserver {
        override fun completed(result: IResult) {
            subject.remove(this)
            if(result.success){
                val data: RequestAuthResult = result.data as RequestAuthResult
                if(!data.active && data.code != "") {
                    requestAuthorizationListener?.onRequestAuthorizationComplete("${data.message}. Código da solicitação: ${data.code}")
                }else{
                    requestAuthorizationListener?.onRequestAuthorizationComplete(data.message)
                }
            }else{
                requestAuthorizationListener?.onRequestAuthorizationComplete(if(result.message != null) result.message!! else "Ocorreu um problema.")
            }
        }
    }

    init {
        api?.run {
            authService = AuthorizationService(this,accountManager)
            authService?.addSubject(subject)
        }
    }

    fun requestAuthorization(){
        authService?.run {
            viewModelScope.launch {
                subject.add(observer)
                request(keyApp)
            }
        }
    }

    override fun onDriveAccessAuthDenied() {
        viewModelScope.launch(Dispatchers.IO) {
            accessDenied()
        }
    }

    override fun onDownloadBackapDataCompleted(success: Boolean, data: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (success) {
                if (data != null) {
                    updateMessage(context.getString(R.string.download_database_success))
                    val dbObject = data.toWarrantReportData()
                    import(dbObject)
                } else {
                    updateMessage(context.getString(R.string.download_backap_success_no_data))
                }
                updateMessage(context.getString(R.string.default_message_progress))
            }
        }
    }

    override fun onDownloadBackapFilesCompleted(success: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success) {
                updateMessage(context.getString(R.string.download_files_success))
            }else{
                updateMessage(context.getString(R.string.download_files_error))
            }
            updateMessage(context.getString(R.string.default_message_progress))
        }
    }

    override fun onDownloadBackapCompleted(success: Boolean, messages: ArrayList<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success){
                updateMessage(context.getString(R.string.download_backap_success))
                complete(true, null)
            }else {
                complete(false, context.getString(R.string.download_backap_error))
            }
        }
    }

    override fun onUploadBackapCompleted(success: Boolean, messages: ArrayList<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success) {
                updateMessage(context.getString(R.string.upload_backap_success))
                complete(true, null)
            }else{
                complete(false,context.getString(R.string.upload_backap_error))
            }
        }
    }

    override fun onUploadBackapFilesCompleted(success: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success) {
                updateMessage(context.getString(R.string.upload_files_success))
            }else{
                updateMessage(context.getString(R.string.upload_files_error))
            }
            updateMessage(context.getString(R.string.default_message_progress))
        }
    }

    override fun onUploadBackapDataCompleted(success: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (success) {
                updateMessage(context.getString(R.string.upload_database_success))
            }else{
                updateMessage(context.getString(R.string.upload_database_error))
            }
            updateMessage(context.getString(R.string.default_message_progress))
        }
    }

    override fun onImportDataCompleted(success: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success) {
                updateMessage(context.getString(R.string.import_data_success))
            }else{
                updateMessage(context.getString(R.string.import_data_error))
            }
            updateMessage(context.getString(R.string.default_message_progress))
        }
    }

    override fun onExportDataCompleted(success: Boolean, exportedDatabase: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success) {
                updateMessage(context.getString(R.string.upload_backap_start))

                val drive = accountManager.getGoogleDriveService()
                if(drive != null) {
                     dataHelper.uploadBackap(
                             exportedDatabase,
                             directoryManager.getRootDiretory(),
                             drive,
                             this@SettingsViewModel,
                             this@SettingsViewModel)
                }else{
                    complete(false,context.getString(R.string.drive_access_error) )
                }

            }else{
                complete(false,context.getString(R.string.export_data_error))
            }
        }
    }

    fun logout(logoutListener:LogoutListener?){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                dataHelper.clearData()
                dataHelper.removeFiles(directoryManager.getRootDiretory())
            } catch (er: Exception) {

            }

            accountManager.signOut(object : CompleteListener {
                override fun onComplete(success: Boolean, message: String?) {
                    logoutListener?.onLogout()
                }
            })
        }
    }

    fun saveBackap() {
        viewModelScope.launch(Dispatchers.IO) {
            dataHelper.export(this@SettingsViewModel)
        }
    }

    fun restoreBackap() {
        viewModelScope.launch(Dispatchers.IO) {
            updateMessage(context.getString(R.string.search_backap_data_message))
            val drive = accountManager.getGoogleDriveService()
            if(drive != null) {
                dataHelper.downloadBackap(
                         directoryManager.getRootDiretory(),
                         drive,
                        this@SettingsViewModel,
                        this@SettingsViewModel)
            }else{
                complete(false,context.getString(R.string.drive_access_error))
            }
        }
    }

    fun importData(inputStream: InputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            val dbObject = inputStream.toWarrantReportData()
            import(dbObject)
        }
    }

    fun importRawData(){
        viewModelScope.launch(Dispatchers.IO) {
            val databaseInputStream = context.resources.openRawResource(R.raw.data)
            val dbObject = databaseInputStream.toWarrantReportData()
            import(dbObject)
        }
    }

    fun accessDriveIsDenied():Boolean?{
        return dataHelper.accessDriveIsDenied()
    }

    private suspend fun import(dbObject: WarrantReportData?){
        if (dbObject != null) {
            dataHelper.import(dbObject, this@SettingsViewModel)
        } else {
            updateMessage(context.getString(R.string.import_data_format_file_error))
        }
    }

    private suspend fun updateMessage(message:String){
        listener?.run {
            withContext(Dispatchers.Main){
                onUpdateMessage(message)
            }
            Thread.sleep(1000)
        }
    }

    private suspend fun complete(success: Boolean,message:String?){
        listener?.run {
            withContext(Dispatchers.Main){
                onCompleteProccess(success,message)
            }
        }
    }

    private suspend fun accessDenied(){
        listener?.run {
            withContext(Dispatchers.Main){
                onShowMessageDriveAccessDenied()
            }
        }
    }

    interface Listener {
        fun onUpdateMessage(message:String)
        fun onCompleteProccess(success: Boolean, message:String?)
        fun onShowMessageDriveAccessDenied()
    }

    interface LogoutListener {
        fun onLogout()
    }

    interface RequestAuthorizationListener {
        fun onRequestAuthorizationComplete(message: String)
    }

    companion object{

        class Factory(private val application: Application,
                      private val context: Context,
                      private val accountManager: IUserAccountManager,
                      private val dataHelper: IDataHelper,
                      private val directoryManager: IReportDirectoryManager,
                      private val listener:Listener?,
                      private val api: Api?,
                      private val requestAuthorizationListener: RequestAuthorizationListener?
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SettingsViewModel(application, context, accountManager,dataHelper,directoryManager,listener, api, requestAuthorizationListener) as T
            }
        }
    }
}