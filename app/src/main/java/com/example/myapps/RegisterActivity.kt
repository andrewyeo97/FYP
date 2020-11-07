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
import kotlinx.android.synthetic.main.activity_edit_password.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

    var currentDate = Calendar.getInstance().time
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

        back_btn_reg.setOnClickListener {
            onBackPressed()
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
            Toast.makeText(this,"Please enter your username",Toast.LENGTH_SHORT).show()
            regUsernameEdit.requestFocus()
            return
        }

        if(regEmailEdit.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter your email address",Toast.LENGTH_SHORT).show()
            regEmailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(regEmailEdit.text.toString()).matches()){
            Toast.makeText(this,"Please enter valid email address",Toast.LENGTH_SHORT).show()
            regEmailEdit.requestFocus()
            return
        }

        if(regPasswordEdit.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show()
            regPasswordEdit.requestFocus()
            return
        }

        if(confirm_regPasswordEdit.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter confirm password",Toast.LENGTH_SHORT).show()
            confirm_regPasswordEdit.requestFocus()
            return
        }

        if(selectedPhotoUri == null){
            Toast.makeText(this,"Please select your profile pic",Toast.LENGTH_SHORT).show()
            return
        }

        val email = regEmailEdit.text.toString()
        val pass = regPasswordEdit.text.toString()
        val conPass = confirm_regPasswordEdit.text.toString()
        if(conPass == pass) {
            if (isValidPassword(pass)) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) {
                            Toast.makeText(baseContext, "Register failed.", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            user.id = FirebaseAuth.getInstance().uid.toString()
                            uploadImageToFirebaseStorage()

                            FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            baseContext, "Email verification sent.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(Intent(this, UserLoginPage::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            baseContext,
                                            "Register failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                        }
                    }
            } else {
                Toast.makeText(
                    baseContext,
                    "Password does not fulfill the criteria",
                    Toast.LENGTH_SHORT
                ).show()
                regPasswordEdit.requestFocus()
                return
            }
        }else{
            Toast.makeText(
                baseContext,
                "Password mismatch",
                Toast.LENGTH_SHORT
            ).show()
            confirm_regPasswordEdit.requestFocus()
            return
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
        val ref = FirebaseDatabase.getInstance().getReference("/Users/${user.id}")
        user.email = regEmailEdit.text.toString()
        user.username = regUsernameEdit.text.toString()
        user.profileImageUrl = profileImageUrl
        user.registerDate = currentDate
        ref.setValue(user)
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

}
