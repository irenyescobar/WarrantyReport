package com.ireny.warrantyreport.utils

import com.ireny.warrantyreport.entities.Report

fun Report.copy():Report{

    return Report(
        this.id,
        this.reportTypeId,
        this.companyId,
        this.distributor,
        this.client,
        this.cityState,
        this.partReference,
        this.sourceInvoice,
        this.invoiceDate,
        this.applicationDate,
        this.warrantyDate,
        this.reasonUnfounded,
        this.comments,
        this.technicalConsultant,
        this.technicalConsultantContact,
        this.analysisDate,
        this.created_at,
        this.code,
        this.code_generated_at,
        this.tecnicalAdvices,
        this.reportType,
        this.company)
}