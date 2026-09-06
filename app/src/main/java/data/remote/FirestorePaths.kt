package data.remote

/**
 * Firestore collection IDs. Names are historical (`googlecloud*`) and must stay
 * stable so existing documents keep resolving.
 *
 * [TEMPLATES] and [HISTORY] share the same collection id. They are distinguished
 * by path: templates are root documents, history is nested under [USERS].
 */
object FirestorePaths {
    const val USERS = "googlecloudusers"

    /** Planned workouts at `googlecloudworkouts/{workoutId}`. */
    const val TEMPLATES = "googlecloudworkouts"

    /** Completed sessions at `googlecloudusers/{uid}/googlecloudworkouts/{sessionId}`. */
    const val HISTORY = "googlecloudworkouts"

    const val EXERCISES = "googlecloudexercises"
    const val SETS = "googlecloudsets"
    const val SETS_LEGACY = "sets"
    const val CATALOG = "googlecloud"
    const val LEADERBOARD = "googlecloudleaderboard"
}
