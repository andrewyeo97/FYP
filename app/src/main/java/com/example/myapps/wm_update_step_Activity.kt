package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myapps.R
import com.example.myapps.Steps
import com.example.myapps.fragments.wmHomeFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_swm__add__step_.*
import kotlinx.android.synthetic.main.activity_wm_update_step_.*
import kotlinx.android.synthetic.main.wm_recycle_update_step.view.*
import java.lang.Double
import java.util.*

class wm_update_step_Activity : AppCompatActivity() {

    var step = Steps()
    var rcpTitle : String = ""
    var rcpId : String = ""
    var stepId : String  = ""
    var currentDate = Calendar.getInstance().time


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wm_update_step_)

        rcpTitle = intent.getStringExtra("updateRcpTitle")

        update_title_step.setText(rcpTitle)

        button_updatepage_update_step.setOnClickListener {
            val rcpId = intent.getStringExtra("updateRcpId")
            val ref = FirebaseDatabase.getInstance().getReference("Recipe/$rcpId")
            updateStep()
            ref.child("updateDate").setValue(currentDate)
        }

        updatepage_re_step_btn.setOnClickListener {
            init()
        }
        button_updatepage_add_step.setOnClickListener {
            val rcpId = intent.getStringExtra("updateRcpId")
            val ref = FirebaseDatabase.getInstance().getReference("Recipe/$rcpId")
            addStep()
            ref.child("updateDate").setValue(currentDate)
        }
        button_updatepage_delete_step.setOnClickListener {
            val rcpId = intent.getStringExtra("updateRcpId")
            val ref = FirebaseDatabase.getInstance().getReference("Recipe/$rcpId")
            val builder = AlertDialog.Builder(this@wm_update_step_Activity)
            builder.setTitle("Delete Step Message")
            builder.setMessage("Are you want to delete this Ingredients?")
            builder.setPositiveButton("Yes"){ dialog, which ->
                Toast.makeText(applicationContext, "Ok, we will delete this step.", Toast.LENGTH_SHORT).show()
                deleteStp()
                update_step_no.setText("")
                update_step_desc.setText("")
                ref.child("updateDate").setValue(currentDate)
            }
            builder.setNegativeButton("No"){ dialog, which ->
                Toast.makeText(
                    applicationContext,
                    "Ok, we will not delete this step.",
                    Toast.LENGTH_SHORT
                ).show()
                update_step_no.setText("")
                update_step_desc.setText("")
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        update_finish_step.setOnClickListener {
//            rcpId = intent.getStringExtra("updateRcpId")
            val intent = Intent(this, swmDashboardActivity::class.java)
//            intent.putExtra(RECIPE_KEY, rcpId)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    companion object{
        val RECIPE_KEY = "RECIPE_KEY"
    }

    private fun deleteStp(){
        val ref = FirebaseDatabase.getInstance().getReference("/Steps").child(stepId)
        ref.removeValue()
    }

    private fun addStep(){
        var numeric = true
        val stepID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Steps/$stepID")
        rcpId = intent.getStringExtra("updateRcpId")

        if(update_step_no.text.toString().isEmpty()){
            update_step_no.error = "Step number cannot be empty..."
            update_step_no.requestFocus()
            return
        }
        if(update_step_desc.text.toString().isEmpty()){
            update_step_desc.error = "Description cannot be empty..."
            update_step_desc.requestFocus()
            return
        }
        try{
            val num = Double.parseDouble(update_step_desc.text.toString())
        }catch (e: NumberFormatException){
            numeric = false
        }
        if(numeric){
            update_step_desc.error = "Description cannot be numeric"
            update_step_desc.requestFocus()
            return
        }



        step.stepID = stepID
        step.stepNo = update_step_no.text.toString().toInt()
        step.desc = update_step_desc.text.toString()
        step.recipeID = rcpId
        ref.setValue(step)
        Toast.makeText(this, "Add successful!", Toast.LENGTH_SHORT).show()
        update_step_no.setText("")
        update_step_desc.setText("")
    }

    private fun updateStep(){
        var numeric2  = true

        val ref = FirebaseDatabase.getInstance().getReference("Steps/$stepId")

        if(update_step_no.text.isNotEmpty()){
            val no = update_step_no.text.toString().toInt()
            ref.child("stepNo").setValue(no)
        }else{
            update_step_no.error = "No. of step cannot be empty..."
            update_step_no.requestFocus()
            return
        }
        if(update_step_desc.text.isNotEmpty()){
            try{
                val num = Double.parseDouble(update_step_desc.text.toString())
            }catch (e: NumberFormatException){
                numeric2 = false
            }
            if(numeric2){
                update_step_desc.error = "Description cannot be numeric"
                update_step_desc.requestFocus()
                return
            }else{
                val desc = update_step_desc.text.toString()
                ref.child("desc").setValue(desc)
            }
        }else{
            update_step_desc.error = "Description cannot be empty..."
            update_step_desc.requestFocus()
            return
        }
        Toast.makeText(baseContext, "Step Update Successful", Toast.LENGTH_SHORT).show()
        update_step_no.setText("")
        update_step_desc.setText("")

    }


    private fun init(){
        val rcpId = intent.getStringExtra("updateRcpId")
        val ref =  FirebaseDatabase.getInstance().getReference("/Steps").orderByChild("recipeID").equalTo(rcpId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            val list = arrayListOf<Steps>()
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    val stp = it.getValue(Steps::class.java)
                    if(stp !=null){
                        list.add(stp)
                        if (list.count() == snapshot.children.count()) {
                            list.sortBy { it.stepNo }
                            list.forEach {
                                adapter.add(stpData(it))
                            }
                        }

                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val stpValue = item as stpData
                    stepId = stpValue.step.stepID
                    update_step_no.setText(stpValue.step.stepNo.toString())
                    update_step_desc.setText(stpValue.step.desc)
                }
                recycle_update_step_dis.adapter = adapter
            }
        })
    }
}

class stpData(val step : Steps): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.update_step_number.text = step.stepNo.toString()
        viewHolder.itemView.update_step_description.text = step.desc
    }
    override fun getLayout(): Int {
        return R.layout.wm_recycle_update_step
    }
}