package com.ireny.warrantyreport.ui.settings

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.WarrantReportData
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
                        private val listener:Listener?): AndroidViewModel(application),
        ImportDataCompletedListener,
        ExportDataCompletedListener,
        UploadBackapDataCompletedListener,
        DownloadBackapDataCompletedListener {

    override fun onImportDataCompleted(success: Boolean, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success) {
                updateMessage(context.getString(R.string.import_data_success))
                complete(true,null)
            }else{
                val error = context.getString(R.string.import_data_error)
                updateMessage(error)
                complete(false,error)
            }
        }
    }

    override fun onDownloadBackapDataCompleted(success: Boolean, data: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            if (success) {
                if (data != null) {
                    updateMessage(context.getString(R.string.download_backap_success))
                    val dbObject = data.toWarrantReportData()
                    import(dbObject)
                } else {
                    updateMessage(context.getString(R.string.download_backap_success_no_data))
                    complete(false,null)
                }
            } else {
                complete(false, context.getString(R.string.download_backap_error))
            }
        }
    }

    override fun onExportDataCompleted(success: Boolean, data: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if(success) {
                updateMessage(context.getString(R.string.upload_backap_start))

                val drive = accountManager.getGoogleDriveService()
                if(drive != null) {
                     dataHelper.uploadBackap(data,drive,this@SettingsViewModel)
                }else{
                    complete(false,context.getString(R.string.drive_access_error) )
                }

            }else{
                complete(false,context.getString(R.string.export_data_error))
            }
        }
    }

    override fun onUploadBackapDataCompleted(success: Boolean, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (success) {
                updateMessage(context.getString(R.string.upload_backap_success))
                complete(true,null)
            } else {
                complete(false,context.getString(R.string.upload_backap_error))
            }
        }
    }

    fun clearDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            dataHelper.clearData()
        }
    }

    fun logout(logoutListener:LogoutListener?){
        accountManager.signOut(object : CompleteListener {
            override fun onComplete(success: Boolean, message: String?) {
                logoutListener?.onLogout()
            }
        })
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
                dataHelper.downloadBackap(drive,this@SettingsViewModel)
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

    private suspend fun import(dbObject: WarrantReportData?){
        if (dbObject != null) {
            dataHelper.import(dbObject, this@SettingsViewModel)
        } else {
            complete(false,context.getString(R.string.import_data_format_file_error))
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

    interface Listener {
        fun onUpdateMessage(message:String)
        fun onCompleteProccess(success: Boolean, message:String?)
    }

    interface LogoutListener {
        fun onLogout()
    }

    companion object{

        class Factory(private val application: Application,
                      private val context: Context,
                      private val accountManager: IUserAccountManager,
                      private val dataHelper: IDataHelper,
                      private val listener:Listener?
        )
            : ViewModelProvider.NewInstanceFactory(){

            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SettingsViewModel(application, context, accountManager,dataHelper,listener) as T
            }
        }
    }
}