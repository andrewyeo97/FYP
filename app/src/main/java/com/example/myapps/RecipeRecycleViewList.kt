package com.example.myapps

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.activity_recipe_recycle_view_list.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.recycle_no_favourite_yet.view.*
import kotlinx.android.synthetic.main.recycle_no_recipe_yet.view.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*
import kotlinx.android.synthetic.main.recycle_row_no_review.view.*
import java.text.SimpleDateFormat
import java.util.*

class RecipeRecycleViewList : AppCompatActivity() {
    val list = arrayListOf<Recipe>()
    val adapter = GroupAdapter<GroupieViewHolder>()
    var category_key : String = ""
    var cuisine_key : String = ""
    var found: Boolean = false
    var historyExist: Boolean = false
    var history = History()
    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var recipeStr : String = ""
    var currentHistoryID : String = ""
    var recipeImg: String = ""
    var recipeTitles: String = ""
    var currentDate = Calendar.getInstance().time
    var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    var prev: String = ""
    var current: String = ""
    val listHistory = arrayListOf<History>()

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
        prev = ""
        current = ""
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
                        recipeStr = recDetail.recipe.recipeID
                        recipeImg = recDetail.recipe.recipeImage
                        recipeTitles = recDetail.recipe.recipeTitle
                        intent.putExtra(RECIPE_KEY, recDetail.recipe.recipeID)
                        checkHistory()
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

    private fun addHistory(){
        val historyID = UUID.randomUUID().toString()
        var timestamp = System.currentTimeMillis()/1000
        val ref = FirebaseDatabase.getInstance().getReference("/History/$historyID")
        history.historyID = historyID
        history.timestamp = timestamp
        history.historyDate = currentDate
        history.userID = currentuserID
        history.recipeID = recipeStr
        history.recipeTitle = recipeTitles
        history.recipeURL = recipeImg
        ref.setValue(history)
    }

    private fun checkHistory(){
        historyExist = false
        listHistory.clear()
        current = formate.format(currentDate)
        var counter: Int = 0
        val ref = FirebaseDatabase.getInstance().getReference("/History").orderByChild("userID").equalTo(currentuserID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val his = it.getValue(History::class.java)
                    if (his != null){
                        if (his.recipeID.equals(recipeStr)){
                            historyExist = true
                            var his2 = it.getValue(History::class.java)
                            his2 = his
                            counter = counter + 1
//                            currentHistoryID = his.historyID
//                            prev = formate.format(his.historyDate)
                            listHistory.add(his2)
                        }
                    }
                }
                if (listHistory.count() == counter) {
                    bindadapter()
                }
//                if (listHistory.count() == snapshot.children.count()) {
//                    if(prev == current){
//                        updateHistory()
//                    }
//                    else if(prev != current){
//                        addHistory()
//                    }
//
//                }
                if(historyExist == false){
                    addHistory()
                }
            }
        })
    }

    private fun bindadapter(){
        listHistory.sortByDescending{ it.timestamp }
        listHistory.forEach {
                val prev: String = formate.format(it.historyDate)
                val current: String = formate.format(currentDate)

                if(prev != current){
                    addHistory()
                }
                if(prev == current){
                    updateHistory(it.historyID)
                }
            return
        }
    }

    private fun updateHistory( str:String){
        val historyID = currentHistoryID
        var timestamp = System.currentTimeMillis()/1000

        val ref = FirebaseDatabase.getInstance().getReference("History/$str")
        ref.child("timestamp").setValue(timestamp)
        ref.child("historyDate").setValue(currentDate)
    }

}