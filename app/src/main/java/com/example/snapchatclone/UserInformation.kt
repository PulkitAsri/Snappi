package com.example.snapchatclone

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserInformation {
    companion object {
        val listFollowing = ArrayList<String>()
        fun startFetching() {
            listFollowing.clear()
            getUserFollowing()
        }


        private fun getUserFollowing() {
            var userFollowingDb = Firebase.database.reference
                .child("users")
                .child(Firebase.auth.currentUser!!.uid)
                .child("following")

            userFollowingDb.addChildEventListener(object : ChildEventListener {

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    if (snapshot.exists()) {
                        var uid = snapshot.ref.key
                        if (uid != null && !listFollowing.contains(uid)) {
                            listFollowing.add(uid)
                        }

                    }
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var uid = snapshot.ref.key
                        if (uid != null ) {
                            listFollowing.remove(uid)
                        }
                    }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            })
        }
    }
}