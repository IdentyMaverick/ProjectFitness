
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessWorkoutEntity
import database.ProjectFitnessWorkoutWithExercises
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemsRepository {
    /**
     * Retrieve items except id = 1 from the given data source.
     */
    fun getAllItemsStream(): Flow<List<ProjectFitnessWorkoutEntity>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<ProjectFitnessWorkoutEntity?>
    fun getWorkoutWithExercises(workoutName: String): List<ProjectFitnessWorkoutWithExercises?>
    fun findWorkoutByName(name: String): List<ProjectFitnessWorkoutEntity?>

    // ChooseExercise.kt'de bulunan her bir seçilen exercise'in set ve rep datalarını çeker
    fun getUpdatedItem(ids: Int): List<ProjectFitnessExerciseEntity>
    fun getExerciseList(workoutId: Int): List<ProjectFitnessExerciseEntity?>
    fun getWorkoutId(workoutName: String): List<ProjectFitnessWorkoutEntity?> // Workout id bulmak için isim sorgulama
    fun getSetRepList(exerciseId : Int): List<ProjectFitnessExerciseEntity> // SetRep Listesi döndürür


    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: ProjectFitnessWorkoutEntity)
    suspend fun insertItems(item: ProjectFitnessExerciseEntity)


    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: ProjectFitnessWorkoutEntity)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: ProjectFitnessWorkoutEntity)
    suspend fun updatesItem(items: ProjectFitnessExerciseEntity)

}