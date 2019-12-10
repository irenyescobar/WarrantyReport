package com.ireny.warrantyreport.services

import com.ireny.warrantyreport.data.retrofit.Api
import com.ireny.warrantyreport.data.retrofit.GetCodeParams
import com.ireny.warrantyreport.observers.ISubject
import com.ireny.warrantyreport.observers.Result
import com.ireny.warrantyreport.services.interfaces.ICodeGenerator
import com.ireny.warrantyreport.services.interfaces.IUserAccountManager

class CodeGenerator(private val api: Api, private val accountManager: IUserAccountManager): ICodeGenerator {

    override suspend fun generateNewCode(): String {

        var success = false
        var message = ""
        var code:String? = null

        val account = accountManager.getUserAccount()
        if (account != null && account.email != null) {

            try {
                val result =  api.getCode(GetCodeParams(account.email!!))
                when {
                    result.status == -1 -> {
                        message = "Ocorreu um problema. Tente mais tarde"
                    }
                    result.status == 0 -> {
                        message = "Você não tem permissão para essa função"
                    }
                    result.status == 1 -> {
                        success = true
                        code = result.code
                        message = result.message
                    }
                }
            }catch (err:Exception){
                message = "Ocorreu um problema. Tente mais tarde"
            }

        }else {
           _subject?.notify(Result(success,message,code))
        }

        _subject?.notify(Result(success,message,code))

        return code?:""
    }

    fun addSubject(subject: ISubject){
        _subject = subject
    }
    private var _subject : ISubject? = null

}