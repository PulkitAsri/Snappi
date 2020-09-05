package com.example.snapchatclone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.snapchatclone.RecyclerViewStory.StoryRVAdapter
import com.example.snapchatclone.RecyclerViewStory.StoryRVHolders
import com.example.snapchatclone.RecyclerViewStory.StoryRVObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatFragment:Fragment(){
    private var swipeContainer: SwipeRefreshLayout? = null

    private val results: ArrayList<StoryRVObject> = ArrayList()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerView.Adapter<StoryRVHolders>
    lateinit var layoutManager: RecyclerView.LayoutManager

    fun newInstance():ChatFragment{
        return ChatFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view:View=inflater.inflate(R.layout.chat_fragment,container,false)
        swipeContainer=view.findViewById(R.id.swipeContainer)

        recyclerView=view.findViewById(R.id.recyclerView)
        recyclerView.isNestedScrollingEnabled=false
        recyclerView.setHasFixedSize(false)

        layoutManager= LinearLayoutManager(context)
        recyclerView.layoutManager=layoutManager
        adapter= StoryRVAdapter(getDataSet(), context!!)
        recyclerView.adapter=adapter


        swipeContainer?.setOnRefreshListener {
            clear()
            listenForData()
            swipeContainer!!.isRefreshing=false
        }

        return view
    }
    private fun listenForData() {
        val receiveDb= Firebase.database.reference
            .child("users")
            .child(Firebase.auth.uid!!)
            .child("received")
        receiveDb.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(snap in  snapshot.children){
                        getUserInfo(snap.key)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun getUserInfo(key: String?) {
        val userDb= Firebase.database.reference
            .child("users")
            .child(key!!)
        userDb.addValueEventListener(object:ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val email=snapshot.child("email").value.toString()
                    val name=snapshot.child("name").value.toString()
                    val uid=snapshot.ref.key
                    val obj=StoryRVObject(email, uid!!,"chat",name)
                    if(!results.contains(obj)){
                        results.add(obj)
                        adapter.notifyDataSetChanged()
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {}


        })

    }

    private fun getDataSet(): List<StoryRVObject> {
        listenForData()
        return results

    }
    private fun clear() {
        val size=this.results.size
        this.results.clear()
        adapter.notifyDataSetChanged()

    }
}