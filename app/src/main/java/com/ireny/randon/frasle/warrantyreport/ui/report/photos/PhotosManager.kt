package com.ireny.randon.frasle.warrantyreport.ui.report.photos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Environment
import com.ireny.randon.frasle.warrantyreport.utils.Constants
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PhotosManager(private val context: Context):
    IPhotosManager {

    private val data: Array<PhotosFragment.Photo> = arrayOf(
        PhotosFragment.Photo(0),
        PhotosFragment.Photo(1),
        PhotosFragment.Photo(2),
        PhotosFragment.Photo(3)
    )

    private val dir = File("${Environment.getExternalStorageDirectory()}${Constants.REPORTS_DIRECTORY}")

    private fun getImage(reportId: Long,photoId: Int): Drawable?{
        val path = "$dir/${reportId}/photo_${photoId}.jpg"
        return Drawable.createFromPath(path)
    }

    override fun getData(reportId: Long): Array<PhotosFragment.Photo> {
        refreshData(reportId)
        return data
    }

    private fun refreshData(reportId: Long){
        data.forEach {
            it.image = getImage(reportId,it.id)
        }
    }

    override fun saveImage(myBitmap: Bitmap, photoId:Int, reportId:Long) {

        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        val reportPath = "$dir/${reportId}"
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
        val reportPath = "$dir/${reportId}"
        val reportDir = File(reportPath)
        val f = File(reportDir,  "photo_${photoId}.jpg")
        f.delete()
    }

}

interface IPhotosManager{
    fun getData(reportId: Long):Array<PhotosFragment.Photo>
    fun saveImage(myBitmap: Bitmap, photoId:Int, reportId:Long)
    fun removeImage(photoId:Int, reportId:Long)
}