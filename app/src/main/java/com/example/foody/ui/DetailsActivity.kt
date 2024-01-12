package com.example.foody.ui

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.foody.R
import com.example.foody.adapters.PagerAdapter
import com.example.foody.databinding.ActivityDetailsBinding
import com.example.foody.ui.fragments.ingredients.IngredientsFragment
import com.example.foody.ui.fragments.instructions.InstructionsFragment
import com.example.foody.ui.fragments.overview.OverviewFragment
import com.example.foody.util.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //array list of fragments
        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        //we need to create a bundle object which we need from the recipesFragment passed to the DetailsActivity
        //create the bundle
        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        //initialize pager adapter
        val adapter = PagerAdapter(
            resultBundle,
            fragments,
            titles,
            supportFragmentManager
        )

        //this sets the adapter for the viewPager in the xml
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //this lets you close the DetailsActivity and navigate back to home
        if (item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}