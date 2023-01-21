package com.example.faketinder

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentTransaction
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreatingAccountFragment : Fragment() {

    private val db = Firebase.firestore
    private var userImage : ImageView ?= null
    private var filepath : Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_creating_account, container, false)


        val nameEditText = view.findViewById<EditText>(R.id.inputFirstName)
        val ageBar = view.findViewById<SeekBar>(R.id.ageBar)
        val genderRadioGroup = view.findViewById<RadioGroup>(R.id.genderRadioGroup)
        val descriptionEditText = view.findViewById<EditText>(R.id.inputDescription)
        val interestedInRadioGroup = view.findViewById<RadioGroup>(R.id.interestedInRadioGroup)
        val registerButton = view.findViewById<Button>(R.id.register_button)
        val ageTextView = view.findViewById<TextView>(R.id.ageTextView)
        userImage = view.findViewById(R.id.userImage)
        val choosePictureButton = view.findViewById<ImageButton>(R.id.chooseImageButton)


        val mainFragment = MainFragment()
        val bundle = Bundle()

        //get userId
        val userId = arguments?.getString("id").toString()
        //send userId to main fragment
        bundle.putString("id",userId)
        mainFragment.arguments = bundle


        displayAge(ageBar,ageTextView)


        //Choose and crop picture
        val cropImage = registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val filePath : Uri? = result.uriContent
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver,filePath)
                userImage?.setImageBitmap(bitmap)
                userImage?.visibility = View.VISIBLE
                setFilePath(filePath)
            }
        }

        choosePictureButton.setOnClickListener {
            chooseImageAndCrop(cropImage)
        }


        //try to register all data to DB
        registerButton.setOnClickListener {
            try {
                //upload image to DB
                val imagePath = "images/$userId.jpg"
                uploadFile(imagePath)

                //Gender radioGroup
                val idGenderRadioGroup = genderRadioGroup.checkedRadioButtonId
                val gender = view.findViewById<RadioButton>(idGenderRadioGroup).text.toString()
                //InterestedIn radioGroup
                val idInterestedInRadioGroup = interestedInRadioGroup.checkedRadioButtonId
                val interestedIn = view.findViewById<RadioButton>(idInterestedInRadioGroup).text.toString()

                // set name and description
                val name = nameEditText.text.toString()
                val description = descriptionEditText.text.toString()

                //set age
                val startingAge = 18
                var age:Int= ageBar.progress.toString().toInt()
                age = age.plus(startingAge)



                //initialize User
                val user = User(userId,name, age, gender, description, interestedIn)

                //adding User to DB
                db.collection("users")
                    .document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        //go to main fragment
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




    private fun setFilePath(filePath : Uri?) {
        this.filepath = filePath
    }

    private fun chooseImageAndCrop( cropImage: ActivityResultLauncher<CropImageContractOptions>) {
        cropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeCamera = true,
                    imageSourceIncludeGallery = true,
                    aspectRatioX = 397,
                    aspectRatioY = 578,
                    fixAspectRatio = true,
                    ),
            ),
        )
    }

    private fun uploadFile(imagePath: String) {
        val imageRef : StorageReference = FirebaseStorage.getInstance().reference.child(imagePath)
        if(filepath == null){
            throw Exception()
        }
        else{
            imageRef.putFile(filepath!!)
        }

    }

    private fun displayAge(ageBar:SeekBar, ageTextView : TextView){
        ageBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
            {
                val startingAge = 18
                ageTextView.text = progress.plus(startingAge).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

}

