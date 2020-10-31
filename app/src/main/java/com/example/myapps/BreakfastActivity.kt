package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_breakfast.*
import kotlinx.android.synthetic.main.activity_recipe_recycle_view_list.*

class BreakfastActivity : AppCompatActivity() {
    val Bcategory: String = "Breakfast"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breakfast)

        breakfastBack.setOnClickListener {
            onBackPressed()
        }

        inviText1.setOnClickListener {
            onBackPressed()
        }

        westernBtnBreakfast.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(rc_category,Bcategory)
            intent.putExtra(rc_cuisine,"Western")
            startActivity(intent)
        }

        chineseBtnBreakfast.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(rc_category,Bcategory)
            intent.putExtra(rc_cuisine,"Chinese")
            startActivity(intent)
        }

        indianBtnBreakfast.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(rc_category,Bcategory)
            intent.putExtra(rc_cuisine,"Indian")
            startActivity(intent)
        }

        mexicanBtnBreakfast.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(rc_category,Bcategory)
            intent.putExtra(rc_cuisine,"Mexican")
            startActivity(intent)
        }

        italianBtnBreakfast.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(rc_category,Bcategory)
            intent.putExtra(rc_cuisine,"Italian")
            startActivity(intent)
        }

        thaiBtnBreakfast.setOnClickListener {
            val intent = Intent(this,RecipeRecycleViewList::class.java)
            intent.putExtra(rc_category,Bcategory)
            intent.putExtra(rc_cuisine,"Thai")
            startActivity(intent)
        }
    }

    companion object{
        val rc_category = "rc_category"
        val rc_cuisine = "rc_cuisine"
    }

}