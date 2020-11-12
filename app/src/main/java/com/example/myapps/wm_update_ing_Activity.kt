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
import java.lang.Double
import java.util.*

class wm_update_ing_Activity : AppCompatActivity() {
    var ingredient = Ingredients()
    var rcpTitle: String = ""
    var ingId: String = ""
    var currentDate = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_update_ing_)

        button_updatepage_add_ing.setOnClickListener {
            val rcpId = intent.getStringExtra("updateRecipeId")
            val ref = FirebaseDatabase.getInstance().getReference("Recipe/$rcpId")
            addIng()
            ref.child("updateDate").setValue(currentDate)
        }
        button_updatepage_delete_ing.setOnClickListener {
            val rcpId = intent.getStringExtra("updateRecipeId")
            val ref = FirebaseDatabase.getInstance().getReference("Recipe/$rcpId")

            val builder = AlertDialog.Builder(this@wm_update_ing_Activity)
            builder.setTitle("Delete Ingredient Message")
            builder.setMessage("Are you want to delete this Ingredients?")
            builder.setPositiveButton("Yes"){ dialog, which ->
                Toast.makeText(applicationContext, "Ok, we will delete this ingredients.", Toast.LENGTH_SHORT).show()
                deleteIng()
                update_ingName.setText("")
                update_ingQty.setText("")
                update_ingUnit.setText("")
                ref.child("updateDate").setValue(currentDate)
            }
            builder.setNegativeButton("No"){ dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Ok, we will not delete this ingredients.",
                    Toast.LENGTH_SHORT
                ).show()
                update_ingName.setText("")
                update_ingQty.setText("")
                update_ingUnit.setText("")

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        update_btn_re.setOnClickListener {
            init()
        }

        button_updatepage_update_ing.setOnClickListener {
            val rcpId = intent.getStringExtra("updateRecipeId")
            val ref = FirebaseDatabase.getInstance().getReference("Recipe/$rcpId")
            updateIng()
            ref.child("updateDate").setValue(currentDate)
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
        rcpTitle = intent.getStringExtra("UpdateRecipeTitle")
        update_recipeName.setText(rcpTitle)
        init()
    }

    private fun addIng(){

        var numeric1 = true
        var numeric2 = true
        val ingID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Ingredient/$ingID")
        val recipeId = intent.getStringExtra("updateRecipeId")

        if(update_ingName.text.toString().isEmpty()){
            update_ingName.error = "Please enter ingredients name"
            update_ingName.requestFocus()
            return
        }
        try{
            val num = Double.parseDouble(update_ingName.text.toString())
        }catch (e: NumberFormatException){
            numeric1 = false
        }
        if(numeric1){
            update_ingName.error = "Ingredients name cannot be numeric"
            update_ingName.requestFocus()
            return
        }

        if(update_ingQty.text.toString().isEmpty()){
            update_ingQty.error = "Please enter ingredients quantity"
            update_ingQty.requestFocus()
            return
        }
        if(update_ingQty.text.toString().isEmpty()){
            update_ingQty.error = "Please enter an unit"
            update_ingQty.requestFocus()
            return
        }
        if(update_ingUnit.text.toString().isEmpty()){
            update_ingUnit.error = "Please enter an unit"
            update_ingUnit.requestFocus()
            return
        }
        try{
            val num = Double.parseDouble(update_ingUnit.text.toString())
        }catch (e: NumberFormatException){
            numeric2 = false
        }
        if(numeric2){
            update_ingUnit.error = "Ingredients Unit cannot be numeric"
            update_ingUnit.requestFocus()
            return
        }

        ingredient.ingredientID = ingID
        ingredient.ingredientName = update_ingName.text.toString()
        ingredient.quantity = update_ingQty.text.toString().toDouble()
        ingredient.unit = update_ingUnit.text.toString()
        ingredient.recipeID = recipeId

        ref.setValue(ingredient)
        Toast.makeText(baseContext, "Added New Ingredient Successful", Toast.LENGTH_SHORT).show()
        update_ingName.setText("")
        update_ingQty.setText("")
        update_ingUnit.setText("")
    }


    private fun deleteIng(){
        val ref = FirebaseDatabase.getInstance().getReference("/Ingredient").child(ingId)
        ref.removeValue()
    }

    private fun updateIng(){
        var numeric3 = true
        var numeric4 = true

        val ref = FirebaseDatabase.getInstance().getReference("Ingredient/$ingId")

        if(update_ingName.text.isNotEmpty()){
            try{
                val num = Double.parseDouble(update_ingName.text.toString())
            }catch (e: NumberFormatException){
                numeric3 = false
            }
            if(numeric3){
                update_ingName.error = "Ingredients name cannot be numeric"
                update_ingName.requestFocus()
                return
            }else{
                val name = update_ingName.text.toString()
                ref.child("ingredientName").setValue(name)
            }
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
            try{
                val num = Double.parseDouble(update_ingUnit.text.toString())
            }catch (e: NumberFormatException){
                numeric4 = false
            }
            if(numeric4){
                update_ingUnit.error = "Ingredients Unit cannot be numeric"
                update_ingUnit.requestFocus()
                return
            }else{
                val unit = update_ingUnit.text.toString()
                ref.child("unit").setValue(unit)
            }
        }
        else{
            update_ingUnit.error = "Ingredient Unit cannot be empty..."
            update_ingUnit.requestFocus()
            return
        }
        Toast.makeText(baseContext, "Ingredient Update Successful", Toast.LENGTH_SHORT).show()
        update_ingName.setText("")
        update_ingQty.setText("")
        update_ingUnit.setText("")
    }

    private fun init(){
        val rcpId = intent.getStringExtra("updateRecipeId")
        val ref =  FirebaseDatabase.getInstance().getReference("/Ingredient").orderByChild("recipeID").equalTo(rcpId)

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