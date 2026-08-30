package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import data.local.entity.ExerciseLogWithSets
import data.remote.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutCompleteAnalysisScreenViewModel(
    private val repo: WorkoutRepository,
    userRepository: UserRepository
) : ViewModel() {
    val _activeWorkoutId = MutableStateFlow("")
    private val _exerciseList = MutableStateFlow<List<ExerciseLogWithSets>>(emptyList())
    val exerciseList: StateFlow<List<ExerciseLogWithSets>> = _exerciseList
    private val _muscleDistribution = MutableStateFlow<Map<String, Int>>(emptyMap())
    private val _ratioDistribution = MutableStateFlow<Map<String, Float>>(emptyMap())
    val ratioDistribution: StateFlow<Map<String, Float>> = _ratioDistribution
    private val _totalSetCount = MutableStateFlow(0)

    fun setWorkoutList() {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = _activeWorkoutId.value
            if (sessionId.isEmpty()) return@launch

            val workoutHistoryFull = repo.observeHistoricalWorkoutExercise(sessionId)
            val exercises = workoutHistoryFull.exerciseWithSets
            _exerciseList.value = exercises

            val (distribution, totalSets) = buildMuscleDistribution(exercises)
            _muscleDistribution.value = distribution
            _totalSetCount.value = totalSets
            _ratioDistribution.value = buildRatioDistribution(distribution)
        }
    }

    /** Kept for call sites that still invoke these after [setWorkoutList]. */
    fun calculateMuscleDistribution() {
        viewModelScope.launch(Dispatchers.Default) {
            val (distribution, totalSets) = buildMuscleDistribution(_exerciseList.value)
            _muscleDistribution.value = distribution
            _totalSetCount.value = totalSets
        }
    }

    fun calculateRatioDistribution() {
        viewModelScope.launch(Dispatchers.Default) {
            _ratioDistribution.value = buildRatioDistribution(_muscleDistribution.value)
        }
    }

    private fun buildMuscleDistribution(
        exercises: List<ExerciseLogWithSets>
    ): Pair<Map<String, Int>, Int> {
        if (exercises.isEmpty()) return emptyMap<String, Int>() to 0

        val distributionMap = mutableMapOf<String, Int>()
        var totalSetCount = 0

        exercises.forEach { eachExerciseWithSets ->
            val primaryMuscle = eachExerciseWithSets.exerciseLog.bodyPart
            val secondaryMuscles = eachExerciseWithSets.exerciseLog.secondaryMuscles
            val setsCount = eachExerciseWithSets.setLogs.count { it.clicked }
                .takeIf { it > 0 }
                ?: eachExerciseWithSets.setLogs.size

            if (primaryMuscle.isNotBlank()) {
                distributionMap[primaryMuscle] =
                    distributionMap.getOrDefault(primaryMuscle, 0) + setsCount
                totalSetCount += setsCount
            }

            secondaryMuscles.forEach { muscle ->
                if (muscle.isNotBlank()) {
                    distributionMap[muscle] =
                        distributionMap.getOrDefault(muscle, 0) + setsCount
                    totalSetCount += setsCount
                }
            }
        }

        return distributionMap to totalSetCount
    }

    private fun buildRatioDistribution(currentDistribution: Map<String, Int>): Map<String, Float> {
        val total = currentDistribution.values.sum()
        if (currentDistribution.isEmpty() || total <= 0) return emptyMap()

        return currentDistribution.mapValues { (_, count) ->
            (count.toFloat() / total) * 100f
        }
    }
}
