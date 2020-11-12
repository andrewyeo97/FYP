package com.example.myapps

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import com.example.myapps.MainActivity
import com.example.myapps.R
import com.example.myapps.Staff
import com.example.myapps.UserLoginPage
import com.example.myapps.fragments.wmSettingFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_swm__staff__register.*
import kotlinx.android.synthetic.main.activity_view_user__profile_.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class swm_Staff_Register : AppCompatActivity() {

    var staff = Staff()
    var selectPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swm__staff__register)

        StaffRegButton.setOnClickListener {
            signUpStaff()
        }

        back_to_setting.setOnClickListener {
            val intent = Intent(this, swmDashboardActivity::class.java)
            startActivity(intent)
        }

        photo_staff_reg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        register_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectPhotoUri)
            photo_staff_reg.setImageBitmap(bitmap)
        }
    }

    private fun signUpStaff() {

        if (selectPhotoUri == null) {
            Toast.makeText(this, "Please select your profile pic", Toast.LENGTH_SHORT).show()
            return
        }

        if (regStaffnameEdit.text.toString().isEmpty()) {
            regStaffnameEdit.error = "Please enter your username"
            regStaffnameEdit.requestFocus()
            return
        }

        if (staffContactNoEdit.text.toString().isEmpty()) {
            staffContactNoEdit.error = "Please enter contact number"
        }

        if (!Patterns.PHONE.matcher(staffContactNoEdit.text.toString()).matches()) {
            staffContactNoEdit.error = "Please enter correct contact number"
        }

        if (staffAddressEdit.text.toString().isEmpty()){
            staffAddressEdit.error = "Please enter Address"
            staffAddressEdit.requestFocus()
            return
        }

        if(staffPositionEdit.text.toString().isEmpty()){
            staffPositionEdit.error = "Please  enter staff position"
            staffPositionEdit.requestFocus()
            return
        }

        if (StaffRegEmailEdit.text.toString().isEmpty()) {
            StaffRegEmailEdit.error = "Please enter your email address"
            StaffRegEmailEdit.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(StaffRegEmailEdit.text.toString()).matches()) {
            StaffRegEmailEdit.error = "Please enter valid email address"
            StaffRegEmailEdit.requestFocus()
            return
        }

        if (StaffRegPasswordEdit.text.toString().isEmpty()) {
            StaffRegPasswordEdit.error = "Please enter your password"
            StaffRegPasswordEdit.requestFocus()
            return
        }

        if(staffConformPassEdit.text.toString().isEmpty()){
            staffConformPassEdit.error = "Please enter the password again"
            staffConformPassEdit.requestFocus()
            return
        }
        if (!staffConformPassEdit.text.toString().equals(StaffRegPasswordEdit.text.toString())){
            staffConformPassEdit.error = "Password is not match"
            staffConformPassEdit.requestFocus()
            return
        }

        val email = StaffRegEmailEdit.text.toString()
        val pass = StaffRegPasswordEdit.text.toString()
        if(isValidPassword(pass)) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Toast.makeText(baseContext, "Register failed.", Toast.LENGTH_SHORT).show()
                    } else {
                        staff.staff_id = FirebaseAuth.getInstance().uid.toString()
                        uploadImageToFirebaseStorage()
                        Toast.makeText(baseContext, "Register Successful.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }else{
            Toast.makeText(baseContext, "Password does not fulfill the criteria", Toast.LENGTH_SHORT).show()
            StaffRegPasswordEdit.requestFocus()
            return

        }
    }
    private fun uploadImageToFirebaseStorage(){

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/StaffProfileImages/$filename")

        ref.putFile(selectPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                saveStaffToDatabase(it.toString())
            }
        }
    }

    private fun saveStaffToDatabase(profileImageUrl: String){
        var currentDate = Calendar.getInstance().time
        val ref = FirebaseDatabase.getInstance().getReference("/Staffs/${staff.staff_id}")
        staff.staff_name = regStaffnameEdit.text.toString()
        staff.staff_email = StaffRegEmailEdit.text.toString()
        staff.staff_contactNo = staffContactNoEdit.text.toString()
        staff.staff_profileImageUrl = profileImageUrl
        staff.staff_address = staffAddressEdit.text.toString()
        staff.staff_position = staffPositionEdit.text.toString()
        staff.register_date = currentDate

        ref.setValue(staff)
    }

    private fun isValidPassword(password: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }
}
