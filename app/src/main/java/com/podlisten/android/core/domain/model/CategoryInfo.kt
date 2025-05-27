package com.podlisten.android.core.domain.model

import com.podlisten.android.core.data.database.model.Category

data class CategoryInfo(
    val id: Long,
    val name: String,
)

const val CategoryTechnology = "Technology"

fun Category.asExternalModel(): CategoryInfo =
    CategoryInfo(
        id = id,
        name = name
    )
