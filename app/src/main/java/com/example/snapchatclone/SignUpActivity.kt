package com.example.snapchatclone

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mikhaellopez.circularimageview.CircularImageView
import java.io.ByteArrayOutputStream
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private var signUpButton : Button?=null
    private lateinit var chooseProfilePicButton: Button
    private lateinit var cIV: CircularImageView
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText


    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAuthStateListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        usernameEditText=findViewById(R.id.usernameEditText)
        signUpButton=findViewById(R.id.signUpButton)
        emailEditText=findViewById(R.id.emailEditText)
        passwordEditText=findViewById(R.id.passwordEditText)
        chooseProfilePicButton=findViewById(R.id.profileImgSelectButton)
        cIV=findViewById(R.id.signUpCIV)

        auth= Firebase.auth
        firebaseAuthStateListener=FirebaseAuth.AuthStateListener {
            val user=it.currentUser
            if(user!=null){
                val intent= Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        chooseProfilePicButton.setOnClickListener{
            val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,1)
        }
    }
    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(firebaseAuthStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener (firebaseAuthStateListener)
    }


    fun signUpClicked(view: View) {
        val name = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if(name.isEmpty() || email.isEmpty() || password.isEmpty() ){
            Toast.makeText(baseContext, "Fill all the fields to continue", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d("SUCCESS", "signInWithEmail:success")
                    val userId = auth.currentUser?.uid.toString()
                    val currentUserDb=Firebase.database.reference.child("users").child(userId)
                    val userInfo:Map<String,String>  = mutableMapOf(
                        "email" to email,
                        "name" to name,
                    "profileImageUrl" to "")
                    currentUserDb.updateChildren(userInfo)

                    uploadToFirebaseStorage()


                }else {
                    // FAILED
                    Log.w("err", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()

                }
            }
    }

    private fun uploadToFirebaseStorage() {
        if(selectedImage==null) return

        val filename= UUID.randomUUID().toString()
        val ref =Firebase.storage.reference.child("profilePictures").child(filename)
        val bitMap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)

        val stream = ByteArrayOutputStream()
        bitMap!!.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        val data = stream.toByteArray()
        val uploadTask = ref.putBytes(data)
            .addOnSuccessListener {
                Toast.makeText(this,"Uploaded", Toast.LENGTH_SHORT).show()
                ref.downloadUrl.addOnSuccessListener {
                    saveDataToDatabase(it.toString())
                }

            }


    }

    private fun saveDataToDatabase(profileImageUrl: String) {
        val ref=Firebase.database.reference.child("users")
            .child(auth.currentUser!!.uid)
            .child("profileImageUrl")
            .setValue(profileImageUrl)
            .addOnSuccessListener {
                Log.i("SUCCESS","profilePicUploaded")
            }
    }

    private var selectedImage:Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        selectedImage = data?.data
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitMap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                cIV.setImageBitmap(bitMap)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}