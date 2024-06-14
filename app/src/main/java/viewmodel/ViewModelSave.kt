package viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import database.Exercise
import database.ProjectFitnessWorkoutEntity
import database.SetRep
import database.Workout
import kotlinx.coroutines.delay

class ViewModelSave() : ViewModel() {
    val name = mutableStateOf("")
    val exercises = mutableStateListOf<String?>()
    val setRepString = mutableStateListOf<String?>()
    val flag = mutableStateOf(true)
    var allowed = mutableStateOf(true)
    var set = mutableStateListOf<String>("0", "0", "0", "0", "0", "0", "0", "0", "0")
    var reps = mutableStateListOf<String>("0", "0", "0", "0", "0", "0", "0", "0", "0")
    var count = mutableStateOf(0)
    var workoutList = mutableListOf<Workout>()
    var selectedItemName =
        mutableStateOf("")   // Create Workout --> Exercise selected --> Workout Details --> ADD EXERCISES --> (selectedItemName = Exercise Name)
    var selectedItemUpdatedName = mutableStateOf("")

    var selectedWorkoutName = mutableStateOf("")
    var selectedCompletedName = mutableStateOf("")

    var exercisesForWorkouts2 = mutableStateListOf<Exercise>()

    var idFlag = mutableStateOf(1)
    var idFlag2 = mutableStateOf(3)

    var flagA = mutableStateOf(false)
    var flagB = mutableStateOf(false)
    var flagC = mutableStateOf(false)
    var flagD = mutableStateOf(false)

    var note = mutableStateOf("")
    var workoutRate = mutableStateOf(0)

    var workoutSize = mutableStateOf(0)
    var completedWorkoutId = mutableStateOf(1000)
    var completedWorkoutSize = mutableStateOf(0)

    var exerciseId = mutableStateOf(1)
    var exerciseIds = mutableStateOf(1)

    var totalSetsOfCompletedWorkout = mutableStateOf(0)
    var totalRepsOfCompletedWorkout = mutableStateOf(0)
    var totalWeightOfCompletedWorkout = mutableStateOf(0)

    var completedWorkoutTime = mutableStateOf("") // Workout bitince zamanı kaydeden değişken
    var hours = mutableStateOf("")

    var hour = mutableStateOf("")
    var minutes = mutableStateOf("")
    var seconds = mutableStateOf("")

    var hourInt = mutableStateOf(0)
    var secondInt = mutableStateOf(0)
    var minuteInt = mutableStateOf(0)

    var selectedWorkoutItem = mutableStateOf(ProjectFitnessWorkoutEntity(0, "", mutableListOf()))

    var ticked = mutableStateOf(false)

    var setrepList = mutableStateListOf<SetRep>()
    var setrepCoach = mutableStateListOf<SetRep>()
    var readyBackFinisher = mutableListOf("Lat Pulldown", "Lat Pulldown 2", "Lat Pulldown 3")
    var coachProjectMain = mutableListOf("")

    var lastSet = mutableStateOf(0)
    var lastRep = mutableStateOf(0)

    var challangesSelectedIndex = mutableStateOf(0)
    var challangesSelectedName = mutableStateOf("")
    var challangesSelectedDifficulty = mutableStateOf(0)

    var coachesSelectedName = mutableStateOf("")
    var coachesSelectedDifficulty = mutableStateOf(0)

    fun updateSelectedItemName(newName: String) {
        selectedItemName.value = newName
    }

    fun workouts(): List<Workout> {
        var exerciseList = exercises.filterNotNull()
        val repsList = reps.map { it.toIntOrNull() ?: 0 }
        val setsList = set.map { it.toIntOrNull() ?: 0 }

        if (exerciseList.isNotEmpty() && allowed.value) {
            var exercisesForWorkout = mutableListOf<Exercise>()
            for (i in exerciseList.indices) {
                val exercise = Exercise(
                    name = exerciseList[i],
                    reps = repsList[i],
                    sets = setsList[i]
                )
                exercisesForWorkout.add(exercise)

            }
            val workout = Workout(name = name.value, exercises = exercisesForWorkout)
            if (workout !in workoutList) {
                workoutList.add(workout)
            }


        }

        return workoutList
    }

    @Composable
    fun RestTimer() {
        LaunchedEffect(key1 = secondInt.value)
        {
            while (hourInt.value >= 0) {
                delay(1000L)
                secondInt.value++
                if (minuteInt.value == 60) {
                    hourInt.value++
                    minuteInt.value = 0
                } else if (secondInt.equals(60)) {
                    minuteInt.value++
                    secondInt.value = 0
                }
            }

        }
        hour.value = "%2dh ".format(hourInt.value)
        minutes.value = "%2dm ".format(minuteInt.value)
        seconds.value = "%2ds ".format(secondInt.value)
    }

}
