package com.example.myapps

import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.user_login_page.*

class UserLoginPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_login_page)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            doLogin()
        }
    }

    fun exit(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        FirebaseAuth.getInstance().signOut()
        this.finish()
    }
    override fun onBackPressed() {
        exit(this)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        updateUI(currentUser)

    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(
                    baseContext, "Please verify your email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
    private fun doLogin(){
        if(emailEdit.text.toString().isEmpty()){
            emailEdit.error = "Please enter email address"
            emailEdit.requestFocus()
            return
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(emailEdit.text.toString()).matches()){
            emailEdit.error = "Please enter valid email address"
            emailEdit.requestFocus()
            return
        }

        if(passwordEdit.text.toString().isEmpty()){
            passwordEdit.error = "Please enter password"
            passwordEdit.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (FirebaseAuth.getInstance().currentUser !== null) {
                        val user = auth.currentUser
                        updateUI(user)

                    } else {
                        Toast.makeText(
                            baseContext, "Login failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        updateUI(null)
                    }
                }
                else {
                    Toast.makeText(
                        baseContext, "Incorrect email or password",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }


}
