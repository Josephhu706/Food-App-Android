package com.example.foody.data

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import com.example.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foody.util.Constants.Companion.PREFERENCES_DIET_TYPE
import com.example.foody.util.Constants.Companion.PREFERENCES_DIET_TYPE_ID
import com.example.foody.util.Constants.Companion.PREFERENCES_MEAL_TYPE
import com.example.foody.util.Constants.Companion.PREFERENCES_MEAL_TYPE_ID
import com.example.foody.util.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

//we need to save the bottomsheet selected chips in our local data store, so when we select a chip the next time it persists
//we need the application context for this (android context stuff so we have access to datastore)
//this repository will be used inside our RecipesViewModel
@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private  val context: Context) {
    //these are our keys we're going to use to access our datastore preferences
    private  object PreferencesKeys {
        //this is for the meal type chip
        val selectedMealType = preferencesKey<String>(PREFERENCES_MEAL_TYPE)
        //this is the id of the meal type chip
        val selectedMealTypeId = preferencesKey<Int>(PREFERENCES_MEAL_TYPE_ID)
        //this is the chip for the diet Type
        val selectedDietType = preferencesKey<String>(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = preferencesKey<Int>(PREFERENCES_DIET_TYPE_ID)
    }
    //create datstore and name it
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCES_NAME
    )

    //store values in datastore preferences using preference keys
    suspend fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.selectedMealType] = mealType
            preferences[PreferencesKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferencesKeys.selectedDietType] = dietType
            preferences[PreferencesKeys.selectedDietTypeId] = dietTypeId
        }
    }

    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data
        .catch { exception ->
            //if there is an error
            if(exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            //selecting the value of the selectedMealType key, and if there is no value saved then emit "main course"
            val selectedMealType = preferences[PreferencesKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            //if there is no id data, then set id to 0
            val selectedMealTypeId = preferences[PreferencesKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferencesKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferencesKeys.selectedDietTypeId] ?: 0
            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }
}

data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)