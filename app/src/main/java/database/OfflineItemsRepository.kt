
import database.ProjectFitnessDao
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessWorkoutEntity
import database.ProjectFitnessWorkoutWithExercises
import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ProjectFitnessDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<ProjectFitnessWorkoutEntity>> {
        return itemDao.getAllItemsStream() // Tüm öğelerin akışını döndürür
    }

    override fun getItemStream(id: Int): Flow<ProjectFitnessWorkoutEntity?> {
        return itemDao.getItemStream(id) // Belirli bir öğenin akışını döndürür
    }

    override fun getWorkoutWithExercises(workoutName: String): List<ProjectFitnessWorkoutWithExercises?> {
        return itemDao.getWorkoutWithExercises(workoutName)
    }

    override fun getExerciseList(workoutId: Int): List<ProjectFitnessExerciseEntity> {
        return itemDao.getExerciseList(workoutId)
    }


    override fun getWorkoutId(workoutName: String): List<ProjectFitnessWorkoutEntity?> {
        return itemDao.getWorkoutId(workoutName)
    }

    override fun findWorkoutByName(name: String): List<ProjectFitnessWorkoutEntity?> {
        return itemDao.findWorkoutByName(name)
    }

    override fun getUpdatedItem(ids: Int): List<ProjectFitnessExerciseEntity> {
        TODO("Not yet implemented")
    }

    override fun getSetRepList(exerciseId : Int): List<ProjectFitnessExerciseEntity> {
        return itemDao.getSetRepList(exerciseId)
    }


    override suspend fun insertItem(item: ProjectFitnessWorkoutEntity) = itemDao.insert(item)
    override suspend fun insertItems(item: ProjectFitnessExerciseEntity) = itemDao.inserts(item)

    override suspend fun deleteItem(item: ProjectFitnessWorkoutEntity) = itemDao.delete(item)

    override suspend fun updateItem(item: ProjectFitnessWorkoutEntity) = itemDao.update(item)
    override suspend fun updatesItem(items: ProjectFitnessExerciseEntity) = itemDao.updates(items)


}