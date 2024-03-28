
import database.ProjectFitnessWorkoutEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemsRepository {
    /**
     * Retrieve all the items from the given data source.
     */
    fun getAllItemsStream(): Flow<List<ProjectFitnessWorkoutEntity>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<ProjectFitnessWorkoutEntity?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: ProjectFitnessWorkoutEntity)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: ProjectFitnessWorkoutEntity)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: ProjectFitnessWorkoutEntity)
}