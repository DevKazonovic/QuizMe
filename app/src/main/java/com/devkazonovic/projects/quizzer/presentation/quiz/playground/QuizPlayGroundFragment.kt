package com.devkazonovic.projects.quizzer.presentation.quiz.playground

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.devkazonovic.projects.quizzer.QuizApplication
import com.devkazonovic.projects.quizzer.R
import com.devkazonovic.projects.quizzer.data.CategoriesStore
import com.devkazonovic.projects.quizzer.databinding.FragmentQuizPlaygroundBinding
import com.devkazonovic.projects.quizzer.domain.manager.AppSettingManager
import com.devkazonovic.projects.quizzer.domain.model.Question
import com.devkazonovic.projects.quizzer.presentation.ViewModelProviderFactory
import com.devkazonovic.projects.quizzer.presentation.common.widgets.LogsRadioButtons.Companion.getAnswerRadio
import com.devkazonovic.projects.quizzer.presentation.common.widgets.LogsRadioButtons.Companion.layoutParams
import com.devkazonovic.projects.quizzer.presentation.quiz.QuizViewModel
import com.devkazonovic.projects.quizzer.util.extensions.hide
import com.devkazonovic.projects.quizzer.util.extensions.hideSystemUI
import com.devkazonovic.projects.quizzer.util.extensions.show
import com.devkazonovic.projects.quizzer.util.extensions.showSystemUI
import com.devkazonovic.projects.quizzer.util.wrappers.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class QuizPlayGroundFragment : Fragment() {

    private lateinit var binding: FragmentQuizPlaygroundBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private val viewModel: QuizViewModel by navGraphViewModels(R.id.graph_quiz) {
        viewModelFactory
    }

    @Inject
    lateinit var appSettingManager: AppSettingManager


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as QuizApplication).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizPlaygroundBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUiVisibilityListener()
        binding.btnNext.setOnClickListener {
            viewModel.onMoveToNextQuiz()
        }
        binding.viewClosePage.setOnClickListener {
            viewModel.onStop()
            findNavController().navigateUp()
        }
        binding.layoutErrors.setOnRefreshListener {
            viewModel.onReferesh()
            binding.layoutErrors.isRefreshing = false
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeDataChange()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        showSystemUI()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    private fun observeDataChange() {

        viewModel.dataState.observe(viewLifecycleOwner, { state ->
            when (state) {
                is DataState.Loading -> onLoading()
                is DataState.Success -> onSuccess()
                is DataState.Error -> onError(state.error)
                is DataState.NetworkException -> onError(state.error)
                is DataState.HttpErrors.NoResults -> onError(state.exception)
                is DataState.HttpErrors.InvalidParameter -> onError(state.exception)
            }
        })

        viewModel.currentQuestion.observe(viewLifecycleOwner, {
            displayQuestion(it)
        })

        viewModel.currentQuestionPosition.observe(viewLifecycleOwner, {
            updateProgressBar(it)
        })

        viewModel.currentQuizSetting.observe(viewLifecycleOwner, { quizSetting ->
            quizSetting.category?.let { categoryID ->
                val category = CategoriesStore.findCategoryById(categoryID)
                binding.textViewCategoryName.text = category.name
                binding.imageViewCategoryIcon.setImageResource(category.icon)
            }
        })

        viewModel.countDown.observe(viewLifecycleOwner, { countDown ->
            binding.textViewQuestionCountDown.text = getString(
                R.string.quiz_countdown_placeholder,
                countDown
            )
        })

        viewModel.isQuizFinished.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                if (it) findNavController().navigate(R.id.action_quiz_to_score)
            }
        })

    }

    private fun updateProgressBar(p: Int) {
        binding.progressBarQuizQuestion.progress = p
        binding.textViewQuestionNumber.text = getString(
            R.string.quiz_progress_placeholder,
            p,
            viewModel.getCurrentQuizzesListSize()
        )
    }

    private fun displayQuestion(question: Question) {
        var id = 0
        binding.radiogroupCardquestionAnswerchoices.clearCheck()
        binding.radiogroupCardquestionAnswerchoices.removeAllViews()
        binding.txtviewCardquestionQuestion.text = question.question
        question.answers.forEach {
            binding.radiogroupCardquestionAnswerchoices.addView(
                getAnswerRadio(requireContext(), it.answer, id),
                layoutParams
            )
            id++
        }

        binding.radiogroupCardquestionAnswerchoices.setOnCheckedChangeListener { _, checkedId ->
            viewModel.onQuestionAnswered(checkedId)
        }
    }

    private fun onSuccess() {
        hideSystemUI()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        binding.progressBar.hide()
        binding.layoutErrors.hide()
        binding.layoutData.show()
        binding.progressBarQuizQuestion.max = viewModel.getCurrentQuizzesListSize()
    }

    private fun onError(message: Int) {
        showSystemUI()
        (activity as AppCompatActivity).supportActionBar?.show()
        binding.progressBar.hide()
        binding.layoutData.hide()
        binding.layoutErrors.show()
        binding.textViewError.text = getString(message)
    }

    private fun onLoading() {
        hideSystemUI()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        binding.layoutData.hide()
        binding.layoutErrors.hide()
        binding.progressBar.show()
    }

    private fun hideSystemUI() {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.hideSystemUI()
    }

    private fun showSystemUI() {
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        activity?.showSystemUI()
        val isDarkTheme = appSettingManager.getCurrentAppTheme()
        if (!isDarkTheme) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }
    }

    private fun setUiVisibilityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            binding.root.setOnApplyWindowInsetsListener { _, insets ->
                if (insets.isVisible(4)) {
                    lifecycleScope.launch {
                        delay(2000)
                        hideSystemUI()
                    }
                }
                insets
            }
        }
    }

}