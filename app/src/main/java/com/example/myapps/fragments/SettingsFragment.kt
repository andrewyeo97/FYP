package com.example.myapps.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapps.DashboardActivity
import com.example.myapps.MainActivity
import com.example.myapps.MainActivity.Companion.launchIntentClearTask

import com.example.myapps.R
import com.example.myapps.UserLoginPage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)



    }

    override fun onStart() {
        super.onStart()

        signOutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context, R.style.AlertTheme)
            builder.setTitle("Sign Out")
            builder.setIcon(R.drawable.ic_action_warning)
            builder.setMessage("Do you want to sign out?")
            builder.setPositiveButton("Yes"){ dialog, which ->
                logout()
            }
            builder.setNegativeButton("No"){ dialog, which ->
                Log.d("Exit","Exit")
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()

        }

        imgViewSignOut.setOnClickListener {
            val builder = AlertDialog.Builder(context, R.style.AlertTheme)
            builder.setTitle("Sign Out")
            builder.setIcon(R.drawable.ic_action_warning)
            builder.setMessage("Do you want to sign out?")
            builder.setPositiveButton("Yes"){ dialog, which ->
                logout()
            }
            builder.setNegativeButton("No"){ dialog, which ->
                Log.d("Exit","Exit")
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
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



}
