package com.example.myapps

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.myapps.R
import com.example.myapps.Recipe
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.internal.InternalTokenProvider
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_recipe_update_.*
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.*
import kotlinx.android.synthetic.main.fragment_wm_add.*
import java.util.*

class Recipe_update_Activity : AppCompatActivity() {

    var selectUpdatePhotoUri: Uri? = null
    var imageString: String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_update_)

        init()

        update_food_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        button_update_page_recipe.setOnClickListener {
            updateValue()
//            val intent = Intent(this, swmDashboardActivity::class.java)
//            startActivity(intent)
        }

        button_update_cancel.setOnClickListener {
            onBackPressed()
        }

        skip_to_updateIng.setOnClickListener {
            val title = update_food_name.text.toString()
            val id = intent.getStringExtra("RecipeId")
            id.toString()
            val intent = Intent(this, wm_update_ing_Activity::class.java)
            intent.putExtra("UpdateRecipeTitle", title)
            intent.putExtra("updateRecipeId", id)
            startActivity(intent)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        init()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectUpdatePhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectUpdatePhotoUri)
            update_food_image.setImageBitmap(bitmap)
        }
    }

    private fun init() {
        val id = intent.getStringExtra("RecipeId")

        val ref2 = FirebaseDatabase.getInstance().getReference("/wmRecipe").orderByChild("recipeID").equalTo(
            id
        )
        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    if (rc != null) {

                        Picasso.get().load(rc.recipeImage).into(update_food_image).toString()
                        update_food_name.setText(rc.recipeTitle)
                        update_food_category.setText(rc.category)
                        //recipe nutrition fact
                        update_food_calories.setText(rc.calories.toString())
                        update_total_fat.setText(rc.totalFat.toString())
                        update_saturated_fat.setText(rc.saturatedFat.toString())
                        update_fibre.setText(rc.fibre.toString())
                        update_protein.setText(rc.protein.toString())
                        update_cholesterol.setText(rc.cholesterol.toString())

                        imageString = rc.recipeImage
                    }
                }
            }
        })
    }

    private fun updateValue(){

        val id = intent.getStringExtra("RecipeId")
        val ref = FirebaseDatabase.getInstance().getReference("wmRecipe/$id")

        if(update_food_name.text.isNotEmpty()){
            val title = update_food_name.text.toString()
            ref.child("recipeTitle").setValue(title)
        }
        else{
            update_food_name.error = "Title cannot be empty..."
            update_food_name.requestFocus()
            return
        }

        if( update_food_category.text.isNotEmpty()){
            val rcpCtg = update_food_category.text.toString()
            ref.child("category").setValue(rcpCtg)
        }
        else{
            update_food_category.error = "Recipe category cannot be empty..."
            update_food_category.requestFocus()
            return
        }
        if (update_food_calories.text.isNotEmpty()){
            val rcpCalories = update_food_calories.text.toString().toDouble()
            ref.child("calories").setValue(rcpCalories)
        }
        else{
            update_food_calories.error = "Calories cannot be empty..."
            update_food_calories.requestFocus()
            return
        }
        if(update_fibre.text.isNotEmpty()){
            val rcpfibre = update_fibre.text.toString().toDouble()
            ref.child("fibre").setValue(rcpfibre)
        }
        else{
            update_fibre.error = "Fibre cannot be empty..."
            update_fibre.requestFocus()
            return
        }
        if(update_protein.text.isNotEmpty()){
            val rcpProtein = update_protein.text.toString().toDouble()
            ref.child("protein").setValue(rcpProtein)
        }
        else{
            update_protein.error = "Protein cannot be empty..."
            update_protein.requestFocus()
            return
        }
        if(update_saturated_fat.text.isNotEmpty()){
            val rcpSaturated = update_saturated_fat.text.toString().toDouble()
            ref.child("saturatedFat").setValue(rcpSaturated)
        }
        else{
            update_saturated_fat.error = "Saturated Fat cannot be empty..."
            update_saturated_fat.requestFocus()
            return
        }
        if(update_total_fat.text.isNotEmpty()){
            val rcpTotalFat = update_total_fat.text.toString().toDouble()
            ref.child("totalFat").setValue(rcpTotalFat)
        }
        else{
            update_total_fat.error = "Total fat cannot be empty..."
            update_total_fat.requestFocus()
            return
        }
        if(update_cholesterol.text.isNotEmpty()){
            val rcpCholesterol = update_cholesterol.text.toString().toDouble()
            ref.child("cholesterol").setValue(rcpCholesterol)
        }else{
            update_cholesterol.error = "Cholesterol cannot be empty..."
            update_cholesterol.requestFocus()
            return
        }

        uploadFoodImageToFirebaseStorage()
        Toast.makeText(baseContext, "Update Successful", Toast.LENGTH_SHORT).show()
    }

    private fun uploadFoodImageToFirebaseStorage(){
        val id = intent.getStringExtra("RecipeId")
        val ref1 = FirebaseDatabase.getInstance().getReference("wmRecipe/$id")
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/RecipeImage/$filename")

        if(selectUpdatePhotoUri==null){
            ref1.child("recipeImage").setValue(imageString)
        }else{
            ref.putFile(selectUpdatePhotoUri!!).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    ref1.child("recipeImage").setValue(it.toString())
                }
            }
        }
        Toast.makeText(baseContext, "Update successful", Toast.LENGTH_SHORT).show()
        val title = update_food_name.text.toString()
        id.toString()
        val intent = Intent(this, wm_update_ing_Activity::class.java)
        intent.putExtra("UpdateRecipeTitle", title)
        intent.putExtra("updateRecipeId", id)
        startActivity(intent)
    }
}