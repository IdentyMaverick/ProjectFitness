package data.local.viewmodel

import androidx.compose.runtime.mutableStateOf
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
    private val userRepository: UserRepository
) : ViewModel() {
    val _activeWorkoutId = MutableStateFlow("")
    private val _exerciseList = MutableStateFlow<List<ExerciseLogWithSets>>(emptyList())
    val exerciseList: StateFlow<List<ExerciseLogWithSets>> = _exerciseList
    val _muscleDistribution = MutableStateFlow<Map<String, Int>>(emptyMap())
    val _ratioDistribution = MutableStateFlow<Map<String, Float>>(emptyMap())
    val ratioDistribution: StateFlow<Map<String, Float>> = _ratioDistribution
    val _totalSetCount = MutableStateFlow(0)
    val totalSetCount: StateFlow<Int> = _totalSetCount


    fun setWorkoutList() {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = _activeWorkoutId.value
            if (sessionId.isNotEmpty()) {
                val workoutHistoryFull = repo.observeHistoricalWorkoutExercise(sessionId)
                _exerciseList.value = workoutHistoryFull.exerciseWithSets
            }
        }
    }

    fun calculateMuscleDistribution() {
        viewModelScope.launch(Dispatchers.Default) {
            val exercises = _exerciseList.value
            val totalSetCount = mutableStateOf(0)
            if (exercises.isEmpty()) return@launch

            val distributionMap = mutableMapOf<String, Int>()

            exercises.forEach { eachExerciseWithSets ->
                val primaryMuscle = eachExerciseWithSets.exerciseLog.bodyPart
                val secondaryMuscles = eachExerciseWithSets.exerciseLog.secondaryMuscles
                val setsCount = eachExerciseWithSets.setLogs.size

                if (primaryMuscle.isNotBlank()) {
                    distributionMap[primaryMuscle] =
                        distributionMap.getOrDefault(primaryMuscle, 0) + setsCount
                    totalSetCount.value += setsCount
                }

                secondaryMuscles.forEach { muscle ->
                    if (muscle.isNotBlank()) {
                        distributionMap[muscle] =
                            distributionMap.getOrDefault(muscle, 0) + setsCount
                        totalSetCount.value += setsCount
                    }
                }
            }

            _muscleDistribution.value = distributionMap
            _totalSetCount.value = totalSetCount.value
        }

    }


    fun calculateRatioDistribution() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentDistribution = _muscleDistribution.value

            val total = currentDistribution.values.sum()

            if (currentDistribution.isNotEmpty() && total > 0) {
                val calculatedRatios = currentDistribution.mapValues { (_, count) ->
                    (count.toFloat() / total) * 100f
                }

                _ratioDistribution.value = calculatedRatios
            }
        }
    }
}