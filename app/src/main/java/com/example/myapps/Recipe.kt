package com.example.myapps

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


 class Recipe {

  var recipeID: String = ""
  var recipeTitle: String = ""
  var recipeImage: String = ""
  var calories: Double = 0.00
  var totalFat: Double = 0.00
  var saturatedFat: Double = 0.00
  var fibre: Double = 0.00
  var protein: Double = 0.00
  var cholesterol: Double = 0.00
  var uploadDate: Date ?= null
  var category: String = ""
  var cuisine: String = ""
  var staffID: String = ""
  var urlRec: String = ""
  var averageRating: Float = 0.0F
 }



