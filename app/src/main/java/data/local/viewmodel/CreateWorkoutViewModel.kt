package data.local.viewmodel

import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grozzbear.projectfitness.data.local.entity.ExerciseCatalogEntity
import com.grozzbear.projectfitness.data.local.entity.SetEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutEntity
import com.grozzbear.projectfitness.data.local.entity.WorkoutExerciseEntity
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@Keep
data class ExerciseDraft(
    val catalogId: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val sets: List<SetDraft> = listOf(SetDraft(), SetDraft(), SetDraft())
)

@Keep
data class SetDraft(
    val reps: Int = 10,
    val sets: Float = 0f,
    val weight: Float = 0f
)

class CreateWorkoutViewModel(
    private val repo: WorkoutRepository
) : ViewModel() {

    val catalogWorkoutList = repo.getAllCatalogExercises()

    private val _selectedExerciseIds: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val selectedExerciseIds: StateFlow<Set<String>> = _selectedExerciseIds.asStateFlow()

    val selectedCatalogExercises: StateFlow<List<ExerciseCatalogEntity>> = combine(
        catalogWorkoutList,
        _selectedExerciseIds
    ) { list, ids ->
        list.filter { item ->
            ids.any { selectedId -> selectedId.trim() == item.id.trim() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _draftExercises = MutableStateFlow<List<ExerciseDraft>>(emptyList())
    val draftExercises: StateFlow<List<ExerciseDraft>> = _draftExercises.asStateFlow()

    fun addExercise(id: String) {
        _selectedExerciseIds.update { current -> current + id }
    }

    fun removeExercise(id: String) {
        _selectedExerciseIds.update { current -> current - id }
    }

    fun onConfirmSelection() {
        viewModelScope.launch {
            val allCatalog = catalogWorkoutList.first()
            val selectedIds = _selectedExerciseIds.value

            val selected = allCatalog.filter { item ->
                selectedIds.contains(item.id)
            }

            Log.d("CreateWorkout", "Seçili ID sayısı: ${selectedIds.size}")
            Log.d("CreateWorkout", "Eşleşen Katalog sayısı: ${selected.size}")

            if (selected.isEmpty() && selectedIds.isNotEmpty()) {
                Log.e("CreateWorkout", "Hata: Seçili ID'ler katalogdaki ID'lerle eşleşmiyor!")
                return@launch
            }

            val currentDrafts = _draftExercises.value.associateBy { it.catalogId }
            val merged = selected.map { e ->
                currentDrafts[e.id] ?: ExerciseDraft(
                    catalogId = e.id,
                    name = e.name,
                    bodyPart = e.bodyPart,
                    equipment = e.equipment,
                    sets = listOf(SetDraft(), SetDraft(), SetDraft())
                )
            }

            _draftExercises.value = merged
            Log.d("CreateWorkout", "Taslaklar güncellendi: ${merged.size} egzersiz hazır.")
        }
    }

    fun removeDraftExercise(catalogId: String) {
        _draftExercises.update { list -> list.filterNot { it.catalogId == catalogId } }
        _selectedExerciseIds.update { it - catalogId }
    }

    fun addSetToExercise(catalogId: String) {
        _draftExercises.update { list ->
            list.map { ex ->
                if (ex.catalogId == catalogId) ex.copy(sets = ex.sets + SetDraft()) else ex
            }
        }
    }

    fun removeSetToExercise(catalogId: String, setDraft: SetDraft) {
        _draftExercises.update { list ->
            list.map { ex ->
                if (ex.catalogId == catalogId) ex.copy(sets = ex.sets - setDraft) else ex
            }
        }
    }

    fun updateSet(catalogId: String, setIndex: Int, reps: Int? = null, weight: Float? = null) {
        _draftExercises.update { list ->
            list.map { ex ->
                if (ex.catalogId != catalogId) return@map ex
                val newSets = ex.sets.mapIndexed { i, s ->
                    if (i != setIndex) s
                    else s.copy(reps = reps ?: s.reps, weight = weight ?: s.weight)
                }
                ex.copy(sets = newSets)
            }
        }
    }

    fun saveWorkout(
        workoutId: String,
        workoutName: String,
        workoutType: String,
        workoutRating: Int,
        ownerUid: String?,
        syncState: Boolean,
        image: Int,
        onDone: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.createWorkout(
                    workoutId = workoutId,
                    name = workoutName,
                    workoutType = workoutType,
                    workoutRating = workoutRating,
                    ownerUid = ownerUid,
                    syncState = syncState,
                    image = image
                )

                val exercisesForSync = mutableListOf<WorkoutExerciseEntity>()
                val setForSync = mutableListOf<SetEntity>()

                for (draft in _draftExercises.value) {
                    val newExerciseId = UUID.randomUUID().toString()

                    repo.addExercise(
                        exerciseId = newExerciseId,
                        workoutId = workoutId,
                        name = draft.name,
                        catalogExerciseId = draft.catalogId,
                        bodyPart = draft.bodyPart,
                        secondaryMuscles = listOf(draft.equipment)
                    )

                    exercisesForSync.add(
                        WorkoutExerciseEntity(
                            exerciseId = newExerciseId,
                            workoutOwnerId = workoutId,
                            exerciseName = draft.name,
                            catalogExerciseId = draft.catalogId
                        )
                    )

                    for (set in draft.sets) {
                        val newSetId = UUID.randomUUID().toString()
                        val newSetEntity = SetEntity(
                            setId = newSetId,
                            exerciseOwnerId = newExerciseId,
                            reps = set.reps,
                            weight = set.weight,
                            note = null
                        )


                        repo.addSet(
                            setId = newSetId,
                            exerciseId = newExerciseId,
                            reps = newSetEntity.reps,
                            weight = newSetEntity.weight,
                            note = newSetEntity.note
                        )
                        setForSync.add(newSetEntity)
                    }
                }

                val newWorkout = WorkoutEntity(
                    workoutId = workoutId,
                    workoutName = workoutName,
                    workoutType = workoutType,
                    workoutRating = workoutRating,
                    ownerUid = ownerUid,
                    syncState = syncState
                )

                repo.saveAndSyncWorkout(newWorkout, exercisesForSync, setForSync)

                onDone()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }
}