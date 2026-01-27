package com.example.projectfitness.data.local.dao

import android.R
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.projectfitness.data.local.entity.SetEntity
import com.example.projectfitness.data.local.entity.WorkoutEntity
import com.example.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.example.projectfitness.data.local.entity.WorkoutFull
import com.example.projectfitness.data.local.entity.WorkoutWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllWorkouts(workoutEntityList: List<WorkoutEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: WorkoutExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Transaction
    @Query("SELECT * FROM workout WHERE workoutType != 'User' ORDER BY workoutId DESC")
    fun observeWorkouts(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workout WHERE workoutId = :id")
    fun observeWorkoutFull(id: String): Flow<WorkoutFull>

    @Transaction
    @Query("SELECT * FROM workout WHERE workoutId = :id")
    fun observeMySelectedWorkout(id: String): Flow<WorkoutWithExercises>

    @Query("DELETE FROM workout WHERE workoutId = :id")
    suspend fun deleteWorkout(id: String)

    @Query("SELECT COUNT(*) FROM workout")
    suspend fun workoutCount(): Int

    @Query("SELECT * FROM workout WHERE ownerUid = :uid")
    fun observeMyWorkouts(uid: String): Flow<List<WorkoutEntity>>

    @Query("DELETE FROM exercise WHERE exerciseId = :exerciseId")
    suspend fun deleteSelectedExercise(exerciseId: String)

    @Query("UPDATE workout SET syncState = 0 WHERE workoutId = :workoutId")
    suspend fun touchWorkout(workoutId: String)

}