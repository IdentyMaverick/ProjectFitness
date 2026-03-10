package com.grozzbear.projectfitness.data.local.db

import android.content.Context
import androidx.room.Room

object DbProvider {
    @Volatile
    private var INSTANCE: ProjectFitnessDb? = null

    fun get(context: Context): ProjectFitnessDb =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ProjectFitnessDb::class.java,
                "project_fitness.db"
            ).build().also { INSTANCE = it }
        }
}