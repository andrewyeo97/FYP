package com.example.myapps.fragments

//import com.example.myapps.MainAdapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.myapps.Favourite
import com.example.myapps.R
import com.example.myapps.Recipe
import com.example.myapps.RecipeDetailActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.recycle_row_item.view.*


/**
 * A simple [Fragment] subclass.
 */


class HomeFragment : Fragment() {
    val filter = arrayListOf<Recipe>()
    val list = arrayListOf<Recipe>()
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onStart() {
        super.onStart()
        init()
        spinnerInit()

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        searchRecipeView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(searchRecipeView.text.isEmpty()){
                    searchRecipeView.clearFocus()
                    hideKeyboard()
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

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
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

    override fun onResume() {
        super.onResume()
        searchRecipeView.setText("")
        list.clear()
        filter.clear()
    }


    private fun init(){
        list.clear()
        adapter.clear()
        filter.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/Recipe")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach {
                    val rep = it.getValue(Recipe::class.java)
                    if(rep != null){
                        adapter.add(bindata(rep))
                        list.add(rep)
                        filter.add(rep)
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val recDetail = item as bindata
                    val intent = Intent(view.context,RecipeDetailActivity::class.java)
                    intent.putExtra(RECIPE_KEY,recDetail.recipe.recipeID)
                    adapter.clear()
                    list.clear()
                    filter.clear()
                    searchRecipeView.setText("")
                    startActivity(intent)
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    private fun spinnerInit(){
        val array = resources.getStringArray(R.array.categoryList)
        val adapter = ArrayAdapter(context!!.applicationContext,R.layout.support_simple_spinner_dropdown_item,array)
        spinnerCategory.adapter = adapter
    }

    private fun filterRecipe(){
        val search = spinnerCategory.selectedItem.toString()
        val adapterf = GroupAdapter<GroupieViewHolder>()

        filter.forEach {
            if(!(search.equals("All"))){
                if(it.category.toUpperCase().contains(search.toUpperCase())){
                    adapterf.add(bindata(it))
                }
            }else{
                adapterf.add(bindata(it))
            }

        }
        adapterf.setOnItemClickListener { item, view ->
            val recDetail = item as bindata
            val intent = Intent(view.context,RecipeDetailActivity::class.java)
            intent.putExtra(RECIPE_KEY,recDetail.recipe.recipeID)
            adapter.clear()
            list.clear()
            filter.clear()
            searchRecipeView.setText("")
            startActivity(intent)
        }
        ryr_view.adapter = adapterf
    }
}



