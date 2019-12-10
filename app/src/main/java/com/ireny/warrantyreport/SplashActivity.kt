package com.ireny.warrantyreport

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ireny.warrantyreport.services.UserAccountManager
import com.ireny.warrantyreport.utils.customApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    private val component by lazy { customApp.component }
    private val accountMannager: UserAccountManager by lazy { component.userAccountManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        MainScope().launch {

            MainScope().launch {
                withContext(Dispatchers.IO) {
                    Thread.sleep(SPLASH_TIME_OUT.toLong())
                }
                start()

            }
        }
    }

    private fun start(){
        val account = accountMannager.getUserAccount()
        if (account != null) {
            val intent = Intent(this, MainActivity::class.java)
            go(intent)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            go(intent)
        }
    }

    private fun go(intent:Intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val SPLASH_TIME_OUT = 1000
    }

}
