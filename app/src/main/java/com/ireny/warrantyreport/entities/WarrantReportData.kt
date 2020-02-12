package com.ireny.warrantyreport.entities

class WarrantReportData (val reportTypes: ArrayList<ReportType>,
                         val companys: ArrayList<Company>,
                         val technicalAdvices: ArrayList<TechnicalAdvice>,
                         val reports: ArrayList<Report>,
                         val assignedTechnicalAdvices: ArrayList<AssignedTechnicalAdvice>)