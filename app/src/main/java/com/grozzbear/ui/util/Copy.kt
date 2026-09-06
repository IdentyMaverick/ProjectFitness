package com.grozzbear.ui.util

fun counted(count: Int, singular: String, plural: String = "${singular}s"): String {
    return "$count ${if (count == 1) singular else plural}"
}

fun isChallengeType(type: String): Boolean {
    val normalized = type.lowercase()
    return normalized.contains("challenge") || normalized.contains("challange")
}

fun isCoachType(type: String): Boolean = type.contains("coach", ignoreCase = true)

fun workoutTypeLabel(type: String): String {
    val trimmed = type.trim()
    return when {
        trimmed.isBlank() || trimmed.equals("User", ignoreCase = true) -> "Custom"
        isChallengeType(trimmed) -> "Challenge"
        isCoachType(trimmed) -> "Coach"
        else -> trimmed.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase() else char.toString()
        }
    }
}
