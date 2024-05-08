package database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverters

@Entity(tableName = "project_workout_room")
@TypeConverters(ExerciseListConverter::class)
data class ProjectFitnessWorkoutEntity (
    @PrimaryKey (autoGenerate = true)
    val workoutId : Int = 0 ,
    val workoutName: String,
    val exercises: MutableList<Exercise>,
)

@Entity(tableName = "project_exercise_room",
    foreignKeys = [ForeignKey(
        entity = ProjectFitnessWorkoutEntity::class,
        parentColumns = ["workoutId"],
        childColumns = ["exerciseId"],
        onDelete = ForeignKey.CASCADE)])

@TypeConverters(SetListConverter::class)
data class ProjectFitnessExerciseEntity(
    @PrimaryKey (autoGenerate = true)
    val ids : Int = 0,
    val exerciseId: Int,
    val exercisesName: String,
    var exercisesRep: Int,
    var exercisesSet: Int,
    val setrepList: MutableList<SetRep>
)

data class ProjectFitnessWorkoutWithExercises(
    @Embedded val workout: ProjectFitnessWorkoutEntity,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "exerciseId"
    )
    val exercises2: List<ProjectFitnessExerciseEntity>
)



