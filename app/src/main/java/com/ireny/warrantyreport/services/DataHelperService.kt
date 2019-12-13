package com.ireny.warrantyreport.services

import android.database.Cursor.*
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.ireny.warrantyreport.data.room.WarrantyReportRoomDatabase
import com.ireny.warrantyreport.entities.WarrantReportData
import com.ireny.warrantyreport.services.interfaces.*
import kotlinx.coroutines.*
import java.io.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

class DataHelperService(private val database: WarrantyReportRoomDatabase): IDataHelper {

    override suspend fun import(data: WarrantReportData, listener: ImportDataCompletedListener?) {

        withContext(Dispatchers.IO) {

            var success = true
            var message = "Importação realizada com sucesso"

            try {
                database.importDataDao().importData(data)
            }catch (err:Exception){
                success = false
                message = err.localizedMessage?:"Ocorreu um erro ao importar os dados"
            }

            listener?.also {
                withContext(Dispatchers.Main){
                    it.onImportDataCompleted(success,message)
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

    override suspend fun uploadBackap(data:String, drive:Drive, listener: UploadBackapDataCompletedListener?) {

        withContext(Dispatchers.IO) {

            var success = true
            var message = "Backap salvo com sucesso"

            val folderName = "${drive.applicationName} App"
            val fileName = "db-${drive.applicationName}.json"
            val mimeTypeJson = "application/json"
            val mimeTypeFolder = "application/vnd.google-apps.folder"

            try {

                var folderId: String

                val folders = drive.files().list()
                        .setQ("name = '$folderName' and mimeType ='$mimeTypeFolder'")
                        .setSpaces("drive")
                        .setFields("files(id, name,size,createdTime,modifiedTime,starred)")
                        .execute()

                val appFolder = folders.files.find { el -> el.name == folderName }

                if (appFolder != null) {
                    folderId = appFolder.id
                } else {

                    val mtFolder = com.google.api.services.drive.model.File()
                            .setMimeType(mimeTypeFolder)
                            .setName(folderName)

                    val googleFolder = drive
                            .files()
                            .create(mtFolder)
                            .setFields("id")
                            .execute()

                    folderId = googleFolder.id
                }

                val files = drive.files().list()
                        .setQ("name = '$fileName' and mimeType ='$mimeTypeJson' and '${folderId}' in parents")
                        .setSpaces("drive")
                        .setFields("files(id, name,size,createdTime,modifiedTime,starred)")
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
                        .setParents(listOf(folderId))
                        .setMimeType(mimeTypeJson)
                        .setName(fileName)

                drive.files()
                        .create(mtFile, inputStreamContent)
                        .setFields("id, parents")
                        .execute()

                tempFile.delete()

            } catch (err: Exception) {
                success = false
                message = err.localizedMessage ?: "Ocorreu um erro ao salvar os dados no drive."
            }

            listener?.also {
                withContext(Dispatchers.Main) {
                    it.onUploadBackapDataCompleted(success, message)
                }
            }
        }
    }

    override suspend fun downloadBackap(drive: Drive, listener: DownloadBackapDataCompletedListener?) {

        withContext(Dispatchers.IO) {
            var success = true
            var data: String? = null

            val folderName = "${drive.applicationName} App"
            val fileName = "db-${drive.applicationName}.json"
            val mimeTypeJson = "application/json"
            val mimeTypeFolder = "application/vnd.google-apps.folder"

            try {

                val folders = drive.files().list()
                        .setQ("name = '$folderName' and mimeType ='$mimeTypeFolder'")
                        .setSpaces("drive")
                        .setFields("files(id, name,size,createdTime,modifiedTime,starred)")
                        .execute()

                val appFolder = folders.files.find { el -> el.name == folderName }

                if (appFolder != null) {

                    val files = drive.files().list()
                            .setQ("name = '$fileName' and mimeType ='$mimeTypeJson' and '${appFolder.id}' in parents")
                            .setSpaces("drive")
                            .setFields("files(id, name,size,createdTime,modifiedTime,starred)")
                            .execute()

                    if (files.files.size > 0) {

                        val fileId = files.files[0].id
                        val outputStream = ByteArrayOutputStream()
                        drive.files().get(fileId).executeMediaAndDownloadTo(outputStream)
                        data = String(outputStream.toByteArray())
                    }
                }
            } catch (err: Exception) {
                success = false
                data = err.localizedMessage ?: "Ocorreu um erro ao fazer o restoreBackap dos dados."
            }

            listener?.also {
                withContext(Dispatchers.Main) {
                    it.onDownloadBackapDataCompleted(success, data)
                }
            }
        }
    }

    override suspend fun clearData() {
        try {
            database.importDataDao().clear()
        }catch (err:Exception){

        }
    }

}