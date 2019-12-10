package com.ireny.warrantyreport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.ireny.warrantyreport.services.UserAccountManager
import com.ireny.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val component by lazy { customApp.component }
    private val accountMannager: UserAccountManager by lazy { component.userAccountManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_in_button.setOnClickListener{
            startActivityForResult(accountMannager.googleSignInClient.signInIntent, GOOGLE_SIGNIN_REQUEST_CODE)
        }

    }

    public override fun onStart() {
        super.onStart()
        val account = accountMannager.getUserAccount()
        if (account != null) {
            onLoggedIn()
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                GOOGLE_SIGNIN_REQUEST_CODE -> try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                    val account = task.getResult(ApiException::class.java)
                    account?.run {
                        onLoggedIn()
                    }

                } catch (e: ApiException) {
                    Snackbar.make(container,"Falha de login: ${e.localizedMessage}", Snackbar.LENGTH_LONG).show()
                }
            }
    }

    private fun onLoggedIn() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    companion object {
        const val GOOGLE_SIGNIN_REQUEST_CODE: Int = 101
    }
}
