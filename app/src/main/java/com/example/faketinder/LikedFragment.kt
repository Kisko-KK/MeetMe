package com.example.faketinder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LikedFragment : Fragment() {

    private lateinit var recyclerAdapter: UserRecycleAdapter
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_liked, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)


        //get userId
        val userId : String? = arguments?.getString("id").toString()


        var likedUsersId = ArrayList<String>()
        val docRef = db.collection("users").document(userId.toString())
        docRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {

                val user = snapshot.toObject(User::class.java)
                if (user != null) {
                    likedUsersId = user.likes
                }

                val likedUserList = ArrayList<User>()
                for (userId in likedUsersId){
                    db.collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener {
                            val likedUser = it.toObject(User::class.java)
                            if (likedUser != null) {
                                likedUserList.add(likedUser)
                            }
                            recyclerAdapter = UserRecycleAdapter(likedUserList)
                            recyclerView.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = recyclerAdapter
                            }

                        }
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
        return view
    }

}