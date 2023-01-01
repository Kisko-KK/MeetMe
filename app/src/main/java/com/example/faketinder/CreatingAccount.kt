package com.example.faketinder

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreatingAccount : Fragment() {

    private val db = Firebase.firestore
    lateinit var filepath : Uri
    private var userImage : ImageView ?= null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_creating_account, container, false)

        var nameEditText = view.findViewById<EditText>(R.id.inputFirstName)
        var ageBar = view.findViewById<SeekBar>(R.id.ageBar)
        var genderRadioGroup = view.findViewById<RadioGroup>(R.id.genderRadioGroup)
        var descriptionEditText = view.findViewById<EditText>(R.id.inputDescription)
        var interestedInRadioGroup = view.findViewById<RadioGroup>(R.id.interestedInRadioGroup)
        var registerButton = view.findViewById<Button>(R.id.register_button)
        var ageTextView = view.findViewById<TextView>(R.id.ageTextView)
        userImage = view.findViewById(R.id.userImage)
        var choosePictureButton = view.findViewById<ImageButton>(R.id.chooseImageButton)


        Log.d("TAG",arguments?.getString("id").toString())
        val userId = arguments?.getString("id").toString()

        val mainFragment = MainFragment()

        displayAge(ageBar,ageTextView)

        choosePictureButton.setOnClickListener {
            startFileChooser()
        }




        registerButton.setOnClickListener {
            try {
                //Gender radioGroup
                val idGenderRadioGroup = genderRadioGroup.checkedRadioButtonId
                val gender = view.findViewById<RadioButton>(idGenderRadioGroup).text.toString()
                //Interested in radioGroup
                val idInterestedInRadioGroup = interestedInRadioGroup.checkedRadioButtonId
                val interestedIn = view.findViewById<RadioButton>(idInterestedInRadioGroup).text.toString()

                //name and description
                val name = nameEditText.text.toString()
                val description = descriptionEditText.text.toString()

                //age
                val startingAge:Int = 18
                var age:Int= ageBar.progress.toString().toInt()
                age = age.plus(startingAge)

                var imagePath : String = "images/$userId.jpg"
                uploadFile(imagePath)

                //initialize User
                var user = User(userId,name, age, gender, description, interestedIn)

                //adding User to DB
                db.collection("users")
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        val fragmentTransaction: FragmentTransaction? =
                            activity?.supportFragmentManager?.beginTransaction()
                        fragmentTransaction?.replace(R.id.fragmentContainerView, mainFragment)
                        fragmentTransaction?.commit()
                    }
                    .addOnFailureListener {
                        Log.d("TAG","Couldn't connect to DB")
                    }
            }catch (e: java.lang.Exception){
                Toast.makeText(activity,"Please fill in the blanks",Toast.LENGTH_SHORT).show()
            }
        }

        return view

    }

    private fun uploadFile(imagePath: String) {

        var imageRef : StorageReference = FirebaseStorage.getInstance().reference.child(imagePath)
        imageRef.putFile(filepath)

    }

    private fun startFileChooser() {
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,111)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode===111 && resultCode=== Activity.RESULT_OK && data !=null){
            filepath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,filepath)
            userImage?.setImageBitmap(bitmap)
        }
    }




    private fun displayAge(ageBar:SeekBar, ageTextView : TextView){
        ageBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
            {
                val startingAge:Int = 18;

                ageTextView.text = progress.plus(startingAge).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
            }
        })
    }

}

