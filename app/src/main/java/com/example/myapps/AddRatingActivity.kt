package com.example.myapps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_add_rating.*
import kotlinx.android.synthetic.main.activity_add_rating.view.*
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import java.util.*

const val RCID = ""

class AddRatingActivity : AppCompatActivity() {

    var isExist : Boolean = false
    var currentRatingID : String = ""
    var username: String = ""
    var url: String = ""
    var rid : String = ""
    val currentuserID = FirebaseAuth.getInstance().currentUser!!.uid
    var rating = Rating()
    var currentDate = Calendar.getInstance().time


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rating)
        deleteBtn.visibility = View.GONE
        deleteBtn.isClickable = false
        findUser()

        postButton.setOnClickListener {
            if (isExist == false) {
                if (ratingBar.rating.toDouble() > 0.00) {
                    addNewRating()
                }
                return@setOnClickListener
            }
            else if(isExist == true){
                if (ratingBar.rating.toDouble() > 0.00) {
                    modifyRating()
                }
            }
        }

        back_btn_rating.setOnClickListener {
            onBackPressed()
        }

        deleteBtn.setOnClickListener {
            deleteRating()
        }


    }

    override fun onStart() {
        super.onStart()
        rid = intent.extras?.getString(RCID,"").toString()
        loadRecipe()
        loadRating()
    }

    private fun findUser(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(currentuserID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        username = user.username
                        url = user.profileImageUrl
                    }
                }
            }
        })
    }


    private fun addNewRating(){
        val ratingID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Rating/$ratingID")
        rating.ratingID = ratingID
        rating.ratingDate = currentDate
        rating.ratingNumber = ratingBar.rating.toFloat()
        rating.recipeID = rid
        rating.userID = currentuserID
        rating.review = comment.text.toString()
        rating.username = username
        rating.profileImageUrl = url
        ref.setValue(rating)

        Toast.makeText(baseContext, "Rating completed", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun modifyRating(){
        val ratingID = currentRatingID

        val txt_review = comment.text.toString()
        val ratingvalue = ratingBar.rating


        val ref = FirebaseDatabase.getInstance().getReference("Rating/$ratingID")
        ref.child("ratingNumber").setValue(ratingvalue)
        ref.child("review").setValue(txt_review)


        Toast.makeText(baseContext, "Rating edited successfully", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun deleteRating(){
        val ref = FirebaseDatabase.getInstance().getReference("/Rating/$currentRatingID")
        ref.removeValue()
        Toast.makeText(baseContext, "Rating removed", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }


    private fun loadRecipe(){
        val ref = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID").equalTo(rid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    if (rc != null) {
                        ratingRecipeTitle.text = rc.recipeTitle
                    }
                }
            }
        })
    }

    private fun loadRating(){
        val ref2 = FirebaseDatabase.getInstance().getReference("/Rating").orderByChild("userID").equalTo(currentuserID)
        ref2.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rate = it.getValue(Rating::class.java)
                    if (rate != null) {


                        val ref3 = FirebaseDatabase.getInstance().getReference("/Rating")
                            .orderByChild("recipeID").equalTo(rid)
                        ref3.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {}
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach {
                                    val rate2 = it.getValue(Rating::class.java)
                                    if (rate2 != null) {
                                        ratingBar.rating = rate2.ratingNumber.toString().toFloat()
                                        comment.setText(rate2.review)
                                        currentRatingID = rate2.ratingID
                                        isExist = true
                                        deleteBtn.visibility = View.VISIBLE
                                        deleteBtn.isClickable = true
                                    }
                                }
                            }


                        })
                    }

                }
            }

        })
    }


}

