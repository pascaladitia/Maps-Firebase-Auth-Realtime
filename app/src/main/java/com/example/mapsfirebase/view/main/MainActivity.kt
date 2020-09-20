package com.example.mapsfirebase.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.mapsfirebase.R
import com.example.mapsfirebase.view.login_register.LoginActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed(Runnable {
            startActivity(Intent(this, LoginActivity::class.java))
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}