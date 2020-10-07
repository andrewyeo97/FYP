package com.example.myapps

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapps.fragments.FavouriteFragment
import com.example.myapps.fragments.HomeFragment
import com.example.myapps.fragments.SettingsFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        checkUserAccountSignIn()

        val homeFragment = HomeFragment()
        val favouriteFragment = FavouriteFragment()
        val settingFragment = SettingsFragment()

        makeCurrentFragment(homeFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_favorite -> makeCurrentFragment(favouriteFragment)
                R.id.ic_settings -> makeCurrentFragment(settingFragment)
            }
            true
        }

    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper,fragment)
            commit()
        }

    private fun checkUserAccountSignIn(){
        if (FirebaseAuth.getInstance().uid.isNullOrEmpty()){
            MainActivity.launchIntentClearTask(this)
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onBackPressed() {
            finishAffinity()
    }

     private fun signOutUser(){
         FirebaseAuth.getInstance().signOut()
         MainActivity.launchIntentClearTask(this)
}









 }
