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
        ingredient.recipeID = "32eb2a4c-9b98-4c45-9027-cedce2f37d6e"
        ref.setValue(ingredient)
    }


}