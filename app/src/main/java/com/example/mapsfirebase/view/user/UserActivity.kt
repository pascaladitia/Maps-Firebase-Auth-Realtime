package com.example.mapsfirebase.view.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.mapsfirebase.R
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val navController = Navigation.findNavController(this, R.id.nav_host_homeUser)
        NavigationUI.setupWithNavController(bottom_navigation_user, navController)
    }
}