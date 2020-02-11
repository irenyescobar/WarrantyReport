package com.ireny.warrantyreport.services

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.services.interfaces.CompleteListener
import com.ireny.warrantyreport.services.interfaces.IUserAccountManager

class UserAccountManager(val context: Context): IUserAccountManager {

    private val driveScope = Scope(DriveScopes.DRIVE_FILE)
    //private val driveFileScope = Scope(DriveScopes.DRIVE_FILE)
    private val scopes = listOf(driveScope.scopeUri)

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestId()
        .requestProfile()
        .requestEmail()
        .requestScopes(driveScope)
        .build()

    private val  googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    override fun signIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    override fun signOut(listener: CompleteListener?){
        googleSignInClient.signOut().addOnCompleteListener {
            listener?.onComplete(true,null)
        }
    }

    override fun getUserAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    override fun getGoogleDriveService():Drive?{

        val googleAccount = getUserAccount()

        googleAccount?.let {

            val credential = GoogleAccountCredential.usingOAuth2(context,scopes)
            credential.selectedAccount = googleAccount.account

            return Drive.Builder(
                            AndroidHttp.newCompatibleTransport(),
                            GsonFactory.getDefaultInstance(),
                            credential)
                            .setApplicationName(context.resources.getString(R.string.app_name))
                            .build()
        }

        return null
    }


}