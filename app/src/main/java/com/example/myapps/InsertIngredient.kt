package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_insert_ingredient.*
import kotlinx.android.synthetic.main.activity_insert_recipe.*
import java.util.*

class InsertIngredient : AppCompatActivity() {

    var ingredient = Ingredients()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_ingredient)

        btnOK.setOnClickListener {
            addIng()
        }
    }

    private fun addIng(){
        val ingID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Ingredient/$ingID")
        ingredient.ingredientID = ingID
        ingredient.ingredientName = insert_ing_name.text.toString()
        ingredient.quantity = insert_ing_qty.text.toString().toDouble()
        ingredient.unit = insert_ing_desc.text.toString()
        ingredient.recipeID = "080c1475-aabe-43d2-a315-6fcbcae8b4c4"
        ref.setValue(ingredient)
    }

    override fun onStart() {
        super.onStart()
        clear1.setOnClickListener {
            insert_ing_name.setText("")
            insert_ing_name.requestFocus()
        }
        clear2.setOnClickListener {
            insert_ing_qty.setText("")
            insert_ing_qty.requestFocus()
        }
        clear3.setOnClickListener {
            insert_ing_desc.setText("")
            insert_ing_desc.requestFocus()
        }
    }

}