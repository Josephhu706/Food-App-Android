package com.example.foody.models


import com.example.foody.models.ExtendedIngredient
import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("aggregateLikes")
    val aggregateLikes: Int,
    @SerializedName("cheap")
    val cheap: Boolean,
    @SerializedName("dairyFree")
    val dairyFree: Boolean,
    @SerializedName("diets")
    val diets: List<String>,
    @SerializedName("extendedIngredients")
    val extendedIngredients: List<ExtendedIngredient>,
    @SerializedName("glutenFree")
    val glutenFree: Boolean,
    @SerializedName("image")
    val image: String,
    @SerializedName("readyInMinutes")
    val readyInMinutes: Int,
    @SerializedName("sourceName")
    val sourceName: String,
    @SerializedName("sourceUrl")
    val sourceUrl: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("vegan")
    val vegan: Boolean,
    @SerializedName("vegetarian")
    val vegetarian: Boolean,
    @SerializedName("veryHealthy")
    val veryHealthy: Boolean,
)