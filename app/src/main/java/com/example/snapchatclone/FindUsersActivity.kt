package com.example.snapchatclone

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapchatclone.RecyclerViewFollow.FollowRVAdapter
import com.example.snapchatclone.RecyclerViewFollow.FollowRVHolders
import com.example.snapchatclone.RecyclerViewFollow.FollowRVObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class FindUsersActivity : AppCompatActivity() {
    private lateinit var recyclerView:RecyclerView
    lateinit var adapter:RecyclerView.Adapter<FollowRVHolders>
    private lateinit var layoutManager:RecyclerView.LayoutManager

    private lateinit var inputEditText: EditText

    private val results: ArrayList<FollowRVObject> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_users)

        inputEditText=findViewById(R.id.inputEditText)
        val searchButton:Button=findViewById<Button>(R.id.searchUsersButton)

        recyclerView=findViewById(R.id.recyclerView)
        recyclerView.isNestedScrollingEnabled=false
        recyclerView.setHasFixedSize(false)

        layoutManager=LinearLayoutManager(application)
        recyclerView.layoutManager=layoutManager
        adapter=FollowRVAdapter(getDataSet(),application)
        recyclerView.adapter=adapter

        searchButton.setOnClickListener {
            clear()
            listenForData()
        }

        //Keyboard Management

    }


    private fun getDataSet(): List<FollowRVObject> {
        listenForData()
        return results

    }
    private fun listenForData() {
        val userDb: DatabaseReference=Firebase.database.reference.child("users")
        val query :Query= userDb.orderByChild("email")
            .startAt(inputEditText.text.toString())
            .endAt(inputEditText.text.toString()+"\uf8ff")

        query.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var email=""
                var name=""
                val uid=snapshot.ref.key
                if(snapshot.child("email").value!=null) {
                    email=snapshot.child("email").value.toString()
                }
                if(snapshot.child("name").value!=null) {
                    name=snapshot.child("name").value.toString()
                }
                if(email != Firebase.auth.currentUser!!.email){
                    val userObject=FollowRVObject(email, uid!!,name)
                    results.add(userObject)
                    adapter.notifyDataSetChanged()
                }

            }
            override fun onCancelled(error: DatabaseError) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}

        })

    }
    private fun clear() {
        this.results.clear()
        adapter.notifyDataSetChanged()

    }

}