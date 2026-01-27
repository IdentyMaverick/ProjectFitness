package com.example.projectfitness.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "workout")
data class WorkoutEntity(
    @PrimaryKey val workoutId: String = "",
    val workoutName: String = "",
    val workoutType: String = "",
    val workoutRating: Int = 0,
    val ownerUid: String? = "",
    val syncState: Boolean = false
)

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "workoutOwnerId"
    )
    val exercises: List<WorkoutExerciseEntity>
)

data class ExerciseWithSets(
    @Embedded val exercise: WorkoutExerciseEntity,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "exerciseOwnerId"
    )
    val sets: List<SetEntity>
)

data class WorkoutFull(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        entity = WorkoutExerciseEntity::class,
        parentColumn = "workoutId",
        entityColumn = "workoutOwnerId"
    )
    val exercises: List<ExerciseWithSets>
)

// GLOBAL EXERCISES

@Entity(
    tableName = "workout_exercise_crossref",
    primaryKeys = ["workoutId", "exerciseId"]
)
data class WorkoutExerciseCrossRef(
    val workoutId: String,   // WorkoutEntity.workoutId
    val exerciseId: String,  // ExerciseCatalogEntity.id (Firestore docId)
    val orderIndex: Int = 0  // opsiyonel: workout içinde sıra
)

data class WorkoutWithCatalogExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "id",
        associateBy = Junction(
            value = WorkoutExerciseCrossRef::class,
            parentColumn = "workoutId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<ExerciseCatalogEntity>
)
