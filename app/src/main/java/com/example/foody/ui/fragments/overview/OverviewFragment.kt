package com.example.foody.ui.fragments.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import com.example.foody.R
import com.example.foody.models.Result
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_overview.view.*
import org.jsoup.Jsoup
import kotlin.reflect.KProperty1

class OverviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)
        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        view.main_imageView.load(myBundle?.image)
        view.favorite_title_textView.text = myBundle?.title
        view.likes_textView.text = myBundle?.aggregateLikes.toString()
        view.time_textView.text = myBundle?.readyInMinutes.toString()
        myBundle?.summary.let {
            val summary = Jsoup.parse(it).text()
            view.summary_textView.text = summary
        }

        val viewsList = listOf(
            Triple(myBundle?.vegetarian, view.vegetarian_imageView, view.vegetarian_textView),
            Triple(myBundle?.vegan, view.vegan_imageView, view.vegan_textView),
            Triple(myBundle?.glutenFree, view.gluten_free_imageView, view.gluten_free_textView),
            Triple(myBundle?.dairyFree, view.dairy_free_imageView, view.dairy_free_textView),
            Triple(myBundle?.veryHealthy, view.healthy_imageView, view.healthy_textView),
            Triple(myBundle?.cheap, view.cheap_imageView, view.cheap_textView),
        )

        for((state, imageView, texView) in viewsList){
            if(state != null && state == true){
                imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                texView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        }

        return view
    }
}