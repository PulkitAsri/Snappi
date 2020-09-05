package com.example.snapchatclone.RecyclerViewFollow

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapchatclone.R


class FollowRVHolders :RecyclerView.ViewHolder{

    var email:TextView
    var followButton:Button
    var name:TextView


    constructor(itemView:View) : super(itemView) {
        email=itemView.findViewById(R.id.email)
        followButton=itemView.findViewById(R.id.followButton)
        name=itemView.findViewById(R.id.name)

    }


}