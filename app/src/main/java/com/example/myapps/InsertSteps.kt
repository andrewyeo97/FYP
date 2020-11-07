package com.example.myapps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_insert_steps.*
import java.util.*

class InsertSteps : AppCompatActivity() {

    var steps = Steps()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_steps)

        btnAddStep.setOnClickListener {
            addStep()
        }
    }

    private fun addStep(){
        val stepID = UUID.randomUUID().toString()
        val ref3 = FirebaseDatabase.getInstance().getReference("/Steps/$stepID")
        steps.stepID = stepID
        steps.stepNo = stepNoAns.text.toString().toInt()
        steps.desc = desc.text.toString()
        steps.recipeID = "080c1475-aabe-43d2-a315-6fcbcae8b4c4"
        ref3.setValue(steps)
    }

    override fun onStart() {
        super.onStart()
        clr111.setOnClickListener {
            desc.setText("")
            desc.requestFocus()
        }

        btnbtn.setOnClickListener{
            stepNoAns.setText("")
            stepNoAns.requestFocus()
        }
    }
}

