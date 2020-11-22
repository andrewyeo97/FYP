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
    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var rating = Rating()
    var currentDate = Calendar.getInstance().time
    var rateNumber: Float = 0.0F
    var found: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rating)
        deleteBtn.visibility = View.GONE
        deleteBtn.isClickable = false
        findUser()
        rid = intent.extras?.getString(RCID,"").toString()
        loadRecipe()
        loadRating()
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
        loadRating()
        Toast.makeText(baseContext, "Rating completed", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }


    private fun modifyRecipeAvgRating(float: Float){
        val ref = FirebaseDatabase.getInstance().getReference("Recipe/$rid")
        ref.child("averageRating").setValue(float)
    }

    private fun modifyRating(){
        val ratingID = currentRatingID

        val txt_review = comment.text.toString()
        val ratingvalue = ratingBar.rating


        val ref = FirebaseDatabase.getInstance().getReference("Rating/$ratingID")
        ref.child("ratingNumber").setValue(ratingvalue)
        ref.child("review").setValue(txt_review)
        loadRating()
        Toast.makeText(baseContext, "Rating edited successfully", Toast.LENGTH_SHORT).show()
        onBackPressed()
    }

    private fun deleteRating(){
        val ref = FirebaseDatabase.getInstance().getReference("/Rating/$currentRatingID")
        ref.removeValue()
        modifyRecipeAvgRating(0.0F)
        loadRating()
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
        found = false
        var avgRating: Float = 0.0F
        var counter: Int = 0
        var ttlRating: Float = 0.0F

        val reff = FirebaseDatabase.getInstance().getReference("/Rating").orderByChild("recipeID").equalTo(rid)
        reff.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rating = it.getValue(Rating::class.java)
                    if(rating != null){
                        ttlRating = ttlRating + rating.ratingNumber
                        counter = counter + 1
                        found = true
                        if(rating.userID.equals(currentuserID)){
                            ratingBar.rating = rating.ratingNumber.toString().toFloat()
                            comment.setText(rating.review)
                            currentRatingID = rating.ratingID
                            isExist = true
                            deleteBtn.visibility = View.VISIBLE
                            deleteBtn.isClickable = true

                        }
                    }

                }
                if(found == true) {
                    avgRating = ttlRating / counter
                    rateNumber = avgRating
                    modifyRecipeAvgRating(avgRating)
                }
            }
        })
    }


}

