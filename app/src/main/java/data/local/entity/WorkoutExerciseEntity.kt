// WorkoutExerciseEntity.kt
package com.example.projectfitness.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName // Bu importu ekle
import java.util.UUID

@Entity(
    tableName = "exercise",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["workoutId"],
            childColumns = ["workoutOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutOwnerId")]
)
data class WorkoutExerciseEntity(
    @PrimaryKey
    var exerciseId: String = UUID.randomUUID().toString(),

    @get:PropertyName("workoutId")
    @set:PropertyName("workoutId")
    var workoutOwnerId: String = "", // Varsayılan değer MUTLAKA olmalı

    var exerciseName: String = "",   // Varsayılan değer MUTLAKA olmalı

    var catalogExerciseId: String? = null
)