package com.example.myapps

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_swm__add__ing_.*
import kotlinx.android.synthetic.main.activity_wm_view_report_.*
import kotlinx.android.synthetic.main.recycle_row_report.view.*
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class wm_view_report_Activity : AppCompatActivity() {
    var found: Boolean = false
    var dateList : MutableList<String> = ArrayList()
    var month = Calendar.getInstance().get(Calendar.MONTH) + 1
    var dateSelected : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_view_report_)
        AdddateList()
    }

    override fun onStart() {
        super.onStart()
        getDate()

    }
    private fun AdddateList(){
//        dateSelected = month.toString()
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
        date_dropdown.adapter = adapter

        date_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val slcItem : String = dateList[position]
                dateSelected = slcItem.toString()

                getDate()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        DateHight(date_dropdown)
    }

    private fun DateHight(date_dropdown : Spinner){
        val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(date_dropdown) as ListPopupWindow
        popupWindow.height = (8 * resources.displayMetrics.density).toInt()
    }

    private fun getDate() {
        var counter: Int = 0
        var counter1 : Int = 0
        var total : Int = 0
        var format  = SimpleDateFormat("dd/MMM/yyyy", Locale.US)
        var today = format.format(Date())
        var formatMonth = SimpleDateFormat("MM", Locale.US)
        var formatYear = SimpleDateFormat("yyyy", Locale.US)
        var year = Calendar.getInstance().get(Calendar.YEAR)
        var month = Calendar.getInstance().get(Calendar.MONTH) + 1

        report_today.setText(today.toString())

        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            val list = arrayListOf<User>()
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val us = it.getValue(User::class.java)
                    val userMonth = formatMonth.format(us?.registerDate)
                    val userYear = formatYear.format(us?.registerDate)
                    if (us != null) {
                        total  = total + 1
                        if (userYear.toString().equals(year.toString())) {
                            if(dateSelected.equals("")){
                                if(userMonth.toString().equals(month.toString())){
                                    counter = counter + 1
                                    list.add(us)
                                    if(list.count() == snapshot.children.count()){
                                        list.sortBy {it.registerDate}
                                        list.forEach {
                                            adapter.add(bindataRe(it))
                                        }
                                    }
                                }
                            }else{
                                if (userMonth.toString().equals(dateSelected)) {
                                    counter = counter + 1
                                    list.add(us)
                                    if(list.count() == snapshot.children.count()){
                                        list.sortBy {it.registerDate}
                                        list.forEach {
                                            adapter.add(bindataRe(it))
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
                recycle_report_dis.adapter = adapter
                report_total_user.setText(total.toString())
                report_total.setText(counter.toString())
            }
        })
    }


    class bindataRe(val user: User) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val date = formate.format(user.registerDate)
            viewHolder.itemView.ryc_report_date.text = date.toString()
            viewHolder.itemView.ryr_report_name.text = user.username
            viewHolder.itemView.ryr_report_email.text = user.email
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_report
        }

    }
}


