package com.example.foody.ui.fragments.foodjoke

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.foody.R
import com.example.foody.databinding.FragmentFoodJokeBinding
import com.example.foody.util.Constants.Companion.API_KEY
import com.example.foody.util.NetworkResult
import com.example.foody.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {
    //you can either create your view model by delegate or using the viewModel provider both are fine
    //ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

    private val mainViewModel by viewModels<MainViewModel>()
    private var _binding: FragmentFoodJokeBinding? = null
    private val binding get() = _binding!!

    //default food joke
    private var foodJoke = "No  Food Joke"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodJokeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel

        setHasOptionsMenu(true)
        mainViewModel.getFoodJoke(API_KEY)

        mainViewModel.foodJokeResponse.observe(viewLifecycleOwner){ response ->
            when(response){
                is NetworkResult.Success -> {
                    binding.foodJokeTextView.text = response.data?.text.toString()
                    //if food joke loaded successfully
                    if(response.data != null){
                        foodJoke = response.data.text
                    }
                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    Log.d("FoodJokeFragment", "Loading")
                }
            }

        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_joke_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_food_joke_menu){
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, foodJoke)
                this.type = "text/plain"
            }
            startActivity(shareIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadDataFromCache(){
        mainViewModel.readFoodJoke.observe(viewLifecycleOwner){ database ->
            //if we have a joke in the db then set the text as the cached joke
            //also set the global foodJoke as the text
            if(!database.isNullOrEmpty()){
                //The Livedata returns a list of FoodJoke which are the rows in the db, however we only ever have one row
                binding.foodJokeTextView.text = database[0].foodJoke.text
                foodJoke = database[0].foodJoke.text
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //avoid memory leaks
        _binding = null
    }

}

