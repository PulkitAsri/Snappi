package com.example.snapchatclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.snapchatclone.RecyclerViewStory.StoryRVObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ImageDisplayActivity : AppCompatActivity() {

    lateinit var userId:String

    lateinit var chatOrStory:String

    lateinit var imageView:ImageView
    private var started=false
    var imageUrlList=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_image_display)

        var b=intent.extras
        userId= b?.getString("userId").toString()
        chatOrStory= b?.getString("chatOrStory").toString()
        imageView=findViewById(R.id.storyImageView)

        when(chatOrStory){
            "chat" -> listenForChat()
            "story" -> listenForStory()
        }
    }

    private fun listenForChat() {
        val chatDb=Firebase.database.reference
            .child("users")
            .child(Firebase.auth.uid!!)
            .child("received")
            .child(userId)
        chatDb.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var imageUrl=""

                for( chatSnapshot in snapshot.children){
                    if(chatSnapshot.child("imageUrl").value!=null){
                        imageUrl= chatSnapshot.child("imageUrl").value.toString()
                    }
                    imageUrlList.add(imageUrl)
                    if(!started) {
                        started=true
                        initializeDisplay()
                    }
                    chatDb.child(chatSnapshot.key!!).removeValue()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun listenForStory() {

        var followingStoryDb= Firebase.database.reference
            .child("users")
            .child(userId)

        followingStoryDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var imageUrl=""
                var begTime:Long=0
                var endTime:Long=0

                for(storySnapshot in snapshot.child("story").children){

                    if(storySnapshot.child("begTimeStamp").value!=null){
                        begTime= storySnapshot.child("begTimeStamp").value.toString().toLong()
                    }
                    if(storySnapshot.child("endTimeStamp").value!=null){
                        endTime= storySnapshot.child("endTimeStamp").value.toString().toLong()
                    }
                    if(storySnapshot.child("imageUrl").value!=null){
                        imageUrl= storySnapshot.child("imageUrl").value.toString()
                    }
                    var currentTime=System.currentTimeMillis()
                    if(currentTime in begTime..endTime){
                        imageUrlList.add(imageUrl)
                        if(!started) {
                            started=true
                            initializeDisplay()
                        }

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private var imageIterator=0
    private fun initializeDisplay(){
        Glide.with(application).load(imageUrlList[imageIterator]).into(imageView)
        imageView.setOnClickListener {
            changeImage()
        }
        val handler=Handler()
        val delay:Long=10000

        handler.postDelayed(object :Runnable{
            override fun run() {
                changeImage()
                handler.postDelayed(this, delay)
            }

        },delay)
    }

    private fun changeImage() {
        if(imageIterator==imageUrlList.size-1){
            finish()
            return
        }
        imageIterator++
        Glide.with(application).load(imageUrlList[imageIterator]).into(imageView)

    }
}