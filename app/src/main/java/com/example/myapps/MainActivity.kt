package com.example.myapps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.os.postDelayed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main_.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    public override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null ) {
            if (auth.currentUser!!.isEmailVerified) {
                val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(FirebaseAuth.getInstance().currentUser?.uid)
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val user = it.getValue(User::class.java)
                            if (user != null) {
                                goUserPage()
                            }
                        }
                    }
                })
            }

            val ref2 = FirebaseDatabase.getInstance().getReference("/Staffs").orderByChild("staff_id").equalTo(FirebaseAuth.getInstance().currentUser?.uid)
            ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val staff = it.getValue(Staff::class.java)
                        if (staff != null) {
                            goStaffPage()
                        }
                    }
                }
            })


        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_)

        btnLoginAsUser.setOnClickListener {
            login()
        }

        btnSignUp.setOnClickListener {
            reg()
        }

        btnLoginAsStaff.setOnClickListener {
            startActivity(Intent(this, StaffLoginActivity::class.java))
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

        fun launchIntentClearTask(context: DashboardActivity){
            val intent = Intent(context,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }
    }

    private fun goUserPage(){
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun goStaffPage(){
        startActivity(Intent(this, swmDashboardActivity::class.java))
        finish()
    }
    }

