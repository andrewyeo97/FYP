package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_edit_password.*
import kotlinx.android.synthetic.main.activity_wm_update_staff_password_.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class wm_update_staff_password_Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_update_staff_password_)

        auth = FirebaseAuth.getInstance()

        button_change_password.setOnClickListener {
            changePass()
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

    private fun changePass() {
        if (change_old_password.text.toString().isEmpty()) {
            change_old_password.error = "Please enter password..."
            change_old_password.requestFocus()
            return
        }

        if (change_new_password.text.toString().isEmpty()) {
            change_new_password.error = "Please enter password..."
            change_new_password.requestFocus()
            return
        }

        if (change_confirm_password.text.toString().isEmpty()) {
            change_confirm_password.error = "Please enter password..."
            change_confirm_password.requestFocus()
            return
        }

        if (change_new_password.text.toString().equals(change_confirm_password.text.toString())) {
            if (isValidPassword(change_new_password.text.toString().trim())) {
                val user = auth.currentUser
                if (user != null && user.email != null) {
                    val credential = EmailAuthProvider.getCredential(
                        user.email!!,
                        change_old_password.text.toString()
                    )

                    user?.reauthenticate(credential)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                baseContext,
                                "Reauthentication successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            user?.updatePassword(change_new_password.text.toString())
                                ?.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Toast.makeText(
                                            baseContext,
                                            "Password changed successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onBackPressed()
                                    }
                                }
                        } else {
                            change_old_password.error = "Current password is incorrect"
                            change_old_password.requestFocus()
                        }
                    }
                } else {
                    startActivity(Intent(this, StaffLoginActivity::class.java))
                    finish()
                }
            } else {
                change_new_password.error = "Password does not fulfill the criteria"
                change_new_password.requestFocus()
                return
            }
        } else {
            change_confirm_password.error = "Password mismatch"
            change_confirm_password.requestFocus()
            return
        }

    }

}