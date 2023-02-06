package com.example.faketinder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MatchedFragment : Fragment() {

    private lateinit var recyclerAdapter: UserRecycleAdapter
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_matched, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView_Matched)

        //get userId
        val userId : String? = arguments?.getString("id").toString()


        var matchedUsers = ArrayList<User>()
        var likedCurrentUsersId = ArrayList<String>()
        val docRef = db.collection("users").document(userId.toString())
        docRef.addSnapshotListener { snapshot, _ ->
            if (snapshot != null && snapshot.exists()) {
                val currentUser = snapshot.toObject(User::class.java)
                if (currentUser != null) {
                    likedCurrentUsersId = currentUser.likes
                }

                //for each user in likedCurrentUsersId find if user likes current user
                var likedUsersId = ArrayList<String>()
                for (id in likedCurrentUsersId){
                    db.collection("users").document(id.toString())
                        .addSnapshotListener{ snapshot, _ ->
                            if (snapshot != null && snapshot.exists()){
                                val user = snapshot.toObject(User::class.java)
                                if (user != null) {
                                    likedUsersId = user.likes
                                    if (likedUsersId.contains(userId)){
                                        if(!matchedUsers.contains(user)){
                                            matchedUsers.add(user)
                                        }
                                    }
                                }
                                recyclerAdapter = UserRecycleAdapter(matchedUsers)
                                recyclerView.apply {
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = recyclerAdapter
                                }

                            }

                        }
                }
            }
        }

        return view
    }

}