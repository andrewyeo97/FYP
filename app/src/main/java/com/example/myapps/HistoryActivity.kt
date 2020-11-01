package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.activity_rating_review.*
import kotlinx.android.synthetic.main.recycle_row_chat_other.view.*
import kotlinx.android.synthetic.main.recycle_row_history.view.*

class HistoryActivity : AppCompatActivity() {
    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var found: Boolean = false
    val listHistory = arrayListOf<History>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        back_btn_history.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        loadHistory()
    }

    private fun loadHistory(){
        listHistory.clear()
        found = false
        val ref = FirebaseDatabase.getInstance().getReference("/History").orderByChild("userID").equalTo(currentuserID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val his = it.getValue(History::class.java)
                    if (his != null) {
                        listHistory.add(his)
                        if (listHistory.count() == snapshot.children.count()){
                            found = true
                            bindadapter()
                        }
                    }
                }
            }
        })
    }

    private fun bindadapter(){
        val adapters = GroupAdapter<GroupieViewHolder>()
        listHistory.sortByDescending{ it.historyDate }
        listHistory.forEach {
            adapters.add(bindHistory(it))
        }
        ryr_history.adapter = adapters
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