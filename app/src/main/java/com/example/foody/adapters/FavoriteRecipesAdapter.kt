package com.example.foody.adapters

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.example.foody.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
): RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(), ActionMode.Callback {
    private var favoriteRecipes = emptyList<FavoritesEntity>()
    private var multiSelection = false
    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()
    class MyViewHolder(val binding: FavoriteRecipesRowLayoutBinding): RecyclerView.ViewHolder(binding.root){
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

    //this occurs for each card in the favorites recycler view
    override fun onBindViewHolder(holder: FavoriteRecipesAdapter.MyViewHolder, position: Int) {
        //add each card view holder to this list of viewHolders
        myViewHolders.add(holder)
        //store the root view in the global variable
        rootView = holder.itemView.rootView
        //dynamically get the position of each row and store it in selectedRecipe
        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)
        //create onclick for each favorite recipe card to navigate to the details fragment of that card
        holder.binding.favoriteRecipesRowLayout.setOnClickListener{
            //if we are multiSelecting, it means we are in the contextual action mode so we can change the style for each card
            if(multiSelection) {
                //we only call applySelection and set our card styles if multiSelection is true, so we do not accidentally navigate to our detailsActivity when we are making selections in our list.
                applySelection(holder, currentRecipe)
            } else {
                //if we are single clicking, NOT in the contextual action mode, we navigate to the details screen for that recipe
                val action =
                    FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
                        currentRecipe.result
                    )
                holder.itemView.findNavController().navigate(action)
            }
        }
        //long lick listener
        holder.binding.favoriteRecipesRowLayout.setOnLongClickListener{
            //if multiSelection is false, we want to set it to true (we are currently multi-selecting) and then start the action mode context
            //The start applying the selection with the view holder of selected item and the current recipe
            if(!multiSelection) {
                //of we have long clicked, we are entering context action mode so we want to select that card
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentRecipe)
                true
            } else {
                //if we are not multiSelecting, we are not in contextual action mode so we set it to false
                multiSelection = false
                false
            }
        }
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe: FavoritesEntity){
        //if the selectedRecipes list contains the current recipe, remove it from the list of selected recipes
        //Then change the stroke color accordingly
        if(selectedRecipes.contains(currentRecipe)){
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            //check if we have any items in our selectedRecipes list, and when we deselect everything, then check if we need to close the contextual action mode.
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    //this changes the stroke and background color of our favorite_recipes_row_layout when selected
    private fun changeRecipeStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int){
        holder.binding.favoriteRecipesRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.favoriteRowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    //if our selectedRecipes are empty, close our contextual action mode
    private fun applyActionModeTitle(){
        when(selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} item selected"
            }
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    //inflate new menu
    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        //get the action mode and store it inside global variable to set the title of the contextual action mode
        mActionMode = actionMode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        //when we click the trash can, loop through all the selectedRecipes entities and delete them in the viewModel
        if(menu?.itemId == R.id.delete_favorite_recipe_menu){
            selectedRecipes.forEach{
                mainViewModel.deleteFavoriteRecipe(it)
            }
            showSnackBar("${selectedRecipes.size} Recipe/s removed.")
            multiSelection = false
            selectedRecipes.clear()
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(p0: ActionMode?) {
        //onDestroy, loop through all the viewHolders and change the style back to the original style
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiSelection = false
        //when we close our action mode, clear all the selected recipes
        selectedRecipes.clear()
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

    //create a snackbar that is displayed after deleting favorite recipes
    private fun showSnackBar(message: String){
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}
            .show()
    }

    fun clearContextualActionMode(){
        if(this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }


}