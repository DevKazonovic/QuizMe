package com.devkazonovic.projects.quizzer.presentation.quiz.answers

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.devkazonovic.projects.quizzer.R
import com.devkazonovic.projects.quizzer.databinding.CardQuestionBinding
import com.devkazonovic.projects.quizzer.domain.model.Question
import com.devkazonovic.projects.quizzer.presentation.common.widgets.LogsRadioButtons.Companion.getCorrectRadio
import com.devkazonovic.projects.quizzer.presentation.common.widgets.LogsRadioButtons.Companion.getInCorrectRadio
import com.devkazonovic.projects.quizzer.presentation.common.widgets.LogsRadioButtons.Companion.getUserCorrectRadio
import com.devkazonovic.projects.quizzer.presentation.common.widgets.LogsRadioButtons.Companion.getUserInCorrectRadio
import com.devkazonovic.projects.quizzer.presentation.common.widgets.LogsRadioButtons.Companion.layoutParams

class QuestionsAdapter(
    private val questions: MutableList<Question>
) : RecyclerView.Adapter<QuestionsAdapter.QuestionsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = CardQuestionBinding.inflate(inflater, parent, false)
        return QuestionsViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuestionsViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int = questions.size

    class QuestionsViewHolder(
        var binding: CardQuestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val question: TextView = itemView.findViewById(R.id.txtview_cardquestion_question)
        private val context: Context = itemView.context

        fun bind(data: Question) {
            binding.txtviewCardquestionQuestion.text = data.question
            displayAnswers(binding, context, data)
        }

        private fun displayAnswers(
            binding: CardQuestionBinding,
            context: Context,
            data: Question
        ) {
            binding.radiogroupCardquestionAnswerchoices.clearCheck()
            binding.radiogroupCardquestionAnswerchoices.removeAllViews()
            var id = 0
            data.answers.forEach {
                if (it.isUser) {
                    if (it.isCorrect) {
                        binding.radiogroupCardquestionAnswerchoices.addView(
                            getUserCorrectRadio(context, it.answer, id), layoutParams
                        )
                        binding.imageviewCardquestionIconanswerstate.setImageResource(R.drawable.ic_round_correct)
                    } else {
                        binding.radiogroupCardquestionAnswerchoices.addView(
                            getUserInCorrectRadio(context, it.answer, id), layoutParams
                        )
                        binding.imageviewCardquestionIconanswerstate.setImageResource(R.drawable.ic_round_wrong)
                    }
                } else {
                    if (it.isCorrect) {
                        binding.radiogroupCardquestionAnswerchoices.addView(
                            getCorrectRadio(context, it.answer, id), layoutParams
                        )
                    } else {
                        binding.radiogroupCardquestionAnswerchoices.addView(
                            getInCorrectRadio(context, it.answer, id),
                            layoutParams
                        )
                    }
                }
                id++
            }
        }

    }


}