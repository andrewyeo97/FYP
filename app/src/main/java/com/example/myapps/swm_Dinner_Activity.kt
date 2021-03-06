package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_dinner.*
import kotlinx.android.synthetic.main.activity_swm__dinner_.*
import kotlinx.android.synthetic.main.recycle_row_category.view.*

class swm_Dinner_Activity : AppCompatActivity() {
    val Dcategory: String = "Dinner"
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swm__dinner_)

        wm_dinner_back.setOnClickListener {
            onBackPressed()
        }

        dinner_textView1.setOnClickListener {
            onBackPressed()
        }
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            wm_recycle_dinner.layoutManager = this
        }

        for(i in 0 until 6 step 1){
            adapter.add(loadItem())}
        wm_recycle_dinner.adapter = adapter
    }

    companion object{
        val rc_category = "rc_category"
        val rc_cuisine = "rc_cuisine"
    }

    class loadItem(): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if(position == 0){
                viewHolder.itemView.txtCat.text = "Western"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.banana)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,wm_view_recipe_list_Activity::class.java)
                    intent.putExtra(swm_Dinner_Activity.rc_category,"Dinner")
                    intent.putExtra(swm_Dinner_Activity.rc_cuisine,"Western")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 1){
                viewHolder.itemView.txtCat.text = "Chinese"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.pancake)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,wm_view_recipe_list_Activity::class.java)
                    intent.putExtra(swm_Dinner_Activity.rc_category,"Dinner")
                    intent.putExtra(swm_Dinner_Activity.rc_cuisine,"Chinese")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 2){
                viewHolder.itemView.txtCat.text = "Indian"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.currysalad)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,wm_view_recipe_list_Activity::class.java)
                    intent.putExtra(swm_Dinner_Activity.rc_category,"Dinner")
                    intent.putExtra(swm_Dinner_Activity.rc_cuisine,"Indian")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 3){
                viewHolder.itemView.txtCat.text = "Mexican"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.taco)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,wm_view_recipe_list_Activity::class.java)
                    intent.putExtra(swm_Dinner_Activity.rc_category,"Dinner")
                    intent.putExtra(swm_Dinner_Activity.rc_cuisine,"Mexican")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 4){
                viewHolder.itemView.txtCat.text = "Italian"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.parmesan)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,wm_view_recipe_list_Activity::class.java)
                    intent.putExtra(swm_Dinner_Activity.rc_category,"Dinner")
                    intent.putExtra(swm_Dinner_Activity.rc_cuisine,"Italian")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 5){
                viewHolder.itemView.txtCat.text = "Thai"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.sweetchili)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,wm_view_recipe_list_Activity::class.java)
                    intent.putExtra(swm_Dinner_Activity.rc_category,"Dinner")
                    intent.putExtra(swm_Dinner_Activity.rc_cuisine,"Thai")
                    it.context.startActivity(intent)
                }

            }
        }
        override fun getLayout(): Int {
            return R.layout.recycle_row_category
        }
    }
}