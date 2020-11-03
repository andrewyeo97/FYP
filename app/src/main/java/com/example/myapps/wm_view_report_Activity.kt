package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.*
import kotlinx.android.synthetic.main.activity_wm_view_report_.*
import kotlinx.android.synthetic.main.recycle_row_ratings.view.*
import kotlinx.android.synthetic.main.recycle_row_ratings.view.ratingImage
import kotlinx.android.synthetic.main.recycle_row_report.view.*
import kotlinx.android.synthetic.main.wm_recycle_row_step2.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class wm_view_report_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_view_report_)

    }

    override fun onStart() {
        super.onStart()
        getDate()
    }



    private fun getDate() {
//        val dateList = arrayListOf<Rating>()
        val list = arrayListOf<Rating>()
        val month = SimpleDateFormat("MM").format(Date())

        val ref1 = FirebaseDatabase.getInstance().getReference("/Rating").child("ratingDate").child("month").equalTo(month)

        ref1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val rat = it.getValue(Rating::class.java)
                    if(rat != null){
                        textView46.setText(rat.ratingID)
                    }else{
                        textView46.setText("hai")
                    }


//                    if (stp != null) {
//                        list.add(stp)
//                        if (list.count() == snapshot.children.count()) {
//                            list.forEach {
//                                adapter.add(wm_view_report_Activity.bindataRating(it))
//                            }
//                        }
//
//                    }
                }
//                recycle_report_dis.adapter = adapter

            }
        })
    }
    private class bindataRating(val rating: Rating): Item<GroupieViewHolder>() {
        override fun bind(viewHolder2: GroupieViewHolder, position: Int) {
            Picasso.get().load(rating.profileImageUrl).into(viewHolder2.itemView.report_foodImage)
            viewHolder2.itemView.report_ratingBar.rating = rating.ratingNumber
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_report
        }
    }



//        val ref1 = FirebaseDatabase.getInstance().getReference("/Rating").child("ratingDate")
//                            isFound = true
//                            ttlRating = ttlRating + rate.ratingNumber
//                            counter = counter + 1
//                        }
//
//                    }
//                    avgRating = ttlRating/counter
//                    ratingText.text = (avgRating.toString() + " ratings | " + counter + " review(s)")
//                    if(isFound == false){
//                        ratingText.text = ("No review yet")}
//                }
//            })
//            handlers.postDelayed(this, 100)
//        }
//    }

//    private fun bindSteps() {


//        val list = arrayListOf<Rating>()
//
//        val ref = FirebaseDatabase.getInstance().getReference("/wmStep").orderByChild("recipeID").equalTo(rc_id)
//        ref.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {}
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val adapter = GroupAdapter<GroupieViewHolder>()
//                snapshot.children.forEach {
//                    val stp = it.getValue(Steps::class.java)
//                    if (stp != null) {
//                        list.add(stp)
//                        if (list.count() == snapshot.children.count()) {
//                            list.sortBy { it.stepNo }
//                            list.forEach {
//                                adapter.add(wmStaff_RecipeDetail_Activity.bindataStep(it))
//                            }
//                        }
//
//                    }
//                }
//                recycle_step_dis2.adapter = adapter
//
//            }
//        })
//    }

//    private class bindataStep(val steps: Steps): Item<GroupieViewHolder>() {
//        override fun bind(viewHolder2: GroupieViewHolder, position: Int) {
//            viewHolder2.itemView.step_number2.text = steps.stepNo.toString()
//            viewHolder2.itemView.step_description2.text = steps.desc
//        }
//
//        override fun getLayout(): Int {
//            return R.layout.wm_recycle_row_step2
//        }
//    }
}


