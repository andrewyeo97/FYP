package com.example.myapps.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapps.R
import com.example.myapps.Recipe
import com.example.myapps.swmDashboardActivity
import com.example.myapps.swm_Add_Ing_Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main_.*
import kotlinx.android.synthetic.main.activity_swm__add__ing_.*
import kotlinx.android.synthetic.main.fragment_wm_add.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [wmAddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class wmAddFragment : Fragment() {

    val currentStaff = FirebaseAuth.getInstance().uid.toString()
    var selectFoodPhotoUri: Uri? = null
    var recipe = Recipe()
    val recID = UUID.randomUUID().toString()
    val ref = FirebaseDatabase.getInstance().getReference("wmRecipe/$recID")

    override fun onStart() {
        super.onStart()

        select_food_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        button_add_recipe.setOnClickListener{

            addRecipe()
            Toast.makeText(context, " successfully", Toast.LENGTH_SHORT).show()
        }

        button_cancel.setOnClickListener {
            food_name.setText("")
            food_category.setText("")
            food_protein.setText("")
            food_total_fat.setText("")
            food_cholesterol.setText("")
            food_calories.setText("")
            food_fibre.setText("")
            food_saturated_fat.setText("")

            swapactivitylayout()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectFoodPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(view?.context?.contentResolver,selectFoodPhotoUri)
            select_food_image.setImageBitmap(bitmap)
        }
    }

    private fun addRecipe(){
        if(food_name.text.toString().isEmpty()){
            food_name.error = "Please Enter Recipe Title!"
            food_name.requestFocus()
            return
        }
        if(food_category.text.toString().isEmpty()){
            food_category.error = "Please Enter Recipe Title!"
            food_category.requestFocus()
            return
        }
        if(food_calories.text.toString().isEmpty()){
            food_calories.error = "Please Enter Recipe Title!"
            food_calories.requestFocus()
            return
        }
        if(food_total_fat.text.toString().isEmpty()){
            food_total_fat.error = "Please Enter Recipe Title!"
            food_total_fat.requestFocus()
            return
        }
        if(food_saturated_fat.text.toString().isEmpty()){
            food_saturated_fat.error = "Please Enter Recipe Title!"
            food_saturated_fat.requestFocus()
            return
        }
        if(food_fibre.text.toString().isEmpty()){
            food_fibre.error = "Please Enter Recipe Title!"
            food_fibre.requestFocus()
            return
        }
        if(food_protein.text.toString().isEmpty()){
            food_protein.error = "Please Enter Recipe Title!"
            food_protein.requestFocus()
            return
        }
        if(food_cholesterol.text.toString().isEmpty()){
            food_cholesterol.error = "Please Enter Recipe Title!"
            food_cholesterol.requestFocus()
            return
        }
        if (selectFoodPhotoUri == null) {
            Toast.makeText(activity, "Please select your recipe picture", Toast.LENGTH_SHORT).show()
            return
        }
        uploadFoodImageToFirebaseStorage()

    }

    private fun uploadFoodImageToFirebaseStorage(){
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/RecipeImage/$filename")

        ref.putFile(selectFoodPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveRecipeToFirebase(it.toString())
            }
        }
    }

    fun saveRecipeToFirebase(foodImageUri: String){
//        val recID = UUID.randomUUID().toString()
//        val ref = FirebaseDatabase.getInstance().getReference("Recipe/$recID")
        recipe.recipeID = recID
        recipe.recipeTitle = food_name.text.toString()
        recipe.category = food_category.text.toString()
        recipe.recipeImage = foodImageUri
        recipe.calories = food_calories.text.toString().toDouble()
        recipe.totalFat = food_total_fat.text.toString().toDouble()
        recipe.saturatedFat = food_saturated_fat.text.toString().toDouble()
        recipe.fibre = food_fibre.text.toString().toDouble()
        recipe.protein = food_protein.text.toString().toDouble()
        recipe.cholesterol = food_cholesterol.text.toString().toDouble()
        recipe.staffID = currentStaff
        ref.setValue(recipe)

        val intent = Intent(context, swm_Add_Ing_Activity::class.java)
        intent.putExtra("RecipeTitle", recipe.recipeTitle)
        intent.putExtra("RecipeId", recID)

        startActivity(intent)
    }

    private fun swapactivitylayout(){
        (activity as swmDashboardActivity).back()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wm_add, container, false)
    }

}