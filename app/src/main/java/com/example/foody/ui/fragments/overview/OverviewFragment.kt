package com.example.foody.ui.fragments.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import com.example.foody.R
import com.example.foody.bindingadapters.RecipesRowBinding
import com.example.foody.databinding.FragmentOverviewBinding
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import org.jsoup.Jsoup

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    //the get() delcares how the property value is retrieved when accessed
    //if we just wrote val binding = _binding, it would immediately be set to null at the time of declaration before it is initialised
    //the get() ensures that binding always takes the latest most recent value of _binding
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val args = arguments
        val myBundle: Result = args!!.getParcelable<Result>(RECIPE_RESULT_KEY) as Result

        binding.mainImageView.load(myBundle.image)
        binding.favoriteTitleTextView.text = myBundle.title
        binding.likesTextView.text = myBundle.aggregateLikes.toString()
        binding.timeTextView.text = myBundle.readyInMinutes.toString()
        RecipesRowBinding.parseHtml(binding.summaryTextView, myBundle.summary)

        val viewsList = listOf(
            Triple(myBundle.vegetarian, binding.vegetarianImageView, binding.vegetarianTextView),
            Triple(myBundle.vegan, binding.veganImageView, binding.veganTextView),
            Triple(myBundle.glutenFree, binding.glutenFreeImageView, binding.glutenFreeTextView),
            Triple(myBundle.dairyFree, binding.dairyFreeImageView, binding.dairyFreeTextView),
            Triple(myBundle.veryHealthy, binding.healthyImageView, binding.healthyTextView),
            Triple(myBundle.cheap, binding.cheapImageView, binding.cheapTextView),
        )

        for((state, imageView, texView) in viewsList){
            if(state){
                imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                texView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}