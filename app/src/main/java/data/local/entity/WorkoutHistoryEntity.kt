package data.local.entity

import androidx.annotation.Keep
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.UUID

@Entity(tableName = "workout_history")
@Keep
data class WorkoutHistoryEntity(
    @PrimaryKey val sessionId: String = UUID.randomUUID().toString(),
    val workoutId: String = "",
    val workoutName: String = "",
    val dateTimestamp: Long = System.currentTimeMillis(),
    val totalDuration: Long = 0,
    val syncState: Boolean = false,
    val isCompleted: Boolean = false,
    val ownerUid: String? = null
)

@Entity(
    tableName = "exercise_logs",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = WorkoutHistoryEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionOwnerId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
@Keep
data class ExerciseLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val sessionOwnerId: String = "",
    val exerciseName: String = "",
    val bodyPart: String = "",
    val secondaryMuscles: List<String> = emptyList(),
    val weight: Double = 0.0,
    val reps: Int = 0,
    val setOrder: Int = 0,
    val log: String = "",
    val imageUrl: String? = null
)

@Entity(
    tableName = "set_logs",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = ExerciseLogEntity::class,
            parentColumns = ["logId"],
            childColumns = ["logOwnerId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
@Keep
data class SetLogEntity(
    @PrimaryKey(autoGenerate = true) val setId: Long = 0,
    val logOwnerId: Long = 0L,
    val reps: Int = 0,
    val weight: Float = 0F,
    val log: String = "",
    val setIndex: Int = 0,
    val clicked: Boolean = false
)

@Keep
data class ExerciseLogWithSets(
    @Embedded val exerciseLog: ExerciseLogEntity,
    @Relation(
        parentColumn = "logId",
        entityColumn = "logOwnerId"
    )
    val setLogs: List<SetLogEntity>
)

@Keep
data class WorkoutHistoryFull(
    @Embedded val workoutHistory: WorkoutHistoryEntity,
    @Relation(
        entity = ExerciseLogEntity::class,
        parentColumn = "sessionId",
        entityColumn = "sessionOwnerId"
    )
    val exerciseWithSets: List<ExerciseLogWithSets>
)