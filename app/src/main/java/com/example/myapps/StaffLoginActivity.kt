package com.example.myapps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_staff_login.*

class StaffLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var isExist : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_login)

        auth = FirebaseAuth.getInstance()

        StaffLoginButton.setOnClickListener {
            doStaffLogin()
        }

        staff_forgetPass.setOnClickListener {
            val intent = Intent(this, wm_staff_forgetPassword_Activity::class.java)
            startActivity(intent)
        }

        login_back.setOnClickListener {
            onBackPressed()
        }

        go_register_page.setOnClickListener {
            val intent = Intent(this, swm_verify_staff_position_Activity::class.java)
            startActivity(intent)
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

        fun goStaffPage(){
            startActivity(Intent(this, swmDashboardActivity::class.java))
            finish()
        }


        auth.signInWithEmailAndPassword(StaffLoginEmailEdit.text.toString(), StaffLoginPasswordEdit.text.toString()).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }else{
                if(FirebaseAuth.getInstance().currentUser != null){
                    val ref = FirebaseDatabase.getInstance().getReference("/Staffs").orderByChild("staff_id").equalTo(FirebaseAuth.getInstance().currentUser?.uid)
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val staff = it.getValue(Staff::class.java)
                                if (staff != null) {
                                    isExist = true
                                    goStaffPage()
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
            }
        }

    }
}