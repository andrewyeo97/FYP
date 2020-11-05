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
        steps.recipeID = "b2ecf99b-626e-4d9e-8d05-8b6858578ae3"
        ref3.setValue(steps)
    }

    override fun onStart() {
        super.onStart()
        clr111.setOnClickListener {
            desc.setText("")
            stepNoAns.requestFocus()
        }

        btnbtn.setOnClickListener{
            stepNoAns.setText("")
            stepNoAns.requestFocus()
        }
    }
}

