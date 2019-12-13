package com.ireny.warrantyreport.services.interfaces

import com.google.api.services.drive.Drive
import com.ireny.warrantyreport.entities.WarrantReportData


interface IDataHelper {
    suspend fun import(data: WarrantReportData, listener:ImportDataCompletedListener?)
    suspend fun export(listener: ExportDataCompletedListener?)
    suspend fun uploadBackap(data: String, drive: Drive, listener: UploadBackapDataCompletedListener?)
    suspend fun downloadBackap(drive: Drive, listener: DownloadBackapDataCompletedListener?)
    suspend fun clearData()
}

interface ImportDataCompletedListener {
    fun onImportDataCompleted(success:Boolean, message: String)
}

interface ExportDataCompletedListener {
    fun onExportDataCompleted(success:Boolean, data: String)
}

interface UploadBackapDataCompletedListener {
    fun onUploadBackapDataCompleted(success:Boolean, message: String)
}

interface DownloadBackapDataCompletedListener {
    fun onDownloadBackapDataCompleted(success:Boolean, data: String?)
}