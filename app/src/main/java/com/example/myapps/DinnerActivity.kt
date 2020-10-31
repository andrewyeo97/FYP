package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_breakfast.*
import kotlinx.android.synthetic.main.activity_dinner.*
import kotlinx.android.synthetic.main.activity_lunch.*

class DinnerActivity : AppCompatActivity() {
    val Dcategory: String = "Dinner"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dinner)

        dinnerBack.setOnClickListener {
            onBackPressed()
        }

        inviText3.setOnClickListener {
            onBackPressed()
        }

        westernBtnDinner.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Dcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Western")
            startActivity(intent)
        }

        chineseBtnDinner.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Dcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Chinese")
            startActivity(intent)
        }

        indianBtnDinner.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Dcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Indian")
            startActivity(intent)
        }

        mexicanBtnDinner.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Dcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Mexican")
            startActivity(intent)
        }

        italianBtnDinner.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Dcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Italian")
            startActivity(intent)
        }

        thaiBtnDinner.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(BreakfastActivity.rc_category,Dcategory)
            intent.putExtra(BreakfastActivity.rc_cuisine,"Thai")
            startActivity(intent)
        }
    }

    companion object{
        val rc_category = "rc_category"
        val rc_cuisine = "rc_cuisine"
    }
}