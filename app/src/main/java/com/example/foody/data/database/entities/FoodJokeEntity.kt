package com.example.foody.data.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.foody.models.FoodJoke
import com.example.foody.util.Constants.Companion.FOOD_JOKE_TABLE

@Entity(tableName = FOOD_JOKE_TABLE)
class FoodJokeEntity(
    //inspects our food joke model class and takes the field and stores it in the new table
    @Embedded
    var foodJoke: FoodJoke
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}