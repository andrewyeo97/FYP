package com.example.myapps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main_.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_)
        auth = FirebaseAuth.getInstance()
        btnLoginAsUser.setOnClickListener {
            login()
        }

        btnSignUp.setOnClickListener {
            reg()
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                updateUI(currentUser)
            } else {
                FirebaseAuth.getInstance().signOut()
            }
        }



    }
    override fun onBackPressed() {
        finishAffinity()
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            }
        }

    private fun login(){
        FirebaseAuth.getInstance().signOut()
        intent = Intent(this, UserLoginPage::class.java )
        startActivity(intent)
    }

    private fun reg(){
        FirebaseAuth.getInstance().signOut()
        intent = Intent(this, RegisterActivity::class.java )
        startActivity(intent)
    }

    companion object{
        fun launchIntent(context: Context){
            val intent = Intent(context,MainActivity::class.java)
            context.startActivity(intent)
        }

        fun launchIntentClearTask(context: Context){
            val intent = Intent(context,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }
    }

