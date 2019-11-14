package com.ireny.randon.frasle.warrantyreport.entites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ReportType(
    @PrimaryKey val id:Int = 0,
    val description:String= "",
    val enabled:Boolean = true
    )