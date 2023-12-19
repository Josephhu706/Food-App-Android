package com.example.foody.util

import androidx.recyclerview.widget.DiffUtil
import com.example.foody.models.Result

//
class RecipesDiffUtil(
    private val oldList: List<Result>,
    private val newList: List<Result>
): DiffUtil.Callback() { //compares the old list with the new list and only updates new views and is more performant
    override fun getOldListSize(): Int { // returns size of old list
        return oldList.size
    }

    override fun getNewListSize(): Int { //returns size of new list
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {//checks if two objects are the same item in the old and new list
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean { //checks if items have the same data
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}