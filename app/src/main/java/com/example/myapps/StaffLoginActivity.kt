package com.example.myapps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_staff_login.*

class StaffLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_login)

        auth = FirebaseAuth.getInstance()

        StaffLoginButton.setOnClickListener {
            doStaffLogin()
        }
    }

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null ) {
            startActivity(Intent(this, swmDashboardActivity::class.java))
            finish()
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

    private fun doStaffLogin(){
        if(StaffLoginEmailEdit.text.toString().isEmpty()){
            StaffLoginEmailEdit.error = "Please enter email address"
            StaffLoginEmailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(StaffLoginEmailEdit.text.toString()).matches()){
            StaffLoginEmailEdit.error = "Please enter valid email address"
            StaffLoginEmailEdit.requestFocus()
            return
        }

        if(StaffLoginPasswordEdit.text.toString().isEmpty()){
            StaffLoginPasswordEdit.error = "Please enter password"
            StaffLoginPasswordEdit.requestFocus()
            return
        }


        auth.signInWithEmailAndPassword(StaffLoginEmailEdit.text.toString(), StaffLoginPasswordEdit.text.toString()).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }else{
                startActivity(Intent(this, swmDashboardActivity::class.java))
                finish()
            }
        }

    }
}