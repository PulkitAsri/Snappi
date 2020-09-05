package com.example.snapchatclone.RecyclerViewReceivers

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapchatclone.R


class ReceiverRVHolders(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var email:TextView = itemView.findViewById(R.id.email)
    var name:TextView = itemView.findViewById(R.id.name)
    var receiveCB:CheckBox = itemView.findViewById(R.id.receiveCB)

}