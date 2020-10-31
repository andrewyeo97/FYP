package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_breakfast.*
import kotlinx.android.synthetic.main.activity_lunch.*

class LunchActivity : AppCompatActivity() {
    val Lcategory: String = "Lunch"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)

        lunchBack.setOnClickListener {
            onBackPressed()
        }

        inviText2.setOnClickListener {
            onBackPressed()
        }

        westernBtnLunch.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Lcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Western")
            startActivity(intent)
        }

        chineseBtnLunch.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Lcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Chinese")
            startActivity(intent)
        }

        indianBtnLunch.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Lcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Indian")
            startActivity(intent)
        }

        mexicanBtnLunch.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Lcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Mexican")
            startActivity(intent)
        }

        italianBtnLunch.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Lcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Italian")
            startActivity(intent)
        }

        thaiBtnLunch.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Lcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Thai")
            startActivity(intent)
        }
    }

    companion object{
        val rc_category = "rc_category"
        val rc_cuisine = "rc_cuisine"
    }
}