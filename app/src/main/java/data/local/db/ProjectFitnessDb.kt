package com.example.projectfitness.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.projectfitness.data.local.dao.ExerciseCatalogDao
import com.example.projectfitness.data.local.dao.WorkoutDao
import com.example.projectfitness.data.local.entity.ExerciseCatalogEntity
import com.example.projectfitness.data.local.entity.SetEntity
import com.example.projectfitness.data.local.entity.WorkoutEntity
import com.example.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.example.projectfitness.data.local.typeconverter.StringListConverter

@Database(
    entities = [WorkoutEntity::class, WorkoutExerciseEntity::class, SetEntity::class, ExerciseCatalogEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class ProjectFitnessDb : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseCatalogDao(): ExerciseCatalogDao
}