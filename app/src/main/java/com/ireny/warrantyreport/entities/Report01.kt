package com.ireny.warrantyreport.entities

import java.util.*

class Report01(val id:Long = 0,
               var code:String? = null,
               val companyId: Int? = null,
               var client:String = "",
               val company:String = "",
               var distributor:String = "",
               var created_at:Date = Date()
)