package com.example.myapps.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapps.*
import com.example.myapps.MainActivity.Companion.launchIntentClearTask

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_manage_account.*
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 */

class SettingsFragment : Fragment() {

    val currentuserID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onStart() {
        super.onStart()
        loadProfile()
        signOutBtn.setOnClickListener {
            verifyExit()
        }

        imgViewSignOut.setOnClickListener {
            verifyExit()
        }

        manageAccBtn.setOnClickListener {
            goManageActivity()
        }

        manageAccBtn2.setOnClickListener {
            goManageActivity()
        }

        imgViewManageAccount.setOnClickListener {
            goManageActivity()
        }

        forwardbutton1.setOnClickListener{
            goManageActivity()
        }

        forwardbutton2.setOnClickListener{
            verifyExit()
        }

    }

    private fun verifyExit(){
        val builder = AlertDialog.Builder(context, R.style.AlertTheme)
        builder.setTitle("Exit")
        builder.setIcon(R.drawable.ic_action_warning)
        builder.setMessage("Do you want to sign out?")
        builder.setPositiveButton("Yes"){ dialog, which ->
            logout()
        }
        builder.setNegativeButton("No"){ dialog, which ->
            Log.d("NoExit","NOExit")
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun goManageActivity(){
        val intent = Intent(context, ManageAccountActivity::class.java)
        startActivity(intent)
    }

    private fun logout(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context,MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        startActivity(intent)
    }

    private fun loadProfile(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(currentuserID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null){
                        manageAccBtn.text = user.username
                        Picasso.get().load(user.profileImageUrl).into(imgViewManageAccount)
                    }

                }
            }
        })
    }



}
