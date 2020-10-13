package com.example.myapps

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.recycle_row_ingredients.view.*
import kotlinx.android.synthetic.main.recycle_row_preparation.view.*


class RecipeDetailActivity : AppCompatActivity() {

    var rec = Recipe()
    var rc_id : String = ""


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)


    }


    override fun onStart() {
        super.onStart()
        rc_id = intent.getStringExtra(HomeFragment.RECIPE_KEY)
        back_button.setOnClickListener {
            onBackPressed()
        }
        init()
        bindIngredient()
        bindSteps()

    }

    

    private fun init() {

        //recipe title & image
        val ref2 = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID")
            .equalTo(rc_id)
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