package com.example.snapchatclone

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.snapchatclone.RecyclerViewStory.StoryRVAdapter
import com.example.snapchatclone.RecyclerViewStory.StoryRVHolders
import com.example.snapchatclone.RecyclerViewStory.StoryRVObject
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class StoryFragment: Fragment() {
    private var swipeContainer: SwipeRefreshLayout? = null
    private val results: ArrayList<StoryRVObject> = ArrayList()

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerView.Adapter<StoryRVHolders>
    lateinit var layoutManager: RecyclerView.LayoutManager


    fun newInstance():StoryFragment {
        return StoryFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.story_fragment,container,false)

        swipeContainer=view.findViewById(R.id.swipeContainer)

        swipeContainer?.setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
        swipeContainer?.setColorSchemeColors(Color.WHITE)

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



    private fun getDataSet(): List<StoryRVObject> {
        listenForData()
        return results

    }

    private fun listenForData() {
        Log.i("Inside","Listenin for data,....."+UserInformation.listFollowing.size)
        for (i in 0 until UserInformation.listFollowing.size) {
            val followingStoryDb=Firebase.database.reference
                .child("users")
                .child(UserInformation.listFollowing[i])

            followingStoryDb.addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name=snapshot.child("name").value.toString()
                    val email=snapshot.child("email").value.toString()
                    val uid=snapshot.ref.key
                    var begTime:Long=0
                    var endTime:Long=0
                    for(storySnapshot in snapshot.child("story").children){

                        if(storySnapshot.child("begTimeStamp").value!=null){
                            begTime= storySnapshot.child("begTimeStamp").value.toString().toLong()
                        }
                        if(storySnapshot.child("endTimeStamp").value!=null){
                            endTime= storySnapshot.child("endTimeStamp").value.toString().toLong()
                        }
                        val currentTime=System.currentTimeMillis()
                        if(currentTime in begTime..endTime){
                            val storyObject=StoryRVObject(email,uid!!,"story",name)
                            if(!results.contains(storyObject)){
                                results.add(storyObject)
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
    private fun clear() {
        this.results.clear()
        adapter.notifyDataSetChanged()

    }

}