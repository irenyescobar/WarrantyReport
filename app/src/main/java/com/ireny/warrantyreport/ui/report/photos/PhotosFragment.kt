package com.ireny.warrantyreport.ui.report.photos

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.di.components.DaggerReportDirectoryComponent
import com.ireny.warrantyreport.di.components.ReportDirectoryComponent
import com.ireny.warrantyreport.di.modules.ReportDirectoryModule
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.services.IReportDirectoryManager
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import kotlinx.android.synthetic.main.report_photos_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.IOException


class PhotosFragment(private var reportId:Long) : FragmentBase(){

    private lateinit var component: ReportDirectoryComponent
    private val photoManager: IReportDirectoryManager by lazy { component.reportDirectoryManager()}
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
    }

    private fun initComponent() {
        component = DaggerReportDirectoryComponent
            .builder()
            .reportDirectoryModule(ReportDirectoryModule(requireContext()))
            .build()

        component.inject(requireContext())
    }

    private fun changePhoto(item: Photo) {
        currentPhoto = item
        showPictureDialog()
    }

    private fun removePhoto(item: Photo) {
        photoManager.removeImage(item.id,reportId)
        refresh()
    }

    override fun bindView(model: Report) {
        reportId = model.id
        refresh()
        previewButtonFunction?.showPreviewButton(true)
    }

    private fun refresh() {
        val data = photoManager.getImages(reportId)
        photo1.setImage(data[0])
        photo2.setImage(data[1])
        photo3.setImage(data[2])
        photo4.setImage(data[3])
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_CAMERA)
    private fun proceedTakePhotoAfterPermission() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun checkPermissionFromCameraAndTakePhoto() {
        EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                        this,
                        REQUEST_PERMISSION_CAMERA,
                        Manifest.permission.CAMERA)
                        .setRationale(R.string.permission_camera_rationale_message)
                        .build()

        )
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireContext())
        val pictureDialogItems = arrayOf(getString(R.string.snackbar_option_gallery_text), getString(R.string.snackbar_option_camera_text))
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> checkPermissionFromCameraAndTakePhoto()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
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
            refresh()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults )

        EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                this )
    }

    private fun ImageView.setImage(photo: Photo){
        this.tag = photo
        if(photo.image != null) {
            this.setImageDrawable(photo.image)
        }else{
            this.setImageResource(R.drawable.ic_quadro)
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
        private const val GALLERY = 1
        private const val CAMERA = 2

        @JvmStatic
        fun newInstance(reportId:Long) = PhotosFragment(reportId)
    }
}
