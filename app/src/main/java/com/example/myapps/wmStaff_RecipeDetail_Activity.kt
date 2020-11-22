package com.example.myapps

import android.content.Intent
import android.content.LocusId
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myapps.*
import com.example.myapps.R
import com.example.myapps.fragments.FavouriteFragment
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.*
import kotlinx.android.synthetic.main.recycle_row_ingredients.view.*
import kotlinx.android.synthetic.main.recycle_row_preparation.view.*
import kotlinx.android.synthetic.main.wm_recycle_row_ing2.view.*
import kotlinx.android.synthetic.main.wm_recycle_row_step2.view.*
import com.google.firebase.database.DatabaseError

import androidx.annotation.NonNull
import androidx.core.net.toUri
import com.example.myapps.fragments.wmHomeFragment

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_recipe_update_.*
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.button_update_recipe
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.view.*


class wmStaff_RecipeDetail_Activity : AppCompatActivity() {

    var rc_id : String = ""
    var found: Boolean = false

    var cuisine_key : String  = ""
    var category_key : String = ""

    private val handlers: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_staff__recipe_detail_)

        button_delete_recipe.setOnClickListener {
            val intent = Intent(this, swmDashboardActivity::class.java)
            val builder = AlertDialog.Builder(this@wmStaff_RecipeDetail_Activity)
            builder.setTitle("Delete Recipe Message")
            builder.setMessage("Are you want to delete this Recipe?")
            builder.setPositiveButton("Yes"){ dialog, which ->
                Toast.makeText(applicationContext, "Ok, we will delete Recipe", Toast.LENGTH_SHORT).show()
                deleteRecipe(rc_id)
                startActivity(intent)
            }
            builder.setNegativeButton("No"){ dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Ok, we will not delete Recipe",
                    Toast.LENGTH_SHORT
                ).show()

            }
            val dialog:AlertDialog = builder.create()
            dialog.show()
        }

        button_update_recipe.setOnClickListener {
            val intent = Intent(this, Recipe_update_Activity::class.java)
            intent.putExtra("RecipeId", rc_id)
            startActivity(intent)
        }

        buttonBack.setOnClickListener {
            onBackPressed()
//            val intent  = Intent(this, wm_view_recipe_list_Activity::class.java)
//            intent.putExtra(rc_cuisine, cuisine_key)
//            intent.putExtra(rc_category, category_key)
//            startActivity(intent)
        }


    }

    override fun onStart() {
        super.onStart()

        rc_id = intent.getStringExtra(wm_view_recipe_list_Activity.RECIPE_KEY)
        rc_id = intent.getStringExtra(wm_update_historyt_Activity.RECIPE_KEY)
//        rc_id = intent.getStringExtra(wm_update_step_Activity.RECIPE_KEY)
//        rc_id = intent.getStringExtra(wmHomeFragment.RECIPE_KEY)
        init()
        bindIngredient()
        bindSteps()
        ToastRunnabler.run()
//
//        displayFavourite()
//        back_button.setOnClickListener {
//            onBackPressed()
//        }
    }
    companion object{
        val rc_category = "rc_category"
        val rc_cuisine = "rc_cuisine"
    }


    override fun onBackPressed() {
        super.onBackPressed()
//        val intent  = Intent(this, wm_view_recipe_list_Activity::class.java)
//        intent.putExtra(rc_cuisine, cuisine_key)
//        intent.putExtra(rc_category, category_key)
//        startActivity(intent)
    }

    private val ToastRunnabler: Runnable = object : Runnable {
        override fun run() {
            found = false
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
                            found = true
                            ttlRating = ttlRating + rate.ratingNumber
                            counter = counter + 1
                        }

                    }
                    avgRating = ttlRating/counter
                    recipeRating.text = (avgRating.toString() + " ratings | " + counter + " review(s)")
                    if(found == false){
                        recipeRating.text = ("No review yet")}
                }
            })
            handlers.postDelayed(this, 100)
        }
    }


    private fun init() {

        //recipe title & image
        val ref2 = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID").equalTo(
            rc_id
        )
        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    if (rc != null) {

                        cuisine_key = rc.cuisine
                        category_key = rc.category
                        Picasso.get().load(rc.recipeImage).into(imageViewRecipe)
                        recipeTitle.text = rc.recipeTitle

                        //recipe nutrition fact
                        textViewCalories.text = rc.calories.toString()
                        textViewTotalFat.text = rc.totalFat.toString()
                        textViewSaturatedFat.text = rc.saturatedFat.toString()
                        textViewFirbe.text = rc.fibre.toString()
                        textViewProtein.text = rc.protein.toString()
                        textViewCholesterol.text = rc.cholesterol.toString()

                    }
                }
            }
        })
    }

    private fun bindIngredient() {

        val ref = FirebaseDatabase.getInstance().getReference("/Ingredient").orderByChild("recipeID").equalTo(
            rc_id
        )
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
                recycle_ingredient_dis2.adapter = adapter
            }
        })
    }
    private class bindataIng(val ingredients: Ingredients): Item<GroupieViewHolder>() {
        override fun bind(viewHolder1: GroupieViewHolder, position: Int) {
            viewHolder1.itemView.added_ing_qty2.text = ingredients.quantity.toString()
            viewHolder1.itemView.added_ing_name2.text = ingredients.ingredientName
            viewHolder1.itemView.added_ing_unit2.text = ingredients.unit

            viewHolder1.itemView.recycle_ing2.setOnLongClickListener {

                val builder = AlertDialog.Builder(it.context)
                builder.setTitle("Delete ingredient")
                builder.setMessage("Do you want to delete ingredient?")
                builder.setPositiveButton("Yes"){ dialog, which ->
                    val ref = FirebaseDatabase.getInstance().getReference("/Ingredient/${ingredients.ingredientID}")
                    ref.removeValue()
                }
                builder.setNegativeButton("No",null)

                val alertDialog = builder.create()
                alertDialog.show()

                true
            }
        }


        override fun getLayout(): Int {
            return R.layout.wm_recycle_row_ing2
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
                        if (list.count() == snapshot.children.count()) {
                            list.sortBy { it.stepNo }
                            list.forEach {
                                adapter.add(bindataStep(it))
                            }
                        }

                    }
                }
                recycle_step_dis2.adapter = adapter

            }
        })
    }

    private class bindataStep(val steps: Steps): Item<GroupieViewHolder>() {
        override fun bind(viewHolder2: GroupieViewHolder, position: Int) {
            viewHolder2.itemView.step_number2.text = steps.stepNo.toString()
            viewHolder2.itemView.step_description2.text = steps.desc
        }
        override fun getLayout(): Int {
            return R.layout.wm_recycle_row_step2
        }
    }

    private fun deleteRecipe(id: String){

        val refRecipe = FirebaseDatabase.getInstance().getReference("Recipe").child(id)
        refRecipe.removeValue()

        val refImage = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID").equalTo(id)
        refImage.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    if (rc != null) {
                        val imageUrl = rc.recipeImage
                        val storageReference: StorageReference =FirebaseStorage.getInstance().reference.child("RecipeImage").child(imageUrl)
                        storageReference.delete()
                    }
                }
            }
        })

        val refIngs = FirebaseDatabase.getInstance().getReference("/Ingredient").orderByChild("recipeID").equalTo(id)
        refIngs.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    it.ref.removeValue()
                }
            }
        })


        val refStep = FirebaseDatabase.getInstance().getReference("Steps").orderByChild("recipeID").equalTo(id)
        refStep.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    it.ref.removeValue()
                }
            }
        })

        val refFav = FirebaseDatabase.getInstance().getReference("Favourite").orderByChild("recipeID").equalTo(id)
        refFav.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    it.ref.removeValue()
                }
            }
        })

        val refHis = FirebaseDatabase.getInstance().getReference("History").orderByChild("recipeID").equalTo(id)
        refHis.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    it.ref.removeValue()
                }
            }
        })

    }


}