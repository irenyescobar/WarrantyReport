package com.ireny.randon.frasle.warrantyreport.entities

import androidx.room.*
import java.util.*

@Entity(foreignKeys = [
            ForeignKey(entity = ReportType::class, parentColumns = arrayOf("id"), childColumns = arrayOf("reportTypeId"), onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = Company::class, parentColumns = arrayOf("id"), childColumns = arrayOf("companyId"), onDelete = ForeignKey.CASCADE)
        ],
        indices = [Index(value = ["reportTypeId"]),
                   Index(value = ["companyId"])
        ])
class Report(@PrimaryKey(autoGenerate = true)
             var id:Long = 0,
             var reportTypeId: Int? = null,
             var companyId:Int? = null,
             var distributor:String = "",
             var client:String = "",
             var cityState:String = "",
             var partReference:String = "",
             var sourceInvoice:Long? = null,
             var invoiceDate: Date? = null,
             var applicationDate: Date? = null,
             var warrantyDate: Date? = null,
             var reasonUnfounded: String = "",
             var comments: String = "",
             var technicalConsultant: String = "",
             var technicalConsultantContact: String = "",
             var analysisDate: Date? = null,
             var created_at:Date = Date(),
             @Ignore var tecnicalAdvices:MutableList<TechnicalAdvice> = mutableListOf(),
             @Ignore var reportType: ReportType? = null,
             @Ignore var company: Company? = null)