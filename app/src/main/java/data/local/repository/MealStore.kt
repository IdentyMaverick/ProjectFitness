package data.local.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.local.entity.FoodEntry
import data.local.entity.MealDayLog
import data.local.entity.NutritionGoal
import data.local.entity.WaterCupType
import data.local.util.todayDateKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.mealDataStore by preferencesDataStore(name = "grozz_meal")

class MealStore(context: Context) {
    private val dataStore = context.applicationContext.mealDataStore
    private val gson = Gson()
    private val entryListType = object : TypeToken<List<FoodEntry>>() {}.type
    private val customCupsType = object : TypeToken<List<WaterCupType>>() {}.type

    fun observe(uid: String): Flow<MealDayLog> {
        val userKey = uid.ifBlank { "local" }
        return dataStore.data.map { prefs ->
            val today = todayDateKey()
            val storedDate = prefs[dateKey(userKey)].orEmpty()
            val goal = NutritionGoal.fromId(prefs[goalKey(userKey)])
            val customCups = decodeCustomCups(prefs[customCupsKey(userKey)])
            val cupType = WaterCupType.fromId(prefs[cupKey(userKey)], customCups)
            val rawWater = prefs[waterKey(userKey)] ?: 0
            val waterMl =
                if (prefs[cupKey(userKey)] == null) {
                    rawWater * WaterCupType.GLASS.ml
                } else {
                    rawWater
                }
            val customWaterMl = prefs[customWaterKey(userKey)]?.takeIf { it > 0 }
            if (storedDate != today) {
                MealDayLog(
                    goal = goal,
                    date = today,
                    waterMl = 0,
                    cupType = cupType,
                    customCups = customCups,
                    customWaterMl = customWaterMl,
                    entries = emptyList(),
                )
            } else {
                MealDayLog(
                    goal = goal,
                    date = today,
                    waterMl = waterMl.coerceAtLeast(0),
                    cupType = cupType,
                    customCups = customCups,
                    customWaterMl = customWaterMl,
                    entries = decodeEntries(prefs[entriesKey(userKey)]),
                )
            }
        }
    }

    suspend fun setGoal(uid: String, goal: NutritionGoal) {
        val userKey = uid.ifBlank { "local" }
        dataStore.edit { prefs ->
            prefs[goalKey(userKey)] = goal.id
        }
    }

    suspend fun setWaterMl(uid: String, milliliters: Int) {
        val userKey = uid.ifBlank { "local" }
        dataStore.edit { prefs ->
            rollToToday(prefs, userKey)
            if (prefs[cupKey(userKey)] == null) {
                prefs[cupKey(userKey)] = WaterCupType.GLASS.id
            }
            prefs[waterKey(userKey)] = milliliters.coerceAtLeast(0)
        }
    }

    suspend fun setCupType(uid: String, cupType: WaterCupType) {
        val userKey = uid.ifBlank { "local" }
        dataStore.edit { prefs ->
            val rawWater = prefs[waterKey(userKey)] ?: 0
            val waterMl =
                if (prefs[cupKey(userKey)] == null) {
                    rawWater * WaterCupType.GLASS.ml
                } else {
                    rawWater
                }
            prefs[cupKey(userKey)] = cupType.id
            if (prefs[dateKey(userKey)] == todayDateKey()) {
                prefs[waterKey(userKey)] = waterMl
            }
        }
    }

    suspend fun addCustomCup(uid: String, cup: WaterCupType): WaterCupType? {
        if (!cup.isCustom || cup.ml !in WaterCupType.MIN_CUSTOM_ML..WaterCupType.MAX_CUSTOM_ML) return null
        val userKey = uid.ifBlank { "local" }
        var saved: WaterCupType? = null
        dataStore.edit { prefs ->
            val current = decodeCustomCups(prefs[customCupsKey(userKey)])
            val existing =
                current.firstOrNull {
                    it.ml == cup.ml && it.label.equals(cup.label, ignoreCase = true)
                }
            saved = existing ?: cup
            if (existing == null) {
                if (current.size >= 12) {
                    saved = null
                    return@edit
                }
                prefs[customCupsKey(userKey)] = gson.toJson(current + cup)
            }
        }
        return saved
    }

    suspend fun removeCustomCup(uid: String, cupId: String) {
        val userKey = uid.ifBlank { "local" }
        dataStore.edit { prefs ->
            val next = decodeCustomCups(prefs[customCupsKey(userKey)]).filterNot { it.id == cupId }
            if (next.isEmpty()) {
                prefs.remove(customCupsKey(userKey))
            } else {
                prefs[customCupsKey(userKey)] = gson.toJson(next)
            }
            if (prefs[cupKey(userKey)] == cupId) {
                prefs[cupKey(userKey)] = WaterCupType.GLASS.id
            }
        }
    }

    suspend fun setCustomWaterMl(uid: String, milliliters: Int?) {
        val userKey = uid.ifBlank { "local" }
        dataStore.edit { prefs ->
            val target = milliliters?.takeIf { it > 0 }
            if (target == null) {
                prefs.remove(customWaterKey(userKey))
            } else {
                prefs[customWaterKey(userKey)] = target.coerceIn(500, 6000)
            }
        }
    }

    suspend fun addEntry(uid: String, entry: FoodEntry) {
        val userKey = uid.ifBlank { "local" }
        dataStore.edit { prefs ->
            rollToToday(prefs, userKey)
            val next = decodeEntries(prefs[entriesKey(userKey)]) + entry
            prefs[entriesKey(userKey)] = gson.toJson(next)
        }
    }

    suspend fun removeEntry(uid: String, entryId: String) {
        val userKey = uid.ifBlank { "local" }
        dataStore.edit { prefs ->
            rollToToday(prefs, userKey)
            val next = decodeEntries(prefs[entriesKey(userKey)]).filterNot { it.id == entryId }
            prefs[entriesKey(userKey)] = gson.toJson(next)
        }
    }

    private fun decodeEntries(raw: String?): List<FoodEntry> =
        try {
            gson.fromJson<List<FoodEntry>>(raw ?: "[]", entryListType).orEmpty()
        } catch (_: Exception) {
            emptyList()
        }

    private fun decodeCustomCups(raw: String?): List<WaterCupType> =
        try {
            gson
                .fromJson<List<WaterCupType>>(raw ?: "[]", customCupsType)
                .orEmpty()
                .filter { it.id.isNotBlank() && it.ml in WaterCupType.MIN_CUSTOM_ML..WaterCupType.MAX_CUSTOM_ML }
                .map { it.copy(isCustom = true) }
        } catch (_: Exception) {
            emptyList()
        }

    private fun rollToToday(
        prefs: androidx.datastore.preferences.core.MutablePreferences,
        userKey: String,
    ) {
        val today = todayDateKey()
        if (prefs[dateKey(userKey)] == today) return
        prefs[dateKey(userKey)] = today
        prefs[waterKey(userKey)] = 0
        prefs[entriesKey(userKey)] = "[]"
    }

    private fun goalKey(uid: String) = stringPreferencesKey("${uid}_goal")

    private fun dateKey(uid: String) = stringPreferencesKey("${uid}_date")

    private fun cupKey(uid: String) = stringPreferencesKey("${uid}_cup")

    private fun customCupsKey(uid: String) = stringPreferencesKey("${uid}_custom_cups")

    private fun customWaterKey(uid: String) = intPreferencesKey("${uid}_water_target")

    private fun waterKey(uid: String) = intPreferencesKey("${uid}_water")

    private fun entriesKey(uid: String) = stringPreferencesKey("${uid}_entries")
}
