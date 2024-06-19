package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectFitnessDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ProjectFitnessWorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun inserts(item: ProjectFitnessExerciseEntity)

    @Update
    suspend fun update(item: ProjectFitnessWorkoutEntity)

    @Update
    suspend fun updates(item: ProjectFitnessExerciseEntity)

    @Update
    suspend fun updateSetRepList(item: ProjectFitnessExerciseEntity)

    @Delete
    suspend fun delete(item: ProjectFitnessWorkoutEntity)

    @Delete
    suspend fun deleteSetRepList(item: ProjectFitnessExerciseEntity)

    @Query("SELECT * from project_workout_room WHERE workoutId=:id")
    fun getItemStream(id: Int): Flow<ProjectFitnessWorkoutEntity?> // Belirli bir öğenin akışını döndüren fonksiyon

    @Query("SELECT * from project_workout_room WHERE workoutId != 1")
    fun getAllItemsStream(): Flow<List<ProjectFitnessWorkoutEntity>> // Tüm öğelerin akışını döndüren fonksiyon

    @Query("SELECT * from project_workout_room WHERE workoutName =:name")
    fun findWorkoutByName(name: String): List<ProjectFitnessWorkoutEntity>

    @Transaction
    @Query("SELECT * FROM project_workout_room,project_exercise_room WHERE workoutId = exerciseId and workoutName = :workoutName")
    fun getWorkoutWithExercises(workoutName: String): List<ProjectFitnessWorkoutWithExercises>

    @Query("SELECT * from project_exercise_room WHERE exerciseId = :workoutId")
    fun getExerciseList(workoutId: Int): List<ProjectFitnessExerciseEntity>

    @Query("SELECT * from project_workout_room WHERE workoutName = :workoutName ")
    fun getWorkoutId(workoutName: String): List<ProjectFitnessWorkoutEntity>

    @Query("SELECT * from project_exercise_room WHERE ids > 2 and exerciseId = :exerciseId")
    fun getSetRepList(exerciseId: Int): List<ProjectFitnessExerciseEntity>

    // CompletedWorkoutDAO -------------------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedWorkout(completedWorkout: ProjectCompletedWorkoutEntity)

    @Query("SELECT * FROM project_completed_workout WHERE completedWorkoutId = :id")
    fun getCompletedWorkoutById(id: Int): Flow<ProjectCompletedWorkoutEntity?>

    @Query("SELECT * FROM project_completed_workout WHERE workoutId = :workoutId")
    fun getCompletedWorkoutsByWorkoutId(workoutId: Int): Flow<List<ProjectCompletedWorkoutEntity>>

    @Query("SELECT * FROM project_completed_workout WHERE completedWorkoutId = :workoutId")
    fun getCmdWorkoutsByWorkoutId(workoutId: Int): Flow<List<ProjectCompletedWorkoutEntity>>

    @Query("SELECT * FROM project_completed_workout")
    fun getAllCompletedWorkouts(): Flow<List<ProjectCompletedWorkoutEntity>>

    @Delete
    suspend fun deleteCompletedWorkout(completedWorkout: ProjectCompletedWorkoutEntity)

    @Query("UPDATE project_completed_workout SET  rateOfWorkout = :rate , notesAboutWorkout = :notes WHERE completedWorkoutId = :completedWorkoutId")
    suspend fun updateCompletedWorkout(rate : Int , notes : String , completedWorkoutId: Int)

    @Query("UPDATE project_completed_workout SET  maxWorkoutVolume = :maxWorkoutVolume WHERE workoutId = :workoutId")
    suspend fun updateCompletedWorkoutVolume( workoutId: Int , maxWorkoutVolume : Int)

    // ProjectCompletedSettingDAO ------------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedSetting(savedcompletedWorkoutId: ProjectCompletedSetting)

    @Query("SELECT * FROM project_completed_setting")
    fun getAllCompletedSetting(): Flow<List<ProjectCompletedSetting>>

    //----------------------------------------------------------------------------------------------

    // Completed Exercise DAO --------------------------------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedExercise(completedExercise: ProjectCompletedExerciseEntity)

    @Query("SELECT * from project_completed_exercise WHERE completedWorkoutId = :completedWorkoutId")
    fun getAllItemsCompleted(completedWorkoutId : Int): Flow<List<ProjectCompletedExerciseEntity>> // Tüm öğelerin akışını döndüren fonksiyon

    @Query("SELECT * from project_completed_exercise WHERE exerciseName = :exerciseName ")
    fun getItemsCompleted(exerciseName : String): Flow<List<ProjectCompletedExerciseEntity>>

    @Query("UPDATE project_completed_exercise SET  maxExerciseVolume = :maxExerciseVolume WHERE completedWorkoutId = :completedworkoutId")
    suspend fun updateCompletedExerciseVolume( completedworkoutId: Int , maxExerciseVolume : Int)

    @Query("SELECT * FROM project_completed_exercise")
    fun getCompletedExercise(): Flow<List<ProjectCompletedExerciseEntity>>

    @Query("UPDATE project_completed_exercise SET  maxExerciseVolume = :maxExerciseVolume WHERE exerciseName = :exerciseName")
    suspend fun updateCompletedSameExerciseVolume( exerciseName: String , maxExerciseVolume : Int)

    //----------------------------------------------------------------------------------------------
    // Project Fitness Exercise DAO ----------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectExercise(projectfitnessexercise: ProjectFitnessExercises)

    @Query("SELECT * from project_fitness_exercises")
    fun getProjectFitnessExercises(): Flow<List<ProjectFitnessExercises>>

    // Project Fitness Challange DAO ---------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectChallanges(projectfitnesschallanges: ProjectFitnessChallanges)

    @Query("SELECT * from project_fitness_challanges")
    fun getProjectFitnessChallanges(): Flow<List<ProjectFitnessChallanges>>

    // Project Fitness Coach DAO ---------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectCoach(projectfitnesscoach: ProjectCoachEntity)

    @Query("SELECT * from project_fitness_coach")
    fun getProjectFitnessCoach(): Flow<List<ProjectCoachEntity>>

    // Project Fitness User DAO ---------------------------------------------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectUser(projectfitnesuser: ProjectFitnessUser)

    @Query("SELECT * from project_fitness_user")
    fun getProjectFitnessUser(): Flow<List<ProjectFitnessUser>>

    @Query("UPDATE project_fitness_user SET  userRemember = :userremember WHERE userId = 0")
    suspend fun updateProjectUser(userremember : Boolean)

    @Delete
    suspend fun deleteProjectUser(projectfitnessuser : ProjectFitnessUser)

    @Query("SELECT * from project_completed_exercise WHERE  completedWorkoutId = :exerciseId")
    fun completedGetSetRep(exerciseId: Int): List<ProjectCompletedExerciseEntity>
}
