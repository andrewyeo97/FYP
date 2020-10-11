package com.example.myapps

import androidx.core.view.drawToBitmap
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    var user = User()
    var selectedPhotoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        regButton.setOnClickListener {
          signUpUser()
        }

        photo_reg.setOnClickListener {
            val intent =Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            photo_reg.setImageBitmap(bitmap)
        }

    }

    fun exit(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        FirebaseAuth.getInstance().signOut()
        this.finish()
    }

    override fun onBackPressed() {
        exit(this)
    }
    private fun signUpUser(){

        if(regUsernameEdit.text.toString().isEmpty()){
            regUsernameEdit.error = "Please enter your username"
            regUsernameEdit.requestFocus()
            return
        }

        if(regEmailEdit.text.toString().isEmpty()){
            regEmailEdit.error = "Please enter your email address"
            regEmailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(regEmailEdit.text.toString()).matches()){
            regEmailEdit.error = "Please enter valid email address"
            regEmailEdit.requestFocus()
            return
        }

        if(regPasswordEdit.text.toString().isEmpty()){
            regPasswordEdit.error = "Please enter your password"
            regPasswordEdit.requestFocus()
            return
        }

        if(selectedPhotoUri == null){
            Toast.makeText(this,"Please select your profile pic",Toast.LENGTH_SHORT).show()
            return
        }

        val email = regEmailEdit.text.toString()
        val pass = regPasswordEdit.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(baseContext, "Register failed.", Toast.LENGTH_SHORT).show()
            }else{
                user.id = FirebaseAuth.getInstance().uid.toString()
                uploadImageToFirebaseStorage()
                startActivity(Intent(this, UserLoginPage::class.java))
                finish()
            }
        }
    }

    private fun uploadImageToFirebaseStorage(){

            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/ProfileImages/$filename")

            ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
                saveUserToDatabase(it.toString())
            }
        }

    private fun saveUserToDatabase(profileImageUrl: String){
                val ref = FirebaseDatabase.getInstance().getReference("/Users/${user.id}")
                user.email = regEmailEdit.text.toString()
                user.username = regUsernameEdit.text.toString()
                user.profileImageUrl = profileImageUrl
                ref.setValue(user)
    }

    }

