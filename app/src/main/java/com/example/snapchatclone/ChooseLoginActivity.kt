package com.example.snapchatclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ViewAnimator

class ChooseLoginActivity : AppCompatActivity() {
    fun loginClicked(view: View){
        intent=Intent(applicationContext,LoginActivity::class.java)
        startActivity(intent)

    }
    fun signUpClicked(view: View){
        intent=Intent(applicationContext,SignUpActivity::class.java)
        startActivity(intent)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_login)

    }
}