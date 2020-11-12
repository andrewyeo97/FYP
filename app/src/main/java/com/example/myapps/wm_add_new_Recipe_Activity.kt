package com.example.myapps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.core.text.isDigitsOnly
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_recipe_update_.*
import kotlinx.android.synthetic.main.activity_wm_add_new__recipe_.*
import java.lang.Double.parseDouble
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*

class wm_add_new_Recipe_Activity : AppCompatActivity() {

    val currentStaff = FirebaseAuth.getInstance().uid.toString()
    var selectFoodPhotoUri: Uri? = null
    var recipe = Recipe()
    val recID = UUID.randomUUID().toString()
    val ref = FirebaseDatabase.getInstance().getReference("Recipe/$recID")

    var categoryList : MutableList<String> = ArrayList()
    var cuisineList : MutableList<String> = ArrayList()
    var ctgSelected : String = ""
    var cuiSelected : String = ""

    val sdf = SimpleDateFormat("dd/M/yyyy")
    var currentDate = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_add_new__recipe_)
        ctgList()
        curList()
    }

    override fun onStart() {
        super.onStart()

        select_food_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        button_add_recipe.setOnClickListener{
            addRecipe()
        }

        button_cancel.setOnClickListener {
            val intent = Intent(this, swmDashboardActivity::class.java)

            food_name.setText("")
//            food_category.setText("")
            food_protein.setText("")
            food_total_fat.setText("")
            food_cholesterol.setText("")
            food_calories.setText("")
            food_fibre.setText("")
            food_saturated_fat.setText("")

            startActivity(intent)
        }

    }

    private fun ctgList(){
        val b: String = "Breakfast"
        val l: String  = "Lunch"
        val d: String = "Dinner"
        categoryList.add("")
        categoryList.add(b)
        categoryList.add(l)
        categoryList.add(d)

        val adapter: ArrayAdapter<String> = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,categoryList)
        add_category_dropdown_list.adapter = adapter

        add_category_dropdown_list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val slctedItem: String = categoryList[position]
                ctgSelected = slctedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        ctgHight(add_category_dropdown_list)
    }

    private fun ctgHight(add_category_dropdown_list: Spinner){
        val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(add_category_dropdown_list) as ListPopupWindow
        popupWindow.height = (5 * resources.displayMetrics.density).toInt()
    }

    private fun curList(){
        val wes : String = "Western"
        val chi : String = "Chinese"
        val ind : String = "Indian"
        val mex : String = "Mexican"
        val ita : String = "Italian"
        val tha : String = "Thai"

        cuisineList.add("")
        cuisineList.add(wes)
        cuisineList.add(chi)
        cuisineList.add(ind)
        cuisineList.add(mex)
        cuisineList.add(ita)
        cuisineList.add(tha)

        val adapter: ArrayAdapter<String> = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,cuisineList)

        add_cuisine_dropdown_list.adapter = adapter
        add_cuisine_dropdown_list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val slctedItem: String = cuisineList[position]
                cuiSelected = slctedItem.toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        cuiHight(add_cuisine_dropdown_list)
    }

    private fun cuiHight(add_cuisine_dropdown_list: Spinner){
        val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(add_cuisine_dropdown_list) as ListPopupWindow
        popupWindow.height = (5 * resources.displayMetrics.density).toInt()
    }









    override fun onBackPressed() {
        val intent = Intent(this, swmDashboardActivity::class.java)
        startActivity(intent)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectFoodPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectFoodPhotoUri)
            select_food_image.setImageBitmap(bitmap)
        }
    }

    private fun addRecipe(){
        var numeric1 = true
        var numeric2 = true
        var numeric3 = true

        if(select_food_image == null){
            Toast.makeText(this,"Please select a food image",Toast.LENGTH_SHORT).show()
            select_food_image.requestFocus()
            return
        }

        if(food_name.text.toString().isEmpty()){
            food_name.error = "Please Enter Recipe Title!"
            food_name.requestFocus()
            return
        }
        try{
            val num = parseDouble(food_name.text.toString())
        }catch (e: NumberFormatException){
            numeric1 = false
        }

        if(numeric1){
            food_name.error = "Food name cannot be numeric"
            food_name.requestFocus()
            return
        }
        if(ctgSelected == null || ctgSelected == ""){
            add_category_dropdown_list.requestFocus()
            Toast.makeText(this, "Please select food category",Toast.LENGTH_SHORT).show()
            return
        }
        if(cuiSelected == null || cuiSelected == ""){
            add_cuisine_dropdown_list.requestFocus()
            Toast.makeText(this, "Please select food cuisine",Toast.LENGTH_SHORT).show()
            return
        }

//        if(food_category.text.toString().isEmpty()){
//            food_category.error = "Please Enter Recipe Category!"
//            food_category.requestFocus()
//            return
//        }
//        if(!food_category.text.toString().toUpperCase().equals("DINNER") && !food_category.text.toString().toUpperCase().equals("LUNCH")
//            && !food_category.text.toString().toUpperCase().equals("BREAKFAST")){
//            food_category.error = "Please Enter Correct Category"
//            food_category.requestFocus()
//            return
//        }
//
//        try{
//            val num = parseDouble(food_category.text.toString())
//        }catch (e: NumberFormatException){
//            numeric2 = false
//        }
//
//        if(numeric2){
//            food_category.error = "Food category cannot be numeric"
//            food_category.requestFocus()
//            return
//        }

        if(food_calories.text.toString().isEmpty()){
            food_calories.error = "Please Enter Food Calories"
            food_calories.requestFocus()
            return
        }
        if(food_total_fat.text.toString().isEmpty()){
            food_total_fat.error = "Please Enter Food Total Fat"
            food_total_fat.requestFocus()
            return
        }
        if(food_saturated_fat.text.toString().isEmpty()){
            food_saturated_fat.error = "Please Enter Food Saturated"
            food_saturated_fat.requestFocus()
            return
        }
        if(food_fibre.text.toString().isEmpty()){
            food_fibre.error = "Please Enter Food Fibre"
            food_fibre.requestFocus()
            return
        }
        if(food_protein.text.toString().isEmpty()){
            food_protein.error = "Please Enter Protein"
            food_protein.requestFocus()
            return
        }
        if(food_cholesterol.text.toString().isEmpty()){
            food_cholesterol.error = "Please Enter Cholesterol"
            food_cholesterol.requestFocus()
            return
        }
        if (selectFoodPhotoUri == null) {
            Toast.makeText(this, "Please select your recipe picture", Toast.LENGTH_SHORT).show()
            return
        }

//        if(food_cuisine.text.toString().isEmpty()){
//            food_cuisine.error = "Please Enter Cuisine"
//            food_cuisine.requestFocus()
//            return
//        }
//
//        if(!food_cuisine.text.toString().toUpperCase().equals("WESTERN") && !food_cuisine.text.toString().toUpperCase().equals("CHINESE")
//            && !food_cuisine.text.toString().toUpperCase().equals("INDIAN") && !food_cuisine.text.toString().toUpperCase().equals("MEXICAN")
//            && !food_cuisine.text.toString().toUpperCase().equals("ITALIAN") && !food_cuisine.text.toString().toUpperCase().equals("THAI")){
//            food_cuisine.error = "Please Enter Correct Cuisine"
//            food_cuisine.requestFocus()
//            return
//        }
//        try{
//            val num = parseDouble(food_cuisine.text.toString())
//        }catch (e: NumberFormatException){
//            numeric3 = false
//        }
//
//        if(numeric3){
//            food_cuisine.error = "Cuisine cannot be numeric"
//            food_cuisine.requestFocus()
//            return
//        }

        if(food_link.text.toString().isEmpty()){
            food_link.error = "Please Enter the Link"
            food_link.requestFocus()
            return
        }

        if(!Patterns.WEB_URL.matcher(food_link.text.toString()).matches())
        {
            food_link.error = "Invalid format"
            food_link.requestFocus()
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
        recipe.category = ctgSelected.toString()
        recipe.cuisine = cuiSelected.toString()
//        recipe.category = food_category.text.toString()
        recipe.recipeImage = foodImageUri
        recipe.calories = food_calories.text.toString().toDouble()
        recipe.totalFat = food_total_fat.text.toString().toDouble()
        recipe.saturatedFat = food_saturated_fat.text.toString().toDouble()
        recipe.fibre = food_fibre.text.toString().toDouble()
        recipe.protein = food_protein.text.toString().toDouble()
        recipe.cholesterol = food_cholesterol.text.toString().toDouble()

//        recipe.cuisine = food_cuisine.text.toString()
        recipe.urlRec = food_link.text.toString()

        recipe.staffID = currentStaff
        recipe.uploadDate = currentDate
        recipe.updateDate = currentDate
        ref.setValue(recipe)

        val intent = Intent(this, swm_Add_Ing_Activity::class.java)
        intent.putExtra("RecipeTitle", recipe.recipeTitle)
        intent.putExtra("RecipeId", recID)
        Toast.makeText(this , " successfully", Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

//    private fun swapactivitylayout(){
//        (activity as swmDashboardActivity).back()
//    }
}