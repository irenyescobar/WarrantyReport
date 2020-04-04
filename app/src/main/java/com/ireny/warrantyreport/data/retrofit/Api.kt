package com.ireny.warrantyreport.data.retrofit

import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    @POST("code/")
    suspend fun getCode(@Body params: GetCodeParams ):CodeResult

    @POST("users/auth/")
    suspend fun requestAuth(@Body params: GetCodeParams ): RequestAuthResult

}

data class GetCodeParams(val email:String, val key:String)
data class CodeResult(val status:Int, val message: String, val code:String )
data class RequestAuthResult(val active:Boolean, val code:String, val message: String)