package database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "project_workout_room")
@TypeConverters(ExerciseListConverter::class)
data class ProjectFitnessWorkoutEntity (
    @PrimaryKey (autoGenerate = true)  val id : Int = 0,
    @ColumnInfo(name = "workout_name") val workoutName: String,
    @ColumnInfo(name = "exercises") val exercises: MutableList<Exercise>,
)


@Entity(tableName = "project_exercise_room")
data class ProjectFitnessExerciseEntity(
    @PrimaryKey (autoGenerate = true)  val id2 : Int = 0,
    @ColumnInfo(name = "exercises_name") val exercisesName: String,
    @ColumnInfo(name = "exercises_rep") val exercisesRep: Int,
    @ColumnInfo(name = "exercises_set") val exercisesSet: Int
)