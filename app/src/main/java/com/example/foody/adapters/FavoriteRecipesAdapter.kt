package com.example.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.data.database.entities.FavoritesEntity
import com.example.foody.databinding.FavoriteRecipesRowLayoutBinding
import com.example.foody.util.RecipesDiffUtil

class FavoriteRecipesAdapter: RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>() {
    private var favoriteRecipes = emptyList<FavoritesEntity>()
    class MyViewHolder(private val binding: FavoriteRecipesRowLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(favoritesEntity: FavoritesEntity){
            binding.favoritesEntity = favoritesEntity
            ///updates our views
            binding.executePendingBindings()
        }

        companion object {
            //binding our layout with our recycler view
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteRecipesAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FavoriteRecipesAdapter.MyViewHolder, position: Int) {
        //dynamically get the position of each row and store it in selectedRecipe
        val selectedRecipe = favoriteRecipes[position]
        holder.bind(selectedRecipe)
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    fun setData(newFavoriteRecipes: List<FavoritesEntity>){
        val favoriteRecipesDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }


}