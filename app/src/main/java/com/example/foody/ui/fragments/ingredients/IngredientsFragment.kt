package com.example.foody.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.foody.R
import com.example.foody.adapters.IngredientsAdapter
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_ingredients.view.*
import kotlinx.android.synthetic.main.fragment_overview.view.*


class IngredientsFragment : Fragment() {
    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)
        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)
        setupRecyclerView(view)
        myBundle?.extendedIngredients?.let {
        //pass the data from the DetailsActivity bundle into the adapter setData function
            mAdapter.setData(it)
        }
        return view
    }


    private fun setupRecyclerView(view: View) {
        view.ingredients_recyclerView.adapter = mAdapter
        view.ingredients_recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }
}