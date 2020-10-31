package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.os.postDelayed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_loading_screen.*

private lateinit var auth: FirebaseAuth

class LoadingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)
        Handler().postDelayed({
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null ) {
            if (auth.currentUser!!.isEmailVerified) {
                val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(
                    FirebaseAuth.getInstance().currentUser?.uid)
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

            val ref2 = FirebaseDatabase.getInstance().getReference("/Staffs").orderByChild("staff_id").equalTo(
                FirebaseAuth.getInstance().currentUser?.uid)
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
        else{
            goMainPage()
        }},1000)
    }

    private fun goUserPage(){

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()

    }

    private fun goStaffPage(){

        startActivity(Intent(this, swmDashboardActivity::class.java))
        finish()
    }

    private fun goMainPage(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


