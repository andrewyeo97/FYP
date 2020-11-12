package com.example.myapps

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_recycle_view_list.*
import kotlinx.android.synthetic.main.activity_recipe_recycle_view_list.searchRecipeView
import kotlinx.android.synthetic.main.activity_wm_view_recipe_list_.*
import kotlinx.android.synthetic.main.recycle_no_recipe_yet.view.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*
import java.util.*

class wm_view_recipe_list_Activity : AppCompatActivity() {
    val list = arrayListOf<Recipe>()
    val adapter = GroupAdapter<GroupieViewHolder>()
    var category_key : String = ""
    var cuisine_key : String = ""
    var found: Boolean = false
    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var recipeStr : String = ""
    var recipeImg: String = ""
    var recipeTitles: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_view_recipe_list_)
    }

    override fun onStart() {
        super.onStart()
        category_key = intent.getStringExtra(swm_Breakfast_Activity.rc_category)
        cuisine_key = intent.getStringExtra(swm_Breakfast_Activity.rc_cuisine)
        category_key = intent.getStringExtra(swm_Lunch_Activity.rc_category)
        cuisine_key = intent.getStringExtra(swm_Lunch_Activity.rc_cuisine)
        category_key = intent.getStringExtra(swm_Dinner_Activity.rc_category)
        cuisine_key = intent.getStringExtra(swm_Dinner_Activity.rc_cuisine)

        category_key = intent.getStringExtra(wmStaff_RecipeDetail_Activity.rc_category)
        cuisine_key = intent.getStringExtra(wmStaff_RecipeDetail_Activity.rc_cuisine)
        wm_search.isEnabled = false

        init()


        wm_return_btn.setOnClickListener {
            onBackPressed()
        }

        wm_search.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                wm_search.hideKeyboard()
                return@OnKeyListener true
            }
            false
        })

        wm_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(wm_search.text.isEmpty()){
                    wm_search.clearFocus()
                    wm_search.hideKeyboard()
                }
                search(wm_search.text.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                search(wm_search.text.toString())
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(wm_search.text.toString())
            }
        })
    }

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        wm_search.setText("")
        list.clear()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun search(title : String){
        adapter.clear()
        list.forEach {
            if(it.recipeTitle.toLowerCase().contains(title.toLowerCase())){
                adapter.add(bindata(it))
            }
        }
        wm_recycle_recipeList.adapter = adapter
    }

    private fun init(){
        list.clear()
        adapter.clear()
        found = false
        val ref = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("category").equalTo(category_key)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    val rep = it.getValue(Recipe::class.java)
                    if(rep != null){
                        if(rep.cuisine.equals(cuisine_key)){
                            found = true
                            wm_search.isEnabled = true
                            adapter.add(bindata(rep))
                            list.add(rep)
                        }
                    }
                }
                if (found == false) {
                    adapter.add(bindNoRecipe("No recipe yet"))
                }

                if(found == true) {
                    adapter.setOnItemClickListener { item, view ->
                        val recDetail = item as bindata
                        val intent = Intent(view.context, wmStaff_RecipeDetail_Activity::class.java)
                        recipeStr = recDetail.recipe.recipeID
                        recipeImg = recDetail.recipe.recipeImage
                        recipeTitles = recDetail.recipe.recipeTitle
                        intent.putExtra(RECIPE_KEY, recDetail.recipe.recipeID)
                        adapter.clear()
                        list.clear()
                        wm_search.setText("")
                        startActivity(intent)
                    }
                }
                wm_recycle_recipeList.adapter = adapter
            }
        })
    }
    private class bindata(val recipe : Recipe): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.ryr_title.text = recipe.recipeTitle
            Picasso.get().load(recipe.recipeImage).into(viewHolder.itemView.ryr_image)
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_item
        }
    }

    class bindNoRecipe(val str: String): Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.txtNoRecipe.text = str
        }
        override fun getLayout(): Int {
            return R.layout.recycle_no_recipe_yet
        }
    }


}