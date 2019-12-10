package com.ireny.warrantyreport.services

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.ireny.warrantyreport.services.interfaces.IUserAccountManager

class UserAccountManager(val context: Context): IUserAccountManager {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestId()
        .requestProfile()
        .requestEmail()
        .build()

    val  googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    override fun getUserAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
}