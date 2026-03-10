package com.grozzbear.projectfitness.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutFull
import com.grozzbear.projectfitness.data.local.entity.WorkoutWithExercises
import data.local.entity.ExerciseLogEntity
import data.local.entity.SetLogEntity
import data.local.entity.WorkoutHistoryEntity
import data.local.entity.WorkoutHistoryFull
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
    suspend fun insertHistoricalExercise(log: ExerciseLogEntity): Long

    @Query("SELECT * FROM exercise_logs WHERE logId = :exerciseId")
    suspend fun observeHistoricalExercise(exerciseId: String): ExerciseLogEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalSet(setLog: SetLogEntity): Long

    @Query("UPDATE set_logs SET log = :log WHERE logOwnerId = :logOwnerId AND setIndex = :setIndex")
    suspend fun updateSetNote(log: String, logOwnerId: Long, setIndex: Int = 0)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalWorkout(workout: WorkoutHistoryEntity)

    @Query("DELETE FROM workout_history WHERE sessionId = :sessionId")
    suspend fun deleteHistoricalWorkoutById(sessionId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoricalExercises(logs: List<ExerciseLogEntity>)

    @Delete
    suspend fun deleteHistoricalSet(set: SetLogEntity): Int

    @Query("UPDATE workout_history SET dateTimestamp = :dateTimestamp, totalDuration = :duration, syncState = :isSynced, isCompleted = :isCompleted WHERE sessionId = :id")
    suspend fun completeWorkout(
        id: String,
        dateTimestamp: Long,
        duration: Long,
        isSynced: Boolean = false,
        isCompleted: Boolean
    )

    @Query("UPDATE exercise_logs SET log = :log WHERE logId = :logId")
    suspend fun updateExerciseNote(logId: Long, log: String)

    @Transaction
    suspend fun saveWorkoutResult(history: WorkoutHistoryEntity, logs: List<ExerciseLogEntity>) {
        insertHistoricalWorkout(history)
        insertHistoricalExercises(logs)
    }

    @Transaction
    @Query("SELECT * FROM workout_history WHERE sessionId = :sessionId")
    fun observeWorkoutHistoryExerciseList(sessionId: String): WorkoutHistoryFull

    @Transaction
    @Query("SELECT * FROM workout_history ORDER BY dateTimestamp DESC")
    fun observeWorkoutHistory(): Flow<List<WorkoutHistoryFull>>

    @Query("SELECT * FROM workout_history WHERE ownerUid = :uid")
    fun observeWorkoutHistoryOther(uid: String): Flow<List<WorkoutHistoryFull>>

    @Transaction
    @Query("SELECT * FROM workout_history WHERE sessionId = :sessionId")
    fun observeWorkoutHistory(sessionId: String): WorkoutHistoryFull

    @Query("SELECT * FROM workout_history WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getWorkoutByIdDirect(sessionId: String): WorkoutHistoryEntity?

    @Transaction
    @Query("SELECT * FROM workout_history WHERE sessionId = :sessionId")
    fun observeWorkoutHistoryFull(sessionId: String): Flow<WorkoutHistoryFull>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: SetEntity): Long

    @Query("DELETE FROM exercise_set WHERE setId = :setId")
    suspend fun deleteSet(setId: String)

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

    @Query("UPDATE exercise SET exerciseImage = :imageName WHERE exerciseName = :name")
    suspend fun updateExerciseImage(name: String, imageName: String)

    @Query("UPDATE exercise SET exerciseImage = :imageUrl WHERE exerciseName = :name")
    suspend fun updateExerciseLogImage(name: String, imageUrl: String)

    @Query("UPDATE set_logs SET clicked = :isClicked WHERE setId = :setId")
    suspend fun updateSetLogClick(isClicked: Boolean, setId: Long)

    @Query("DELETE FROM set_logs WHERE setId IN (:setId)")
    suspend fun deleteMultipleSets(setId: List<Long>)

    @Update
    suspend fun updateSet(set: SetEntity)

    @Delete
    suspend fun deleteSet(set: SetEntity)
}