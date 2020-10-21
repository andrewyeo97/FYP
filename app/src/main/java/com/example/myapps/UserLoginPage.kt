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
import kotlinx.android.synthetic.main.user_login_page.*

class UserLoginPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    val existUser = FirebaseAuth.getInstance().currentUser

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
            emailEdit.error = "Please enter email address"
            emailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches()){
            emailEdit.error = "Please enter valid email address"
            emailEdit.requestFocus()
            return
        }

        if(passwordEdit.text.toString().isEmpty()){
            passwordEdit.error = "Please enter password"
            passwordEdit.requestFocus()
            return
        }


        auth.signInWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString()).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this,"Invalid Email or Password",Toast.LENGTH_SHORT).show()
            }else{
                if(FirebaseAuth.getInstance().currentUser != null){
                    if (FirebaseAuth.getInstance().currentUser?.isEmailVerified!!){
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
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



}