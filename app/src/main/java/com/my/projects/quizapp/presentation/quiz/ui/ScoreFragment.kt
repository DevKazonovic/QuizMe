package com.my.projects.quizapp.presentation.quiz.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.my.projects.quizapp.R
import com.my.projects.quizapp.data.model.QuestionModel
import com.my.projects.quizapp.databinding.FragmentScoreBinding
import com.my.projects.quizapp.databinding.SaveQuizLayoutBinding
import com.my.projects.quizapp.di.QuizInjector
import com.my.projects.quizapp.presentation.quiz.adapter.QuestionsAdapter
import com.my.projects.quizapp.presentation.quiz.controller.QuizViewModel

class ScoreFragment : Fragment() {
    private lateinit var scoreBinding: FragmentScoreBinding
    private lateinit var quizViewModel: QuizViewModel

    private lateinit var adapter: QuestionsAdapter
    private lateinit var list: MutableList<QuestionModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scoreBinding = FragmentScoreBinding.inflate(inflater)

        return scoreBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        quizViewModel = ViewModelProvider(
            requireActivity(),
            QuizInjector(requireActivity().application).provideQuizViewModelFactory()
        ).get(QuizViewModel::class.java)

        scoreBinding.btnSave.setOnClickListener {
            createAlterDialog()
        }
        initLogsList()
        showHideLogs()
        observeData()
    }

    private fun observeData() {
        quizViewModel.score.observe(viewLifecycleOwner, { score ->
            scoreBinding.txtTotalQuestions.text =
                quizViewModel.getCurrentQuizzesListSize().toString()
            scoreBinding.txtCorrectAnswers.text = score.toString()
            scoreBinding.txtWrongAnswers.text =
                (quizViewModel.getCurrentQuizzesListSize() - score).toString()

        })
    }

    private fun initLogsList() {
        //get DataList
        list = quizViewModel.onGetQuizLogs()

        //Setup RecyclerView
        scoreBinding.recyclerQuestions.layoutManager = LinearLayoutManager(requireContext())
        adapter = QuestionsAdapter(list)
        scoreBinding.recyclerQuestions.adapter = adapter

    }

    private fun showHideLogs() {
        var isHidden = true
        scoreBinding.layoutShowLogs.setOnClickListener {
            if (isHidden) {
                scoreBinding.recyclerQuestions.visibility = VISIBLE
                scoreBinding.icShowLogs.setImageResource(R.drawable.ic_round_arrow_down)
                isHidden = false
            } else {
                scoreBinding.recyclerQuestions.visibility = GONE
                scoreBinding.icShowLogs.setImageResource(R.drawable.ic_round_arrow_right)
                isHidden = true
            }
        }
    }

    private fun createAlterDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Quiz Name")
        }
        val layout = SaveQuizLayoutBinding.inflate(layoutInflater)
        builder.setView(layout.root)
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }.setPositiveButton("Save") { dialog, which ->
            // Respond to positive button press
            val nameEt = layout.nameInLayout
            val name = nameEt.editText?.text.toString()
            quizViewModel.saveQuiz(name)
        }.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_score_to_categories)
                }
            })

    }

}