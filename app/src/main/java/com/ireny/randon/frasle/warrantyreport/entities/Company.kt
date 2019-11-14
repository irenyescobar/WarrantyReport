package com.ireny.randon.frasle.warrantyreport.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Company(
    @PrimaryKey val id:Int = 0,
    val description:String = "",
    val enabled:Boolean = true
)