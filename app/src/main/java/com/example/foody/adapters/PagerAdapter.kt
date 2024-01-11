package com.example.foody.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

//specifies how to pass data from our recipesFragment to our DetailActivity
class PagerAdapter(
    //this is used to pass data from DetailsActivity to our fragments
    private val resultBundle: Bundle,
    private val fragments: ArrayList<Fragment>,
    private val title: ArrayList<String>,
    fm: FragmentManager
): FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        //number of fragments, this is 3 because we are adding 3 fragment tabs
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        //pass the result from our recipe to the fragments
        fragments[position].arguments = resultBundle
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //return the exact title of the fragments
        return title[position]
    }
}