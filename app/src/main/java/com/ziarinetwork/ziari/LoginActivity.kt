package com.ziarinetwork.ziari

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.widget.LoginButton


class LoginActivity : AppCompatActivity() {

    private val callbackManager = com.facebook.CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)

        val loginButton = findViewById<LoginButton>(R.id.login_button)
        loginButton.setReadPermissions("email")

        loginButton.registerCallback(callbackManager, object : com.facebook.FacebookCallback<com.facebook.login.LoginResult> {
            override fun onSuccess(result: com.facebook.login.LoginResult) {
                android.util.Log.d("LoginActivity", "Facebook login success")
            }

            override fun onCancel() {
                android.util.Log.d("LoginActivity", "Facebook login cancel")
            }

            override fun onError(exception: com.facebook.FacebookException) {
                android.util.Log.d("LoginActivity", "Facebook login error")
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}