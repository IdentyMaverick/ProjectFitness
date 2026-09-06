package data.local.util

import com.grozzbear.projectfitness.data.local.entity.WorkoutWithExercises
import com.grozzbear.ui.util.isChallengeType
import com.grozzbear.ui.util.isCoachType
import data.local.entity.NutritionGoal
import data.local.entity.NutritionTargets
import data.local.entity.WaterCupType
import kotlin.math.ceil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

fun todayDateKey(now: Long = System.currentTimeMillis()): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(now))

fun formatMealDateLabel(now: Long = System.currentTimeMillis()): String =
    SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(Date(now))

fun parseAgeYears(birthDate: String): Int? {
    if (birthDate.isBlank()) return null
    return try {
        val parsed =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .apply { isLenient = false }
                .parse(birthDate) ?: return null
        val birth = Calendar.getInstance().apply { time = parsed }
        val now = Calendar.getInstance()
        var age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) age -= 1
        age.takeIf { it in 13..90 }
    } catch (_: Exception) {
        null
    }
}

fun parsePositiveFloat(raw: String): Float? =
    raw.replace(',', '.').toFloatOrNull()?.takeIf { it > 0f }

fun calculateNutritionTargets(
    weightKg: Float?,
    heightCm: Float?,
    ageYears: Int?,
    isFemale: Boolean,
    goal: NutritionGoal,
    trainedToday: Boolean,
    cupType: WaterCupType = WaterCupType.GLASS,
): NutritionTargets {
    val weight = weightKg?.takeIf { it in 35f..250f } ?: 75f
    val height = heightCm?.takeIf { it in 120f..230f } ?: 175f
    val age = ageYears ?: 28
    val usedFallback = weightKg == null || heightCm == null

    val bmr =
        if (isFemale) {
            10f * weight + 6.25f * height - 5f * age - 161f
        } else {
            10f * weight + 6.25f * height - 5f * age + 5f
        }
    val tdee = bmr * if (trainedToday) 1.55f else 1.4f
    val calories =
        when (goal) {
            NutritionGoal.CUT -> tdee - 400f
            NutritionGoal.MAINTAIN -> tdee
            NutritionGoal.BULK -> tdee + 300f
        }.toInt().coerceIn(1400, 4500)

    val proteinG = (weight * if (goal == NutritionGoal.CUT) 2.2f else 1.8f).toInt()
    val fatG = (weight * 0.8f).toInt().coerceAtLeast(40)
    val carbsG = ((calories - proteinG * 4 - fatG * 9) / 4).coerceAtLeast(80)
    val waterMl = (weight * 35f).toInt().coerceIn(1500, 4000)
    val waterGlasses = waterCupsFor(waterMl, cupType.ml)

    return NutritionTargets(
        calories = calories,
        proteinG = proteinG,
        carbsG = carbsG,
        fatG = fatG,
        waterGlasses = waterGlasses,
        waterMl = waterMl,
        usedFallbackProfile = usedFallback,
    )
}

fun waterCupsFor(waterMl: Int, cupMl: Int): Int {
    if (cupMl <= 0) return 0
    return ceil(waterMl / cupMl.toFloat()).toInt().coerceIn(1, 24)
}

fun pickTodaysTraining(workouts: List<WorkoutWithExercises>): WorkoutWithExercises? {
    if (workouts.isEmpty()) return null
    val pool =
        workouts
            .filterNot { it.workout.workoutType.equals("User", ignoreCase = true) }
            .ifEmpty { workouts }
            .sortedBy { it.workout.workoutId }
    val calendar = Calendar.getInstance()
    val daySeed =
        calendar.get(Calendar.YEAR) * 1_000L +
            calendar.get(Calendar.DAY_OF_YEAR)
    return pool[Random(daySeed).nextInt(pool.size)]
}

fun trainingFuelNote(workout: WorkoutWithExercises?): Pair<String, String> {
    if (workout == null) {
        return "Rest day" to "Keep protein high at each meal and eat to your calorie target."
    }
    val name = workout.workout.workoutName
    val type = workout.workout.workoutType
    val lower = "$name $type".lowercase(Locale.getDefault())
    val note =
        when {
            listOf("leg", "squat", "glute", "lower").any { it in lower } ->
                "Lower-body day — keep carbs higher around this session."
            listOf("push", "pull", "chest", "back", "shoulder", "upper").any { it in lower } ->
                "Upper-body day — hit protein at every meal and carbs near training."
            isCoachType(type) ->
                "Coach session today — eat to the plan and don’t cut calories hard."
            isChallengeType(type) ->
                "Challenge day — fuel with protein and easy carbs before you start."
            else ->
                "Training day — protein at each meal, carbs around the workout."
        }
    return name to note
}

fun isSameLocalDay(timestamp: Long, now: Long = System.currentTimeMillis()): Boolean {
    val stampDay = Calendar.getInstance().apply { timeInMillis = timestamp }
    val nowDay = Calendar.getInstance().apply { timeInMillis = now }
    return stampDay.get(Calendar.YEAR) == nowDay.get(Calendar.YEAR) &&
        stampDay.get(Calendar.DAY_OF_YEAR) == nowDay.get(Calendar.DAY_OF_YEAR)
}
