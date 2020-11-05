package com.example.myapps

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_breakfast.*
import kotlinx.android.synthetic.main.activity_recipe_recycle_view_list.*
import kotlinx.android.synthetic.main.recycle_row_category.view.*
import kotlinx.android.synthetic.main.recycle_row_ingredients.view.*

class BreakfastActivity : AppCompatActivity() {
    val Bcategory: String = "Breakfast"
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breakfast)


        breakfastBack.setOnClickListener {
            onBackPressed()
        }

        inviText1.setOnClickListener {
            onBackPressed()
        }

        StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL).apply {
            ryr_breakfast.layoutManager = this
        }

        for(i in 0 until 6 step 1){
        adapter.add(loadItem())}
        ryr_breakfast.adapter = adapter





//        westernBtnBreakfast.setOnClickListener {
//            val intent = Intent(this,RecipeRecycleViewList::class.java)
//            intent.putExtra(rc_category,Bcategory)
//            intent.putExtra(rc_cuisine,"Western")
//            startActivity(intent)
//        }
//
//        chineseBtnBreakfast.setOnClickListener {
//            val intent = Intent(this,RecipeRecycleViewList::class.java)
//            intent.putExtra(rc_category,Bcategory)
//            intent.putExtra(rc_cuisine,"Chinese")
//            startActivity(intent)
//        }
//
//        indianBtnBreakfast.setOnClickListener {
//            val intent = Intent(this,RecipeRecycleViewList::class.java)
//            intent.putExtra(rc_category,Bcategory)
//            intent.putExtra(rc_cuisine,"Indian")
//            startActivity(intent)
//        }
//
//        mexicanBtnBreakfast.setOnClickListener {
//            val intent = Intent(this,RecipeRecycleViewList::class.java)
//            intent.putExtra(rc_category,Bcategory)
//            intent.putExtra(rc_cuisine,"Mexican")
//            startActivity(intent)
//        }
//
//        italianBtnBreakfast.setOnClickListener {
//            val intent = Intent(this,RecipeRecycleViewList::class.java)
//            intent.putExtra(rc_category,Bcategory)
//            intent.putExtra(rc_cuisine,"Italian")
//            startActivity(intent)
//        }
//
//        thaiBtnBreakfast.setOnClickListener {
//            val intent = Intent(this,RecipeRecycleViewList::class.java)
//            intent.putExtra(rc_category,Bcategory)
//            intent.putExtra(rc_cuisine,"Thai")
//            startActivity(intent)
//        }
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
                    val intent = Intent(it.context,RecipeRecycleViewList::class.java)
                    intent.putExtra(rc_category,"Breakfast")
                    intent.putExtra(rc_cuisine,"Western")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 1){
                viewHolder.itemView.txtCat.text = "Chinese"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.wantan1)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,RecipeRecycleViewList::class.java)
                    intent.putExtra(rc_category,"Breakfast")
                    intent.putExtra(rc_cuisine,"Chinese")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 2){
                viewHolder.itemView.txtCat.text = "Indian"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.currysalad)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,RecipeRecycleViewList::class.java)
                    intent.putExtra(rc_category,"Breakfast")
                    intent.putExtra(rc_cuisine,"Indian")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 3){
                viewHolder.itemView.txtCat.text = "Mexican"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.taco)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,RecipeRecycleViewList::class.java)
                    intent.putExtra(rc_category,"Breakfast")
                    intent.putExtra(rc_cuisine,"Mexican")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 4){
                viewHolder.itemView.txtCat.text = "Italian"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.parmesan)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,RecipeRecycleViewList::class.java)
                    intent.putExtra(rc_category,"Breakfast")
                    intent.putExtra(rc_cuisine,"Italian")
                    it.context.startActivity(intent)
                }
            }
            else if(position == 5){
                viewHolder.itemView.txtCat.text = "Thai"
                viewHolder.itemView.imgCat.setImageResource(R.drawable.sweetchili)

                viewHolder.itemView.cons_break.setOnClickListener {
                    val intent = Intent(it.context,RecipeRecycleViewList::class.java)
                    intent.putExtra(rc_category,"Breakfast")
                    intent.putExtra(rc_cuisine,"Thai")
                    it.context.startActivity(intent)
                }

            }





        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_category
        }
    }

}