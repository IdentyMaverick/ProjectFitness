package com.grozzbear.projectfitness.data.remote

import androidx.annotation.Keep
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity

@Keep
data class Workoutin(
    val instructions: String = "",
    val level: String = "",
    val gifUrl: String? = null,
    val name: String = "",
    val bodyPart: String = "",
    val equipment: String = "",
    val secondaryMuscles: List<String> = emptyList(),
    val movementType: String = "",
    val isActive: Boolean = true,
    val exerciseImage: String = ""
)

fun Workoutin.toEntity(id: String): ExerciseCatalogEntity {
    return ExerciseCatalogEntity(
        id = id,
        instructions = instructions,
        level = level,
        gifUrl = gifUrl,
        name = name,
        bodyPart = bodyPart,
        equipment = equipment,
        movementType = movementType,
        secondaryMuscles = secondaryMuscles,
        isActive = isActive
    )
}
