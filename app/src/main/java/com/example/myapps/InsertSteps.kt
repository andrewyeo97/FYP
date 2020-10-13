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
        steps.recipeID = "a20820fd-4bb8-4d7e-aef0-7c5a32434821"
        ref3.setValue(steps)
    }
}

