package com.devkazonovic.projects.quizzer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int,
    val name: String,
    val icon: Int
) : Parcelable