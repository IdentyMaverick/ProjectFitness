package com.grozzbear.projectfitness.data.local.entity

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Keep
@Entity(tableName = "workout")
data class WorkoutEntity(
    @PrimaryKey val workoutId: String = "",
    val workoutName: String = "",
    val workoutType: String = "",
    val workoutRating: Int = 0,
    val ownerUid: String? = "",
    val syncState: Boolean = false,
    val image: Int = 0
)

@Keep
data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "workoutOwnerId"
    )
    val exercises: List<WorkoutExerciseEntity>
)

@Keep
data class ExerciseWithSets(
    @Embedded val exercise: WorkoutExerciseEntity,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "exerciseOwnerId"
    )
    val sets: List<SetEntity>
)

@Keep
data class WorkoutFull(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        entity = WorkoutExerciseEntity::class,
        parentColumn = "workoutId",
        entityColumn = "workoutOwnerId"
    )
    val exercises: List<ExerciseWithSets>
)

@Entity(
    tableName = "workout_exercise_crossref",
    primaryKeys = ["workoutId", "exerciseId"]
)
@Keep
data class WorkoutExerciseCrossRef(
    val workoutId: String,
    val exerciseId: String,
    val orderIndex: Int = 0
)

@Keep
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
