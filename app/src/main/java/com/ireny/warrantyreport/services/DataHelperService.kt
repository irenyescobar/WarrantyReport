package com.ireny.warrantyreport.services

import android.content.Context
import android.database.Cursor.*
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.data.room.WarrantyReportRoomDatabase
import com.ireny.warrantyreport.entities.WarrantReportData
import com.ireny.warrantyreport.services.interfaces.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DataHelperService(private val context: Context,
                        private val database: WarrantyReportRoomDatabase): IDataHelper {

    private val mimeTypeJson = "application/json"
    private val mimeTypePdf = "application/pdf"
    private val mimeTypeImage = "image/jpeg"
    private val mimeTypeFolder = "application/vnd.google-apps.folder"
    private var accessDenied:Boolean? = null

    override fun accessDriveIsDenied(): Boolean? {
        return accessDenied
    }

    override suspend fun import(data: WarrantReportData, importDataCompletedListener: ImportDataCompletedListener?) {

        withContext(Dispatchers.IO) {

            var success = true
            try {
                database.importDataDao().importData(data)
            }catch (err:Exception){
                success = false
            }

            importDataCompletedListener?.also {
                withContext(Dispatchers.Main){
                    it.onImportDataCompleted(success)
                }
            }
        }
    }

    override suspend fun export(listener: ExportDataCompletedListener?) {

        withContext(Dispatchers.IO) {

            var success = true
            var message: String

            try {

                val db = database.openHelper.readableDatabase
                var cursor =
                        db.query("SELECT name FROM  sqlite_master  WHERE  type ='table' AND name NOT LIKE 'sqlite_%' AND name NOT LIKE 'android_%' AND name NOT LIKE 'room_%'")

                val tables: ArrayList<String> = arrayListOf()

                while (!cursor.isClosed && cursor.moveToNext()) {
                    tables.add(cursor.getString(0))
                }

                cursor.close()

                val data = StringBuilder()
                data.appendln("{")

                val tableCount = tables.count()

                (0 until tableCount).map { tableIndex ->

                    val tableName = tables[tableIndex].decapitalize()

                    cursor = db.query("SELECT * FROM $tableName")

                    data.appendln(" \"${tableName}s\" : [")

                    val rowCount = cursor.count

                    (0 until cursor.count).map { rowIndex ->

                        if (!cursor.isClosed && cursor.moveToNext()) {

                            val columnCount = cursor.columnCount

                            data.appendln("  {")

                            (0 until columnCount).map { columnIndex ->

                                var value = ""

                                when (cursor.getType(columnIndex)) {

                                    FIELD_TYPE_NULL -> {
                                        value = "null"
                                    }
                                    FIELD_TYPE_STRING -> {
                                        value = "\"${cursor.getString(columnIndex)}\""
                                    }
                                    FIELD_TYPE_INTEGER -> {
                                        value = "${cursor.getLong(columnIndex)}"
                                    }
                                    FIELD_TYPE_FLOAT -> {
                                        value = "${cursor.getFloat(columnIndex)}"
                                    }
                                    FIELD_TYPE_BLOB -> {
                                        value = "${cursor.getBlob(columnIndex)}"
                                    }
                                }

                                data.appendln("   \"${cursor.getColumnName(columnIndex)}\": $value ${if (columnIndex < (columnCount - 1)) "," else ""}")
                            }

                            data.appendln("  }${if (rowIndex < (rowCount - 1)) "," else ""}")
                        }
                    }

                    data.appendln(" ]${if (tableIndex < (tableCount - 1)) "," else ""}")

                    cursor.close()
                }

                data.appendln("}")

                message = data.toString()

            }catch (err:Exception){
                success = false
                message = err.localizedMessage?:"Ocorreu um erro ao exportar os dados"
            }

            listener?.also {
                withContext(Dispatchers.Main){
                    it.onExportDataCompleted(success,message)
                }
            }
        }
    }

    override suspend fun uploadBackap(exportedDatabase: String,
                                      localFilesDir: String,
                                      drive: Drive,
                                      uploadBackapListener: UploadBackapListener?,
                                      driveAccessAuthDeniedListener: DriveAccessAuthDeniedListener?) {

        withContext(Dispatchers.IO) {

            accessDenied = null
            var databaseOk = false
            var filesOk = false
            val messages: ArrayList<String> = arrayListOf()

            val folderName = "${drive.applicationName} App"
            val folderFilesName =  "files-${drive.applicationName}"
            val fileName = "db-${drive.applicationName}.json"

            try {

                val folderRootId = addFolderRoot(folderName, drive)
                uploadDatabase(folderRootId, fileName, exportedDatabase, drive)

                databaseOk = true
                messages.add(context.getString(R.string.upload_database_success))
                uploadBackapListener?.also {
                    withContext(Dispatchers.Main) {
                        it.onUploadBackapDataCompleted(databaseOk)
                    }
                }

                val folderFilesId = addFolderFilesRoot(folderFilesName, folderRootId, drive)

                val folderReports = File(localFilesDir)
                if (folderReports.exists() && folderReports.isDirectory) {

                    folderReports.walk()
                            .filter { it.isDirectory && it.nameWithoutExtension != folderReports.nameWithoutExtension }
                            .forEach { folder ->

                                val parentId = addFolder(folder, folderFilesId, drive)

                                folder.walk()
                                        .filter { it.isFile }
                                        .forEach { file ->
                                            uploadFile(file, parentId, drive)
                                        }
                            }
                }

                filesOk = true
                messages.add(context.getString(R.string.upload_files_success))
                uploadBackapListener?.also {
                    withContext(Dispatchers.Main) {
                        it.onUploadBackapFilesCompleted(filesOk)
                    }
                }

            }catch (err: UserRecoverableAuthIOException){
                accessDenied = true
                driveAccessAuthDeniedListener?.also {
                    withContext(Dispatchers.Main) {
                        it.onDriveAccessAuthDenied()
                    }
                }

            }catch (err: Exception) {
                messages.add(err.localizedMessage ?: context.getString(R.string.upload_backap_error_message))
            } 

            uploadBackapListener?.also {
                withContext(Dispatchers.Main) {
                    it.onUploadBackapCompleted(databaseOk && filesOk, messages)
                }
            }
        }
    }

    override suspend fun downloadBackap(localFilesDir:String,
                                        drive: Drive,
                                        downloadBackapListener: DownloadBackapListener?,
                                        driveAccessAuthDeniedListener: DriveAccessAuthDeniedListener?) {

        withContext(Dispatchers.IO) {

            accessDenied = null

            var databaseOk = false
            var filesOk = false
            val messages: ArrayList<String> = arrayListOf()

            val driveFolderRootName = "${drive.applicationName} App"
            val driveFolderFilesName=  "files-${drive.applicationName}"
            val dbFileName = "db-${drive.applicationName}.json"

            try {

                val folders = drive.files().list()
                        .setQ("name = '$driveFolderRootName' and mimeType ='$mimeTypeFolder'")
                        .setSpaces("drive")
                        .setFields("files(id,name)")
                        .execute()

                val appFolder = folders.files.firstOrNull() { el -> el.name == driveFolderRootName }

                if (appFolder != null) {

                    val data = downloadDatabase(dbFileName, appFolder.id, drive)
                    databaseOk = true
                    messages.add("Download da base de dados finalizado.")
                    downloadBackapListener?.also {
                        withContext(Dispatchers.Main) {
                            it.onDownloadBackapDataCompleted(databaseOk, data)
                        }
                    } 
                    
                    downloadFiles(localFilesDir, driveFolderFilesName, appFolder.id, drive)
                    filesOk = true
                    messages.add("Download dos arquivos finalizado.")
                    downloadBackapListener?.also {
                        withContext(Dispatchers.Main) {
                            it.onDownloadBackapFilesCompleted(filesOk)
                        }
                    }

                }

            }catch (err: UserRecoverableAuthIOException){
                accessDenied = true
                driveAccessAuthDeniedListener?.also {
                    withContext(Dispatchers.Main) {
                        it.onDriveAccessAuthDenied()
                    }
                }
            }
            catch (err: Exception) {
                messages.add(err.localizedMessage ?: "Ocorreu um erro ao fazer o download do backap.")
            }

            downloadBackapListener?.also {
                withContext(Dispatchers.Main) {
                    it.onDownloadBackapCompleted(databaseOk && filesOk, messages)
                }
            }
        }
    }

    private fun downloadDatabase(dbFileName:String,parentId: String, drive:Drive):String? {

        val files = drive.files().list()
            .setQ("name = '$dbFileName' and mimeType ='$mimeTypeJson' and '${parentId}' in parents")
            .setSpaces("drive")
            .setFields("files(id,name)")
            .execute()

        if (files.files.size > 0) {

            val fileId = files.files[0].id
            val outputStream = ByteArrayOutputStream()
            drive.files().get(fileId).executeMediaAndDownloadTo(outputStream)
            return String(outputStream.toByteArray())
        }

        return null
    }

    private fun downloadFiles(localFilesDir: String,
                              driveFolderFilesName: String,
                              parentId: String,
                              drive:Drive) {

        val searchFolderFilesRoot = drive.files().list()
            .setQ("'${parentId}' in parents")
            .setSpaces("drive")
            .setFields("files(id,name)")
            .execute()

        val folderFilesRoot = searchFolderFilesRoot.files.firstOrNull { el -> el.name == driveFolderFilesName }

        if(folderFilesRoot != null) {

            var pageToken: String? = null

            do {
                val driveFilesFolders = drive.files().list()
                        .setQ("mimeType ='$mimeTypeFolder' and '${folderFilesRoot.id}' in parents")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id,name)")
                        .setPageToken(pageToken)
                        .execute()

                for (dir in driveFilesFolders.files) {

                    val localDir = File("$localFilesDir/${dir.name}")

                    if (!localDir.exists()) {
                        localDir.mkdirs()
                    }

                    val drivefiles = drive.files().list()
                            .setQ("'${dir.id}' in parents")
                            .setSpaces("drive")
                            .setFields("files(id,name)")
                            .execute()

                    for (file in drivefiles.files) {
                        val fileId = file.id
                        val outputStream = ByteArrayOutputStream()
                        drive.files().get(fileId).executeMediaAndDownloadTo(outputStream)

                        val newFile = File(localDir, file.name)
                        newFile.writeBytes(outputStream.toByteArray())
                        outputStream.close()
                    }
                }

                pageToken = driveFilesFolders.nextPageToken

            } while (pageToken != null)
        }
    }

    override suspend fun clearData() {
        try {
            database.importDataDao().clear()
        }catch (err:Exception){}
    }

    override suspend fun removeFiles(localFilesDir: String) {
        try {
            val file = File(localFilesDir)
            if(file.exists()){
                if(file.parentFile != null) {
                    file.parentFile?.deleteRecursively()
                }else{
                    file.deleteRecursively()
                }
            }

        }catch (err:Exception){}
    }

    private fun addFolderRoot(folderName:String, drive:Drive): String {

        val folders = drive.files().list()
            .setQ("name = '$folderName' and mimeType ='$mimeTypeFolder'")
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()

        val appFolder = folders.files.find { el -> el.name == folderName }

        if (appFolder != null) {
            return appFolder.id
        } else {

            val mtFolder = com.google.api.services.drive.model.File()
                .setMimeType(mimeTypeFolder)
                .setName(folderName)

            val googleFolder = drive
                .files()
                .create(mtFolder)
                .setFields("id")
                .execute()

            return googleFolder.id
        }
    }

    private fun addFolderFilesRoot(folderName:String, parentId:String, drive:Drive): String {

        val folders = drive.files().list()
            .setQ("name = '$folderName' and mimeType ='$mimeTypeFolder'")
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()

        folders.files.forEach {
            drive.files().delete(it.id).execute()
        }

        val folder = com.google.api.services.drive.model.File()
            .setParents(listOf(parentId))
            .setMimeType(mimeTypeFolder)
            .setName(folderName)

        val googleFolder = drive
            .files()
            .create(folder)
            .setFields("id, parents")
            .execute()

        return googleFolder.id
    }

    private fun addFolder(file:File, parentId:String, drive:Drive): String {

        val folder = com.google.api.services.drive.model.File()
            .setParents(listOf(parentId))
            .setMimeType(mimeTypeFolder)
            .setName(file.nameWithoutExtension)

        val googleFolder = drive
            .files()
            .create(folder)
            .setFields("id, parents")
            .execute()

        return googleFolder.id
    }

    private fun uploadFile(file:File, parentId:String, drive:Drive) {

        val mimeType = if(file.extension == "pdf" ) mimeTypePdf else mimeTypeImage
        val stream = FileInputStream(file)
        val content = InputStreamContent(mimeType, stream)

        val fileContent = com.google.api.services.drive.model.File()
            .setParents(listOf(parentId))
            .setMimeType(mimeType)
            .setName(file.name)

        drive.files()
            .create(fileContent, content)
            .setFields("id, parents")
            .execute()
    }

    private fun uploadDatabase(parentId:String, fileName:String, data:String, drive:Drive) {

        val files = drive.files().list()
            .setQ("name = '$fileName' and mimeType ='$mimeTypeJson' and '${parentId}' in parents")
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute()

        files.files.forEach {
            drive.files().delete(it.id).execute()
        }

        val tempFile = File.createTempFile("temp", "database")
        val fileOutputStream = FileOutputStream(tempFile, true)
        fileOutputStream.write(data.toByteArray())
        fileOutputStream.close()

        val targetStream = FileInputStream(tempFile)
        val inputStreamContent = InputStreamContent(mimeTypeJson, targetStream)

        val mtFile = com.google.api.services.drive.model.File()
            .setParents(listOf(parentId))
            .setMimeType(mimeTypeJson)
            .setName(fileName)

        drive.files()
            .create(mtFile, inputStreamContent)
            .setFields("id, parents")
            .execute()

        tempFile.delete()

    }

}