package com.example.faketinder

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class ExploreFragment : Fragment() {

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        val imageView = view.findViewById<ImageView>(R.id.image_main)
        val likeButton = view.findViewById<ImageView>(R.id.likeButton)
        val dislikeButton = view.findViewById<ImageView>(R.id.dislikeButton)
        val nameTextView = view.findViewById<TextView>(R.id.nameTextView_main)
        val ageTextView = view.findViewById<TextView>(R.id.ageTextView_main)
        val noMoreMatchesTextView = view.findViewById<TextView>(R.id.noMoreMatchesTextView)


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

                        //find all his potential matches
                        db.collection("users")
                            .whereNotEqualTo("id",userId)
                            .get()
                            .addOnSuccessListener { result ->
                                val potentialMatches = ArrayList<User>()
                                for (data in result.documents) {
                                    val user = data.toObject(User::class.java)
                                    if (user != null) {
                                        if(isAGoodMatch(user,currentUser)){
                                            potentialMatches.add(user)
                                        }
                                    }
                                }
                                //put first potential match
                                setCardData(potentialMatches[0], imageView, nameTextView, ageTextView)




                                var index = 0
                                dislikeButton.setOnClickListener {

                                    dislikeButton.animation = AnimationUtils.loadAnimation(context,R.anim.scale)

                                    index++;
                                    if (index > potentialMatches.size-1)
                                    {
                                        disableExploring(likeButton, dislikeButton, imageView, nameTextView, ageTextView, noMoreMatchesTextView)
                                    }
                                    else{
                                        setCardData(potentialMatches[index], imageView, nameTextView, ageTextView)
                                    }
                                }

                                likeButton.setOnClickListener{

                                    likeButton.animation = AnimationUtils.loadAnimation(context,R.anim.scale)

                                    db.collection("users").document(currentUser.id)
                                        .update("likes", FieldValue.arrayUnion(potentialMatches[index].id))

                                    index++;
                                    if (index > potentialMatches.size-1)
                                    {
                                        disableExploring(likeButton, dislikeButton, imageView, nameTextView, ageTextView, noMoreMatchesTextView)
                                    }
                                    else{
                                        setCardData(potentialMatches[index], imageView, nameTextView, ageTextView)
                                    }
                                }

                            }
                    }
                }
        }
        return view
    }

    private fun disableExploring(likeButton: ImageView, dislikeButton: ImageView, imageView: ImageView, nameTextView: TextView, ageTextView: TextView, noMoreMatchesTextView: TextView) {
        likeButton.visibility = View.GONE
        dislikeButton.visibility = View.GONE
        imageView.visibility = View.GONE
        nameTextView.visibility = View.GONE
        ageTextView.visibility = View.GONE
        noMoreMatchesTextView.visibility = View.VISIBLE
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

    private fun setCardData(user : User, imageView:ImageView, nameTextView: TextView, ageTextView: TextView) {
        val imagePath = "images/${user.id}.jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child(imagePath)

        context?.let {
            Glide.with(it)
                .load(storageRef)
                .into(imageView)
        }

        nameTextView.text = user.name
        ageTextView.text = user.age.toString()
    }


}