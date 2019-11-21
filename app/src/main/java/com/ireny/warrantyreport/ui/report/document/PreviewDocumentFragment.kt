package com.ireny.warrantyreport.ui.report.document

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
import com.ireny.warrantyreport.utils.toDateTextFormatted
import kotlinx.android.synthetic.main.report_preview_document_fragment.*
import java.io.File


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
        return inflater.inflate(R.layout.report_preview_document_fragment, container, false)
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

            if(model.code != null) {
                val file = directoryManager.getReportFile(model.id)
                if(file != null){
                    showDocument(file)
                }else{
                    showReportScreen(model)
                    confirmReGenerate(model)
                }
            }else{
                showReportScreen(model)
            }
        }
    }

    override fun createDocument(model: Report) {
        val file = createPdf(model.id)
        if(file != null) {
            showDocument(file)
        }else{
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.dialog_message_no_save_file))
                .setNeutralButton(getString(R.string.dialog_button_cancel_text)){ dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    private fun confirmReGenerate(model: Report){

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.app_name)
            .setMessage(getString(R.string.dialog_no_found_file_message))
            .setPositiveButton(getString(R.string.dialog_button_confirm_text)) { dialog, _ ->
                dialog.dismiss()
                createDocument(model)
            }
            .setNeutralButton(getString(R.string.dialog_button_cancel_text)){ dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun initComponent() {
        component = DaggerReportDirectoryComponent
            .builder()
            .reportDirectoryModule(ReportDirectoryModule(requireContext()))
            .build()

        component.inject(requireContext())
    }

    private fun showReportScreen(entity: Report){

        container.visibility = View.VISIBLE

        entity.company?.run {
            text_company_value.text = description
        }

        text_cod_value.text = if(entity.code != null) entity.code else "SEM CÓDIGO"
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

        val data = directoryManager.getData(entity.id)
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

    private fun showDocument(file: File) {

        val parcelFileDescriptor =
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

        if (parcelFileDescriptor != null) {

            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val page = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            container.visibility = View.GONE
            img.visibility = View.VISIBLE
            img.setImageBitmap(bitmap)

            page.close()
            pdfRenderer.close()
        }
    }

    private fun createPdf(reportId: Long) :File?{

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

        val file = directoryManager.saveFile(document,reportId)

        document.close()

        return file
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            PreviewDocumentFragment()

        const val REQUEST_PERMISSION_STORAGE = 111
    }
}
