package com.example.myapps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        regButton.setOnClickListener {
          signUpUser()
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
        val username = regEmailEdit.text.toString().trim()
        val email = regEmailEdit.text.toString().trim()
        val pass = regPasswordEdit.text.toString().trim()

        if(regEmailEdit.text.toString().isEmpty()){
            regEmailEdit.error = "Please enter email address"
            regEmailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(regEmailEdit.text.toString()).matches()){
            regEmailEdit.error = "Please enter valid email address"
            regEmailEdit.requestFocus()
            return
        }

        if(regPasswordEdit.text.toString().isEmpty()){
            regPasswordEdit.error = "Please enter password"
            regPasswordEdit.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(regEmailEdit.text.toString(), regPasswordEdit.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val ref = FirebaseDatabase.getInstance().getReference("Users")
                    val uid = FirebaseAuth.getInstance().uid ?:""
                    val myuser = User(uid,username,email)

                    ref.child(uid).setValue(myuser)

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task->
                            if(task.isSuccessful) {

                        startActivity(Intent(this, UserLoginPage::class.java))
                        finish()
                            }
                        }
                    }
                else {
                    Toast.makeText(baseContext, "Register failed.",
                        Toast.LENGTH_SHORT).show()
                }
                }

            }
    }

