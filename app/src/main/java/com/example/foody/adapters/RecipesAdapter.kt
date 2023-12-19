package com.example.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.databinding.RecipesRowLayoutBinding
import com.example.foody.models.FoodRecipe
import com.example.foody.models.Result
import com.example.foody.util.RecipesDiffUtil

class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    private var recipes = emptyList<Result>()
    class MyViewHolder(private val binding: RecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(result: Result) {
            binding.result = result
            binding.executePendingBindings() //updates our layout when there is a change in our data
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder { //lets us access this from else where
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding) //returns class we have created and the inflated binding
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { //we can get the parent and receive the parent and use it to create the layout inflater
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //stores the current item inside the recycler view and dynamically get the row position
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe) //your view holder and you need to call bind to bind the variable with the current result
    }

    fun setData(newData: FoodRecipe){
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        //calculates difference between new and old data and only updates different views
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        //we're gonna call this from the fragment and pass new data to this function and the store it inside the empty list in the adapter
        //this is called every time we fetch new data to update the list
        recipes = newData.results
        //this notifies the recycler view that data has changed
        //notifyDataSetChanged() // this updates the whole recycler view list all over again and is not very performant
        //it does not check if old recipes are different
        diffUtilResult.dispatchUpdatesTo(this)
    }
}