package com.ziarinetwork.ziari

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login()
    }


    private fun login() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}