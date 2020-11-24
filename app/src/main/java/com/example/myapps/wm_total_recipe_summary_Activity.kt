package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Spinner
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_wm_total_recipe_summary_.*
import kotlinx.android.synthetic.main.activity_wm_view_report_.*
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*

class wm_total_recipe_summary_Activity : AppCompatActivity() {

    val sdf = SimpleDateFormat("dd/M/yyyy")
    var currentDate = Calendar.getInstance().time

    var dateList : MutableList<String> = ArrayList()
    var dateSelected : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_total_recipe_summary_)

        sum_today.setText(sdf.format(currentDate))
        AddDateList()
    }

    override fun onStart() {
        super.onStart()
        getTotal()
    }

    private fun AddDateList(){
        dateList.add("")
        dateList.add("1")
        dateList.add("2")
        dateList.add("3")
        dateList.add("4")
        dateList.add("5")
        dateList.add("6")
        dateList.add("7")
        dateList.add("8")
        dateList.add("9")
        dateList.add("10")
        dateList.add("11")
        dateList.add("12")

        val adapter: ArrayAdapter<String> = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item, dateList)
        sum_date_dropdown.adapter = adapter

        sum_date_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val slcItem : String = dateList[position]
                dateSelected = slcItem.toString()

                getTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        DateHight(sum_date_dropdown)
    }

    private fun DateHight(sum_date_dropdown : Spinner){
        val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(sum_date_dropdown) as ListPopupWindow
        popupWindow.height = (8 * resources.displayMetrics.density).toInt()
    }

    private fun getTotal(){
        var totalThis : Int = 0
        var breakTotal : Int = 0
        var lunchTotal : Int = 0
        var dinnerTotal : Int = 0
        var totalRecp : Int = 0
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
                            if(dateSelected.equals("")){
                                if(RecpMonth.toString().equals(month.toString())){
                                    totalThis = totalThis + 1
                                }
                            }else{
                                if(RecpMonth.toString().equals(dateSelected)){
                                    totalThis = totalThis + 1
                                }
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