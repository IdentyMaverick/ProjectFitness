package database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [ProjectFitnessWorkoutEntity::class], version = 1, exportSchema = false)
abstract class ProjectFitnessRoom : RoomDatabase() {
    abstract fun projectFitnessDao() : ProjectFitnessDao
    companion object {
        @Volatile
        private var projectFitnessInstance : ProjectFitnessRoom? = null

        fun getDatabase(context: Context): ProjectFitnessRoom {

            return projectFitnessInstance ?: synchronized(this) {

                Room.databaseBuilder(context, ProjectFitnessRoom::class.java, "project_fitness_room").fallbackToDestructiveMigration().build().also{ projectFitnessInstance = it}

            }
        }
    }
}