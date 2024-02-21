package com.example.tasktracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onBackPressed() {
        val isLogin: Boolean = mAuth.currentUser == null

        if (!isLogin) {
            super.onBackPressed()
        }
    }

}