package com.example.myapps

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_wm_staff__recipe_detail_.*
import kotlinx.android.synthetic.main.activity_wm_update_historyt_.*
import kotlinx.android.synthetic.main.activity_wm_view_report_.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*
import kotlinx.android.synthetic.main.recycle_row_report.view.*
import kotlinx.android.synthetic.main.wm_recycle_history_list.view.*
import java.text.SimpleDateFormat
import java.util.*

class wm_update_historyt_Activity : AppCompatActivity() {

    val sdf = SimpleDateFormat("dd/M/yyyy")
    var currentDate = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_update_historyt_)

        history_today.setText(sdf.format(currentDate))
    }

    override fun onStart() {
        super.onStart()
        getHis()
    }

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }
    private fun getHis(){
        var format  = SimpleDateFormat("dd/MMM/yyyy", Locale.US)
        var today = format.format(Date())

        val ref = FirebaseDatabase.getInstance().getReference("/Recipe")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    val update = format.format(rc?.updateDate)
                    if (rc != null) {
                        if(update.equals(today))
                            adapter.add(bindataHis(rc))
                    }

                    adapter.setOnItemClickListener{ item, view ->
                        val rcDetail = item as bindataHis
                        val intent = Intent(view.context, wmStaff_RecipeDetail_Activity::class.java)
                        intent.putExtra(RECIPE_KEY,rcDetail.recipe.recipeID)
                        startActivity(intent)
                    }
                }
                wm_recycle_his.adapter = adapter
            }
        })
    }

    class bindataHis(val recipe: Recipe) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.ryr_history_name.text = recipe.recipeTitle
            Picasso.get().load(recipe.recipeImage)
                .into(viewHolder.itemView.ryr_history_image)
        }

        override fun getLayout(): Int {
            return R.layout.wm_recycle_history_list
        }

    }

}
