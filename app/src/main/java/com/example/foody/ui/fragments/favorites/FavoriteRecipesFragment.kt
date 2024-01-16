package com.example.foody.ui.fragments.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.adapters.FavoriteRecipesAdapter
import com.example.foody.databinding.FragmentFavoriteRecipesBinding
import com.example.foody.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_favorite_recipes.view.*

//anytime we create our MainViewModel that requires dependency injection,
//we need to annotate with AndroidEntryPoint so that hilt can create the dependencies
@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {

    private val mAdapter: FavoriteRecipesAdapter by lazy { FavoriteRecipesAdapter() }
    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter
        val view = inflater.inflate(R.layout.fragment_favorite_recipes, container, false)
        setupRecyclerView(binding.favoriteRecipesRecyclerView)
        //observe the data from reading Favorite Recipes from Room database and set the data in the recycler view adapter
        mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner){favoritesEntity ->
            mAdapter.setData(favoritesEntity)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView(recyclerView: RecyclerView){
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

}