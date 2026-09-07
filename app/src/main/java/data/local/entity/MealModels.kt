package data.local.entity

data class FoodEntry(
    val id: String = "",
    val slot: String = MealSlot.BREAKFAST.id,
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    /** Serving size in the entry's [unit] (g or ml). */
    val amount: Int = 0,
    /** `"g"` for solids, `"ml"` for liquids (liters are converted to ml when logging). */
    val unit: String = FoodAmountUnit.G.label,
)

data class MealDayLog(
    val goal: NutritionGoal = NutritionGoal.MAINTAIN,
    val date: String = "",
    val waterMl: Int = 0,
    val cupType: WaterCupType = WaterCupType.GLASS,
    val customCups: List<WaterCupType> = emptyList(),
    val customWaterMl: Int? = null,
    val entries: List<FoodEntry> = emptyList(),
)

data class NutritionTargets(
    val calories: Int = 2200,
    val proteinG: Int = 140,
    val carbsG: Int = 220,
    val fatG: Int = 70,
    val waterGlasses: Int = 8,
    val waterMl: Int = 2000,
    val usedFallbackProfile: Boolean = true,
)

/** Solid foods use grams; drinks use milliliters (optionally entered as liters). */
enum class FoodAmountUnit(val label: String) {
    G("g"),
    ML("ml"),
    ;

    val isLiquid: Boolean get() = this == ML

    val inputOptions: List<String>
        get() =
            when (this) {
                G -> listOf("g")
                ML -> listOf("ml", "L")
            }

    fun perBaseLabel(): String = "Per $FOOD_TEMPLATE_BASE_AMOUNT$label"

    companion object {
        fun fromLabel(label: String?): FoodAmountUnit =
            entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: G
    }
}

data class FoodTemplate(
    val name: String,
    /** Macros below are always per [FOOD_TEMPLATE_BASE_AMOUNT] of [unit]. */
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val unit: FoodAmountUnit = FoodAmountUnit.G,
    val popular: Boolean = false,
)

/** Templates store nutrition for this serving baseline (100g or 100ml). */
const val FOOD_TEMPLATE_BASE_AMOUNT = 100

/** Scale a per-100 macro to [amount] in the same unit. */
fun scaleMacroPerBase(valuePerBase: Int, amount: Int): Int {
    val safeAmount = amount.coerceAtLeast(0)
    return ((valuePerBase.toLong() * safeAmount) / FOOD_TEMPLATE_BASE_AMOUNT).toInt()
}

data class ScaledFoodMacros(
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    /** Amount stored on the entry, always in the template base unit (g or ml). */
    val amount: Int,
    val unit: FoodAmountUnit,
)

/**
 * [inputAmount] is what the user typed.
 * [inputUnitLabel] is `g`, `ml`, or `L`. Liters are converted to ml before scaling.
 */
fun FoodTemplate.scaledTo(
    inputAmount: Int,
    inputUnitLabel: String = unit.label,
): ScaledFoodMacros {
    val baseAmount = toBaseAmount(inputAmount, inputUnitLabel)
    return ScaledFoodMacros(
        calories = scaleMacroPerBase(calories, baseAmount),
        protein = scaleMacroPerBase(protein, baseAmount),
        carbs = scaleMacroPerBase(carbs, baseAmount),
        fat = scaleMacroPerBase(fat, baseAmount),
        amount = baseAmount,
        unit = unit,
    )
}

fun FoodTemplate.toBaseAmount(inputAmount: Int, inputUnitLabel: String): Int {
    val safe = inputAmount.coerceAtLeast(0)
    return if (unit.isLiquid && inputUnitLabel.equals("L", ignoreCase = true)) {
        safe * 1000
    } else {
        safe
    }
}

fun formatFoodAmount(amount: Int, unitLabel: String): String {
    if (amount <= 0) return ""
    if (unitLabel.equals("ml", ignoreCase = true) && amount >= 1000 && amount % 1000 == 0) {
        return "${amount / 1000} L"
    }
    return "$amount $unitLabel"
}

data class WaterCupType(
    val id: String,
    val label: String,
    val sizeLabel: String,
    val ml: Int,
    val unitPlural: String,
    val isCustom: Boolean = false,
) {
    companion object {
        val CUP = WaterCupType("cup", "Cup", "200 ml", 200, "cups")
        val GLASS = WaterCupType("glass", "Glass", "250 ml", 250, "glasses")
        val BGLASS = WaterCupType("bglass", "Glass", "400 ml", 400, "glasses")
        val BOTTLE = WaterCupType("bottle", "Bottle", "500 ml", 500, "bottles")
        val LITER = WaterCupType("liter", "Liter", "1 L", 1000, "liters")
        val TLITER = WaterCupType("tliter", "Liter", "2 L", 2000, "liters")

        val PRESETS = listOf(CUP, GLASS, BGLASS, BOTTLE, LITER, TLITER)

        const val MIN_CUSTOM_ML = 50
        const val MAX_CUSTOM_ML = 2000

        fun fromId(id: String?, customCups: List<WaterCupType> = emptyList()): WaterCupType =
            (PRESETS + customCups).firstOrNull { it.id.equals(id, ignoreCase = true) } ?: GLASS

        fun createCustom(label: String, milliliters: Int): WaterCupType {
            val name =
                label
                    .trim()
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    .ifBlank { "Cup" }
                    .take(20)
            val size = milliliters.coerceIn(MIN_CUSTOM_ML, MAX_CUSTOM_ML)
            val sizeLabel =
                if (size >= 1000 && size % 1000 == 0) {
                    "${size / 1000} L"
                } else {
                    "$size ml"
                }
            val lower = name.lowercase()
            val unitPlural =
                when {
                    "liter" in lower || "litre" in lower -> "liters"
                    "bottle" in lower -> "bottles"
                    "glass" in lower -> "glasses"
                    else -> "cups"
                }
            return WaterCupType(
                id = "custom_${java.util.UUID.randomUUID()}",
                label = name,
                sizeLabel = sizeLabel,
                ml = size,
                unitPlural = unitPlural,
                isCustom = true,
            )
        }
    }
}

enum class NutritionGoal(val id: String, val label: String, val hint: String) {
    CUT("cut", "Cut", "−400 kcal"),
    MAINTAIN("maintain", "Maintain", "At TDEE"),
    BULK("bulk", "Bulk", "+300 kcal"),
    ;

    companion object {
        fun fromId(id: String?): NutritionGoal =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: MAINTAIN
    }
}

enum class MealSlot(val id: String, val label: String) {
    BREAKFAST("breakfast", "Breakfast"),
    LUNCH("lunch", "Lunch"),
    DINNER("dinner", "Dinner"),
    SNACK("snack", "Snack"),
    ;

    companion object {
        fun fromId(id: String?): MealSlot =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: SNACK
    }
}

val GROZZ_FOOD_TEMPLATES =
    listOf(
        // Solids: macros per 100g. Liquids: macros per 100ml.
        FoodTemplate("Protein shake", 60, 10, 3, 1, unit = FoodAmountUnit.ML, popular = true),
        FoodTemplate("Yogurt", 100, 10, 8, 3, popular = true),
        FoodTemplate("Oatmeal", 120, 4, 20, 2, popular = true),
        FoodTemplate("Eggs & toast", 175, 11, 14, 8, popular = true),
        FoodTemplate("Chicken & rice", 130, 11, 14, 3, popular = true),
        FoodTemplate("Turkey wrap", 154, 11, 14, 5, popular = true),
        FoodTemplate("Salmon & veg", 137, 11, 5, 8, popular = true),
        FoodTemplate("Banana", 89, 1, 23, 0, popular = true),
        FoodTemplate("Whey + banana", 114, 13, 13, 1, popular = true),
        FoodTemplate("Beef & potatoes", 124, 9, 10, 4, popular = true),
        FoodTemplate("Chicken breast", 165, 31, 0, 4),
        FoodTemplate("Egg whites", 52, 11, 1, 0),
        FoodTemplate("Whole eggs", 143, 13, 1, 10),
        FoodTemplate("Cottage cheese", 98, 11, 3, 4),
        FoodTemplate("Skim milk", 34, 3, 5, 0, unit = FoodAmountUnit.ML),
        FoodTemplate("Whey isolate", 370, 85, 5, 2),
        FoodTemplate("Casein pudding", 107, 16, 5, 1),
        FoodTemplate("Tuna", 116, 26, 0, 1),
        FoodTemplate("Salmon fillet", 208, 20, 0, 13),
        FoodTemplate("Lean beef", 176, 26, 0, 8),
        FoodTemplate("Turkey mince", 148, 21, 0, 7),
        FoodTemplate("Shrimp", 99, 24, 0, 1),
        FoodTemplate("Tofu", 76, 8, 2, 5),
        FoodTemplate("White rice", 130, 3, 28, 0),
        FoodTemplate("Brown rice", 111, 3, 23, 1),
        FoodTemplate("Pasta", 131, 5, 25, 1),
        FoodTemplate("Sweet potato", 86, 2, 20, 0),
        FoodTemplate("Potato", 77, 2, 17, 0),
        FoodTemplate("Quinoa", 120, 4, 21, 2),
        FoodTemplate("Rice cakes", 387, 8, 82, 3),
        FoodTemplate("Whole wheat bread", 247, 13, 41, 4),
        FoodTemplate("Bagel", 257, 10, 50, 2),
        FoodTemplate("Avocado", 160, 2, 9, 15),
        FoodTemplate("Peanut butter", 588, 25, 20, 50),
        FoodTemplate("Olive oil", 884, 0, 0, 100, unit = FoodAmountUnit.ML),
        FoodTemplate("Almonds", 579, 21, 22, 50),
        FoodTemplate("Apple", 52, 0, 14, 0),
        FoodTemplate("Berries", 57, 1, 14, 0),
        FoodTemplate("Orange", 47, 1, 12, 0),
        FoodTemplate("Broccoli", 34, 3, 7, 0),
        FoodTemplate("Mixed salad", 20, 1, 4, 0),
        FoodTemplate("Protein bar", 350, 33, 37, 10),
        FoodTemplate("Granola", 471, 10, 64, 20),
        FoodTemplate("Pancakes", 227, 6, 28, 10),
        FoodTemplate("Pizza", 266, 11, 33, 10),
        FoodTemplate("Burger", 254, 13, 19, 14),
        FoodTemplate("Sushi", 143, 9, 20, 3),
        FoodTemplate("Hummus", 166, 8, 14, 10),
        FoodTemplate("Latte", 54, 3, 5, 2, unit = FoodAmountUnit.ML),
    )
