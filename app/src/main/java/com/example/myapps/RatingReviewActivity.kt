package com.example.myapps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_rating_review.*
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.recycle_row_no_review.view.*
import kotlinx.android.synthetic.main.recycle_row_ratings.view.*
import java.text.SimpleDateFormat
import java.util.*

const val RCID2 = ""

class RatingReviewActivity : AppCompatActivity() {

    var rid : String = ""
    var userid : String = ""
    var found: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_review)
    }

    override fun onStart() {
        super.onStart()
        rid = intent.extras?.getString(RCID,"").toString()

        btn_exit.setOnClickListener {
            onBackPressed()
        }
        loadRecipe()
        loadReview()
    }

    private fun loadRecipe(){
        val ref = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID").equalTo(rid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    if (rc != null) {
                        Picasso.get().load(rc.recipeImage).into(imageRecipeRate)
                        titleRecipeRate.text = rc.recipeTitle

                    }
                }
            }
        })
    }

    private fun loadReview(){
        val ref = FirebaseDatabase.getInstance().getReference("/Rating").orderByChild("recipeID").equalTo(rid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val rate = it.getValue(Rating::class.java)
                    if (rate != null) {
                        found = true
                        adapter.add(bindRating(rate))
                    }
                }
                if(found == false){
                adapter.add(bindNoReview("No review yet"))
                }
                ryr_rating.adapter = adapter
            }
        })
    }

    class bindRating(val rates : Rating): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            var str = formate.format(rates.ratingDate)

            viewHolder.itemView.ratingOwnerName.text = rates.username.capitalize()
            viewHolder.itemView.ratingBarRow.rating = rates.ratingNumber
            viewHolder.itemView.commentText.text = rates.review
            viewHolder.itemView.dateText.text = str.toString()
            Picasso.get().load(rates.profileImageUrl).into(viewHolder.itemView.ratingImage)
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_ratings
        }


    }


    class bindNoReview(val str: String): Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.txtNoReview.text = str

        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_no_review
        }
    }

}