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


    @Delete
    suspend fun delete(item: ProjectFitnessWorkoutEntity)


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
    fun getSetRepList(exerciseId : Int): List<ProjectFitnessExerciseEntity>

}