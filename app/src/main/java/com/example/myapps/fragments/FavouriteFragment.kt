package com.example.myapps.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import kotlinx.android.synthetic.main.recycle_row_item.view.*
import kotlinx.android.synthetic.main.recycle_row_item2.view.*

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : Fragment() {
    var rc_idd = ""
    val currentuser = FirebaseAuth.getInstance().currentUser!!.uid
    val listz = arrayListOf<Recipe>()
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onStart() {
        super.onStart()
        init()

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
            val recDetail = item as bindata
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
        val ref = FirebaseDatabase.getInstance().getReference("/Favourite").orderByChild("userID")
            .equalTo(currentuser)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
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
                                        adapter.add(bindata(res))
                                        listz.add(res)
                                    }
                                }
                                adapter.setOnItemClickListener { item, view ->
                                    val recDetail = item as bindata
                                    val intent = Intent(view.context, RecipeDetailActivity::class.java)
                                    intent.putExtra(RECIPE_KEY, recDetail.recipe.recipeID)
                                    adapter.clear()
                                    listz.clear()
                                    searchFavouriteView.setText("")
                                    startActivity(intent)
                                }
                                ryr_favourite.adapter = adapter
                            }

                        })

                    }

                }

            }
        })

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



}
