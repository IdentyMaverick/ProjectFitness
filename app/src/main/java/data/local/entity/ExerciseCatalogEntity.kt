package com.example.projectfitness.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_catalog")
data class ExerciseCatalogEntity(
    @PrimaryKey val id: String = "", // Firestore docId
    val name: String = "",
    val bodyPart: String = "",
    val equipment: String = "",
    val movementType: String = "",
    val secondaryMuscles: List<String> = emptyList(), // TypeConverter ile
    val isActive: Boolean = false
)
