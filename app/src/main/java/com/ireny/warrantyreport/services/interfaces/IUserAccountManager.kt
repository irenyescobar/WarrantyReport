package com.ireny.warrantyreport.services.interfaces

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface IUserAccountManager {

    fun getUserAccount(): GoogleSignInAccount?
}