package com.example.snapchatclone


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapchatclone.RecyclerViewReceivers.ReceiverRVAdapter
import com.example.snapchatclone.RecyclerViewReceivers.ReceiverRVHolders
import com.example.snapchatclone.RecyclerViewReceivers.ReceiverRVObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


class ChooseReceiverActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerView.Adapter<ReceiverRVHolders>
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var bitmap: Bitmap?=null
    private lateinit var U_ID:String
    var uri:Uri?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_reciever)

        U_ID= Firebase.auth.uid.toString()

        recyclerView=findViewById(R.id.recyclerView)
        recyclerView.isNestedScrollingEnabled=false
        recyclerView.setHasFixedSize(false)

        layoutManager= LinearLayoutManager(application)
        recyclerView.layoutManager=layoutManager
        adapter= ReceiverRVAdapter(getDataSet(),application)
        recyclerView.adapter=adapter

        val extras = intent.extras
        if (extras != null) {
            uri = (extras["imageUri"] as Uri)
        }
        val sendButton=findViewById<Button>(R.id.fab)
        sendButton.setOnClickListener {
            saveToStory()
        }

        val mBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
        //depreciated method if doesnt work use this
        //        mBitmap = decodeUriToBitmap(applicationContext,uri)

        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(uri?.path!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val orientation: Int = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED)!!

        bitmap = rotateBitmap(mBitmap, orientation)
         /*
         The methods that convert the image at a URI(Location in the phone) into a bitmap
         sometimes rotates the image by 90 degrees coz of the exif Interface...
         just check the orientation and rotate in by that much
         rotateBitmap method is just down below


         */
    }
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap? {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return try {
            val bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    private val results: ArrayList<ReceiverRVObject> = ArrayList()
    private fun getDataSet(): List<ReceiverRVObject> {
        listenForData()
        return results

    }

    private fun listenForData() {

        for ( i in 0 until UserInformation.listFollowing.size) {
            val userDb: DatabaseReference = Firebase.database.reference
                .child("users").child(UserInformation.listFollowing[i])

            userDb.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var email = ""
                    var name = ""
                    val uid = snapshot.ref.key
                    if (snapshot.child("email").value != null)
                        email = snapshot.child("email").value.toString()

                    if (snapshot.child("name").value != null)
                        name = snapshot.child("name").value.toString()

                    val receiverObject = ReceiverRVObject(email, uid!!, false,name)
                    if (!results.contains(receiverObject)) {
                        results.add(receiverObject)
                        adapter.notifyDataSetChanged()
                    }

                }

                override fun onCancelled(error: DatabaseError) {}

            })
        }
    }
    private fun saveToStory() {
        val ref:DatabaseReference=FirebaseDatabase.getInstance().reference
            .child("users")
            .child(U_ID)
            .child("story")

        val key=ref.push().key
        val storageRef: StorageReference =Firebase.storage.reference.child("captures").child(key!!)

        val bitmap: Bitmap? =bitmap
        val byteStream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 20, byteStream)
        val data = byteStream.toByteArray()

//        val uploadTask = storageRef.putFile(uri!!)

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this,"Upload failed", Toast.LENGTH_SHORT).show()
            finish()

        }.addOnSuccessListener {
            Toast.makeText(this,"Uploaded", Toast.LENGTH_SHORT).show()
            val task=it.storage.downloadUrl
            var downloadUrl=""
            task.addOnSuccessListener {

                downloadUrl= task.result.toString()

                val currentTime:Long=System.currentTimeMillis()
                val endTime:Long=currentTime+(24*60*60*1000)

                val addToStory=findViewById<CheckBox>(R.id.saveToStoryCB)

                if(addToStory.isChecked){
                    val mapToUpload:Map<String,Any> =mapOf(
                        "imageUrl" to downloadUrl,
                        "begTimeStamp" to currentTime,
                        "endTimeStamp" to endTime
                    )
                    ref.child(key).setValue(mapToUpload)
                }
                for (r in results){
                    if(r.receive){
                        val userDb:DatabaseReference=FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(r.uid)
                            .child("received")
                            .child(U_ID)
                        val mapToUpload:Map<String,Any> =mapOf("imageUrl" to downloadUrl,
                            "begTimeStamp" to currentTime,
                            "endTimeStamp" to endTime)
                        userDb.child(key).setValue(mapToUpload)
                    }
                }
            }
            task.addOnFailureListener{
                val intentToMainActivity=Intent(applicationContext,MainActivity::class.java)
                intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                Toast.makeText(applicationContext,"Failed To Upload",Toast.LENGTH_SHORT).show()
                startActivity(intentToMainActivity)
                finish()
            }
        }
        val intentToMainActivity=Intent(applicationContext,MainActivity::class.java)
        intentToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intentToMainActivity)

    }
    private fun decodeUriToBitmap(mContext: Context, sendUri: Uri?): Bitmap? {
        var getBitmap: Bitmap? = null
        try {
            val image_stream: InputStream
            try {
                image_stream = mContext.contentResolver.openInputStream(sendUri!!)!!
                getBitmap = BitmapFactory.decodeStream(image_stream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getBitmap
    }

}