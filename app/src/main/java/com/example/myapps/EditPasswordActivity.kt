package com.example.myapps

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_edit_password.*
import kotlinx.android.synthetic.main.user_login_page.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class EditPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_password)

        auth = FirebaseAuth.getInstance()
        back_btn_editPassword.setOnClickListener {
            onBackPressed()
        }

        savePasswordButton.setOnClickListener {
            changePassword()
        }

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
   // (?=.*[A-Z])

    private fun changePassword(){

        if(oldPasswordEdit.text.toString().isEmpty()){
            Toast.makeText(baseContext, "Please enter the field", Toast.LENGTH_SHORT).show()
            oldPasswordEdit.requestFocus()
            return
        }

        if(newPasswordEdit.text.toString().isEmpty()){
            Toast.makeText(baseContext, "Please enter the field", Toast.LENGTH_SHORT).show()
            newPasswordEdit.requestFocus()
            return
        }

        if(confirmPasswordEdit.text.toString().isEmpty()){
            Toast.makeText(baseContext, "Please enter the field", Toast.LENGTH_SHORT).show()
            confirmPasswordEdit.requestFocus()
            return
        }

            if (newPasswordEdit.text.toString().equals(confirmPasswordEdit.text.toString())) {
                if(isValidPassword(newPasswordEdit.text.toString().trim())) {
                    val user = auth.currentUser
                    if (user != null && user.email != null) {
                        val credential = EmailAuthProvider.getCredential(
                            user.email!!,
                            oldPasswordEdit.text.toString()
                        )
                        user?.reauthenticate(credential)?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    baseContext,
                                    "Reauthentication successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                                user?.updatePassword(newPasswordEdit.text.toString())
                                    ?.addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            Toast.makeText(
                                                baseContext,
                                                "Password changed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onBackPressed()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    baseContext,
                                    "Current password is incorrect",
                                    Toast.LENGTH_SHORT
                                ).show()
                                oldPasswordEdit.requestFocus()
                            }
                        }

                    } else {
                        startActivity(Intent(this, UserLoginPage::class.java))
                        finish()}
                }
                else{
                    Toast.makeText(baseContext, "Password does not fulfill the criteria", Toast.LENGTH_SHORT).show()
                    newPasswordEdit.requestFocus()
                    return}
            } else {
                Toast.makeText(baseContext, "Password mismatch", Toast.LENGTH_SHORT).show()
                confirmPasswordEdit.requestFocus()
                return}

    }

}