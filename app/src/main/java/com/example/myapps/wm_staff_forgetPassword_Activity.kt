package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_wm_staff_forget_password_.*

class wm_staff_forgetPassword_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_staff_forget_password_)

        send_mail_button.setOnClickListener {
            resetPassword()
        }
        click_to_login.setOnClickListener {
            val intent = Intent(this, StaffLoginActivity::class.java)
            startActivity(intent)
        }
        forget_back.setOnClickListener {
            onBackPressed()
        }

    }


    private fun resetPassword(){
        val email = recovery_email.text.toString().trim()
        if(email.isEmpty()){
            recovery_email.error = "Please enter your email"
            recovery_email.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            recovery_email.error = "Email input is invalid"
            recovery_email.requestFocus()
            return
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Email sent", Toast.LENGTH_SHORT).show()
//                Intent(this,UserLoginPage::class.java).also {
//                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(it)
            }
//            }else{
//                Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT).show()
//            }
        }
    }
}