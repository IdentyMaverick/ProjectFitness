
import database.ProjectFitnessDao
import database.ProjectFitnessWorkoutEntity
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ProjectFitnessDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<ProjectFitnessWorkoutEntity>> {
        return itemDao.getAllItemsStream() // Tüm öğelerin akışını döndürür
    }

    override fun getItemStream(id: Int): Flow<ProjectFitnessWorkoutEntity?> {
        return itemDao.getItemStream(id) // Belirli bir öğenin akışını döndürür
    }
    override suspend fun insertItem(item: ProjectFitnessWorkoutEntity) = itemDao.insert(item)

    override suspend fun deleteItem(item: ProjectFitnessWorkoutEntity) = itemDao.delete(item)

    override suspend fun updateItem(item: ProjectFitnessWorkoutEntity) = itemDao.update(item)
}