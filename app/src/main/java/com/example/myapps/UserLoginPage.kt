package com.example.myapps

import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_username.*
import kotlinx.android.synthetic.main.user_login_page.*

class UserLoginPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val existUserID = FirebaseAuth.getInstance().currentUser?.uid
    var isExist : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_login_page)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            doLogin()
        }

        forgotPasswordText.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        back_btn_log.setOnClickListener {
            onBackPressed()
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

    private fun doLogin(){
        if(emailEdit.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter email address",Toast.LENGTH_SHORT).show()
            emailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches()){
            Toast.makeText(this,"Please enter valid email address",Toast.LENGTH_SHORT).show()
            emailEdit.requestFocus()
            return
        }

        if(passwordEdit.text.toString().isEmpty()){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_SHORT).show()
            passwordEdit.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString()).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this,"Invalid email or password",Toast.LENGTH_SHORT).show()
            }else{
                if(FirebaseAuth.getInstance().currentUser != null){
                    if (FirebaseAuth.getInstance().currentUser?.isEmailVerified!!){
                    val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(FirebaseAuth.getInstance().currentUser?.uid)
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val user = it.getValue(User::class.java)
                                if (user != null) {
                                    isExist = true
                                    goUserPage()
                                }
                            }
                            if (isExist == false){
                                FirebaseAuth.getInstance().signOut()
                                Toast.makeText(baseContext, "Invalid email address.",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                  }
                    else{
                        Toast.makeText(baseContext, "Please verify your email address.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(baseContext, "Login failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goUserPage(){
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }


}

