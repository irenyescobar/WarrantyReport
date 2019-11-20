package com.ireny.warrantyreport.ui.report.photos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.di.components.DaggerPhotosComponent
import com.ireny.warrantyreport.di.components.PhotosComponent
import com.ireny.warrantyreport.di.modules.PhotosModule
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import com.ireny.warrantyreport.ui.report.services.IReportDirectoryManager
import kotlinx.android.synthetic.main.report_photos_fragment.*
import java.io.IOException


class PhotosFragment(val reportId:Long) : FragmentBase(){

    private lateinit var component: PhotosComponent
    private val photoManager: IReportDirectoryManager by lazy { component.photoManager()}
    private var currentPhoto: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.report_photos_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        photo1.setup()
        photo2.setup()
        photo3.setup()
        photo4.setup()
        checkPermissionFromStorage()
    }

    private fun initComponent() {
        component = DaggerPhotosComponent
            .builder()
            .photosModule(PhotosModule(requireContext()))
            .build()

        component.inject(requireContext())
    }

    private fun changePhoto(item: Photo) {
        currentPhoto = item
        showPictureDialog()
    }

    private fun removePhoto(item: Photo) {
        photoManager.removeImage(item.id,reportId)
        refresh(reportId)
    }

    override fun bindView(model: Report) {
        refresh(model.id)
        previewButtonFunction?.showPreviewButton(true)
    }

    private fun refresh(reportId: Long) {
        val data = photoManager.getData(reportId)
        photo1.setImage(data[0])
        photo2.setImage(data[1])
        photo3.setImage(data[2])
        photo4.setImage(data[3])
    }

    private fun proceedTakePhotoAfterPermission() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun checkPermissionFromCameraAndTakePhoto() {
        val permissionCamera = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
        }else{
            proceedTakePhotoAfterPermission()
        }
    }

    private fun checkPermissionFromStorage() {
        val read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (read != PackageManager.PERMISSION_GRANTED || write != PackageManager.PERMISSION_GRANTED) {

             requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_STORAGE)

        }else{
            refresh(reportId)
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        val pictureDialogItems = arrayOf(getString(R.string.snackbar_option_gallery_text), getString(R.string.snackbar_option_camera_text))
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> checkPermissionFromCameraAndTakePhoto()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap? = null

        data?.let {
            try {
                if (requestCode == GALLERY) {
                    val contentURI = it.data
                    bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, contentURI)
                }
                else if (requestCode == CAMERA){
                    it.extras?.run {
                        bitmap = get("data") as Bitmap
                    }
                }
            } catch (e: IOException) {      }
        }

        currentPhoto?.run {
            bitmap?.let {
                photoManager.saveImage(it,id,reportId)
            }
            refresh(reportId)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {

            var allgranted = false
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true
                } else {
                    allgranted = false
                    break
                }
            }

            when (requestCode) {
                REQUEST_PERMISSION_CAMERA -> {
                    if(allgranted){
                        proceedTakePhotoAfterPermission()
                    }
                }
                REQUEST_PERMISSION_STORAGE -> {
                    if(allgranted){
                        refresh(reportId)
                    }
                }
            }
        }
    }

    private fun ImageView.setImage(photo: Photo){
        this.tag = photo
        if(photo.image != null) {
            this.setImageDrawable(photo.image)
        }else{
            this.setImageResource(R.drawable.ic_photo_size_select_actual_black_24dp)
        }
    }

    private fun ImageView.setup(){
        this.setOnClickListener{
            val item = it.tag
            if(item is Photo){
                changePhoto(item)
            }
        }

        this.setOnLongClickListener{
            val item = it.tag
            if(item is Photo){
                removePhoto(item)
            }
            true
        }
    }

    data class Photo( var id:Int, var image:Drawable? = null)

    companion object {

        private const val REQUEST_PERMISSION_CAMERA= 202
        private const val REQUEST_PERMISSION_STORAGE= 203
        private const val GALLERY = 1
        private const val CAMERA = 2

        @JvmStatic
        fun newInstance(reportId:Long) = PhotosFragment(reportId)
    }
}
