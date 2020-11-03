package com.example.myapps.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapps.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main_.*
import kotlinx.android.synthetic.main.activity_swm__add__ing_.*
import kotlinx.android.synthetic.main.fragment_wm_add.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [wmAddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class wmAddFragment : Fragment() {

    val currentStaff = FirebaseAuth.getInstance().uid.toString()
    var selectFoodPhotoUri: Uri? = null
    var recipe = Recipe()
    val recID = UUID.randomUUID().toString()
    val ref = FirebaseDatabase.getInstance().getReference("wmRecipe/$recID")

    override fun onStart() {
        super.onStart()
        val intent = Intent(context, wm_add_new_Recipe_Activity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wm_add, container, false)
    }

}