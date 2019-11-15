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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.di.components.DaggerPhotosComponent
import com.ireny.warrantyreport.di.components.PhotosComponent
import com.ireny.warrantyreport.di.modules.PhotosModule
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.listeners.ItemClickListener
import com.ireny.warrantyreport.ui.listeners.ItemLongClickListener
import com.ireny.warrantyreport.ui.report.base.FragmentBase
import com.ireny.warrantyreport.ui.report.services.IPhotosManager
import com.ireny.warrantyreport.utils.reportActivity
import java.io.IOException


class PhotosFragment(val reportId:Long) : FragmentBase(),  ItemClickListener<PhotosFragment.Photo> , ItemLongClickListener<PhotosFragment.Photo>{

    private lateinit var component: PhotosComponent
    private val photoManager: IPhotosManager by lazy { component.photoManager()}
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PhotoRecyclerViewAdapter
    private var currentPhoto: Photo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        reportActivity.supportActionBar?.apply {
            title = "Fotos"
        }
        val view = inflater.inflate(R.layout.report_photos_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager =  GridLayoutManager(activity, 2)
        adapter = PhotoRecyclerViewAdapter(this,this)
        recyclerView.adapter = adapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkPermissionFromStorage()
    }

    override fun onClicked(item: Photo) {
        changePhoto(item)
    }

    override fun onLongClicked(item: Photo) {
        removePhoto(item)
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

    override fun refresh(entity: Report) {
        refresh(entity.id)
    }

    private fun refresh(reportId: Long) {
        val data = photoManager.getData(reportId)
        adapter.refresh(data)
    }

    override fun updateReport(entity: Report) {  }

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
