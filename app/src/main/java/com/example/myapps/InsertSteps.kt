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
        steps.recipeID = "0a3197b4-8fe0-4ce8-9590-37e63ec35d51"
        ref3.setValue(steps)
    }
}

