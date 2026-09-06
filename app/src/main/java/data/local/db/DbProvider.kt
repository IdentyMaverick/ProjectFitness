package com.grozzbear.projectfitness.data.local.db

import android.content.Context
import androidx.room.Room

object DbProvider {
    @Volatile
    private var instance: ProjectFitnessDb? = null

    fun get(context: Context): ProjectFitnessDb = instance ?: synchronized(this) {
        instance ?: Room
            .databaseBuilder(
                context.applicationContext,
                ProjectFitnessDb::class.java,
                "project_fitness.db",
            ).build()
            .also { instance = it }
    }
}
