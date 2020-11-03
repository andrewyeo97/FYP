package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.myapps.fragments.FavouriteFragment
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
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_rating_review.*
import kotlinx.android.synthetic.main.activity_recipe_recycle_view_list.*
import kotlinx.android.synthetic.main.fragment_favourite.*
import kotlinx.android.synthetic.main.recycle_no_favourite_yet.view.*
import kotlinx.android.synthetic.main.recycle_no_history.view.*
import kotlinx.android.synthetic.main.recycle_row_chat_other.view.*
import kotlinx.android.synthetic.main.recycle_row_history.view.*

class HistoryActivity : AppCompatActivity() {
    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var found: Boolean = false
    val listHistory = arrayListOf<History>()
    val adapters = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        back_btn_history.setOnClickListener {
            onBackPressed()
        }


    }

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    override fun onStart() {
        super.onStart()
        loadHistory()
        clearSearch.setOnClickListener {
            if (found == true) {
                clearHistory()
            }
        }
    }

    private fun clearHistory(){
        adapters.clear()
        listHistory.clear()
            val ref = FirebaseDatabase.getInstance().getReference("/History").orderByChild("userID").equalTo(currentuserID)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val his = it.getValue(History::class.java)
                    if (his != null) {
                        it.ref.removeValue()
                        found = false
                        adapters.add(bindNoHistory("No history yet"))
                        adapters.setOnItemClickListener { item, view ->
                            clearSearch.isEnabled = false
                            return@setOnItemClickListener
                        }
                    }
                }
                ryr_history.adapter = adapters
            }
        })
    }


    private fun loadHistory(){
            listHistory.clear()
            found = false
            val ref = FirebaseDatabase.getInstance().getReference("/History").orderByChild("userID")
                .equalTo(currentuserID)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val his = it.getValue(History::class.java)
                        if (his != null) {
                            listHistory.add(his)
                            if (listHistory.count() == snapshot.children.count()) {
                                found = true
                                bindadapter()
                            }
                        }
                    }
                    if (found == false) {
                        adapters.clear()
                        adapters.add(bindNoHistory("No history yet"))
                        adapters.setOnItemClickListener { item, view ->
                            clearSearch.isEnabled = false
                            return@setOnItemClickListener
                        }
                    }


                    ryr_history.adapter = adapters
                }
            })

        }


    class bindNoHistory(val str: String): Item<GroupieViewHolder>() {

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.txtNoHis.text = str
        }

        override fun getLayout(): Int {
            return R.layout.recycle_no_history
        }
    }

    private fun bindadapter(){

        listHistory.sortByDescending{ it.historyDate }
        listHistory.forEach {
            adapters.add(bindHistory(it))
        }
        adapters.setOnItemClickListener { item, view ->
            val history = item as bindHistory
            val intent = Intent(view.context, RecipeDetailActivity::class.java)
            val hisID = history.history.historyID
            var timestamp = System.currentTimeMillis()/1000

            val ref = FirebaseDatabase.getInstance().getReference("History/$hisID")
            ref.child("historyDate").setValue(timestamp)
            intent.putExtra(RECIPE_KEY, history.history.recipeID)
                adapters.clear()
                listHistory.clear()
                startActivity(intent)
        }

    }

    private class bindHistory(val history: History): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.recipeTitleHistory.text = history.recipeTitle
            Picasso.get().load(history.recipeURL).into(viewHolder.itemView.recipeImageHistory)
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_history
        }

    }


}