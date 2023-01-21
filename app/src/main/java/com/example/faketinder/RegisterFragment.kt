package com.example.faketinder

import  android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth


class RegisterFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val registerButton = view.findViewById<Button>(R.id.login_button);
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance()
        val bundle = Bundle()
        val creatingAccountFragment = CreatingAccountFragment()

        var userId:String


        registerButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email = view.findViewById<EditText>(R.id.inputEmail_register).text.toString()
            val password = view.findViewById<EditText>(R.id.inputPassword_register).text.toString()

            try {
                firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnSuccessListener{
                        userId = it.user?.uid.toString()

                        bundle.putString("id",userId)
                        creatingAccountFragment.arguments = bundle

                        //switch to Create Account Fragment
                        val fragmentTransaction: FragmentTransaction? =
                            activity?.supportFragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.fragmentContainerView, creatingAccountFragment)
                        fragmentTransaction?.commit()
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(activity,"Please enter valid data",Toast.LENGTH_LONG).show()
                    }
            }catch (e:java.lang.Exception){
                progressBar.visibility = View.GONE
                Toast.makeText(activity,"Please enter valid data",Toast.LENGTH_LONG).show()
            }
        }
        return view;
    }
}