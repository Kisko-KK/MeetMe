package com.example.faketinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth


class Login : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        //hide actionBar
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        val registerTextView = view.findViewById<TextView>(R.id.register_textview)
        val loginButton = view.findViewById<Button>(R.id.login_button)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar_login)

        val registerFragment = Register()
        val mainFragment = MainFragment()
        firebaseAuth = FirebaseAuth.getInstance()



        loginButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = view.findViewById<EditText>(R.id.inputEmail_login).text.toString()
            val password = view.findViewById<EditText>(R.id.inputPassword_login).text.toString()

            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {

                        //send userId to mainFragment
                        val userId = it.user?.uid.toString()
                        val bundle = Bundle()
                        bundle.putString("id",userId)
                        mainFragment.arguments = bundle

                        //go to mainFragment
                        val fragmentTransaction: FragmentTransaction? =
                            activity?.supportFragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.fragmentContainerView, mainFragment)
                        fragmentTransaction?.commit()
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(activity, "Please enter valid data", Toast.LENGTH_LONG)
                            .show()
                    }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(activity, "Please enter valid data", Toast.LENGTH_LONG).show()
            }


        }
        //go to register fragment
        registerTextView.setOnClickListener {
            val fragmentTransaction: FragmentTransaction? =
                activity?.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragmentContainerView, registerFragment)
            fragmentTransaction?.commit()
        }

        return view
}}