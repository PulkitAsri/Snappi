package com.example.snapchatclone.RecyclerViewFollow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapchatclone.R
import com.example.snapchatclone.UserInformation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FollowRVAdapter : RecyclerView.Adapter<FollowRVHolders> {

    private var usersList:List<FollowRVObject>
    private var context:Context

    constructor(usersList :List<FollowRVObject>, context: Context){
        this.usersList=usersList
        this.context=context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowRVHolders {
        var layoutView:View=LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_follower_item,null)
        return FollowRVHolders(layoutView)
    }

    override fun getItemCount(): Int {
        return this.usersList.size
    }

    override fun onBindViewHolder(holder: FollowRVHolders, position: Int) {
        holder.email.text = usersList[position].email
        holder.name.text=usersList[position].name

        if(UserInformation.listFollowing.contains(usersList[holder.layoutPosition].uid)){
            holder.followButton.text = "Following"
            holder.followButton.setBackgroundResource(R.drawable.following_button)
        }else{
            holder.followButton.text = "Follow"
            holder.followButton.setBackgroundResource(R.drawable.follow_button)
        }

        holder.followButton.setOnClickListener {
            var userId= Firebase.auth.currentUser?.uid
            if(!UserInformation.listFollowing.contains(usersList[holder.layoutPosition].uid)){
                holder.followButton.text = "Following"
                holder.followButton.setBackgroundResource(R.drawable.following_button)
                Firebase.database.reference
                    .child("users")
                    .child(userId!!)
                    .child("following")
                    .child(usersList[holder.layoutPosition].uid)
                    .setValue(true)

            }else{
                holder.followButton.text = "Follow"
                holder.followButton.setBackgroundResource(R.drawable.follow_button)
                Firebase.database.reference
                    .child("users")
                    .child(userId!!)
                    .child("following")
                    .child(usersList[holder.layoutPosition].uid)
                    .removeValue()

            }

        }
    }
}