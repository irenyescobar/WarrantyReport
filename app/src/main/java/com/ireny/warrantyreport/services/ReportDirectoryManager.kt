package com.ireny.warrantyreport.ui.report.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.util.Log
import android.widget.Toast
import com.ireny.warrantyreport.MyWarrantReportApp
import com.ireny.warrantyreport.services.UserAccountManager
import com.ireny.warrantyreport.ui.report.photos.PhotosFragment
import com.ireny.warrantyreport.utils.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ReportDirectoryManager(private val context: Context): IReportDirectoryManager {

    private val component by lazy { (MyWarrantReportApp.applicationContext() as MyWarrantReportApp).component }
    private val accountMannager: UserAccountManager by lazy { component.userAccountManager() }

    private var userId:String

    init {
        val account = accountMannager.getUserAccount() ?: throw Exception("Usuário não conectado!")
        userId = account.id?: throw Exception("Usuário não identificado!")
    }

    private val data: Array<PhotosFragment.Photo> = arrayOf(
        PhotosFragment.Photo(0),
        PhotosFragment.Photo(1),
        PhotosFragment.Photo(2),
        PhotosFragment.Photo(3)
    )

    //private val dir = File("${Environment.getExternalStorageDirectory()}${Constants.REPORTS_DIRECTORY}/$userId")
    private val dir = File("${context.getExternalFilesDir(null)}${Constants.REPORTS_DIRECTORY}/$userId")

    private fun getImage(reportId: Long,photoId: Int): Drawable?{
        val path = "${getPath(reportId)}photo_${photoId}.jpg"
        return Drawable.createFromPath(path)
    }

    override fun getFile(reportId: Long,photoId: Int): File?{
        val path = "${getPath(reportId)}photo_${photoId}.jpg"
        val f =  File(path)
        if(f.exists()){
            return f
        }
        return null
    }

    override fun getImages(reportId: Long): Array<PhotosFragment.Photo> {
        refreshData(reportId)
        return data
    }

    private fun refreshData(reportId: Long){
        data.forEach {
            it.image = getImage(reportId,it.id)
        }
    }

    override fun saveImage(myBitmap: Bitmap, photoId:Int, reportId:Long) {

        if(reportId == 0L) {
            throw Exception("Laudo não identificado: reportId = $reportId")
        }

        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        val reportPath = getPath(reportId)
        val reportDir = File(reportPath)
        if (!reportDir.exists()) {
            reportDir.mkdirs()
        }

        try
        {
            val f = File(reportDir,  "photo_${photoId}.jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context,arrayOf(f.path),arrayOf("image/jpeg"), null)
            fo.close()
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    override fun removeImage(photoId: Int, reportId: Long) {
        val reportPath = getPath(reportId)
        val reportDir = File(reportPath)
        val f = File(reportDir,  "photo_${photoId}.jpg")
        f.delete()
    }

    override fun getReportFile(reportId: Long): File? {

        val targetPdf = "${getPath(reportId)}document.pdf"
        val file = File(targetPdf)
        if(file.exists()){
            return file
        }
        return null
    }

    override fun getReportDirectory(reportId: Long):String?{

        val path = getPath(reportId)
        val dir = File(path)

        if(dir.exists()){
            return path
        }

        return null
    }

    private fun getPath(reportId: Long):String {
        return "$dir/${reportId}/"
    }


    override fun saveFile(document: PdfDocument, reportId: Long): File? {
        val directoryPath = getPath(reportId)
        val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }

        val targetPdf = directoryPath + "document.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
        } catch (e: IOException) {
            Log.e("main", "error $e")
            Toast.makeText(context, "Something wrong: $e", Toast.LENGTH_LONG).show()
        }

        return getReportFile(reportId)
    }

    private fun removeImages(reportId: Long){
        data.forEach {
            removeImage(it.id,reportId)
        }
    }
}

interface IReportDirectoryManager{
    fun getImages(reportId: Long):Array<PhotosFragment.Photo>
    fun saveImage(myBitmap: Bitmap, photoId:Int, reportId:Long)
    fun removeImage(photoId:Int, reportId:Long)
    fun getReportDirectory(reportId: Long):String?
    fun getReportFile(reportId: Long):File?
    fun saveFile(document: PdfDocument, reportId: Long):File?
    fun getFile(reportId: Long,photoId: Int): File?
}