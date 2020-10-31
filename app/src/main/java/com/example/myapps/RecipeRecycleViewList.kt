package com.example.myapps

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.myapps.fragments.FavouriteFragment
import com.example.myapps.fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_add_rating.*
import kotlinx.android.synthetic.main.activity_recipe_recycle_view_list.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.recycle_no_favourite_yet.view.*
import kotlinx.android.synthetic.main.recycle_no_recipe_yet.view.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*
import kotlinx.android.synthetic.main.recycle_row_no_review.view.*

class RecipeRecycleViewList : AppCompatActivity() {
    val list = arrayListOf<Recipe>()
    val adapter = GroupAdapter<GroupieViewHolder>()
    var category_key : String = ""
    var cuisine_key : String = ""
    var found: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_recycle_view_list)
    }

    override fun onStart() {
        super.onStart()
        category_key = intent.getStringExtra(BreakfastActivity.rc_category)
        cuisine_key = intent.getStringExtra(BreakfastActivity.rc_cuisine)
        category_key = intent.getStringExtra(LunchActivity.rc_category)
        cuisine_key = intent.getStringExtra(LunchActivity.rc_cuisine)
        category_key = intent.getStringExtra(DinnerActivity.rc_category)
        cuisine_key = intent.getStringExtra(DinnerActivity.rc_cuisine)
        searchRecipeView.isEnabled = false

        init()


        btn_return.setOnClickListener {
            onBackPressed()
        }

        searchRecipeView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                searchRecipeView.hideKeyboard()
                return@OnKeyListener true
            }
            false
        })

        searchRecipeView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(searchRecipeView.text.isEmpty()){
                    searchRecipeView.clearFocus()
                    searchRecipeView.hideKeyboard()
                }
                search(searchRecipeView.text.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                search(searchRecipeView.text.toString())
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(searchRecipeView.text.toString())
            }
        })
    }


    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    override fun onResume() {
        super.onResume()
        searchRecipeView.setText("")
        list.clear()
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
//    private fun Activity.hideKeyboard() {
//        hideKeyboard(currentFocus ?: View(this))
//    }
//
//    private fun Context.hideKeyboard(view: View) {
//        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//    }

    private fun search(title : String){
        adapter.clear()
        list.forEach {
            if(it.recipeTitle.toLowerCase().contains(title.toLowerCase())){
                adapter.add(bindata(it))
            }
        }
        ryr_view.adapter = adapter
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
                            searchRecipeView.isEnabled = true
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
                        val intent = Intent(view.context, RecipeDetailActivity::class.java)
                        intent.putExtra(RECIPE_KEY, recDetail.recipe.recipeID)
                        adapter.clear()
                        list.clear()
                        searchRecipeView.setText("")
                        startActivity(intent)
                    }
                }
                ryr_view.adapter = adapter
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