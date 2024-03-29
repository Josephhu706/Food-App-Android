package com.example.foody.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.foody.data.DataStoreRepository
import com.example.foody.data.MealAndDietType
import com.example.foody.util.Constants
import com.example.foody.util.Constants.Companion.API_KEY
import com.example.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foody.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.example.foody.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.example.foody.util.Constants.Companion.QUERY_API_KEY
import com.example.foody.util.Constants.Companion.QUERY_DIET
import com.example.foody.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.example.foody.util.Constants.Companion.QUERY_NUMBER
import com.example.foody.util.Constants.Companion.QUERY_SEARCH
import com.example.foody.util.Constants.Companion.QUERY_TYPE
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
): AndroidViewModel(application) {

    private lateinit var mealAndDiet: MealAndDietType

    var networkStatus = false
    var backOnline = false

    //this checks the value of our mealAndDietType for our bottomSheet in local storage
    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    //this checks the value of our networkConnection in local storage and converts it to liveData
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData()

    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealAndDiet.selectedMealType, mealAndDiet.selectedMealTypeId, mealAndDiet.selectedDietType, mealAndDiet.selectedDietTypeId)
    }
    fun saveMealAndDietTypeTemp(mealType: String, mealTypeId: Int, dietType:String, dietTypeId: Int) {
        mealAndDiet = MealAndDietType(
            mealType,
            mealTypeId,
            dietType,
            dietTypeId
        )
    }
    //this function passes the internet connection status to the dataStoreRepo and saves it in local storage
    fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO){
            dataStoreRepository.saveBackOnline(backOnline)
        }

    fun applyQueries(): HashMap<String, String>{
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealAndDiet.selectedMealType
        queries[QUERY_DIET] = mealAndDiet.selectedDietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_SEARCH] = searchQuery
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    //show a toast if we don't have internet connection
    fun showNetworkStatus() {
        if (!networkStatus) {
            //when we lose our internet connection we want to save the status of backOnline to true
            Toast.makeText(getApplication(), "No Internet Connection.", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if(networkStatus) {
            if(backOnline){
                Toast.makeText(getApplication(), "We're back online.", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}