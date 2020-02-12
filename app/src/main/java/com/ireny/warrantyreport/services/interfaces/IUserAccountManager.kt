package com.ireny.warrantyreport.services.interfaces

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.services.drive.Drive

interface IUserAccountManager {

    fun getUserAccount(): GoogleSignInAccount?
    fun getGoogleDriveService(): Drive?
    fun signOut(listener: CompleteListener?)
    fun signIntent(): Intent
}

interface CompleteListener{
    fun onComplete(success:Boolean, message:String?)
}