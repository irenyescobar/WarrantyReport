package com.ireny.warrantyreport.data.retrofit

import com.ireny.warrantyreport.entities.CodeResult
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    @POST("code/")
    suspend fun getCode(@Body params: GetCodeParams ):CodeResult

}

data class GetCodeParams(val email:String)