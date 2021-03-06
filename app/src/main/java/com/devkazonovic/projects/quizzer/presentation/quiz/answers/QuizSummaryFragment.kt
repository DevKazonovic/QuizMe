package com.devkazonovic.projects.quizzer.presentation.quiz.answers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.devkazonovic.projects.quizzer.QuizApplication
import com.devkazonovic.projects.quizzer.R
import com.devkazonovic.projects.quizzer.databinding.FragmentQuizSummaryBinding
import com.devkazonovic.projects.quizzer.domain.model.Question
import com.devkazonovic.projects.quizzer.presentation.ViewModelProviderFactory
import com.devkazonovic.projects.quizzer.presentation.quiz.QuizViewModel
import com.devkazonovic.projects.quizzer.util.extensions.setToolbar
import javax.inject.Inject


class QuizSummaryFragment : Fragment() {

    private lateinit var binding: FragmentQuizSummaryBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private val viewModel: QuizViewModel by navGraphViewModels(R.id.graph_quiz) {
        viewModelFactory
    }

    private lateinit var adapter: QuestionsAdapter
    private lateinit var list: MutableList<Question>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as QuizApplication).component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizSummaryBinding.inflate(inflater)
        setToolbar(binding.toolbar)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showUserAnswers()
    }

    private fun showUserAnswers() {
        list = viewModel.onCurrentQuizSummary()
        binding.recyclerViewUserAnswers.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionsAdapter(list)
        binding.recyclerViewUserAnswers.adapter = adapter
    }


}