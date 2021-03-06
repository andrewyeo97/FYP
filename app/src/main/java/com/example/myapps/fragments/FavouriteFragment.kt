package com.example.myapps.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.myapps.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.recycle_no_favourite_yet.view.*
import kotlinx.android.synthetic.main.recycle_no_recipe_yet.view.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*
import kotlinx.android.synthetic.main.recycle_row_item2.view.*
import kotlinx.android.synthetic.main.recycle_row_no_review.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : Fragment() {
    var rc_idd = ""
    val currentuser = FirebaseAuth.getInstance().currentUser!!.uid
    val listz = arrayListOf<Recipe>()
    val filterz = arrayListOf<Recipe>()
    val adapter = GroupAdapter<GroupieViewHolder>()
    val listHistory = arrayListOf<History>()
    var historyExist: Boolean = false
    var currentDate = Calendar.getInstance().time
    var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    var current: String = ""
    var recipeStr : String = ""
    var recipeImg: String = ""
    var recipeTitles: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onStart() {
        super.onStart()
        searchFavouriteView.isEnabled = false
        spinnerCategory2.isEnabled = false
        init()
        spinnerInit()

        spinnerCategory2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filterRecipe()
            }
        }

        searchFavouriteView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                hideKeyboard()
                return@OnKeyListener true
            }
            false
        })

        searchFavouriteView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(searchFavouriteView.text.isEmpty()){
                    searchFavouriteView.clearFocus()
                    hideKeyboard()
                }
                search(searchFavouriteView.text.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                search(searchFavouriteView.text.toString())
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(searchFavouriteView.text.toString())
            }
        })
    }

    private fun search(title : String){
        adapter.clear()
        listz.forEach {
            if(it.recipeTitle.toLowerCase().contains(title.toLowerCase())){
                adapter.add(bindata(it))
            }
        }
        adapter.setOnItemClickListener { item, view ->
            recipeStr = ""
            recipeImg = ""
            recipeTitles = ""
            val recDetail = item as bindata
            recipeStr = recDetail.recipe.recipeID
            recipeImg = recDetail.recipe.recipeImage
            recipeTitles = recDetail.recipe.recipeTitle
            checkHistory(recipeStr)
            val intent = Intent(view.context, RecipeDetailActivity::class.java)
            intent.putExtra(RECIPE_KEY, recDetail.recipe.recipeID)
            adapter.clear()
            listz.clear()
            searchFavouriteView.setText("")
            startActivity(intent)
        }
        ryr_favourite.adapter = adapter
    }


    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    override fun onResume() {
        super.onResume()
        searchFavouriteView.setText("")
        listz.clear()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun init() {
        listz.clear()
        adapter.clear()
        filterz.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/Favourite").orderByChild("userID")
            .equalTo(currentuser)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val fav = it.getValue(Favourite::class.java)
                    if (fav != null) {
                        rc_idd = fav.recipeID
                        val ref2 = FirebaseDatabase.getInstance().getReference("/Recipe")
                            .orderByChild("recipeID").equalTo(rc_idd)
                        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {}
                            override fun onDataChange(snapshot: DataSnapshot) {

                                snapshot.children.forEach {
                                    val res = it.getValue(Recipe::class.java)
                                    if (res != null) {
                                        spinnerCategory2.isEnabled = true
                                        searchFavouriteView.isEnabled = true
                                        adapter.add(bindata(res))
                                        listz.add(res)
                                        filterz.add(res)
                                    }
                                }
                                adapter.setOnItemClickListener { item, view ->
                                    recipeStr = ""
                                    recipeImg = ""
                                    recipeTitles = ""
                                    val recDetail = item as bindata
                                    recipeStr = recDetail.recipe.recipeID
                                    recipeImg = recDetail.recipe.recipeImage
                                    recipeTitles = recDetail.recipe.recipeTitle
                                    checkHistory(recipeStr)
                                    val intent = Intent(view.context, RecipeDetailActivity::class.java)
                                    intent.putExtra(RECIPE_KEY, recDetail.recipe.recipeID)
                                    adapter.clear()
                                    listz.clear()
                                    filterz.clear()
                                    searchFavouriteView.setText("")
                                    startActivity(intent)
                                }

                            }

                        })

                    }


                }
                if(snapshot.children.count() < 1){
                    adapter.add(bindNoFav("No favourite yet"))
                    adapter.setOnItemClickListener { item, view ->
                        searchFavouriteView.isEnabled = false
                        return@setOnItemClickListener
                    }
                }
                ryr_favourite.adapter = adapter


            }

        })

    }

    class bindNoFav(val str: String): Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.txtNoFav.text = str

        }

        override fun getLayout(): Int {
            return R.layout.recycle_no_favourite_yet
        }
    }

    private class bindata(val recipe : Recipe): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.ryr_title2.text = recipe.recipeTitle
            Picasso.get().load(recipe.recipeImage).into(viewHolder.itemView.ryr_image2)
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_item2
        }


    }

    private fun spinnerInit(){
        val array = resources.getStringArray(R.array.categoryList)
        val adapter = ArrayAdapter(context!!.applicationContext,R.layout.support_simple_spinner_dropdown_item,array)
        spinnerCategory2.adapter = adapter
    }

    private fun filterRecipe(){
        val search = spinnerCategory2.selectedItem.toString()
        val adapterf = GroupAdapter<GroupieViewHolder>()

        filterz.forEach {
            if(!(search.equals("All"))){
                if(it.category.toUpperCase().contains(search.toUpperCase())){
                    searchFavouriteView.clearFocus()
                    hideKeyboard()
                    adapterf.add(bindata(it))
                }
            }else{
                searchFavouriteView.clearFocus()
                hideKeyboard()
                adapterf.add(bindata(it))
            }

        }
        adapterf.setOnItemClickListener { item, view ->
            recipeStr = ""
            recipeImg = ""
            recipeTitles = ""
            val recDetail = item as bindata
            val intent = Intent(view.context,RecipeDetailActivity::class.java)
            recipeStr = recDetail.recipe.recipeID
            recipeImg = recDetail.recipe.recipeImage
            recipeTitles = recDetail.recipe.recipeTitle
            checkHistory(recipeStr)
            intent.putExtra(RECIPE_KEY,recDetail.recipe.recipeID)
            adapter.clear()
            listz.clear()
            filterz.clear()
            searchFavouriteView.setText("")
            startActivity(intent)
        }
        ryr_favourite.adapter = adapterf
    }

    private fun checkHistory(str: String){
        historyExist = false
        listHistory.clear()
        current = formate.format(currentDate)
        var counter: Int = 0
        val ref = FirebaseDatabase.getInstance().getReference("/History").orderByChild("userID").equalTo(currentuser)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val his = it.getValue(History::class.java)
                    if (his != null){
                        if (his.recipeID.equals(str)){
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
        var timestamp = System.currentTimeMillis()/1000

        val ref = FirebaseDatabase.getInstance().getReference("History/$str")
        ref.child("timestamp").setValue(timestamp)
        ref.child("historyDate").setValue(currentDate)
    }

    private fun addHistory(){
        var history = History()
        val historyID = UUID.randomUUID().toString()
        var timestamp = System.currentTimeMillis()/1000
        val ref = FirebaseDatabase.getInstance().getReference("/History/$historyID")
        history.historyID = historyID
        history.timestamp = timestamp
        history.historyDate = currentDate
        history.userID = currentuser
        history.recipeID = recipeStr
        history.recipeTitle = recipeTitles
        history.recipeURL = recipeImg
        ref.setValue(history)
    }


}
