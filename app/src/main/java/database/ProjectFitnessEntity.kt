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
    var setrepList: MutableList<SetRep>
)

data class ProjectFitnessWorkoutWithExercises(
    @Embedded val workout: ProjectFitnessWorkoutEntity,
    @Relation(
        parentColumn = "workoutId",
        entityColumn = "exerciseId"
    )
    val exercises2: List<ProjectFitnessExerciseEntity>
)

@Entity(tableName = "project_completed_workout",
    foreignKeys = [ForeignKey(
        entity = ProjectFitnessWorkoutEntity::class,
        parentColumns = ["workoutId"],
        childColumns = ["workoutId"],
        onDelete = ForeignKey.CASCADE)])
@TypeConverters(DateTypeConverter::class)
data class ProjectCompletedWorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val completedWorkoutId : Int = 1000,
    val workoutId: Int,
    val completionDate : String,
    val durationMinutes: String,
    val totalSets: Int,
    val totalReps: Int,
    var rateOfWorkout : Int, // Rate between 1 to 5
    var notesAboutWorkout : String,
    var completedWorkoutName : String,
    val totalWorkoutVolume : Int,
    val maxWorkoutVolume : Int = 0
)

@Entity(
    tableName = "project_completed_exercise",
    foreignKeys = [ForeignKey(
        entity = ProjectCompletedWorkoutEntity::class,
        parentColumns = ["completedWorkoutId"],
        childColumns = ["completedWorkoutId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(SetListConverter::class)
data class ProjectCompletedExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val completedExerciseId: Int = 0,
    val completedWorkoutId: Int, // Completed Workout ile ilişki
    val exerciseName: String,
    val exerciseRep: Int,
    val exerciseSet: Int,
    var setrepListCompleted: MutableList<SetRep>,
    var totalExerciseVolume : Int,
    var maxExerciseVolume : Int = 0
)


@Entity(tableName = "project_completed_setting")
data class ProjectCompletedSetting(
    @PrimaryKey
    var  savedcompletedWorkoutId : Int
)

@Entity(tableName = "project_fitness_exercises")
data class ProjectFitnessExercises(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var exerciseName : String,
    var bodyPart : String,
    var secondaryMuscles : String
)

@Entity(tableName = "project_fitness_challanges")
data class ProjectFitnessChallanges(
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0,
    var challangeName : String ,
    var challangePhoto : Int ,
    var challangeDifficulty : Int
)

@Entity(tableName = "project_fitness_coach")
data class ProjectCoachEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0 ,
    var coachName : String ,
    var coachPhoto : Int ,
    var coachDifficulty : Int
)

@TypeConverters(ProjectUserConverter::class)
@Entity(tableName = "project_fitness_user")
data class ProjectFitnessUser(
    @PrimaryKey(autoGenerate = false)
    var userId : String ,
    var userName : String ,
    var userEmail : String ,
    var userPassword : String ,
    var userPhotoUri : String ,
    var userRemember : Boolean ,
    var nickName : String
)




