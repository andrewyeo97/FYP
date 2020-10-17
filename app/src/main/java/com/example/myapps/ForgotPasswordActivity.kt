package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        return_btn.setOnClickListener {
            onBackPressed()
        }

        resetButton.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword(){
        val email = forgetPasswordEmail.text.toString().trim()
        if(email.isEmpty()){
            forgetPasswordEmail.error = "Please enter your email"
            forgetPasswordEmail.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            forgetPasswordEmail.error = "Email input is invalid"
            forgetPasswordEmail.requestFocus()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Email sent", Toast.LENGTH_SHORT).show()
                Intent(this,UserLoginPage::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }else{
                Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}