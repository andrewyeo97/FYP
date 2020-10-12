package com.example.myapps

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.recycle_row_ingredients.*
import kotlinx.android.synthetic.main.recycle_row_ingredients.view.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*

class RecipeDetailActivity : AppCompatActivity() {

    var rec = Recipe()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init(){
        val ref = FirebaseDatabase.getInstance().getReference("/Recipe")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val rep = it.getValue(Recipe::class.java)
                    if(rep != null){
                        adapter.add(bindata(rep))
                    }
                }
                ryr_ings.adapter = adapter
            }
        })

    }

    class bindata(val recipe : Recipe): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.ing_name.text = recipe.calories.toString()
            viewHolder.itemView.ing_qty.text = recipe.cholesterol.toString()
            viewHolder.itemView.ing_desc.text = recipe.totalFat.toString()
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_ingredients
        }


    }

}