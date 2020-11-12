package com.example.myapps

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_user__profile_.*
import java.text.SimpleDateFormat
import java.util.*

class View_user_Profile_Activity : AppCompatActivity() {

    var selectStaffUpdatePhotoUri: Uri? = null
    var imageString: String =""
    val currentuserID = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_user__profile_)

        loadProfile()

        profile_staff_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        staff_profile_update.setOnClickListener {
            updateProfile()
        }

        staff_profile_update_pass.setOnClickListener {
            val intent = Intent(this, wm_update_staff_password_Activity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectStaffUpdatePhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(
                contentResolver,
                selectStaffUpdatePhotoUri
            )
            profile_staff_image.setImageBitmap(bitmap)
        }
    }

    private fun loadProfile(){
        val ref = FirebaseDatabase.getInstance().getReference("/Staffs").orderByChild("staff_id").equalTo(
            currentuserID
        )
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val staff = it.getValue(Staff::class.java)
                    if (staff != null) {
                        var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                        var date  = formate.format(staff.register_date)
                        profile_staff_name.setText(staff.staff_name.toUpperCase())
                        Picasso.get().load(staff.staff_profileImageUrl).into(profile_staff_image)
                        profile_staff_contactNo.setText(staff.staff_contactNo)
                        profile_staff_email.setText(staff.staff_email)
                        profile_staff_address.setText(staff.staff_address)
                        profile_staff_joinDate.setText(date.toString())
                        profile_staff_position.setText(staff.staff_position.toUpperCase())

                        imageString = staff.staff_profileImageUrl
                    }

                }
            }
        })
    }

    private fun updateProfile(){
        val ref = FirebaseDatabase.getInstance().getReference("/Staffs/${currentuserID}")

        if(profile_staff_contactNo.text.isNotEmpty()){
            val cNo = profile_staff_contactNo.text.toString()
            ref.child("staff_contactNo").setValue(cNo)
        }else{
            profile_staff_contactNo.error = "Contact Number cannot be empty..."
            profile_staff_contactNo.requestFocus()
            return
        }
        if(profile_staff_address.text.isNotEmpty()){
            val address = profile_staff_address.text.toString()
            ref.child("staff_address").setValue(address)
        }else{
            profile_staff_address.error = "Address cannot be empty..."
            profile_staff_address.requestFocus()
            return
        }
        uploadStaffImageToFirebaseStorage()
    }

    private fun uploadStaffImageToFirebaseStorage(){
        val ref = FirebaseDatabase.getInstance().getReference("/Staffs/${currentuserID}")
        val filename = UUID.randomUUID().toString()
        val ref1 = FirebaseStorage.getInstance().getReference("/StaffProfileImages/$filename")

        if(selectStaffUpdatePhotoUri==null){
            ref.child("staff_profileImageUrl").setValue(imageString)
        }else{
            ref1.putFile(selectStaffUpdatePhotoUri!!).addOnSuccessListener {
                ref1.downloadUrl.addOnSuccessListener {
                    ref.child("staff_profileImageUrl").setValue(it.toString())
                }
            }
        }
        Toast.makeText(baseContext, "Update successful", Toast.LENGTH_SHORT).show()
//        val title = update_food_name.text.toString()
//        id.toString()
//        val intent = Intent(this, wm_update_ing_Activity::class.java)
//        intent.putExtra("UpdateRecipeTitle", title)
//        intent.putExtra("updateRecipeId", id)
//        startActivity(intent)
    }
}