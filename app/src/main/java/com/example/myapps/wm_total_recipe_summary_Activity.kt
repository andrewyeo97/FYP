package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_wm_total_recipe_summary_.*
import java.text.SimpleDateFormat
import java.util.*

class wm_total_recipe_summary_Activity : AppCompatActivity() {

    val sdf = SimpleDateFormat("dd/M/yyyy")
    var currentDate = Calendar.getInstance().time
    var breakTotal : Int = 0
    var lunchTotal : Int = 0
    var dinnerTotal : Int = 0
    var totalRecp : Int = 0

    var totalThis : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_total_recipe_summary_)

        sum_today.setText(sdf.format(currentDate))
    }

    override fun onStart() {
        super.onStart()
        getTotal()
    }

    private fun getTotal(){

        var formatMonth = SimpleDateFormat("MM", Locale.US)
        var formatYear = SimpleDateFormat("yyyy", Locale.US)
        var month = Calendar.getInstance().get(Calendar.MONTH) + 1
        var year = Calendar.getInstance().get(Calendar.YEAR)

        val ref = FirebaseDatabase.getInstance().getReference("/Recipe")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    val RecpMonth = formatMonth.format(rc?.uploadDate)
                    val RecpYear = formatYear.format(rc?.uploadDate)
                    if(rc !=null){
                        totalRecp = totalRecp + 1
                        if(rc.category.toUpperCase().equals("BREAKFAST")){
                            breakTotal = breakTotal + 1
                        }
                        else if(rc.category.toUpperCase().equals("LUNCH")){
                            lunchTotal = lunchTotal + 1
                        }
                        else if (rc.category.toUpperCase().equals("DINNER")){
                            dinnerTotal = dinnerTotal + 1
                        }
                        if(RecpYear.toString().equals(year.toString())){
                            if(RecpMonth.toString().equals(month.toString())){
                                totalThis = totalThis + 1
                            }
                        }
                    }
                }
                total_break.setText(breakTotal.toString())
                total_lunch.setText(lunchTotal.toString())
                total_dinner.setText(dinnerTotal.toString())
                total_recipe.setText(totalRecp.toString())
                total_this_month.setText(totalThis.toString())
            }
        })


    }
}