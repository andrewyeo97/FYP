package com.example.myapps

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myapps.fragments.FavouriteFragment
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_insert_recipe.*
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.recycle_row_ingredients.view.*
import kotlinx.android.synthetic.main.recycle_row_preparation.view.*
import java.util.*


class RecipeDetailActivity : AppCompatActivity() {

    val currentuser = FirebaseAuth.getInstance().uid.toString()
    var favourite = Favourite()
    var rc_id : String = ""
    var isFound : Boolean = false
    var exist  : Boolean = false
    var favDD : String = ""
    private val handlers: Handler = Handler()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        favourite_button.setOnClickListener {
            if(exist == true){
                removeFavourite()
            }
            else if(exist == false){
                addFavourite()
            }
        }

        ratingText.setOnClickListener {
            val intent = Intent(this,RatingReviewActivity::class.java)
            intent.putExtra(RCID2,rc_id)
            startActivity(intent)
        }

        chat_button.setOnClickListener {
            val intent = Intent(this,ChatActivity::class.java)
            intent.putExtra(RCID3,rc_id)
            startActivity(intent)
        }

    }


    override fun onStart() {
        super.onStart()
        rc_id = intent.getStringExtra(HomeFragment.RECIPE_KEY)
        rc_id = intent.getStringExtra(FavouriteFragment.RECIPE_KEY)
        ToastRunnabler.run()
        init()
        bindIngredient()
        bindSteps()
        //showRatingText()
        displayFavourite()
        back_button.setOnClickListener {
            onBackPressed()
        }

        rate_button.setOnClickListener {
            val intent = Intent(this,AddRatingActivity::class.java)
            intent.putExtra(RCID,rc_id)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        handlers.removeCallbacks(ToastRunnabler)
    }

    override fun onResume() {
        super.onResume()
        ToastRunnabler.run()
    }

    private fun addFavourite(){
        val favID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Favourite/$favID")
        favDD = favID
        favourite.favouriteID = favID
        favourite.userID = currentuser
        favourite.recipeID = rc_id
        ref.setValue(favourite)
        favourite_button.setImageResource(R.drawable.favourite_button_red)
        exist = true
        Toast.makeText(baseContext, "Added to favourite", Toast.LENGTH_SHORT).show()
    }

    private fun removeFavourite(){
            val ref = FirebaseDatabase.getInstance().getReference("/Favourite/$favDD")
            ref.removeValue()
            favourite_button.setImageResource(R.drawable.favourite_button_orange)
            exist = false
        Toast.makeText(baseContext, "Removed from favourite", Toast.LENGTH_SHORT).show()

    }


    private fun displayFavourite(){
        val ref = FirebaseDatabase.getInstance().getReference("/Favourite").orderByChild("userID").equalTo(currentuser)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val fav = it.getValue(Favourite::class.java)
                    if (fav != null){
                        if (fav.recipeID.equals(rc_id)){
                            favDD = fav.favouriteID
                            exist = true
                            favourite_button.setImageResource(R.drawable.favourite_button_red)
                        }
                    }
                }
            }
        })
    }

    private val ToastRunnabler: Runnable = object : Runnable {
        override fun run() {
        isFound = false
        var avgRating: Float = 0.0F
        var counter: Int = 0
        var ttlRating: Float = 0.0F
        val ref = FirebaseDatabase.getInstance().getReference("/Rating").orderByChild("recipeID").equalTo(rc_id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rate = it.getValue(Rating::class.java)
                    if (rate != null){
                        isFound = true
                        ttlRating = ttlRating + rate.ratingNumber
                        counter = counter + 1
                    }

                }
                avgRating = ttlRating/counter
                ratingText.text = (avgRating.toString() + " ratings | " + counter + " review(s)")
                if(isFound == false){
                ratingText.text = ("No review yet")}
            }
        })
            handlers.postDelayed(this, 100)
        }
 }
    

    private fun init() {

        //recipe title & image
        val ref2 = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID").equalTo(rc_id)
        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    if (rc != null) {
                        Picasso.get().load(rc.recipeImage).into(recipeDetailImage)
                        recipeDetailTitle.text = rc.recipeTitle


                        //recipe nutrition fact
                        caloriesNumberText.text = rc.calories.toString()
                        totalFatNumberText.text = rc.totalFat.toString()
                        saturatedFatNumberText.text = rc.saturatedFat.toString()
                        fibreNumberText.text = rc.fibre.toString()
                        proteinNumberText.text = rc.protein.toString()
                        cholesterolNumberText.text = rc.cholesterol.toString()

                    }
                }
            }
        })
    }


    private fun bindIngredient() {
        val ref = FirebaseDatabase.getInstance().getReference("/Ingredient").orderByChild("recipeID").equalTo(rc_id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val ing = it.getValue(Ingredients::class.java)
                    if (ing != null) {
                        adapter.add(bindataIng(ing))
                    }
                }
                ryr_ingredient.adapter = adapter
            }
        })
    }

    private class bindataIng(val ingredients: Ingredients): Item<GroupieViewHolder>() {
        override fun bind(viewHolder1: GroupieViewHolder, position: Int) {
            viewHolder1.itemView.ing_qty.text = ingredients.quantity.toString()
            viewHolder1.itemView.ing_name.text = ingredients.ingredientName
            viewHolder1.itemView.ing_desc.text = ingredients.unit
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_ingredients
        }
    }

    private fun bindSteps() {
        val list = arrayListOf<Steps>()

        val ref = FirebaseDatabase.getInstance().getReference("/Steps").orderByChild("recipeID").equalTo(rc_id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val stp = it.getValue(Steps::class.java)
                    if (stp != null) {
                        list.add(stp)
                        if(list.count() == snapshot.children.count()){
                            list.sortBy { it.stepNo }
                            list.forEach {
                                adapter.add(bindataStep(it))
                            }
                        }
                    }
                }
                ryr_steps.adapter = adapter
            }
        })
    }


    private class bindataStep(val steps: Steps): Item<GroupieViewHolder>() {
        override fun bind(viewHolder2: GroupieViewHolder, position: Int) {
            viewHolder2.itemView.counterText.text = steps.stepNo.toString()
            viewHolder2.itemView.prepareText.text = steps.desc
        }
        override fun getLayout(): Int {
            return R.layout.recycle_row_preparation
        }
    }



}


