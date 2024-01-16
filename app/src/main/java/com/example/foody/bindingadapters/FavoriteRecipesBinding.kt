package com.example.foody.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.adapters.FavoriteRecipesAdapter
import com.example.foody.data.database.entities.FavoritesEntity

class FavoriteRecipesBinding {

    companion object {
        //when setting these attributes, they refer to the constructor parameters respectively, so order is important, viewVisibility refers to the view and so on.
        //favoritesEntity is our live data object from our view model, we are reading our database and checking if it's null or empty and then dispaly the hidden views and hide the recyclerview
        @BindingAdapter("viewVisibility", "setData", requireAll = false)
        @JvmStatic
        fun setDataAndViewVisibility(view: View, favoritesEntity: List<FavoritesEntity>?, mAdapter: FavoriteRecipesAdapter?){
            if (favoritesEntity.isNullOrEmpty()){
                when (view) {
                    is ImageView -> {
                        view.visibility = View.VISIBLE
                    }
                    is TextView -> {
                        view.visibility = View.VISIBLE
                    }
                    is RecyclerView -> {
                        view.visibility = View.INVISIBLE
                    }
                }
            } else {
                when (view) {
                    is ImageView -> {
                        view.visibility = View.INVISIBLE
                    }
                    is TextView -> {
                        view.visibility = View.INVISIBLE
                    }
                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        mAdapter?.setData(favoritesEntity)
                    }
                }
            }
        }
    }
}