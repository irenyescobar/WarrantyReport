package com.ireny.warrantyreport.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TechnicalAdvice(
    @PrimaryKey val id:Int = 0,
    val description:String= "",
    val enabled:Boolean = true
)