package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapps.fragments.wmAddFragment
import com.example.myapps.fragments.wmHomeFragment
import com.example.myapps.fragments.wmSettingFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class swmDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swm_dashboard)

        checkUserAccountSignIn()

        val homeFragment = wmHomeFragment()
        val addFragment = wmAddFragment()
        val settingFragment = wmSettingFragment()

        makeCurrentFragment(homeFragment)

        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.swm_ic_home -> makeCurrentFragment(homeFragment)
                R.id.swm_ic_add -> makeCurrentFragment(addFragment)
                R.id.swm_ic_setting-> makeCurrentFragment(settingFragment)
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
            //MainActivity.launchIntentClearTask(this)
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }


    fun back(){
        val trans = supportFragmentManager.beginTransaction()
        val fragment = wmHomeFragment() //call fragment .kt file
        trans.addToBackStack(null)
        trans.replace(R.id.fl_wrapper,fragment) //put fragment into the layout
        trans.commit()
    }

}