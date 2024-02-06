package com.example.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foody.viewmodels.MainViewModel
import com.example.foody.R
import com.example.foody.adapters.RecipesAdapter
import com.example.foody.databinding.FragmentRecipesBinding
import com.example.foody.util.NetworkListener
import com.example.foody.util.NetworkResult
import com.example.foody.util.observeOnce
import com.example.foody.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {
    //automatically added by navigation component when we specify arguments in our my_nav
    private val args by navArgs<RecipesFragmentArgs>()
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val mAdapter by lazy { RecipesAdapter() }

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this //in our fragmentRecipesFragment, we are using live data objects so we need to specify the lifecycle owner.
        binding.mainViewModel = mainViewModel //bind our mainViewModel to our fragment

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                //Add menu items here
                menuInflater.inflate(R.menu.recipes_menu, menu)
                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        setupRecyclerView()

        //get the lastest value from dataStore and set that value to the backOnline variable
        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }
        //launch a coroutine because .collect is a suspend function
        //checkNetworkAvailability is a mutable state flow and persists in the background even when the current fragment is not active
        //When we we are in another fragment and we turn on or off internet connection, readDatabase is called
        //we only want to collect our mutableStateFlow when our lifecycle is started for this fragment and not when we are in another fragment
        //when we call launcheWhenStarted, it means this is called when at least the view is created.
        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            //collect the MutableStateFlow for networkAvailability and observe its value
            networkListener.checkNetworkAvailability(requireContext())
                .collect{ status ->
                    Log.d("NetworkListener", status.toString())
                    //check the status from the networkListener util class, and observe it, then set the vm global variable to the value of the status
                    //display a toast accordingly
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    //whenever our network status changes, we want to read the room db or make api call
                    //the app was crashing when we turn network on or off in another fragment.
                    //this is because the RecipesFragment still exists in memory even when we navigate to a different fragment and the onCreateView is still called to rebuild the UI even when not visible
                    //because of this, checkNetworkAvailability .collect is always observing and the lifecycleScope.launch persists beyond the view's lifecycle.
                    //readRecipes is managed by the viewLifecycleOwner and when readRecipes is called outside of the viewLifecycleOwner Scope, the app crashes because we are trying to access the view.
                    // the lifecycle scope of the fragment exists outside of onDestroyView, the view's lifecycle does not.
                    readDatabase()
                }
        }

        binding.recipesFab.setOnClickListener{
            if (recipesViewModel.networkStatus) {
                //only allow us to open the bottom sheet if we have internet connection
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            } else {
                //else show a toast with an error
                recipesViewModel.showNetworkStatus()
            }

        }
        return binding.root
    }

    private fun setupRecyclerView(){
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun readDatabase() {
        lifecycleScope.launch{
            //don't call read database, only call readDatabase when this function is called
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner){database ->
                //if our database is not empty and we're not backFromBottomSheet, then load the data
                //when we get back from our bottomSheet, it means we want to create a new query for our GET request. We only read from the database when backFromBottomSheet false
                //when we leave the bottomSheet we want to request new ai
                var test = false
                if(arguments != null) {
                    test = requireArguments().getBoolean("backFromBottomSheet", false)
                }
                if (database.isNotEmpty() && !test){ //if there's a row in the db then set the data for the recycler view
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database[0].foodRecipe) //access the row in the db
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData(){
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner){response ->
            when(response){
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { mAdapter.setData(it) }
                    //only save our mealAndDietType when the response is successful, if we get no results don't save our bottomSheet state in local storage
                    recipesViewModel.saveMealAndDietType()
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }

        }
    }

    private fun searchApiData(searchQuery: String){
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchedRecipesResponse.observe(viewLifecycleOwner){ response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let { mAdapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun loadDataFromCache(){
        lifecycleScope.launch{
            mainViewModel.readRecipes.observe(viewLifecycleOwner){database ->
                if(database.isNotEmpty()){
                    mAdapter.setData(database[0].foodRecipe)
                }
            }
        }
    }

    private fun showShimmerEffect(){
        binding.shimmerFrameLayout.startShimmer()
        binding.recyclerView.visibility = View.GONE
    }

    private fun hideShimmerEffect(){
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null //this prevents memory leaks
        //whenever the recipes fragment is destroyed then the binding will be set to null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null) {
            searchApiData(query)
        }
        return true
    }

    //we don't want to search onChange because it's bad ux, so we'll not set any code
    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }
}