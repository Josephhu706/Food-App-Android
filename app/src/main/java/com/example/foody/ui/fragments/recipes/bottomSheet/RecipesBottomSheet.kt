package com.example.foody.ui.fragments.recipes.bottomSheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.example.foody.R
import com.example.foody.databinding.RecipesBottomSheetBinding
import com.example.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.example.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.example.foody.viewmodels.RecipesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.util.*

class RecipesBottomSheet : BottomSheetDialogFragment() {
    private lateinit var recipesViewModel: RecipesViewModel
    private var _binding : RecipesBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //create vm and pass in context
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = RecipesBottomSheetBinding.inflate(inflater, container, false)

        //observe the preferences datastore flow as livedata and set the variables above
        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner){value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(value.selectedMealTypeId, binding.mealTypeChipGroup)
            updateChip(value.selectedDietTypeId, binding.dietTypeChipGroup)
        }

        //store meal type chip data in variables
        binding.mealTypeChipGroup.setOnCheckedStateChangeListener{ group, selectedChipId ->
            //get the selecte chip
            val chip = group.findViewById<Chip>(selectedChipId.first())
            //get the text as lowercase on the chip
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId.first()
        }

        //store diet type chip data in variables
        binding.dietTypeChipGroup.setOnCheckedStateChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId.first())
            val selectedDietType = chip.text.toString().lowercase(Locale.ROOT)
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId.first()
        }

        binding.applyBtn.setOnClickListener {
            //save the values of the bottomsheet chips
            //this always saves the most recent bottom sheet state in a local variable in the vm
            recipesViewModel.saveMealAndDietTypeTemp(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
            //when we leave the recipes bottomSheet, we want to pass true to this action
            val action = RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(backFromBottomSheet = true)
            findNavController().navigate(action)
        }
        return binding.root
    }

    private  fun updateChip(chipId: Int, chipGroup: ChipGroup){
        //if we have a chip id
        if(chipId != 0){
            try {
                //find the target chip and set its checked state to true
                val targetView = chipGroup.findViewById<Chip>(chipId)
                targetView.isChecked = true
                //set the focus of the chip so it is always visible if checked
                chipGroup.requestChildFocus(targetView, targetView)
                //find the checked chip and change the checked state to true (you can't uncheck a chip by clicking it)
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            } catch (e: Exception) {
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
    }

}