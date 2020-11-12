package com.example.myapps.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapps.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_user__profile_.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_wm_setting.*
import java.text.SimpleDateFormat
import java.util.*

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
            checkPosition()
        }
        imageViewAddNewStaff.setOnClickListener{
            checkPosition()
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
            val intent = Intent(context, wm_report_Activity::class.java)
            startActivity(intent)
        }

        buttonViewReport.setOnClickListener {
            val intent = Intent(context, wm_report_Activity::class.java)
            startActivity(intent)
        }

        update_his.setOnClickListener {
            val intent = Intent(context, wm_update_historyt_Activity::class.java)
            startActivity(intent)
        }
        imageViewHistory.setOnClickListener {
            val intent = Intent(context, wm_update_historyt_Activity::class.java)
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

    private fun checkPosition(){
        val currentuserID = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/Staffs").orderByChild("staff_id").equalTo(
            currentuserID
        )
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val staff = it.getValue(Staff::class.java)
                    if (staff != null) {
                        if(!staff.staff_position.toUpperCase().equals("MANAGER")){
                            val dialogBuilder = AlertDialog.Builder(activity!!)
                            dialogBuilder.setMessage(it.toString())
                                .setCancelable(false)
                                .setPositiveButton("Ok", DialogInterface.OnClickListener {
                                        dialog, id ->
                                    dialog.dismiss()
                                })
                            val alert = dialogBuilder.create()
                            alert.setTitle("Cannot Access Message")
                            alert.setMessage("You are not manager so you cannot access to the staff register page.")
                            alert.show()
                        }else{
                            val intent = Intent(context, swm_Staff_Register::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        })

    }

}
