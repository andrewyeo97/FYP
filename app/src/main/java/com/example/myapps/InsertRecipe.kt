package com.example.myapps

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
    private val chanelID = "1234"
    private val notifiID = 101

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

    private fun saveUserToDatabase(ImageUrl: String){
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
        recipe.recipeImage = ImageUrl
        recipe.averageRating = 0.00F
        recipe.urlRec = "https://www.slenderkitchen.com/recipe/thai-basil-shrimp-stir-fry"
        ref.setValue(recipe)
        createnotify()
        sendNotify()
    }

    private fun createnotify(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "New Recipe Added"
            val desc = recipeTitle.text.toString()
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(chanelID,name,importance).apply{
                description = desc
            }
            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotify(){
        val intent = Intent(this,MainActivity::class.java).apply{
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending : PendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val time= SimpleDateFormat("HH:mm").format(Date())
        val builder =   NotificationCompat.Builder(this,chanelID)
            .setSmallIcon(R.drawable.new_recipe)
            .setContentTitle("New Release")
            .setContentText(recipeTitle.text.toString())
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notifiID,builder.build())
        }
    }
}