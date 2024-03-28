package database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface ProjectFitnessDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ProjectFitnessWorkoutEntity)
    @Update
    suspend fun update(item: ProjectFitnessWorkoutEntity)

    @Delete
    suspend fun delete(item: ProjectFitnessWorkoutEntity)


    @Query("SELECT * from project_workout_room WHERE id=:id")
    fun getItemStream(id: Int): Flow<ProjectFitnessWorkoutEntity?> // Belirli bir öğenin akışını döndüren fonksiyon

    @Query("SELECT * from project_workout_room")
    fun getAllItemsStream(): Flow<List<ProjectFitnessWorkoutEntity>> // Tüm öğelerin akışını döndüren fonksiyon

}