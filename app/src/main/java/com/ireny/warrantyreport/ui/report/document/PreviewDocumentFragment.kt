package com.ireny.warrantyreport.ui.report.document

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.di.components.DaggerReportDirectoryComponent
import com.ireny.warrantyreport.di.components.ReportDirectoryComponent
import com.ireny.warrantyreport.di.modules.ReportDirectoryModule
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.report.interfaces.IBindView
import com.ireny.warrantyreport.ui.report.interfaces.ICreateDocument
import com.ireny.warrantyreport.ui.report.services.IReportDirectoryManager
import com.ireny.warrantyreport.utils.Constants
import com.ireny.warrantyreport.utils.toDateTextFormatted
import kotlinx.android.synthetic.main.report_preview_document_fragment.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class PreviewDocumentFragment : Fragment(), ICreateDocument<Report> ,IBindView<Report>{

    private lateinit var component: ReportDirectoryComponent
    private val directoryManager: IReportDirectoryManager by lazy { component.reportDirectoryManager()}
    private var report: Report? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(com.ireny.warrantyreport.R.layout.report_preview_document_fragment, container, false)
    }

    override fun bindView(model: Report) {
        report = model

        val read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        val write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (read != PackageManager.PERMISSION_GRANTED || write != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_STORAGE
            )

        }else{
            showReportScreen(model)
        }
    }

    override fun createDocument(model: Report) {
        createPdf(model.id)
        showDocument(model.id)
    }

    private fun initComponent() {
        component = DaggerReportDirectoryComponent
            .builder()
            .reportDirectoryModule(ReportDirectoryModule(requireContext()))
            .build()

        component.inject(requireContext())
    }

    private fun showReportScreen(entity: Report){

        entity.company?.run {
            text_company_value.text = description
        }

        text_invoiceDate_value.text = if(entity.invoiceDate != null) entity.invoiceDate?.toDateTextFormatted() else ""
        text_applicationDate_value.text = if(entity.applicationDate != null) entity.applicationDate?.toDateTextFormatted() else ""
        text_text_warrantyDate_value.text = if(entity.warrantyDate != null) entity.warrantyDate?.toDateTextFormatted() else ""
        text_analysisDate_value.text = if(entity.analysisDate != null) entity.analysisDate?.toDateTextFormatted() else ""

        if(entity.sourceInvoice != null){
            text_sourceInvoice_value.text = entity.sourceInvoice.toString()
        }else{
            text_sourceInvoice_value.text = ""
        }

        var aux = ""
        entity.tecnicalAdvices.forEach{
            aux += "${it.description}  "
        }

        entity.run {
            text_distributor_value.text = distributor
            text_cityState_value.text = cityState
            text_client_value.text = client
            text_partReference_value.text = partReference
            text_reasonUnfounded_value.text = reasonUnfounded
            text_comments_value.text = comments
            text_technicalConsultant_value.text = technicalConsultant
            text_technicalConsultantContact_value.text = technicalConsultantContact
            text_TechnicalAdvice_value.text = aux
        }

        val data = photoManager.getData(entity.id)
        photo1.setImageDrawable(data[0].image)
        photo2.setImageDrawable(data[1].image)
        photo3.setImageDrawable(data[2].image)
        photo4.setImageDrawable(data[3].image)
    }

    private fun createBitmapFromView(view: View, width: Int, height: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888 )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun showDocument(reportId: Long) {

        val directoryPath = getDirectory(reportId)

        val targetPdf = directoryPath + "document.pdf"
        val file = File(targetPdf)

        val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

        if (parcelFileDescriptor != null) {

            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val page = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width,page.height,Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            container.visibility = View.GONE
            img.visibility = View.VISIBLE
            img.setImageBitmap(bitmap)

            page.close()
            pdfRenderer.close()
        }
    }

    private fun createPdf(reportId: Long) {

        val displaymetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displaymetrics)
        val hight = displaymetrics.heightPixels.toFloat()
        val width = displaymetrics.widthPixels.toFloat()
        val convertHighet = hight.toInt()
        val convertWidth = width.toInt()

        var bitmap = createBitmapFromView(container,container.width,container.height)
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true)

        val document = PdfDocument()
        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)

        val canvas = page.canvas
        canvas.drawBitmap(bitmap, 0f, 0f , null)

        document.finishPage(page)

        saveFile(document,reportId)

        document.close()
    }

    private fun saveFile(document: PdfDocument, reportId: Long){

        val directoryPath = getDirectory(reportId)

        val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }

        val targetPdf = directoryPath + "document.pdf"
        val filePath = File(targetPdf)
        try {
            document.writeTo(FileOutputStream(filePath))
            Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e("main", "error $e")
            Toast.makeText(requireContext(), "Something wrong: $e", Toast.LENGTH_LONG).show()
        }
    }

    private fun getDirectory(reportId: Long):String{
       return "${Environment.getExternalStorageDirectory()}${Constants.REPORTS_DIRECTORY}/${reportId}/"
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PreviewDocumentFragment()

        const val REQUEST_PERMISSION_STORAGE = 111
    }
}
