package com.example.myapps

import android.R.attr.button
import android.app.AlertDialog
import android.os.Bundle
import android.view.View.OnLongClickListener
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.recycle_chat_time.view.*
import kotlinx.android.synthetic.main.recycle_row_chat_other.view.*
import kotlinx.android.synthetic.main.recycle_row_chat_owner.view.*
import kotlinx.android.synthetic.main.recycle_row_his_date.view.*
import kotlinx.android.synthetic.main.recycle_row_no_review.view.*
import java.text.SimpleDateFormat
import java.util.*


const val RCID3 = ""

class ChatActivity : AppCompatActivity() {

    val adapters = GroupAdapter<GroupieViewHolder>()
    var previousDate: Date? = null
    var found: Boolean = false
    var comment = Comment()
    val currentuserID = FirebaseAuth.getInstance().uid.toString()
    var rid : String = ""
    var url: String = ""
    var username: String = ""
    val msg = arrayListOf<Comment>()
    var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    override fun onStart() {
        super.onStart()
        rid = intent.extras?.getString(RCID3,"").toString()
        findUser()
        loadRecipe()
        loadmsg()
        popUpMsgAfterSend()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        back_btn_chat.setOnClickListener {
            onBackPressed()
        }

        btnSend.setOnClickListener {
            if(chatboxTxt.text.isNotEmpty()) {
                sendMessage()
                loadmsg()
            }
        }

    }

    private fun findUser(){
        val ref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("id").equalTo(currentuserID)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        username = user.username
                        url = user.profileImageUrl
                    }
                }
            }
        })
    }


    private fun loadmsg(){
        msg.clear()
        adapters.clear()
        previousDate = null
        val ref = FirebaseDatabase.getInstance().getReference("/RecipeComments").orderByChild("recID").equalTo(rid)
        ref.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    found = false
                    val m = it.getValue(Comment::class.java)
                    if (m != null){
                        msg.add(m)
                        if (msg.count() == snapshot.children.count()){
                            found = true
                            bindadapter()
                        }
                    }
                }
                ryr_chat.adapter = adapters
            }
        })
    }



    private fun popUpMsgAfterSend(){
        msg.clear()
        val ref = FirebaseDatabase.getInstance().getReference("/RecipeComments").orderByChild("recID").equalTo(rid)
        ref.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(error: DatabaseError) {
                loadmsg()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                loadmsg()
            }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                loadmsg()
                }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                loadmsg()
                if(found == false) {
                    bindNull()
                }
            }
        })
    }

    private fun bindadapter(){

        msg.sortBy { it.timestamp }
        msg.forEach {
            if(previousDate == null ){
                previousDate = it.dateSent
                adapters.add(bindDates(it))
            }
            else{
                val prev: String = formate.format(previousDate)
                val current: String = formate.format(it.dateSent)
                if(prev != current){
                    adapters.add(bindDates(it))
                }
                previousDate = it.dateSent
            }

            if(it.userID.equals(currentuserID)){
                adapters.add(ChatToItem(it))
            }else {
                adapters.add(ChatFromItem(it))
            }
        }

    }


    private fun bindNull(){
        adapters.clear()
        ryr_chat.adapter = adapters
    }


    private class bindDates(val com: Comment): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var formateD = SimpleDateFormat("dd MMM yyyy", Locale.US)
            viewHolder.itemView.chatDateTxt.text = formateD.format(com.dateSent).toString()
        }

        override fun getLayout(): Int {
            return R.layout.recycle_chat_time
        }

    }

    private fun sendMessage(){
        var currentDate = Calendar.getInstance().time
        val commentID = UUID.randomUUID().toString()
        var timestamp = System.currentTimeMillis()/1000
        val ref = FirebaseDatabase.getInstance().getReference("/RecipeComments/$commentID")
        comment.commentID = commentID
        comment.recID = rid
        comment.userID = currentuserID
        comment.commentText = chatboxTxt.text.toString()
        comment.timestamp = timestamp
        comment.userimageUrl = url
        comment.username = username
        comment.dateSent = currentDate
        ref.setValue(comment)
        chatboxTxt.setText("")
    }


    private fun loadRecipe(){
        val ref = FirebaseDatabase.getInstance().getReference("/Recipe").orderByChild("recipeID").equalTo(rid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val rc = it.getValue(Recipe::class.java)
                    if (rc != null) {
                        recipeTitleChat.text = rc.recipeTitle 
                    }
                }
            }
        })
    }


    private class ChatFromItem(val comments: Comment): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var formateD = SimpleDateFormat("hh:mm a", Locale.US)
            viewHolder.itemView.senderNameTxt.text = comments.username
            viewHolder.itemView.chat_message_other.text = comments.commentText
            viewHolder.itemView.timeTxt.text = formateD.format(comments.dateSent).toString()
            Picasso.get().load(comments.userimageUrl).into(viewHolder.itemView.chat_image_other)
        }
        override fun getLayout(): Int {
            return R.layout.recycle_row_chat_other
        }

    }

    private class ChatToItem(val comments: Comment): Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var formateD = SimpleDateFormat("hh:mm a", Locale.US)
            viewHolder.itemView.chat_message_owner.text = comments.commentText
            viewHolder.itemView.timeText.text = formateD.format(comments.dateSent).toString()
            Picasso.get().load(comments.userimageUrl).into(viewHolder.itemView.chat_image_owner)

            viewHolder.itemView.chat_owner_root.setOnLongClickListener {

                val builder = AlertDialog.Builder(it.context, R.style.AlertTheme)
                builder.setTitle("Delete message")
                builder.setIcon(R.drawable.ic_action_warning)
                builder.setMessage("Do you want to delete message?")
                builder.setPositiveButton("Yes"){ dialog, which ->
                    val ref = FirebaseDatabase.getInstance().getReference("/RecipeComments/${comments.commentID}")
                    ref.removeValue()
                }
                builder.setNegativeButton("No",null)

                val alertDialog = builder.create()
                alertDialog.show()

                true
            }

        }

        override fun getLayout(): Int {
            return R.layout.recycle_row_chat_owner
        }
    }



}


