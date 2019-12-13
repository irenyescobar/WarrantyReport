@file:Suppress("DEPRECATION")

package com.ireny.warrantyreport.utils

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.ireny.warrantyreport.MainActivity
import com.ireny.warrantyreport.MyWarrantReportApp
import com.ireny.warrantyreport.ui.report.ReportActivity
import com.ireny.warrantyreport.utils.Constants.Companion.LOCALE_BRAZIL
import java.text.SimpleDateFormat
import java.util.*


val Activity.customApp: MyWarrantReportApp
    get() = application as MyWarrantReportApp

val Fragment.customApp: MyWarrantReportApp
    get() = MyWarrantReportApp.applicationContext() as MyWarrantReportApp

val Fragment.reportActivity: ReportActivity
    get() = activity as ReportActivity

fun Date?.toDateTimeTextFormatted():String{
    this?.run {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",LOCALE_BRAZIL)
        return formatter.format(this)
    }
    return ""
}

fun Date?.toDateTextFormatted():String{
    this?.run {
        val formatter = SimpleDateFormat("dd/MM/yyyy",LOCALE_BRAZIL)
        return formatter.format(this)
    }
    return ""
}

fun String.toDate():Date?{
    if(this.isBlank()){
        return null
    }
    val formatter = SimpleDateFormat("dd/MM/yyyy",LOCALE_BRAZIL)
    return formatter.parse(this)
}

fun String.toDateTime():Date?{
    if(this.isBlank()){
        return null
    }
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss",LOCALE_BRAZIL)
    return formatter.parse(this)
}

fun EditText.setOnClickDatePicker(context:Context,
                                  year:Int,
                                  month:Int,
                                  day:Int ){
    this.setOnClickListener {
        val datePicker = DatePickerDialog(context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val m = (monthOfYear +1).toString().padStart(2, '0')
                val d = dayOfMonth.toString().padStart(2, '0')
                val date = "$d/$m/$year"
                this.setText(date)
            }, year, month, day)
        datePicker.show()
    }
}
