package com.example.foody.adapters

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.data.database.entities.FavoritesEntity
import com.example.foody.databinding.FavoriteRecipesRowLayoutBinding
import com.example.foody.ui.fragments.favorites.FavoriteRecipesFragmentDirections
import com.example.foody.util.RecipesDiffUtil
import kotlinx.android.synthetic.main.favorite_recipes_row_layout.view.*

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity
): RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(), ActionMode.Callback {
    private var favoriteRecipes = emptyList<FavoritesEntity>()
    private var multiSelection = false
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
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
        //create onclick for each favorite recipe card to navigate to the details fragment of that card
        holder.itemView.favoriteRecipesRowLayout.setOnClickListener{
            val action =
                FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
                    selectedRecipe.result
                )
            holder.itemView.findNavController().navigate(action)
        }
        //long lick listener
        holder.itemView.favoriteRecipesRowLayout.setOnLongClickListener{
            requireActivity.startActionMode(this)
            true
        }
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: FavoritesEntity){
        if(selectedRecipes.contains(currentRecipe)){
            selectedRecipes.remove(currentRecipe)
        } else {
            selectedRecipes.add(currentRecipe)
        }
    }

    //this changes the stroke and background color of our favorite_recipes_row_layout when selected
    private fun changeRecipeStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int){
        holder.itemView.favoriteRecipesRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.itemView.favorite_row_cardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    //inflate new menu
    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
        return true
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        applyStatusBarColor(R.color.statusBarColor)
    }

    //change the color of the status bar color so that it matches the context menu action bar color
    private fun applyStatusBarColor(color: Int){
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, color)
    }

    fun setData(newFavoriteRecipes: List<FavoritesEntity>){
        val favoriteRecipesDiffUtil = RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }


}