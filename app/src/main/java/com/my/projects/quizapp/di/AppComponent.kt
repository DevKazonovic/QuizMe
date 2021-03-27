package com.my.projects.quizapp.di

import android.app.Application
import android.content.Context
import com.my.projects.quizapp.di.module.*
import com.my.projects.quizapp.presentation.history.detail.QuizDetailFragment
import com.my.projects.quizapp.presentation.history.list.FilterDialogFragment
import com.my.projects.quizapp.presentation.history.list.HistoryFragment
import com.my.projects.quizapp.presentation.quiz.playground.QuizPlayGroundFragment
import com.my.projects.quizapp.presentation.quiz.score.QuizScoreFragment
import com.my.projects.quizapp.presentation.quiz.setting.QuizSettingFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DatabaseModule::class,
        ViewModelFactoryModule::class,
        ViewModelModule::class,
        PreferenceModule::class
    ]
)
interface AppComponent {


    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @BindsInstance context: Context
        ): AppComponent
    }

    fun inject(fragment: QuizScoreFragment)
    fun inject(playGroundFragment: QuizPlayGroundFragment)
    fun inject(fragment: QuizSettingFragment)
    fun inject(fragment: HistoryFragment)
    fun inject(fragment: QuizDetailFragment)
    fun inject(fragment: FilterDialogFragment)

}