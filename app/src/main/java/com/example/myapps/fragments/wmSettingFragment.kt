package com.example.myapps.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapps.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_wm_setting.*

/**
 * A simple [Fragment] subclass.
 * Use the [wmSettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class wmSettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wm_setting, container, false)

    }

    override fun onStart() {
        super.onStart()
        StaffSignOutBtn.setOnClickListener {
            logout()
        }
        imgViewStaffSignOut.setOnClickListener {
            logout()
        }

        buttonAddNewStaff.setOnClickListener {
            val intent = Intent(context, swm_Staff_Register::class.java)
            startActivity(intent)
        }
        imageViewAddNewStaff.setOnClickListener{
            val intent = Intent(context, swm_Staff_Register::class.java)
            startActivity(intent)
        }

        StaffAccBtn.setOnClickListener {
            val intent = Intent(context, View_user_Profile_Activity::class.java)
            startActivity(intent)
        }
        imgViewStaffAccount.setOnClickListener {
            val intent = Intent(context, View_user_Profile_Activity::class.java)
            startActivity(intent)
        }

        imageViewReport.setOnClickListener {
            val intent = Intent(context, wm_view_report_Activity::class.java)
            startActivity(intent)
        }

        buttonViewReport.setOnClickListener {
            val intent = Intent(context, wm_view_report_Activity::class.java)
            startActivity(intent)
        }


    }
    private fun logout(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}