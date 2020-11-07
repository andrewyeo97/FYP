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
import kotlinx.android.synthetic.main.recycle_row_category.view.*
import kotlinx.android.synthetic.main.recycle_row_chat_other.view.*
import kotlinx.android.synthetic.main.recycle_row_his_date.view.*
import kotlinx.android.synthetic.main.recycle_row_history.view.*
import kotlinx.android.synthetic.main.recycle_row_ratings.view.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {
    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var found: Boolean = false
    val listHistory = arrayListOf<History>()
    val adapters = GroupAdapter<GroupieViewHolder>()
    var previousDate: Date? = null
    var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    var currentDate = Calendar.getInstance().time

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

                    }
                }
                if(found == false){
                    adapters.clear()
                    listHistory.clear()
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



    private fun loadHistory(){
        previousDate = null
        adapters.clear()
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
                        if (
                            listHistory.count() == snapshot.children.count()) {
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

        listHistory.sortByDescending{ it.timestamp }
        listHistory.forEach {
            if(previousDate == null ){
                previousDate = it.historyDate
                adapters.add(bindDate(it))
            }else{
                val prev: String = formate.format(previousDate)
                val current: String = formate.format(it.historyDate)

                if(prev != current){
                    adapters.add(bindDate(it))
                }
                previousDate = it.historyDate
            }
            adapters.add(bindHistory(it))

        }


    }

    private class bindHistory(val history: History): Item<GroupieViewHolder>() {
        var existHisID: String = ""
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.recipeTitleHistory.text = history.recipeTitle
            Picasso.get().load(history.recipeURL).into(viewHolder.itemView.recipeImageHistory)


            viewHolder.itemView.hisConst.setOnClickListener {
                var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                var currentDate = Calendar.getInstance().time
                val prev: String = formate.format(history.historyDate)
                val current: String = formate.format(currentDate)
                val hisID = history.historyID
                val recID: String = history.recipeID
                var isExist: Boolean = false
                existHisID = ""

                if(prev == current){
                var timestamp = System.currentTimeMillis() / 1000
                val ref = FirebaseDatabase.getInstance().getReference("History/$hisID")
                ref.child("timestamp").setValue(timestamp)
                ref.child("historyDate").setValue(currentDate)
                val intent = Intent(it.context, RecipeDetailActivity::class.java)
                intent.putExtra(RECIPE_KEY, history.recipeID)
                it.context.startActivity(intent)}

                else if (prev != current) {

                    val reff = FirebaseDatabase.getInstance().getReference("/History")
                        .orderByChild("recipeID").equalTo(recID)
                    reff.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {}
                        override fun onDataChange(snapshot: DataSnapshot) {
                            snapshot.children.forEach {
                                val his = it.getValue(History::class.java)
                                if (his != null) {
                                    val hisDate: String = formate.format(his.historyDate)
                                    if (hisDate == current) {
                                        isExist = true
                                        existHisID = his.historyID
                                        var timestamps = System.currentTimeMillis() / 1000
                                        val ref = FirebaseDatabase.getInstance().getReference("History/$existHisID")
                                        ref.child("timestamp").setValue(timestamps)
                                        ref.child("historyDate").setValue(currentDate)
                                    }
                                }
                            }
                            if(isExist == true){
                                val intent = Intent(it.context, RecipeDetailActivity::class.java)
                                intent.putExtra(RECIPE_KEY, history.recipeID)
                                it.context.startActivity(intent)
                            }
                            if (isExist == false) {
                                var his = History()
                                val historyID = UUID.randomUUID().toString()
                                var timestamp = System.currentTimeMillis() / 1000
                                val ref =
                                    FirebaseDatabase.getInstance().getReference("/History/$historyID")
                                his.historyID = historyID
                                his.timestamp = timestamp
                                his.historyDate = currentDate
                                his.userID = history.userID
                                his.recipeID = history.recipeID
                                his.recipeTitle = history.recipeTitle
                                his.recipeURL = history.recipeURL
                                ref.setValue(his)
                                val intent = Intent(it.context, RecipeDetailActivity::class.java)
                                intent.putExtra(RECIPE_KEY, history.recipeID)
                                it.context.startActivity(intent)
                            }
                        }
                    })


                }
            }
            }



        override fun getLayout(): Int {
            return R.layout.recycle_row_history
        }

    }


    private class bindDate(val his: History): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var formate = SimpleDateFormat("dd MMMM yyyy", Locale.US)
            viewHolder.itemView.textDates.text = formate.format(his.historyDate).toString()
        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_his_date
        }

    }


}