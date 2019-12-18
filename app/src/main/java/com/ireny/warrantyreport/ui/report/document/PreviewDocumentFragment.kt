package com.ireny.warrantyreport.ui.report.document

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.StaticLayout
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withTranslation
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
import kotlin.math.roundToInt


class PreviewDocumentFragment : Fragment(), ICreateDocument<Report> ,IBindView<Report>{

    private lateinit var component: ReportDirectoryComponent
    private val directoryManager: IReportDirectoryManager by lazy { component.reportDirectoryManager()}
    private var report: Report? = null

    private val a4Wpt = 595
    private val a4Hpt = 842

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
        if(model.code != null) {
            val file = directoryManager.getReportFile(model.id)
            if(file != null){
                showDocument(file)
            }else{
                showDocument(generatePdfDocument(model))
                confirmReGenerate(model)
            }
        }else{
            showDocument(generatePdfDocument(model))
        }
    }

    override fun createDocument(model: Report) {
        val file = createPdfFile(model.id,model)
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

    private fun showDocument(file: File) {

        val parcelFileDescriptor =
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)

        if (parcelFileDescriptor != null) {

            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            val page = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            img.setImageBitmap(bitmap)

            page.close()
            pdfRenderer.close()
        }
    }

    private fun showDocument(document: PdfDocument) {

        val file = File.createTempFile("temp","document")

        document.writeTo(file.outputStream())

        document.close()

        showDocument(file)

        file.delete()
    }

    private fun generatePdfDocument(entity: Report):PdfDocument{

        val document = PdfDocument()

        val pageInfo: PdfDocument.PageInfo = PdfDocument.PageInfo.Builder(a4Wpt, a4Hpt, 1) .create()
        val page: PdfDocument.Page = document.startPage(pageInfo)

        val canvas = page.canvas
        val margin = 10f
        val linespace = 10f

        val paintRotulo = Paint()
        paintRotulo.color = resources.getColor(R.color.colorBlack,null)
        paintRotulo.textSize = 12f
        paintRotulo.typeface = Typeface.create(paintRotulo.typeface,Typeface.BOLD)

        val paintValues = Paint()
        paintValues.color = resources.getColor(R.color.colorBlack,null)
        paintRotulo.textSize = 12f

        val textPaint = TextPaint()

        val paintTitle = Paint()
        paintTitle.color = resources.getColor(R.color.colorBlack,null)
        paintTitle.style = Paint.Style.STROKE
        paintTitle.strokeWidth = 0.5f
        canvas.drawRect(Rect(1,1,canvas.width -1,canvas.height-1),paintTitle)

        paintTitle.style = Paint.Style.FILL
        paintTitle.color = resources.getColor(R.color.colorPrimary,null)
        paintTitle.textAlign = Paint.Align.CENTER
        paintTitle.textSize = 20f

        var text = "LAUDO DE GARANTIA"
        var x = (canvas.width/2 - text.length /2).toFloat()
        var y = paintTitle.textSize + margin
        canvas.drawText(text,0,text.length, x,y, paintTitle)

        text = "Suspensys    Fras-le    Controil    LonaFlex    Master    Jost"
        canvas.drawText(text,0,text.length, (canvas.width/2f),canvas.height - 15f, paintTitle)


        paintTitle.textSize = 15f
        paintTitle.color = resources.getColor(R.color.colorRed,null)
        paintTitle.textAlign = Paint.Align.LEFT

        text = ""
        entity.code?.run {
            text = this
        }

        x = (canvas.width - (paintTitle.measureText(text) + margin))
        canvas.drawText(text,0,text.length, x,y, paintTitle)

        paintTitle.color = resources.getColor(R.color.colorBlack,null)

        text = "N°: "
        x -= paintTitle.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintTitle)


        text = ""
        entity.company?.run {
            text = description
        }
        x = (canvas.width - (paintValues.measureText(text) + margin))
        y += paintValues.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "EMPRESA: "
        x -= paintRotulo.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintRotulo)


        paintRotulo.textAlign = Paint.Align.RIGHT
        paintValues.textAlign = Paint.Align.RIGHT

        text = "DISTRIBUIDOR: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = entity.distributor
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "CIDADE/ESTADO: "
        x = canvas.width/2 + paintRotulo.measureText(text) + margin
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = entity.cityState
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "CLIENTE: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = entity.client
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "REFERÊNCIA DA PEÇA: "
        x = canvas.width/2 + paintRotulo.measureText(text) + margin
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = entity.partReference
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "NOTA FISCAL DE ORIGEM: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = if(entity.sourceInvoice != null){ entity.sourceInvoice.toString() } else { "" }
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "DATA DA NOTA: "
        x = canvas.width/2 + paintRotulo.measureText(text) + margin
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = if(entity.invoiceDate != null){ entity.invoiceDate.toDateTextFormatted() } else { "" }
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "DATA DA APLICAÇÃO: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = if(entity.applicationDate != null){ entity.applicationDate.toDateTextFormatted() } else { "" }
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "DATA DO RETORNO: "
        x = canvas.width/2 + paintRotulo.measureText(text) + margin
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = if(entity.warrantyDate != null){ entity.warrantyDate.toDateTextFormatted() } else { "" }
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)


        text = "PARECER TÉCNICO: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = ""
        entity.tecnicalAdvices.forEach{
            text += "${it.description}  "
        }
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "MOTIVO DA IMPROCEDÊNCIA DA GARANTIA: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)


        text = entity.reasonUnfounded
        var staticLayout =
            StaticLayout.Builder.obtain (
                text, 0, text.length, textPaint , (canvas.width - margin*2).roundToInt()
            ) .build ()

        y += linespace
        canvas.withTranslation(margin, y) {
            staticLayout.draw(this)
        }

        text = "OBSERVAÇÕES: "
        x = paintRotulo.measureText(text) + margin
        y +=  staticLayout.height + paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = entity.comments
        staticLayout =
            StaticLayout.Builder.obtain (
                text, 0, text.length, textPaint , (canvas.width - margin*2).roundToInt()
            ) .build ()

        y += linespace
        canvas.withTranslation(margin, y) {
            staticLayout.draw(this)
        }

        y +=  staticLayout.height

        text = "CONSULTOR TÉCNICO: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = entity.technicalConsultant
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "TELEFONE: "
        x = canvas.width/2 + paintRotulo.measureText(text) + margin
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = entity.technicalConsultantContact
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        text = "DATA: "
        x = paintRotulo.measureText(text) + margin
        y += paintRotulo.textSize + linespace
        canvas.drawText(text,0,text.length, x,y, paintRotulo)

        text = if(entity.analysisDate != null) { entity.analysisDate.toDateTextFormatted() } else { "" }
        x += paintValues.measureText(text)
        canvas.drawText(text,0,text.length, x,y, paintValues)

        y += paintValues.textSize + linespace

        val hrodape = 30
        val w = (canvas.width/2 - margin *2).roundToInt()
        val h = ((canvas.height - y) / 2 - margin * 2 ).roundToInt() - hrodape

        val data = directoryManager.getImages(entity.id)

        data[0].image?.run {
            val b = this.toBitmap(w,h,Bitmap.Config.ARGB_8888 )
            canvas.drawBitmap(b,margin,y,null)
        }

        data[1].image?.run {
            val b = this.toBitmap(w,h,Bitmap.Config.ARGB_8888 )
            canvas.drawBitmap(b,(canvas.width/2 + margin),y,null)
        }

        y += h + margin

        data[2].image?.run {
            val b = this.toBitmap(w,h,Bitmap.Config.ARGB_8888 )
            canvas.drawBitmap(b,(margin),y,null)
        }

        data[3].image?.run {
            val b = this.toBitmap(w,h,Bitmap.Config.ARGB_8888 )
            canvas.drawBitmap(b,(canvas.width/2 + margin),y,null)
        }

        document.finishPage(page)

        return document

    }

    private fun createPdfFile(reportId: Long, entity: Report) :File?{

        val document = generatePdfDocument(entity)

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
