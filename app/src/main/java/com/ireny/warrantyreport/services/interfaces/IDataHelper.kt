package com.ireny.warrantyreport.services.interfaces

import com.google.api.services.drive.Drive
import com.ireny.warrantyreport.entities.WarrantReportData


interface IDataHelper {
    suspend fun import(data: WarrantReportData,
                       importDataCompletedListener:ImportDataCompletedListener?)
    suspend fun export(listener: ExportDataCompletedListener?)
    suspend fun uploadBackap(exportedDatabase: String,
                             localFilesDir:String,
                             drive: Drive,
                             uploadBackapListener: UploadBackapListener?,
                             driveAccessAuthDeniedListener: DriveAccessAuthDeniedListener?)
    suspend fun downloadBackap(localFilesDir: String,
                               drive: Drive,
                               downloadBackapListener: DownloadBackapListener?,
                               driveAccessAuthDeniedListener: DriveAccessAuthDeniedListener?)
    suspend fun clearData()
    suspend fun removeFiles(localFilesDir: String)

    fun accessDriveIsDenied(): Boolean?
}

interface ImportDataCompletedListener {
    fun onImportDataCompleted(success:Boolean)
}

interface ExportDataCompletedListener {
    fun onExportDataCompleted(success:Boolean, exportedDatabase: String)
}

interface UploadBackapListener {
    fun onUploadBackapDataCompleted(success:Boolean)
    fun onUploadBackapFilesCompleted(success:Boolean)
    fun onUploadBackapCompleted(success:Boolean, messages: ArrayList<String>?)
}

interface DownloadBackapListener {
    fun onDownloadBackapDataCompleted(success:Boolean, data: String?)
    fun onDownloadBackapFilesCompleted(success:Boolean)
    fun onDownloadBackapCompleted(success:Boolean, messages: ArrayList<String>?)
}

interface DriveAccessAuthDeniedListener {
    fun onDriveAccessAuthDenied()
}
