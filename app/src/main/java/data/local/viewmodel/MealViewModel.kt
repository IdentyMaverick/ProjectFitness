package data.local.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.grozzbear.projectfitness.data.local.repository.WorkoutRepository
import com.grozzbear.projectfitness.data.local.entity.WorkoutWithExercises
import data.local.entity.FoodEntry
import data.local.entity.FoodTemplate
import data.local.entity.MealDayLog
import data.local.entity.MealSlot
import data.local.entity.NutritionGoal
import data.local.entity.NutritionTargets
import data.local.entity.WaterCupType
import data.local.entity.WorkoutHistoryFull
import data.local.repository.MealStore
import data.local.util.calculateNutritionTargets
import data.local.util.formatMealDateLabel
import data.local.util.isSameLocalDay
import data.local.util.parseAgeYears
import data.local.util.parsePositiveFloat
import data.local.util.pickTodaysTraining
import data.local.util.trainingFuelNote
import data.local.util.waterCupsFor
import data.remote.UserProfile
import data.remote.UserRepository
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

data class MealUiState(
    val isLoading: Boolean = true,
    val dateLabel: String = "",
    val goal: NutritionGoal = NutritionGoal.MAINTAIN,
    val targets: NutritionTargets = NutritionTargets(),
    val entries: List<FoodEntry> = emptyList(),
    val waterGlasses: Int = 0,
    val waterCupType: WaterCupType = WaterCupType.GLASS,
    val customCups: List<WaterCupType> = emptyList(),
    val suggestedWaterMl: Int = 2000,
    val hasCustomWaterTarget: Boolean = false,
    val eatenCalories: Int = 0,
    val eatenProtein: Int = 0,
    val eatenCarbs: Int = 0,
    val eatenFat: Int = 0,
    val trainingTitle: String = "Rest day",
    val trainingNote: String = "",
    val profileIncomplete: Boolean = false,
)

class MealViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val mealStore: MealStore,
) : ViewModel() {
    private val uidFlow = MutableStateFlow(currentUid())
    private val profileFlow = MutableStateFlow<UserProfile?>(null)
    private val _uiState = MutableStateFlow(MealUiState())
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    private fun currentUid(): String =
        Firebase.auth.currentUser
            ?.uid
            .orEmpty()

    init {
        refreshProfile()
        viewModelScope.launch {
            uidFlow
                .flatMapLatest { userId ->
                    combine(
                        mealStore.observe(userId),
                        workoutRepository.templates.observeWorkouts(),
                        workoutRepository.sessions.observeHistoricalWorkouts(),
                        profileFlow,
                    ) { day, workouts, history, profile ->
                        buildState(day, workouts, history, profile)
                    }
                }.collect { _uiState.value = it }
        }
    }

    fun refreshProfile() {
        val currentUid = currentUid()
        uidFlow.value = currentUid
        viewModelScope.launch {
            profileFlow.value =
                if (currentUid.isBlank()) {
                    null
                } else {
                    runCatching { userRepository.getUserProfile(currentUid) }.getOrNull()
                }
        }
    }

    fun setGoal(goal: NutritionGoal) {
        viewModelScope.launch { mealStore.setGoal(currentUid(), goal) }
    }

    fun setWater(glasses: Int) {
        viewModelScope.launch {
            val cupMl = _uiState.value.waterCupType.ml
            mealStore.setWaterMl(currentUid(), glasses.coerceAtLeast(0) * cupMl)
        }
    }

    fun setCupType(cupType: WaterCupType) {
        viewModelScope.launch { mealStore.setCupType(currentUid(), cupType) }
    }

    fun addCustomCup(cup: WaterCupType) {
        viewModelScope.launch { mealStore.addCustomCup(currentUid(), cup) }
    }

    fun removeCustomCup(cupId: String) {
        viewModelScope.launch { mealStore.removeCustomCup(currentUid(), cupId) }
    }

    fun setCustomWaterTarget(milliliters: Int) {
        viewModelScope.launch { mealStore.setCustomWaterMl(currentUid(), milliliters) }
    }

    fun clearCustomWaterTarget() {
        viewModelScope.launch { mealStore.setCustomWaterMl(currentUid(), null) }
    }

    fun addTemplate(slot: MealSlot, template: FoodTemplate) {
        addFood(
            slot = slot,
            name = template.name,
            calories = template.calories,
            protein = template.protein,
            carbs = template.carbs,
            fat = template.fat,
        )
    }

    fun addFood(slot: MealSlot, name: String, calories: Int, protein: Int, carbs: Int, fat: Int) {
        val trimmed = name.trim()
        if (trimmed.isBlank() || calories <= 0) return
        viewModelScope.launch {
            mealStore.addEntry(
                currentUid(),
                FoodEntry(
                    id = UUID.randomUUID().toString(),
                    slot = slot.id,
                    name = trimmed,
                    calories = calories,
                    protein = protein.coerceAtLeast(0),
                    carbs = carbs.coerceAtLeast(0),
                    fat = fat.coerceAtLeast(0),
                ),
            )
        }
    }

    fun removeFood(entryId: String) {
        viewModelScope.launch { mealStore.removeEntry(currentUid(), entryId) }
    }

    private fun buildState(
        day: MealDayLog,
        workouts: List<WorkoutWithExercises>,
        history: List<WorkoutHistoryFull>,
        profile: UserProfile?,
    ): MealUiState {
        val trainedToday = history.any { isSameLocalDay(it.workoutHistory.dateTimestamp) }
        val weight = profile?.weight?.let(::parsePositiveFloat)
        val height = profile?.height?.let(::parsePositiveFloat)
        val autoTargets =
            calculateNutritionTargets(
                weightKg = weight,
                heightCm = height,
                ageYears = parseAgeYears(profile?.birthDate.orEmpty()),
                isFemale = profile?.gender == true,
                goal = day.goal,
                trainedToday = trainedToday,
                cupType = day.cupType,
            )
        val waterMl = day.customWaterMl ?: autoTargets.waterMl
        val targets =
            autoTargets.copy(
                waterMl = waterMl,
                waterGlasses = waterCupsFor(waterMl, day.cupType.ml),
            )
        val filledCups = if (day.cupType.ml == 0) 0 else day.waterMl / day.cupType.ml
        val (title, note) = trainingFuelNote(pickTodaysTraining(workouts))
        return MealUiState(
            isLoading = false,
            dateLabel = formatMealDateLabel(),
            goal = day.goal,
            targets = targets,
            entries = day.entries,
            waterGlasses = filledCups.coerceIn(0, targets.waterGlasses),
            waterCupType = day.cupType,
            customCups = day.customCups,
            suggestedWaterMl = autoTargets.waterMl,
            hasCustomWaterTarget = day.customWaterMl != null,
            eatenCalories = day.entries.sumOf { it.calories },
            eatenProtein = day.entries.sumOf { it.protein },
            eatenCarbs = day.entries.sumOf { it.carbs },
            eatenFat = day.entries.sumOf { it.fat },
            trainingTitle = title,
            trainingNote = note,
            profileIncomplete = profile == null || weight == null || height == null,
        )
    }
}
