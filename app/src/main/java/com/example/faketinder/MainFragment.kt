package com.example.faketinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class MainFragment : Fragment() {

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val imageView = view.findViewById<ImageView>(R.id.image_main)

        //get userId
        val userId : String? = arguments?.getString("id").toString()
        println("SAD")

        //get current user
        if (userId != null) {
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { result ->
                    val currentUser = result.toObject(User::class.java)
                    if (currentUser != null) {
                        setCardImage(currentUser.id, imageView)

                        //find all his potential matches
                        db.collection("users")
                            .whereNotEqualTo("id",userId)
                            .get()
                            .addOnSuccessListener { result ->
                                var potentialMatches = ArrayList<User>();
                                for (data in result.documents) {
                                    val user = data.toObject(User::class.java)
                                    if (user != null) {
                                        if(isAGoodMatch(user,currentUser)){
                                            potentialMatches.add(user)
                                        }
                                    }
                                }
                                println("LISTA")
                                println(potentialMatches)

                            }
                            .addOnFailureListener {
                            }
                    }
                }
                .addOnFailureListener {
                }
        }









        return view
    }

    private fun isAGoodMatch(user: User, currentUser: User) :Boolean{
        if(currentUser.interestedIn == "Female" && user.gender == "Female" && (user.interestedIn == "Both" || user.interestedIn==currentUser.gender)){
            return true
        }
        else if(currentUser.interestedIn == "Male" && user.gender == "Male" && (user.interestedIn == "Both" || user.interestedIn==currentUser.gender)){
            return true
        }
        else if(currentUser.interestedIn == "Both" && (user.interestedIn == "Both" || user.interestedIn==currentUser.gender)){
            return true
        }
        return false
    }

    private fun setCardImage(userId:String, imageView:ImageView) {
        val imagePath = "images/$userId.jpg"
        var storageRef = FirebaseStorage.getInstance().reference.child(imagePath)


        context?.let {
            Glide.with(it)
                .load(storageRef)
                .into(imageView)

        }
    }


}