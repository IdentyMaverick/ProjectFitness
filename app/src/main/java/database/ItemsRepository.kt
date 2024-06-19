
import database.ProjectCoachEntity
import database.ProjectCompletedExerciseEntity
import database.ProjectCompletedSetting
import database.ProjectCompletedWorkoutEntity
import database.ProjectFitnessChallanges
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessExercises
import database.ProjectFitnessUser
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
    suspend fun deleteSetRepList(item: ProjectFitnessExerciseEntity)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: ProjectFitnessWorkoutEntity)
    suspend fun updatesItem(items: ProjectFitnessExerciseEntity)

    suspend fun updateSetRepList(item: ProjectFitnessExerciseEntity)

    // CompletedWorkoutDAO -------------------------------------------------------------------------

    suspend fun insertCompletedWorkout(completedWorkout: ProjectCompletedWorkoutEntity)

    fun getCompletedWorkoutById(id: Int): Flow<ProjectCompletedWorkoutEntity?>

    fun getCompletedWorkoutsByWorkoutId(workoutId: Int): Flow<List<ProjectCompletedWorkoutEntity>>

    fun getCmdWorkoutsByWorkoutId(workoutId: Int): Flow<List<ProjectCompletedWorkoutEntity>>

    fun getAllCompletedWorkouts(): Flow<List<ProjectCompletedWorkoutEntity>>

    suspend fun deleteCompletedWorkout(completedWorkout: ProjectCompletedWorkoutEntity)

    suspend fun updateCompletedWorkout(rate : Int , notes : String , completedWorkoutId: Int)

    suspend fun updateCompletedWorkoutVolume( workoutId : Int , maxWorkoutVolume : Int)

    // ---------------------------------------------------------------------------------------------

    suspend fun insertCompletedSetting(savedcompletedWorkoutId: ProjectCompletedSetting)

    fun getAllCompletedSetting(): Flow<List<ProjectCompletedSetting>>

    // Completed Exercise DAO ---------------------------------------------------------------------------------------------

    suspend fun insertCompletedExercise(completedExercise: ProjectCompletedExerciseEntity)

    fun getAllItemsCompleted(completedWorkoutId : Int): Flow<List<ProjectCompletedExerciseEntity>>

    fun getItemsCompleted(exerciseName : String): Flow<List<ProjectCompletedExerciseEntity>>

    fun completedGetSetRep(exerciseId: Int): List<ProjectCompletedExerciseEntity>

    suspend fun updateCompletedExerciseVolume( completedworkoutId: Int , maxExerciseVolume : Int)

    fun getCompletedExercise(): Flow<List<ProjectCompletedExerciseEntity>>

    suspend fun updateCompletedSameExerciseVolume( exerciseName : String , maxExerciseVolume : Int )

    // Project Fitness Exercise DAO ---------------------------------------------------------------------------------------------

    suspend fun insertProjectExercise(projectfitnessexercise: ProjectFitnessExercises)

    fun getProjectFitnessExercises(): Flow<List<ProjectFitnessExercises>>

    // Project Fitness Challange DAO ---------------------------------------------------------------------------------------------

    suspend fun insertProjectChallanges(projectfitnesschallanges: ProjectFitnessChallanges)

    fun getProjectFitnessChallanges(): Flow<List<ProjectFitnessChallanges>>

    // Project Fitness Coach DAO ---------------------------------------------------------------------------------------------

    suspend fun insertProjectCoach(projectfitnesscoach: ProjectCoachEntity)

    fun getProjectFitnessCoach(): Flow<List<ProjectCoachEntity>>

    // Project Fitness User DAO ---------------------------------------------------------------------------------------------

    suspend fun insertProjectUser(projectfitnessuser: ProjectFitnessUser)

    fun getProjectFitnessUser(): Flow<List<ProjectFitnessUser>>

    suspend fun updateProjectUser(userremember : Boolean)

    suspend fun deleteProjectUser(projectfitnessuser : ProjectFitnessUser)

}