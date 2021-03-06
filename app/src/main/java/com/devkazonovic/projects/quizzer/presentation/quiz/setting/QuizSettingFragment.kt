package com.devkazonovic.projects.quizzer.presentation.quiz.setting

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.devkazonovic.projects.quizzer.QuizApplication
import com.devkazonovic.projects.quizzer.R
import com.devkazonovic.projects.quizzer.data.CategoriesStore
import com.devkazonovic.projects.quizzer.databinding.FragmentQuizSettingBinding
import com.devkazonovic.projects.quizzer.domain.enums.Difficulty
import com.devkazonovic.projects.quizzer.domain.enums.Type
import com.devkazonovic.projects.quizzer.domain.manager.AppSettingManager
import com.devkazonovic.projects.quizzer.domain.model.QuizSetting
import com.devkazonovic.projects.quizzer.presentation.ViewModelProviderFactory
import com.devkazonovic.projects.quizzer.presentation.common.adapter.MaterialSpinnerAdapter
import com.devkazonovic.projects.quizzer.presentation.quiz.QuizViewModel
import com.devkazonovic.projects.quizzer.util.BundleUtil.KEY_QUIZ_CATEGORY_SELECTED
import com.devkazonovic.projects.quizzer.util.DomainUtil.Companion.COUNTDOWNPERIODS
import com.devkazonovic.projects.quizzer.util.DomainUtil.Companion.DIFFICULTIES
import com.devkazonovic.projects.quizzer.util.DomainUtil.Companion.TYPES
import com.devkazonovic.projects.quizzer.util.UiUtil
import com.devkazonovic.projects.quizzer.util.extensions.setToolbar
import javax.inject.Inject

private var argCategoryId: Int? = null

class QuizSettingFragment : Fragment() {

    private lateinit var binding: FragmentQuizSettingBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private val viewModel: QuizViewModel by navGraphViewModels(R.id.graph_quiz) {
        viewModelFactory
    }

    @Inject
    lateinit var appSettingManager: AppSettingManager

    //Save Selected Items From AutoCompleteTextView
    private var typeItemSelectedPosition: Int = 0
    private var difficultyItemSelectedPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argCategoryId = arguments?.getInt(KEY_QUIZ_CATEGORY_SELECTED)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as QuizApplication).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizSettingBinding.inflate(inflater)
        setToolbar(binding.toolbar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSelectedCategory()
        initInputFields()
        binding.btnStartQuiz.setOnClickListener {
            viewModel.getQuiz(getQuizSetting())
            findNavController().navigate(R.id.action_quizSetting_to_quiz)
        }
    }

    private fun initInputFields() {

        binding.editTextQuestionsNumber.text =
            UiUtil.getEditableInstance(appSettingManager.getNumberOfQuestions().toString())

        val countDownPeriodsAdapter = MaterialSpinnerAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            COUNTDOWNPERIODS.toTypedArray()
        )

        val difficultiesAdapter = MaterialSpinnerAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            DIFFICULTIES.keys.map { it.text }.toTypedArray()
        )

        val typesAdapter = MaterialSpinnerAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            TYPES.keys.map { it.text }.toTypedArray()
        )

        binding.editTextQuestionsCountDown.setAdapter(countDownPeriodsAdapter)
        binding.editTextQuestionsDifficulty.setAdapter(difficultiesAdapter)
        binding.editTextQuestionsType.setAdapter(typesAdapter)

        binding.editTextQuestionsCountDown.setText(
            appSettingManager.getCountDownTimer().toString()
        )
        binding.editTextQuestionsDifficulty.setText(Difficulty.ANY.text)
        binding.editTextQuestionsType.setText(Type.ANY.text)

        binding.editTextQuestionsNumber.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrEmpty()) {
                if (text.toString().toLong() > 50) {
                    binding.btnStartQuiz.isEnabled = false
                    binding.textInputLayoutQuestionsNumber.error =
                        getString(R.string.quizsetting_amount_input_error)
                } else {
                    binding.btnStartQuiz.isEnabled = true
                    binding.textInputLayoutQuestionsNumber.error = null
                }
            } else {
                binding.btnStartQuiz.isEnabled = false
                binding.textInputLayoutQuestionsNumber.error =
                    getString(R.string.all_input_error_required)
            }
        }
        binding.editTextQuestionsType.setOnItemClickListener { _, _, position, _ ->
            typeItemSelectedPosition = position
        }
        binding.editTextQuestionsDifficulty.setOnItemClickListener { _, _, position, _ ->
            difficultyItemSelectedPosition = position
        }

    }

    private fun showSelectedCategory() {
        val category = CategoriesStore.findCategoryById(argCategoryId!!)
        category.let {
            binding.textViewCategoryName.text = it.name
            binding.imageViewCategoryIcon.setImageResource(it.icon)
        }

    }

    private fun getQuizSetting(): QuizSetting {
        val inputNumberOfQuestions = binding.editTextQuestionsNumber.text.toString()
        val inputCountDownInSeconds = binding.editTextQuestionsCountDown.text.toString()
        return QuizSetting(
            category = argCategoryId,
            numberOfQuestions = inputNumberOfQuestions.toInt(),
            type = TYPES.toList()[typeItemSelectedPosition].second,
            difficulty = DIFFICULTIES.toList()[difficultyItemSelectedPosition].second,
            countDownInSeconds = inputCountDownInSeconds.toInt()
        )
    }
}