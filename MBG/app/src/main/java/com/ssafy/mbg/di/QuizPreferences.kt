package com.ssafy.mbg.di

import android.content.Context

object QuizPreferences  {

    private const val PREF_NAME = "quiz_preferences"
    private const val KEY_QUIZ_COMPLETED = "is_quiz_completed"

    fun setQuizCompleted(context: Context, isCompleted: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_QUIZ_COMPLETED, isCompleted)
            .apply()
    }

    fun isQuizCompleted(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_QUIZ_COMPLETED, false)
    }
}