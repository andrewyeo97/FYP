package com.example.myapps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_rating.*
import kotlinx.android.synthetic.main.activity_manage_account.*
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class ManageAccountActivity : AppCompatActivity() {

    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_account)
        back_btn_profile.setOnClickListener {
            onBackPressed()
        }

        labelProfilePhoto.setOnClickListener {
            changeProfilePhoto()
        }
        profile_pic.setOnClickListener {
            changeProfilePhoto()
        }
        arrowProfilePic.setOnClickListener {
            changeProfilePhoto()
        }
        ownerUsername.setOnClickListener {
            changeUsername()
        }
        usernameProfiletxt.setOnClickListener {
            changeUsername()
        }
        arrowUsername.setOnClickListener {
            changeUsername()
        }
        passwordLabels.setOnClickListener {
            changePassword()
        }
        arrowPassword.setOnClickListener {
            changePassword()
        }

    }

    override fun onStart() {
        super.onStart()
        loadProfile()

    }

    private fun changeUsername(){
        val intent = Intent(this,EditUsernameActivity::class.java)
        startActivity(intent)
    }

    private fun changePassword(){
        val intent = Intent(this,EditPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun loadProfile(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(currentuserID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null){
                        usernameProfiletxt.text = user.username
                        emailAns.text = user.email
                        Picasso.get().load(user.profileImageUrl).into(profile_pic)
                    }

                }
            }
        })
    }

    private fun changeProfilePhoto(){
        val intent =Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            profile_pic.setImageBitmap(bitmap)
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage(){

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/ProfileImages/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveUserToDatabase(it.toString())
            }

        }
    }

    private fun saveUserToDatabase(profileImageUrl: String){
        val ref2 = FirebaseDatabase.getInstance().getReference("/Users/$currentuserID")
        ref2.child("profileImageUrl").setValue(profileImageUrl)
        Toast.makeText(baseContext, "Profile pic uploaded", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,ManageAccountActivity::class.java)
        startActivity(intent)
        finish()
    }




}

