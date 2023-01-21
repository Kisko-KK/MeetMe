package com.example.faketinder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class UserRecycleAdapter(
    val items: ArrayList<User>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.userlist,
                parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> {
                holder.bind(position, items[position])
            }
        }
    }


    class UserViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        private val userImage =
            view.findViewById<ImageView>(R.id.userImageView_recycleView)
        private val userName =
            view.findViewById<TextView>(R.id.nameTextView_userList)
        private val userDescription =
            view.findViewById<TextView>(R.id.descriptionTextView_userList)
        private val userAge =
            view.findViewById<TextView>(R.id.ageTextView_userList)
        fun bind(
            index: Int,
            user: User
        ) {
            val imagePath = "images/${user.id}.jpg"
            val storageRef = FirebaseStorage.getInstance().reference.child(imagePath)

            Glide.with(view.context)
                .load(storageRef)
                .into(userImage)


            userName.text = user.name
            userDescription.text = user.description
            userAge.text = user.age.toString()
        }
    }
}