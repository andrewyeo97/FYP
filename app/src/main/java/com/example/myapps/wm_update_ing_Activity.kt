package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myapps.Ingredients
import com.example.myapps.R
import com.example.myapps.Recipe
import com.example.myapps.RecipeDetailActivity
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_update_.*
import kotlinx.android.synthetic.main.activity_swm__add__ing_.*
import kotlinx.android.synthetic.main.activity_wm_update_ing_.*
import kotlinx.android.synthetic.main.activity_wm_update_ing_.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*
import kotlinx.android.synthetic.main.wm_recycle_row_ing.view.*
import kotlinx.android.synthetic.main.wm_recycle_update_ing.view.*
import java.util.*

class wm_update_ing_Activity : AppCompatActivity() {
    var ingredient = Ingredients()
    var rcpTitle: String = ""
    var ingId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_update_ing_)

        rcpTitle = intent.getStringExtra("UpdateRecipeTitle")
        update_recipeName.text = rcpTitle

        button_updatepage_add_ing.setOnClickListener {
            addIng()
        }
        update_btn_re.setOnClickListener {
            init()
        }
        button_updatepage_delete_ing.setOnClickListener {
            val builder = AlertDialog.Builder(this@wm_update_ing_Activity)
            builder.setTitle("Delete Ingredient Message")
            builder.setMessage("Are you want to delete this Ingredients?")
            builder.setPositiveButton("Yes"){ dialog, which ->
                Toast.makeText(applicationContext, "Ok, we will delete this ingredients.", Toast.LENGTH_SHORT).show()
                deleteIng()
            }
            builder.setNegativeButton("No"){ dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Ok, we will not delete this ingredients.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        update_btn_re.setOnClickListener {
            init()
        }

        button_updatepage_update_ing.setOnClickListener {
            updateIng()

        }

        update_next_button.setOnClickListener {
            val rcpId = intent.getStringExtra("updateRecipeId")
            val intent = Intent (this, wm_update_step_Activity::class.java)
            intent.putExtra("updateRcpTitle", rcpTitle)
            intent.putExtra("updateRcpId", rcpId)
            startActivity(intent)
        }

    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun addIng(){
        val ingID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/wmIngredient/$ingID")
        val rcpId = intent.getStringExtra("updateRecipeId")

        if(update_ingName.text.toString().isEmpty()){
            update_ingName.error = "Please enter ingredients name"
            ingName.requestFocus()
            return
        }
        if(update_ingQty.text.toString().isEmpty()){
            update_ingQty.error = "Please enter ingredients quantity"
            ingQty.requestFocus()
            return
        }
        if(update_ingUnit.text.toString().isEmpty()){
            update_ingUnit.error = "Please enter an unit"
            ingUnit.requestFocus()
            return
        }
        ingredient.ingredientID = ingID
        ingredient.ingredientName = update_ingName.text.toString()
        ingredient.quantity = update_ingQty.text.toString().toDouble()
        ingredient.unit = update_ingUnit.text.toString()
        ingredient.recipeID = rcpId
        ref.setValue(ingredient)
        Toast.makeText(baseContext, "Added New Ingredient Successful", Toast.LENGTH_SHORT).show()
    }


    private fun deleteIng(){
        val ref = FirebaseDatabase.getInstance().getReference("/wmIngredient").child(ingId)
        ref.removeValue()
    }

    private fun updateIng(){
        val ref = FirebaseDatabase.getInstance().getReference("wmIngredient/$ingId")

        if(update_ingName.text.isNotEmpty()){
            val name = update_ingName.text.toString()
            ref.child("ingredientName").setValue(name)
        }
        else{
            update_ingName.error = "Ingredient Name cannot be empty..."
            update_ingName.requestFocus()
            return
        }

        if(update_ingQty.text.isNotEmpty()){
            val qty = update_ingQty.text.toString().toDouble()
            ref.child("quantity").setValue(qty)
        }
        else{
            update_ingQty.error = "Ingredient Quantity cannot be empty..."
            update_ingQty.requestFocus()
            return
        }

        if(update_ingUnit.text.isNotEmpty()){
            val unit = update_ingUnit.text.toString()
            ref.child("unit").setValue(unit)
        }
        else{
            update_ingUnit.error = "Ingredient Unit cannot be empty..."
            update_ingUnit.requestFocus()
            return
        }
        Toast.makeText(baseContext, "Ingredient Update Successful", Toast.LENGTH_SHORT).show()
    }

    private fun init(){
        val rcpId = intent.getStringExtra("updateRecipeId")
        val ref =  FirebaseDatabase.getInstance().getReference("/wmIngredient").orderByChild("recipeID").equalTo(rcpId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    val ing = it.getValue(Ingredients::class.java)
                    if(ing !=null){
                        adapter.add(ingData(ing))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val ingValue = item as ingData
                    ingId = ingValue.ing.ingredientID
                    update_ingName.setText(ingValue.ing.ingredientName)
                    update_ingQty.setText(ingValue.ing.quantity.toString())
                    update_ingUnit.setText(ingValue.ing.unit)
                }
                recycle_update_ing_dis.adapter = adapter
            }
        })
    }
}

class ingData(val ing : Ingredients): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.updated_ing_qty.text = ing.quantity.toString()
        viewHolder.itemView.updated_ing_unit.text = ing.unit
        viewHolder.itemView.updated_ing_name.text = ing.ingredientName
    }
    override fun getLayout(): Int {
        return R.layout.wm_recycle_update_ing
    }
}