package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_add_rating.*
import kotlinx.android.synthetic.main.activity_edit_password.*
import kotlinx.android.synthetic.main.activity_edit_username.*

class EditUsernameActivity : AppCompatActivity() {

    val currentuserID = FirebaseAuth.getInstance().uid.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_username)

        back_btn_editUsername.setOnClickListener {
            onBackPressed()
        }

        saveButton.setOnClickListener {
            changeUsername()
        }

    }

    override fun onStart() {
        super.onStart()
        loadUsername()
    }

    private fun loadUsername(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(currentuserID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        editUsernameAns.setText(user.username)
                    }
                }
            }
        })
    }

    private fun changeUsername(){
        val editUsername = editUsernameAns.text.toString()
        if(editUsernameAns.text.isNotEmpty()) {
            val ref = FirebaseDatabase.getInstance().getReference("Users/$currentuserID")
            ref.child("username").setValue(editUsername)
            Toast.makeText(baseContext, "Username edited successfully", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }else{
            Toast.makeText(baseContext, "Please enter your username", Toast.LENGTH_SHORT).show()
        }
    }


}