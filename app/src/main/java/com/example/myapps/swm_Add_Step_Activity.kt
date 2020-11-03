package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapps.Ingredients
import com.example.myapps.R
import com.example.myapps.Steps
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_swm__add__ing_.*
import kotlinx.android.synthetic.main.activity_swm__add__step_.*
import kotlinx.android.synthetic.main.wm_recycle_row_ing.view.*
import kotlinx.android.synthetic.main.wm_recycle_row_step.view.*
import java.lang.Double
import java.util.*

class swm_Add_Step_Activity : AppCompatActivity() {

    var step = Steps()

    override fun onCreate(savedInstanceState: Bundle?) {

        val recipeTitle = intent.getStringExtra("Ing_RecipeTitle")
        val recipeId = intent.getStringExtra("Ing_RecipeId")
//
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swm__add__step_)

        recipe_title_step.text = recipeTitle
////
        re_step_btn.setOnClickListener {
            initStep()
        }

        button_add_step.setOnClickListener {
            addStep()
        }
        finish_step.setOnClickListener {
            val intent = Intent(this, swmDashboardActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        initStep()
    }

    private fun addStep(){
        var numeric = true
        val stepID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/wmStep/$stepID")
        val recipeId = intent.getStringExtra("Ing_RecipeId")

        if(step_no.text.toString().isEmpty()){
            step_no.error = "Please enter step number"
            step_no.requestFocus()
            return
        }
        if(step_desc.text.toString().isEmpty()){
            step_desc.error = "Please enter description"
            step_desc.requestFocus()
            return
        }
        try{
            val num = Double.parseDouble(step_desc.text.toString())
        }catch (e: NumberFormatException){
            numeric = false
        }
        if(numeric){
            step_desc.error = "Description cannot be numeric"
            step_desc.requestFocus()
            return
        }

        step.stepID = stepID
        step.stepNo = step_no.text.toString().toInt()
        step.desc = step_desc.text.toString()
        step.recipeID = recipeId
        ref.setValue(step)
        Toast.makeText(this, "Add successful!", Toast.LENGTH_SHORT).show()
    }

    private fun initStep(){
        val recipeId = intent.getStringExtra("Ing_RecipeId")
        val ref =  FirebaseDatabase.getInstance().getReference("/wmStep").orderByChild("recipeID").equalTo(recipeId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    val stp = it.getValue(Steps::class.java)
                    if(stp !=null){
                        adapter.add(bindataStep(stp))
                    }
                }
                recycle_step_dis.adapter = adapter
            }
        })
    }

}
class bindataStep(val step : Steps): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.step_number.text = step.stepNo.toString()
        viewHolder.itemView.step_description.text = step.desc
    }

    override fun getLayout(): Int {
        return R.layout.wm_recycle_row_step
    }
}