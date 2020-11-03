package com.example.myapps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_insert_recipe.*
import kotlinx.android.synthetic.main.activity_register.*
import java.text.SimpleDateFormat
import java.util.*

class InsertRecipe : AppCompatActivity() {

    var recipe = Recipe()
    var selectedPhotoUri: Uri? = null
    val sdf = SimpleDateFormat("dd/M/yyyy")
    var currentDate = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_recipe)

        addbutton.setOnClickListener {
            addRecipe()
        }

        recipeimage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            recipeimage.setImageBitmap(bitmap)
        }

    }

    private fun addRecipe(){
        val recipeImgID = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/RecipeImages/$recipeImgID")
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveUserToDatabase(it.toString())
            }

        }
    }

    private fun saveUserToDatabase(profileImageUrl: String){
        val rec = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Recipe/$rec")
        recipe.recipeID = rec
        recipe.recipeTitle = recipeTitle.text.toString()
        recipe.calories = energy.text.toString().toDouble()
        recipe.totalFat = totalFat.text.toString().toDouble()
        recipe.saturatedFat = saturatedFat.text.toString().toDouble()
        recipe.fibre = fibre.text.toString().toDouble()
        recipe.protein = protein.text.toString().toDouble()
        recipe.cholesterol = cholesterol.text.toString().toDouble()
        recipe.category = category.text.toString()
        recipe.staffID = "2dIignQXe3dAfDoXp6zSdPeLQRR2"
        recipe.cuisine = cuisine.text.toString()
        recipe.uploadDate = currentDate
        recipe.recipeImage = profileImageUrl
        recipe.averageRating = 0.00F
        recipe.urlRec = "https://www.slenderkitchen.com/recipe/frozen-breakfast-quesadillas"
        ref.setValue(recipe)
    }
}