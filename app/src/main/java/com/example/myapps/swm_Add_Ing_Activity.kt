package com.example.myapps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.myapps.Ingredients
import com.example.myapps.R
import com.example.myapps.Recipe
import com.example.myapps.fragments.FavouriteFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_swm__add__ing_.*
import kotlinx.android.synthetic.main.activity_wm_add_new__recipe_.*
import kotlinx.android.synthetic.main.recycle_row_item2.view.*
import kotlinx.android.synthetic.main.wm_recycle_row_ing.view.*
import java.lang.Double
import java.lang.reflect.Field
import java.util.*

class swm_Add_Ing_Activity : AppCompatActivity() {

    var unitList: MutableList<String> = ArrayList()
    var unitSelected: String = ""

    var ingredient = Ingredients()
    override fun onCreate(savedInstanceState: Bundle?) {

        val recipeTitle = intent.getStringExtra("RecipeTitle")
        val recipeId = intent.getStringExtra("RecipeId")

        val rcpT: String  = recipeTitle.toString()
        val rcpId: String = recipeId.toString()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swm__add__ing_)

        recipeName.text = recipeTitle

        button_add_ing.setOnClickListener {
            addIng()
        }

        btn_re.setOnClickListener {
            init()
            ingQty.setText("")
//            ingUnit.setText("")
            ingName.setText("")
        }

        next_ing.setOnClickListener {
            val intent = Intent(this, swm_Add_Step_Activity::class.java)
            intent.putExtra("Ing_RecipeTitle", rcpT)
            intent.putExtra("Ing_RecipeId", rcpId)
            startActivity(intent)

            //intent.putExtra("RecipeId", recipeId)
        }
        addDropdown()
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun addDropdown(){
        val tsp: String = "tsp"
        val tbsp: String  = "tbsp"
        val pc: String  = "pc"
        val slc: String = "slc"
        val ml: String = "ml"
        val g:String  = "g"
        val cup:String = "cup"

        unitList.add("")
        unitList.add(tsp)
        unitList.add(tbsp)
        unitList.add(pc)
        unitList.add(slc)
        unitList.add(ml)
        unitList.add(g)
        unitList.add(cup)

        val adapter: ArrayAdapter<String> = ArrayAdapter(this,
            R.layout.support_simple_spinner_dropdown_item, unitList)
        add_unit_dropdown.adapter = adapter

        add_unit_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val slcItem : String = unitList[position]
                unitSelected = slcItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        UnitHight(add_unit_dropdown)
    }

    private fun UnitHight( add_unit_dropdown: Spinner){
        val popup: Field = Spinner::class.java.getDeclaredField("mPopup")
        popup.isAccessible = true

        val popupWindow: ListPopupWindow = popup.get(add_unit_dropdown) as ListPopupWindow
        popupWindow.height = (8 * resources.displayMetrics.density).toInt()
    }

    private fun addIng(){
        var numeric1 = true
        var numeric2 = true
        val ingID = UUID.randomUUID().toString()
        val ref = FirebaseDatabase.getInstance().getReference("/Ingredient/$ingID")
        val recipeId = intent.getStringExtra("RecipeId")

        if(ingName.text.toString().isEmpty()){
            ingName.error = "Please enter ingredients name"
            ingName.requestFocus()
            return
        }
        try{
            val num = Double.parseDouble(ingName.text.toString())
        }catch (e: NumberFormatException){
            numeric1 = false
        }
        if(numeric1){
            ingName.error = "Ingredients name cannot be numeric"
            ingName.requestFocus()
            return
        }

        if(ingQty.text.toString().isEmpty()){
            ingQty.error = "Please enter ingredients quantity"
            ingQty.requestFocus()
            return
        }

        if(unitSelected.toString().equals("")){
            Toast.makeText(this, "Please select ingredient unit...", Toast.LENGTH_SHORT).show()
            return
        }

        ingredient.unit = unitSelected.toString()
        ingredient.ingredientID = ingID
        ingredient.ingredientName = ingName.text.toString()
        ingredient.quantity = ingQty.text.toString().toDouble()
        ingredient.recipeID = recipeId
        ref.setValue(ingredient)
        Toast.makeText(baseContext, "Added New Ingredient Successful", Toast.LENGTH_SHORT).show()

        ingName.setText("")
        ingQty.setText("")
    }

    private fun init(){
        val recipeId = intent.getStringExtra("RecipeId")
        val ref =  FirebaseDatabase.getInstance().getReference("/Ingredient").orderByChild("recipeID").equalTo(recipeId)

        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    val ing = it.getValue(Ingredients::class.java)
                    if(ing !=null){
                        adapter.add(bindata(ing))
                    }
                }
                recycle_ing_dis.adapter = adapter
            }
        })
    }

}
class bindata(val ing : Ingredients): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.added_ing_qty.text = ing.quantity.toString()
        viewHolder.itemView.added_ing_unit.text = ing.unit
        viewHolder.itemView.added_ing_name.text = ing.ingredientName
    }
    override fun getLayout(): Int {
        return R.layout.wm_recycle_row_ing
    }
}
