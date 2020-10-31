package com.example.myapps.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapps.R
import com.example.myapps.Recipe
import com.example.myapps.RecipeDetailActivity
import com.example.myapps.wmStaff_RecipeDetail_Activity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [wmHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class wmHomeFragment : Fragment() {
    var rec = Recipe()
    override fun onStart() {
        super.onStart()
        init()
    }

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    fun init(){
        val ref = FirebaseDatabase.getInstance().getReference("/wmRecipe")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val rep = it.getValue(Recipe::class.java)
                    if(rep != null){
                        adapter.add(bindata(rep))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val recDetail = item as bindata
                    val intent = Intent(view.context, wmStaff_RecipeDetail_Activity::class.java)
                    intent.putExtra(RECIPE_KEY,recDetail.recipe.recipeID)
                    startActivity(intent)
                }
        //        ryr_view.adapter = adapter
            }
        })
    }

    class bindata(val recipe : Recipe): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.ryr_title.text = recipe.recipeTitle
            Picasso.get().load(recipe.recipeImage).into(viewHolder.itemView.ryr_image)
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_item
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }
}