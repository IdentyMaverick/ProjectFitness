package com.example.projectfitness.data.remote

import com.example.projectfitness.data.local.entity.ExerciseCatalogEntity


data class Workoutin(
    val name: String = "",
    val bodyPart: String = "",
    val equipment: String = "",
    val secondaryMuscles: List<String> = emptyList(),
    val movementType: String = "",
    val isActive: Boolean = true
)

fun Workoutin.toEntity(id: String) : ExerciseCatalogEntity {
    return ExerciseCatalogEntity(
        id = id,
        name = name,
        bodyPart = bodyPart,
        equipment = equipment,
        movementType = movementType,
        secondaryMuscles = secondaryMuscles,
        isActive = isActive
    )
}
