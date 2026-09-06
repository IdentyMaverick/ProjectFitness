package data.local.entity

data class FoodEntry(
    val id: String = "",
    val slot: String = MealSlot.BREAKFAST.id,
    val name: String = "",
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
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

data class FoodTemplate(
    val name: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val popular: Boolean = false,
)

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
        FoodTemplate("Protein shake", 180, 30, 8, 3, popular = true),
        FoodTemplate("Greek yogurt", 150, 15, 12, 4, popular = true),
        FoodTemplate("Oatmeal", 300, 10, 50, 6, popular = true),
        FoodTemplate("Eggs & toast", 350, 22, 28, 16, popular = true),
        FoodTemplate("Chicken & rice", 520, 42, 55, 10, popular = true),
        FoodTemplate("Turkey wrap", 430, 32, 38, 14, popular = true),
        FoodTemplate("Salmon & veg", 480, 38, 18, 28, popular = true),
        FoodTemplate("Banana", 105, 1, 27, 0, popular = true),
        FoodTemplate("Whey + banana", 250, 28, 28, 3, popular = true),
        FoodTemplate("Beef & potatoes", 560, 40, 45, 20, popular = true),
        FoodTemplate("Chicken breast", 165, 31, 0, 4),
        FoodTemplate("Egg whites", 80, 16, 1, 0),
        FoodTemplate("Whole eggs (2)", 140, 12, 1, 10),
        FoodTemplate("Cottage cheese", 180, 24, 8, 5),
        FoodTemplate("Skim milk", 90, 8, 12, 0),
        FoodTemplate("Whey isolate", 120, 27, 2, 1),
        FoodTemplate("Casein pudding", 160, 24, 8, 2),
        FoodTemplate("Tuna (can)", 180, 40, 0, 2),
        FoodTemplate("Salmon fillet", 280, 34, 0, 16),
        FoodTemplate("Lean beef", 250, 32, 0, 13),
        FoodTemplate("Turkey mince", 220, 28, 0, 12),
        FoodTemplate("Shrimp", 120, 24, 1, 2),
        FoodTemplate("Tofu", 140, 16, 4, 8),
        FoodTemplate("White rice", 200, 4, 44, 0),
        FoodTemplate("Brown rice", 215, 5, 45, 2),
        FoodTemplate("Pasta", 280, 10, 54, 2),
        FoodTemplate("Sweet potato", 180, 4, 41, 0),
        FoodTemplate("Potato", 160, 4, 36, 0),
        FoodTemplate("Quinoa", 220, 8, 39, 4),
        FoodTemplate("Rice cakes (2)", 70, 1, 15, 0),
        FoodTemplate("Whole wheat bread", 80, 4, 14, 1),
        FoodTemplate("Bagel", 280, 10, 54, 2),
        FoodTemplate("Avocado", 240, 3, 12, 22),
        FoodTemplate("Peanut butter", 190, 8, 7, 16),
        FoodTemplate("Olive oil (1 tbsp)", 120, 0, 0, 14),
        FoodTemplate("Almonds", 170, 6, 6, 15),
        FoodTemplate("Apple", 95, 0, 25, 0),
        FoodTemplate("Berries", 70, 1, 17, 0),
        FoodTemplate("Orange", 80, 1, 19, 0),
        FoodTemplate("Broccoli", 55, 4, 11, 1),
        FoodTemplate("Mixed salad", 40, 2, 7, 0),
        FoodTemplate("Protein bar", 210, 20, 22, 6),
        FoodTemplate("Granola", 240, 6, 32, 10),
        FoodTemplate("Pancakes", 350, 10, 52, 11),
        FoodTemplate("Pizza slice", 285, 12, 36, 10),
        FoodTemplate("Burger", 540, 28, 40, 28),
        FoodTemplate("Sushi (8 pcs)", 300, 18, 42, 6),
        FoodTemplate("Hummus & pita", 260, 8, 32, 10),
        FoodTemplate("Latte", 150, 8, 14, 6),
    )
