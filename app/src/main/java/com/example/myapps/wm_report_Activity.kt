package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_wm_report_.*
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.*

class wm_report_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_report_)

        buttonUserReport.setOnClickListener {
            val intent = Intent(this, wm_view_report_Activity::class.java)
            startActivity(intent)
        }
        imageViewUser.setOnClickListener {
            val intent = Intent(this, wm_view_report_Activity::class.java)
            startActivity(intent)
        }

        buttonRecipeSummary.setOnClickListener {
            val intent = Intent(this, wm_total_recipe_summary_Activity::class.java)
            startActivity(intent)
        }

        imageViewRecipeSummary.setOnClickListener {
            val intent = Intent(this, wm_total_recipe_summary_Activity::class.java)
            startActivity(intent)
        }
    }
}