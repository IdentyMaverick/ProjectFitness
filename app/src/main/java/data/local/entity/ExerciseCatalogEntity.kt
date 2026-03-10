package com.grozzbear.projectfitness.data.local.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Keep
@Entity(tableName = "exercise_catalog")
data class ExerciseCatalogEntity(
    @PrimaryKey val id: String = "",
    val instructions: String = "",
    val level: String = "",
    @get:PropertyName("gifUrl")
    @set:PropertyName("gifUrl")
    var gifUrl: String? = null,
    val name: String = "",
    val bodyPart: String = "",
    val equipment: String = "",
    val movementType: String = "",
    val secondaryMuscles: List<String> = emptyList(),
    val isActive: Boolean = false
)
