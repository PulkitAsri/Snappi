package com.example.snapchatclone.RecyclerViewStory

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapchatclone.R
import com.example.snapchatclone.ImageDisplayActivity


class StoryRVHolders(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener{
    var email:TextView
    var name:TextView
    var layout:LinearLayout
    init {
        itemView.setOnClickListener(this)
        email=itemView.findViewById(R.id.email)
        layout=itemView.findViewById(R.id.layout)
        name=itemView.findViewById(R.id.name)
    }

    override fun onClick(p0: View?) {
        val intent= Intent(p0?.context,ImageDisplayActivity::class.java)
        val b = Bundle()
        b.putString("userId", email.tag.toString())
        b.putString("chatOrStory", layout.tag.toString())
        intent.putExtras(b)
        p0?.context?.startActivity(intent)
    }



}