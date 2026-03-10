package com.grozzbear.projectfitness.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grozzbear.projectfitness.data.local.dao.ExerciseCatalogDao
import com.grozzbear.projectfitness.data.local.dao.WorkoutDao
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.grozzbear.projectfitness.data.local.util.Converters
import data.local.entity.ExerciseLogEntity
import data.local.entity.SetLogEntity
import data.local.entity.WorkoutHistoryEntity

@Database(
    entities = [WorkoutEntity::class, WorkoutExerciseEntity::class, SetEntity::class, ExerciseCatalogEntity::class, ExerciseLogEntity::class, SetLogEntity::class, WorkoutHistoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ProjectFitnessDb : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseCatalogDao(): ExerciseCatalogDao
}