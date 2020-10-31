package com.example.myapps.fragments

//import com.example.myapps.MainAdapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.myapps.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*


/**
 * A simple [Fragment] subclass.
 */


class HomeFragment : Fragment() {

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onStart() {
        super.onStart()
        checkUserAccountSignIn()

        breakfastCL.setOnClickListener {
            goBreakfast()
        }

        lunchCL.setOnClickListener {
            goLunch()
        }

        dinnerCL.setOnClickListener {
            goDinner()
        }

    }

    private fun checkUserAccountSignIn(){
        if (FirebaseAuth.getInstance().uid.isNullOrEmpty()){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(view?.context,MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun goBreakfast(){
        val intent = Intent(view?.context, BreakfastActivity::class.java)
        startActivity(intent)
    }

    private fun goLunch(){
        val intent = Intent(view?.context, LunchActivity::class.java)
        startActivity(intent)
    }

    private fun goDinner(){
        val intent = Intent(view?.context, DinnerActivity::class.java)
        startActivity(intent)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}



