package com.example.projectfitness.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "exercise_set",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exerciseOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseOwnerId")]
)
data class SetEntity(
    @PrimaryKey
    val setId: String = "",
    val exerciseOwnerId: String, // WorkoutExerciseEntity.exerciseId (String)
    val reps: Int,
    val weight: Float,
    val note: String? = null
)