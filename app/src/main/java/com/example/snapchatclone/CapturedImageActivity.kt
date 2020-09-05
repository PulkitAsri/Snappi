package com.example.snapchatclone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class CapturedImageActivity : AppCompatActivity() {
    private lateinit var Uid:String
    private lateinit var imageView:ImageView
    private lateinit var sendImage:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_captured_image)
        Uid= Firebase.auth.uid.toString()

        sendImage=findViewById(R.id.sendImage)
        imageView=findViewById(R.id.capturedImageView)
        val intentToNext= Intent(applicationContext,ChooseReceiverActivity::class.java)

        val path:Uri?

        val extras = intent.extras
        if (extras != null) {
            path = (extras["imageUri"] as Uri)
            imageView.setImageURI(path)
            intentToNext.putExtra("imageUri",path)
        }

        sendImage.setOnClickListener {
            startActivity(intentToNext)
            return@setOnClickListener
        }
    }


}