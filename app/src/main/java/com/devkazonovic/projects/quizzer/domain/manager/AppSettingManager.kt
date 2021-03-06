package com.devkazonovic.projects.quizzer.domain.manager

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun getCurrentAppTheme(): Boolean = sharedPreferences.getBoolean(KEY_APP_THEME, false)

    fun getCountDownTimer(): Short =
        (sharedPreferences.getString(KEY_COUNT_DOWN_TIMER_PERIOD, "60")
            ?.toShort() ?: 60)

    fun getNumberOfQuestions(): Int =
        sharedPreferences.getString(KEY_NUMBER_OF_QUESTIONS, "10")?.toInt() ?: 10


    companion object {
        const val KEY_COUNT_DOWN_TIMER_PERIOD = "KEY_COUNT_DOWN_TIMER_PERIOD"
        const val KEY_NUMBER_OF_QUESTIONS = "KEY_NUMBER_OF_QUESTIONS"
        const val KEY_APP_THEME = "KEY_APP_THEME"
    }
}