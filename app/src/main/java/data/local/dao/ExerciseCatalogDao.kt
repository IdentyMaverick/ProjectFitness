package com.grozzbear.projectfitness.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseCatalogDao {

    @Query("SELECT * FROM exercise_catalog WHERE isActive = 1 ORDER BY bodyPart")
    fun observeAllActive(): Flow<List<ExerciseCatalogEntity>>

    @Query(
        """
        SELECT * FROM exercise_catalog 
        WHERE isActive = 1 
        AND name LIKE '%' || :q || '%'
        ORDER BY bodyPart
    """
    )
    fun observeSearch(q: String): Flow<List<ExerciseCatalogEntity>>

    @Upsert
    suspend fun upsertAll(items: List<ExerciseCatalogEntity>)

    @Query("SELECT COUNT(*) FROM exercise_catalog")
    suspend fun count(): Int
}
