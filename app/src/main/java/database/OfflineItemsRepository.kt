
import database.ProjectCoachEntity
import database.ProjectCompletedExerciseEntity
import database.ProjectCompletedSetting
import database.ProjectCompletedWorkoutEntity
import database.ProjectFitnessChallanges
import database.ProjectFitnessDao
import database.ProjectFitnessExerciseEntity
import database.ProjectFitnessExercises
import database.ProjectFitnessUser
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
    override suspend fun deleteSetRepList(item: ProjectFitnessExerciseEntity) = itemDao.deleteSetRepList(item)

    override suspend fun updateItem(item: ProjectFitnessWorkoutEntity) = itemDao.update(item)
    override suspend fun updatesItem(items: ProjectFitnessExerciseEntity) = itemDao.updates(items)

    override suspend fun updateSetRepList(item: ProjectFitnessExerciseEntity) = itemDao.updates(item)

    // CompletedWorkoutDAO -------------------------------------------------------------------------

    override suspend fun insertCompletedWorkout(completedWorkout: ProjectCompletedWorkoutEntity) {
        itemDao.insertCompletedWorkout(completedWorkout)
    }

    override fun getCompletedWorkoutById(id: Int): Flow<ProjectCompletedWorkoutEntity?> {
        return itemDao.getCompletedWorkoutById(id)
    }

    override fun getCompletedWorkoutsByWorkoutId(workoutId: Int): Flow<List<ProjectCompletedWorkoutEntity>> {
        return itemDao.getCompletedWorkoutsByWorkoutId(workoutId)
    }

    override fun getCmdWorkoutsByWorkoutId(workoutId: Int): Flow<List<ProjectCompletedWorkoutEntity>> {
        return itemDao.getCmdWorkoutsByWorkoutId(workoutId)
    }

    override fun getAllCompletedWorkouts(): Flow<List<ProjectCompletedWorkoutEntity>> {
        return itemDao.getAllCompletedWorkouts()
    }

    override suspend fun deleteCompletedWorkout(completedWorkout: ProjectCompletedWorkoutEntity) {
        itemDao.deleteCompletedWorkout(completedWorkout)
    }

    // ---------------------------------------------------------------------------------------------
    override suspend fun insertCompletedSetting(savedcompletedWorkoutId: ProjectCompletedSetting) {
        itemDao.insertCompletedSetting(savedcompletedWorkoutId)
    }

    override fun getAllCompletedSetting(): Flow<List<ProjectCompletedSetting>> {
        return itemDao.getAllCompletedSetting()
    }

    override suspend fun updateCompletedWorkout(rate : Int , notes : String , completedWorkoutId: Int) {
        itemDao.updateCompletedWorkout(rate , notes , completedWorkoutId)
    }

    // Completed Exercise DAO Repo --------------------------------------------------------

    override suspend fun insertCompletedExercise(completedExercise: ProjectCompletedExerciseEntity) {
        itemDao.insertCompletedExercise(completedExercise)
    }

    override fun getAllItemsCompleted(completedWorkoutId : Int): Flow<List<ProjectCompletedExerciseEntity>> {
        return itemDao.getAllItemsCompleted(completedWorkoutId) // Tüm öğelerin akışını döndürür
    }

    override fun completedGetSetRep(exerciseId: Int): List<ProjectCompletedExerciseEntity> {
        return itemDao.completedGetSetRep(exerciseId)
    }

    // Project Fitness Exercise DAO Repo --------------------------------------------------------

    override suspend fun insertProjectExercise(projectfitnessexercise: ProjectFitnessExercises) {
        itemDao.insertProjectExercise(projectfitnessexercise)
    }

    override fun getProjectFitnessExercises(): Flow<List<ProjectFitnessExercises>>  {
        return itemDao.getProjectFitnessExercises() // Tüm öğelerin akışını döndürür
    }

    // Project Fitness Challange DAO Repo --------------------------------------------------------

    override suspend fun insertProjectChallanges(projectfitnesschallanges: ProjectFitnessChallanges) {
        itemDao.insertProjectChallanges(projectfitnesschallanges)
    }

    override fun getProjectFitnessChallanges(): Flow<List<ProjectFitnessChallanges>>  {
        return itemDao.getProjectFitnessChallanges() // Tüm öğelerin akışını döndürür
    }

    // Project Fitness Coach DAO Repo --------------------------------------------------------

    override suspend fun insertProjectCoach(projectfitnesscoach: ProjectCoachEntity) {
        itemDao.insertProjectCoach(projectfitnesscoach)
    }

    override fun getProjectFitnessCoach(): Flow<List<ProjectCoachEntity>>  {
        return itemDao.getProjectFitnessCoach() // Tüm öğelerin akışını döndürür
    }

    // Project Fitness User DAO Repo --------------------------------------------------------

    override suspend fun insertProjectUser(projectfitnessuser: ProjectFitnessUser) {
        itemDao.insertProjectUser(projectfitnessuser)
    }

    override fun getProjectFitnessUser(): Flow<List<ProjectFitnessUser>>  {
        return itemDao.getProjectFitnessUser() // Tüm öğelerin akışını döndürür
    }

    override suspend fun updateProjectUser(userremember : Boolean) {
        itemDao.updateProjectUser(userremember)
    }

    override suspend fun deleteProjectUser(projectfitnessuser : ProjectFitnessUser)
    = itemDao.deleteProjectUser(projectfitnessuser)

}