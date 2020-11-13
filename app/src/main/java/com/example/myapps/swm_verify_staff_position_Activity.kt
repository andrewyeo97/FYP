package com.example.myapps

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_swm_verify_staff_position_.*

class swm_verify_staff_position_Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    var isExist : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swm_verify_staff_position_)

        auth = FirebaseAuth.getInstance()

        verify_back.setOnClickListener {
            onBackPressed()
        }

        verify_forget_pass.setOnClickListener {
            val intent = Intent(this, wm_staff_forgetPassword_Activity::class.java)
            startActivity(intent)
        }

        verify_btn.setOnClickListener {
            verifyPosition()
        }

    }

    private fun goRegister(){
        val intent = Intent(this, swm_Staff_Register::class.java)
        startActivity(intent)
    }


    private fun verifyPosition(){
        if(verify_email.text.toString().isEmpty()){
            verify_email.error = "Please enter email address..."
            verify_email.requestFocus()
            return
        }
        if(verify_email.text.toString().isEmpty()){
            verify_password.error = "Please enter password..."
            verify_password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(verify_email.text.toString(), verify_password.text.toString()).addOnCompleteListener {
            if(!it.isSuccessful){
                Toast.makeText(this,"Invalid Email or Password", Toast.LENGTH_SHORT).show()
            }else{
                if(FirebaseAuth.getInstance().currentUser !=null){
                    val ref = FirebaseDatabase.getInstance().getReference("/Staffs").orderByChild("staff_id").equalTo(FirebaseAuth.getInstance().currentUser?.uid)
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val staff = it.getValue(Staff::class.java)
                                if (staff != null) {
                                    isExist = true
                                    if(staff.staff_position.toString().toUpperCase().equals("MANAGER")){
                                        goRegister()
                                        FirebaseAuth.getInstance().signOut()
                                    }else{
                                        val dialogBuilder = AlertDialog.Builder(this@swm_verify_staff_position_Activity)
                                        dialogBuilder.setMessage(it.toString())
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", DialogInterface.OnClickListener{
                                                    dialog, id ->
                                                dialog.dismiss()
                                            })
                                        val alert = dialogBuilder.create()
                                        alert.setTitle("Disable Access Message")
                                        alert.setMessage("You are not manager so you cannot access to the staff register page.")
                                        alert.show()
                                    }
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