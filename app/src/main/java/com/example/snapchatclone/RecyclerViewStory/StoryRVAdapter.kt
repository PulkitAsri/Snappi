package com.example.snapchatclone.RecyclerViewStory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapchatclone.R
import com.example.snapchatclone.UserInformation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StoryRVAdapter(private var usersList: List<StoryRVObject>, private var context: Context) :
    RecyclerView.Adapter<StoryRVHolders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryRVHolders {
        var layoutView:View=LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_story_item,null)
        return StoryRVHolders(layoutView)
    }

    override fun getItemCount(): Int {
        return this.usersList.size
    }

    override fun onBindViewHolder(holder: StoryRVHolders, position: Int) {
        holder.email.text = usersList[position].email
        holder.email.tag=usersList[position].uid
        holder.name.text = usersList[position].name
        holder.layout.tag=usersList[position].chatOrStory

    }
}