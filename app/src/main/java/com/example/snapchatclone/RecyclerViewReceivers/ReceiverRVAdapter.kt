package com.example.snapchatclone.RecyclerViewReceivers

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

class ReceiverRVAdapter(
    private var usersList: List<ReceiverRVObject>,
    private var context: Context
) : RecyclerView.Adapter<ReceiverRVHolders>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiverRVHolders {
        var layoutView:View=LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_receiver_item,null)
        return ReceiverRVHolders(layoutView)
    }

    override fun getItemCount(): Int {
        return this.usersList.size
    }

    override fun onBindViewHolder(holder: ReceiverRVHolders, position: Int) {
        holder.email.text = usersList[position].email
        holder.name.text = usersList[position].name
        holder.receiveCB.setOnClickListener {
            var receiveState=!usersList[holder.layoutPosition].receive
            usersList[holder.layoutPosition].receive=receiveState
        }


    }
    }
