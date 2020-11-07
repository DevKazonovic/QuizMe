package com.my.projects.quizapp.presentation.categories

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.my.projects.quizapp.R
import com.my.projects.quizapp.databinding.FragmentCategoriesBinding
import com.my.projects.quizapp.util.Const.Companion.KEY_CATEGORY


class CategoriesFragment : Fragment() {

    private lateinit var categoriesBinding: FragmentCategoriesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        categoriesBinding = FragmentCategoriesBinding.inflate(inflater)

        setUpButtonListeners()

        return categoriesBinding.root
    }

    private fun setUpButtonListeners() {
        categoriesBinding.btnGK.setOnClickListener {
          navigateToCategory(it,9)
        }

        categoriesBinding.btnSport.setOnClickListener {
            navigateToCategory(it,21)
        }
        categoriesBinding.btnCeleb.setOnClickListener {
            navigateToCategory(it,26)
        }
    }


    private fun navigateToCategory(view:View,catID:Int) {
        view.findNavController().navigate(
            R.id.action_categories_to_quizSetting,
            bundleOf(KEY_CATEGORY to catID)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.app_setting, menu)


    }



}