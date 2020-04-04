package com.ireny.warrantyreport.services

import com.ireny.warrantyreport.data.retrofit.Api
import com.ireny.warrantyreport.data.retrofit.GetCodeParams
import com.ireny.warrantyreport.observers.ISubject
import com.ireny.warrantyreport.observers.Result
import com.ireny.warrantyreport.services.interfaces.IUserAccountManager

class AuthorizationService(private val api: Api,
                           private val accountManager: IUserAccountManager
) {
    suspend fun request(keyApp: String) {
        val account = accountManager.getUserAccount()
        if (account != null && account.email != null) {
            try {
                val result = api.requestAuth(GetCodeParams(account.email!!, keyApp))
                _subject?.notify(Result(true,"",result))
            }catch (err:Exception){
                _subject?.notify(Result(false,"Ocorreu um problema. Tente mais tarde",null))
            }
        }else {
            _subject?.notify(Result(false,"Não foi possível acessar informações do usuário logado",null))
        }
    }

    fun addSubject(subject: ISubject){
        _subject = subject
    }

    private var _subject : ISubject? = null
}