package com.example.myapps

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.*
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
import kotlinx.android.synthetic.main.activity_wm_add_new__recipe_.*
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.*
import kotlinx.android.synthetic.main.fragment_wm_add.*
import java.lang.Double
import java.lang.Double.parseDouble
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Recipe_update_Activity : AppCompatActivity() {


    var selectUpdatePhotoUri: Uri? = null
    var imageString: String =""

    var categoryList : MutableList<String> = ArrayList()
    var cuisineList : MutableList<String> = ArrayList()
    var ctgSelected : String = ""
    var currentCtg : String = ""
    var cuiSelected : String = ""
    var currentCui : String = ""

    val sdf = SimpleDateFormat("dd/M/yyyy")
    var currentDate = Calendar.getInstance().time

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
//        ctgList()
    }

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

        val ref2 = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID").equalTo(
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
//                        update_food_category.setText(rc.category)
                        //recipe nutrition fact
                        currentCtg = rc.category
                        currentCui = rc.cuisine
                        update_food_calories.setText(rc.calories.toString())
                        update_total_fat.setText(rc.totalFat.toString())
                        update_saturated_fat.setText(rc.saturatedFat.toString())
                        update_fibre.setText(rc.fibre.toString())
                        update_protein.setText(rc.protein.toString())
                        update_cholesterol.setText(rc.cholesterol.toString())
                        update_food_link.setText(rc.urlRec)

                        imageString = rc.recipeImage

                        ctgList()
                        curList()
                    }
                }
            }
        })
    }

    private fun ctgList(){
        val b: String = "Breakfast"
        val l: String  = "Lunch"
        val d: String = "Dinner"


        if(currentCtg.equals("Breakfast")) {
            categoryList.add(currentCtg)
            categoryList.add(l)
            categoryList.add(d)
        }

        else if(currentCtg.equals("Lunch")){
            categoryList.add(currentCtg)
            categoryList.add(b)
            categoryList.add(d)
        }
        else if(currentCtg.equals("Dinner")){
            categoryList.add(currentCtg)
            categoryList.add(b)
            categoryList.add(l)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(this,

            R.layout.support_simple_spinner_dropdown_item,categoryList)
        category_dropdown_list.adapter = adapter

        category_dropdown_list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

        ctgHight(category_dropdown_list)
    }

    private fun ctgHight(category_dropdown_list: Spinner){
        val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(category_dropdown_list) as ListPopupWindow
        popupWindow.height = (200 * resources.displayMetrics.density).toInt()
    }

    private fun curList(){
        val wes : String = "Western"
        val chi : String = "Chinese"
        val ind : String = "Indian"
        val mex : String = "Mexican"
        val ita : String = "Italian"
        val tha : String = "Thai"

        if(currentCui.equals("Western")){
            cuisineList.add(currentCui)
            cuisineList.add(chi)
            cuisineList.add(ind)
            cuisineList.add(mex)
            cuisineList.add(ita)
            cuisineList.add(tha)
        }
        if(currentCui.equals("Chinese")){
            cuisineList.add(currentCui)
            cuisineList.add(wes)
            cuisineList.add(ind)
            cuisineList.add(mex)
            cuisineList.add(ita)
            cuisineList.add(tha)
        }
        if(currentCui.equals("Indian")){
            cuisineList.add(currentCui)
            cuisineList.add(chi)
            cuisineList.add(wes)
            cuisineList.add(mex)
            cuisineList.add(ita)
            cuisineList.add(tha)
        }
        if(currentCui.equals("Mexican")){
            cuisineList.add(currentCui)
            cuisineList.add(chi)
            cuisineList.add(wes)
            cuisineList.add(ind)
            cuisineList.add(ita)
            cuisineList.add(tha)
        }
        if(currentCui.equals("Italian")){
            cuisineList.add(currentCui)
            cuisineList.add(chi)
            cuisineList.add(wes)
            cuisineList.add(ind)
            cuisineList.add(mex)
            cuisineList.add(tha)
        }
        if(currentCui.equals("Thai")){
            cuisineList.add(currentCui)
            cuisineList.add(chi)
            cuisineList.add(wes)
            cuisineList.add(ind)
            cuisineList.add(mex)
            cuisineList.add(ita)
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item,cuisineList)

        cuisine_dropdown_list.adapter = adapter
        cuisine_dropdown_list.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
        cuiHight(cuisine_dropdown_list)
    }

    private fun cuiHight(cuisine_dropdown_list: Spinner){
        val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(cuisine_dropdown_list) as ListPopupWindow
        popupWindow.height = (5 * resources.displayMetrics.density).toInt()
    }


    private fun updateValue(){

        var numeric1 = true

        val id = intent.getStringExtra("RecipeId")
        val ref = FirebaseDatabase.getInstance().getReference("Recipe/$id")

        if(update_food_name.text.isNotEmpty()){
            val title = update_food_name.text.toString()
            ref.child("recipeTitle").setValue(title)
        }
        else{
            update_food_name.error = "Title cannot be empty..."
            update_food_name.requestFocus()
            return
        }

        try{
            val num = Double.parseDouble(update_food_name.text.toString())
        }catch (e: NumberFormatException){
            numeric1 = false
        }
        if(numeric1){
            update_food_name.error = "Food name cannot be numeric"
            update_food_name.requestFocus()
            return
        }else{
            val title = update_food_name.text.toString()
            ref.child("recipeTitle").setValue(title)
        }

        if(ctgSelected == null || ctgSelected == ""){
            val category  = currentCtg.toString()
            ref.child("category").setValue(currentCui.toString())
        }else{
            ref.child("category").setValue(ctgSelected.toString())
        }
        if(cuiSelected == null || cuiSelected == ""){
            val cuisine  = currentCui.toString()
            ref.child("category").setValue(currentCui.toString())
        }else{
            ref.child("cuisine").setValue(ctgSelected.toString())
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

        if(update_food_link.text.isEmpty()){
            update_food_link.error = "Recipe Link cannot be empty..."
            update_food_link.requestFocus()
            return
        }else{
            val link = update_food_link.text.toString()
            ref.child("urlRec").setValue(link)
        }
        if(!Patterns.WEB_URL.matcher(update_food_link.text.toString()).matches()) {
            update_food_link.error = "Invalid format"
            update_food_link.requestFocus()
            return
        }

        ref.child("updateDate").setValue(currentDate)

        ref.child("category").setValue(ctgSelected)
        ref.child("cuisine").setValue(cuiSelected)


        uploadFoodImageToFirebaseStorage()
        Toast.makeText(baseContext, "Update Successful", Toast.LENGTH_SHORT).show()
    }

    private fun uploadFoodImageToFirebaseStorage(){
        val id = intent.getStringExtra("RecipeId")
        val ref1 = FirebaseDatabase.getInstance().getReference("Recipe/$id")
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